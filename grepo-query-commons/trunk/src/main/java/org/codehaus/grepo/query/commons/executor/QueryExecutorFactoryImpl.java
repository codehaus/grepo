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

import org.codehaus.grepo.exception.ConfigurationException;
import org.codehaus.grepo.query.commons.naming.QueryNamingStrategy;

/**
 * @author dguggi
 */
public class QueryExecutorFactoryImpl implements QueryExecutorFactory {
    /** The query naming strategy. */
    private QueryNamingStrategy queryNamingStrategy;

    /**
     * {@inheritDoc}
     */
    public QueryExecutor<?> createExecutor(Class<? extends QueryExecutor<?>> clazz) {
        try {
            QueryExecutor<?> executor = clazz.newInstance();
            executor.setQueryNamingStrategy(getQueryNamingStrategy());
            return executor;
        } catch (InstantiationException e) {
            throw ConfigurationException.instantiateException(clazz, e);
        } catch (IllegalAccessException e) {
            throw ConfigurationException.accessException(clazz, e);
        }
    }

    public void setQueryNamingStrategy(QueryNamingStrategy queryNamingStrategy) {
        this.queryNamingStrategy = queryNamingStrategy;
    }

    protected QueryNamingStrategy getQueryNamingStrategy() {
        return queryNamingStrategy;
    }

}
