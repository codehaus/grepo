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
import javax.persistence.FlushModeType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.grepo.core.validator.GenericValidationUtils;
import org.codehaus.grepo.query.commons.annotation.GenericQuery;
import org.codehaus.grepo.query.commons.aop.QueryMethodParameterInfo;
import org.codehaus.grepo.query.commons.executor.QueryExecutor;
import org.codehaus.grepo.query.commons.repository.GenericRepositorySupport;
import org.codehaus.grepo.query.jpa.annotation.JpaFlushMode;
import org.codehaus.grepo.query.jpa.annotation.JpaQueryOptions;
import org.codehaus.grepo.query.jpa.executor.JpaQueryExecutionContext;
import org.codehaus.grepo.query.jpa.executor.JpaQueryExecutionContextImpl;
import org.codehaus.grepo.query.jpa.executor.JpaQueryExecutor;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.jpa.DefaultJpaDialect;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * @author dguggi
 *
 * @param <T> The main entity type.
 */
public class DefaultJpaRepository<T> extends GenericRepositorySupport<T> implements JpaRepository<T> {
    /** The logger for this class. */
    private static final Log LOG = LogFactory.getLog(DefaultJpaRepository.class);

    /** The entity manager factory. */
    private EntityManagerFactory entityManagerFactory;

    /** A map of jpa properties. */
    private Map<String, Object> jpaPropertyMap;

    /** The jpa dialect. */
    private JpaDialect jpaDialect = new DefaultJpaDialect();

    /** Flag to indicate whether or not exceptions should be translated. */
    private boolean translateExceptions = false;

    /** The jpa flush mode to set. */
    private JpaFlushMode flushMode = JpaFlushMode.UNDEFINED;

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

        JpaCallbackCreator callback = new JpaCallbackCreator() {
            @Override
            protected Object doExecute(JpaQueryExecutionContext context) {
                Object result = executor.execute(qmpi, context);
                if (LOG.isTraceEnabled()) {
                    String msg = String.format("Query result is '%s'", result);
                    LOG.trace(msg);
                }
                return result;
            }
        };

