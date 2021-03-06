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

package org.codehaus.grepo.query.hibernate.repository;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.codehaus.grepo.core.validator.GenericValidationUtils;
import org.codehaus.grepo.query.commons.annotation.GenericQuery;
import org.codehaus.grepo.query.commons.aop.QueryMethodParameterInfo;
import org.codehaus.grepo.query.commons.executor.QueryExecutor;
import org.codehaus.grepo.query.commons.generator.GeneratorUtils;
import org.codehaus.grepo.query.commons.repository.GenericQueryRepositorySupport;
import org.codehaus.grepo.query.hibernate.annotation.HibernateCacheMode;
import org.codehaus.grepo.query.hibernate.annotation.HibernateCaching;
import org.codehaus.grepo.query.hibernate.annotation.HibernateFlushMode;
import org.codehaus.grepo.query.hibernate.annotation.HibernateQueryOptions;
import org.codehaus.grepo.query.hibernate.context.HibernateQueryExecutionContext;
import org.codehaus.grepo.query.hibernate.context.HibernateQueryExecutionContextImpl;
import org.codehaus.grepo.query.hibernate.executor.HibernateQueryExecutor;
import org.codehaus.grepo.query.hibernate.filter.FilterDescriptor;
import org.codehaus.grepo.query.hibernate.generator.HibernateCriteriaGenerator;
import org.codehaus.grepo.query.hibernate.generator.HibernateQueryGenerator;
import org.codehaus.grepo.query.hibernate.type.ArgumentTypeFactory;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.JDBCException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.event.EventSource;
import org.hibernate.exception.GenericJDBCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

/**
 * @author dguggi
 * @param <T> The main entity type.
 */
public class DefaultHibernateRepository<T> extends GenericQueryRepositorySupport<T> implements HibernateRepository<T> {

    private static final Logger logger = LoggerFactory.getLogger(DefaultHibernateRepository.class);

    /** The session factory. */
    private SessionFactory sessionFactory;

    /** Flag to indicate whether or not the native session should be exposed. */
    private boolean exposeNativeSession = true;

    /** Flag to indicate wether or not we have to use a new session always. */
    private boolean alwaysUseNewSession = false;

    /**
     * Flag to indicate whether or not it is allowed to create a non transactional session if there is no transactional
     * session bound to the current thread.
     */
    private boolean allowCreate = true;

    /** The hibernate flush mode to set. */
    private HibernateFlushMode flushMode = HibernateFlushMode.UNDEFINED;

    /** The hibernate caching flag. */
    private HibernateCaching caching = HibernateCaching.UNDEFINED;

    /** The cache region. */
    private String cacheRegion;

    /** the hibernate cache mode to set. */
    private HibernateCacheMode cacheMode = HibernateCacheMode.UNDEFINED;

    /** The entity interceptor. */
    private Interceptor entityInterceptor;

    /** The hibernate filters to use. */
    private FilterDescriptor[] filters;

    /** Flag to indicate whether or not exceptions should be translated. */
    private boolean translateExceptions = false;

    /** The jdbc exception translator. */
    private SQLExceptionTranslator jdbcExceptionTranslator;

    /** The default jdbc exception translator. */
    private SQLExceptionTranslator defaultJdbcExceptionTranslator;

    /** The default fetch size for criteria and queries. */
    private Integer fetchSize;

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
    public Object executeGenericQuery(QueryMethodParameterInfo qmpi, GenericQuery annotation) throws Exception {
        createStatisticsEntry(qmpi);
        try {
            Object result = executeQuery(qmpi, annotation);

            result = convertResult(result, qmpi, annotation);

            validateResult(result, qmpi, annotation);

            return result;
        } finally {
            completeStatisticsEntry(qmpi.getStatisticsEntry());
        }
    }

