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

import org.codehaus.grepo.core.registry.GenericRegistryMap;
import org.codehaus.grepo.query.commons.aop.QueryMethodParameterInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dguggi
 */
public class QueryExecutorFindingStrategyImpl implements QueryExecutorFindingStrategy {

    private static final Logger logger = LoggerFactory.getLogger(QueryExecutorFindingStrategyImpl.class);

    private GenericRegistryMap<String, Class<? extends QueryExecutor<?>>> executorRegistry;
    private QueryExecutorNamingStrategy executorNamingStrategy;

    /**
     * {@inheritDoc}
     */
    public Class<? extends QueryExecutor<?>> findExecutor(Class<? extends QueryExecutor<?>> specifiedExecutor,
            QueryMethodParameterInfo qmpi) {
        Class<? extends QueryExecutor<?>> executorToUse = null;

        // true if user has defined a valid converter via annotation...
        boolean isValidUserExecutor = QueryExecutorUtils.isValidQueryExecutor(specifiedExecutor);

        if (isValidUserExecutor) {
            executorToUse = specifiedExecutor;
        } else {
            String key = executorNamingStrategy.getExecutorName(qmpi);
            executorToUse = executorRegistry.get(key, true);
        }

        if (executorToUse != null) {
            logger.debug("Found queryExecutor '{}' for execution of method '{}'", executorToUse.getName(),
                qmpi.getMethodName());
        }

        return executorToUse;
    }

    public void setExecutorNamingStrategy(QueryExecutorNamingStrategy executorNamingStrategy) {
        this.executorNamingStrategy = executorNamingStrategy;
    }

    protected QueryExecutorNamingStrategy getExecutorNamingStrategy() {
        return executorNamingStrategy;
    }

    public void setExecutorRegistry(GenericRegistryMap<String, Class<? extends QueryExecutor<?>>> executorRegistry) {
        this.executorRegistry = executorRegistry;
    }

    protected GenericRegistryMap<String, Class<? extends QueryExecutor<?>>> getExecutorRegistry() {
        return executorRegistry;
    }

}
