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

package org.codehaus.grepo.query.hibernate.executor;

import org.hibernate.type.Type;

/**
 * Used to locate any specific type mappings that might be necessary for a dao implementation.
 *
 * @author dguggi
 */
public interface ArgumentTypeFactory {
    /**
     * @param value The value to check.
     * @return Returns the desired type for the given <code>arg</code>.
     */
    Type getArgumentType(Object value);
}
