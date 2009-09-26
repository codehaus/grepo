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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.grepo.query.commons.generator.DynamicNamedParam;
import org.codehaus.grepo.query.commons.generator.DynamicNamedParamsAware;

/**
 * Used to desribe a query.
 *
 * @author dguggi
 * @param <Q> The type of Query instances used.
 * @param <P> The type of {@link DynamicNamedParam} instances used.
 */
public class QueryDescriptor<Q, P extends DynamicNamedParam> implements DynamicNamedParamsAware<P> {

    /** The query. */
    private Q query;

    /** The dynamic named params. */
    private Map<String,P> dynamicNamedParams = new HashMap<String,P>();

    /** Flag to indicate whether or not the query was generated using a {@link QueryGenerator}. */
    private boolean generatedQuery;

    /**
     * @param query The query to set.
     * @param generatedQuery The flag to set.
     */
    public QueryDescriptor(Q query, boolean generatedQuery) {
        this.query = query;
        this.generatedQuery = generatedQuery;
    }

    /**
     * @param query The query to set.
     * @param isGeneratedQuery The flag to set.
     * @param params The params to set.
     */
    public QueryDescriptor(Q query, boolean isGeneratedQuery, Collection<P> params) {
        this(query, isGeneratedQuery);
        for (P param : params) {
            dynamicNamedParams.put(param.getName(), param);
        }
    }

    public Q getQuery() {
        return query;
    }

    protected void setQuery(Q query) {
        this.query = query;
    }

    public Collection<P> getDynamicNamedParams() {
        return dynamicNamedParams.values();
    }

    protected void setDynamicNamedParams(Map<String, P> dynamicNamedParams) {
        this.dynamicNamedParams = dynamicNamedParams;
    }

    /**
     * @return Returns true if dynamic named params are available.
     */
    public boolean hasDynamicNamedParams() {
        return !dynamicNamedParams.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasDynamicNamedParam(String name) {
        return dynamicNamedParams.containsKey(name);
    }

    /**
     * @param name The param name.
     * @return Returns the value for the given {@code name} or {@code null}.
     */
    public P getDynamicNamedParam(String name) {
        return dynamicNamedParams.get(name);
    }

    public boolean isGeneratedQuery() {
        return generatedQuery;
    }

    /**
     * @param generatedQuery The flag to set.
     */
    protected void setGeneratedQuery(boolean generatedQuery) {
        this.generatedQuery = generatedQuery;
    }

}
