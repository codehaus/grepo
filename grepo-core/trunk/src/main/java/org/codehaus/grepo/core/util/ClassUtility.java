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

package org.codehaus.grepo.core.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class improving java5's <code>Class.isAssignableFrom</code> method.
 * This method returns false for primitives and their wrapper objects
 * (which is not really correct, because of auto-boxing).
 *
 * @author dguggi
 */
public final class ClassUtility {

    /**
     * Map of all nine java-primitives and their associated wrapper objects.
     */
    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER = new HashMap<Class<?>, Class<?>>();
    static {
        PRIMITIVE_TO_WRAPPER.put(long.class, Long.class);
        PRIMITIVE_TO_WRAPPER.put(boolean.class, Boolean.class);
        PRIMITIVE_TO_WRAPPER.put(int.class, Integer.class);
        PRIMITIVE_TO_WRAPPER.put(short.class, Short.class); // @NOPMD
        PRIMITIVE_TO_WRAPPER.put(float.class, Float.class);
        PRIMITIVE_TO_WRAPPER.put(double.class, Double.class);
        PRIMITIVE_TO_WRAPPER.put(byte.class, Byte.class);
        PRIMITIVE_TO_WRAPPER.put(char.class, Character.class);
        PRIMITIVE_TO_WRAPPER.put(void.class, Void.class);
    }

    /**
     * @param to The class one.
     * @param from The class two.
     * @return Returns true if <code>to</code> is assignable from <code>from</code>.
     */
    public static boolean isAssignableFrom(Class<?> to, Class<?> from) {
        if (PRIMITIVE_TO_WRAPPER.get(to) == from || PRIMITIVE_TO_WRAPPER.get(from) == to) {
            return true;
        }
        return to.isAssignableFrom(from);
    }

    /**
     * @param clazz The class to check.
     * @return Returns true if the given <code>clazz</code> is of void type.
     */
    public static boolean isVoidType(Class<?> clazz) {
        return (clazz == void.class || clazz == Void.class);
    }
}
