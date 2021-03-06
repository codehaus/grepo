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

package org.codehaus.grepo.query.commons.executor;

import org.codehaus.grepo.query.commons.aop.QueryMethodParameterInfo;
import org.codehaus.grepo.query.commons.context.QueryExecutionContext;

/**
 * A query executor is responsible for executing database queries for methods annotated with
 * {@link org.codehaus.grepo.query.commons.annotation.GenericQuery}.
 *
 * @author dguggi
 * @param <T> The type.
 */
public interface QueryExecutor<T extends QueryExecutionContext> {

    /**
     * Execute a generic query.
     *
     * @param qmpi The query method parameter info.
     * @param obj The type.
     * @return Returns the result.
     */
    Object execute(QueryMethodParameterInfo qmpi, T obj);

    /**
     * This method should return {@code true} if the execution of the queries requires read-only access.
     *
     * @return The flag.
     */
    boolean isReadOnlyOperation();

}
