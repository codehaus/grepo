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

import org.aopalliance.intercept.MethodInterceptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.grepo.core.converter.ResultConversionService;
import org.codehaus.grepo.query.commons.executor.QueryExecutorFactory;
import org.codehaus.grepo.query.commons.executor.QueryExecutorFindingStrategy;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

/**
 * Factory bean used to create generic repository beans (aop proxies).
 *
 * @author dguggi
 * @param <T> The entity class type.
 */
public class GenericRepositoryFactoryBean<T> implements FactoryBean, InitializingBean, ApplicationContextAware {

    /** The logger for this class. */
    private static final Log LOG = LogFactory.getLog(GenericRepositoryFactoryBean.class);

    /** Log message for autodetection mode. */
    protected static final String AUTODETECT_MSG_UNABLE_NOTFOUND =
        "Unable to auto-detect grepo bean of type '%s' - no beans found";

    /** Log message for autodetection mode. */
    protected static final String AUTODETECT_MSG_UNABLE_TOOMANYFOUND =
        "Unable to auto-detect grepo bean of type '%s' - too many beans found (%s)";

    /** Log message for autodetection mode. */
    protected static final String AUTODETECT_MSG_SUCCESS = "Successfully auto-detected grepo bean of type '%s' (id=%s)";

    /** The mandatory interface to proxy. */
    private Class<?> proxyInterface;

    /** The mandatory target class. */
    private Class<?> targetClass;

    /** The application context. */
    private ApplicationContext applicationContext;

    /** Flag to control whether or not grepo beans should be auto-detected (default is {@code true}. */
    private boolean autoDetectGrepoBeans = true;

    /** The mandatory entity class  (may be retrieved automatically). */
    private Class<T> entityClass;

    /** The optional result conversion service (may be auto-detected). */
    private ResultConversionService resultConversionService;

    /** The mandatory query executor factory (may be auto-detected). */
    private QueryExecutorFactory queryExecutorFactory;

    /** The mandatory query executor finding strategy (may be auto-detected). */
    private QueryExecutorFindingStrategy queryExecutorFindingStrategy;

    /** The mandatory method interceptor (may be auto-detected). */
    private MethodInterceptor methodInterceptor;

    /** The optional transaction template. */
    private TransactionTemplate transactionTemplate;

    /** The optional read only transaction template. */
    private TransactionTemplate readOnlyTransactionTemplate;

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("PMD")
    public Object getObject() throws Exception {
        // validate factory configuration
        validate();

        // create target
        GenericRepositorySupport<T> target = createTarget();
        configureTarget(target);

        // create proxy
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(target);
        proxyFactory.setInterfaces(new Class[] { proxyInterface });
        proxyFactory.addAdvice(methodInterceptor);
        return proxyFactory.getProxy();
    }

    /**
     * {@inheritDoc}
     */
    public Class<?> getObjectType() {
        return proxyInterface;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSingleton() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("PMD")
    public void afterPropertiesSet() throws Exception {
        initEntityClass();
        initMethodInterceptor();
        initQueryExecutorFactory();
        initQueryExecutorFindingStrategy();
        initResultConversionService();
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

            entityClass = (Class<T>)GenericRepositoryUtils.getEntityClass(proxyInterface);
            if (entityClass == null) {
                String msg = String.format("Unable to retrieve entityClass via proxyInterface '%s'", proxyInterface
                    .getName());
                LOG.warn(msg);
            } else if (LOG.isDebugEnabled()) {
                String msg = String.format("Retrieved entityClass '%s' via proxyInterface '%s'", entityClass.getName(),
                    proxyInterface.getName());
                LOG.debug(msg);
            }
        }
    }

