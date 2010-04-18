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

package org.codehaus.grepo.query.commons.repository;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author dguggi
 */
public final class GenericRepositoryUtils {

    /**
     * Retrieves the entity class for the given {@code clazz} via introspection.
     *
     * @param clazz The class to check.
     * @return Returns the entity class or {@code null}.
     */
    public static Class<?> getEntityClass(Class<?> clazz) {
        for (Type type : clazz.getGenericInterfaces()) {
            if (type instanceof ParameterizedType) {
                ParameterizedType pType = (ParameterizedType)type;
                if (isGenericQueryRepository(pType) && pType.getActualTypeArguments().length > 0
                    && pType.getActualTypeArguments()[0] instanceof Class<?>) {
                    return (Class<?>)pType.getActualTypeArguments()[0];
                }
            }

            if (type instanceof Class<?>) {
                Class<?> result = getEntityClass((Class<?>)type);
                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }

    /**
     * Checks if the given parameterized type is a {@link GenericQueryRepository}.
     *
     * @param type The type to check.
     * @return Returns {@code true} if the given parameterized type is a
     *          {@link GenericQueryRepository} and {@code false} otherwise.
     */
    private static boolean isGenericQueryRepository(ParameterizedType type) {
        boolean retVal = type.getRawType().equals(GenericQueryRepository.class);
        if (!retVal) {
            // type is not GenericQueryRepository check if it is assignable to...
            retVal = GenericQueryRepository.class.isAssignableFrom((Class<?>)type.getRawType());
        }
        return retVal;
    }
}
