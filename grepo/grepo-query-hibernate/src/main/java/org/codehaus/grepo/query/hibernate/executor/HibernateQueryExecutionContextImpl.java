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

import org.codehaus.grepo.query.hibernate.annotation.HibernateCaching;
import org.hibernate.Session;

/**
 * @author dguggi
 */
public class HibernateQueryExecutionContextImpl implements HibernateQueryExecutionContext {
    /** The session. */
    private Session session;

    /** The caching option. */
    private HibernateCaching caching;

    /** The cache region. */
    private String cacheRegion;

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

    public String getCacheRegion() {
        return cacheRegion;
    }

    public void setCacheRegion(String cacheRegion) {
        this.cacheRegion = cacheRegion;
    }

}
