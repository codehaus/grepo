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

/**
 * Basic interface for a generic read-only hibernate data access object.
 *
 * @author dguggi
 * @param <T> The main entity type.
 * @param <PK> The primary key type.
 */
public interface ReadOnlyHibernateRepository<T, PK extends Serializable>
    extends HibernateRepository<T> {

    /**
     * Loads the entity by primary key.
     *
     * @param id The primary key.
     * @return Returns the found entity.
     */
    T load(PK id);

    /**
     * Gets the entity by primary key.
     *
     * @param id The primary key.
     * @return Returns the found entity.
     */
    T get(PK id);

    /**
     * Refreshes an entity.
     *
     * @param entity The entity to refresh.
     */
    void refresh(T entity);

    /**
     * Evicts an entity.
     *
     * @param entity The entity to evict.
     */
    void evict(T entity);

    /**
     * Check if the given {@code entity} is associated with the session.
     *
     * @param entity The entity to check.
     * @return Returns {@code true} if the given {@code entity} is associated with the session.
     */
    boolean contains(T entity);
}
