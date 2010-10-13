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

import org.hibernate.LockMode;
import org.hibernate.ReplicationMode;

/**
 * Basic interface for a generic read-write hibernate data access object.
 *
 * @author dguggi
 * @param <T> The main entity type.
 * @param <PK> The primary key type.
 */
public interface ReadWriteHibernateRepository<T, PK extends Serializable> extends ReadOnlyHibernateRepository<T, PK> {

    /**
     * Gets an entity by primary key.
     *
     * @param id The primary key.
     * @param lockMode The lock mode.
     * @return Returns the found entity.
     */
    T get(PK id, LockMode lockMode);

    /**
     * Gets an entity by entityName and primary key.
     * @param entityName The entity name.
     * @param id The primary key.
     * @param lockMode The lock mode.
     * @return Returns the found entity.
     */
    T get(String entityName, PK id, LockMode lockMode);

    /**
     * Loads an entity by primary key.
     *
     * @param id The primary key.
     * @param lockMode The lock mode.
     * @return Returns the found entity.
     */
    T load(PK id, LockMode lockMode);

    /**
     * Loads an entity by entityName and primary key.
     *
     * @param entityName The entity name.
     * @param id The primary key.
     * @param lockMode The lock mode.
     * @return Returns the found entity.
     */
    T load(String entityName, PK id, LockMode lockMode);

    /**
     * Refreshes an entity.
     *
     * @param entity The entity.
     * @param lockMode The lock mode.
     */
    void refresh(T entity, LockMode lockMode);

    /**
     * Locks an entity.
     *
     * @param entity The entity.
     * @param lockMode The lock mode.
     */
    void lock(T entity, LockMode lockMode);

    /**
     * Locks an entity.
     *
     * @param entityName The entityName.
     * @param entity The entity.
     * @param lockMode The lock mode.
     */
    void lock(String entityName, T entity, LockMode lockMode);

    /**
     * Saves an entity.
     *
     * @param entity The entity.
     * @return The primary key.
     */
    PK save(T entity);

    /**
     * Saves an entity.
     *
     * @param entityName The entityName.
     * @param entity The entity.
     * @return The primary key.
     */
    PK save(String entityName, T entity);

    /**
     * Updates an entity.
     *
     * @param entity The entity.
     */
    void update(T entity);

    /**
     * Updates an entity.
     *
     * @param entityName The entityName.
     * @param entity The entity.
     */
    void update(String entityName, T entity);

    /**
     * Saves or updates an entity.
     *
     * @param entity The entity.
     */
    void saveOrUpdate(T entity);

    /**
     * Saves or updates an entity.
     *
     * @param entityName The entityName.
     * @param entity The entity.
     */
    void saveOrUpdate(String entityName, T entity);

    /**
     * Persists an entity.
     *
     * @param entity The entity.
     */
    void persist(T entity);

    /**
     * Persists an entity.
     *
     * @param entityName The entityName.
     * @param entity The entity.
     */
    void persist(String entityName, T entity);

    /**
     * Deletes an entity.
     *
     * @param entity The entity.
     */
    void delete(T entity);

    /**
     * Deletes an entity.
     *
     * @param entityName The entityName.
     * @param entity The entity.
     */
    void delete(String entityName, T entity);

    /**
     * Merges an entity.
     *
     * @param entity The entity.
     * @return Returns the merged entity.
     */
    T merge(T entity);

    /**
     * Merges an entity.
     *
     * @param entityName The entityName.
     * @param entity The entity.
     * @return Returns the merged entity.
     */
    T merge(String entityName, T entity);

    /**
     * Replicates an entity.
     *
     * @param entity The entity.
     * @param replicationMode The replication mode.
     */
    void replicate(T entity, ReplicationMode replicationMode);

    /**
     * Replicates an entity.
     *
     * @param entityName The entityName.
     * @param entity The entity.
     * @param replicationMode The replication mode.
     */
    void replicate(String entityName, T entity, ReplicationMode replicationMode);

    /**
     * Force the Hibernate session to flush.
     */
    void flush();
}
