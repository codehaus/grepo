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
     * @return Returns <code>true</code> if a parameter with the given <code>name</code> exists and <code>false</code>
     *         otherwise.
     */
    boolean hasDynamicNamedParam(String name);

    /**
     * @return Returns <code>true</code> if dynamic named params exist and <code>false</code> otherwise.
     */
    boolean hasDynamicNamedParams();

    /**
     * @param name The name of the param.
     * @return Returns the param identified by <code>name</code> or <code>null</code>.
     */
    P getDynamicNamedParam(String name);
}
