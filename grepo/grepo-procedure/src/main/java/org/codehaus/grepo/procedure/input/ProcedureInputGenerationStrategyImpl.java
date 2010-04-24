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

import org.codehaus.grepo.procedure.aop.ProcedureMethodParameterInfo;
import org.codehaus.grepo.procedure.executor.ProcedureExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of {@link ProcedureInputGenerationStrategy}.
 *
 * @author dguggi
 */
public class ProcedureInputGenerationStrategyImpl implements ProcedureInputGenerationStrategy {
    /** The logger for this class. */
    private final Logger logger = LoggerFactory.getLogger(ProcedureInputGenerationStrategyImpl.class);

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> generate(ProcedureMethodParameterInfo pmpi, ProcedureExecutionContext context) {
        Map<String, Object> map = new HashMap<String, Object>();

        for (int i = 0; i < pmpi.getParameters().size(); i++) {
            String paramName = pmpi.getParameterName(i);
            if (paramName == null) {
                logger.debug("Ignoring method parameter[{}] (value={})", i, pmpi.getParameter(i));
            } else {
                map.put(paramName, pmpi.getParameter(i));
            }
        }

        logger.debug("Generated input-map: {}", map);
        return map;
    }

}
