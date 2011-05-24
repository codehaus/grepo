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

import org.codehaus.grepo.query.jpa.context.JpaQueryExecutionContext;

/**
 * @author dguggi
 * @param <T> The main entity type.
 * @param <PK> The primary key type.
 */
public class ReadOnlyJpaRepositoryImpl<T, PK extends Serializable> extends DefaultJpaRepository<T> implements
        ReadOnlyJpaRepository<T, PK> {

    public ReadOnlyJpaRepositoryImpl() {
        super();
    }

    public ReadOnlyJpaRepositoryImpl(Class<T> entityType) {
        super(entityType);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T find(final PK id) {
        JpaCallbackCreator callback = new JpaCallbackCreator() {

            @Override
            protected Object doExecute(JpaQueryExecutionContext context) {
                return context.getEntityManager().find(getEntityClass(), id);
            }
        };
        return (T)executeCallbackWithStatistics("find", callback.create(null, true), true);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T getReference(final PK id) {
        JpaCallbackCreator callback = new JpaCallbackCreator() {

            @Override
            protected Object doExecute(JpaQueryExecutionContext context) {
                return context.getEntityManager().getReference(getEntityClass(), id);
            }
        };
        return (T)executeCallbackWithStatistics("getReference", callback.create(null, true), true);
    }

    /**
     * {@inheritDoc}
     */
    public void refresh(final T entity) {
        JpaCallbackCreator callback = new JpaCallbackCreator() {

            @Override
            protected Object doExecute(JpaQueryExecutionContext context) {
                context.getEntityManager().refresh(entity);
                return null;
            }
        };
        executeCallbackWithStatistics("refresh", callback.create(null, true), true);
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(final T entity) {
        JpaCallbackCreator callback = new JpaCallbackCreator() {

            @Override
            protected Object doExecute(JpaQueryExecutionContext context) {
                return context.getEntityManager().contains(entity);
            }
        };
        return (Boolean)executeCallbackWithStatistics("contains", callback.create(null, true), true);
    }

}
