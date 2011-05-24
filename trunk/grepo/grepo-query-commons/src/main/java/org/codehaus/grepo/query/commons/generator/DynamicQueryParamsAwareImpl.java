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

package org.codehaus.grepo.query.commons.generator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dguggi
 * @param <P> The query param type.
 */
public class DynamicQueryParamsAwareImpl<P extends QueryParam> implements DynamicQueryParamsAware<P> {

    private static final long serialVersionUID = -8862952977476048514L;

    private Map<String, P> dynamicQueryParams;

    /**
     * {@inheritDoc}
     */
    public P getDynamicQueryParam(String name) {
        if (hasDynamicQueryParams()) {
            return dynamicQueryParams.get(name);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasDynamicQueryParam(String name) {
        if (hasDynamicQueryParams()) {
            return dynamicQueryParams.containsKey(name);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasDynamicQueryParams() {
        return dynamicQueryParams != null && !dynamicQueryParams.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    public void addDynamicQueryParam(P param) {
        if (dynamicQueryParams == null) {
            this.dynamicQueryParams = new HashMap<String, P>();
        }
        dynamicQueryParams.put(param.getName(), param);
    }

    /**
     * {@inheritDoc}
     */
    public void addDynamicQueryParams(P... params) {
        if (params != null) {
            for (P param : params) {
                addDynamicQueryParam(param);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<P> getDynamicQueryParams() {
        if (dynamicQueryParams != null) {
            return dynamicQueryParams.values();
        }
        return null;
    }


}