        return executeCallback(callback.create(qmpi), executor.isReadOnlyOperation());
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
     * @param emHolder The current entity manager info.
     */
    protected void closeNewEntityManager(CurrentEntityManagerHolder emHolder) {
        if (emHolder.isNewEm()) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Closing new EntityManager after generic repository execution");
            }
            EntityManagerFactoryUtils.closeEntityManager(emHolder.getEntityManager());
        } else {
            // we have an existing entity manager
            if (emHolder.getPreviousFlushMode() != null) {
                if (LOG.isTraceEnabled()) {
                    String msg = "Setting flushMode back to previous value '%s' after generic repository execution";
                    LOG.trace(String.format(msg, emHolder.getPreviousFlushMode()));
                }
                emHolder.getEntityManager().setFlushMode(emHolder.getPreviousFlushMode());
            }
        }
    }

    /**
     * Creates a hibernate query execution context.
     *
     * @param emHolder The mandatory entityManagerHolder.
     * @return Returns the newly created {@link HibernateQueryExecutionContext}.
     */
    protected JpaQueryExecutionContext createQueryExecutionContext(CurrentEntityManagerHolder emHolder) {
        JpaQueryExecutionContextImpl context = new JpaQueryExecutionContextImpl();
        context.setEntityManager(emHolder.getEntityManager());
        return context;
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

    /**
     * Convert the given runtime exception to an appropriate exception from the {@code org.springframework.dao}
     * hierarchy if necessary, or return the exception itself if it is not persistence related.
     * @param ex runtime exception that occured, which may or may not be JPA-related
     * @return the corresponding DataAccessException instance if wrapping should occur, otherwise the raw exception
     */
    protected RuntimeException translateIfNecessary(RuntimeException ex) {
        if (isTranslateExceptions()) {
            return DataAccessUtils.translateIfNecessary(ex, getJpaDialect());
        } else {
            return ex;
        }
    }

    /**
     * Apply the flush mode that's been specified.
     *
     * @param emHolder The current entity manager holder.
     * @param queryOptions the query options.
     */
    protected void applyFlushMode(CurrentEntityManagerHolder emHolder, JpaQueryOptions queryOptions) {
        JpaFlushMode flushModeToUse = getFlushMode(queryOptions);

        FlushModeType flushModeToSet = null;
        FlushModeType previousFlushMode = null;

        if (flushModeToUse != null && flushModeToUse.value() != null) {
            if (emHolder.isNewEm()) {
                flushModeToSet = flushModeToUse.value();
            } else {
                // we have an existing entity manager...
                previousFlushMode = emHolder.getEntityManager().getFlushMode();
                if (previousFlushMode != flushModeToUse.value()) {
                    flushModeToSet = flushModeToUse.value();
                }
            }
        }

        if (flushModeToSet != null) {
            if (LOG.isTraceEnabled()) {
                String msg = "Setting flushMode to '%s' for generic repository execution";
                LOG.trace(String.format(msg, flushModeToSet));
            }
            emHolder.getEntityManager().setFlushMode(flushModeToSet);
        }

        if (previousFlushMode != null) {
            emHolder.setPreviousFlushMode(previousFlushMode);
        }
    }

    /**
     * Flush the given the Hibernate session if necessary.
     * @param emHolder The session holder.
     * @param queryOptions the query options.
     */
    protected void flushIfNecessary(CurrentEntityManagerHolder emHolder, JpaQueryOptions queryOptions) {
        JpaFlushMode flushModeToUse = getFlushMode(queryOptions);

        if (flushModeToUse != null && flushModeToUse == JpaFlushMode.EAGER) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Eagerly flushing Jpa entity manager");
            }
            emHolder.getEntityManager().flush();
        }
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public Map<String, Object> getJpaPropertyMap() {
        return jpaPropertyMap;
    }

    public void setJpaPropertyMap(Map<String, Object> jpaPropertyMap) {
        this.jpaPropertyMap = jpaPropertyMap;
    }

    /**
     * @return Returns {@code true} if jpa properties available.
     */
    public boolean hasJpaProperties() {
        return !CollectionUtils.isEmpty(jpaPropertyMap);
    }

    public void setJpaDialect(JpaDialect jpaDialect) {
        this.jpaDialect = (jpaDialect == null ? new DefaultJpaDialect() : jpaDialect);
    }

    public JpaDialect getJpaDialect() {
        return this.jpaDialect;
    }

    public boolean isTranslateExceptions() {
        return translateExceptions;
    }

    public void setTranslateExceptions(boolean translateExceptions) {
        this.translateExceptions = translateExceptions;
    }

    public JpaFlushMode getFlushMode() {
        return flushMode;
    }

    /**
     * @param queryOptions The query options.
     * @return Returns the flush mode.
     */
    public JpaFlushMode getFlushMode(JpaQueryOptions queryOptions) {
        JpaFlushMode flushModeToUse = getFlushMode();
        if (queryOptions != null && queryOptions.flushMode() != JpaFlushMode.UNDEFINED) {
            flushModeToUse = queryOptions.flushMode();
        }
        return flushModeToUse;
    }

    public void setFlushMode(JpaFlushMode flushMode) {
        this.flushMode = flushMode;
    }

    /**
     * @author dguggi
     */
    protected class CurrentEntityManagerHolder {
        /** The is new flag. */
        private boolean newEm;

        /** The entity manager. */
        private EntityManager entityManager;

        /** The previous flush mode. */
        private FlushModeType previousFlushMode;

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

        public FlushModeType getPreviousFlushMode() {
            return previousFlushMode;
        }

        public void setPreviousFlushMode(FlushModeType previousFlushMode) {
            this.previousFlushMode = previousFlushMode;
        }

    }

    /**
     * @author dguggi
     */
    protected abstract class JpaCallbackCreator {

        /**
         * Creates a new transaction callback.
         * @param qmpi The query method parameter info. Note that this parameter is null for methods
         *        which are not annotated with {@code GenericQuery}.
         * @return Returns the call back.
         */
        public TransactionCallback create(final QueryMethodParameterInfo qmpi) {
            return new TransactionCallback() {
                public Object doInTransaction(TransactionStatus status) {
                    CurrentEntityManagerHolder emHolder = getCurrentEntityManager();

                    JpaQueryOptions queryOptions = null;
                    if (qmpi != null) {
                        queryOptions = qmpi.getMethodAnnotation(JpaQueryOptions.class);
                    }

                    try {
                        applyFlushMode(emHolder, queryOptions);

                        Object result = doExecute(createQueryExecutionContext(emHolder));

                        flushIfNecessary(emHolder, queryOptions);
                        return result;
                    } catch (RuntimeException e) {
                        throw translateIfNecessary(e);
                    } finally {
                        closeNewEntityManager(emHolder);
                    }
                }

            };
        }

        /**
         * Stuff to be executed in transaction.
         *
         * @param context The hibernate query execution context.
         * @return Returns the result or {@code null}.
         */
        protected abstract Object doExecute(JpaQueryExecutionContext context);
    }
}
