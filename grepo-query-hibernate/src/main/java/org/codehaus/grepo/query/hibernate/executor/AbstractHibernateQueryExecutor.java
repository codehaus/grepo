/*
 * Copyright 2009 Grepo Committers.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.grepo.query.hibernate.executor; //NOPMD

import java.util.Collection;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.codehaus.grepo.core.util.ClassUtils;
import org.codehaus.grepo.query.commons.annotation.FirstResult;
import org.codehaus.grepo.query.commons.annotation.GenericQuery;
import org.codehaus.grepo.query.commons.annotation.MaxResults;
import org.codehaus.grepo.query.commons.aop.QueryMethodParameterInfo;
import org.codehaus.grepo.query.commons.executor.AbstractQueryExecutor;
import org.codehaus.grepo.query.hibernate.annotation.EntityClass;
import org.codehaus.grepo.query.hibernate.annotation.GScrollMode;
import org.codehaus.grepo.query.hibernate.annotation.GType;
import org.codehaus.grepo.query.hibernate.annotation.HibernateCaching;
import org.codehaus.grepo.query.hibernate.annotation.HibernateQueryOptions;
import org.codehaus.grepo.query.hibernate.annotation.Join;
import org.codehaus.grepo.query.hibernate.annotation.Scalar;
import org.codehaus.grepo.query.hibernate.converter.PlaceHolderResultTransformer;
import org.codehaus.grepo.query.hibernate.generator.CriteriaGenerator;
import org.codehaus.grepo.query.hibernate.generator.HibernateNativeQueryGenerator;
import org.codehaus.grepo.query.hibernate.generator.HibernateQueryGenerator;
import org.codehaus.grepo.query.hibernate.generator.HibernateQueryParam;
import org.codehaus.grepo.query.hibernate.generator.PlaceHolderCriteriaGenerator;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.ToListResultTransformer;
import org.hibernate.transform.Transformers;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.SessionFactoryUtils;

/**
 * @author dguggi
 */
