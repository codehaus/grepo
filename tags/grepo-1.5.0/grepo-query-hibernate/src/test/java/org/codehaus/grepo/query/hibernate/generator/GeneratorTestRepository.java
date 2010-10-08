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

package org.codehaus.grepo.query.hibernate.generator;

import org.codehaus.grepo.core.annotation.Param;
import org.codehaus.grepo.query.commons.annotation.GenericQuery;
import org.codehaus.grepo.query.commons.repository.GenericQueryRepository;
import org.codehaus.grepo.query.hibernate.TestEntity;
import org.codehaus.grepo.query.hibernate.annotation.HibernateQueryOptions;

/**
 * @author dguggi
 */
public interface GeneratorTestRepository extends GenericQueryRepository<TestEntity> {
    /**
     * @param username The username.
     * @return Returns the entity.
     */
    @GenericQuery(queryGenerator = TestHQLGenerator.class)
    TestEntity getWithHQLGenerator(
            @Param("un") String username);

    /**
     * @return Returns the entity.
     */
    @GenericQuery(queryGenerator = TestHQLGeneratorUsingDynParams.class)
    TestEntity getWithHQLGeneratorUsingDynParams();

    /**
     * @param username  The username.
     * @return Returns the entity.
     */
    @GenericQuery(queryGenerator = TestNativeGenerator.class)
    TestEntity getWithNativeGenerator(
        @Param("un") String username);

    /**
     * @return Returns the entity.
     */
    @GenericQuery(
        queryGenerator = TestNativeGeneratorUsingDynParams.class)
    TestEntity getWithNativeGeneratorUsingDynParams();

    /**
     * @param usernames The usernames.
     * @return Returns the entity.
     */
    @GenericQuery
    @HibernateQueryOptions(criteriaGenerator = TestCriteriaGenerator.class)
    TestEntity getWithCriteriaGenerator(@Param("usernames") String... usernames);

}
