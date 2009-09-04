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

import java.util.regex.Pattern;

/**
 * This converter can convert instances of <code>java.lang.Number</code> objects to <code>java.lang.Boolean</code> - for
 * any other type <code>Boolean.FALSE</code> will be returned. <br>
 * <br>
 * <b>Note:</b> This converter will never return <code>null</code>. If the input to be converted is <code>null</code>
 * then <code>Boolean.FALSE</code> will be returned. If input is of type <code>java.lang.String</code> then the input's
 * value has to match pattern <code>(t|true|1|yes)</code> in order to return <code>Boolean.TRUE</code> - for any other
 * value <code>Boolean.FALSE</code> will be returned.
 *
 * @author dguggi
 */
public class ResultToBooleanConverter implements ResultConverter<Boolean> {

    /** The string pattern. */
    private static final Pattern TRUE_STRING_PATTERN = Pattern.compile("(t|true|1|yes)", Pattern.CASE_INSENSITIVE);

    /**
     * {@inheritDoc}
     */
    public Boolean convert(Object o) {
        if (o == null) {
            return Boolean.FALSE;
        }

        if (o instanceof Boolean) {
            return (Boolean)o;
        }

        if (o instanceof Number) {
            return ((Number)o).intValue() > 0;
        }

        if (o instanceof String) {
            return TRUE_STRING_PATTERN.matcher((String)o).matches();
        }

        return Boolean.FALSE;
    }

}
