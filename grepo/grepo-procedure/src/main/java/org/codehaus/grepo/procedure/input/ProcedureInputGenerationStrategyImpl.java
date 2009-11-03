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

package org.codehaus.grepo.procedure.input;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.grepo.procedure.aop.ProcedureMethodParameterInfo;
import org.codehaus.grepo.procedure.executor.ProcedureExecutionContext;

/**
 * Default implementation of {@link ProcedureInputGenerationStrategy}.
 *
 * @author dguggi
 */
public class ProcedureInputGenerationStrategyImpl implements ProcedureInputGenerationStrategy {
    /** The logger for this class. */
    private static final Log LOG = LogFactory.getLog(ProcedureInputGenerationStrategyImpl.class);

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> generate(ProcedureMethodParameterInfo pmpi, ProcedureExecutionContext context) {
        Map<String, Object> map = new HashMap<String, Object>();

        for (int i = 0; i < pmpi.getParameters().size(); i++) {
            String paramName = pmpi.getParameterName(i);
            if (paramName == null) {
                if (LOG.isDebugEnabled()) {
                    String msg = "Ignoring method parameter[%s] (value=%s)";
                    LOG.debug(String.format(msg, i, pmpi.getParameter(i)));
                }
            } else {
                map.put(paramName, pmpi.getParameter(i));
            }
        }

        if (LOG.isTraceEnabled()) {
            LOG.trace("Generated input-map: " + map);
        }
        return map;
    }

}
