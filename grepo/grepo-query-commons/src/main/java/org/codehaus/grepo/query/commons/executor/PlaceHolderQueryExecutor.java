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
import org.codehaus.grepo.query.commons.context.PlaceHolderQueryExecutionContext;
import org.codehaus.grepo.query.commons.naming.QueryNamingStrategy;

/**
 * PlaceHolder may not be used or extended.
 *
 * @author dguggi
 */
public final class PlaceHolderQueryExecutor implements QueryExecutor<PlaceHolderQueryExecutionContext> {

    /**
     * {@inheritDoc}
     */
    public Object execute(QueryMethodParameterInfo qmpi, PlaceHolderQueryExecutionContext obj) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isReadOnlyOperation() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void setQueryNamingStrategy(QueryNamingStrategy queryNamingStrategy) {
    }

}
