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

package org.codehaus.grepo.core.util;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.grepo.core.exception.ConfigurationException;

/**
 * @author dguggi
 */
public final class ClassUtils {

    private ClassUtils() {
    }

    /**
     * Map of all nine java-primitives and their associated wrapper objects.
     */
    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER = new HashMap<Class<?>, Class<?>>();
    static {
        PRIMITIVE_TO_WRAPPER.put(long.class, Long.class);
        PRIMITIVE_TO_WRAPPER.put(boolean.class, Boolean.class);
        PRIMITIVE_TO_WRAPPER.put(int.class, Integer.class);
        PRIMITIVE_TO_WRAPPER.put(short.class, Short.class);
        PRIMITIVE_TO_WRAPPER.put(float.class, Float.class);
        PRIMITIVE_TO_WRAPPER.put(double.class, Double.class);
        PRIMITIVE_TO_WRAPPER.put(byte.class, Byte.class);
        PRIMITIVE_TO_WRAPPER.put(char.class, Character.class);
        PRIMITIVE_TO_WRAPPER.put(void.class, Void.class);
    }

    /**
     * Improves java5's {@code Class.isAssignableFrom} method.
     * This method returns false for primitives and their wrapper objects
     * (which is not really correct, because of auto-boxing).
     *
     * @param to The class one.
     * @param from The class two.
     * @return Returns true if {@code to} is assignable from {@code from}.
     */
    public static boolean isAssignableFrom(Class<?> to, Class<?> from) {
        if (PRIMITIVE_TO_WRAPPER.get(to) == from || PRIMITIVE_TO_WRAPPER.get(from) == to) {
            return true;
        }
        return to.isAssignableFrom(from);
    }

    /**
     * @param clazz The class to check.
     * @return Returns {@code true} if the given {@code clazz} is of void type.
     */
    public static boolean isVoidType(Class<?> clazz) {
        return (clazz == void.class || clazz == Void.class);
    }

    /**
     * @param <T> The type of clazz.
     * @param clazz The clazz to instantiate.
     * @return Returns the newly created instance.
     * @throws ConfigurationException in case of errors.
     */
    public static <T> T instantiateClass(Class<T> clazz) throws ConfigurationException {
        try {
            return (T) clazz.newInstance();
        } catch (InstantiationException e) {
            throw ConfigurationException.instantiateException(clazz, e);
        } catch (IllegalAccessException e) {
            throw ConfigurationException.accessException(clazz, e);
        }
    }
}