public abstract class AbstractHibernateQueryExecutor extends AbstractQueryExecutor<HibernateQueryExecutionContext>
        implements HibernateQueryExecutor {

    /** SerialVersionUid. */
    private static final long serialVersionUID = 1138696235036750544L;

    /** The logger for this class. */
    private final Logger logger = LoggerFactory.getLogger(AbstractHibernateQueryExecutor.class); // NOPMD

    /** The argument type factory. */
    private ArgumentTypeFactory argumentTypeFactory;

    /**
     * @param clazz The generator class.
     * @param qmpi The query method parameter info.
     * @return Returns detached critieria object.
     */
    protected DetachedCriteria generateCriteria(Class<? extends CriteriaGenerator> clazz, QueryMethodParameterInfo qmpi) {
        CriteriaGenerator generator = ClassUtils.instantiateClass(clazz);
        return generator.generate(qmpi);
    }

    /**
     * @param genericQuery The annotation.
     * @param qmpi The query method parameter info.
     * @param context The query execution context.
     * @return Returns the prepared critiera instance.
     */
    protected Criteria prepareCriteria(GenericQuery genericQuery, QueryMethodParameterInfo qmpi,
        HibernateQueryExecutionContext context) {
        HibernateQueryOptions queryOptions = qmpi.getMethodAnnotation(HibernateQueryOptions.class);
        DetachedCriteria detachedCriteria = generateCriteria(queryOptions.criteriaGenerator(), qmpi);
        Criteria criteria = detachedCriteria.getExecutableCriteria(context.getSession());

        logger.debug("Using criteria generated by: {}", queryOptions.criteriaGenerator());

        // set result transformer if available
        ResultTransformer resultTransformer = getResultTransformer(queryOptions);
        if (resultTransformer != null) {
            criteria.setResultTransformer(resultTransformer);
        }

        // set fetch size if available
        Integer fetchSize = getFetchSize(queryOptions, context.getFetchSize());
        if (fetchSize != null) {
            criteria.setFetchSize(fetchSize);
        }

        // set first result if available
        Integer firstResult = getFirstResult(qmpi, genericQuery);
        if (firstResult != null) {
            criteria.setFirstResult(firstResult);
        }

        // set max results if available
        Integer maxResults = getMaxResults(qmpi, genericQuery, context.getMaxResults());
        if (maxResults != null) {
            criteria.setMaxResults(maxResults);
        }

        // handle caching...
        if (isCachingEnabled(context, queryOptions)) {
            criteria.setCacheable(true);
            String cacheRegion = getCacheRegion(context, queryOptions);
            if (cacheRegion != null) {
                criteria.setCacheRegion(cacheRegion);
            }
        }

        // query/session timeout (Andy)
        if ((queryOptions != null) && (queryOptions.queryTimeout() > 0)) {
            // apply query timeout
            criteria.setTimeout(queryOptions.queryTimeout());
        } else {
            // apply transaction timeout if necessary...
            SessionFactoryUtils.applyTransactionTimeout(criteria, context.getSessionFactory());
        }

        return criteria;
    }


    /**
     * @param genericQuery The generic query.
     * @param qmpi The query method parameter info.
     * @param context The query execution context.
     * @return Returns prepared query instance.
     */
    @SuppressWarnings("PMD")
    protected Query prepareQuery(GenericQuery genericQuery, QueryMethodParameterInfo qmpi,
        HibernateQueryExecutionContext context) {
        HibernateQueryDescriptor queryDesc = null;

        HibernateQueryOptions queryOptions = qmpi.getMethodAnnotation(HibernateQueryOptions.class);
        if (validateQueryGenerator(genericQuery.queryGenerator(), HibernateQueryGenerator.class)) {
            // query generator specified, so generate query dynamically...
            @SuppressWarnings("unchecked")
            Class<? extends HibernateQueryGenerator> generator =
                (Class<? extends HibernateQueryGenerator>)genericQuery.queryGenerator();
            queryDesc = generateQuery(generator, queryOptions, qmpi, context.getSession());
        } else if (StringUtils.isNotEmpty(genericQuery.query())) {
            // query specified, so use specified query...
            Query query = null;
            if (genericQuery.isNativeQuery()) {
                query = context.getSession().createSQLQuery(genericQuery.query());
                configureNativeQuery((SQLQuery)query, queryOptions, null);
            } else {
                query = context.getSession().createQuery(genericQuery.query());
            }
            queryDesc = new HibernateQueryDescriptor(query, false);
        } else {
            // resolve query-name via naming strategy...
            // Note: configureNativeQuery is not called for named-native-queries, this information
            // is supposed to be provided where the named-query is defined...
            String queryName = getQueryNamingStrategy().getQueryName(qmpi);
            queryDesc = new HibernateQueryDescriptor(context.getSession().getNamedQuery(queryName), false);
        }

        logger.debug("Using query: {}", queryDesc.getQuery().getQueryString().trim());

        // set result transformer if available
        ResultTransformer resultTransformer = getResultTransformer(queryOptions);
        if (resultTransformer != null) {
            queryDesc.getQuery().setResultTransformer(resultTransformer);
        }

        // set fetch size if available
        Integer fetchSize = getFetchSize(queryOptions, context.getFetchSize());
        if (fetchSize != null) {
            queryDesc.getQuery().setFetchSize(fetchSize);
        }

        // set first result if available
        Integer firstResult = getFirstResult(qmpi, genericQuery);
        if (firstResult != null) {
            queryDesc.getQuery().setFirstResult(firstResult);
        }

        // set max results if available
        Integer maxResults = getMaxResults(qmpi, genericQuery, context.getMaxResults());
        if (maxResults != null) {
            queryDesc.getQuery().setMaxResults(maxResults);
        }

        // handle caching...
        if (isCachingEnabled(context, queryOptions)) {
            queryDesc.getQuery().setCacheable(true);
            String cacheRegion = getCacheRegion(context, queryOptions);
            if (cacheRegion != null) {
                queryDesc.getQuery().setCacheRegion(cacheRegion);
            }
        }


        // query/session timeout (Andy)
        if ((queryOptions != null) && (queryOptions.queryTimeout() > 0)) {
            // apply query timeout
            queryDesc.getQuery().setTimeout(queryOptions.queryTimeout());
        } else {
            // apply transaction timeout if necessary...
            SessionFactoryUtils.applyTransactionTimeout(queryDesc.getQuery(), context.getSessionFactory());
        }

        String[] namedParameters = queryDesc.getQuery().getNamedParameters();
        if (namedParameters.length > 0) {
            setNamedParams(queryDesc, namedParameters, qmpi);
        } else if (!queryDesc.isGeneratedQuery()) {
            // only set positional params if query was not generated dynamically!
            setPositionalParams(queryDesc, qmpi);
        }
        return queryDesc.getQuery();
    }


    /**
     * @param clazz The generator class.
     * @param queryOptions The query options.
     * @param qmpi The query method parameter info.
     * @param session The hibernate session.
     * @return Returns a hibernate query descriptor instance.
     */
    protected HibernateQueryDescriptor generateQuery(Class<? extends HibernateQueryGenerator> clazz,
        HibernateQueryOptions queryOptions, QueryMethodParameterInfo qmpi, Session session) {
        HibernateQueryGenerator generator = ClassUtils.instantiateClass(clazz);
        String queryString = generator.generate(qmpi);

        HibernateQueryDescriptor queryDesc = null;
        if (generator instanceof HibernateNativeQueryGenerator) {
            SQLQuery query = session.createSQLQuery(queryString);
            HibernateNativeQueryGenerator sqlGenerator = (HibernateNativeQueryGenerator)generator;
            configureNativeQuery(query, queryOptions, sqlGenerator);
            queryDesc = new HibernateQueryDescriptor(query, true, generator.getDynamicQueryParams());
        } else {
            Query query = session.createQuery(queryString);
            queryDesc = new HibernateQueryDescriptor(query, true, generator.getDynamicQueryParams());
        }
        return queryDesc;
    }

    /**
     * @param query The native query object.
     * @param queryOptions The query options.
     * @param generator The native query generator (may be null).
     */
    protected void configureNativeQuery(SQLQuery query, HibernateQueryOptions queryOptions,
        HibernateNativeQueryGenerator generator) {
        addEntityClasses(query, queryOptions, generator);
        addScalars(query, queryOptions, generator);
        addJoins(query, queryOptions, generator);
        addTimeout(query, queryOptions); // Andy: added Timeout
    }

    /**
     * @param sqlQuery The sql query.
     * @param queryOptions The query options.
     * @param generator The native query generator.
     */
    protected void addEntityClasses(SQLQuery sqlQuery, HibernateQueryOptions queryOptions,
        HibernateNativeQueryGenerator generator) {
        if (queryOptions != null) {
            for (EntityClass ec : queryOptions.entityClasses()) {
                if (StringUtils.isEmpty(ec.alias())) {
                    sqlQuery.addEntity(ec.clazz());
                } else {
                    sqlQuery.addEntity(ec.alias(), ec.clazz());
                }
            }
        }

        if (generator != null && generator.getEntities() != null) {
            Set<Class<?>> keySet = generator.getEntities().keySet();
            for (Class<?> entityClass : keySet) {
                String alias = generator.getEntities().get(entityClass);
                if (StringUtils.isEmpty(alias)) {
                    sqlQuery.addEntity(entityClass);
                } else {
                    sqlQuery.addEntity(alias, entityClass);
                }
            }
        }
    }

    /**
     * @param sqlQuery The sql query.
     * @param queryOptions The query options.
     * @param generator The native query generator.
     */
    protected void addScalars(SQLQuery sqlQuery, HibernateQueryOptions queryOptions,
        HibernateNativeQueryGenerator generator) {
        if (queryOptions != null) {
            for (Scalar s : queryOptions.scalars()) {
                if (isValidScalarType(s.type())) {
                    Type type = ClassUtils.instantiateClass(s.type());
                    sqlQuery.addScalar(s.alias(), type);
                } else {
                    sqlQuery.addScalar(s.alias());
                }
            }
        }

        if (generator != null && generator.getScalars() != null) {
            Set<String> keySet = generator.getScalars().keySet();
            for (String alias : keySet) {
                Type scalarType = generator.getScalars().get(alias);
                if (scalarType == null) {
                    sqlQuery.addScalar(alias);
                } else {
                    sqlQuery.addScalar(alias, scalarType);
                }
            }
        }
    }

    /**
     * @param sqlQuery The sql query.
     * @param queryOptions The query options.
     * @param generator The native query generator.
     */
    protected void addJoins(SQLQuery sqlQuery, HibernateQueryOptions queryOptions,
        HibernateNativeQueryGenerator generator) {
        if (queryOptions != null) {
            for (Join j : queryOptions.joins()) {
                sqlQuery.addJoin(j.alias(), j.path());
            }
        }

        if (generator != null && generator.getJoins() != null) {
            Set<String> keySet = generator.getJoins().keySet();
            for (String alias : keySet) {
                String path = generator.getJoins().get(alias);
                sqlQuery.addJoin(alias, path);
            }
        }
    }


    /**
     * @param sqlQuery The sql query.
     * @param queryOptions The query options.
     * @param generator The native query generator.
     */
    protected void addTimeout(SQLQuery sqlQuery, HibernateQueryOptions queryOptions) {

        if ((queryOptions != null) && (queryOptions.queryTimeout() > 0)) {
            sqlQuery.setTimeout(queryOptions.queryTimeout());
        }
    }


    /**
     * @param queryDesc The query descriptor.
     * @param namedParameters The named parameters of the query.
     * @param qmpi The query method parameter info.
     */
    protected void setNamedParams(HibernateQueryDescriptor queryDesc, String[] namedParameters,
        QueryMethodParameterInfo qmpi) {
        for (String namedParam : namedParameters) {
            HibernateQueryParam dnp = getNamedParam(namedParam, queryDesc, qmpi);

            logSetParameter(namedParam, dnp.getValue(), dnp.getType());

            if (dnp.getValue() instanceof Collection<?>) {
                if (dnp.getType() == null) {
                    queryDesc.getQuery().setParameterList(namedParam, (Collection<?>)dnp.getValue());
                } else {
                    queryDesc.getQuery().setParameterList(namedParam, (Collection<?>)dnp, dnp.getType());
                }
            } else if (dnp.getValue() != null && dnp.getValue().getClass().isArray()) {
                if (dnp.getType() == null) {
                    queryDesc.getQuery().setParameterList(namedParam, (Object[])dnp.getValue());
                } else {
                    queryDesc.getQuery().setParameterList(namedParam, (Object[])dnp.getValue(), dnp.getType());
                }
            } else {
                if (dnp.getType() == null) {
                    queryDesc.getQuery().setParameter(namedParam, dnp.getValue());
                } else {
                    queryDesc.getQuery().setParameter(namedParam, dnp.getValue(), dnp.getType());
                }
            }
        }
    }

    /**
     * @param queryDesc The query descriptor.
     * @param qmpi The query method parameter info.
     */
    protected void setPositionalParams(HibernateQueryDescriptor queryDesc, QueryMethodParameterInfo qmpi) {
        int index = 0;
        for (int i = 0; i < qmpi.getParameters().size(); i++) {
            Object value = qmpi.getParameter(i);

            if (!qmpi.parameterHasAnnotation(i, MaxResults.class) && !qmpi.parameterHasAnnotation(i, FirstResult.class)
                && !qmpi.parameterHasAnnotation(i, GScrollMode.class)) {

                Type argType = getArgumentType(i, value, qmpi);
                logSetParameter(index, value, argType);

                if (argType == null) {
                    queryDesc.getQuery().setParameter(index, value);
                } else {
                    queryDesc.getQuery().setParameter(index, value, argType);
                }

                // increment index
                index += 1;
            }
        }
    }

    /**
     * @param index The index.
     * @param value The value.
     * @param qmpi The query method parameter info.
     * @return Returns the argument type or null.
     */
    protected Type getArgumentType(int index, Object value, QueryMethodParameterInfo qmpi) {
        Type retVal = null;
        GType gType = qmpi.getParameterAnnotation(index, GType.class);
        if (gType != null) {
            retVal = ClassUtils.instantiateClass(gType.value());
        }

        if (retVal == null) {
            // type not set via GType annotation, check argumentType factory
            retVal = getArgumentType(value);
        }

        return retVal;
    }

    /**
     * @param value The value.
     * @return Returns the argument type or null.
     */
    protected Type getArgumentType(Object value) {
        Type argType = null;
        if (getArgumentTypeFactory() != null) {
            argType = getArgumentTypeFactory().getArgumentType(value);
        }
        return argType;
    }

    /**
     * @param index The index.
     * @param value The value
     * @param argType The argument type.
     */
    protected void logSetParameter(int index, Object value, Type argType) {
        logSetParameter(String.valueOf(index), value, argType);
    }

    /**
     * @param name The parameter name.
     * @param value The parameter value.
     * @param argType The argument type.
     */
    protected void logSetParameter(String name, Object value, Type argType) {
        if (logger.isDebugEnabled()) {
            StringBuilder str = new StringBuilder("Setting parameter '");
            str.append(name);
            str.append("' to '");
            str.append(value);
            str.append("'");
            if (argType != null) {
                str.append(" using type '");
                str.append(argType.getClass().getName());
                str.append("'");
            }
            logger.debug(str.toString());
        }
    }

    /**
     * @param queryOptions The annotation.
     * @return Returns the result transformer or null.
     */
    protected ResultTransformer getResultTransformer(HibernateQueryOptions queryOptions) {
        ResultTransformer transformer = null;
        if (hasValidResultTransformer(queryOptions)) {
            if (ToListResultTransformer.class.isAssignableFrom(queryOptions.resultTransformer())) {
                transformer = Transformers.TO_LIST;
            } else if (AliasToEntityMapResultTransformer.class.isAssignableFrom(queryOptions.resultTransformer())) {
                transformer = Transformers.ALIAS_TO_ENTITY_MAP;
            } else if (ResultTransformer.class.isAssignableFrom(queryOptions.resultTransformer())) {
                transformer = (ResultTransformer)ClassUtils.instantiateClass(queryOptions.resultTransformer());
            } else {
                transformer = Transformers.aliasToBean(queryOptions.resultTransformer());
            }
        }

        if (transformer != null) {
            logger.debug("Using result transformer: {}", transformer.getClass());
        }
        return transformer;
    }

    /**
     * The fetch size to read from the given annotation.
     * 
     * @param queryOptions The annotation to check.
     * @param defaultValue The default value.
     * @return Returns the fetch size or {@code null}.
     */
    protected Integer getFetchSize(HibernateQueryOptions queryOptions, Integer defaultValue) {
        Integer retVal = null;

        if (defaultValue != null && defaultValue > 0) {
            retVal = defaultValue;
        }
        if (queryOptions != null && queryOptions.fetchSize() > 0) {
            retVal = queryOptions.fetchSize();
        }

        if (retVal != null) {
            logger.debug("Using fetch size: {}", retVal);
        }
        return retVal;
    }

    /**
     * Checks if caching for queries should be enabled. First checks the {@code context}'s caching property. This value
     * may be overridden via the given {@code queryOptions}'s caching property.
     * 
     * @param context The context.
     * @param queryOptions The annotation.
     * @return Returns {@code true} if caching should be enabled and {@code false} otherwise.
     */
    protected boolean isCachingEnabled(HibernateQueryExecutionContext context, HibernateQueryOptions queryOptions) {
        boolean retVal = (context.getCaching() != null && context.getCaching() == HibernateCaching.ENABLED);

        // check value from annotation
        if (queryOptions != null && queryOptions.caching() != HibernateCaching.UNDEFINED) {
            retVal = (queryOptions.caching() == HibernateCaching.ENABLED);
        }

        return retVal;
    }

    /**
     * Retrieves the cache region to use. First checks the {@code context}'s cacheRegion property. This value may be
     * overridden via the qiven {@code queryOptions}'s property.
     * 
     * @param context The context.
     * @param queryOptions The annotation.
     * @return Returns the cache region or {@code null} if none was specified.
     */
    protected String getCacheRegion(HibernateQueryExecutionContext context, HibernateQueryOptions queryOptions) {
        String retVal = null;

        if (StringUtils.isNotEmpty(context.getCacheRegion())) {
            retVal = context.getCacheRegion();
        }

        if (queryOptions != null && StringUtils.isNotEmpty(queryOptions.cacheRegion())) {
            retVal = queryOptions.cacheRegion();
        }

        return retVal;
    }

    /**
     * @param name The name of the param.
     * @param queryDesc The query descriptor.
     * @param qmpi The query method parameter info.
     * @return Returns the dynamic named param.
     */
    protected HibernateQueryParam getNamedParam(String name, HibernateQueryDescriptor queryDesc,
        QueryMethodParameterInfo qmpi) {
        HibernateQueryParam retVal = null;
        if (queryDesc.hasDynamicQueryParam(name)) {
            retVal = queryDesc.getDynamicQueryParam(name);
        } else {
            int index = qmpi.getParameterIndexByParamName(name);
            Object value = qmpi.getParameter(index);
            Type type = getArgumentType(index, value, qmpi);
            retVal = new HibernateQueryParam(name, value, type);
        }

        if (retVal.getType() == null) {
            Type argType = getArgumentType(retVal.getValue());
            if (argType != null) {
                retVal.setType(argType);
            }
        }

        return retVal;
    }

    public void setArgumentTypeFactory(ArgumentTypeFactory argumentTypeFactory) {
        this.argumentTypeFactory = argumentTypeFactory;
    }

    protected ArgumentTypeFactory getArgumentTypeFactory() {
        return argumentTypeFactory;
    }

    /**
     * @param queryOptions The query options.
     * @return Returns {@code true} if a valid generator is specified within the annotation and {@code false} otherwise.
     */
    protected static boolean hasValidCriteriaGenerator(HibernateQueryOptions queryOptions) {
        boolean retVal = false;
        if (queryOptions != null) {
            retVal =
                (queryOptions.criteriaGenerator() != null && queryOptions.criteriaGenerator() != PlaceHolderCriteriaGenerator.class);
        }
        return retVal;
    }

    /**
     * @param queryOptions The query options.
     * @return Returns {@code true} if a valid tranformer is specified within the annotation and {@code false}
     *         otherwise.
     */
    protected static boolean hasValidResultTransformer(HibernateQueryOptions queryOptions) {
        boolean retVal = false;
        if (queryOptions != null) {
            retVal =
                (queryOptions.resultTransformer() != null && queryOptions.resultTransformer() != PlaceHolderResultTransformer.class);
        }
        return retVal;
    }

    /**
     * @param typeClass The type class.
     * @return Returns {@code true} if the given class is a valid type-class and {@code false} otherwise.
     */
    protected static boolean isValidScalarType(Class<? extends Type> typeClass) {
        return (typeClass != null && typeClass != PlaceHolderType.class);
    }
}