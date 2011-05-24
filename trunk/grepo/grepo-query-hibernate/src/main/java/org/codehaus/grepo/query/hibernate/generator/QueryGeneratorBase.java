/*
 * Copyright 2011 Grepo Committers.
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

package org.codehaus.grepo.query.hibernate.generator;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.codehaus.grepo.core.util.ClassUtils;
import org.codehaus.grepo.query.commons.annotation.FirstResult;
import org.codehaus.grepo.query.commons.annotation.GenericQuery;
import org.codehaus.grepo.query.commons.annotation.MaxResults;
import org.codehaus.grepo.query.commons.aop.QueryMethodParameterInfo;
import org.codehaus.grepo.query.commons.generator.DynamicQueryParamsAware;
import org.codehaus.grepo.query.commons.generator.DynamicQueryParamsAwareImpl;
import org.codehaus.grepo.query.commons.generator.GeneratorUtils;
import org.codehaus.grepo.query.hibernate.annotation.EntityClass;
import org.codehaus.grepo.query.hibernate.annotation.GScrollMode;
import org.codehaus.grepo.query.hibernate.annotation.GType;
import org.codehaus.grepo.query.hibernate.annotation.HibernateQueryOptions;
import org.codehaus.grepo.query.hibernate.annotation.Join;
import org.codehaus.grepo.query.hibernate.annotation.Scalar;
import org.codehaus.grepo.query.hibernate.context.HibernateQueryExecutionContext;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.SessionFactoryUtils;

/**
 * @author dguggi
 */
