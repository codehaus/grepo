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

package org.codehaus.grepo.core.converter;


/**
 * This converter can convert <code>java.lang.String</code> and <code>java.lang.Integer</code>
 * objects to <code>java.lang.Long</code> - for any other type null will be returned.
 *
 * @author dguggi
 */
public class ResultToLongConverter implements ResultConverter<Long> {

    /**
     * {@inheritDoc}
     */
    public Long convert(Object o) {
        if (o == null) {
            return null;
        }

        if (o instanceof Number) {
            return ((Number)o).longValue();
        }

        if (o instanceof String) {
            return Long.valueOf((String)o);
        }

        return null;
    }

}
