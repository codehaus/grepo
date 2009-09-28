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

package org.codehaus.grepo.procedure.repository;

import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.aopalliance.intercept.MethodInterceptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.grepo.core.converter.ResultConversionService;
import org.codehaus.grepo.procedure.cache.ProcedureCachingStrategy;
import org.codehaus.grepo.procedure.compile.ProcedureCompilationStrategy;
import org.codehaus.grepo.procedure.input.ProcedureInputGenerationStrategy;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

/**
 * Factory bean used to create generic procedure repository beans (aop proxies).
 *
 * @author dguggi
 */
public class GenericProcedureRepositoryFactoryBean implements FactoryBean, InitializingBean, ApplicationContextAware {

    /** The logger for this class. */
    private static final Log LOG = LogFactory.getLog(GenericProcedureRepositoryFactoryBean.class);

    /** Log message for autodetection mode. */
    protected static final String AUTODETECT_MSG_UNABLE_NOTFOUND =
        "Unable to auto-detect grepo bean of type '%s' - no beans found";

    /** Log message for autodetection mode. */
    protected static final String AUTODETECT_MSG_UNABLE_TOOMANYFOUND =
        "Unable to auto-detect grepo bean of type '%s' - too many beans found (%s)";

    /** Log message for autodetection mode. */
    protected static final String AUTODETECT_MSG_SUCCESS = "Successfully auto-detected grepo bean of type '%s' (id=%s)";

    /** Default target class to use. */
    private static final Class<GenericProcedureRepositoryImpl> DEFAULT_TARGET_CLASS =
        GenericProcedureRepositoryImpl.class;

    /** The mandatory interface to proxy. */
    private Class<?> proxyInterface;

    /** The mandatory target class (will be defaulted with {@link #DEFAULT_TARGET_CLASS}). */
    private Class<?> targetClass;

    /** The mandatory datasource. */
    private DataSource dataSource;

    /** The application context. */
    private ApplicationContext applicationContext;

    /** Flag to control whether or not grepo beans should be auto-detected (default is {@code true}. */
    private boolean autoDetectGrepoBeans = true;

    /** The optional result conversion service (may be auto-detected). */
    private ResultConversionService resultConversionService;

    /** The mandatory procedure caching strategy (may be auto-detected). */
    private ProcedureCachingStrategy procedureCachingStrategy;

    /** The mandatory procedure compilation strategy (may be auto-detected). */
    private ProcedureCompilationStrategy procedureCompilationStrategy;

