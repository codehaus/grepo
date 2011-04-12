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

package org.codehaus.grepo.procedure.cache;

import java.io.Serializable;

import org.codehaus.grepo.procedure.aop.ProcedureMethodParameterInfo;
import org.springframework.jdbc.object.StoredProcedure;

/**
 * A caching strategy is used to cache stored procedures (compiled only once).
 *
 * @author dguggi
 */
public interface ProcedureCachingStrategy extends Serializable {
    /**
     * Get a procedure from the cache.
     *
     * @param cacheName The cache name.
     * @return Returns the procedure for the given {@code cacheName} or {@code null}.
     */
    StoredProcedure getFromCache(String cacheName);

    /**
     * Add the given {@code storedProcedure} to the cache with the given
     * {@code cacheName}.
     *
     * @param storedProcedure The procedure to add.
     * @param cacheName The cache name.
     */
    void addToCache(StoredProcedure storedProcedure, String cacheName);

    /**
     * Removes the procedure with the given {@code cacheName} from the cache.
     *
     * @param cacheName The cache name.
     * @return Returns the stored procedure.
     */
    StoredProcedure removeFromCache(String cacheName);

    /**
     * Generated a cache name.
     *
     * @param pmpi The procedure method parameter info.
     * @return Returns the generated cache name.
     */
    String generateCacheName(ProcedureMethodParameterInfo pmpi);

}
