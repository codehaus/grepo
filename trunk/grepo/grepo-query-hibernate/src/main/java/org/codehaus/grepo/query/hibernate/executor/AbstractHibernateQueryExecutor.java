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

package org.codehaus.grepo.query.hibernate.executor;    //NOPMD

import java.util.Collection;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.grepo.exception.ConfigurationException;
import org.codehaus.grepo.query.commons.annotation.FirstResult;
import org.codehaus.grepo.query.commons.annotation.GenericQuery;
import org.codehaus.grepo.query.commons.annotation.MaxResults;
import org.codehaus.grepo.query.commons.aop.QueryMethodParameterInfo;
import org.codehaus.grepo.query.commons.executor.AbstractQueryExecutor;
import org.codehaus.grepo.query.hibernate.annotation.EntityClass;
import org.codehaus.grepo.query.hibernate.annotation.GScrollMode;
import org.codehaus.grepo.query.hibernate.annotation.GType;
import org.codehaus.grepo.query.hibernate.annotation.HibernateQueryOptions;
import org.codehaus.grepo.query.hibernate.annotation.Join;
import org.codehaus.grepo.query.hibernate.annotation.Scalar;
import org.codehaus.grepo.query.hibernate.converter.PlaceHolderResultTransformer;
import org.codehaus.grepo.query.hibernate.generator.CriteriaGenerator;
import org.codehaus.grepo.query.hibernate.generator.HibernateNativeQueryGenerator;
import org.codehaus.grepo.query.hibernate.generator.HibernateQueryGenerator;
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

/**
 * @author dguggi
 */
