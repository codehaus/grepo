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

/**
 * @author dguggi
 *
 * @param <T> The main entity type.
 * @param <PK> The primary key type.
 */
public interface ReadWriteJpaRepository<T, PK extends Serializable>
    extends ReadOnlyJpaRepository<T, PK> {

    /**
     * Locks the entity.
     *
     * @param entity The entity.
     * @param lockModeType The lock mode type.
     */
    void lock(T entity, LockModeType lockModeType);

    /**
     * Merges the entity.
     *
     * @param entity The entity.
     * @return Returns the merged entity.
     */
    T merge(T entity);

    /**
     * Persists the entity.
     *
     * @param entity The entity.
     */
    void persist(T entity);

    /**
     * Removes the entity.
     *
     * @param entity The entity.
     */
    void remove(T entity);
}
