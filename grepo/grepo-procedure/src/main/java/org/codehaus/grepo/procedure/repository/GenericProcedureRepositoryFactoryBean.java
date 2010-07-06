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
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.codehaus.grepo.procedure.cache.ProcedureCachingStrategy;
import org.codehaus.grepo.procedure.compile.ProcedureCompilationStrategy;
import org.codehaus.grepo.procedure.input.ProcedureInputGenerationStrategy;
import org.codehaus.grepo.statistics.repository.GenericStatisticsRepositoryFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * Factory bean used to create generic procedure repository beans (aop proxies).
 *
 * @author dguggi
 */
public class GenericProcedureRepositoryFactoryBean //
        extends GenericStatisticsRepositoryFactoryBean<GenericProcedureRepositorySupport> {

    /** The logger for this class. */
    private final Logger logger = LoggerFactory.getLogger(GenericProcedureRepositoryFactoryBean.class);

    /** The mandatory datasource. */
    private DataSource dataSource;

    /** The mandatory procedure caching strategy (may be auto-detected). */
    private ProcedureCachingStrategy procedureCachingStrategy;

    /** The mandatory procedure compilation strategy (may be auto-detected). */
    private ProcedureCompilationStrategy procedureCompilationStrategy;

    /** The the mandatory procedure input generation strategy (may be auto-detected). */
    private ProcedureInputGenerationStrategy procedureInputGenerationStrategy;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doInitialization() {
        super.doInitialization();
        initProcedureInputGenerationStrategy();
        initProcedureCachingStrategy();
        initProcedureCompilationStrategy();
        initDataSource();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initMethodInterceptor() {
        if (getMethodInterceptor() == null && isAutoDetectBeans()) {
            Map<String, GenericProcedureMethodInterceptor> beans = getApplicationContext().getBeansOfType(
                GenericProcedureMethodInterceptor.class);

            if (beans.isEmpty()) {
                logger.warn(AUTODETECT_MSG_UNABLE_NOTFOUND, GenericProcedureMethodInterceptor.class.getName());
            } else if (beans.size() > 1) {
                logger.warn(AUTODETECT_MSG_UNABLE_TOOMANYFOUND, GenericProcedureMethodInterceptor.class.getName(),
                    beans.keySet());
            } else {
                // we found excatly one bean...
                Entry<String, GenericProcedureMethodInterceptor> entry = beans.entrySet().iterator().next();
                setMethodInterceptor(entry.getValue());
                logger.debug(AUTODETECT_MSG_SUCCESS, GenericProcedureMethodInterceptor.class.getName(), entry.getKey());
            }
        }
    }

    /**
     * If the {@link #dataSource} is not set and {@code isAutoDetectBeans()} returns {@code true}, this method tries to
     * retrieve the {@link #dataSource} automatically.
     */
    protected void initDataSource() {
        if (dataSource == null && isAutoDetectBeans()) {
            Map<String, DataSource> beans = getApplicationContext().getBeansOfType(DataSource.class);

            if (beans.isEmpty()) {
                logger.warn(AUTODETECT_MSG_UNABLE_NOTFOUND, DataSource.class.getName());
            } else if (beans.size() > 1) {
                logger.warn(AUTODETECT_MSG_UNABLE_TOOMANYFOUND, DataSource.class.getName(), beans.keySet());
            } else {
                // we found excatly one bean...
                Entry<String, DataSource> entry = beans.entrySet().iterator().next();
                dataSource = entry.getValue();
                logger.debug(AUTODETECT_MSG_SUCCESS, DataSource.class.getName(), entry.getKey());
            }
        }
    }

    /**
     * If the {@link #procedureInputGenerationStrategy} is not set and {@code isAutoDetectBeans} returns {@code true},
     * this method tries to retrieve the {@link #procedureInputGenerationStrategy} automatically.
     */
    protected void initProcedureInputGenerationStrategy() {
        if (procedureInputGenerationStrategy == null && isAutoDetectBeans()) {
            Map<String, ProcedureInputGenerationStrategy> beans = getApplicationContext().getBeansOfType(
                ProcedureInputGenerationStrategy.class);

            if (beans.isEmpty()) {
                logger.warn(AUTODETECT_MSG_UNABLE_NOTFOUND, ProcedureInputGenerationStrategy.class.getName());
            } else if (beans.size() > 1) {
                logger.warn(AUTODETECT_MSG_UNABLE_TOOMANYFOUND, ProcedureInputGenerationStrategy.class.getName(),
                    beans.keySet());
            } else {
                // we found excatly one bean...
                Entry<String, ProcedureInputGenerationStrategy> entry = beans.entrySet().iterator().next();
                procedureInputGenerationStrategy = entry.getValue();
                logger.debug(AUTODETECT_MSG_SUCCESS, ProcedureInputGenerationStrategy.class.getName(), entry.getKey());
            }
        }
    }

    /**
     * If the {@link #procedureCachingStrategy} is not set and {@code isAutoDetectBeans} returns {@code true}, this
     * method tries to retrieve the {@link #procedureCachingStrategy} automatically.
     */
    protected void initProcedureCachingStrategy() {
        if (procedureCachingStrategy == null && isAutoDetectBeans()) {
            Map<String, ProcedureCachingStrategy> beans = getApplicationContext().getBeansOfType(
                ProcedureCachingStrategy.class);

            if (beans.isEmpty()) {
                logger.warn(AUTODETECT_MSG_UNABLE_NOTFOUND, ProcedureCachingStrategy.class.getName());
            } else if (beans.size() > 1) {
                logger.warn(AUTODETECT_MSG_UNABLE_TOOMANYFOUND, ProcedureCachingStrategy.class.getName(),
                    beans.keySet());
            } else {
                // we found excatly one bean...
                Entry<String, ProcedureCachingStrategy> entry = beans.entrySet().iterator().next();
                procedureCachingStrategy = entry.getValue();
                logger.debug(AUTODETECT_MSG_SUCCESS, ProcedureCachingStrategy.class.getName(), entry.getKey());
            }
        }
    }

    /**
     * If the {@link #procedureCompilationStrategy} is not set and {@code isAutoDetectBeans} returns {@code true}, this
     * method tries to retrieve the {@link #procedureCompilationStrategy} automatically.
     */
    protected void initProcedureCompilationStrategy() {
        if (procedureCompilationStrategy == null && isAutoDetectBeans()) {
            Map<String, ProcedureCompilationStrategy> beans = getApplicationContext().getBeansOfType(
                ProcedureCompilationStrategy.class);

            if (beans.isEmpty()) {
                logger.warn(AUTODETECT_MSG_UNABLE_NOTFOUND, ProcedureCompilationStrategy.class .getName());
            } else if (beans.size() > 1) {
                logger.warn(AUTODETECT_MSG_UNABLE_TOOMANYFOUND, ProcedureCompilationStrategy.class.getName(),
                    beans.keySet());
            } else {
                // we found excatly one bean...
                Entry<String, ProcedureCompilationStrategy> entry = beans.entrySet().iterator().next();
                procedureCompilationStrategy = entry.getValue();
                logger.debug(AUTODETECT_MSG_SUCCESS, ProcedureCompilationStrategy.class.getName(), entry.getKey());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validate() {
        super.validate();
        validateProxyInterface();
        validateTargetClass();

        Assert.notNull(dataSource, "dataSource must not be null");
        Assert.notNull(procedureInputGenerationStrategy, "procedureInputGenerationStrategy must not be null");
        Assert.notNull(procedureCompilationStrategy, "procedureCompilationStrategy must not be null");
        Assert.notNull(procedureCachingStrategy, "procedureCachingStrategy must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateTargetClass() {
        super.validateTargetClass();
        Assert.isAssignable(GenericProcedureRepositorySupport.class, getTargetClass());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configureTarget(GenericProcedureRepositorySupport target) {
        super.configureTarget(target);
        // set mandatory properties...
        target.setDataSource(dataSource);
        target.setProcedureInputGenerationStrategy(procedureInputGenerationStrategy);
        target.setProcedureCachingStrategy(procedureCachingStrategy);
        target.setProcedureCompilationStrategy(procedureCompilationStrategy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<?> getDefaultTargetClass() {
        return GenericProcedureRepositoryImpl.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<?> getRequiredGenericRepositoryType() {
        return GenericProcedureRepository.class;
    }

    public ProcedureCachingStrategy getProcedureCachingStrategy() {
        return procedureCachingStrategy;
    }

    public void setProcedureCachingStrategy(ProcedureCachingStrategy procedureCachingStrategy) {
        this.procedureCachingStrategy = procedureCachingStrategy;
    }

    public ProcedureCompilationStrategy getProcedureCompilationStrategy() {
        return procedureCompilationStrategy;
    }

    public void setProcedureCompilationStrategy(ProcedureCompilationStrategy procedureCompilationStrategy) {
        this.procedureCompilationStrategy = procedureCompilationStrategy;
    }

    public ProcedureInputGenerationStrategy getProcedureInputGenerationStrategy() {
        return procedureInputGenerationStrategy;
    }

    public void setProcedureInputGenerationStrategy(ProcedureInputGenerationStrategy procedureInputGenerationStrategy) {
        this.procedureInputGenerationStrategy = procedureInputGenerationStrategy;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

}
