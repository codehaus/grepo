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

package org.codehaus.grepo.query.hibernate.repository;

import org.codehaus.grepo.query.commons.repository.GenericRepositoryFactoryBean;
import org.codehaus.grepo.query.commons.repository.GenericRepositorySupport;
import org.hibernate.SessionFactory;
import org.springframework.util.Assert;

/**
 * Factory bean for creating hibernate generic repositories (aop proxies).
 *
 * @author dguggi
 * @param <T> The entity class type.
 */
public class HibernateRepositoryFactoryBean<T> extends GenericRepositoryFactoryBean<T> {

    /** Default target class to use. */
    @SuppressWarnings("unchecked")
    private static final Class<ReadWriteHibernateRepositoryImpl> DEFAULT_TARGET_CLASS =
            ReadWriteHibernateRepositoryImpl.class;

    /** The session factory. */
    private SessionFactory sessionFactory;

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
        // ensure that sessionfactory is set
        Assert.notNull(sessionFactory, "sessionFactory must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateTargetClass() {
        Assert.notNull(getTargetClass(), "targetClass must not be null");
        Assert.isAssignable(DefaultHibernateRepository.class, getTargetClass());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configureTarget(GenericRepositorySupport<T> target) {
        super.configureTarget(target);

        // set session factory...
        DefaultHibernateRepository<T> hibernateTarget = (DefaultHibernateRepository<T>)target;
        hibernateTarget.setSessionFactory(sessionFactory);
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

}
