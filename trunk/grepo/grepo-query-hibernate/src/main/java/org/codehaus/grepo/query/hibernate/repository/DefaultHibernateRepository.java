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

import org.hibernate.Session;

/**
 * @author dguggi
 * @param <T> The main entity type.
 *
 * @deprecated Use {@link HibernateRepositoryImpl} instead.
 */
@Deprecated
public class DefaultHibernateRepository<T> extends HibernateRepositoryImpl<T> {
    /**
     * Default constructor.
     */
    public DefaultHibernateRepository() {
        super();
    }

    /**
     * @param entityType The main entity type.
     */
    public DefaultHibernateRepository(Class<T> entityType) {
        super(entityType);
    }

    /**
     * @return Returns the the current session.
     * @deprecated Use {@link #getCurrentSession()} instead.
     */
    @Deprecated
    protected Session getSession() {
        return getCurrentSession().getSession();
    }

}