    /**
     * @param qmpi The query method parameter info.
     * @param genericQuery The annotation
     * @return Returns the result of the query execution.
     */
    @SuppressWarnings("unchecked")
    protected Object executeQuery(final QueryMethodParameterInfo qmpi, final GenericQuery genericQuery) {
        Class<? extends QueryExecutor<?>> clazz =
            (Class<? extends QueryExecutor<?>>)getQueryExecutorFindingStrategy().findExecutor(
                genericQuery.queryExecutor(), qmpi);

        final HibernateQueryExecutor executor = (HibernateQueryExecutor)getQueryExecutorFactory().createExecutor(clazz);
        GeneratorUtils.validateQueryGenerator(genericQuery.queryGenerator(), HibernateQueryGenerator.class,
            HibernateCriteriaGenerator.class);

        HibernateCallbackCreator callback = new HibernateCallbackCreator() {

            @Override
            protected Object doExecute(HibernateQueryExecutionContext context) throws HibernateException {
                Object result = executor.execute(qmpi, context);
                logger.debug("Query result is '{}'", result);
                return result;
            }

        };

        return executeCallback(callback.create(qmpi, isExposeNativeSession()), executor.isReadOnlyOperation());
    }

    /**
     * Creates a hibernate query execution context.
     *
     * @param sessionHolder The mandatory session holder.
     * @param doExposeNativeSession Controls whether to expose the native Hibernate session to callback code.
     * @return Returns the newly created {@link HibernateQueryExecutionContext}.
     */
    protected HibernateQueryExecutionContext createQueryExecutionContext(CurrentSessionHolder sessionHolder,
                                                                         boolean doExposeNativeSession) {
        HibernateQueryExecutionContextImpl context = new HibernateQueryExecutionContextImpl();
        context.setApplicationContext(getApplicationContext());
        context.setCaching(getCaching());
        context.setCacheRegion(getCacheRegion());
        context.setMaxResults(getMaxResults());
        context.setFetchSize(getFetchSize());
        context.setSessionFactory(getSessionFactory());
        context.setQueryNamingStrategy(getQueryNamingStrategy());
        context.setArgumentTypeFactory(getArgumentTypeFactory());

        if (doExposeNativeSession) {
            context.setSession(sessionHolder.getSession());
        } else {
            context.setSession(createSessionProxy(sessionHolder.getSession()));
        }

        return context;
    }

    /**
     * Create a close-suppressing proxy for the given Hibernate Session.
     *
     * @param session The Hibernate Session to create a proxy for.
     * @return The Session proxy
     */
    protected Session createSessionProxy(Session session) {
        Class<?>[] sessionIfcs = null;
        Class<?> mainIfc =
            (session instanceof org.hibernate.classic.Session ? org.hibernate.classic.Session.class : Session.class);
        if (session instanceof EventSource) {
            sessionIfcs = new Class[] {mainIfc, EventSource.class };
        } else if (session instanceof SessionImplementor) {
            sessionIfcs = new Class[] {mainIfc, SessionImplementor.class };
        } else {
            sessionIfcs = new Class[] {mainIfc };
        }
        return (Session)Proxy.newProxyInstance(session.getClass().getClassLoader(), sessionIfcs,
            new CloseSuppressingInvocationHandler(session));
    }

