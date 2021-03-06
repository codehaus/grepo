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

package org.codehaus.grepo.query.jpa.repository;

import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManagerFactory;

import org.codehaus.grepo.query.commons.repository.GenericQueryRepositoryFactoryBean;
import org.codehaus.grepo.query.commons.repository.GenericQueryRepositorySupport;
import org.codehaus.grepo.query.jpa.annotation.JpaFlushMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.util.Assert;

/**
 * Factory bean for creating jpa generic repositories (aop proxies).
 *
 * @author dguggi
 * @param <T> The entity class type.
 */
public class JpaRepositoryFactoryBean<T> extends GenericQueryRepositoryFactoryBean<T, GrepoQueryJpaConfiguration> {

    private static final Logger logger = LoggerFactory.getLogger(JpaRepositoryFactoryBean.class);

    /** The entity manager factory. */
    private EntityManagerFactory entityManagerFactory;

    /** A map of jpa properties. */
    private Map<String, Object> jpaPropertyMap;

    /** A map of default query hints. */
    private Map<String, Object> defaultQueryHints;

    /** The jpa dialect. */
    private JpaDialect jpaDialect;

    /** Flag to indicate whether or not exceptions should be translated. */
    private Boolean translateExceptions;

    /** The jpa flush mode to set. */
    private JpaFlushMode flushMode;

    /** Flag to indicate whether or not the native entity manager should be exposed. */
    private Boolean exposeNativeEntityManager;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doInitialization() {
        super.doInitialization();
        initEntityManagerFactory();
    }

    /**
     * If the {@link #entityManagerFactory} is not set and {@code #isAutoDetectBeans()} returns {@code true}, this
     * method tries to retrieve the {@link #entityManagerFactory} automatically.
     */
    protected void initEntityManagerFactory() {
        if (entityManagerFactory == null && isAutoDetectBeans()) {
            Map<String, EntityManagerFactory> beans =
                getApplicationContext().getBeansOfType(EntityManagerFactory.class);

            if (beans.isEmpty()) {
                logger.warn(AUTODETECT_MSG_UNABLE_NOTFOUND, EntityManagerFactory.class.getName());
            } else if (beans.size() > 1) {
                logger.warn(AUTODETECT_MSG_UNABLE_TOOMANYFOUND, EntityManagerFactory.class.getName(), beans.keySet());
            } else {
                // we found excatly one bean...
                Entry<String, EntityManagerFactory> entry = beans.entrySet().iterator().next();
                entityManagerFactory = entry.getValue();
                logger.debug(AUTODETECT_MSG_SUCCESS, EntityManagerFactory.class.getName(), entry.getKey());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validate() {
        super.validate();
        Assert.notNull(entityManagerFactory, "entityManagerFactory must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateTargetClass() {
        Assert.notNull(getTargetClass(), "targetClass must not be null");
        Assert.isAssignable(DefaultJpaRepository.class, getTargetClass());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<GrepoQueryJpaConfiguration> getRequiredConfigurationType() {
        return GrepoQueryJpaConfiguration.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configureTarget(GenericQueryRepositorySupport<T> target) {
        super.configureTarget(target);

        DefaultJpaRepository<T> jpaTarget = (DefaultJpaRepository<T>)target;
        jpaTarget.setEntityManagerFactory(entityManagerFactory);

        if (jpaPropertyMap != null) {
            jpaTarget.setJpaPropertyMap(jpaPropertyMap);
        }
        if (defaultQueryHints != null) {
            jpaTarget.setDefaultQueryHints(defaultQueryHints);
        }
        if (jpaDialect != null) {
            jpaTarget.setJpaDialect(jpaDialect);
        }
        if (flushMode != null) {
            jpaTarget.setFlushMode(flushMode);
        }
        if (translateExceptions != null) {
            jpaTarget.setTranslateExceptions(translateExceptions);
        }
        if (exposeNativeEntityManager != null) {
            jpaTarget.setExposeNativeEntityManager(exposeNativeEntityManager);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<?> getDefaultTargetClass() {
        return ReadWriteJpaRepositoryImpl.class;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public Map<String, Object> getJpaPropertyMap() {
        return jpaPropertyMap;
    }

    public void setJpaPropertyMap(Map<String, Object> jpaPropertyMap) {
        this.jpaPropertyMap = jpaPropertyMap;
    }

    public Map<String, Object> getDefaultQueryHints() {
        return defaultQueryHints;
    }

    public void setDefaultQueryHints(Map<String, Object> defaultQueryHints) {
        this.defaultQueryHints = defaultQueryHints;
    }

    public JpaDialect getJpaDialect() {
        return jpaDialect;
    }

    public void setJpaDialect(JpaDialect jpaDialect) {
        this.jpaDialect = jpaDialect;
    }

    public Boolean getTranslateExceptions() {
        return translateExceptions;
    }

    public void setTranslateExceptions(Boolean translateExceptions) {
        this.translateExceptions = translateExceptions;
    }

    public Boolean getExposeNativeEntityManager() {
        return exposeNativeEntityManager;
    }

    public void setExposeNativeEntityManager(Boolean exposeNativeEntityManager) {
        this.exposeNativeEntityManager = exposeNativeEntityManager;
    }

    public JpaFlushMode getFlushMode() {
        return flushMode;
    }

    public void setFlushMode(JpaFlushMode flushMode) {
        this.flushMode = flushMode;
    }
}
