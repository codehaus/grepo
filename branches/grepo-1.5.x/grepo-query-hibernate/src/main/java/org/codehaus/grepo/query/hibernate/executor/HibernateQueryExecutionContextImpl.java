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

package org.codehaus.grepo.query.hibernate.executor;

import org.codehaus.grepo.query.commons.executor.AbstractQueryExecutionContext;
import org.codehaus.grepo.query.hibernate.annotation.HibernateCaching;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * @author dguggi
 */
public class HibernateQueryExecutionContextImpl
    extends AbstractQueryExecutionContext implements HibernateQueryExecutionContext {

    /** The session factory. */
    private SessionFactory sessionFactory;

    /** The session. */
    private Session session;

    /** The caching option. */
    private HibernateCaching caching;

    /** The cache region. */
    private String cacheRegion;

    /** The fetch size. */
    private Integer fetchSize;

    /**
     * {@inheritDoc}
     */
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * {@inheritDoc}
     */
    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    /**
     * {@inheritDoc}
     */
    public HibernateCaching getCaching() {
        return caching;
    }

    public void setCaching(HibernateCaching caching) {
        this.caching = caching;
    }

    /**
     * {@inheritDoc}
     */
    public String getCacheRegion() {
        return cacheRegion;
    }

    public void setCacheRegion(String cacheRegion) {
        this.cacheRegion = cacheRegion;
    }

    /**
     * {@inheritDoc}
     */
    public Integer getFetchSize() {
        return fetchSize;
    }

    public void setFetchSize(Integer fetchSize) {
        this.fetchSize = fetchSize;
    }

}
