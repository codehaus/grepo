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

package org.codehaus.grepo.core.converter;

/**
 * This converter can convert instances of type <code>java.lang.String</code> and <code>java.lang.Long</code> objects to
 * <code>java.lang.Integer</code> - for any other type null will be returned.
 *
 * @author dguggi
 */
public class ResultToIntegerConverter implements ResultConverter<Integer> {

    /**
     * {@inheritDoc}
     */
    public Integer convert(Object o) {
        if (o == null) {
            return null;
        }

        if (o instanceof Number) {
            return ((Number)o).intValue();
        }

        if (o instanceof String) {
            return Integer.valueOf((String)o);
        }

        return null;
    }

}
