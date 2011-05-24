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

package org.codehaus.grepo.query.jpa.generator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.persistence.QueryHint;
import javax.persistence.TemporalType;

import org.codehaus.grepo.core.annotation.Param;
import org.codehaus.grepo.query.commons.annotation.FirstResult;
import org.codehaus.grepo.query.commons.annotation.GenericQuery;
import org.codehaus.grepo.query.commons.annotation.MaxResults;
import org.codehaus.grepo.query.commons.aop.QueryMethodParameterInfo;
import org.codehaus.grepo.query.commons.generator.DynamicQueryParamsAware;
import org.codehaus.grepo.query.commons.generator.DynamicQueryParamsAwareImpl;
import org.codehaus.grepo.query.commons.generator.GeneratorUtils;
import org.codehaus.grepo.query.jpa.annotation.GTemporal;
import org.codehaus.grepo.query.jpa.annotation.JpaQueryOptions;
import org.codehaus.grepo.query.jpa.context.JpaQueryExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dguggi
 */
public abstract class QueryGeneratorBase implements JpaQueryGenerator, DynamicQueryParamsAware<JpaQueryParam> {

    private static final long serialVersionUID = 2313992822474539874L;

    private static Logger logger = LoggerFactory.getLogger(QueryGeneratorBase.class);

    private static final String INVALID_TEMPORALTYPE_MSG =
        "TemporalType '{}' specified, but parameter-value '{}'"
            + " has unsupported type (either '{}' or '{}' is required) - ignoring specified temporal-type...";

    /** Allows to suppress setting of positional parameters. */
    private boolean suppressSetPositionalParameters;

    /** Allows to suppress setting of named parameters. */
    private boolean suppressSetNamedParameters;

    private DynamicQueryParamsAware<JpaQueryParam> dynamicParams = new DynamicQueryParamsAwareImpl<JpaQueryParam>();

