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

import org.codehaus.grepo.procedure.cache.ProcedureCachingStrategy;
import org.codehaus.grepo.procedure.compile.ProcedureCompilationStrategy;
import org.codehaus.grepo.procedure.input.ProcedureInputGenerationStrategy;
import org.codehaus.grepo.statistics.repository.GenericStatisticsRepositorySupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author dguggi
 */
public abstract class GenericProcedureRepositorySupport extends GenericStatisticsRepositorySupport
        implements GenericProcedureRepository {

    private static final Logger logger = LoggerFactory.getLogger(GenericProcedureRepositorySupport.class);

    /** The caching strategy. */
    private ProcedureCachingStrategy procedureCachingStrategy;

    /** The compilation strategy. */
    private ProcedureCompilationStrategy procedureCompilationStrategy;

    /** The input generation strategy. */
    private ProcedureInputGenerationStrategy procedureInputGenerationStrategy;

    /** The datasource. */
    private DataSource dataSource;

    /**
     * Executes the given {@code callback} with either an read-only, normal or none transaction template.
     *
     * @param callback The callback to execute.
     * @param preferReadOnlyTransactionTemplate Flag to indicate if the read-only template should be prefered.
     * @return Returns the result.
     */
    @SuppressWarnings("unchecked")
    protected Map<String, Object> executeCallback(TransactionCallback<Object> callback,
            boolean preferReadOnlyTransactionTemplate) {
        boolean isReadOnlyTemplateUsed = false;
        TransactionTemplate templateToUse = null;
        if (preferReadOnlyTransactionTemplate && getReadOnlyTransactionTemplate() != null) {
            isReadOnlyTemplateUsed = true;
            templateToUse = getReadOnlyTransactionTemplate();
        } else {
            templateToUse = getTransactionTemplate();
        }

        Map<String, Object> retVal = null;
        if (templateToUse == null) {
            logger.debug("Executing procedure without using transaction template");
            // execute without transaction...
            retVal = (Map<String, Object>)callback.doInTransaction(null);
        } else {
            logger.debug("Executing procedure using {} transaction template",
                (isReadOnlyTemplateUsed ? "read-only" : ""));
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

}
