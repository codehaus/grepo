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

import org.codehaus.grepo.core.converter.ResultConversionService;
import org.codehaus.grepo.query.commons.executor.QueryExecutorFactory;
import org.codehaus.grepo.query.commons.executor.QueryExecutorFindingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author dguggi
 *
 * @param <T> The entity class type.
 */
public abstract class GenericRepositorySupport<T> implements GenericQueryRepository<T> {
    /** The logger for this class. */
    private final Logger logger = LoggerFactory.getLogger(GenericRepositorySupport.class);

    /** The application context. */
    private ApplicationContext applicationContext;

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

    /** The default max results to use. */
    private Integer maxResults;

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
            logger.debug("Executing query without using transaction template");
            // execute without transaction...
            retVal = callback.doInTransaction(null);
        } else {
            logger.debug("Executing query using {} transaction template",
                (isReadOnlyTemplateUsed ? " read-only " : " "));
            retVal = templateToUse.execute(callback);
        }
        return retVal;
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

    protected ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
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

    public Integer getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(Integer maxResults) {
        this.maxResults = maxResults;
    }
}
