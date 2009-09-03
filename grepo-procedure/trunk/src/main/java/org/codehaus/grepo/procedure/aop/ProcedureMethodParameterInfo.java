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

package org.codehaus.grepo.procedure.aop;

import org.codehaus.grepo.core.aop.MethodParameterInfo;

/**
 * Method parameter info for methods annotated with {@link GenericProcedure}.
 *
 * @author dguggi
 */
public interface ProcedureMethodParameterInfo extends MethodParameterInfo {

    /**
     * Retrieves the procedure parameter name at the specified parameter index. The name retrieved using the
     * {@link Param}, {@link In} or {@link InOut} annotation
     *
     * @param index The parameter index.
     * @return Returns the name of <code>null</code> if the parameter at the given <code>index</code> is not annotated.
     */
    String getParameterName(int index);
}
