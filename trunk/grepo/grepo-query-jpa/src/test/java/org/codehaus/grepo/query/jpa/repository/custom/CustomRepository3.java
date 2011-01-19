/*
 * Copyright 2011 Grepo Committers.
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

package org.codehaus.grepo.query.jpa.repository.custom;

import org.codehaus.grepo.core.annotation.Param;
import org.codehaus.grepo.query.commons.annotation.GenericQuery;
import org.codehaus.grepo.query.jpa.TestEntity;
import org.codehaus.grepo.query.jpa.repository.ReadWriteJpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author dguggi
 */
@Repository
public interface CustomRepository3 extends ReadWriteJpaRepository<TestEntity, Long> {

    /**
     * @param id The entity id.
     * @return Returns the entity.
     */
    @GenericQuery(queryExecutor = CustomQueryExecutor.class)
    TestEntity doSomethingSpecial(@Param("id") long id);
}
