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

import java.io.Serializable;

import org.codehaus.grepo.query.hibernate.context.HibernateQueryExecutionContext;

/**
 * @author dguggi
 * @param <T> The main entity type.
 * @param <PK> The primary key type.
 */
public class ReadOnlyHibernateRepositoryImpl<T, PK extends Serializable> extends DefaultHibernateRepository<T>
        implements ReadOnlyHibernateRepository<T, PK> {

    public ReadOnlyHibernateRepositoryImpl() {
        super();
    }

    public ReadOnlyHibernateRepositoryImpl(Class<T> entityType) {
        super(entityType);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T load(final PK id) {
        HibernateCallbackCreator callback = new HibernateCallbackCreator() {

            @Override
            protected Object doExecute(HibernateQueryExecutionContext context) {
                return context.getSession().load(getEntityClass(), id);
            }
        };
        return (T)executeCallbackWithStatistics("load", callback.create(null, true), true);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T load(final String entityName, final PK id) {
        HibernateCallbackCreator callback = new HibernateCallbackCreator() {

            @Override
            protected Object doExecute(HibernateQueryExecutionContext context) {
                return context.getSession().load(entityName, id);
            }
        };
        return (T)executeCallbackWithStatistics("load", callback.create(null, true), true);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T get(final PK id) {
        HibernateCallbackCreator callback = new HibernateCallbackCreator() {

            @Override
            protected Object doExecute(HibernateQueryExecutionContext context) {
                return context.getSession().get(getEntityClass(), id);
            }
        };
        return (T)executeCallbackWithStatistics("get", callback.create(null, true), true);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T get(final String entityName, final PK id) {
        HibernateCallbackCreator callback = new HibernateCallbackCreator() {

            @Override
            protected Object doExecute(HibernateQueryExecutionContext context) {
                return context.getSession().get(entityName, id);
            }
        };
        return (T)executeCallbackWithStatistics("get", callback.create(null, true), true);
    }

    /**
     * {@inheritDoc}
     */
    public void refresh(final T entity) {
        HibernateCallbackCreator callback = new HibernateCallbackCreator() {

            @Override
            protected Object doExecute(HibernateQueryExecutionContext context) {
                context.getSession().refresh(entity);
                return null;
            }
        };
        executeCallbackWithStatistics("refresh", callback.create(null, true), true);
    }

    /**
     * {@inheritDoc}
     */
    public void evict(final T entity) {
        HibernateCallbackCreator callback = new HibernateCallbackCreator() {

            @Override
            protected Object doExecute(HibernateQueryExecutionContext context) {
                context.getSession().evict(entity);
                return null;
            }
        };
        executeCallbackWithStatistics("evict", callback.create(null, true), true);
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(final T entity) {
        HibernateCallbackCreator callback = new HibernateCallbackCreator() {

            @Override
            protected Object doExecute(HibernateQueryExecutionContext context) {
                return context.getSession().contains(entity);
            }
        };
        return (Boolean)executeCallbackWithStatistics("contains", callback.create(null, true), true);
    }

}
