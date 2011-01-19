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

import java.io.Serializable;

import org.codehaus.grepo.query.jpa.repository.ReadWriteJpaRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dguggi
 *
 * @param <T> The main entity type.
 * @param <PK> The primary key type.
 */
public class CustomReadWriteJpaRespositoryImpl<T,PK extends Serializable> //
    extends ReadWriteJpaRepositoryImpl<T, PK> implements CustomReadWriteJpaRepository<T, PK> {

    /** SerialVersionUid. */
    private static final long serialVersionUID = 3560090748707528113L;

    /** The logger for this class. */
    private final Logger logger = LoggerFactory.getLogger(CustomReadWriteJpaRespositoryImpl.class); // NOPMD

    /** The result. */
    public static final String RESULT = "something-useful";

    /**
     * {@inheritDoc}
     */
    public String doSomethingUseful() {
        logger.info("doing something useful");
        return RESULT;
    }

}
