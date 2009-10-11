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

import org.codehaus.grepo.query.commons.repository.GenericRepository;

/**
 * @author dguggi
 *
 * @param <T> The main entity type.
 * @param <PK> The primary key type.
 */
public interface ReadOnlyJpaRepository <T,PK extends Serializable> extends GenericRepository<T> {

    /**
     * @param id The primary key.
     * @return Returns the entity.
     */
    T getReference(PK id);

    /**
     * @param id The primary key.
     * @return Returns the entity.
     */
    T find(PK id);

    /**
     * @param entity The entity.
     */
    void refresh(T entity);
}
