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

import org.codehaus.grepo.query.commons.repository.GenericRepository;

/**
 * Basic interface for a generic read-only hibernate data access object.
 *
 * @author dguggi
 * @param <T> The main entity type.
 * @param <PK> The primary key type.
 */
public interface ReadOnlyHibernateRepository<T, PK extends Serializable> extends GenericRepository<T> {

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
}
