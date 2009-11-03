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

import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.grepo.core.converter.ResultConversionService;
import org.codehaus.grepo.procedure.cache.ProcedureCachingStrategy;
import org.codehaus.grepo.procedure.compile.ProcedureCompilationStrategy;
import org.codehaus.grepo.procedure.input.ProcedureInputGenerationStrategy;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author dguggi
 */
public abstract class GenericProcedureRepositorySupport implements GenericProcedureRepository {
    /** The logger for this class. */
    private static final Log LOG = LogFactory.getLog(GenericProcedureRepositorySupport.class);

    /** The caching strategy. */
    private ProcedureCachingStrategy procedureCachingStrategy;

    /** The compilation strategy. */
    private ProcedureCompilationStrategy procedureCompilationStrategy;

    /** The input generation strategy. */
    private ProcedureInputGenerationStrategy procedureInputGenerationStrategy;

    /** The result conversion service. */
    private ResultConversionService resultConversionService;

    /** The datasource. */
    private DataSource dataSource;

    /** The application context. */
    private ApplicationContext applicationContext;

    /** The transaction template to use. */
    private TransactionTemplate transactionTemplate;

    /** The read-only transaction template to use (optional). */
    private TransactionTemplate readOnlyTransactionTemplate;

    /**
     * @param callback The callback to execute.
     * @return Returns the result.
     * @see #executeCallback(TransactionCallback, boolean)
     */
    protected Map<String, Object> executeCallback(TransactionCallback callback) {
        return executeCallback(callback, false);
    }

    /**
     * Executes the given {@code callback} with either an normal or none transaction template.
     *
     * @param callback The callback to execute.
     * @param preferReadOnlyTransactionTemplate Flag to indicate if the read-only template should be prefered.
     * @return Returns the result.
     */
    @SuppressWarnings("unchecked")
    protected Map<String, Object> executeCallback(TransactionCallback callback,
                                    boolean preferReadOnlyTransactionTemplate) {
        boolean isReadOnlyTemplateUsed = false;
        TransactionTemplate templateToUse = null;
        if (preferReadOnlyTransactionTemplate && readOnlyTransactionTemplate != null) {
            isReadOnlyTemplateUsed = true;
            templateToUse = readOnlyTransactionTemplate;
        } else {
            templateToUse = transactionTemplate;
        }

        Map<String, Object> retVal = null;
        if (templateToUse == null) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Executing procedure without using transaction template");
            }
            // execute without transaction...
            retVal = (Map<String, Object>)callback.doInTransaction(null);
        } else {
            LOG.trace("Executing procedure using" + (isReadOnlyTemplateUsed ? " read-only " : " ")
                + "transaction template");
            retVal = (Map<String, Object>)templateToUse.execute(callback);
        }
        return retVal;
    }

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

    public void setProcedureInputGenerationStrategy(
                ProcedureInputGenerationStrategy procedureInputGenerationStrategy) {
        this.procedureInputGenerationStrategy = procedureInputGenerationStrategy;
    }

    protected ProcedureInputGenerationStrategy getProcedureInputGenerationStrategy() {
        return procedureInputGenerationStrategy;
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

    protected TransactionTemplate getReadOnlyTransactionTemplate() {
        return readOnlyTransactionTemplate;
    }

    public void setReadOnlyTransactionTemplate(TransactionTemplate readOnlyTransactionTemplate) {
        this.readOnlyTransactionTemplate = readOnlyTransactionTemplate;
    }

    protected ResultConversionService getResultConversionService() {
        return resultConversionService;
    }

    public void setResultConversionService(ResultConversionService resultConversionService) {
        this.resultConversionService = resultConversionService;
    }

    protected ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

}
