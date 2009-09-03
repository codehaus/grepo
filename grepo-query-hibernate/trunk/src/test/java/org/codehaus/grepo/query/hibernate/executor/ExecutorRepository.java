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

package org.codehaus.grepo.query.hibernate.executor;

import java.util.Iterator;
import java.util.List;

import org.codehaus.grepo.query.commons.annotation.GenericQuery;
import org.codehaus.grepo.query.commons.repository.GenericRepository;
import org.codehaus.grepo.query.hibernate.TestEntity;
import org.codehaus.grepo.query.hibernate.annotation.GScrollMode;
import org.codehaus.grepo.query.hibernate.annotation.HibernateQueryOptions;
import org.codehaus.grepo.query.hibernate.annotation.HibernateScrollMode;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;

/**
 * @author dguggi
 */
public interface ExecutorRepository extends GenericRepository<TestEntity> {
    /**
     * @param type The type.
     * @return Returns the entity.
     */
    @GenericQuery
    TestEntity getByType(int type);

    /**
     * @param type The type.
     * @return Returns the entity.
     */
    @GenericQuery
    TestEntity loadByType(int type);

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
     * @param username The username.
     * @return Returns the entity.
     */
    @GenericQuery
    TestEntity loadByUsername(String username);

    /**
     * @param username The username.
     * @return Returns a list of entities.
     */
    @GenericQuery
    List<TestEntity> findByUsername(String username);

    /**
     * @param username The username.
     * @return Returns an iterator.
     */
    @GenericQuery
    Iterator<TestEntity> iterateByUsername(String username);

    /**
     * @param username The username.
     * @return Returns the scrollable results.
     */
    @GenericQuery
    ScrollableResults scrollByUsername(String username);

    /**
     * @param username The username.
     * @return Returns the scrollable results.
     */
    @GenericQuery(queryName =
        "org.codehaus.grepo.query.hibernate.TestEntity.ByUsername")
    @HibernateQueryOptions(scrollMode = HibernateScrollMode.FORWARD_ONLY)
    ScrollableResults scrollByUsernameWithStaticScrollMode(String username);

    /**
     * @param username The username.
     * @param hsm The hibernate scroll mode.
     * @return Returns the scrollable results.
     */
    @GenericQuery(queryName =
        "org.codehaus.grepo.query.hibernate.TestEntity.ByUsername")
    ScrollableResults scrollByUsernameWithDynamicScrollMode1(String username,
            @GScrollMode HibernateScrollMode hsm);

    /**
     * @param username The username.
     * @param sm The scroll mode.
     * @return Returns the scrollable results.
     */
    @GenericQuery(queryName = "org.codehaus.grepo.query.hibernate.TestEntity.ByUsername")
    ScrollableResults scrollByUsernameWithDynamicScrollMode2(String username,
            @GScrollMode ScrollMode sm);

    /**
     * @param type The type.
     * @param username The username.
     * @return Returns the number of entities updated.
     */
    @GenericQuery(queryName =
        "org.codehaus.grepo.query.hibernate.TestEntity.updateByUsername")
    int updateTypeByUsername(int type, String username);

    /**
     * @param username The username.
     * @return Returns the number of entities deleted.
     */
    @GenericQuery(queryName
        = "org.codehaus.grepo.query.hibernate.TestEntity.deleteByUsername")
    int deleteByUsername(String username);
}
