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

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

/**
 * @author dguggi
 *
 * @param <T> The main entity type.
 * @param <PK> The primary key type.
 */
public class ReadWriteJpaRepositoryImpl<T,PK extends Serializable>
    extends ReadOnlyJpaRepositoryImpl<T,PK> implements ReadWriteJpaRepository<T,PK> {

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
        TransactionCallback callback = new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                CurrentEntityManagerHolder emHolder = getCurrentEntityManager();
                try {
                    emHolder.getEntityManager().lock(entity, lockModeType);
                } finally {
                    closeNewEntityManager(emHolder);
                }
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
            public Object doInTransaction(final TransactionStatus status) {
                CurrentEntityManagerHolder emHolder = getCurrentEntityManager();
                try {
                    return emHolder.getEntityManager().merge(entity);
                } finally {
                    closeNewEntityManager(emHolder);
                }
            }
        };

        return (T)executeCallback(callback, false);
    }

    /**
     * {@inheritDoc}
     */
    public void persist(final T entity) {
        TransactionCallback callback = new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                CurrentEntityManagerHolder emHolder = getCurrentEntityManager();
                try {
                    emHolder.getEntityManager().persist(entity);
                } finally {
                    closeNewEntityManager(emHolder);
                }
            }
        };
        executeCallback(callback, false);
    }

    /**
     * {@inheritDoc}
     */
    public void remove(final T entity) {
        TransactionCallback callback = new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                CurrentEntityManagerHolder emHolder = getCurrentEntityManager();
                try {
                    emHolder.getEntityManager().remove(entity);
                } finally {
                    closeNewEntityManager(emHolder);
                }
            }
        };
        executeCallback(callback, false);
    }

}
