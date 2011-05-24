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

package org.codehaus.grepo.query.commons.repository;

import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.grepo.query.commons.executor.QueryExecutorFactory;
import org.codehaus.grepo.query.commons.executor.QueryExecutorFindingStrategy;
import org.codehaus.grepo.query.commons.naming.QueryNamingStrategy;
import org.codehaus.grepo.statistics.repository.GenericStatisticsRepositoryFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * Factory bean used to create generic repository beans (aop proxies).
 *
 * @author dguggi
 * @param <E> The factory class type.
 */
public abstract class GenericQueryRepositoryFactoryBean<E> extends
        GenericStatisticsRepositoryFactoryBean<GenericQueryRepositorySupport<E>> {

    private static final Logger logger = LoggerFactory.getLogger(GenericQueryRepositoryFactoryBean.class);

    /** The mandatory entity class (may be retrieved automatically). */
    private Class<E> entityClass;

    /** The mandatory query executor factory (may be auto-detected). */
    private QueryExecutorFactory queryExecutorFactory;

    /** The mandatory query executor finding strategy (may be auto-detected). */
    private QueryExecutorFindingStrategy queryExecutorFindingStrategy;

    /** The mandatory query naming strategy (may be auto-detected). */
    private QueryNamingStrategy queryNamingStrategy;

    /** The max results. */
    private Integer maxResults;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doInitialization() {
        super.doInitialization();
        initEntityClass();
        initQueryExecutorFactory();
        initQueryExecutorFindingStrategy();
        initStatisticsManager();
        initStatisticsEntryIdentifierGenerationStrategy();
        initQueryNamingStrategy();
    }

    /**
     * If the {@link #entityClass} is not set, this method tries to retrieve {@link #entityClass} via introspection of
     * the {@link #proxyInterface}.
     */
    @SuppressWarnings("unchecked")
    protected void initEntityClass() {
        if (entityClass == null) {
            // no entity class is defined, so try to retrieve entity class
            // via introspection of the proxyInterface...
            validateProxyInterface();

            entityClass = (Class<E>)GenericRepositoryUtils.getEntityClass(getProxyInterface());
            if (entityClass == null) {
                logger.warn("Unable to determine entityClass via proxyInterface '{}'", getProxyInterface().getName());
            } else {
                logger.debug("Determined entityClass '{}' via proxyInterface '{}'", entityClass.getName(),
                    getProxyInterface().getName());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initMethodInterceptor() {
        if (getMethodInterceptor() == null && isAutoDetectBeans()) {
            Map<String, GenericQueryMethodInterceptor> beans =
                getApplicationContext().getBeansOfType(GenericQueryMethodInterceptor.class);

            if (beans.isEmpty()) {
                logger.warn(AUTODETECT_MSG_UNABLE_NOTFOUND, GenericQueryMethodInterceptor.class.getName());
            } else if (beans.size() > 1) {
                logger.warn(AUTODETECT_MSG_UNABLE_TOOMANYFOUND, GenericQueryMethodInterceptor.class.getName(), beans
                    .keySet());
            } else {
                // we found excatly one bean...
                Entry<String, GenericQueryMethodInterceptor> entry = beans.entrySet().iterator().next();
                setMethodInterceptor(entry.getValue());
                logger.debug(AUTODETECT_MSG_SUCCESS, GenericQueryMethodInterceptor.class.getName(), entry.getKey());
            }
        }
    }

    /**
     * If the {@link #queryExecutorFactory} is not set and {@code isAutoDetectBeans()} returns {@code true}, this method
     * tries to retrieve the {@link #queryExecutorFactory} automatically.
     */
    protected void initQueryExecutorFactory() {
        if (queryExecutorFactory == null && isAutoDetectBeans()) {
            Map<String, QueryExecutorFactory> beans =
                getApplicationContext().getBeansOfType(QueryExecutorFactory.class);

            if (beans.isEmpty()) {
                logger.warn(AUTODETECT_MSG_UNABLE_NOTFOUND, QueryExecutorFactory.class.getName());
            } else if (beans.size() > 1) {
                logger.warn(AUTODETECT_MSG_UNABLE_TOOMANYFOUND, QueryExecutorFactory.class.getName(), beans.keySet());
            } else {
                // we found excatly one bean...
                Entry<String, QueryExecutorFactory> entry = beans.entrySet().iterator().next();
                queryExecutorFactory = entry.getValue();
                logger.debug(AUTODETECT_MSG_SUCCESS, QueryExecutorFactory.class.getName(), entry.getKey());
            }
        }
    }

    /**
     * If the {@link #queryExecutorFindingStrategy} is not set and {@code isAutoDetectBeans()} returns {@code true},
     * this method tries to retrieve the {@link #queryExecutorFindingStrategy} automatically.
     */
    protected void initQueryExecutorFindingStrategy() {
        if (queryExecutorFindingStrategy == null && isAutoDetectBeans()) {
            Map<String, QueryExecutorFindingStrategy> beans =
                getApplicationContext().getBeansOfType(QueryExecutorFindingStrategy.class);

            if (beans.isEmpty()) {
                logger.warn(AUTODETECT_MSG_UNABLE_NOTFOUND, QueryExecutorFindingStrategy.class.getName());
            } else if (beans.size() > 1) {
                logger.warn(AUTODETECT_MSG_UNABLE_TOOMANYFOUND, QueryExecutorFindingStrategy.class.getName(), beans
                    .keySet());
            } else {
                // we found exactly one bean...
                Entry<String, QueryExecutorFindingStrategy> entry = beans.entrySet().iterator().next();
                queryExecutorFindingStrategy = entry.getValue();
                logger.debug(AUTODETECT_MSG_SUCCESS, QueryExecutorFindingStrategy.class.getName(), entry.getKey());
            }
        }
    }

    /**
     * If the {@link #queryNamingStrategy} is not set and {@code isAutoDetectBeans()} returns {@code true}, this method
     * tries to retrieve the {@link #queryNamingStrategy} automatically.
     */
    protected void initQueryNamingStrategy() {
        if (queryNamingStrategy == null && isAutoDetectBeans()) {
            Map<String, QueryNamingStrategy> beans = getApplicationContext().getBeansOfType(QueryNamingStrategy.class);

            if (beans.isEmpty()) {
                logger.warn(AUTODETECT_MSG_UNABLE_NOTFOUND, QueryNamingStrategy.class.getName());
            } else if (beans.size() > 1) {
                logger.warn(AUTODETECT_MSG_UNABLE_TOOMANYFOUND, QueryNamingStrategy.class.getName(), beans.keySet());
            } else {
                // we found exactly one bean...
                Entry<String, QueryNamingStrategy> entry = beans.entrySet().iterator().next();
                queryNamingStrategy = entry.getValue();
                logger.debug(AUTODETECT_MSG_SUCCESS, QueryNamingStrategy.class.getName(), entry.getKey());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<?> getRequiredGenericRepositoryType() {
        return GenericQueryRepository.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validate() {
        super.validate();
        Assert.notNull(entityClass, "entityClass must not be null");
        Assert.notNull(queryExecutorFactory, "queryExecutorFactory must not be null");
        Assert.notNull(queryExecutorFindingStrategy, "queryExecutorFindingStrategy must not be null");
        Assert.notNull(queryNamingStrategy, "queryNamingStrategy must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateTargetClass() {
        super.validateTargetClass();
        Assert.isAssignable(GenericQueryRepositorySupport.class, getTargetClass());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configureTarget(GenericQueryRepositorySupport<E> target) {
        super.configureTarget(target);
        // set mandatory properties...
        target.setEntityClass(entityClass);
        target.setQueryExecutorFactory(queryExecutorFactory);
        target.setQueryExecutorFindingStrategy(queryExecutorFindingStrategy);
        target.setQueryNamingStrategy(queryNamingStrategy);

        // set optional properties...
        if (maxResults != null) {
            target.setMaxResults(maxResults);
        }
    }

    public Class<E> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<E> entityClass) {
        this.entityClass = entityClass;
    }

    public QueryExecutorFactory getQueryExecutorFactory() {
        return queryExecutorFactory;
    }

    public void setQueryExecutorFactory(QueryExecutorFactory queryExecutorFactory) {
        this.queryExecutorFactory = queryExecutorFactory;
    }

    public QueryExecutorFindingStrategy getQueryExecutorFindingStrategy() {
        return queryExecutorFindingStrategy;
    }

    public void setQueryExecutorFindingStrategy(QueryExecutorFindingStrategy queryExecutorFindingStrategy) {
        this.queryExecutorFindingStrategy = queryExecutorFindingStrategy;
    }

    public QueryNamingStrategy getQueryNamingStrategy() {
        return queryNamingStrategy;
    }

    public void setQueryNamingStrategy(QueryNamingStrategy queryNamingStrategy) {
        this.queryNamingStrategy = queryNamingStrategy;
    }

    public Integer getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(Integer maxResults) {
        this.maxResults = maxResults;
    }

}
