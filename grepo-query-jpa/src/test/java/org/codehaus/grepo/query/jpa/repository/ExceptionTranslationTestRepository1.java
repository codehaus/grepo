/*
 * Copyright 2010 Grepo Committers.
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

import org.codehaus.grepo.query.commons.annotation.GenericQuery;
import org.codehaus.grepo.query.commons.repository.GenericQueryRepository;
import org.codehaus.grepo.query.jpa.TestEntity;

/**
 * @author dguggi
 */
public interface ExceptionTranslationTestRepository1  extends GenericQueryRepository<TestEntity> {
    /**
     * @param firstName The first name.
     * @return Returns the test entity.
     */
    @GenericQuery(query = "from TestEntity where firstname = ?")
    TestEntity getByFirstName(String firstName);
}
