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

package org.codehaus.grepo.procedure.compile;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Compares two {@link ProcedureParamDescriptor}s based on the {@code index} property.
 *
 * @author dguggi
 */
public class ProcedureParamDescriptorComparator implements Comparator<ProcedureParamDescriptor>, Serializable {

    /** SerialVersionUid. */
    private static final long serialVersionUID = -6254953564582494430L;

    /**
     * {@inheritDoc}
     */
    public int compare(ProcedureParamDescriptor param1, ProcedureParamDescriptor param2) {
        if (param1.getIndex() < param2.getIndex()) {
            return -1;
        } else if (param1.getIndex() == param2.getIndex()) {
            return 0;
        } else {
            return 1;
        }
    }

}
