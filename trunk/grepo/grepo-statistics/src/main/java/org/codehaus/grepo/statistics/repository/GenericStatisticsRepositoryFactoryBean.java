/*
 * Copyright 2010 Grepo Committers.
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

package org.codehaus.grepo.statistics.repository;

import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.grepo.core.repository.GenericRepositoryFactoryBean;
import org.codehaus.grepo.statistics.service.StatisticsEntryIdentifierGenerationStrategy;
import org.codehaus.grepo.statistics.service.StatisticsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dguggi
 * @param <T> The target class (base) type.
 */
public abstract class GenericStatisticsRepositoryFactoryBean<T extends GenericStatisticsRepositorySupport, //
    C extends GrepoStatisticsConfiguration> extends GenericRepositoryFactoryBean<T, C> {

    private static final Logger logger = LoggerFactory.getLogger(GenericStatisticsRepositoryFactoryBean.class);

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doInitialization() {
        super.doInitialization();
        initStatisticsManager();
        initStatisticsEntryIdentifierGenerationStrategy();
    }

    protected void initStatisticsManager() {
        if (isAutoDetectBeans() && tryToInitStatisticsManager()) {
            Map<String, StatisticsManager> beans = getApplicationContext().getBeansOfType(StatisticsManager.class);
            if (beans.isEmpty()) {
                logger.warn(AUTODETECT_MSG_UNABLE_NOTFOUND, StatisticsManager.class.getName());
            } else if (beans.size() > 1) {
                logger.warn(AUTODETECT_MSG_UNABLE_TOOMANYFOUND, StatisticsManager.class.getName(), beans.keySet());
            } else {
                Entry<String, StatisticsManager> entry = beans.entrySet().iterator().next();
                getConfiguration().setStatisticsManager(entry.getValue());
                logger.debug(AUTODETECT_MSG_SUCCESS, StatisticsManager.class.getName(), entry.getKey());
            }
        }
    }

    protected void initStatisticsEntryIdentifierGenerationStrategy() {
        if (isAutoDetectBeans() && tryToInitStatisticsEntryIdentifierGenerationStrategy()) {
            Map<String, StatisticsEntryIdentifierGenerationStrategy> beans =
                getApplicationContext().getBeansOfType(StatisticsEntryIdentifierGenerationStrategy.class);

            if (beans.isEmpty()) {
                logger.warn(AUTODETECT_MSG_UNABLE_NOTFOUND, //
                    StatisticsEntryIdentifierGenerationStrategy.class.getName());
            } else if (beans.size() > 1) {
                logger.warn(AUTODETECT_MSG_UNABLE_TOOMANYFOUND, StatisticsEntryIdentifierGenerationStrategy.class
                    .getName(), beans.keySet());
            } else {
                Entry<String, StatisticsEntryIdentifierGenerationStrategy> entry = beans.entrySet().iterator().next();
                getConfiguration().setStatisticsEntryIdentifierGenerationStrategy(entry.getValue());
                logger.debug(AUTODETECT_MSG_SUCCESS, StatisticsEntryIdentifierGenerationStrategy.class.getName(), entry
                    .getKey());
            }
        }
    }

    /**
     * @return Returns {@code true} if the {@link StatisticsManager} should be initialized. This is the case when the
     *         user has enabled statistics {@code AND} no {@link StatisticsManager} was specified.
     */
    private boolean tryToInitStatisticsManager() {
        return getConfiguration() != null && getConfiguration().isStatisticsEnabled()
            && getConfiguration().getStatisticsManager() == null;
    }

    /**
     * @return Returns {@code true} if the {@link StatisticsEntryIdentifierGenerationStrategy} should be initialized.
     *         This is the case when the user has enabled statistics {@code AND} no
     *         {@link StatisticsEntryIdentifierGenerationStrategy} was specified.
     */
    private boolean tryToInitStatisticsEntryIdentifierGenerationStrategy() {
        return getConfiguration() != null && getConfiguration().isStatisticsEnabled()
            && getConfiguration().getStatisticsEntryIdentifierGenerationStrategy() == null;
    }

}
