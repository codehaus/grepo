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

/**
 * A registry used to store entries via key-value pairs.
 *
 * @author dguggi
 * @param <K> The key.
 * @param <V> The value.
 */
public interface GenericRegistry<K, V> {

    /**
     * @param key The key to add.
     * @param value The value to add.
     */
    void add(K key, V value);

    /**
     * @param key The key to remove.
     */
    void remove(K key);

    /**
     * @param key The key.
     * @return Returns the value for the given <code>key</code>.
     */
    V get(K key);

    /**
     * @param key The key.
     * @param throwExceptionIfNotFound Is set to <code>true</code> an exception will be thrown if not value was found
     *            for the given <code>key</code>.
     * @return Returns the value for the given <code>key</code>
     * @throws RegistryException if <code>throwExceptionIfNotFound</code> is set to <code>true</code> and no value was
     *             found.
     */
    V get(K key, boolean throwExceptionIfNotFound) throws RegistryException;

    /**
     * Clears the registry.
     */
    void clear();
}
