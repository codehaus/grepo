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

import java.util.Collection;

import org.codehaus.grepo.query.commons.aop.QueryMethodParameterInfo;
import org.codehaus.grepo.query.commons.context.PlaceHolderQueryExecutionContext;

/**
 * QueryGenerator which is used as a placeholder. May not be extended or used.
 *
 * @author dguggi
 */
public final class PlaceHolderQueryGenerator implements QueryGenerator<String, PlaceHolderQueryExecutionContext> {
    /** SerialVersionUid. */
    private static final long serialVersionUID = 4403126044675851070L;


    /**
     * {@inheritDoc}
     */
    public String generate(QueryMethodParameterInfo qmpi, PlaceHolderQueryExecutionContext context) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<QueryParam> getDynamicQueryParams() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasDynamicQueryParam(String name) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasDynamicQueryParams() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public QueryParam getDynamicQueryParam(String name) {
        return null;
    }

}