    /**
     * @param result The query result to convert.
     * @param qmpi The query method parameter info.
     * @param genericQuery The annotation.
     * @return Returns the possibly converted result.
     */
    protected Object convertResult(Object result, QueryMethodParameterInfo qmpi, GenericQuery genericQuery) {
        if (getResultConversionService() == null) {
            logger.debug("No result conversion is performed, because no resultConversionService is configured");
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
    protected void validateResult(Object result, QueryMethodParameterInfo qmpi, //
                                  GenericQuery genericQuery) throws Exception {
        GenericValidationUtils.validateResult(qmpi, genericQuery.resultValidator(), result);
    }

    /**
     * @return Returns the current sesssion holder.
     */
    protected CurrentSessionHolder getCurrentSession() {
        Session session = null;
        if (isAlwaysUseNewSession()) {
            session = SessionFactoryUtils.getNewSession(getSessionFactory(), getEntityInterceptor());
        } else if (isAllowCreate()) {
            session =
                SessionFactoryUtils.getSession(getSessionFactory(), getEntityInterceptor(),
                    getJdbcExceptionTranslator());
        } else if (SessionFactoryUtils.hasTransactionalSession(getSessionFactory())) {
            session = SessionFactoryUtils.getSession(getSessionFactory(), false);
        } else {
            try {
                session = getSessionFactory().getCurrentSession();
            } catch (HibernateException ex) {
                throw new DataAccessResourceFailureException("Could not obtain current Hibernate Session", ex);
            }
        }

        boolean existingTransaction =
            (!isAlwaysUseNewSession() && (!isAllowCreate() || SessionFactoryUtils.isSessionTransactional(session,
                getSessionFactory())));

        if (!existingTransaction) {
            logger.debug("No existing transactional Hibernate Session for generic repository execution found");
        }

        return new CurrentSessionHolder(session, existingTransaction);
    }

    /**
     * Closes new (not-transactional) sessions.
     *
     * @param sessionHolder The session holder.
     */
    protected void closeNewSession(CurrentSessionHolder sessionHolder) {
        if (sessionHolder.isExistingTransaction()) {
            disableFilters(sessionHolder);
            if (sessionHolder.getPreviousFlushMode() != null) {
                logger.debug("Setting flushMode back to previous value '{}' after generic repository execution",
                    sessionHolder.getPreviousFlushMode());
                sessionHolder.getSession().setFlushMode(sessionHolder.getPreviousFlushMode());
            }
            if (sessionHolder.getPreviousCacheMode() != null) {
                logger.debug("Setting cacheMode back to previous value '{}' after generic repository execution",
                    sessionHolder.getPreviousCacheMode());
                sessionHolder.getSession().setCacheMode(sessionHolder.getPreviousCacheMode());
            }
        } else {
            logger.debug("Closing (new) non-transactional Hibernate Session after generic repository execution");
            SessionFactoryUtils.closeSession(sessionHolder.getSession());
        }
    }


    /**
     * Flush the given the Hibernate session if necessary.
     *
     * @param sessionHolder The session holder.
     * @param queryOptions the query options.
     * @throws HibernateException in case of Hibernate flushing errors
     */
    protected void flushIfNecessary(CurrentSessionHolder sessionHolder, //
                                    HibernateQueryOptions queryOptions) throws HibernateException {
        HibernateFlushMode flushModeToUse = getFlushMode(queryOptions);

        if (flushModeToUse != null && (flushModeToUse == HibernateFlushMode.EAGER //
            || (!sessionHolder.isExistingTransaction() && flushModeToUse != HibernateFlushMode.MANUAL))) {
            logger.debug("Eagerly flushing Hibernate session");
            sessionHolder.getSession().flush();
        }
    }

    /**
     * Apply the flush mode that's been specified.
     *
     * @param sessionHolder The current session holder.
     * @param queryOptions the query options.
     */
    protected void applyFlushMode(CurrentSessionHolder sessionHolder, HibernateQueryOptions queryOptions) {
        HibernateFlushMode flushModeToUse = getFlushMode(queryOptions);

        if (flushModeToUse != null) {
            FlushMode flushModeToSet = null;
            FlushMode previousFlushMode = null;

            if (flushModeToUse == HibernateFlushMode.MANUAL) {
                if (sessionHolder.isExistingTransaction()) {
                    previousFlushMode = sessionHolder.getSession().getFlushMode();
                    if (!previousFlushMode.lessThan(FlushMode.COMMIT)) {
                        flushModeToSet = FlushMode.MANUAL;
                    }
                } else {
                    flushModeToSet = FlushMode.MANUAL;
                }
            } else if (flushModeToUse == HibernateFlushMode.EAGER) {
                if (sessionHolder.isExistingTransaction()) {
                    previousFlushMode = sessionHolder.getSession().getFlushMode();
                    if (!previousFlushMode.equals(FlushMode.AUTO)) {
                        flushModeToSet = FlushMode.AUTO;
                    }
                }
                // else rely on default FlushMode.AUTO
            } else if (flushModeToUse == HibernateFlushMode.COMMIT) {
                if (sessionHolder.isExistingTransaction()) {
                    previousFlushMode = sessionHolder.getSession().getFlushMode();
                    if (previousFlushMode.equals(FlushMode.AUTO) || previousFlushMode.equals(FlushMode.ALWAYS)) {
                        flushModeToSet = FlushMode.COMMIT;
                    }
                } else {
                    flushModeToSet = FlushMode.COMMIT;
                }
            } else if (flushModeToUse == HibernateFlushMode.ALWAYS) {
                if (sessionHolder.isExistingTransaction()) {
                    previousFlushMode = sessionHolder.getSession().getFlushMode();
                    if (!previousFlushMode.equals(FlushMode.ALWAYS)) {
                        flushModeToSet = FlushMode.ALWAYS;
                    }
                } else {
                    flushModeToSet = FlushMode.ALWAYS;
                }
            }

            if (flushModeToSet != null) {
                logger.debug("Setting flushMode to '{}' for generic repository execution", flushModeToSet);
                sessionHolder.getSession().setFlushMode(flushModeToSet);
            }

            if (previousFlushMode != null) {
                sessionHolder.setPreviousFlushMode(previousFlushMode);
            }
        }
    }

    /**
     * Apply the cache mode that's been specified.
     *
     * @param sessionHolder The session holder.
     * @param queryOptions The query options.
     */
    protected void applyCacheMode(CurrentSessionHolder sessionHolder, HibernateQueryOptions queryOptions) {
        HibernateCacheMode cacheModeToUse = getCacheMode(queryOptions);

        if (cacheModeToUse != null && cacheModeToUse != HibernateCacheMode.UNDEFINED) {
            // we have a cache mode specified...
            logger.debug("Setting cacheMode to '{}' for generic repository execution", cacheModeToUse);
            CacheMode previousCacheMode = sessionHolder.getSession().getCacheMode();
            sessionHolder.getSession().setCacheMode(cacheModeToUse.value());

            if (previousCacheMode != null) {
                sessionHolder.setPreviousCacheMode(previousCacheMode);
            }
        }
    }

    /**
     * Obtain a default SQLExceptionTranslator, lazily creating it if necessary.
     *
     * @return Returns the default jdbc exception translator.
     */
    protected SQLExceptionTranslator getDefaultJdbcExceptionTranslator() {
        if (this.defaultJdbcExceptionTranslator == null) {
            this.defaultJdbcExceptionTranslator = SessionFactoryUtils.newJdbcExceptionTranslator(getSessionFactory());
        }
        return this.defaultJdbcExceptionTranslator;
    }

    /**
     * Convert the given runtime exception to an appropriate exception from the {@code org.springframework.dao}
     * hierarchy if necessary, or return the exception itself if it is not persistence related.
     *
     * @param e runtime exception that occured, which may or may not be Hibernate-related
     * @return the corresponding DataAccessException instance if wrapping should occur, otherwise the raw exception
     */
    protected RuntimeException translateIfNecessary(RuntimeException e) {
        if (isTranslateExceptions() && e instanceof HibernateException) {
            return convertHibernateAccessException((HibernateException)e);
        } else {
            return e;
        }
    }

    /**
     * Convert the given HibernateException to an appropriate exception from the {@code org.springframework.dao}
     * hierarchy. Will automatically apply a specified SQLExceptionTranslator to a Hibernate JDBCException, else rely on
     * Hibernate's default translation.
     *
     * @param ex HibernateException that occured
     * @return a corresponding DataAccessException
     */
    public DataAccessException convertHibernateAccessException(HibernateException ex) {
        if (getJdbcExceptionTranslator() != null && ex instanceof JDBCException) {
            return convertJdbcAccessException((JDBCException)ex, getJdbcExceptionTranslator());
        } else if (GenericJDBCException.class.equals(ex.getClass())) {
            return convertJdbcAccessException((GenericJDBCException)ex, getDefaultJdbcExceptionTranslator());
        }
        return SessionFactoryUtils.convertHibernateAccessException(ex);
    }

    /**
     * Convert the given Hibernate JDBCException to an appropriate exception from the {@code org.springframework.dao}
     * hierarchy, using the given SQLExceptionTranslator.
     *
     * @param ex Hibernate JDBCException that occured
     * @param translator the SQLExceptionTranslator to use
     * @return a corresponding DataAccessException
     */
    protected DataAccessException convertJdbcAccessException(JDBCException ex, SQLExceptionTranslator translator) {
        return translator.translate("Hibernate operation: " + ex.getMessage(), ex.getSQL(), ex.getSQLException());
    }

    /**
     * @param qmpi The query method parameter info.
     * @param sessionHolder The session holder.
     */
    protected void enableFilters(QueryMethodParameterInfo qmpi, CurrentSessionHolder sessionHolder) {
        if (getFilters() != null) {
            for (FilterDescriptor fd : getFilters()) {
                if (fd.hasEnabler()) {
                    fd.getEnabler().enable(fd.getName(), qmpi, sessionHolder.getSession());
                } else {
                    sessionHolder.getSession().enableFilter(fd.getName());
                }
            }
        }
    }

    /**
     * @param sessionHolder The session holder.
     */
    protected void disableFilters(CurrentSessionHolder sessionHolder) {
        if (getFilters() != null) {
            for (FilterDescriptor fd : getFilters()) {
                sessionHolder.getSession().disableFilter(fd.getName());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GrepoQueryHibernateConfiguration getConfiguration() {
        return (GrepoQueryHibernateConfiguration)super.getConfiguration();
    }

    public ArgumentTypeFactory getArgumentTypeFactory() {
        return getConfiguration().getArgumentTypeFactory();
    }

    public void setFilter(FilterDescriptor fd) {
        this.filters = new FilterDescriptor[] {fd };
    }

    public void setFilters(FilterDescriptor[] fds) {
        this.filters = fds;
    }

    public FilterDescriptor[] getFilters() {
        return this.filters;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public boolean isExposeNativeSession() {
        return exposeNativeSession;
    }

    public void setExposeNativeSession(boolean exposeNativeSession) {
        this.exposeNativeSession = exposeNativeSession;
    }

    public boolean isAlwaysUseNewSession() {
        return alwaysUseNewSession;
    }

    public void setAlwaysUseNewSession(boolean alwaysUseNewSession) {
        this.alwaysUseNewSession = alwaysUseNewSession;
    }

    public boolean isAllowCreate() {
        return allowCreate;
    }

    public void setAllowCreate(boolean allowCreate) {
        this.allowCreate = allowCreate;
    }

    public HibernateFlushMode getFlushMode() {
        return flushMode;
    }

    /**
     * @param queryOptions The query options.
     * @return Returns the flush mode.
     */
    public HibernateFlushMode getFlushMode(HibernateQueryOptions queryOptions) {
        HibernateFlushMode flushModeToUse = getFlushMode();
        if (queryOptions != null && queryOptions.flushMode() != HibernateFlushMode.UNDEFINED) {
            flushModeToUse = queryOptions.flushMode();
        }
        return flushModeToUse;
    }

    public void setFlushMode(HibernateFlushMode flushMode) {
        this.flushMode = flushMode;
    }


    public HibernateCacheMode getCacheMode() {
        return cacheMode;
    }

    /**
     * @param queryOptions The query options.
     * @return Returns the cache mode.
     */
    public HibernateCacheMode getCacheMode(HibernateQueryOptions queryOptions) {
        HibernateCacheMode cacheModeToUse = getCacheMode();
        if (queryOptions != null && queryOptions.cacheMode() != HibernateCacheMode.UNDEFINED) {
            cacheModeToUse = queryOptions.cacheMode();
        }
        return cacheModeToUse;
    }

    public void setCacheMode(HibernateCacheMode cacheMode) {
        this.cacheMode = cacheMode;
    }

    public HibernateCaching getCaching() {
        return caching;
    }

    public void setCaching(HibernateCaching caching) {
        this.caching = caching;
    }

    public String getCacheRegion() {
        return cacheRegion;
    }

    public void setCacheRegion(String cacheRegion) {
        this.cacheRegion = cacheRegion;
    }

    public Interceptor getEntityInterceptor() {
        return entityInterceptor;
    }

    public void setEntityInterceptor(Interceptor entityInterceptor) {
        this.entityInterceptor = entityInterceptor;
    }

    public boolean isTranslateExceptions() {
        return translateExceptions;
    }

    public void setTranslateExceptions(boolean translateExceptions) {
        this.translateExceptions = translateExceptions;
    }

    public SQLExceptionTranslator getJdbcExceptionTranslator() {
        return jdbcExceptionTranslator;
    }

    public void setJdbcExceptionTranslator(SQLExceptionTranslator jdbcExceptionTranslator) {
        this.jdbcExceptionTranslator = jdbcExceptionTranslator;
    }

    public Integer getFetchSize() {
        return fetchSize;
    }

    public void setFetchSize(Integer fetchSize) {
        this.fetchSize = fetchSize;
    }


    /**
     * @author dguggi
     */
    protected class CurrentSessionHolder {

        private Session session;
        private boolean existingTransaction;
        private FlushMode previousFlushMode;
        private CacheMode previousCacheMode;

        public CurrentSessionHolder(Session session, boolean existingTransaction) {
            this.session = session;
            this.existingTransaction = existingTransaction;
        }

        public Session getSession() {
            return session;
        }

        public void setSession(Session session) {
            this.session = session;
        }

        public boolean isExistingTransaction() {
            return existingTransaction;
        }

        public void setExistingTransaction(boolean existingTransaction) {
            this.existingTransaction = existingTransaction;
        }

        public FlushMode getPreviousFlushMode() {
            return previousFlushMode;
        }

        public void setPreviousFlushMode(FlushMode previousFlushMode) {
            this.previousFlushMode = previousFlushMode;
        }

        public CacheMode getPreviousCacheMode() {
            return previousCacheMode;
        }

        public void setPreviousCacheMode(CacheMode previousCacheMode) {
            this.previousCacheMode = previousCacheMode;
        }

    }


    /**
     * @author dguggi
     */
    protected abstract class HibernateCallbackCreator {

        /**
         * Creates a new transaction callback.
         *
         * @param qmpi The query method parameter info. Note that this parameter is null for methods which are not
         *            annotated with {@code GenericQuery}.
         * @param doExposeNativeSession Controls whether to expose the native Hibernate session to callback code.
         * @return Returns the call back.
         */
        public TransactionCallback<Object> create(final QueryMethodParameterInfo qmpi,
                                                  final boolean doExposeNativeSession) {
            return new TransactionCallback<Object>() {

                public Object doInTransaction(TransactionStatus status) {
                    CurrentSessionHolder sessionHolder = getCurrentSession();

                    HibernateQueryOptions queryOptions = null;
                    if (qmpi != null) {
                        queryOptions = qmpi.getMethodAnnotation(HibernateQueryOptions.class);
                    }

                    try {
                        applyFlushMode(sessionHolder, queryOptions);
                        applyCacheMode(sessionHolder, queryOptions);
                        enableFilters(qmpi, sessionHolder);

                        Object result = doExecute(createQueryExecutionContext(sessionHolder, doExposeNativeSession));

                        flushIfNecessary(sessionHolder, queryOptions);
                        return result;
                    } catch (RuntimeException e) {
                        throw translateIfNecessary(e);
                    } finally {
                        closeNewSession(sessionHolder);
                    }
                }

            };
        }

        /**
         * Stuff to be executed in transaction.
         *
         * @param context The hibernate query execution context.
         * @return Returns the result or {@code null}.
         * @throws HibernateException in case of errors.
         */
        protected abstract Object doExecute(HibernateQueryExecutionContext context) throws HibernateException;
    }


    /**
     * Invocation handler that suppresses close calls on Hibernate session.
     *
     * @author dguggi
     */
    private class CloseSuppressingInvocationHandler implements InvocationHandler {

        private final Session target;

        public CloseSuppressingInvocationHandler(Session target) {
            this.target = target;
        }

        /**
         * {@inheritDoc}
         */
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("equals")) {
                // Only consider equal when proxies are identical.
                return (proxy == args[0]);
            } else if (method.getName().equals("hashCode")) {
                // Use hashCode of Session proxy.
                return System.identityHashCode(proxy);
            } else if (method.getName().equals("close")) {
                // Handle close method: suppress, not valid.
                return null;
            }

            // Invoke method on target Session.
            try {
                Object retVal = method.invoke(this.target, args);

                return retVal;
            } catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
        }
    }
}
