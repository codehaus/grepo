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
 * @author dguggi
 */
public class TestResultConverter implements ResultConverter<Object> {

    /** The return value. */
    private static Object returnValue;

    public static void setReturnValue(Object returnValue) {
        TestResultConverter.returnValue = returnValue;
    }

    public static Object getReturnValue() {
        return returnValue;
    }

    /**
     * Reset converter.
     */
    public static void reset() {
        setReturnValue(null);
    }

    /**
     * {@inheritDoc}
     */
    public Object convert(Object o) {
        return getReturnValue();
    }

}
