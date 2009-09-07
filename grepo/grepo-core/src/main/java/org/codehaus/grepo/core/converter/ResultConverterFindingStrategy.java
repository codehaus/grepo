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
 * Used to find a {@link ResultConverter} based on given <code>specifiedConverter</code> (from user), the
 * {@link MethodParameterInfo} and the <code>result</code> to be converted.
 *
 * @author dguggi
 */
public interface ResultConverterFindingStrategy {

    /**
     * @param specifiedConverter The converter specified by the user.
     * @param mpi The method parameter info.
     * @param result The result to be converted.
     * @return Returns the converter or null if no converter may be found.
     */
    Class<? extends ResultConverter<?>> findConverter(Class<? extends ResultConverter<?>> specifiedConverter,
            MethodParameterInfo mpi, Object result);
}
