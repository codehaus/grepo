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

package org.codehaus.grepo.query.commons.generator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dguggi
 *
 * @param <P> The parmeter type.
 */
public abstract class AbstractQueryGenerator<P extends DynamicNamedParam>
    implements QueryGenerator<P> {

    /** Dynamic named parameters map. */
    private Map<String,P> dynamicNamedParams = new HashMap<String,P>();


    /**
     * {@inheritDoc}
     */
    public P getDynamicNamedParam(String name) {
        return dynamicNamedParams.get(name);
    }

    /**
     * {@inheritDoc}
     */
    public Collection<P> getDynamicNamedParams() {
        return dynamicNamedParams.values();
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasDynamicNamedParam(String name) {
        return dynamicNamedParams.containsKey(name);
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasDynamicNamedParams() {
        return !dynamicNamedParams.isEmpty();
    }

    /**
     * @param param The param to add.
     */
    protected void addDynamicNamedParam(P param) {
        dynamicNamedParams.put(param.getName(), param);
    }

    /**
     * @param name The name of the param to remove.
     */
    protected void removeDynamicNamedParam(String name) {
        dynamicNamedParams.remove(name);
    }

    /**
     * Clears all dynamic named parameters.
     */
    protected void clearDynamicNamedParams() {
        dynamicNamedParams.clear();
    }

    protected void setDynamicNamedParams(Map<String,P> dynamicNamedParams) {
        this.dynamicNamedParams = dynamicNamedParams;
    }

}