public abstract class AbstractHibernateQueryExecutor extends AbstractQueryExecutor<Session>
    implements HibernateQueryExecutor {

    /** The logger for this class. */
    private static final Log LOG = LogFactory.getLog(AbstractHibernateQueryExecutor.class);

    /** The argument type factory. */
    private ArgumentTypeFactory argumentTypeFactory;

    /**
     * @param clazz The generator class.
     * @param qmpi The query method parameter info.
     * @return Returns detached critieria object.
     */
    protected DetachedCriteria generateCriteria(Class<? extends CriteriaGenerator> clazz,
                QueryMethodParameterInfo qmpi) {
        try {
            return clazz.newInstance().generate(qmpi);
        } catch (InstantiationException e) {
            throw ConfigurationException.instantiateException(clazz, e);
        } catch (IllegalAccessException e) {
            throw ConfigurationException.accessException(clazz, e);
        }
    }

    /**
     * @param genericQuery The annotation.
     * @param qmpi The query method parameter info.
     * @param session The hibernate session.
     * @return Returns the prepared critiera instance.
     */
    protected Criteria prepareCriteria(GenericQuery genericQuery, QueryMethodParameterInfo qmpi, Session session) {
        HibernateQueryOptions queryOptions = qmpi.getMethodAnnotation(HibernateQueryOptions.class);
        DetachedCriteria detachedCriteria = generateCriteria(queryOptions.criteriaGenerator(), qmpi);
        Criteria criteria = detachedCriteria.getExecutableCriteria(session);

        if (LOG.isTraceEnabled()) {
            LOG.trace("Using criteria generated by: " + queryOptions.criteriaGenerator());
        }

        // set result transformer if available
        ResultTransformer resultTransformer = getResultTransformer(queryOptions);
        if (resultTransformer != null) {
            criteria.setResultTransformer(resultTransformer);
        }

        // set first result if available
        Integer firstResult = getFirstResult(qmpi, genericQuery);
        if (firstResult != null) {
            criteria.setFirstResult(firstResult);
        }

        // set max results if available
        Integer maxResults = getMaxResults(qmpi, genericQuery);
        if (maxResults != null) {
            criteria.setMaxResults(maxResults);
        }

        return criteria;
    }

    /**
     * @param genericQuery The generic query.
     * @param qmpi The query method parameter info.
     * @param session The hibernate session.
     * @return Returns prepared query instance.
     */
    protected Query prepareQuery(GenericQuery genericQuery, QueryMethodParameterInfo qmpi, Session session) {
        HibernateQueryDescriptor queryDesc = null;

        HibernateQueryOptions queryOptions = qmpi.getMethodAnnotation(HibernateQueryOptions.class);
        if (validateQueryGenerator(genericQuery.queryGenerator(), HibernateQueryGenerator.class)) {
            // query generator specified, so generate query dynamically...
            @SuppressWarnings("unchecked")
            Class<? extends HibernateQueryGenerator> generator = (Class<? extends HibernateQueryGenerator>)genericQuery
                .queryGenerator();
            queryDesc = generateQuery(generator, queryOptions, qmpi, session);
        } else if (StringUtils.isNotEmpty(genericQuery.query())) {
            // query specified, so use specified query...
            Query query = null;
            if (genericQuery.isNativeQuery()) {
                query = session.createSQLQuery(genericQuery.query());
                configureNativeQuery((SQLQuery)query, queryOptions, null);
            } else {
                query = session.createQuery(genericQuery.query());
            }
            queryDesc = new HibernateQueryDescriptor(query, false);
        } else {
            // resolve query-name via naming strategy...
            // Note: configureNativeQuery is not called for named-native-queries, this information
            // is supposed to be provided where the named-query is defined...
            String queryName = getQueryNamingStrategy().getQueryName(qmpi);
            queryDesc = new HibernateQueryDescriptor(session.getNamedQuery(queryName), false);
        }

        if (LOG.isTraceEnabled()) {
            LOG.trace("Using query: " + queryDesc.getQuery().getQueryString().trim());
        }

        // set result transformer if available
        ResultTransformer resultTransformer = getResultTransformer(queryOptions);
        if (resultTransformer != null) {
            queryDesc.getQuery().setResultTransformer(resultTransformer);
        }

        // set first result if available
        Integer firstResult = getFirstResult(qmpi, genericQuery);
        if (firstResult != null) {
            queryDesc.getQuery().setFirstResult(firstResult);
        }

        // set max results if available
        Integer maxResults = getMaxResults(qmpi, genericQuery);
        if (maxResults != null) {
            queryDesc.getQuery().setMaxResults(maxResults);
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
        try {
            HibernateQueryDescriptor queryDesc = null;
            HibernateQueryGenerator generator = clazz.newInstance();
            String queryString = generator.generate(qmpi);

            if (generator instanceof HibernateNativeQueryGenerator) {
                SQLQuery query = session.createSQLQuery(queryString);
                HibernateNativeQueryGenerator sqlGenerator = (HibernateNativeQueryGenerator)generator;
                configureNativeQuery(query, queryOptions, sqlGenerator);
                queryDesc = new HibernateQueryDescriptor(query, true, generator.getDynamicNamedParams());
            } else {
                Query query = session.createQuery(queryString);
                queryDesc = new HibernateQueryDescriptor(query, true, generator.getDynamicNamedParams());
            }
            return queryDesc;
        } catch (InstantiationException e) {
            throw ConfigurationException.instantiateException(clazz, e);
        } catch (IllegalAccessException e) {
            throw ConfigurationException.accessException(clazz, e);
        }
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
    }

    /**
     * @param sqlQuery The sql query.
     * @param queryOptions The query options.
     * @param generator The native query generator.
     */
    private void addEntityClasses(SQLQuery sqlQuery, HibernateQueryOptions queryOptions,
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
    private void addScalars(SQLQuery sqlQuery, HibernateQueryOptions queryOptions,
            HibernateNativeQueryGenerator generator) {
        if (queryOptions != null) {
            for (Scalar s : queryOptions.scalars()) {
                if (isValidScalarType(s.type())) {
                    try {
                        Type type = s.type().newInstance();
                        sqlQuery.addScalar(s.alias(), type);
                    } catch (InstantiationException e) {
                        throw ConfigurationException.instantiateException(s.type(), e);
                    } catch (IllegalAccessException e) {
                        throw ConfigurationException.accessException(s.type(), e);
                    }
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
    private void addJoins(SQLQuery sqlQuery, HibernateQueryOptions queryOptions,
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
     * @param queryDesc The query descriptor.
     * @param namedParameters The named parameters of the query.
     * @param qmpi The query method parameter info.
     */
    private void setNamedParams(HibernateQueryDescriptor queryDesc, String[] namedParameters,
            QueryMethodParameterInfo qmpi) {
        for (String namedParam : namedParameters) {
            DynamicNamedHibernateParam dnp = getNamedParam(namedParam, queryDesc, qmpi);

            traceSetParameter(namedParam, dnp.getValue(), dnp.getType());

            if (dnp.getValue() instanceof Collection) {
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
    private void setPositionalParams(HibernateQueryDescriptor queryDesc, QueryMethodParameterInfo qmpi) {
        int index = 0;
        for (int i = 0; i < qmpi.getParameters().size(); i++) {
            Object value = qmpi.getParameter(i);

            if (!qmpi.parameterHasAnnotation(i, MaxResults.class) && !qmpi.parameterHasAnnotation(i, FirstResult.class)
                && !qmpi.parameterHasAnnotation(i, GScrollMode.class)) {

                Type argType = getArgumentType(i, value, qmpi);
                traceSetParameter(index, value, argType);

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
    private Type getArgumentType(int index, Object value, QueryMethodParameterInfo qmpi) {
        Type retVal = null;
        GType gType = qmpi.getParameterAnnotation(index, GType.class);
        if (gType != null) {
            try {
                retVal = gType.value().newInstance();
            } catch (InstantiationException e) {
                throw ConfigurationException.instantiateException(gType.value(), e);
            } catch (IllegalAccessException e) {
                throw ConfigurationException.accessException(gType.value(), e);
            }
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
    private Type getArgumentType(Object value) {
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
    private void traceSetParameter(int index, Object value, Type argType) {
        traceSetParameter(String.valueOf(index), value, argType);
    }

    /**
     * @param name The parameter name.
     * @param value The parameter value.
     * @param argType The argument type.
     */
    private void traceSetParameter(String name, Object value, Type argType) {
        if (LOG.isTraceEnabled()) {
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
            LOG.trace(str.toString());
        }
    }

    /**
     * @param queryOptions The annotation.
     * @return Returns the result transformer or null.
     */
    private ResultTransformer getResultTransformer(HibernateQueryOptions queryOptions) {
        ResultTransformer transformer = null;
        if (hasValidResultTransformer(queryOptions)) {
            if (ToListResultTransformer.class.isAssignableFrom(queryOptions.resultTransformer())) {
                transformer = Transformers.TO_LIST;
            } else if (AliasToEntityMapResultTransformer.class.isAssignableFrom(queryOptions.resultTransformer())) {
                transformer = Transformers.ALIAS_TO_ENTITY_MAP;
            } else if (ResultTransformer.class.isAssignableFrom(queryOptions.resultTransformer())) {
                try {
                    transformer = (ResultTransformer)queryOptions.resultTransformer().newInstance();
                } catch (InstantiationException e) {
                    throw ConfigurationException.instantiateException(queryOptions.resultTransformer(), e);
                } catch (IllegalAccessException e) {
                    throw ConfigurationException.accessException(queryOptions.resultTransformer(), e);
                }
            } else {
                transformer = Transformers.aliasToBean(queryOptions.resultTransformer());
            }
        }

        if (transformer != null && LOG.isTraceEnabled()) {
            LOG.trace("Using result transformer: " + transformer.getClass());
        }
        return transformer;
    }

    /**
     * @param name The name of the param.
     * @param queryDesc The query descriptor.
     * @param qmpi The query method parameter info.
     * @return Returns the dynamic named param.
     */
    private DynamicNamedHibernateParam getNamedParam(String name, HibernateQueryDescriptor queryDesc,
            QueryMethodParameterInfo qmpi) {
        DynamicNamedHibernateParam retVal = null;
        if (queryDesc.hasDynamicNamedParam(name)) {
            retVal = queryDesc.getDynamicNamedParam(name);
        } else {
            int index = qmpi.getParameterIndexByParamName(name);
            Object value = qmpi.getParameter(index);
            Type type = getArgumentType(index, value, qmpi);
            retVal = new DynamicNamedHibernateParam(name, value, type);
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
     * @return Returns {@code true} if a valid generator is specified within the annotation and {@code false}
     *         otherwise.
     */
    protected static boolean hasValidCriteriaGenerator(HibernateQueryOptions queryOptions) {
        boolean retVal = false;
        if (queryOptions != null) {
            retVal = (queryOptions.criteriaGenerator() != null
                    && queryOptions.criteriaGenerator() != PlaceHolderCriteriaGenerator.class);
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
            retVal = (queryOptions.resultTransformer() != null
                    && queryOptions.resultTransformer() != PlaceHolderResultTransformer.class);
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
