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

package org.codehaus.grepo.query.commons.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.grepo.core.converter.ResultConversionService;
import org.codehaus.grepo.query.commons.executor.QueryExecutorFactory;
import org.codehaus.grepo.query.commons.executor.QueryExecutorFindingStrategy;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author dguggi
 *
 * @param <T> The entity class type.
 */
public abstract class GenericRepositorySupport<T> implements GenericRepository<T> {

    /** The logger for this class. */
    private static final Log LOG = LogFactory.getLog(GenericRepositorySupport.class);

    /** The transaction template to use. */
    private TransactionTemplate transactionTemplate;

    /** The read-only transaction template to use (optional). */
    private TransactionTemplate readOnlyTransactionTemplate;

    /** The executor finding strategy. */
    private QueryExecutorFindingStrategy queryExecutorFindingStrategy;

    /** The result conversion service. */
    private ResultConversionService resultConversionService;

    /** The query executor factory. */
    private QueryExecutorFactory queryExecutorFactory;

    /** The entity class. */
    private Class<T> entityClass;

    /**
     * Default constructor.
     */
    public GenericRepositorySupport() {
        super();
    }

    /**
     * @param entityClass The entity class.
     */
    public GenericRepositorySupport(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * {@inheritDoc}
     */
    public Class<T> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * @param callback The callback to execute.
     * @return Returns the result.
     * @see #executeCallback(TransactionCallback, boolean)
     */
    protected Object executeCallback(TransactionCallback callback) {
        return executeCallback(callback, false);
    }

    /**
     * Executes the given {@code callback} with either an read-only, normal or none transaction template.
     *
     * @param callback The callback to execute.
     * @param preferReadOnlyTransactionTemplate Flag to indicate if the read-only template should be prefered.
     * @return Returns the result.
     */
    @SuppressWarnings("PMD")
    protected Object executeCallback(TransactionCallback callback,
                boolean preferReadOnlyTransactionTemplate) {
        boolean isReadOnlyTemplateUsed = false;
        TransactionTemplate templateToUse = null;
        if (preferReadOnlyTransactionTemplate && readOnlyTransactionTemplate != null) {
            isReadOnlyTemplateUsed = true;
            templateToUse = readOnlyTransactionTemplate;
        } else {
            templateToUse = transactionTemplate;
        }

        Object retVal = null;
        if (templateToUse == null) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Executing query without using transaction template");
            }
            // execute without transaction...
            retVal = callback.doInTransaction(null);
        } else {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Executing query using" + (isReadOnlyTemplateUsed ? " read-only " : " ")
                    + "transaction template");
            }
            retVal = templateToUse.execute(callback);
        }
        return retVal;
    }

    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    protected TransactionTemplate getReadOnlyTransactionTemplate() {
        return readOnlyTransactionTemplate;
    }

    public void setReadOnlyTransactionTemplate(TransactionTemplate readOnlyTransactionTemplate) {
        this.readOnlyTransactionTemplate = readOnlyTransactionTemplate;
    }

    protected TransactionTemplate getTransactionTemplate() {
        return transactionTemplate;
    }

    protected QueryExecutorFindingStrategy getQueryExecutorFindingStrategy() {
        return queryExecutorFindingStrategy;
    }

    public void setQueryExecutorFindingStrategy(QueryExecutorFindingStrategy queryExecutorFindingStrategy) {
        this.queryExecutorFindingStrategy = queryExecutorFindingStrategy;
    }

    protected ResultConversionService getResultConversionService() {
        return resultConversionService;
    }

    public void setResultConversionService(ResultConversionService resultConversionService) {
        this.resultConversionService = resultConversionService;
    }

    protected QueryExecutorFactory getQueryExecutorFactory() {
        return queryExecutorFactory;
    }

    public void setQueryExecutorFactory(QueryExecutorFactory queryExecutorFactory) {
        this.queryExecutorFactory = queryExecutorFactory;
    }

}
