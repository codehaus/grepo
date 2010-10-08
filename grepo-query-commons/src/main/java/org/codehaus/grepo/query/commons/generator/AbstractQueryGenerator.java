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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author dguggi
 *
 * @param <P> The parmeter type.
 */
public abstract class AbstractQueryGenerator<P extends QueryParam>
    implements QueryGenerator<P> {

    /** Dynamic query parameters map. */
    private Map<String,P> dynamicQueryParams = new HashMap<String,P>();


    /**
     * {@inheritDoc}
     */
    public P getDynamicQueryParam(String name) {
        return dynamicQueryParams.get(name);
    }

    /**
     * {@inheritDoc}
     */
    public Collection<P> getDynamicQueryParams() {
        return dynamicQueryParams.values();
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasDynamicQueryParam(String name) {
        return dynamicQueryParams.containsKey(name);
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasDynamicQueryParams() {
        return !dynamicQueryParams.isEmpty();
    }

    /**
     * @param param The param to add.
     */
    protected void addDynamicQueryParam(P param) {
        dynamicQueryParams.put(param.getName(), param);
    }

    /**
     * @param params The params to add.
     */
    protected void addDynamicQueryParams(Collection<P> params) {
        if (params != null) {
            Iterator<P> it = params.iterator();
            while (it.hasNext()) {
                addDynamicQueryParam(it.next());
            }
        }
    }

    /**
     * @param name The name of the param to remove.
     */
    protected void removeDynamicQueryParam(String name) {
        dynamicQueryParams.remove(name);
    }

    /**
     * Clears all dynamic query parameters.
     */
    protected void clearDynamicQueryParams() {
        dynamicQueryParams.clear();
    }

    protected void setDynamicQueryParams(Map<String,P> dynamicQueryParams) {
        this.dynamicQueryParams = dynamicQueryParams;
    }

}
