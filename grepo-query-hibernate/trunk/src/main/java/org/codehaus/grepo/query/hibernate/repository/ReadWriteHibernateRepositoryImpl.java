/*
 * Copyright (c) 2007 Daniel Guggi.
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

import org.hibernate.LockMode;
import org.hibernate.ReplicationMode;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

/**
 * @author dguggi
 *
 * @param <T> The main entity type.
 * @param <PK> The primary key type.
 */
public class ReadWriteHibernateRepositoryImpl<T,PK extends Serializable>
    extends ReadOnlyHibernateRepositoryImpl<T,PK> implements ReadWriteHibernateRepository<T,PK> {

    /**
     * Default constructor.
     */
    public ReadWriteHibernateRepositoryImpl() {
        super();
    }

    /**
     * @param entityType The main entity type.
     */
    public ReadWriteHibernateRepositoryImpl(Class<T> entityType) {
        super(entityType);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked") //NOPMD
    public T get(final PK id, final LockMode lockMode) {
        TransactionCallback callback = new TransactionCallback() {

            public Object doInTransaction(TransactionStatus status) {
                return getSession().get(getEntityType(), id, lockMode);
            }
        };
        return (T)executeCallback(callback, false);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T load(final PK id, final LockMode lockMode) {
        TransactionCallback callback = new TransactionCallback() {

            public Object doInTransaction(TransactionStatus status) {
                return getSession().load(getEntityType(), id, lockMode);
            }
        };
        return (T)executeCallback(callback, false);
    }

    /**
     * {@inheritDoc}
     */
    public void refresh(final T entity, final LockMode lockMode) {
        TransactionCallback callback = new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                getSession().refresh(entity, lockMode);
            }
        };
        executeCallback(callback, false);
    }

    /**
     * {@inheritDoc}
     */
    public void lock(final T entity, final LockMode lockMode) {
        TransactionCallback callback = new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                getSession().lock(entity, lockMode);
            }
        };
        executeCallback(callback, false);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public PK save(final T entity) {
        TransactionCallback callback = new TransactionCallback() {

            public Object doInTransaction(TransactionStatus status) {
                return getSession().save(entity);
            }
        };
        return (PK)executeCallback(callback, false);
    }

    /**
     * {@inheritDoc}
     */
    public void update(final T entity) {
        TransactionCallback callback = new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                getSession().update(entity);
            }
        };
        executeCallback(callback, false);
    }

    /**
     * {@inheritDoc}
     */
    public void saveOrUpdate(final T entity) {
        TransactionCallback callback = new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                getSession().saveOrUpdate(entity);
            }
        };
        executeCallback(callback, false);
    }

    /**
     * {@inheritDoc}
     */
    public void persist(final T entity) {
        TransactionCallback callback = new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                getSession().persist(entity);
            }
        };
        executeCallback(callback, false);
    }

    /**
     * {@inheritDoc}
     */
    public void delete(final T entity) {
        TransactionCallback callback = new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                getSession().delete(entity);
            }
        };
        executeCallback(callback, false);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T merge(final T entity) {
        TransactionCallback callback = new TransactionCallback() {

            public Object doInTransaction(TransactionStatus status) {
                return getSession().merge(entity);
            }
        };
        return (T)executeCallback(callback, false);
    }

    /**
     * {@inheritDoc}
     */
    public void replicate(final T entity, final ReplicationMode replicationMode) {
        TransactionCallback callback = new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                getSession().replicate(entity, replicationMode);
            }

        };
        executeCallback(callback, false);
    }

}
