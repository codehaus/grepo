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
 * Interface providing a hook for converting the result of a qeneric-query or -procedure call.
 *
 * @author dguggi
 *
 * @param <T> The result type of conversion.
 */
public interface ResultConverter<T> {
    /**
     * @param o The object to convert.
     * @return Returns the converted object.
     */
    T convert(Object o);
}
