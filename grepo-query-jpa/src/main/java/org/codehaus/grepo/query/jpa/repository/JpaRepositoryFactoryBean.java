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

import javax.persistence.EntityManagerFactory;

import org.codehaus.grepo.query.commons.repository.GenericRepositoryFactoryBean;
import org.codehaus.grepo.query.commons.repository.GenericRepositorySupport;
import org.springframework.util.Assert;

/**
 * Factory bean for creating jpa generic repositories (aop proxies).
 *
 * @author dguggi
 * @param <T> The entity class type.
 */
public class JpaRepositoryFactoryBean<T> extends GenericRepositoryFactoryBean<T> {

    /** Default target class to use. */
    @SuppressWarnings("unchecked")
    private static final Class<ReadWriteJpaRepositoryImpl> DEFAULT_TARGET_CLASS =
            ReadWriteJpaRepositoryImpl.class;

    /** The entity manager factory. */
    private EntityManagerFactory entityManagerFactory;

    /** A map of jpa properties. */
    private Map<String, Object> jpaPropertyMap;

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD")
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        // init targetClass if necessary...
        if (getTargetClass() == null) {
            setTargetClass(DEFAULT_TARGET_CLASS);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validate() {
        super.validate();
        // ensure that entitymanager factory is set
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
    protected void configureTarget(GenericRepositorySupport<T> target) {
        super.configureTarget(target);

        // set entitymanager factory and property map...
        DefaultJpaRepository<T> jpaTarget = (DefaultJpaRepository<T>)target;
        jpaTarget.setEntityManagerFactory(entityManagerFactory);
        jpaTarget.setJpaPropertyMap(jpaPropertyMap);
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

}
