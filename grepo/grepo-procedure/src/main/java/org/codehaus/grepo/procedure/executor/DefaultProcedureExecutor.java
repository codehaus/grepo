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

package org.codehaus.grepo.procedure.executor;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.grepo.core.converter.ResultConversionService;
import org.codehaus.grepo.core.validator.GenericValidationUtils;
import org.codehaus.grepo.procedure.annotation.GenericProcedure;
import org.codehaus.grepo.procedure.aop.ProcedureMethodParameterInfo;
import org.codehaus.grepo.procedure.compile.ProcedureCompilationStrategy;
import org.codehaus.grepo.procedure.input.InputGenerationStrategy;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Default implementation of {@link ProcedureExecutor}.
 *
 * @author dguggi
 */
public class DefaultProcedureExecutor implements ProcedureExecutor {

    /** The logger for this class. */
    private static final Log LOG = LogFactory.getLog(DefaultProcedureExecutor.class);

    /** The caching strategy. */
    private ProcedureCachingStrategy procedureCachingStrategy;

    /** The compilation strategy. */
    private ProcedureCompilationStrategy procedureCompilationStrategy;

    /** The input generation strategy. */
    private InputGenerationStrategy inputGenerationStrategy;

    /** The result conversion service. */
    private ResultConversionService resultConversionService;

    /** The datasource. */
    private DataSource dataSource;

    /** The transaction template to use. */
    private TransactionTemplate transactionTemplate;

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("PMD")
    public Object execute(final ProcedureMethodParameterInfo pmpi, GenericProcedure genericProcedure) throws Exception {
        Map<String, Object> resultMap = executeProcedure(pmpi);

        Object result = convertResult(resultMap, pmpi, genericProcedure);

        validateResult(result, pmpi, genericProcedure);

        return result;

    }

    /**
     * Execute the procedure.
     *
     * @param pmpi The method parameter info.
     * @return Returns the result map of the procedure call.
     */
    protected Map<String, Object> executeProcedure(final ProcedureMethodParameterInfo pmpi) {
        TransactionCallback callback = new TransactionCallback() {

            @SuppressWarnings("unchecked")
            public Object doInTransaction(final TransactionStatus status) {
                StoredProcedure sp = prepareProcedure(pmpi);

                Map<String, Object> input = null;
                if (pmpi.getParameters().size() == 1 && pmpi.getParameter(0) instanceof Map) {
                    input = pmpi.getParameter(0, Map.class);
                } else {
                    input = inputGenerationStrategy.generate(dataSource, pmpi);
                }

                if (input == null) {
                    input = new HashMap<String, Object>();
                }

                if (LOG.isDebugEnabled()) {
                    LOG.debug("About to execute procedure: " + sp.getSql());
                    LOG.debug("Using input map: " + input);
                }

                return sp.execute(input);
            }
        };

        return executeCallback(callback);
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
            if (LOG.isTraceEnabled()) {
                LOG.trace("No result conversion is performed, because no resultConversionService is configured");
            }
        } else {
            if (!StringUtils.isEmpty(genericProcedure.returnParamName())) {
                if (LOG.isTraceEnabled()) {
                    String msg = String.format("GenericProcedure has returnParamName '%s' specified", genericProcedure
                        .returnParamName());
                    LOG.trace(msg);
                }
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
    @SuppressWarnings("PMD")
    protected void validateResult(Object result, ProcedureMethodParameterInfo pmpi, GenericProcedure genericProcedure)
            throws Exception {
        GenericValidationUtils.validateResult(pmpi, genericProcedure.resultValidator(), result);
    }

    /**
     * @param pmpi The procedure method parameter info.
     * @return Returns the stored procedure.
     */
    protected StoredProcedure prepareProcedure(ProcedureMethodParameterInfo pmpi) {
        GenericProcedure annotation = pmpi.getMethodAnnotation(GenericProcedure.class);

        StoredProcedure storedProcedure = null;
        String cacheName = null;
        if (annotation.cachingEnabled()) {
            // try to get an already compiled procedure from the cache...
            cacheName = procedureCachingStrategy.generateCacheName(pmpi);
            storedProcedure = procedureCachingStrategy.getFromCache(cacheName);
        }

        if (storedProcedure == null) {
            // no procedure found in cache, so create new procedure...
            storedProcedure = procedureCompilationStrategy.compile(dataSource, pmpi);
            if (annotation.cachingEnabled()) {
                // cache the newly compiled procedure...
                procedureCachingStrategy.addToCache(storedProcedure, cacheName);
            }

        }
        return storedProcedure;
    }

    /**
     * Executes the given <code>callback</code> with either an normal or none transaction template.
     *
     * @param callback The callback to execute.
     * @return Returns the result.
     */
    @SuppressWarnings("unchecked")
    protected Map<String, Object> executeCallback(TransactionCallback callback) {
        Map<String, Object> retVal = null;
        if (transactionTemplate == null) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Executing procedure without using transaction template");
            }
            // execute without transaction...
            retVal = (Map<String, Object>)callback.doInTransaction(null);
        } else {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Executing procedure using transaction template");
            }
            retVal = (Map<String, Object>)transactionTemplate.execute(callback);
        }
        return retVal;
    }

    @Required
    public void setProcedureCachingStrategy(final ProcedureCachingStrategy procedureCachingStrategy) {
        this.procedureCachingStrategy = procedureCachingStrategy;
    }

    protected ProcedureCachingStrategy getProcedureCachingStrategy() {
        return procedureCachingStrategy;
    }

    public void setProcedureCompilationStrategy(ProcedureCompilationStrategy procedureCompilationStrategy) {
        this.procedureCompilationStrategy = procedureCompilationStrategy;
    }

    protected ProcedureCompilationStrategy getProcedureCompilationStrategy() {
        return procedureCompilationStrategy;
    }

    public void setInputGenerationStrategy(InputGenerationStrategy inputGenerationStrategy) {
        this.inputGenerationStrategy = inputGenerationStrategy;
    }

    protected InputGenerationStrategy getInputGenerationStrategy() {
        return inputGenerationStrategy;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected DataSource getDataSource() {
        return dataSource;
    }

    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    protected TransactionTemplate getTransactionTemplate() {
        return this.transactionTemplate;
    }

    protected ResultConversionService getResultConversionService() {
        return resultConversionService;
    }

    public void setResultConversionService(ResultConversionService resultConversionService) {
        this.resultConversionService = resultConversionService;
    }

}
