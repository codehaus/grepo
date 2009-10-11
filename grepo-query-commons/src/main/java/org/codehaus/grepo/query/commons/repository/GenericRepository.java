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

package org.codehaus.grepo.query.commons.repository;

import org.codehaus.grepo.query.commons.annotation.GenericQuery;
import org.codehaus.grepo.query.commons.aop.QueryMethodParameterInfo;

/**
 * @author dguggi
 *
 * @param <T> The main entity type.
 */
public interface GenericRepository<T> {
    /**
     * @return Returns the entity class.
     */
    Class<T> getEntityClass();

    /**
     * @param qmpi The query method parameter info.
     * @param genericQuery The annotation.
     * @return Returns the result the generic query invocation.
     * @throws Exception In case of errors.
     */
    @SuppressWarnings("PMD")
    Object executeGenericQuery(QueryMethodParameterInfo qmpi, GenericQuery genericQuery) throws Exception;  // NOPMD
}
