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

package org.codehaus.grepo.query.hibernate.converter;

import java.util.List;
import java.util.Map;

import org.codehaus.grepo.core.converter.TestResultConverter;
import org.codehaus.grepo.query.commons.annotation.GenericQuery;
import org.codehaus.grepo.query.commons.repository.GenericRepository;
import org.codehaus.grepo.query.hibernate.TestEntity;
import org.codehaus.grepo.query.hibernate.TestEntityDto;
import org.codehaus.grepo.query.hibernate.annotation.HibernateQueryOptions;
import org.codehaus.grepo.query.hibernate.annotation.Scalar;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.hibernate.transform.ToListResultTransformer;

/**
 * @author dguggi
 */
public interface ConverterTestRepository extends GenericRepository<TestEntity> {
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
    @GenericQuery(resultConverter = TestResultConverter.class,
        queryName = "org.codehaus.grepo.query.hibernate.TestEntity.ExistingUsername")
    boolean isExistingUsernameWithSpecifiedConverter(String username);

    /**
     * @param username The username.
     * @return Returns the type.
     */
    @GenericQuery
    int getTypeByUsername(String username);

    /**
     * Note: this is an invalid generic repository configuration because the method's return
     * type is {@code String}, but the query will return an instance of
     * {@code TestEntity} an no result-converter is specified.
     *
     * @param username The username
     * @return Returns a string.
     */
    @GenericQuery
    String getByUsername(String username);

    /**
     * @return Returns a list containing list elements.
     */
    @GenericQuery(queryName =
        "org.codehaus.grepo.query.hibernate.TestEntity.TransformerQuery")
    @HibernateQueryOptions(resultTransformer = ToListResultTransformer.class)
    List<List<Object>> findAllWithToListResultTransformer();

    /**
     * @return Returns a list containing map elements.
     */
    @GenericQuery(queryName =
        "org.codehaus.grepo.query.hibernate.TestEntity.TransformerQuery")
    @HibernateQueryOptions(resultTransformer = AliasToEntityMapResultTransformer.class)
    List<Map<String, Object>> findAllWithAliasToEntityMapResultTransformer();

    /**
     * @return Returns a list containing TestEntityDto elements.
     */
    @GenericQuery(queryName =
        "org.codehaus.grepo.query.hibernate.TestEntity.TransformerQuery")
    @HibernateQueryOptions(resultTransformer = TestEntityDto.class)
    List<TestEntityDto> findAllWithAliasToBeanResultTransformer();

    /**
     * @return Returns a list containing TestEntityDto elements.
     */
    @GenericQuery(queryName =
        "org.codehaus.grepo.query.hibernate.TestEntity.NativeTransformerQuery")
    @HibernateQueryOptions(resultTransformer = TestEntityDto.class)
    List<TestEntityDto> findAllWithAliasToBeanResultTransformerAndNativeQuery();

    /**
     * @return Returns a list containing TestEntityDto elements.
     */
    @GenericQuery(query = "SELECT username AS un, firstname AS fn FROM TEST_ENTITY", isNativeQuery = true)
    @HibernateQueryOptions(resultTransformer = TestEntityDto.class,
        scalars = {
            @Scalar(alias = "un"),
            @Scalar(alias = "fn")
    })
    List<TestEntityDto> findAllWithAliasToBeanResultTransformerAndSpecifiedNativeQuery();
}
