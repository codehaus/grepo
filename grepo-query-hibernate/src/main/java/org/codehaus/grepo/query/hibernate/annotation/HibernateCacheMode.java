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

package org.codehaus.grepo.query.hibernate.annotation;

import org.hibernate.CacheMode;

/**
 * @author dguggi
 */
public enum HibernateCacheMode {
    /** UNDEFINED. */
    UNDEFINED(null),
    /** NORMAL. */
    NORMAL(CacheMode.NORMAL),
    /** IGNORE. */
    IGNORE(CacheMode.IGNORE),
    /** GET. */
    GET(CacheMode.GET),
    /** PUT. */
    PUT(CacheMode.PUT),
    /** REFRESH. */
    REFRESH(CacheMode.REFRESH);

    /** The cache mode. */
    private CacheMode cacheMode;

    /**
     * @param cacheMode The casche mode to set.
     */
    private HibernateCacheMode(CacheMode cacheMode) {
        this.cacheMode = cacheMode;
    }

    /**
     * @return Returns the cache mode.
     */
    public CacheMode value() {
        return cacheMode;
    }
}
