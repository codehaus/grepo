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

import org.codehaus.grepo.statistics.repository.GenericStatisticsRepositoryFactoryBean;
import org.codehaus.grepo.statistics.repository.GrepoStatisticsConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * Factory bean used to create generic repository beans (aop proxies).
 *
 * @author dguggi
 * @param <E> The factory class type.
 */
public abstract class GenericQueryRepositoryFactoryBean<E, C extends GrepoStatisticsConfiguration> extends
    GenericStatisticsRepositoryFactoryBean<GenericQueryRepositorySupport<E>, C> {

    private static final Logger logger = LoggerFactory.getLogger(GenericQueryRepositoryFactoryBean.class);

    private Class<E> entityClass;
    private Integer maxResults;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doInitialization() {
        super.doInitialization();
        initEntityClass();
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
        target.setEntityClass(entityClass);
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

    public Integer getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(Integer maxResults) {
        this.maxResults = maxResults;
    }

}
