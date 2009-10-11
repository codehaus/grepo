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

package org.codehaus.grepo.query.jpa.executor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.QueryHint;
import javax.persistence.TemporalType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.grepo.core.annotation.Param;
import org.codehaus.grepo.exception.ConfigurationException;
import org.codehaus.grepo.query.commons.annotation.FirstResult;
import org.codehaus.grepo.query.commons.annotation.GenericQuery;
import org.codehaus.grepo.query.commons.annotation.MaxResults;
import org.codehaus.grepo.query.commons.aop.QueryMethodParameterInfo;
import org.codehaus.grepo.query.commons.executor.AbstractQueryExecutor;
import org.codehaus.grepo.query.jpa.annotation.GTemporal;
import org.codehaus.grepo.query.jpa.annotation.JpaQueryOptions;
import org.codehaus.grepo.query.jpa.generator.JpaNativeQueryGenerator;
import org.codehaus.grepo.query.jpa.generator.JpaQueryGenerator;
import org.codehaus.grepo.query.jpa.generator.JpaQueryParam;
import org.springframework.util.CollectionUtils;

/**
 * Abstract jpa query executor.
 *
 * @author dguggi
 */
public abstract class AbstractJpaQueryExecutor
    extends AbstractQueryExecutor<EntityManager> implements JpaQueryExecutor {

    /** The logger for this class. */
    private static final Log LOG = LogFactory.getLog(AbstractJpaQueryExecutor.class);

    /**
     * @param genericQuery The annotation.
     * @param qmpi The query method parameter info.
     * @param entityManager The entity manager.
     * @return Returns the query.
     */
    protected Query prepareQuery(GenericQuery genericQuery, QueryMethodParameterInfo qmpi,
            EntityManager entityManager) {
        JpaQueryDescriptor queryDesc = null;

        JpaQueryOptions queryOptions = qmpi.getMethodAnnotation(JpaQueryOptions.class);
        if (validateQueryGenerator(genericQuery.queryGenerator(), JpaQueryGenerator.class)) {
            // query generator specified, so generate query dynamically...
            @SuppressWarnings("unchecked")
            Class<? extends JpaQueryGenerator> jpaGenerator = (Class<? extends JpaQueryGenerator>)genericQuery
                .queryGenerator();
            queryDesc = generateQuery(jpaGenerator, queryOptions, qmpi, entityManager);
        } else if (StringUtils.isNotEmpty(genericQuery.query())) {
            // query specified, so use specified query...
            Query query = null;
            if (genericQuery.isNativeQuery()) {
                Class<?> resultClass = getResultClass(queryOptions, null);
                String resultSetMapping = getResultSetMapping(queryOptions, null);
                query = createNativeQuery(genericQuery.query(), resultClass, resultSetMapping, entityManager);
            } else {
                query = entityManager.createQuery(genericQuery.query());
            }
            // configure query options
            configureQuery(query, queryOptions, null);

            queryDesc = new JpaQueryDescriptor(query, false);
        } else {
            // resolve query-name via naming strategy...
            // Note: configureQuery is not invoked for named-queries, this information
            // is supposed to be provided where the named-query is defined...
            String queryName = getQueryNamingStrategy().getQueryName(qmpi);
            Query query = entityManager.createNamedQuery(queryName);
            queryDesc = new JpaQueryDescriptor(query, false);
        }

        if (LOG.isTraceEnabled()) {
            LOG.trace("Using query: " + queryDesc.getQuery().toString().trim());
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

        // set named/positional parameters
        if (hasNamedParameters(queryDesc, qmpi)) {
            setNamedParams(queryDesc, qmpi);
        } else if (!queryDesc.isGeneratedQuery()) {
            setPositionalParams(queryDesc, qmpi);
        }

        return queryDesc.getQuery();
    }

    /**
     * @param queryOptions The query options.
     * @param generator The query generator.
     * @return Returns the result class or null if not specified.
     */
    private Class<?> getResultClass(JpaQueryOptions queryOptions, JpaNativeQueryGenerator generator) {
        Class<?> resultClass = null;
        if (queryOptions != null && isValidResultClass(queryOptions.resultClass())) {
            resultClass = queryOptions.resultClass();
        }

        if (generator != null && isValidResultClass(generator.getResultClass())) {
            resultClass = generator.getResultClass();
        }
        return resultClass;
    }

    /**
     * @param queryOptions The query options.
     * @param generator The query generator.
     * @return Returns the result set mapping or null if not specified.
     */
    private String getResultSetMapping(JpaQueryOptions queryOptions, JpaNativeQueryGenerator generator) {
        String resultSetMapping = null;
        if (queryOptions != null && StringUtils.isNotEmpty(queryOptions.resultSetMapping())) {
            resultSetMapping = queryOptions.resultSetMapping();
        }

        if (generator != null && StringUtils.isNotEmpty(generator.getResultSetMapping())) {
            resultSetMapping = generator.getResultSetMapping();
        }
        return resultSetMapping;
    }

    /**
     * @param queryString The query string.
     * @param resultClass The result class.
     * @param resultSetMapping The result set mapping.
     * @param entityManager The entity manager.
     * @return Returns the created query.
     */
    private Query createNativeQuery(String queryString, Class<?> resultClass, String resultSetMapping,
            EntityManager entityManager) {
        Query query = null;
        if (resultClass == null) {
            // no result class specified, so check for result set mapping...
            if (resultSetMapping == null) {
                // no result set mapping specified...
                query = entityManager.createNativeQuery(queryString);
            } else {
                // result set mapping specified...
                query = entityManager.createNativeQuery(queryString, resultSetMapping);
            }
        } else {
            // result class specified...
            query = entityManager.createNativeQuery(queryString, resultClass);
            if (resultSetMapping != null) {
                LOG.warn("Both resultClass and resultSetMapping specified - using specified resultClass");
            }
        }
        return query;
    }

    /**
     * @param query The query.
     * @param queryOptions The query options.
     * @param generator The query generator.
     */
    protected void configureQuery(Query query, JpaQueryOptions queryOptions, JpaQueryGenerator generator) {
        addHints(query, queryOptions, generator);
    }

    /**
     * @param query The query.
     * @param queryOptions The query options.
     * @param generator The query generator.
     */
    private void addHints(Query query, JpaQueryOptions queryOptions, JpaQueryGenerator generator) {
        if (queryOptions != null) {
            for (QueryHint hint : queryOptions.queryHints()) {
                query.setHint(hint.name(), hint.value());
                if (LOG.isTraceEnabled()) {
                    LOG.trace(String.format("Setting hint name=%s value=%s", hint.name(), hint.value()));
                }
            }
        }

        if (generator != null && !CollectionUtils.isEmpty(generator.getHints())) {
            // we have hints specified, so set hints on query object...
            for (Entry<String, Object> entry : generator.getHints().entrySet()) {
                query.setHint(entry.getKey(), entry.getValue());
                if (LOG.isTraceEnabled()) {
                    LOG.trace(String.format("Setting hint name=%s value=%s", entry.getKey(), entry.getValue()));
                }
            }
        }
    }

    /**
     * @param queryDesc The query descriptor.
     * @param qmpi The query method parameter info.
     * @return Returns {@code true} if named parameters found.
     */
    protected boolean hasNamedParameters(JpaQueryDescriptor queryDesc, QueryMethodParameterInfo qmpi) {
        List<Object> params = qmpi.getAnnotatedParameters(Param.class);
        boolean retVal = !params.isEmpty();

        if (params.isEmpty()) {
            retVal = (queryDesc.isGeneratedQuery() && queryDesc.hasDynamicQueryParams());
        }
        return retVal;
    }

    /**
     * @param clazz The generator clazz.
     * @param queryOptions The query options.
     * @param qmpi The query method parameter info.
     * @param entityManager The entity manager.
     * @return Returns the jpa query descriptor.
     */
    protected JpaQueryDescriptor generateQuery(Class<? extends JpaQueryGenerator> clazz,
            JpaQueryOptions queryOptions, QueryMethodParameterInfo qmpi, EntityManager entityManager) {
        try {
            JpaQueryGenerator generator = clazz.newInstance();
            String queryString = generator.generate(qmpi);

            Query query = null;
            if (generator instanceof JpaNativeQueryGenerator) {
                JpaNativeQueryGenerator nativeGenerator = (JpaNativeQueryGenerator)generator;
                Class<?> resultClass = getResultClass(queryOptions, nativeGenerator);
                String resultSetMapping = getResultSetMapping(queryOptions, nativeGenerator);
                query = createNativeQuery(queryString, resultClass, resultSetMapping, entityManager);
            } else {
                query = entityManager.createQuery(queryString);
            }

            // configure query options...
            configureQuery(query, queryOptions, generator);

            return new JpaQueryDescriptor(query, true, generator.getDynamicQueryParams());

        } catch (InstantiationException e) {
            throw ConfigurationException.instantiateException(clazz, e);
        } catch (IllegalAccessException e) {
            throw ConfigurationException.accessException(clazz, e);
        }
    }

    /**
     * @param queryDesc The query descriptor.
     * @param qmpi The query method parameter info.
     */
    @SuppressWarnings("PMD")
    private void setNamedParams(JpaQueryDescriptor queryDesc, QueryMethodParameterInfo qmpi) {
        List<String> alreadyHandeledParamNames = new ArrayList<String>();
        if (queryDesc.isGeneratedQuery() && queryDesc.hasDynamicQueryParams()) {
            // first set all dynamic named parameters...
            for (JpaQueryParam dynParam : queryDesc.getDynamicQueryParams()) {
                setNamedParam(queryDesc.getQuery(), dynParam);
                alreadyHandeledParamNames.add(dynParam.getName());
            }
        }

        // now handle all method-parameters annotated with @Param
        for (int i = 0; i < qmpi.getParameters().size(); i++) {
            if (qmpi.parameterHasAnnotation(i, Param.class)) {
                Param queryParam = qmpi.getParameterAnnotation(i, Param.class);
                JpaQueryParam param = null;
                if (!alreadyHandeledParamNames.contains(queryParam.value())) {
                    Object value = qmpi.getParameter(i);
                    GTemporal gt = qmpi.getParameterAnnotation(i, GTemporal.class);
                    param = new JpaQueryParam(queryParam.value(), value,
                        (gt == null ? null : gt.value()));
                    setNamedParam(queryDesc.getQuery(), param);
                }
            }
        }
    }

    /**
     * @param query The query.
     * @param param The named param.
     */
    private void setNamedParam(Query query, JpaQueryParam param) {
        if (param.getTemporalType() != null && (param.getValue() instanceof Calendar)) {
            traceSetParameter(param.getName(), param.getValue(), param.getTemporalType());
            query.setParameter(param.getName(), (Calendar)param.getValue(), param.getTemporalType());
        } else if (param.getTemporalType() != null && (param.getValue() instanceof Date)) {
            traceSetParameter(param.getName(), param.getValue(), param.getTemporalType());
            query.setParameter(param.getName(), (Date)param.getValue(), param.getTemporalType());
        } else {
            traceSetParameter(param.getName(), param.getValue(), null);
            query.setParameter(param.getName(), param.getValue());
            if (param.getTemporalType() != null) {
                String msg = String.format(
                    "TemporalType '%s' specified, but parameter-value '%s' has unsupported type (eiter '%s' or '%s') "
                        + "is required - ignoring specified temporal-type...", param.getTemporalType().toString(),
                    param.getValue(), Date.class.getName(), Calendar.class.getName());
                LOG.warn(msg);
            }
        }
    }

    /**
     * @param queryDesc The query descriptor.
     * @param qmpi The query method parameter info.
     */
    private void setPositionalParams(JpaQueryDescriptor queryDesc, QueryMethodParameterInfo qmpi) {
        int index = 1;
        for (int i = 0; i < qmpi.getParameters().size(); i++) {
            if (!qmpi.parameterHasAnnotation(i, MaxResults.class)
                    && !qmpi.parameterHasAnnotation(i, FirstResult.class)) {

                Object value = qmpi.getParameter(i);
                GTemporal gt = qmpi.getParameterAnnotation(i, GTemporal.class);
                setPositionalParam(queryDesc, index, value, (gt == null ? null : gt.value()));

                // increment index
                index += 1;
            }
        }
    }

    /**
     * @param queryDesc The query descriptor.
     * @param index The index.
     * @param value The value.
     * @param temporalType The temporal type.
     */
    private void setPositionalParam(JpaQueryDescriptor queryDesc, int index, Object value,
            TemporalType temporalType) {
        if (temporalType != null && value instanceof Calendar) {
            traceSetParameter(index, value, temporalType);
            queryDesc.getQuery().setParameter(index, (Calendar)value, temporalType);
        } else if (temporalType != null && value instanceof Date) {
            traceSetParameter(index, (Date)value, temporalType);
            queryDesc.getQuery().setParameter(index, (Date)value, temporalType);
        } else {
            traceSetParameter(index, value, null);
            queryDesc.getQuery().setParameter(index, value);
            if (temporalType != null) {
                String msg = String.format(
                    "TemporalType '%s' specified, but parameter-value '%s' has unsupported type (eiter '%s' or '%s') "
                        + "is required - ignoring specified temporal-type...", temporalType.toString(), value,
                    Date.class.getName(), Calendar.class.getName());
                LOG.warn(msg);
            }
        }
    }

    /**
     * @param index The index.
     * @param value The value.
     * @param temporalType The temporal type.
     */
    private void traceSetParameter(int index, Object value, TemporalType temporalType) {
        traceSetParameter(String.valueOf(index), value, temporalType);
    }

    /**
     * @param name The parameter name.
     * @param value The parameter value.
     * @param temporalType The temporalType.
     */
    private void traceSetParameter(String name, Object value, TemporalType temporalType) {
        if (LOG.isTraceEnabled()) {
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
            LOG.trace(str.toString());
        }
    }

    /**
     * @param resultClass The class to check.
     * @return Returns {@code true} if the class is valid and {@code false} otherwise.
     */
    protected static boolean isValidResultClass(Class<?> resultClass) {
        return (resultClass != null && resultClass != PlaceHolderResultClass.class);
    }

}
