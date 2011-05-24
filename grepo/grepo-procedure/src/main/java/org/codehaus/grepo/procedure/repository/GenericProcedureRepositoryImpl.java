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

package org.codehaus.grepo.procedure.repository;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.grepo.core.validator.GenericValidationUtils;
import org.codehaus.grepo.procedure.annotation.GenericProcedure;
import org.codehaus.grepo.procedure.aop.ProcedureMethodParameterInfo;
import org.codehaus.grepo.procedure.context.ProcedureExecutionContext;
import org.codehaus.grepo.procedure.context.ProcedureExecutionContextImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

/**
 * Default implementation of {@link GenericProcedureRepository}.
 *
 * @author dguggi
 */
public class GenericProcedureRepositoryImpl extends GenericProcedureRepositorySupport {

    private static final Logger logger = LoggerFactory.getLogger(GenericProcedureRepositoryImpl.class);

    /**
     * {@inheritDoc}
     */
    public Object executeGenericProcedure(final ProcedureMethodParameterInfo pmpi, GenericProcedure genericProcedure)
        throws Exception {
        createStatisticsEntry(pmpi);
        try {
            Map<String, Object> resultMap = executeProcedure(pmpi, genericProcedure);

            Object result = convertResult(resultMap, pmpi, genericProcedure);

            validateResult(result, pmpi, genericProcedure);

            return result;
        } finally {
            completeStatisticsEntry(pmpi.getStatisticsEntry());
        }
    }

    /**
     * Execute the procedure.
     *
     * @param pmpi The method parameter info.
     * @param genericProcedure The annotation.
     * @return Returns the result map of the procedure call.
     */
    protected Map<String, Object> executeProcedure(final ProcedureMethodParameterInfo pmpi,
                                                   GenericProcedure genericProcedure) {
        TransactionCallback<Object> callback = new TransactionCallback<Object>() {

            public Object doInTransaction(final TransactionStatus status) {
                ProcedureExecutionContext context = createProcedureExecutionContext();

                StoredProcedure sp = prepareProcedure(pmpi, context);

                Map<String, Object> input = generateInputMap(pmpi, context);

                if (logger.isDebugEnabled()) {
                    logger.debug("About to execute procedure: {}", sp.getSql());
                    logger.debug("Using input map: {}", input);
                }

                Object result = sp.execute(input);
                logger.debug("Procedure result is '{}'", result);
                return result;
            }
        };

        return executeCallback(callback, genericProcedure.isReadOnly());
    }

    /**
     * @param resultMap The procedure result to convert.
     * @param pmpi The procedure method parameter info.
     * @param genericProcedure The procedure.
     * @return Returns the possibly converted result.
     */
    protected Object convertResult(Map<String, Object> resultMap, ProcedureMethodParameterInfo pmpi,
                                   GenericProcedure genericProcedure) {
        Object result = resultMap;
        if (getResultConversionService() == null) {
            logger.debug("No result conversion is performed, because no resultConversionService is configured");
        } else {
            if (!StringUtils.isEmpty(genericProcedure.returnParamName())) {
                logger.debug("GenericProcedure has returnParamName '{}' specified", genericProcedure.returnParamName());
                result = resultMap.get(genericProcedure.returnParamName());
            }

            result = getResultConversionService().convert(pmpi, genericProcedure.resultConverter(), result);
        }
        return result;
    }

    /**
     * @param result The result to validate.
     * @param pmpi The procedure method parameter info.
     * @param genericProcedure The annotation.
     * @throws Exception in case of errors.
     */
    protected void validateResult(Object result, ProcedureMethodParameterInfo pmpi, GenericProcedure genericProcedure)
        throws Exception {
        GenericValidationUtils.validateResult(pmpi, genericProcedure.resultValidator(), result);
    }

    /**
     * Creates a procedure execution context.
     *
     * @return Returns the newly created {@link ProcedureExecutionContext}.
     */
    protected ProcedureExecutionContext createProcedureExecutionContext() {
        ProcedureExecutionContextImpl context = new ProcedureExecutionContextImpl();
        context.setApplicationContext(getApplicationContext());
        context.setDataSource(getDataSource());
        return context;
    }

    /**
     * @param pmpi The procedure method parameter info.
     * @param context The procedure execution context.
     * @return Returns the stored procedure.
     */
    protected StoredProcedure prepareProcedure(ProcedureMethodParameterInfo pmpi, ProcedureExecutionContext context) {
        GenericProcedure annotation = pmpi.getMethodAnnotation(GenericProcedure.class);

        StoredProcedure storedProcedure = null;
        String cacheName = null;
        if (annotation.cachingEnabled()) {
            // try to get an already compiled procedure from the cache...
            cacheName = getProcedureCachingStrategy().generateCacheName(pmpi);
            storedProcedure = getProcedureCachingStrategy().getFromCache(cacheName);
        }

        if (storedProcedure == null) {
            // no procedure found in cache, so create new procedure...
            storedProcedure = getProcedureCompilationStrategy().compile(pmpi, context);
            if (annotation.cachingEnabled()) {
                // cache the newly compiled procedure...
                getProcedureCachingStrategy().addToCache(storedProcedure, cacheName);
            }

        }
        return storedProcedure;
    }

    /**
     * @param pmpi The procedure method parameter info.
     * @param context The procedure execution context.
     * @return Returns the generated input map.
     */
    @SuppressWarnings("unchecked")
    protected Map<String, Object> generateInputMap(ProcedureMethodParameterInfo pmpi, //
        ProcedureExecutionContext context) {
        Map<String, Object> input = null;
        if (pmpi.getParameters().size() == 1 && pmpi.getParameter(0) instanceof Map) {
            input = pmpi.getParameter(0, Map.class);
        } else {
            input = getProcedureInputGenerationStrategy().generate(pmpi, context);
        }

        if (input == null) {
            input = new HashMap<String, Object>();
        }
        return input;
    }

}
