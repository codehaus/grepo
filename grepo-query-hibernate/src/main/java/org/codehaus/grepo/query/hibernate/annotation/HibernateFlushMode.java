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

import org.hibernate.FlushMode;

/**
 * @author dguggi
 */
public enum HibernateFlushMode {
    /** UNDEFINED. */
    UNDEFINED(null),
    /** EAGER. */
    EAGER(null),
    /** MANUAL. */
    MANUAL(FlushMode.MANUAL),
    /** COMMIT. */
    COMMIT(FlushMode.COMMIT),
    /** AUTO. */
    AUTO(FlushMode.AUTO),
    /** ALWAYS. */
    ALWAYS(FlushMode.ALWAYS);

    /** The flush mode. */
    private FlushMode flushMode;

    /**
     * @param flushMode The flush mode to set.
     */
    private HibernateFlushMode(FlushMode flushMode) {
        this.flushMode = flushMode;
    }

    /**
     * @return Returns the flush mode.
     */
    public FlushMode value() {
        return flushMode;
    }

}