    private Map<String, Object> hints;


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
    @Override
    public Query generate(QueryMethodParameterInfo qmpi, JpaQueryExecutionContext context) {
        Query query = createQuery(qmpi, context);
        logger.debug("Using query: {}", query.toString().trim());
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
    protected abstract Query createQuery(QueryMethodParameterInfo qmpi, JpaQueryExecutionContext context);


    protected Query createNativeQuery(String queryString, QueryMethodParameterInfo qmpi,
                                      JpaQueryExecutionContext context) {
        JpaQueryOptions queryOptions = qmpi.getMethodAnnotation(JpaQueryOptions.class);
        final Class<?> resultClass = getResultClass(queryOptions);
        final String resultSetMapping = getResultSetMapping(queryOptions);

        final Query query;
        if (resultClass == null) {
            if (resultSetMapping == null) {
                query = context.getEntityManager().createNativeQuery(queryString);
            } else {
                logger.debug("Using resultSetMapping: {}", resultSetMapping);
                query = context.getEntityManager().createNativeQuery(queryString, resultSetMapping);
            }
        } else {
            query = context.getEntityManager().createNativeQuery(queryString, resultClass);
            logger.debug("Using resultClass: {}", resultClass);
            if (resultSetMapping != null) {
                logger.warn("Both resultClass and resultSetMapping specified - using specified resultClass");
            }
        }
        return query;
    }

    protected Class<?> getResultClass(JpaQueryOptions queryOptions) {
        return JpaGeneratorUtils.getResultClass(queryOptions);
    }

    protected String getResultSetMapping(JpaQueryOptions queryOptions) {
        return JpaGeneratorUtils.getResultSetMapping(queryOptions);
    }

    /**
     * {@inheritDoc}
     */
    public void addDynamicQueryParam(JpaQueryParam param) {
        dynamicParams.addDynamicQueryParam(param);
    }

    /**
     * {@inheritDoc}
     */
    public void addDynamicQueryParams(JpaQueryParam... params) {
        dynamicParams.addDynamicQueryParams(params);
    }

    /**
     * {@inheritDoc}
     */
    public JpaQueryParam getDynamicQueryParam(String name) {
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
    public Collection<JpaQueryParam> getDynamicQueryParams() {
        return dynamicParams.getDynamicQueryParams();
    }

    /**
     * Applies required settings to the given {@code query} instance.
     *
     * @param qmpi The query method parameter info.
     * @param context The execution context.
     * @param query The query.
     */
    protected void applyQuerySettings(QueryMethodParameterInfo qmpi, JpaQueryExecutionContext context, Query query) {
        GenericQuery genericQuery = qmpi.getMethodAnnotation(GenericQuery.class);
        JpaQueryOptions queryOptions = qmpi.getMethodAnnotation(JpaQueryOptions.class);

        applyFirstResultSetting(genericQuery, qmpi, query);
        applyMaxResultsSetting(genericQuery, qmpi, context, query);
        applyAddHintsSetting(queryOptions, query);
    }

    protected void applyAddHintsSetting(JpaQueryOptions queryOptions, Query query) {
        if (queryOptions != null) {
            for (QueryHint hint : queryOptions.queryHints()) {
                query.setHint(hint.name(), hint.value());
                logger.debug("Setting hint name={} value={}", hint.name(), hint.value());
            }
        }
    }

    protected void applyFirstResultSetting(GenericQuery genericQuery, QueryMethodParameterInfo qmpi, Query query) {
        Integer firstResult = GeneratorUtils.getFirstResult(qmpi, genericQuery);
        if (firstResult != null) {
            query.setFirstResult(firstResult);
        }
    }

    protected void applyMaxResultsSetting(GenericQuery genericQuery, QueryMethodParameterInfo qmpi,
                                          JpaQueryExecutionContext context, Query query) {
        Integer maxResults = GeneratorUtils.getMaxResults(qmpi, genericQuery, context.getMaxResults());
        if (maxResults != null) {
            query.setMaxResults(maxResults);
        }
    }

    /**
     * Applies the query parameters (named or positional) for the given {@code query}.
     *
     * @param qmpi The query method parameter info.
     * @param context The context.
     * @param query The query.
     */
    protected void applyQueryParameters(QueryMethodParameterInfo qmpi, JpaQueryExecutionContext context, Query query) {
        if (hasNamedParameters(qmpi)) {
            setNamedParameters(qmpi, query);
        } else {
            setPositionalParameters(qmpi, query);
        }
    }

    protected boolean hasNamedParameters(QueryMethodParameterInfo qmpi) {
        List<Object> params = qmpi.getAnnotatedParameters(Param.class);
        boolean retVal = !params.isEmpty();
        if (params.isEmpty()) {
            retVal = hasDynamicQueryParams();
        }
        return retVal;
    }

    protected void setNamedParameters(QueryMethodParameterInfo qmpi, Query query) {
        if (!suppressSetNamedParameters) {
            List<String> alreadyHandeledParamNames = new ArrayList<String>();
            if (hasDynamicQueryParams()) {
                // first set all dynamic named parameters...
                for (JpaQueryParam dynParam : getDynamicQueryParams()) {
                    setNamedParameter(dynParam, query);
                    alreadyHandeledParamNames.add(dynParam.getName());
                }
            }

            // now handle all method-parameters annotated with @Param
            for (int i = 0; i < qmpi.getParameters().size(); i++) {
                if (qmpi.parameterHasAnnotation(i, Param.class)) {
                    Param queryParam = qmpi.getParameterAnnotation(i, Param.class);
                    if (queryParam != null && !alreadyHandeledParamNames.contains(queryParam.value())) {
                        Object value = qmpi.getParameter(i);
                        GTemporal gt = qmpi.getParameterAnnotation(i, GTemporal.class);
                        JpaQueryParam param =
                            new JpaQueryParam(queryParam.value(), value, (gt == null ? null : gt.value()));
                        setNamedParameter(param, query);
                    }
                }
            }
        }
    }

    protected void setPositionalParameters(QueryMethodParameterInfo qmpi, Query query) {
        if (!suppressSetPositionalParameters) {
            int index = 1;
            for (int i = 0; i < qmpi.getParameters().size(); i++) {
                if (isValidPositionalParameter(qmpi, i)) {
                    Object value = qmpi.getParameter(i);
                    GTemporal gt = qmpi.getParameterAnnotation(i, GTemporal.class);
                    setPositionalParameter(index, value, (gt == null ? null : gt.value()), query);

                    // increment index
                    index += 1;
                }
            }
        }
    }

    private boolean isValidPositionalParameter(QueryMethodParameterInfo qmpi, int index) {
        return !qmpi.parameterHasAnnotation(index, MaxResults.class)
            && !qmpi.parameterHasAnnotation(index, FirstResult.class);
    }

    protected void setNamedParameter(JpaQueryParam param, Query query) {
        if (param.getTemporalType() != null && param.getValue() == null) {
            // GREPO-58 handle null values AND temporalTypes correctly!
            logSetParameter(param.getName(), null, param.getTemporalType());
            query.setParameter(param.getName(), (Calendar)null, param.getTemporalType());
        } else if (param.getTemporalType() != null && param.getValue() instanceof Calendar) {
            logSetParameter(param.getName(), param.getValue(), param.getTemporalType());
            query.setParameter(param.getName(), (Calendar)param.getValue(), param.getTemporalType());
        } else if (param.getTemporalType() != null && param.getValue() instanceof Date) {
            logSetParameter(param.getName(), param.getValue(), param.getTemporalType());
            query.setParameter(param.getName(), (Date)param.getValue(), param.getTemporalType());
        } else {
            logSetParameter(param.getName(), param.getValue(), null);
            query.setParameter(param.getName(), param.getValue());
            if (param.getTemporalType() != null) {
                Object[] params =
                    new Object[] {param.getTemporalType().toString(), param.getValue(), Date.class.getName(),
                        Calendar.class.getName() };
                logger.warn(INVALID_TEMPORALTYPE_MSG, params);
            }
        }
    }

    protected void setPositionalParameter(int index, Object value, TemporalType temporalType, Query query) {
        if (temporalType != null && value == null) {
            // GREPO-58 handle null values AND temporalTypes correctly!
            logSetParameter(index, null, temporalType);
            query.setParameter(index, (Calendar)null, temporalType);
        } else if (temporalType != null && value instanceof Calendar) {
            logSetParameter(index, value, temporalType);
            query.setParameter(index, (Calendar)value, temporalType);
        } else if (temporalType != null && value instanceof Date) {
            logSetParameter(index, (Date)value, temporalType);
            query.setParameter(index, (Date)value, temporalType);
        } else {
            logSetParameter(index, value, null);
            query.setParameter(index, value);
            if (temporalType != null) {
                Object[] params =
                    new Object[] {temporalType.toString(), value, Date.class.getName(), Calendar.class.getName() };
                logger.warn(INVALID_TEMPORALTYPE_MSG, params);
            }
        }
    }

    protected boolean isNativeQuery(GenericQuery genericQuery) {
        return genericQuery.isNativeQuery();
    }

    protected void addHint(String key, Object value) {
        if (hints == null) {
            hints = new HashMap<String, Object>();
        }
        hints.put(key, value);
    }

    protected boolean hasHints() {
        return (hints != null && !hints.isEmpty());
    }

    private void logSetParameter(int index, Object value, TemporalType temporalType) {
        logSetParameter(String.valueOf(index), value, temporalType);
    }

    private void logSetParameter(String name, Object value, TemporalType temporalType) {
        if (logger.isDebugEnabled()) {
            StringBuilder str = new StringBuilder("Setting parameter '");
            str.append(name);
            str.append("' to '");
            str.append(value);
            str.append("'");
            if (temporalType != null) {
                str.append(" using temporal-type '");
                str.append(temporalType.name());
                str.append("'");
            }
            logger.debug(str.toString());
        }
    }
}
