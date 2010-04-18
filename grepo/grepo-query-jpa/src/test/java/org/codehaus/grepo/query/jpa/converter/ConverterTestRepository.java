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

package org.codehaus.grepo.query.jpa.converter;

import org.codehaus.grepo.core.converter.TestResultConverter;
import org.codehaus.grepo.query.commons.annotation.GenericQuery;
import org.codehaus.grepo.query.commons.repository.GenericQueryRepository;
import org.codehaus.grepo.query.jpa.TestEntity;

/**
 * @author dguggi
 */
public interface ConverterTestRepository extends GenericQueryRepository<TestEntity> {
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
        queryName = "org.codehaus.grepo.query.jpa.TestEntity.ExistingUsername")
    boolean isExistingUsernameWithSpecifiedConverter(String username);

    /**
     * @param username The username.
     * @return Returns the type.
     */
    @GenericQuery
    int getTypeByUsername(String username);

    /**
     * Note: this is an invalid generic repository configuration because the method's
     * return type is {@code String}, but the query will return an instance of
     * {@code TestEntity} and no result-converter is specified.
     *
     * @param username The username.
     * @return Returns a string.
     */
    @GenericQuery
    String getByUsername(String username);

}
