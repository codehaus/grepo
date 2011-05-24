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

package org.codehaus.grepo.query.commons.generator;

import java.io.Serializable;

import org.codehaus.grepo.query.commons.aop.QueryMethodParameterInfo;
import org.codehaus.grepo.query.commons.context.QueryExecutionContext;

/**
 * Interface to be implemented if a hql query should be generated dynamically.
 *
 * @author dguggi
 *
 * @param <T> The query type.
 * @param <C> The query execution context.
 */
public interface QueryGenerator<T, C extends QueryExecutionContext> extends Serializable {

    /**
     * @param qmpi The method parameter info.
     * @param context The context.
     * @return Returns the generated query.
     */
    T generate(QueryMethodParameterInfo qmpi, C context);

}
