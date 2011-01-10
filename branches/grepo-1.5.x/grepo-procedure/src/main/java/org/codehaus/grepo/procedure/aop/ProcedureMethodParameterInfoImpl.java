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

import java.lang.reflect.Method;

import org.codehaus.grepo.core.annotation.Param;
import org.codehaus.grepo.procedure.annotation.In;
import org.codehaus.grepo.procedure.annotation.InOut;
import org.codehaus.grepo.statistics.aop.StatisticsMethodParameterInfoImpl;

/**
 * Implementation of {@link ProcedureMethodParameterInfo}.
 *
 * @author dguggi
 */
public class ProcedureMethodParameterInfoImpl extends StatisticsMethodParameterInfoImpl //
                implements ProcedureMethodParameterInfo {
    /** SerialVersionUid. */
    private static final long serialVersionUID = -3642826696564121841L;

    /**
     * @param method The method.
     * @param parameters The parameters.
     */
    public ProcedureMethodParameterInfoImpl(Method method, Object[] parameters) {
        super(method, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public String getParameterName(int index) {
        String retVal = null;
        if (index < getParameters().size()) {
            // 1. try Param annotation
            Param param = getParameterAnnotation(index, Param.class);
            if (param == null) {
                // 2. try IN annotation
                In in = getParameterAnnotation(index, In.class);
                if (in == null) {
                    // 3. try INOUT annotation
                    InOut inout = getParameterAnnotation(index, InOut.class);
                    if (inout != null) {
                        retVal = inout.name();
                    }
                } else {
                    retVal = in.name();
                }
            } else {
                retVal = param.value();
            }
        }
        return retVal;
    }


}
