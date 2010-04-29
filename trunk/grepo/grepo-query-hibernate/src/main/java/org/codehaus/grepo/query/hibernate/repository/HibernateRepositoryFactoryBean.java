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

import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.grepo.query.commons.repository.GenericQueryRepositoryFactoryBean;
import org.codehaus.grepo.query.commons.repository.GenericRepositorySupport;
import org.codehaus.grepo.query.hibernate.annotation.HibernateCacheMode;
import org.codehaus.grepo.query.hibernate.annotation.HibernateCaching;
import org.codehaus.grepo.query.hibernate.annotation.HibernateFlushMode;
import org.codehaus.grepo.query.hibernate.filter.FilterDescriptor;
import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.util.Assert;

/**
 * Factory bean for creating hibernate generic repositories (aop proxies).
 *
 * @author dguggi
 * @param <T> The entity class type.
 */
public class HibernateRepositoryFactoryBean<T> extends GenericQueryRepositoryFactoryBean<T> {
    /** The logger for this class. */
    private final Logger logger = LoggerFactory.getLogger(HibernateRepositoryFactoryBean.class);

    /** The session factory. */
    private SessionFactory sessionFactory;

    /** Flag to indicate whether or not the native Hibernate session should be exposed. */
    private Boolean exposeNativeSession;

    /** Flag to indicate wether or not we have to use a new session always. */
    private Boolean alwaysUseNewSession;

    /**
     * Flag to indicate whether or not it is allowed to create a non transactional session if there is no transactional
     * session bound to the current thread.
     */
    private Boolean allowCreate;

    /** The hibernate flush mode to set. */
    private HibernateFlushMode flushMode;

    /** The hibernate caching flag. */
    private HibernateCaching caching;

    /** The cache region. */
    private String cacheRegion;

    /** the hibernate cache mode to set. */
    private HibernateCacheMode cacheMode;

    /** The entity interceptor. */
    private Interceptor entityInterceptor;

    /** The hibernate filters to use. */
    private FilterDescriptor[] filters;

    /** Flag to indicate whether or not exceptions should be translated. */
    private Boolean translateExceptions;

    /** The jdbc exception translator. */
    private SQLExceptionTranslator jdbcExceptionTranslator;

    /** The fetch size. */
    private Integer fetchSize;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doInitialization() {
        super.doInitialization();
        initSessionFactory();
    }

    /**
     * If the {@link #sessionFactory} is not set and {@code isAutoDetectBeans()} returns {@code true}, this
     * method tries to retrieve the {@link #sessionFactory} automatically.
     */
    protected void initSessionFactory() {
        if (sessionFactory == null && isAutoDetectBeans()) {
            Map<String, SessionFactory> beans = getApplicationContext()
                .getBeansOfType(SessionFactory.class);

            if (beans.isEmpty()) {
                logger.warn(AUTODETECT_MSG_UNABLE_NOTFOUND, SessionFactory.class.getName());
            } else if (beans.size() > 1) {
                logger.warn(AUTODETECT_MSG_UNABLE_TOOMANYFOUND, SessionFactory.class.getName(), beans.keySet());
            } else {
                // we found excatly one bean...
                Entry<String, SessionFactory> entry = beans.entrySet().iterator().next();
                sessionFactory = entry.getValue();
                logger.debug(AUTODETECT_MSG_SUCCESS, SessionFactory.class.getName(), entry.getKey());
            }
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

        if (alwaysUseNewSession != null) {
            hibernateTarget.setAlwaysUseNewSession(alwaysUseNewSession);
        }
        if (allowCreate != null) {
            hibernateTarget.setAllowCreate(allowCreate);
        }
        if (flushMode != null) {
            hibernateTarget.setFlushMode(flushMode);
        }
        if (caching != null) {
            hibernateTarget.setCaching(caching);
        }
        if (cacheRegion != null) {
            hibernateTarget.setCacheRegion(cacheRegion);
        }
        if (cacheMode != null) {
            hibernateTarget.setCacheMode(cacheMode);
        }
        if (filters != null) {
            hibernateTarget.setFilters(filters);
        }
        if (translateExceptions != null) {
            hibernateTarget.setTranslateExceptions(translateExceptions);
        }
        if (jdbcExceptionTranslator != null) {
            hibernateTarget.setJdbcExceptionTranslator(jdbcExceptionTranslator);
        }
        if (entityInterceptor != null) {
            hibernateTarget.setEntityInterceptor(entityInterceptor);
        }
        if (exposeNativeSession != null) {
            hibernateTarget.setExposeNativeSession(exposeNativeSession);
        }
        if (fetchSize != null) {
            hibernateTarget.setFetchSize(fetchSize);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<?> getDefaultTargetClass() {
        return ReadWriteHibernateRepositoryImpl.class;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Boolean getExposeNativeSession() {
        return exposeNativeSession;
    }

    public void setExposeNativeSession(Boolean exposeNativeSession) {
        this.exposeNativeSession = exposeNativeSession;
    }

    public Boolean getAlwaysUseNewSession() {
        return alwaysUseNewSession;
    }

    public void setAlwaysUseNewSession(Boolean alwaysUseNewSession) {
        this.alwaysUseNewSession = alwaysUseNewSession;
    }

    public Boolean getAllowCreate() {
        return allowCreate;
    }

    public void setAllowCreate(Boolean allowCreate) {
        this.allowCreate = allowCreate;
    }

    public HibernateFlushMode getFlushMode() {
        return flushMode;
    }

    public void setFlushMode(HibernateFlushMode flushMode) {
        this.flushMode = flushMode;
    }

    public HibernateCaching getCaching() {
        return caching;
    }

    public void setCaching(HibernateCaching caching) {
        this.caching = caching;
    }

    public String getCacheRegion() {
        return cacheRegion;
    }

    public void setCacheRegion(String cacheRegion) {
        this.cacheRegion = cacheRegion;
    }

    public HibernateCacheMode getCacheMode() {
        return cacheMode;
    }

    public void setCacheMode(HibernateCacheMode cacheMode) {
        this.cacheMode = cacheMode;
    }

    public Interceptor getEntityInterceptor() {
        return entityInterceptor;
    }

    public void setEntityInterceptor(Interceptor entityInterceptor) {
        this.entityInterceptor = entityInterceptor;
    }

    public FilterDescriptor[] getFilters() {
        return filters;
    }

    public void setFilters(FilterDescriptor[] filters) {
        this.filters = filters;
    }

    public void setFilter(FilterDescriptor fd) {
        this.filters = new FilterDescriptor[] { fd };
    }

    public Boolean getTranslateExceptions() {
        return translateExceptions;
    }

    public void setTranslateExceptions(Boolean translateExceptions) {
        this.translateExceptions = translateExceptions;
    }

    public SQLExceptionTranslator getJdbcExceptionTranslator() {
        return jdbcExceptionTranslator;
    }

    public void setJdbcExceptionTranslator(SQLExceptionTranslator jdbcExceptionTranslator) {
        this.jdbcExceptionTranslator = jdbcExceptionTranslator;
    }

    public Integer getFetchSize() {
        return fetchSize;
    }

    public void setFetchSize(Integer fetchSize) {
        this.fetchSize = fetchSize;
    }

}
