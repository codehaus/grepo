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

import java.util.Date;
import java.util.List;

import javax.persistence.TemporalType;

import org.codehaus.grepo.core.annotation.Param;
import org.codehaus.grepo.query.commons.annotation.FirstResult;
import org.codehaus.grepo.query.commons.annotation.GenericQuery;
import org.codehaus.grepo.query.commons.annotation.MaxResults;
import org.codehaus.grepo.query.jpa.TestEntity;
import org.codehaus.grepo.query.jpa.annotation.GTemporal;
import org.codehaus.grepo.query.jpa.annotation.JpaQueryOptions;

/**
 * @author dguggi
 */
public interface JpaTestRepository extends ReadWriteJpaRepository<TestEntity, Long> {
    /**
     * @param username The username.
     * @return Returns the entity.
     */
    @GenericQuery
    TestEntity getByUsername(String username);

    /**
     * @param username The username.
     * @return Returns the entity.
     */
    @GenericQuery(query = "FROM TestEntity WHERE username = ?")
    TestEntity getByUsernameWithSpecifiedQuery(String username);

    /**
     * @param username The username.
     * @return Returns the entity.
     */
    @GenericQuery(query = "SELECT * FROM TEST_ENTITY WHERE USERNAME = ?", isNativeQuery = true)
    @JpaQueryOptions(resultClass = TestEntity.class)
    TestEntity getByUsernameWithSpecifiedNativeQuery(String username);

    /**
     * @param username The username.
     * @return Returns the entity.
     */
    @GenericQuery
    TestEntity getByUsernameUsingNamedParams(
            @Param("username") String username);

    /**
     * @param username The username.
     * @return Returns the entity.
     */
    @GenericQuery
    TestEntity getByUsernameWithNamedNativeQuery(String username);

    /**
     * @param username The username.
     * @return Returns the entity.
     */
    @GenericQuery
    TestEntity getByUsernameWithNamedNativeQueryUsingNamedParams(
            @Param("username") String username);

    /**
     * @param username The username.
     * @return Returns the entity.
     */
    @GenericQuery(queryName = "SpecialQueryName")
    TestEntity getByUsernameWithSpecifiedQueryName(String username);

    /**
     * @param type The type.
     * @param firstResult The first result.
     * @param maxResults The max results.
     * @return Returns a list of entities.
     */
    @GenericQuery
    List<TestEntity> findByType(int type, @FirstResult int firstResult, @MaxResults int maxResults);

    /**
     * @param type The type.
     * @return Returns the entity.
     */
    @GenericQuery(queryName = "org.codehaus.grepo.query.jpa.TestEntity.ByType",
        firstResult = 0, maxResults = 1)
    TestEntity getByTypeWithDefaultPaging(int type);

    /**
     * @param list A list of usernames.
     * @return Returns a list of entities.
     */
    @GenericQuery
    List<TestEntity> findByUsernames(@Param("list") List<String> list);

    /**
     * @param minDate The min date.
     * @return Returns a list of entities.
     */
    @GenericQuery
    List<TestEntity> findByCreationDate1(@GTemporal(TemporalType.DATE) Date minDate);

    /**
     * @param minDate The min date.
     * @return Returns a list of entities.
     */
    @GenericQuery
    List<TestEntity> findByCreationDate2(@GTemporal(TemporalType.DATE) @Param("minDate") Date minDate);
}
