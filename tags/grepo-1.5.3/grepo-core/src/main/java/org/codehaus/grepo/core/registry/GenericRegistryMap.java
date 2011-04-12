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

package org.codehaus.grepo.core.registry;

import java.util.HashMap;
import java.util.Map;

/**
 * Generic registry using a backing hash map.
 *
 * @author dguggi
 * @param <K> The key type.
 * @param <V> The value type.
 */
public class GenericRegistryMap<K, V> implements GenericRegistry<K, V> {

    /** SerialVersionUid. */
    private static final long serialVersionUID = -6280514682934281021L;

    /** The registry map. */
    private Map<K, V> registryMap = new HashMap<K, V>();

    /**
     * {@inheritDoc}
     */
    public void add(K key, V value) {
        registryMap.put(key, value);

    }

    /**
     * {@inheritDoc}
     */
    public void remove(K key) {
        registryMap.remove(key);
    }

    /**
     * {@inheritDoc}
     */
    public V get(K key) {
        return get(key, false);
    }

    /**
     * {@inheritDoc}
     */
    public V get(K key, boolean throwExceptionIfNotFound) throws RegistryException {
        V value = registryMap.get(key);
        if (value == null) {
            String msg = String.format("No entry found for key '%s'", key);
            throw new RegistryException(msg);
        }
        return value;
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        registryMap.clear();
    }

    public void setRegistryMap(Map<K, V> registryMap) {
        this.registryMap = registryMap;
    }

    protected Map<K, V> getRegistryMap() {
        return registryMap;
    }

}
