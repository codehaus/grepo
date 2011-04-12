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

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codehaus.grepo.core.annotation.Param;
import org.codehaus.grepo.core.exception.ConfigurationException;
import org.codehaus.grepo.procedure.annotation.GenericProcedure;
import org.codehaus.grepo.procedure.aop.ProcedureMethodParameterInfo;
import org.codehaus.grepo.procedure.executor.ProcedureExecutionContext;
import org.codehaus.grepo.procedure.executor.StoredProcedureImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.object.StoredProcedure;

/**
 * Default implementation of {@link ProcedureCompilationStrategy}.
 *
 * @author dguggi
 */
public class ProcedureCompilationStrategyImpl implements ProcedureCompilationStrategy {
    /** SerialVersionUid. */
    private static final long serialVersionUID = 530579506570148624L;

    /** The logger for this class. */
    private final Logger logger = LoggerFactory.getLogger(ProcedureCompilationStrategyImpl.class); // NOPMD

    /**
     * {@inheritDoc}
     */
    public StoredProcedure compile(ProcedureMethodParameterInfo pmpi, ProcedureExecutionContext context) {
        GenericProcedure annotation = pmpi.getMethodAnnotation(GenericProcedure.class);
        List<ProcedureParamDescriptor> params = ProcedureCompilationUtils.collectParams(pmpi, context);

        validateParamNames(annotation, pmpi, params);

        StoredProcedure storedProcedure = new StoredProcedureImpl(context.getDataSource(), annotation.sql());
        storedProcedure.setFunction(annotation.function());

        if (ProcedureCompilationUtils.allParamsHaveValidIndex(params)) {
            // all parameters have valid index defined...
            declareParameters(storedProcedure, params, true);
        } else if (storedProcedure.isFunction()) {
            declareParametersForFunction(storedProcedure, params);
        } else {
            declareParametersForProcedure(storedProcedure, params);
        }

        storedProcedure.compile();
        logger.debug("Compiled stored procedure: {}", storedProcedure);

        return storedProcedure;
    }

    /**
     * Validates procedure parameters.
     *
     * @param genericProcedure The annotation.
     * @param pmpi The method parameter info.
     * @param params The collected procedure param descriptors.
     * @throws ConfigurationException if configuration is invalid.
     */
    protected void validateParamNames(GenericProcedure genericProcedure, ProcedureMethodParameterInfo pmpi,
            List<ProcedureParamDescriptor> params) throws ConfigurationException {
        if (StringUtils.isNotEmpty(genericProcedure.returnParamName())) {
            ProcedureParamDescriptor desc = ProcedureCompilationUtils.getParamWithName(params, genericProcedure
                .returnParamName());
            if (desc == null || desc.getType() == ProcedureParamType.IN) {
                String msg = String.format("Attribute returnParamName set to '%s' but no "
                    + "appropriate Out/InOut-Parameter is defined", genericProcedure.returnParamName());
                throw new ConfigurationException(msg);
            }
        }

        // validate names for method-parameters annotated with @Param
        List<Param> list = pmpi.getParameterAnnotations(Param.class);
        for (Param param : list) {
            ProcedureParamDescriptor desc = ProcedureCompilationUtils.getParamWithName(params, param.value());
            if (desc == null) {
                String msg = String.format("Procedure parameter '%s' is invalid", param.value());
                throw new ConfigurationException(msg);
            }
        }
    }

    /**
     * Used to declare procedure parameters for a function. Parameters are defined in the following order:<br>
     * <br>
     * <ul>
     * <li>OUT Params</li>
     * <li>INOUT Params</li>
     * <li>IN Params</li>
     * </ul>
     *
     * @param storedProcedure The procedure.
     * @param params The params.
     */
    protected void declareParametersForFunction(StoredProcedure storedProcedure,
                                                    List<ProcedureParamDescriptor> params) {
        // declare out, in-out, in prameters in that order...
        List<ProcedureParamDescriptor> outParams = ProcedureCompilationUtils.getParamsWithType(params,
            ProcedureParamType.OUT);
        declareParameters(storedProcedure, outParams, false);

        List<ProcedureParamDescriptor> inoutParams = ProcedureCompilationUtils.getParamsWithType(params,
            ProcedureParamType.INOUT);
        declareParameters(storedProcedure, inoutParams, false);

        List<ProcedureParamDescriptor> inParams = ProcedureCompilationUtils.getParamsWithType(params,
            ProcedureParamType.IN);
        declareParameters(storedProcedure, inParams, false);
    }

    /**
     * Used to declare procedure parameters for a procedure. Parameters are defined in the following order:<br>
     * <br>
     * <ul>
     * <li>IN Params</li>
     * <li>INOUT Params</li>
     * <li>OUT Params</li>
     * </ul>
     *
     * @param storedProcedure The procedure.
     * @param params The params.
     */
    protected void declareParametersForProcedure(StoredProcedure storedProcedure,
                                                List<ProcedureParamDescriptor> params) {
        // declare in, in-out, out prameters in that order...
        List<ProcedureParamDescriptor> inParams = ProcedureCompilationUtils.getParamsWithType(params,
            ProcedureParamType.IN);
        declareParameters(storedProcedure, inParams, false);

        List<ProcedureParamDescriptor> inoutParams = ProcedureCompilationUtils.getParamsWithType(params,
            ProcedureParamType.INOUT);
        declareParameters(storedProcedure, inoutParams, false);

        List<ProcedureParamDescriptor> outParams = ProcedureCompilationUtils.getParamsWithType(params,
            ProcedureParamType.OUT);
        declareParameters(storedProcedure, outParams, false);
    }

    /**
     * Declares all procedure-parameters.
     *
     * @param storedProcedure The procedure.
     * @param list A list of procedure parameters.
     * @param forceSort If set to {@code true}, the {@code list} will definitely be sorted.
     */
    protected void declareParameters(StoredProcedure storedProcedure, List<ProcedureParamDescriptor> list,
            boolean forceSort) {
        if (forceSort || ProcedureCompilationUtils.allParamsHaveValidIndex(list)) {
            Collections.sort(list, new ProcedureParamDescriptorComparator());
        }
        for (ProcedureParamDescriptor desc : list) {
            storedProcedure.declareParameter(desc.getSqlParameter());
            logger.debug("Declared procedure param name={}, type={}", desc.getName(), desc.getSqlParameter());
        }
    }

}
