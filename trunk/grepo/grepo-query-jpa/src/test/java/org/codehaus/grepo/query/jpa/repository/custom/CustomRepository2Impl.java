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

import org.codehaus.grepo.query.jpa.TestEntity;
import org.codehaus.grepo.query.jpa.repository.ReadWriteJpaRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * @author dguggi
 */
@Repository
public class CustomRepository2Impl extends ReadWriteJpaRepositoryImpl<TestEntity, Long> implements CustomRepository2 {

    /** SerialVersionUid. */
    private static final long serialVersionUID = -3958784831607962107L;

    /** The logger for this class. */
    private final Logger logger = LoggerFactory.getLogger(CustomRepository2Impl.class); // NOPMD

    /** The result. */
    public static final String RESULT = "something-special";

    /**
     * {@inheritDoc}
     */
    public String doSomethingSpecial() {
        logger.info("doing something special");
        return RESULT;
    }

}
