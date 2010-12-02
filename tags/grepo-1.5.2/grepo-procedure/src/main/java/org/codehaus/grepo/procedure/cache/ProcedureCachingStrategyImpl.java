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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.grepo.procedure.annotation.GenericProcedure;
import org.codehaus.grepo.procedure.aop.ProcedureMethodParameterInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.object.StoredProcedure;

/**
 * Implementation of {@link ProcedureCachingStrategy} which uses {@code java.util.HashMap} for storing compiled
 * procedures.
 *
 * @author dguggi
 */
public class ProcedureCachingStrategyImpl implements ProcedureCachingStrategy {
    /** The logger for this class. */
    private final Logger logger = LoggerFactory.getLogger(ProcedureCachingStrategyImpl.class); // NOPMD

    /** The cache. */
    private Map<String, StoredProcedure> cache = new HashMap<String, StoredProcedure>();

    /**
     * {@inheritDoc}
     */
    public void addToCache(final StoredProcedure storedProcedure, final String cacheName) {
        if (storedProcedure != null) {
            if (StringUtils.isEmpty(cacheName)) {
                String msg = String.format("Unable to cache procedure (%s), because cacheName is empty",
                    storedProcedure.getSql());
                throw new ProcedureCachingException(msg);
            }

            if (cache.containsKey(cacheName)) {
                String msg = String.format(
                    "Unable to cache procedure (%s), because entry already exists for cacheName=%s", storedProcedure
                        .getSql(), cacheName);
                throw new ProcedureCachingException(msg);
            }

            // add to cache
            cache.put(cacheName, storedProcedure);
        }
    }

    /**
     * {@inheritDoc}
     */
    public StoredProcedure getFromCache(final String cacheName) {
        StoredProcedure sp = cache.get(cacheName);
        logger.debug("Got procedure from cache key={}, value={}", cacheName, sp);
        return sp;
    }

    /**
     * {@inheritDoc}
     */
    public StoredProcedure removeFromCache(final String cacheName) {
        return cache.remove(cacheName);
    }

    /**
     * {@inheritDoc}
     */
    public String generateCacheName(final ProcedureMethodParameterInfo pmpi) {
        GenericProcedure annotation = pmpi.getMethodAnnotation(GenericProcedure.class);
        String name = null;
        if (StringUtils.isEmpty(annotation.cacheName())) {
            name = pmpi.getMethodName() + "/" + annotation.sql();
        } else {
            name = annotation.cacheName();
        }
        return name;
    }

    protected Map<String, StoredProcedure> getCache() {
        return cache;
    }

    protected void setCache(Map<String, StoredProcedure> cache) {
        this.cache = cache;
    }

}
