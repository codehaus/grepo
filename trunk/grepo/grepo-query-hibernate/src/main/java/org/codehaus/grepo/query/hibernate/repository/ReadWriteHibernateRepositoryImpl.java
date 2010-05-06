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

import org.codehaus.grepo.query.hibernate.executor.HibernateQueryExecutionContext;
import org.hibernate.LockMode;
import org.hibernate.ReplicationMode;

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
    @SuppressWarnings("unchecked")  // NOPMD
    public T get(final PK id, final LockMode lockMode) {
        HibernateCallbackCreator callback = new HibernateCallbackCreator() {
            @Override
            protected Object doExecute(HibernateQueryExecutionContext context) {
                return context.getSession().get(getEntityClass(), id, lockMode);
            }
        };
        return (T)executeCallback(callback.create(null, true), false);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")  // NOPMD
    public T get(final String entityName, final PK id, final LockMode lockMode) {
        HibernateCallbackCreator callback = new HibernateCallbackCreator() {
            @Override
            protected Object doExecute(HibernateQueryExecutionContext context) {
                return context.getSession().get(entityName, id, lockMode);
            }
        };
        return (T)executeCallback(callback.create(null, true), false);
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T load(final PK id, final LockMode lockMode) {
        HibernateCallbackCreator callback = new HibernateCallbackCreator() {
            @Override
            protected Object doExecute(HibernateQueryExecutionContext context) {
                return context.getSession().load(getEntityClass(), id, lockMode);
            }
        };
        return (T)executeCallback(callback.create(null, true), false);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T load(final String entityName, final PK id, final LockMode lockMode) {
        HibernateCallbackCreator callback = new HibernateCallbackCreator() {
            @Override
            protected Object doExecute(HibernateQueryExecutionContext context) {
                return context.getSession().load(entityName, id, lockMode);
            }
        };
        return (T)executeCallback(callback.create(null, true), false);
    }


    /**
     * {@inheritDoc}
     */
    public void refresh(final T entity, final LockMode lockMode) {
        HibernateCallbackCreator callback = new HibernateCallbackCreator() {
            @Override
            protected Object doExecute(HibernateQueryExecutionContext context) {
                context.getSession().refresh(entity, lockMode);
                return null;
            }
        };
        executeCallback(callback.create(null, true), false);
    }

    /**
     * {@inheritDoc}
     */
    public void lock(final T entity, final LockMode lockMode) {
        HibernateCallbackCreator callback = new HibernateCallbackCreator() {
            @Override
            protected Object doExecute(HibernateQueryExecutionContext context) {
                context.getSession().lock(entity, lockMode);
                return null;
            }
        };
        executeCallback(callback.create(null, true), false);
    }

    /**
     * {@inheritDoc}
     */
    public void lock(final String entityName, final T entity, final LockMode lockMode) {
        HibernateCallbackCreator callback = new HibernateCallbackCreator() {
            @Override
            protected Object doExecute(HibernateQueryExecutionContext context) {
                context.getSession().lock(entityName, entity, lockMode);
                return null;
            }
        };
        executeCallback(callback.create(null, true), false);
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public PK save(final T entity) {
        HibernateCallbackCreator callback = new HibernateCallbackCreator() {
            @Override
            protected Object doExecute(HibernateQueryExecutionContext context) {
                return context.getSession().save(entity);
            }
        };
        return (PK)executeCallback(callback.create(null, true), false);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public PK save(final String entityName, final T entity) {
        HibernateCallbackCreator callback = new HibernateCallbackCreator() {
            @Override
            protected Object doExecute(HibernateQueryExecutionContext context) {
                return context.getSession().save(entityName, entity);
            }
        };
        return (PK)executeCallback(callback.create(null, true), false);
    }

    /**
     * {@inheritDoc}
     */
    public void update(final T entity) {
        HibernateCallbackCreator callback = new HibernateCallbackCreator() {
            @Override
            protected Object doExecute(HibernateQueryExecutionContext context) {
                context.getSession().update(entity);
                return null;
            }
        };
        executeCallback(callback.create(null, true), false);
    }

    /**
     * {@inheritDoc}
     */
    public void update(final String entityName, final T entity) {
        HibernateCallbackCreator callback = new HibernateCallbackCreator() {
            @Override
            protected Object doExecute(HibernateQueryExecutionContext context) {
                context.getSession().update(entityName, entity);
                return null;
            }
        };
        executeCallback(callback.create(null, true), false);
    }

    /**
     * {@inheritDoc}
     */
    public void saveOrUpdate(final T entity) {
        HibernateCallbackCreator callback = new HibernateCallbackCreator() {
            @Override
            protected Object doExecute(HibernateQueryExecutionContext context) {
                context.getSession().saveOrUpdate(entity);
                return null;
            }
        };
        executeCallback(callback.create(null, true), false);
    }

    /**
     * {@inheritDoc}
     */
    public void saveOrUpdate(final String entityName, final T entity) {
        HibernateCallbackCreator callback = new HibernateCallbackCreator() {
            @Override
            protected Object doExecute(HibernateQueryExecutionContext context) {
                context.getSession().saveOrUpdate(entityName, entity);
                return null;
            }
        };
        executeCallback(callback.create(null, true), false);
    }

    /**
     * {@inheritDoc}
     */
    public void persist(final T entity) {
        HibernateCallbackCreator callback = new HibernateCallbackCreator() {
            @Override
            protected Object doExecute(HibernateQueryExecutionContext context) {
                context.getSession().persist(entity);
                return null;
            }
        };
        executeCallback(callback.create(null, true), false);
    }

    /**
     * {@inheritDoc}
     */
    public void persist(final String entityName, final T entity) {
        HibernateCallbackCreator callback = new HibernateCallbackCreator() {
            @Override
            protected Object doExecute(HibernateQueryExecutionContext context) {
                context.getSession().persist(entityName, entity);
                return null;
            }
        };
        executeCallback(callback.create(null, true), false);
    }

    /**
     * {@inheritDoc}
     */
    public void delete(final T entity) {
        HibernateCallbackCreator callback = new HibernateCallbackCreator() {
            @Override
            protected Object doExecute(HibernateQueryExecutionContext context) {
                context.getSession().delete(entity);
                return null;
            }
        };
        executeCallback(callback.create(null, true), false);
    }

    /**
     * {@inheritDoc}
     */
    public void delete(final String entityName, final T entity) {
        HibernateCallbackCreator callback = new HibernateCallbackCreator() {
            @Override
            protected Object doExecute(HibernateQueryExecutionContext context) {
                context.getSession().delete(entityName, entity);
                return null;
            }
        };
        executeCallback(callback.create(null, true), false);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T merge(final T entity) {
        HibernateCallbackCreator callback = new HibernateCallbackCreator() {
            @Override
            protected Object doExecute(HibernateQueryExecutionContext context) {
                return context.getSession().merge(entity);
            }
        };
        return (T)executeCallback(callback.create(null, true), false);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T merge(final String entityName, final T entity) {
        HibernateCallbackCreator callback = new HibernateCallbackCreator() {
            @Override
            protected Object doExecute(HibernateQueryExecutionContext context) {
                return context.getSession().merge(entityName, entity);
            }
        };
        return (T)executeCallback(callback.create(null, true), false);
    }

    /**
     * {@inheritDoc}
     */
    public void replicate(final T entity, final ReplicationMode replicationMode) {
        HibernateCallbackCreator callback = new HibernateCallbackCreator() {
            @Override
            protected Object doExecute(HibernateQueryExecutionContext context) {
                context.getSession().replicate(entity, replicationMode);
                return null;
            }
        };
        executeCallback(callback.create(null, true), false);
    }

    /**
     * {@inheritDoc}
     */
    public void replicate(final String entityName, final T entity, final ReplicationMode replicationMode) {
        HibernateCallbackCreator callback = new HibernateCallbackCreator() {
            @Override
            protected Object doExecute(HibernateQueryExecutionContext context) {
                context.getSession().replicate(entityName, entity, replicationMode);
                return null;
            }
        };
        executeCallback(callback.create(null, true), false);
    }

    /**
     * {@inheritDoc}
     */
    public void flush() {
        HibernateCallbackCreator callback = new HibernateCallbackCreator() {
            @Override
            protected Object doExecute(HibernateQueryExecutionContext context) {
                context.getSession().flush();
                return null;
            }
        };
        executeCallback(callback.create(null, true), false);
    }


}
