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

import org.codehaus.grepo.core.repository.GenericRepositorySupport;
import org.codehaus.grepo.statistics.aop.StatisticsMethodParameterInfo;
import org.codehaus.grepo.statistics.domain.StatisticsEntry;
import org.codehaus.grepo.statistics.service.StatisticsEntryIdentifierGenerationStrategy;
import org.codehaus.grepo.statistics.service.StatisticsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dguggi
 */
public class GenericStatisticsRepositorySupport extends GenericRepositorySupport {

    /** The logger for this class. */
    private final Logger logger = LoggerFactory.getLogger(GenericStatisticsRepositorySupport.class);

    /** The statistics enabled flag (default is false). */
    private boolean statisticsEnabled = false;

    /** The statistics manager. */
    private StatisticsManager statisticsManager;

    /** The statistics entry identifier generation strategy. */
    private StatisticsEntryIdentifierGenerationStrategy statisticsEntryIdentifierGenerationStrategy;

    /**
     * @param mpi Procedure method parameter info.
     * @return Returns the statistics etnry.
     */
    protected StatisticsEntry createStatisticsEntry(StatisticsMethodParameterInfo mpi) {
        StatisticsEntry entry = null;
        try {
            if (statisticsEnabled) {
                if (statisticsManager == null) {
                    logger.warn("Unable to collect statistics, because statisticsManager is null");
                } else {
                    if (statisticsEntryIdentifierGenerationStrategy == null) {
                        logger.warn("Unable to collect statistics, because "
                            + "statisticsEntryIdentifierGenerationStrategy is null");
                    } else {
                        String identifier = statisticsEntryIdentifierGenerationStrategy.getIdentifier(mpi);
                        entry = statisticsManager.createStatisticsEntry(identifier);
                        mpi.setStatisticsEntry(entry);
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Unable to complete StatisticsEntry: " + e.getMessage(), e);
        }
        return entry;
    }

    /**
     * @param identifier The identifier.
     * @return Returns the statistics entry.
     */
    protected StatisticsEntry createStatisticsEntry(String identifier) {
        StatisticsEntry entry = null;
        try {
            if (statisticsEnabled) {
                if (statisticsManager == null) {
                    logger.warn("Unable to collect statistics, because statisticsManager is null");
                } else {
                    entry = statisticsManager.createStatisticsEntry(identifier);
                }
            }
        } catch (Exception e) {
            logger.warn("Unable to complete StatisticsEntry: " + e.getMessage(), e);
        }
        return entry;
    }


    /**
     * @param entry The statistics entry.
     */
    protected void completeStatisticsEntry(StatisticsEntry entry) {
        try {
            if (statisticsEnabled && entry != null) {
                if (statisticsManager == null) {
                    logger.warn("Unable to collect statistics, because statisticsManager is null");
                } else {
                    statisticsManager.completeStatisticsEntry(entry);
                }
            }
        } catch (Exception e) {
            logger.warn("Unable to complete StatisticsEntry: " + e.getMessage(), e);
        }
    }

    protected boolean isStatisticsEnabled() {
        return statisticsEnabled;
    }

    public void setStatisticsEnabled(boolean statisticsEnabled) {
        this.statisticsEnabled = statisticsEnabled;
    }

    protected StatisticsManager getStatisticsManager() {
        return statisticsManager;
    }

    public void setStatisticsManager(StatisticsManager statisticsManager) {
        this.statisticsManager = statisticsManager;
    }

    protected StatisticsEntryIdentifierGenerationStrategy getStatisticsEntryIdentifierGenerationStrategy() {
        return statisticsEntryIdentifierGenerationStrategy;
    }

    public void setStatisticsEntryIdentifierGenerationStrategy(
            StatisticsEntryIdentifierGenerationStrategy statisticsEntryIdentifierGenerationStrategy) {
        this.statisticsEntryIdentifierGenerationStrategy = statisticsEntryIdentifierGenerationStrategy;
    }

}