    /**
     * If the {@link #methodInterceptor} is not set and {@link #autoDetectGrepoBeans} is set to {@code true}, this
     * method tries to retrieve the {@link #methodInterceptor} automatically.
     */
    protected void initMethodInterceptor() {
        if (methodInterceptor == null && autoDetectGrepoBeans) {
            @SuppressWarnings("unchecked")
            Map<String, GenericQueryMethodInterceptor> beans = applicationContext
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
                methodInterceptor = entry.getValue();
                if (LOG.isDebugEnabled()) {
                    String msg = String.format(AUTODETECT_MSG_SUCCESS, GenericQueryMethodInterceptor.class.getName(),
                        entry.getKey());
                    LOG.debug(msg);
                }
            }
        }
    }

    /**
     * If the {@link #resultConversionService} is not set and {@link #autoDetectGrepoBeans} is set to {@code true}, this
     * method tries to retrieve the {@link #resultConversionService} automatically.
     */
    protected void initResultConversionService() {
        if (resultConversionService == null && autoDetectGrepoBeans) {
            @SuppressWarnings("unchecked")
            Map<String, ResultConversionService> beans = applicationContext
                .getBeansOfType(ResultConversionService.class);

            if (beans.isEmpty()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(String.format(AUTODETECT_MSG_UNABLE_NOTFOUND, ResultConversionService.class.getName()));
                }
            } else if (beans.size() > 1) {
                String msg = String.format(AUTODETECT_MSG_UNABLE_TOOMANYFOUND, ResultConversionService.class.getName(),
                    beans.keySet());
                LOG.warn(msg);
            } else {
                // we found excatly one bean...
                Entry<String, ResultConversionService> entry = beans.entrySet().iterator().next();
                resultConversionService = entry.getValue();
                if (LOG.isDebugEnabled()) {
                    String msg = String.format(AUTODETECT_MSG_SUCCESS, ResultConversionService.class.getName(), entry
                        .getKey());
                    LOG.debug(msg);
                }
            }
        }
    }

    /**
     * If the {@link #queryExecutorFactory} is not set and {@link #autoDetectGrepoBeans} is set to {@code true}, this
     * method tries to retrieve the {@link #queryExecutorFactory} automatically.
     */
    protected void initQueryExecutorFactory() {
        if (queryExecutorFactory == null && autoDetectGrepoBeans) {
            @SuppressWarnings("unchecked")
            Map<String, QueryExecutorFactory> beans = applicationContext.getBeansOfType(QueryExecutorFactory.class);

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
     * If the {@link #queryExecutorFindingStrategy} is not set and {@link #autoDetectGrepoBeans} is set to {@code true},
     * this method tries to retrieve the {@link #queryExecutorFindingStrategy} automatically.
     */
    protected void initQueryExecutorFindingStrategy() {
        if (queryExecutorFindingStrategy == null && autoDetectGrepoBeans) {
            @SuppressWarnings("unchecked")
            Map<String, QueryExecutorFindingStrategy> beans = applicationContext
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
     * Validates the configuration for this factory bean.
     */
    protected void validate() {
        validateProxyInterface();
        validateTargetClass();

        Assert.notNull(entityClass, "entityClass must not be null");
        Assert.notNull(methodInterceptor, "methodInterceptor must not be null");
        Assert.notNull(queryExecutorFactory, "queryExecutorFactory must not be null");
        Assert.notNull(queryExecutorFindingStrategy, "queryExecutorFindingStrategy must not be null");
    }

    /**
     * Validates the {@link #proxyInterface} property.
     */
    protected void validateProxyInterface() {
        Assert.notNull(proxyInterface, "proxyInterface must not be null");
        Assert.isTrue(proxyInterface.isInterface(), "proxyInterface is not an interface");
        Assert.isAssignable(GenericRepository.class, proxyInterface);
    }

    /**
     * Validates the {@link #targetClass} property.
     */
    protected void validateTargetClass() {
        Assert.notNull(targetClass, "targetClass must not be null");
        Assert.isAssignable(GenericRepositorySupport.class, targetClass);
    }

    /**
     * @return Returns a new target instance.
     * @throws Exception in case of errors.
     */
    @SuppressWarnings({ "unchecked", "PMD" })
    protected GenericRepositorySupport<T> createTarget() throws Exception {
        return (GenericRepositorySupport<T>)targetClass.newInstance();
    }

    /**
     * Configures the given {@code target} instance.
     *
     * @param target The instance to configure.
     */
    protected void configureTarget(GenericRepositorySupport<T> target) {
        // set mandatory properties...
        target.setEntityClass(entityClass);
        target.setQueryExecutorFactory(queryExecutorFactory);
        target.setQueryExecutorFindingStrategy(queryExecutorFindingStrategy);

        // set optional properties...
        if (resultConversionService != null) {
            target.setResultConversionService(resultConversionService);
        }
        if (transactionTemplate != null) {
            target.setTransactionTemplate(transactionTemplate);
        }
        if (readOnlyTransactionTemplate != null) {
            target.setReadOnlyTransactionTemplate(readOnlyTransactionTemplate);
        }
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public Class<?> getProxyInterface() {
        return proxyInterface;
    }

    public void setProxyInterface(Class<?> proxyInterface) {
        this.proxyInterface = proxyInterface;
    }

    public MethodInterceptor getMethodInterceptor() {
        return methodInterceptor;
    }

    public void setMethodInterceptor(MethodInterceptor methodInterceptor) {
        this.methodInterceptor = methodInterceptor;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public TransactionTemplate getTransactionTemplate() {
        return transactionTemplate;
    }

    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    public TransactionTemplate getReadOnlyTransactionTemplate() {
        return readOnlyTransactionTemplate;
    }

    public void setReadOnlyTransactionTemplate(TransactionTemplate readOnlyTransactionTemplate) {
        this.readOnlyTransactionTemplate = readOnlyTransactionTemplate;
    }

    public boolean isAutoDetectGrepoBeans() {
        return autoDetectGrepoBeans;
    }

    public void setAutoDetectGrepoBeans(boolean autoDetectGrepoBeans) {
        this.autoDetectGrepoBeans = autoDetectGrepoBeans;
    }

    public ResultConversionService getResultConversionService() {
        return resultConversionService;
    }

    public void setResultConversionService(ResultConversionService resultConversionService) {
        this.resultConversionService = resultConversionService;
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
