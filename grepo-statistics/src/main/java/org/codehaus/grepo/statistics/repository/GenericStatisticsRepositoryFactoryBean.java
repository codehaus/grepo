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
import org.springframework.util.Assert;

/**
 * @author dguggi
 * @param <T> The target class (base) type.
 */
public abstract class GenericStatisticsRepositoryFactoryBean<T extends GenericStatisticsRepositorySupport>
                    extends GenericRepositoryFactoryBean<T> {

    /** The logger for this class. */
    private final Logger logger = LoggerFactory.getLogger(GenericStatisticsRepositoryFactoryBean.class); // NOPMD

    /** The optional statistics enabled flag (default is false). */
    private boolean statisticsEnabled = false; // NOPMD

    /** The statistics manager (required if statisticsEnabled is true). */
    private StatisticsManager statisticsManager; // NOPMD

    /** The statistics entry identifier generation strategy (required if statisticsEnables is true). */
    private StatisticsEntryIdentifierGenerationStrategy statisticsEntryIdentifierGenerationStrategy; // NOPMD

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doInitialization() {
        super.doInitialization();
        initStatisticsManager();
        initStatisticsEntryIdentifierGenerationStrategy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configureTarget(T target) {
        super.configureTarget(target);
        if (statisticsEnabled) {
            target.setStatisticsEnabled(statisticsEnabled);
        }
        if (statisticsEntryIdentifierGenerationStrategy != null) {
            target.setStatisticsEntryIdentifierGenerationStrategy(statisticsEntryIdentifierGenerationStrategy);
        }
        if (statisticsManager != null) {
            target.setStatisticsManager(statisticsManager);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validate() {
        super.validate();
        if (statisticsEnabled) {
            Assert.notNull(statisticsManager, "statisticsManager must not be null if statistics is enabled");
            Assert.notNull(statisticsEntryIdentifierGenerationStrategy, //
                "statisticsEntryIdentifierGenerationStrategy must not be null if statistics is enabled");
        }
    }

    /**
     * If the {@link #statisticsManager} is not set and {@code isAutoDetectBeans()} returns {@code true},
     * this method tries to retrieve the {@link #statisticsManager} automatically.
     */
    protected void initStatisticsManager() {
        if (statisticsManager == null && isAutoDetectBeans()) {
            Map<String, StatisticsManager> beans = getApplicationContext().getBeansOfType(StatisticsManager.class);

            if (beans.isEmpty()) {
                if (statisticsEnabled) {
                    logger.warn(AUTODETECT_MSG_UNABLE_NOTFOUND, StatisticsManager.class.getName());
                } else {
                    logger.debug(AUTODETECT_MSG_UNABLE_NOTFOUND, StatisticsManager.class.getName());
                }
            } else if (beans.size() > 1) {
                if (statisticsEnabled) {
                    logger.warn(AUTODETECT_MSG_UNABLE_TOOMANYFOUND, StatisticsManager.class.getName(),
                        beans.keySet());
                } else {
                    logger.debug(AUTODETECT_MSG_UNABLE_TOOMANYFOUND, StatisticsManager.class.getName(),
                        beans.keySet());
                }
            } else {
                // we found exactly one bean...
                Entry<String, StatisticsManager> entry = beans.entrySet().iterator().next();
                statisticsManager = entry.getValue();
                logger.debug(AUTODETECT_MSG_SUCCESS, StatisticsManager.class.getName(), entry.getKey());
            }
        }
    }

    /**
     * If the {@link #statisticsEntryIdentifierGenerationStrategy} is not set and {@code isAutoDetectBeans()}
     * returns {@code true}, this method tries to retrieve the {@link #statisticsEntryIdentifierGenerationStrategy}
     * automatically.
     */
    protected void initStatisticsEntryIdentifierGenerationStrategy() {
        if (statisticsEntryIdentifierGenerationStrategy == null && isAutoDetectBeans()) {
            Map<String, StatisticsEntryIdentifierGenerationStrategy> beans =
                    getApplicationContext().getBeansOfType(StatisticsEntryIdentifierGenerationStrategy.class);

            if (beans.isEmpty()) {
                if (statisticsEnabled) {
                    logger.warn(AUTODETECT_MSG_UNABLE_NOTFOUND,
                                    StatisticsEntryIdentifierGenerationStrategy.class.getName());
                } else {
                    logger.debug(AUTODETECT_MSG_UNABLE_NOTFOUND,
                                    StatisticsEntryIdentifierGenerationStrategy.class.getName());
                }
            } else if (beans.size() > 1) {
                if (statisticsEnabled) {
                    logger.warn(AUTODETECT_MSG_UNABLE_TOOMANYFOUND,
                                StatisticsEntryIdentifierGenerationStrategy.class.getName(), beans.keySet());
                } else {
                    logger.debug(AUTODETECT_MSG_UNABLE_TOOMANYFOUND,
                                StatisticsEntryIdentifierGenerationStrategy.class.getName(), beans.keySet());
                }
            } else {
                // we found exactly one bean...
                Entry<String, StatisticsEntryIdentifierGenerationStrategy> entry = beans.entrySet().iterator().next();
                statisticsEntryIdentifierGenerationStrategy = entry.getValue();
                logger.debug(AUTODETECT_MSG_SUCCESS, StatisticsEntryIdentifierGenerationStrategy.class.getName(),
                                entry.getKey());
            }
        }
    }


    public void setStatisticsEnabled(boolean statisticsEnabled) {
        this.statisticsEnabled = statisticsEnabled;
    }

    public void setStatisticsManager(StatisticsManager statisticsManager) {
        this.statisticsManager = statisticsManager;
    }

    public void setStatisticsEntryIdentifierGenerationStrategy(
            StatisticsEntryIdentifierGenerationStrategy statisticsEntryIdentifierGenerationStrategy) {
        this.statisticsEntryIdentifierGenerationStrategy = statisticsEntryIdentifierGenerationStrategy;
    }

}
