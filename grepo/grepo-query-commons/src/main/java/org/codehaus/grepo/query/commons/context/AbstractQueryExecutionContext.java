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

package org.codehaus.grepo.query.commons.context;

import org.codehaus.grepo.core.context.AbstractGenericExecutionContext;
import org.codehaus.grepo.query.commons.naming.QueryNamingStrategy;

/**
 * @author dguggi
 */
public abstract class AbstractQueryExecutionContext extends AbstractGenericExecutionContext
    implements QueryExecutionContext {

    private Integer maxResults;
    private QueryNamingStrategy queryNamingStrategy;

    /**
     * {@inheritDoc}
     */
    public QueryNamingStrategy getQueryNamingStrategy() {
        return queryNamingStrategy;
    }

    public void setQueryNamingStrategy(QueryNamingStrategy queryNamingStrategy) {
        this.queryNamingStrategy = queryNamingStrategy;
    }

    /**
     * {@inheritDoc}
     */
    public Integer getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(Integer maxResults) {
        this.maxResults = maxResults;
    }
}
