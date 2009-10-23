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

package org.codehaus.grepo.query.hibernate.filter;

/**
 * Describes a hibernate filter.
 *
 * @author dguggi
 */
public class FilterDescriptor {
    /** The name for the filter. */
    private String name;

    /** An optional filter enabler. */
    private FilterEnabler enabler;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FilterEnabler getEnabler() {
        return enabler;
    }

    public void setEnabler(FilterEnabler enabler) {
        this.enabler = enabler;
    }
    /**
     * @return Returns {@code true} if enable is available.
     */
    public boolean hasEnabler() {
        return getEnabler() != null;
    }
}
