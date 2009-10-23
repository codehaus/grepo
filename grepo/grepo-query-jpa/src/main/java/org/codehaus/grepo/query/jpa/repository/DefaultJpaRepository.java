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

package org.codehaus.grepo.query.jpa.repository;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.grepo.core.validator.GenericValidationUtils;
import org.codehaus.grepo.query.commons.annotation.GenericQuery;
import org.codehaus.grepo.query.commons.aop.QueryMethodParameterInfo;
import org.codehaus.grepo.query.commons.executor.QueryExecutor;
import org.codehaus.grepo.query.commons.repository.GenericRepositorySupport;
import org.codehaus.grepo.query.jpa.executor.JpaQueryExecutor;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * @author dguggi
 *
 * @param <T> The main entity type.
 */
public class DefaultJpaRepository<T> extends GenericRepositorySupport<T> {
    /** The logger for this class. */
    private static final Log LOG = LogFactory.getLog(DefaultJpaRepository.class);

    /** The entity manager factory. */
    private EntityManagerFactory entityManagerFactory;

    /** A map of jpa properties. */
    private Map<String, Object> jpaPropertyMap;

    /**
     * Default constructor.
     */
    public DefaultJpaRepository() {
        super();
    }

    /**
     * @param entityType The main entity type.
     */
    public DefaultJpaRepository(Class<T> entityType) {
        super(entityType);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("PMD")
    public Object executeGenericQuery(QueryMethodParameterInfo qmpi, GenericQuery genericQuery) throws Exception {
        Object result = executeQuery(qmpi, genericQuery);

        result = convertResult(result, qmpi, genericQuery);

        validateResult(result, qmpi, genericQuery);

        return result;
    }

    /**
     * @param qmpi The query method parameter info.
     * @param genericQuery The annotation.
     * @return Returns the result of query execution.
     */
    protected Object executeQuery(final QueryMethodParameterInfo qmpi, final GenericQuery genericQuery) {
        Class<? extends QueryExecutor<?>> clazz = (Class<? extends QueryExecutor<?>>)getQueryExecutorFindingStrategy()
            .findExecutor(genericQuery.queryExecutor(), qmpi);

        final JpaQueryExecutor executor = (JpaQueryExecutor)getQueryExecutorFactory().createExecutor(clazz);

        TransactionCallback callback = new TransactionCallback() {
            public Object doInTransaction(final TransactionStatus status) {
                CurrentEntityManagerHolder info = getCurrentEntityManager();

                try {
                    Object result = executor.execute(qmpi, info.getEntityManager());
                    if (LOG.isTraceEnabled()) {
                        String msg = String.format("Query result is '%s'", result);
                        LOG.trace(msg);
                    }
                    return result;
                } finally {
                    closeNewEntityManager(info);
                }
            }
        };

        return executeCallback(callback, executor.isReadOnlyOperation());
    }

    /**
     * @return Returns {@code true} if the entity manager was newly created.
     */
    @SuppressWarnings("PMD")
    protected CurrentEntityManagerHolder getCurrentEntityManager() {
        boolean isNewEm = false;
        EntityManager em = getTransactionalEntityManager();
        if (em == null) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Creating new EntityManager for generic repository execution");
            }
            em = createEntityManager();
            isNewEm = true;
        }
        return new CurrentEntityManagerHolder(isNewEm, em);
    }

    /**
     * @param info The current entity manager info.
     */
    protected void closeNewEntityManager(CurrentEntityManagerHolder info) {
        if (info.isNewEm()) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Closing new EntityManager after generic repository execution");
            }
            EntityManagerFactoryUtils.closeEntityManager(info.getEntityManager());
        }
    }

    /**
     * @param result The result to convert.
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
     * @param result The result.
     * @param qmpi The query method parameter info.
     * @param genericQuery The annotation.
     * @throws Exception in case of errors.
     */
    @SuppressWarnings("PMD")
    protected void validateResult(Object result, QueryMethodParameterInfo qmpi, GenericQuery genericQuery)
            throws Exception {
        GenericValidationUtils.validateResult(qmpi, genericQuery.resultValidator(), result);
    }

    /**
     * @return Returns the transaction manager.
     * @throws IllegalStateException in case factory is null
     */
    protected EntityManager getTransactionalEntityManager() throws IllegalStateException {
        Assert.state(getEntityManagerFactory() != null, "No EntityManagerFactory specified");
        return EntityManagerFactoryUtils.getTransactionalEntityManager(getEntityManagerFactory(), getJpaPropertyMap());
    }

    /**
     * @return Returns the entity manager.
     * @throws IllegalStateException in case factory is null
     */
    protected EntityManager createEntityManager() throws IllegalStateException {
        Assert.state(getEntityManagerFactory() != null, "No EntityManagerFactory specified");
        return (hasJpaProperties() ? getEntityManagerFactory().createEntityManager(getJpaPropertyMap())
            : getEntityManagerFactory().createEntityManager());
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    protected EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    protected Map<String, Object> getJpaPropertyMap() {
        return jpaPropertyMap;
    }

    public void setJpaPropertyMap(Map<String, Object> jpaPropertyMap) {
        this.jpaPropertyMap = jpaPropertyMap;
    }

    /**
     * @return Returns {@code true} if jpa properties available.
     */
    protected boolean hasJpaProperties() {
        return !CollectionUtils.isEmpty(jpaPropertyMap);
    }

    /**
     * @author dguggi
     */
    protected class CurrentEntityManagerHolder {
        /** The is new flag. */
        private boolean newEm;

        /** The entity manager. */
        private EntityManager entityManager;

        /**
         * @param isNew The flag.
         * @param entityManager The entity manager.
         */
        public CurrentEntityManagerHolder(boolean isNew, EntityManager entityManager) {
            super();
            this.newEm = isNew;
            this.entityManager = entityManager;
        }

        public boolean isNewEm() {
            return newEm;
        }

        public void setNewEm(boolean isNew) {
            this.newEm = isNew;
        }

        public EntityManager getEntityManager() {
            return entityManager;
        }

        public void setEntityManager(EntityManager entityManager) {
            this.entityManager = entityManager;
        }

    }
}
