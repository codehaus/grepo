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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.grepo.core.repository.GenericRepositoryFactoryBean;
import org.codehaus.grepo.query.commons.executor.QueryExecutorFactory;
import org.codehaus.grepo.query.commons.executor.QueryExecutorFindingStrategy;
import org.springframework.util.Assert;

/**
 * Factory bean used to create generic repository beans (aop proxies).
 *
 * @author dguggi
 *
 * @param <E> The factory class type.
 */
public abstract class GenericQueryRepositoryFactoryBean<E> //
        extends GenericRepositoryFactoryBean<GenericRepositorySupport<E>> {

    /** The logger for this class. */
    private static final Log LOG = LogFactory.getLog(GenericQueryRepositoryFactoryBean.class);

    /** The mandatory entity class  (may be retrieved automatically). */
    private Class<E> entityClass;

    /** The mandatory query executor factory (may be auto-detected). */
    private QueryExecutorFactory queryExecutorFactory;

    /** The mandatory query executor finding strategy (may be auto-detected). */
    private QueryExecutorFindingStrategy queryExecutorFindingStrategy;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doInitialization() {
        super.doInitialization();
        initEntityClass();
        initQueryExecutorFactory();
        initQueryExecutorFindingStrategy();
    }

    /**
     * If the {@link #entityClass} is not set, this method tries to retrieve {@link #entityClass} via introspection of
     * the {@link #proxyInterface}.
     */
    @SuppressWarnings("unchecked")  // NOPMD
    protected void initEntityClass() {
        if (entityClass == null) {
            // no entity class is defined, so try to retrieve entity class
            // via introspection of the proxyInterface...
            validateProxyInterface();

            entityClass = (Class<E>)GenericRepositoryUtils.getEntityClass(getProxyInterface());
            if (entityClass == null) {
                String msg = String.format("Unable to retrieve entityClass via proxyInterface '%s'", getProxyInterface()
                    .getName());
                LOG.warn(msg);
            } else if (LOG.isDebugEnabled()) {
                String msg = String.format("Retrieved entityClass '%s' via proxyInterface '%s'", entityClass.getName(),
                    getProxyInterface().getName());
                LOG.debug(msg);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initMethodInterceptor() {
        if (getMethodInterceptor() == null && isAutoDetectBeans()) {
            @SuppressWarnings("unchecked")
            Map<String, GenericQueryMethodInterceptor> beans = getApplicationContext()
                .getBeansOfType(GenericQueryMethodInterceptor.class);

            if (beans.isEmpty()) {
                LOG.warn(String.format(AUTODETECT_MSG_UNABLE_NOTFOUND, GenericQueryMethodInterceptor.class.getName()));
            } else if (beans.size() > 1) {
                String msg = String.format(AUTODETECT_MSG_UNABLE_TOOMANYFOUND, GenericQueryMethodInterceptor.class
                    .getName(), beans.keySet());
                LOG.warn(msg);
            } else {
                // we found excatly one bean...
                Entry<String, GenericQueryMethodInterceptor> entry = beans.entrySet().iterator().next();
                setMethodInterceptor(entry.getValue());
                if (LOG.isDebugEnabled()) {
                    String msg = String.format(AUTODETECT_MSG_SUCCESS, GenericQueryMethodInterceptor.class.getName(),
                        entry.getKey());
                    LOG.debug(msg);
                }
            }
        }
    }

    /**
     * If the {@link #queryExecutorFactory} is not set and {@code isAutoDetectBeans()} returns {@code true}, this
     * method tries to retrieve the {@link #queryExecutorFactory} automatically.
     */
    protected void initQueryExecutorFactory() {
        if (queryExecutorFactory == null && isAutoDetectBeans()) {
            @SuppressWarnings("unchecked")
            Map<String, QueryExecutorFactory> beans = getApplicationContext()
                .getBeansOfType(QueryExecutorFactory.class);

            if (beans.isEmpty()) {
                LOG.warn(String.format(AUTODETECT_MSG_UNABLE_NOTFOUND, QueryExecutorFactory.class.getName()));
            } else if (beans.size() > 1) {
                String msg = String.format(AUTODETECT_MSG_UNABLE_TOOMANYFOUND, QueryExecutorFactory.class.getName(),
                    beans.keySet());
                LOG.warn(msg);
            } else {
                // we found excatly one bean...
                Entry<String, QueryExecutorFactory> entry = beans.entrySet().iterator().next();
                queryExecutorFactory = entry.getValue();
                if (LOG.isDebugEnabled()) {
                    String msg = String.format(AUTODETECT_MSG_SUCCESS, QueryExecutorFactory.class.getName(), entry
                        .getKey());
                    LOG.debug(msg);
                }
            }
        }
    }

    /**
     * If the {@link #queryExecutorFindingStrategy} is not set and {@code isAutoDetectBeans()} returns {@code true},
     * this method tries to retrieve the {@link #queryExecutorFindingStrategy} automatically.
     */
    protected void initQueryExecutorFindingStrategy() {
        if (queryExecutorFindingStrategy == null && isAutoDetectBeans()) {
            @SuppressWarnings("unchecked")
            Map<String, QueryExecutorFindingStrategy> beans = getApplicationContext()
                .getBeansOfType(QueryExecutorFindingStrategy.class);

            if (beans.isEmpty()) {
                LOG.warn(String.format(AUTODETECT_MSG_UNABLE_NOTFOUND, QueryExecutorFindingStrategy.class.getName()));
            } else if (beans.size() > 1) {
                String msg = String.format(AUTODETECT_MSG_UNABLE_TOOMANYFOUND, QueryExecutorFindingStrategy.class
                    .getName(), beans.keySet());
                LOG.warn(msg);
            } else {
                // we found exactly one bean...
                Entry<String, QueryExecutorFindingStrategy> entry = beans.entrySet().iterator().next();
                queryExecutorFindingStrategy = entry.getValue();
                if (LOG.isDebugEnabled()) {
                    String msg = String.format(AUTODETECT_MSG_SUCCESS, QueryExecutorFindingStrategy.class.getName(),
                        entry.getKey());
                    LOG.debug(msg);
                }
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
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateTargetClass() {
        super.validateTargetClass();
        Assert.isAssignable(GenericRepositorySupport.class, getTargetClass());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configureTarget(GenericRepositorySupport<E> target) {
        // set mandatory properties...
        target.setApplicationContext(getApplicationContext());
        target.setEntityClass(entityClass);
        target.setQueryExecutorFactory(queryExecutorFactory);
        target.setQueryExecutorFindingStrategy(queryExecutorFindingStrategy);

        // set optional properties...
        if (getResultConversionService() != null) {
            target.setResultConversionService(getResultConversionService());
        }
        if (getTransactionTemplate() != null) {
            target.setTransactionTemplate(getTransactionTemplate());
        }
        if (getReadOnlyTransactionTemplate() != null) {
            target.setReadOnlyTransactionTemplate(getReadOnlyTransactionTemplate());
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

}
