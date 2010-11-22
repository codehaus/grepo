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

package org.codehaus.grepo.core.repository;

import java.util.Map;
import java.util.Map.Entry;

import org.aopalliance.intercept.MethodInterceptor;
import org.codehaus.grepo.core.converter.ResultConversionService;
import org.codehaus.grepo.core.exception.ConfigurationException;
import org.codehaus.grepo.core.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * @param <T> The target class (base) type.
 */
public abstract class GenericRepositoryFactoryBean<T extends GenericRepositorySupport> implements FactoryBean,
            InitializingBean, ApplicationContextAware {
    /** Log message for autodetection mode. */
    protected static final String AUTODETECT_MSG_UNABLE_NOTFOUND =
        "Unable to auto-detect grepo bean of type '{}' - no bean found";

    /** Log message for autodetection mode. */
    protected static final String AUTODETECT_MSG_UNABLE_TOOMANYFOUND =
        "Unable to auto-detect grepo bean of type '{}' - too many beans found: {}";

    /** Log message for autodetection mode. */
    protected static final String AUTODETECT_MSG_SUCCESS = "Successfully auto-detected grepo bean of type '{}' (id={})";

    /** The logger for this class. */
    private final Logger logger = LoggerFactory.getLogger(GenericRepositoryFactoryBean.class); // NOPMD

    /** The mandatory interface to proxy. */
    private Class<?> proxyInterface;

    /** The mandatory target class. */
    private Class<?> targetClass;

    /** The optional proxy class, which may be used to determine {@link #proxyInterface} and {@link #targetClass}. */
    private Class<?> proxyClass;

    /** The application context. */
    private ApplicationContext applicationContext;

    /** Flag to control whether or not beans should be auto-detected (default is {@code true}. */
    private boolean autoDetectBeans = true;

    /** Flag to control whether or not bean validation is performed after
     * {@link #afterPropertiesSet()} method was invoked. */
    private boolean validateAfterPropertiesSet = true;

    /** The optional result conversion service (may be auto-detected). */
    private ResultConversionService resultConversionService;

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
        T target = createTarget();
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
    @SuppressWarnings("PMD")
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
    public final void afterPropertiesSet() throws Exception {
        doInitialization();
        // validate factory configuration
        if (validateAfterPropertiesSet) {
            validate();
        }
    }

    /**
     * Performs required initialization.
     */
    protected void doInitialization() {
        initProxyInterfaceAndTargetClass();
        initMethodInterceptor();
        initResultConversionService();
    }

    /**
     * If the {@link #methodInterceptor} is not set and {@link #autoDetectBean} is set to {@code true}, this
     * method can try to retrieve the {@link #methodInterceptor} automatically. This implementation
     * however does nothing and is supposed to be implemented by concrete classes.
     */
    @SuppressWarnings("PMD")
    protected void initMethodInterceptor() {
    }

    /**
     * If the {@link #resultConversionService} is not set and {@link #autoDetectBeans} is set to {@code true}, this
     * method tries to retrieve the {@link #resultConversionService} automatically.
     */
    protected void initResultConversionService() {
        if (resultConversionService == null && autoDetectBeans) {
            Map<String, ResultConversionService> beans = applicationContext
                .getBeansOfType(ResultConversionService.class);

            if (beans.isEmpty()) {
                logger.debug(AUTODETECT_MSG_UNABLE_NOTFOUND, ResultConversionService.class.getName());
            } else if (beans.size() > 1) {
                logger.warn(AUTODETECT_MSG_UNABLE_TOOMANYFOUND, ResultConversionService.class.getName(),
                    beans.keySet());
            } else {
                // we found excatly one bean...
                Entry<String, ResultConversionService> entry = beans.entrySet().iterator().next();
                resultConversionService = entry.getValue();
                logger.debug(AUTODETECT_MSG_SUCCESS, ResultConversionService.class.getName(),
                    entry.getKey());
            }
        }
    }

    /**
     * This method initializes the required settings for the generic repository (aop) proxy. That is setting
     * the {@link #proxyInterface}} property and the {@link #targetClass} property based on the
     * {@link #proxyClass} property.
     */
    protected void initProxyInterfaceAndTargetClass() {
        if (getProxyInterface() == null && getProxyClass() != null) {
            // proxy interface is not set, but proxy class is set, so we
            // try to retrieve proxy interface and eventuall target class
            // from the specified proxy class...
            if (getProxyClass().isInterface()) {
                setProxyInterface(getProxyClass());
            } else {
                boolean foundProxyInterface = false;

                Class<?>[] interfaces = getProxyClass().getInterfaces();
                for (Class<?> intf : interfaces) {
                    if (getRequiredGenericRepositoryType().isAssignableFrom(intf)) {
                        logger.debug("Determined proxyInterface '{}' for targetClass '{}'", intf.getName(),
                            getProxyClass().getName());

                        setProxyInterface(intf);
                        // if target class wasn't set, then use default target class..
                        if (getTargetClass() == null) {
                            setTargetClass(getProxyClass());
                        }
                        foundProxyInterface = true;
                        break;
                    }
                }

                if (!foundProxyInterface) {
                    logger.warn("Unable to determine proxyInterface from proxyClass '{}'", getProxyClass().getName());
                }
            }
        }

        // if target class wasn't set, then use default target class..
        if (getTargetClass() == null) {
            setTargetClass(getDefaultTargetClass());
        }
    }

    /**
     * Validates the configuration for this factory bean.
     */
    protected void validate() {
        validateProxyInterface();
        validateTargetClass();

        Assert.notNull(methodInterceptor, "methodInterceptor must not be null");
    }

    /**
     * Validates the {@link #proxyInterface} property.
     */
    protected void validateProxyInterface() {
        Assert.notNull(proxyInterface, "proxyInterface must not be null");
        Assert.isTrue(proxyInterface.isInterface(), "proxyInterface is not an interface");
        Assert.isAssignable(getRequiredGenericRepositoryType(), proxyInterface,
            "proxyInterface is not of required type '" + getRequiredGenericRepositoryType().getName() + "'");
    }

    /**
     * Validates the {@link #targetClass} property.
     */
    protected void validateTargetClass() {
        Assert.notNull(targetClass, "targetClass must not be null");
    }

    /**
     * @return Returns a new target instance.
     * @throws ConfigurationException in case of errors.
     */
    @SuppressWarnings("unchecked")
    protected T createTarget() throws ConfigurationException {
        return (T)ClassUtils.instantiateClass(targetClass);
    }

    /**
     * Configures the given {@code target} instance.
     *
     * @param target The instance to configure.
     */
    protected void configureTarget(T target) {
        target.setApplicationContext(applicationContext);
        target.setProxyInterface(proxyInterface);

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

    /**
     * @return Returns the required {@link GenericRepository} type for this factory.
     */
    protected abstract Class<?> getRequiredGenericRepositoryType();

    /**
     * @return Returns the default target class for this factory.
     */
    protected abstract Class<?> getDefaultTargetClass();

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public Class<?> getProxyInterface() {
        return proxyInterface;
    }

    public void setProxyInterface(Class<?> proxyInterface) {
        this.proxyInterface = proxyInterface;
    }

    public Class<?> getProxyClass() {
        return proxyClass;
    }

    public void setProxyClass(Class<?> proxyClass) {
        this.proxyClass = proxyClass;
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

    public boolean isAutoDetectBeans() {
        return autoDetectBeans;
    }

    public void setAutoDetectBeans(boolean autoDetectBeans) {
        this.autoDetectBeans = autoDetectBeans;
    }

    public boolean isValidateAfterPropertiesSet() {
        return validateAfterPropertiesSet;
    }

    public void setValidateAfterPropertiesSet(boolean validateAfterPropertiesSet) {
        this.validateAfterPropertiesSet = validateAfterPropertiesSet;
    }

    public ResultConversionService getResultConversionService() {
        return resultConversionService;
    }

    public void setResultConversionService(ResultConversionService resultConversionService) {
        this.resultConversionService = resultConversionService;
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

}