    /** The the mandadory procedure input generation strategy (may be auto-detected). */
    private ProcedureInputGenerationStrategy procedureInputGenerationStrategy;

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
        GenericProcedureRepositorySupport target = createTarget();
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
        initTargetClass();
        initMethodInterceptor();
        initProcedureInputGenerationStrategy();
        initProcedureCachingStrategy();
        initProcedureCompilationStrategy();
        initResultConversionService();
    }

    /**
     * if the {@link #targetClass} is not set, this method sets the property to {@link #DEFAULT_TARGET_CLASS}.
     */
    protected void initTargetClass() {
        if (targetClass == null) {
            targetClass = DEFAULT_TARGET_CLASS;
        }
    }

    /**
     * If the {@link #methodInterceptor} is not set and {@link #autoDetectGrepoBeans} is set to {@code true}, this
     * method tries to retrieve the {@link #methodInterceptor} automatically.
     */
    protected void initMethodInterceptor() {
        if (methodInterceptor == null && autoDetectGrepoBeans) {
            @SuppressWarnings("unchecked")  // NOPMD
            Map<String, GenericProcedureMethodInterceptor> beans = applicationContext
                .getBeansOfType(GenericProcedureMethodInterceptor.class);

            if (beans.isEmpty()) {
                LOG.warn(String.format(AUTODETECT_MSG_UNABLE_NOTFOUND, GenericProcedureMethodInterceptor.class
                    .getName()));
            } else if (beans.size() > 1) {
                String msg = String.format(AUTODETECT_MSG_UNABLE_TOOMANYFOUND, GenericProcedureMethodInterceptor.class
                    .getName(), beans.keySet());
                LOG.warn(msg);
            } else {
                // we found excatly one bean...
                Entry<String, GenericProcedureMethodInterceptor> entry = beans.entrySet().iterator().next();
                methodInterceptor = entry.getValue();
                if (LOG.isDebugEnabled()) {
                    String msg = String.format(AUTODETECT_MSG_SUCCESS, GenericProcedureMethodInterceptor.class
                        .getName(), entry.getKey());
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
     * If the {@link #procedureInputGenerationStrategy} is not set and {@link #autoDetectGrepoBeans} is set to {@code
     * true}, this method tries to retrieve the {@link #procedureInputGenerationStrategy} automatically.
     */
    protected void initProcedureInputGenerationStrategy() {
        if (procedureInputGenerationStrategy == null && autoDetectGrepoBeans) {
            @SuppressWarnings("unchecked")
            Map<String, ProcedureInputGenerationStrategy> beans = applicationContext
                .getBeansOfType(ProcedureInputGenerationStrategy.class);

            if (beans.isEmpty()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(String.format(AUTODETECT_MSG_UNABLE_NOTFOUND, ProcedureInputGenerationStrategy.class
                        .getName()));
                }
            } else if (beans.size() > 1) {
                String msg = String.format(AUTODETECT_MSG_UNABLE_TOOMANYFOUND, ProcedureInputGenerationStrategy.class
                    .getName(), beans.keySet());
                LOG.warn(msg);
            } else {
                // we found excatly one bean...
                Entry<String, ProcedureInputGenerationStrategy> entry = beans.entrySet().iterator().next();
                procedureInputGenerationStrategy = entry.getValue();
                if (LOG.isDebugEnabled()) {
                    String msg = String.format(AUTODETECT_MSG_SUCCESS,
                        ProcedureInputGenerationStrategy.class.getName(), entry.getKey());
                    LOG.debug(msg);
                }
            }
        }
    }

    /**
     * If the {@link #procedureCachingStrategy} is not set and {@link #autoDetectGrepoBeans} is set to {@code
     * true}, this method tries to retrieve the {@link #procedureCachingStrategy} automatically.
     */
    protected void initProcedureCachingStrategy() {
        if (procedureCachingStrategy == null && autoDetectGrepoBeans) {
            @SuppressWarnings("unchecked")
            Map<String, ProcedureCachingStrategy> beans = applicationContext
                .getBeansOfType(ProcedureCachingStrategy.class);

            if (beans.isEmpty()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(String.format(AUTODETECT_MSG_UNABLE_NOTFOUND, ProcedureCachingStrategy.class
                        .getName()));
                }
            } else if (beans.size() > 1) {
                String msg = String.format(AUTODETECT_MSG_UNABLE_TOOMANYFOUND, ProcedureCachingStrategy.class
                    .getName(), beans.keySet());
                LOG.warn(msg);
            } else {
                // we found excatly one bean...
                Entry<String, ProcedureCachingStrategy> entry = beans.entrySet().iterator().next();
                procedureCachingStrategy = entry.getValue();
                if (LOG.isDebugEnabled()) {
                    String msg = String.format(AUTODETECT_MSG_SUCCESS,
                        ProcedureCachingStrategy.class.getName(), entry.getKey());
                    LOG.debug(msg);
                }
            }
        }
    }

    /**
     * If the {@link #procedureCompilationStrategy} is not set and {@link #autoDetectGrepoBeans} is set to {@code
     * true}, this method tries to retrieve the {@link #procedureCompilationStrategy} automatically.
     */
    protected void initProcedureCompilationStrategy() {
        if (procedureCompilationStrategy == null && autoDetectGrepoBeans) {
            @SuppressWarnings("unchecked")
            Map<String, ProcedureCompilationStrategy> beans = applicationContext
                .getBeansOfType(ProcedureCompilationStrategy.class);

            if (beans.isEmpty()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(String.format(AUTODETECT_MSG_UNABLE_NOTFOUND, ProcedureCompilationStrategy.class
                        .getName()));
                }
            } else if (beans.size() > 1) {
                String msg = String.format(AUTODETECT_MSG_UNABLE_TOOMANYFOUND, ProcedureCompilationStrategy.class
                    .getName(), beans.keySet());
                LOG.warn(msg);
            } else {
                // we found excatly one bean...
                Entry<String, ProcedureCompilationStrategy> entry = beans.entrySet().iterator().next();
                procedureCompilationStrategy = entry.getValue();
                if (LOG.isDebugEnabled()) {
                    String msg = String.format(AUTODETECT_MSG_SUCCESS,
                        ProcedureCompilationStrategy.class.getName(), entry.getKey());
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

        Assert.notNull(dataSource, "dataSource must not be null");
        Assert.notNull(methodInterceptor, "methodInterceptor must not be null");
        Assert.notNull(procedureInputGenerationStrategy, "procedureInputGenerationStrategy must not be null");
        Assert.notNull(procedureCompilationStrategy, "procedureCompilationStrategy must not be null");
        Assert.notNull(procedureCachingStrategy, "procedureCachingStrategy must not be null");
    }

    /**
     * Validates the {@link #proxyInterface} property.
     */
    protected void validateProxyInterface() {
        Assert.notNull(proxyInterface, "proxyInterface must not be null");
        Assert.isTrue(proxyInterface.isInterface(), "proxyInterface is not an interface");
    }

    /**
     * Validates the {@link #targetClass} property.
     */
    protected void validateTargetClass() {
        Assert.notNull(targetClass, "targetClass must not be null");
        Assert.isAssignable(GenericProcedureRepositorySupport.class, targetClass);
    }

    /**
     * @return Returns a new target instance.
     * @throws Exception in case of errors.
     */
    @SuppressWarnings("PMD")
    protected GenericProcedureRepositorySupport createTarget() throws Exception {
        return (GenericProcedureRepositorySupport)targetClass.newInstance();
    }

    /**
     * Configures the given {@code target} instance.
     *
     * @param target The instance to configure.
     */
    protected void configureTarget(GenericProcedureRepositorySupport target) {
        // set mandatory properties...
        target.setDataSource(dataSource);
        target.setProcedureInputGenerationStrategy(procedureInputGenerationStrategy);
        target.setProcedureCachingStrategy(procedureCachingStrategy);
        target.setProcedureCompilationStrategy(procedureCompilationStrategy);

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

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
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

    public ProcedureCachingStrategy getProcedureCachingStrategy() {
        return procedureCachingStrategy;
    }

    public void setProcedureCachingStrategy(ProcedureCachingStrategy procedureCachingStrategy) {
        this.procedureCachingStrategy = procedureCachingStrategy;
    }

    public ProcedureCompilationStrategy getProcedureCompilationStrategy() {
        return procedureCompilationStrategy;
    }

    public void setProcedureCompilationStrategy(ProcedureCompilationStrategy procedureCompilationStrategy) {
        this.procedureCompilationStrategy = procedureCompilationStrategy;
    }

    public ProcedureInputGenerationStrategy getProcedureInputGenerationStrategy() {
        return procedureInputGenerationStrategy;
    }

    public void setProcedureInputGenerationStrategy(ProcedureInputGenerationStrategy procedureInputGenerationStrategy) {
        this.procedureInputGenerationStrategy = procedureInputGenerationStrategy;
    }

}
