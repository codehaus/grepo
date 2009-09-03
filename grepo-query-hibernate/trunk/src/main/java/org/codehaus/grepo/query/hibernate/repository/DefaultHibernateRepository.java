/*
 * Copyright (c) 2007 Daniel Guggi.
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

package org.codehaus.grepo.query.hibernate.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.grepo.core.validator.GenericValidationUtils;
import org.codehaus.grepo.query.commons.annotation.GenericQuery;
import org.codehaus.grepo.query.commons.aop.QueryMethodParameterInfo;
import org.codehaus.grepo.query.commons.executor.QueryExecutor;
import org.codehaus.grepo.query.commons.repository.AbstractGenericRepository;
import org.codehaus.grepo.query.hibernate.executor.HibernateQueryExecutor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

/**
 * @author dguggi
 *
 * @param <T> The main entity type.
 */
public class DefaultHibernateRepository<T> extends AbstractGenericRepository<T> {
    /** The logger for this class. */
    private static final Log LOG = LogFactory.getLog(DefaultHibernateRepository.class);

    /** The session factory. */
    private SessionFactory sessionFactory;

    /**
     * Default constructor.
     */
    public DefaultHibernateRepository() {
        super();
    }

    /**
     * @param entityType The main entity type.
     */
    public DefaultHibernateRepository(Class<T> entityType) {
        super(entityType);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("PMD")
    public Object executeGenericQuery(QueryMethodParameterInfo qmpi, GenericQuery annotation)
            throws Exception {

        Object result = executeQuery(qmpi, annotation);

        result = convertResult(result, qmpi, annotation);

        validateResult(result, qmpi, annotation);

        return result;
    }

    /**
     * @param qmpi The query method parameter info.
     * @param genericQuery The annotation
     * @return Returns the result of the query execution.
     */
    protected Object executeQuery(final QueryMethodParameterInfo qmpi, GenericQuery genericQuery) {
        Class<? extends QueryExecutor<?>> clazz =
            (Class<? extends QueryExecutor<?>>) getExecutorFindingStrategy().findExecutor(
                    genericQuery.queryExecutor(), qmpi);

        final HibernateQueryExecutor executor = (HibernateQueryExecutor)getExecutorFactory().createExecutor(clazz);

        TransactionCallback callback = new TransactionCallback() {
            public Object doInTransaction(TransactionStatus status) {
                Object result = executor.execute(qmpi, getSession());
                if (LOG.isTraceEnabled()) {
                    String msg = String.format("Query result is '%s'", result);
                    LOG.trace(msg);
                }
                return result;
            }
        };

        return executeCallback(callback, executor.isReadOnlyOperation());
    }

    /**
     * @param result The query result to convert.
     * @param qmpi The query method parameter info.
     * @param genericQuery The annotation.
     * @return Returns the possibly converted result.
     */
    protected Object convertResult(Object result, QueryMethodParameterInfo qmpi, GenericQuery genericQuery) {
        if (getResultConversionService() == null) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("No result conversion is performed, because no resultConversionService is configured");
            }
            return result;
        } else {
            return getResultConversionService().convert(qmpi, genericQuery.resultConverter(), result);
        }
    }

    /**
     * @param result The result to validate.
     * @param qmpi The query method parameter info.
     * @param genericQuery The annotation.
     * @throws Exception in case of errors.
     */
    @SuppressWarnings("PMD")
    protected void validateResult(Object result, QueryMethodParameterInfo qmpi, GenericQuery genericQuery)
            throws Exception {
        GenericValidationUtils.validateResult(qmpi, genericQuery.resultValidator(), result);
    }

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    protected SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

}
