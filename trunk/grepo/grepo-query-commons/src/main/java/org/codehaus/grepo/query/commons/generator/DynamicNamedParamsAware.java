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

package org.codehaus.grepo.query.commons.generator;

import java.util.Collection;

/**
 * @author dguggi
 * @param <P> The parameter type.
 */
public interface DynamicNamedParamsAware<P extends DynamicNamedParam> {

    /**
     * @return Returns a collection of dynamic named param instances.
     */
    Collection<P> getDynamicNamedParams();

    /**
     * @param name The name of the param.
     * @return Returns {@code true} if a parameter with the given {@code name} exists and {@code false}
     *         otherwise.
     */
    boolean hasDynamicNamedParam(String name);

    /**
     * @return Returns {@code true} if dynamic named params exist and {@code false} otherwise.
     */
    boolean hasDynamicNamedParams();

    /**
     * @param name The name of the param.
     * @return Returns the param identified by {@code name} or {@code null}.
     */
    P getDynamicNamedParam(String name);
}
