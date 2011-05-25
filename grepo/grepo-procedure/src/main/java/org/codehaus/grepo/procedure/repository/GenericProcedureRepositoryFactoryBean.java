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

import org.codehaus.grepo.statistics.repository.GenericStatisticsRepositoryFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * Factory bean used to create generic procedure repository beans (aop proxies).
 *
 * @author dguggi
 */
public class GenericProcedureRepositoryFactoryBean extends
    GenericStatisticsRepositoryFactoryBean<GenericProcedureRepositorySupport, GrepoProcedureConfiguration> {

    private static final Logger logger = LoggerFactory.getLogger(GenericProcedureRepositoryFactoryBean.class);

    private DataSource dataSource;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doInitialization() {
        super.doInitialization();
        initDataSource();
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
     * {@inheritDoc}
     */
    @Override
    protected void validate() {
        super.validate();
        Assert.notNull(dataSource, "dataSource must not be null");
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
        target.setDataSource(dataSource);
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<GrepoProcedureConfiguration> getRequiredConfigurationType() {
        return GrepoProcedureConfiguration.class;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

}
