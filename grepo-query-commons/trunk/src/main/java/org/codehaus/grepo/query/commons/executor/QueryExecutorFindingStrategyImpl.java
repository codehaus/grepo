/*
 * Copyright (c) 2007 Daniel Guggi.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.grepo.core.registry.GenericRegistryMap;
import org.codehaus.grepo.query.commons.aop.QueryMethodParameterInfo;

/**
 * @author dguggi
 */
public class QueryExecutorFindingStrategyImpl implements QueryExecutorFindingStrategy {

    /** The logger for this class. */
    private static final Log LOG = LogFactory.getLog(QueryExecutorFindingStrategyImpl.class);

    /** Class registry. */
    private GenericRegistryMap<String, Class<? extends QueryExecutor<?>>> executorRegistry;

    /** The naming strategy. */
    private QueryExecutorNamingStrategy executorNamingStrategy;

    /**
     * {@inheritDoc}
     */
    public Class<? extends QueryExecutor<?>> findExecutor(Class<? extends QueryExecutor<?>> specifiedExecutor,
            QueryMethodParameterInfo qmpi) {
        Class<? extends QueryExecutor<?>> executorToUse = null;

        // true if user has defined a valid converter via annotation...
        boolean isValidUserExecutor = validateQueryExecutor(specifiedExecutor);

        if (isValidUserExecutor) {
            executorToUse = specifiedExecutor;
        } else {
            String key = executorNamingStrategy.getExecutorName(qmpi);
            executorToUse = executorRegistry.get(key, true);
        }

        traceFoundQueryExecutor(executorToUse, qmpi);

        return executorToUse;
    }

    /**
     * @param clazz The class.
     * @param qmpi The query method parameter info.
     */
    private void traceFoundQueryExecutor(Class<? extends QueryExecutor<?>> clazz, QueryMethodParameterInfo qmpi) {
        if (clazz != null && LOG.isTraceEnabled()) {
            String msg = String.format("Found queryExecutor '%s' for execution of method '%s'", clazz.getName(), qmpi
                .getMethodName());
            LOG.trace(msg);
        }
    }

    /**
     * @param clazz The class.
     * @return Returns <code>true</code> if valid and <code>false</code> otherwise.
     */
    protected boolean validateQueryExecutor(Class<? extends QueryExecutor<?>> clazz) {
        return (clazz != null && clazz != PlaceHolderQueryExecutor.class);
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