public abstract class QueryGeneratorBase implements HibernateQueryGenerator,
        DynamicQueryParamsAware<HibernateQueryParam> {

    private static final long serialVersionUID = -6852547996581856796L;

    private static Logger logger = LoggerFactory.getLogger(QueryGeneratorBase.class);

    /** Allows to suppress setting of positional parameters. */
    private boolean suppressSetPositionalParameters;

    /** Allows to suppress setting of named parameters. */
    private boolean suppressSetNamedParameters;

    private DynamicQueryParamsAware<HibernateQueryParam> dynamicParams =
        new DynamicQueryParamsAwareImpl<HibernateQueryParam>();


    public QueryGeneratorBase(boolean suppressSetNamedParameters, boolean suppressSetPositionalParameters) {
        this.suppressSetNamedParameters = suppressSetNamedParameters;
        this.suppressSetPositionalParameters = suppressSetPositionalParameters;
    }

    public QueryGeneratorBase() {
        this(false, false);
    }

    /**
     * {@inheritDoc}
     */
    public Query generate(QueryMethodParameterInfo qmpi, HibernateQueryExecutionContext context) {
        Query query = createQuery(qmpi, context);
        logger.debug("Using query: {}", query.getQueryString().trim());
        applyQuerySettings(qmpi, context, query);
        return query;
    }

    /**
     * Factory method to create a query object.
     *
     * @param qmpi The query method parameter info.
     * @param context The execution context.
     * @return Returns the generated query.
     */
    protected abstract Query createQuery(QueryMethodParameterInfo qmpi, HibernateQueryExecutionContext context);

    /**
     * {@inheritDoc}
     */
    public void addDynamicQueryParam(HibernateQueryParam param) {
        dynamicParams.addDynamicQueryParam(param);
    }

    /**
     * {@inheritDoc}
     */
    public void addDynamicQueryParams(HibernateQueryParam... params) {
        dynamicParams.addDynamicQueryParams(params);
    }

    /**
     * {@inheritDoc}
     */
    public HibernateQueryParam getDynamicQueryParam(String name) {
        return dynamicParams.getDynamicQueryParam(name);
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasDynamicQueryParam(String name) {
        return dynamicParams.hasDynamicQueryParam(name);
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasDynamicQueryParams() {
        return dynamicParams.hasDynamicQueryParams();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<HibernateQueryParam> getDynamicQueryParams() {
        return dynamicParams.getDynamicQueryParams();
    }

    /**
     * Applies required settings to the given {@code query} instance.
     *
     * @param qmpi The query method parameter info.
     * @param context The execution context.
     * @param query The query.
     */
    protected void applyQuerySettings(QueryMethodParameterInfo qmpi, HibernateQueryExecutionContext context, //
                                      Query query) {
        GenericQuery genericQuery = qmpi.getMethodAnnotation(GenericQuery.class);
        HibernateQueryOptions queryOptions = qmpi.getMethodAnnotation(HibernateQueryOptions.class);

        applyResultTransformerSetting(queryOptions, query);
        applyFetchSizeSetting(queryOptions, context, query);
        applyFirstResultSetting(genericQuery, qmpi, query);
        applyMaxResultsSetting(genericQuery, qmpi, context, query);
        applyCachingSetting(queryOptions, context, query);
        applyTimeoutSetting(context, query);

        if (query instanceof SQLQuery) {
            applyNativeQuerySettings(queryOptions, (SQLQuery)query);
        }
    }

    /**
     * Applies required native-settings to the given {@code native query} instance.
     *
     * @param queryOptions The query options.
     * @param query The query
     */
    protected void applyNativeQuerySettings(HibernateQueryOptions queryOptions, SQLQuery query) {
        if (queryOptions != null) {
            for (EntityClass ec : queryOptions.entityClasses()) {
                applyAddEntitySetting(ec.alias(), ec.clazz(), query);
            }
            for (Scalar s : queryOptions.scalars()) {
                applyAddScalarSetting(s.alias(), s.type(), query);
            }
            for (Join j : queryOptions.joins()) {
                applyAddJoinSetting(j.alias(), j.path(), query);
            }
        }
    }

    protected void applyResultTransformerSetting(HibernateQueryOptions queryOptions, Query query) {
        ResultTransformer resultTransformer = HibernateGeneratorUtils.getResultTransformer(queryOptions);
        if (resultTransformer != null) {
            query.setResultTransformer(resultTransformer);
        }
    }

    protected void applyFetchSizeSetting(HibernateQueryOptions queryOptions, HibernateQueryExecutionContext context,
                                         Query query) {
        Integer fetchSize = HibernateGeneratorUtils.getFetchSize(queryOptions, context.getFetchSize());
        if (fetchSize != null) {
            query.setFetchSize(fetchSize);
        }
    }

    protected void applyFirstResultSetting(GenericQuery genericQuery, QueryMethodParameterInfo qmpi, Query query) {
        Integer firstResult = GeneratorUtils.getFirstResult(qmpi, genericQuery);
        if (firstResult != null) {
            query.setFirstResult(firstResult);
        }
    }

    protected void applyMaxResultsSetting(GenericQuery genericQuery, QueryMethodParameterInfo qmpi,
                                          HibernateQueryExecutionContext context, Query query) {
        Integer maxResults = GeneratorUtils.getMaxResults(qmpi, genericQuery, context.getMaxResults());
        if (maxResults != null) {
            query.setMaxResults(maxResults);
        }
    }

    protected void applyCachingSetting(HibernateQueryOptions queryOptions, HibernateQueryExecutionContext context,
                                       Query query) {
        if (HibernateGeneratorUtils.isCachingEnabled(queryOptions, context)) {
            query.setCacheable(true);
            String cacheRegion = HibernateGeneratorUtils.getCacheRegion(queryOptions, context);
            if (cacheRegion != null) {
                query.setCacheRegion(cacheRegion);
            }
        }
    }

    protected void applyTimeoutSetting(HibernateQueryExecutionContext context, Query query) {
        SessionFactoryUtils.applyTransactionTimeout(query, context.getSessionFactory());
    }

    protected void applyAddEntitySetting(String alias, Class<?> entityClass, SQLQuery query) {
        if (StringUtils.isEmpty(alias)) {
            query.addEntity(entityClass);
        } else {
            query.addEntity(alias, entityClass);
        }
    }

    protected void applyAddScalarSetting(String columnAlias, Class<? extends Type> typeClass, SQLQuery query) {
        if (HibernateGeneratorUtils.isValidScalarType(typeClass)) {
            Type type = ClassUtils.instantiateClass(typeClass);
            applyAddScalarSetting(columnAlias, type, query);
        } else {
            applyAddScalarSetting(columnAlias, (Type)null, query);
        }
    }

    protected void applyAddScalarSetting(String columnAlias, Type type, SQLQuery query) {
        if (type == null) {
            query.addScalar(columnAlias);
        } else {
            query.addScalar(columnAlias, type);
        }
    }

    protected void applyAddJoinSetting(String alias, String path, SQLQuery query) {
        query.addJoin(alias, path);
    }

    /**
     * Applies the query parameters (named or positional) for the given {@code query}.
     *
     * @param qmpi The query method parameter info.
     * @param context The context.
     * @param query The query.
     */
    protected void applyQueryParameters(QueryMethodParameterInfo qmpi, HibernateQueryExecutionContext context,
                                        Query query) {
        String[] namedParameters = query.getNamedParameters();
        if (namedParameters.length > 0) {
            setNamedParameters(qmpi, namedParameters, context, query);
        } else {
            setPositionalParameters(qmpi, context, query);
        }
    }

    protected void setNamedParameters(QueryMethodParameterInfo qmpi, String[] namedParameters,
                                      HibernateQueryExecutionContext context, Query query) {
        if (!suppressSetNamedParameters) {
            for (String namedParam : namedParameters) {
                HibernateQueryParam dnp = getNamedParameter(qmpi, namedParam, context, query);

                logSetParameter(namedParam, dnp.getValue(), dnp.getType());

                if (dnp.getValue() instanceof Collection<?>) {
                    if (dnp.getType() == null) {
                        query.setParameterList(namedParam, (Collection<?>)dnp.getValue());
                    } else {
                        query.setParameterList(namedParam, (Collection<?>)dnp, dnp.getType());
                    }
                } else if (dnp.getValue() != null && dnp.getValue().getClass().isArray()) {
                    if (dnp.getType() == null) {
                        query.setParameterList(namedParam, (Object[])dnp.getValue());
                    } else {
                        query.setParameterList(namedParam, (Object[])dnp.getValue(), dnp.getType());
                    }
                } else {
                    if (dnp.getType() == null) {
                        query.setParameter(namedParam, dnp.getValue());
                    } else {
                        query.setParameter(namedParam, dnp.getValue(), dnp.getType());
                    }
                }
            }
        }
    }

    protected void setPositionalParameters(QueryMethodParameterInfo qmpi, HibernateQueryExecutionContext context,
                                           Query query) {
        if (!suppressSetPositionalParameters) {
            int index = 0;
            for (int i = 0; i < qmpi.getParameters().size(); i++) {
                if (isValidPositionalParameter(qmpi, i)) {
                    Object value = qmpi.getParameter(i);

                    Type argType = getArgumentType(i, value, qmpi, context);
                    logSetParameter(index, value, argType);

                    if (argType == null) {
                        query.setParameter(index, value);
                    } else {
                        query.setParameter(index, value, argType);
                    }

                    // increment index
                    index += 1;
                }
            }
        }
    }

    private boolean isValidPositionalParameter(QueryMethodParameterInfo qmpi, int index) {
        return !qmpi.parameterHasAnnotation(index, MaxResults.class)
            && !qmpi.parameterHasAnnotation(index, FirstResult.class)
            && !qmpi.parameterHasAnnotation(index, GScrollMode.class);
    }

    private HibernateQueryParam getNamedParameter(QueryMethodParameterInfo qmpi, String name,
                                                  HibernateQueryExecutionContext context, Query query) {
        HibernateQueryParam retVal = null;
        if (hasDynamicQueryParam(name)) {
            retVal = getDynamicQueryParam(name);
        } else {
            int index = qmpi.getParameterIndexByParamName(name);
            Object value = qmpi.getParameter(index);
            Type type = getArgumentType(index, value, qmpi, context);
            retVal = new HibernateQueryParam(name, value, type);
        }

        if (retVal.getType() == null) {
            Type argType = getArgumentType(retVal.getValue(), context);
            if (argType != null) {
                retVal.setType(argType);
            }
        }
        return retVal;
    }

    private Type getArgumentType(int index, Object value, QueryMethodParameterInfo qmpi,
                                 HibernateQueryExecutionContext context) {
        Type retVal = null;
        GType gType = qmpi.getParameterAnnotation(index, GType.class);
        if (gType != null) {
            retVal = ClassUtils.instantiateClass(gType.value());
        }

        if (retVal == null) {
            // type not set via GType annotation, check argumentType factory
            retVal = getArgumentType(value, context);
        }

        return retVal;
    }

    protected Type getArgumentType(Object value, HibernateQueryExecutionContext context) {
        Type argType = null;
        if (context.getArgumentTypeFactory() != null) {
            argType = context.getArgumentTypeFactory().getArgumentType(value);
        }
        return argType;
    }

    protected boolean isNativeQuery(GenericQuery genericQuery) {
        return genericQuery.isNativeQuery();
    }


    private void logSetParameter(int index, Object value, Type argType) {
        logSetParameter(String.valueOf(index), value, argType);
    }

    private void logSetParameter(String name, Object value, Type argType) {
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

    protected void setSuppressSetPositionalParameters(boolean suppressSetPositionalParameters) {
        this.suppressSetPositionalParameters = suppressSetPositionalParameters;
    }

    protected void setSuppressSetNamedParameters(boolean suppressSetNamedParameters) {
        this.suppressSetNamedParameters = suppressSetNamedParameters;
    }

}
