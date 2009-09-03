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

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

/**
 * @author dguggi
 *
 * @param <T> The main entity type.
 * @param <PK> The primary key type.
 */
public class ReadOnlyHibernateRepositoryImpl<T,PK extends Serializable>
    extends DefaultHibernateRepository<T> implements ReadOnlyHibernateRepository<T,PK> {

    /**
     * Default constructor.
     */
    public ReadOnlyHibernateRepositoryImpl() {
        super();
    }

    /**
     * @param entityType The main entity type.
     */
    public ReadOnlyHibernateRepositoryImpl(Class<T> entityType) {
        super(entityType);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T load(final PK id) {
        TransactionCallback callback = new TransactionCallback() {
            public Object doInTransaction(TransactionStatus status) {
                return getSession().load(getEntityType(), id);
            }
        };
        return (T)executeCallback(callback, true);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T get(final PK id) {
        TransactionCallback callback = new TransactionCallback() {
            public Object doInTransaction(TransactionStatus status) {
                return getSession().get(getEntityType(), id);
            }
        };
        return (T)executeCallback(callback, true);
    }

    /**
     * {@inheritDoc}
     */
    public void refresh(final T entity) {
        TransactionCallback callback = new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                getSession().refresh(entity);
            }
        };
        executeCallback(callback, true);
    }


}
