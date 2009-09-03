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

package org.codehaus.grepo.query.hibernate.annotation;

import org.hibernate.ScrollMode;

/**
 * @author dguggi
 */
public enum HibernateScrollMode {
    /** NONE. */
    NONE(null),
    /** FORWARD_ONLY. */
    FORWARD_ONLY(ScrollMode.FORWARD_ONLY),
    /** SCROLL_SENSITIVE. */
    SCROLL_SENSITIVE(ScrollMode.SCROLL_SENSITIVE),
    /** SCROLL_INSENSITIVE. */
    SCROLL_INSENSITIVE(ScrollMode.SCROLL_INSENSITIVE);

    /** The scroll mode. */
    private ScrollMode scrollMode;

    /**
     * @param scrollMode The scroll mode to set.
     */
    private HibernateScrollMode(ScrollMode scrollMode) {
        this.scrollMode = scrollMode;
    }

    /**
     * @return Returns the scroll mode.
     */
    public ScrollMode value() {
        return scrollMode;
    }

}
