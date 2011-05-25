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

package org.codehaus.grepo.query.commons.repository;

import org.codehaus.grepo.query.commons.executor.QueryExecutorFactory;
import org.codehaus.grepo.query.commons.executor.QueryExecutorFindingStrategy;
import org.codehaus.grepo.query.commons.naming.QueryNamingStrategy;
import org.codehaus.grepo.statistics.repository.GrepoStatisticsConfiguration;
import org.springframework.util.Assert;

/**
 * @author dguggi
 */
public class GrepoQueryConfiguration extends GrepoStatisticsConfiguration {

    private QueryExecutorFactory queryExecutorFactory;
    private QueryExecutorFindingStrategy queryExecutorFindingStrategy;
    private QueryNamingStrategy queryNamingStrategy;

    public QueryExecutorFactory getQueryExecutorFactory() {
        return queryExecutorFactory;
    }

    public void setQueryExecutorFactory(QueryExecutorFactory queryExecutorFactory) {
        this.queryExecutorFactory = queryExecutorFactory;
    }

    public QueryExecutorFindingStrategy getQueryExecutorFindingStrategy() {
        return queryExecutorFindingStrategy;
    }

    public void setQueryExecutorFindingStrategy(QueryExecutorFindingStrategy queryExecutorFindingStrategy) {
        this.queryExecutorFindingStrategy = queryExecutorFindingStrategy;
    }

    public QueryNamingStrategy getQueryNamingStrategy() {
        return queryNamingStrategy;
    }

    public void setQueryNamingStrategy(QueryNamingStrategy queryNamingStrategy) {
        this.queryNamingStrategy = queryNamingStrategy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        super.validate();
        Assert.notNull(queryExecutorFactory, "queryExecutorFactory must not be null");
        Assert.notNull(queryExecutorFindingStrategy, "queryExecutorFindingStrategy must not be null");
        Assert.notNull(queryNamingStrategy, "queryNamingStrategy must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateMethodInterceptor() {
        super.validateMethodInterceptor();
        Assert.isInstanceOf(GenericQueryMethodInterceptor.class, getMethodInterceptor());
    }
}
