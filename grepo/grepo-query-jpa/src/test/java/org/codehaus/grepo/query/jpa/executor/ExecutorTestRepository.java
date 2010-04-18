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

package org.codehaus.grepo.query.jpa.executor;

import java.util.List;

import org.codehaus.grepo.query.commons.annotation.GenericQuery;
import org.codehaus.grepo.query.commons.repository.GenericQueryRepository;
import org.codehaus.grepo.query.jpa.TestEntity;

/**
 * @author dguggi
 */
public interface ExecutorTestRepository extends GenericQueryRepository<TestEntity> {

    /**
     * @param username The username.
     * @return Returns the entity.
     */
    @GenericQuery
    List<TestEntity> findByUsername(String username);

    /**
     * @param username The username.
     * @return Returns the entity.
     */
    @GenericQuery
    TestEntity getByUsername(String username);

    /**
     * @param username The username.
     * @return Returns the flag.
     */
    @GenericQuery
    boolean isExistingUsername(String username);

    /**
     * @param username The username.
     * @return Returns the flag.
     */
    @GenericQuery
    boolean hasExistingUsername(String username);

    /**
     * @param type The type.
     * @return Returns the entity.
     */
    @GenericQuery
    TestEntity getByType(int type);

    /**
     * @param type the type.
     * @return Returns the entity.
     */
    @GenericQuery
    TestEntity loadByType(int type);

    /**
     * @param username The username.
     * @return Returns the entity.
     */
    @GenericQuery
    TestEntity loadByUsername(String username);

    /**
     * @param type The type.
     * @param username The username.
     * @return Returns the number of entities updated.
     */
    @GenericQuery(queryName =
        "org.codehaus.grepo.query.jpa.TestEntity.updateByUsername")
    int updateTypeByUsername(int type, String username);

    /**
     * @param username The username.
     * @return Returns the number of entities deleted..
     */
    @GenericQuery(queryName = "org.codehaus.grepo.query.jpa.TestEntity.deleteByUsername")
    int deleteByUsername(String username);
}
