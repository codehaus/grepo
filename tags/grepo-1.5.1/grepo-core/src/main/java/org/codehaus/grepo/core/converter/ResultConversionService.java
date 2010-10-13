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

import org.codehaus.grepo.core.aop.MethodParameterInfo;

/**
 * Used to convert the result of a generic-query or -procedure invocation to desired type.
 *
 * @author dguggi
 */
public interface ResultConversionService {
    /**
     * Converts the given {@code result} using the given {@link ResultConverter} {@code clazz}.

     * @param mpi The method parameter info.
     * @param clazz The converter class.
     * @param result The result to convert.
     * @return Returns the converted result.
     */
    Object convert(MethodParameterInfo mpi, Class<? extends ResultConverter<?>> clazz,
        Object result);
}
