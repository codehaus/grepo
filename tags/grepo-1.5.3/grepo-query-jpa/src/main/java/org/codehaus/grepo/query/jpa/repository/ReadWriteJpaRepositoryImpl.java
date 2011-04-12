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

import java.io.Serializable;

import javax.persistence.LockModeType;

import org.codehaus.grepo.query.jpa.executor.JpaQueryExecutionContext;

/**
 * @author dguggi
 *
 * @param <T> The main entity type.
 * @param <PK> The primary key type.
 */
public class ReadWriteJpaRepositoryImpl<T,PK extends Serializable>
    extends ReadOnlyJpaRepositoryImpl<T,PK> implements ReadWriteJpaRepository<T,PK> {

    /** SerialVersionUid. */
    private static final long serialVersionUID = 3694751462675284209L;

    /**
     * Default constructor.
     */
    public ReadWriteJpaRepositoryImpl() {
        super();
    }

    /**
     * @param entityType The main entity type.
     */
    public ReadWriteJpaRepositoryImpl(Class<T> entityType) {
        super(entityType);
    }

    /**
     * {@inheritDoc}
     */
    public void lock(final T entity, final LockModeType lockModeType) {
        JpaCallbackCreator callback = new JpaCallbackCreator() {
            @Override
            protected Object doExecute(JpaQueryExecutionContext context) {
                context.getEntityManager().lock(entity, lockModeType);
                return null;
            }
        };
        executeCallbackWithStatistics("lock", callback.create(null, true), false);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T merge(final T entity) {
        JpaCallbackCreator callback = new JpaCallbackCreator() {
            @Override
            protected Object doExecute(JpaQueryExecutionContext context) {
                return context.getEntityManager().merge(entity);
            }
        };
        return (T)executeCallbackWithStatistics("merge", callback.create(null, true), false);
    }

    /**
     * {@inheritDoc}
     */
    public void persist(final T entity) {
        JpaCallbackCreator callback = new JpaCallbackCreator() {
            @Override
            protected Object doExecute(JpaQueryExecutionContext context) {
                context.getEntityManager().persist(entity);
                return null;
            }
        };
        executeCallbackWithStatistics("persist", callback.create(null, true), false);
    }

    /**
     * {@inheritDoc}
     */
    public void remove(final T entity) {
        JpaCallbackCreator callback = new JpaCallbackCreator() {
            @Override
            protected Object doExecute(JpaQueryExecutionContext context) {
                context.getEntityManager().remove(entity);
                return null;
            }
        };
        executeCallbackWithStatistics("remove", callback.create(null, true), false);
    }

    /**
     * {@inheritDoc}
     */
    public void flush() {
        JpaCallbackCreator callback = new JpaCallbackCreator() {
            @Override
            protected Object doExecute(JpaQueryExecutionContext context) {
                context.getEntityManager().flush();
                return null;
            }
        };
        executeCallbackWithStatistics("flush", callback.create(null, true), false);
    }

}
