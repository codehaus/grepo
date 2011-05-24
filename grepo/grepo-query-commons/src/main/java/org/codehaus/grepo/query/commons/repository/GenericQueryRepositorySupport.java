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

import org.codehaus.grepo.query.commons.executor.QueryExecutorFactory;
import org.codehaus.grepo.query.commons.executor.QueryExecutorFindingStrategy;
import org.codehaus.grepo.query.commons.naming.QueryNamingStrategy;
import org.codehaus.grepo.statistics.domain.StatisticsEntry;
import org.codehaus.grepo.statistics.repository.GenericStatisticsRepositorySupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author dguggi
 * @param <T> The entity class type.
 */
public abstract class GenericQueryRepositorySupport<T> extends GenericStatisticsRepositorySupport
    implements GenericQueryRepository<T> {

    private static final Logger logger = LoggerFactory.getLogger(GenericQueryRepositorySupport.class);

    /** The executor finding strategy. */
    private QueryExecutorFindingStrategy queryExecutorFindingStrategy;

    /** The query executor factory. */
    private QueryExecutorFactory queryExecutorFactory;

    /** The query naming strategy. */
    private QueryNamingStrategy queryNamingStrategy;

    /** The entity class. */
    private Class<T> entityClass;

    /** The default max results to use. */
    private Integer maxResults;

    /**
     * Default constructor.
     */
    public GenericQueryRepositorySupport() {
        super();
    }

    /**
     * @param entityClass The entity class.
     */
    public GenericQueryRepositorySupport(Class<T> entityClass) {
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

    protected QueryExecutorFindingStrategy getQueryExecutorFindingStrategy() {
        return queryExecutorFindingStrategy;
    }

    public void setQueryExecutorFindingStrategy(QueryExecutorFindingStrategy queryExecutorFindingStrategy) {
        this.queryExecutorFindingStrategy = queryExecutorFindingStrategy;
    }

    protected QueryExecutorFactory getQueryExecutorFactory() {
        return queryExecutorFactory;
    }

    public void setQueryExecutorFactory(QueryExecutorFactory queryExecutorFactory) {
        this.queryExecutorFactory = queryExecutorFactory;
    }

    public QueryNamingStrategy getQueryNamingStrategy() {
        return queryNamingStrategy;
    }

    public void setQueryNamingStrategy(QueryNamingStrategy queryNamingStrategy) {
        this.queryNamingStrategy = queryNamingStrategy;
    }

    public Integer getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(Integer maxResults) {
        this.maxResults = maxResults;
    }

    /**
     * Executes the given {@code callback} with either an read-only, normal or none transaction template.
     *
     * @param callback The callback to execute.
     * @param preferReadOnlyTransactionTemplate Flag to indicate if the read-only template should be prefered.
     * @return Returns the result.
     */
    protected Object executeCallback(TransactionCallback<Object> callback, boolean preferReadOnlyTransactionTemplate) {
        boolean isReadOnlyTemplateUsed = false;
        TransactionTemplate templateToUse = null;
        if (preferReadOnlyTransactionTemplate && getReadOnlyTransactionTemplate() != null) {
            isReadOnlyTemplateUsed = true;
            templateToUse = getReadOnlyTransactionTemplate();
        } else {
            templateToUse = getTransactionTemplate();
        }

        Object retVal = null;
        if (templateToUse == null) {
            logger.debug("Executing query without using transaction template");
            // execute without transaction...
            retVal = callback.doInTransaction(null);
        } else {
            logger.debug("Executing query using{}transaction template",
                (isReadOnlyTemplateUsed ? " read-only " : " "));
            retVal = templateToUse.execute(callback);
        }
        return retVal;
    }

    /**
     * Exectures the given {@code callback} and creates a {@link StatisticsEntry} for each invocation if necessary.
     *
     * @param methodName The method name.
     * @param callback The callback.
     * @param preferReadOnlyTransactionTemplate Flag to indicate if the read-only template should be prefered.
     * @return Returns the result.
     */
    protected Object executeCallbackWithStatistics(String methodName, TransactionCallback<Object> callback,
            boolean preferReadOnlyTransactionTemplate) {
        StatisticsEntry entry = null;
        if (isStatisticsEnabled()) {
            entry = createStatisticsEntry(createStatisticEntryIdentifier(methodName));
        }

        try {
            return executeCallback(callback, preferReadOnlyTransactionTemplate);
        } finally {
            completeStatisticsEntry(entry);
        }
    }

    /**
     * @param methodName The method name.
     * @return Returns the proxy interface method name.
     */
    protected String createStatisticEntryIdentifier(String methodName) {
        return getProxyInterface().getName() + "." + methodName;
    }
}
