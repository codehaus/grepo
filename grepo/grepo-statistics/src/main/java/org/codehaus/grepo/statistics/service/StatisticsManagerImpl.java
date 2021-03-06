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

package org.codehaus.grepo.statistics.service;

import java.util.Calendar;

import org.apache.commons.lang.StringUtils;
import org.codehaus.grepo.statistics.collection.StatisticsCollectionStrategy;
import org.codehaus.grepo.statistics.domain.DurationAwareStatisticsEntry;
import org.codehaus.grepo.statistics.domain.StatisticsEntry;
import org.codehaus.grepo.statistics.domain.StatisticsEntryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * @author dguggi
 */
public class StatisticsManagerImpl implements StatisticsManager {

    private static final Logger logger = LoggerFactory.getLogger(StatisticsManagerImpl.class);

    private StatisticsEntryFactory statisticsEntryFactory;
    private StatisticsCollectionStrategy statisticsCollectionStrategy;
    private boolean enabled = true;

    /**
     * {@inheritDoc}
     */
    public StatisticsEntry createStatisticsEntry(String identifier) {
        return createStatisticsEntry(identifier, null);
    }

    /**
     * {@inheritDoc}
     */
    public StatisticsEntry createStatisticsEntry(String identifier, String origin) {
        StatisticsEntry entry = null;
        try {
            if (isEnabled() && StringUtils.isNotEmpty(identifier)) {
                entry = statisticsEntryFactory.createStatisticsEntry(identifier, Calendar.getInstance(), origin);

                if (statisticsCollectionStrategy != null) {
                    statisticsCollectionStrategy.startStatistics(entry);
                }
            }
        } catch (Exception e) {
            logger.warn("Unable to create StatisticsEntry: " + e.getMessage());
            if (logger.isDebugEnabled()) {
                logger.debug("Got unexpected exception: " + e.getMessage(), e);
            }
        }
        return entry;
    }

    /**
     * {@inheritDoc}
     */
    public void completeStatisticsEntry(StatisticsEntry entry) {
        try {
            if (isEnabled()) {
                if (entry instanceof DurationAwareStatisticsEntry) {
                    DurationAwareStatisticsEntry daEntry = (DurationAwareStatisticsEntry)entry;
                    daEntry.setCompletion(Calendar.getInstance());
                    calculateDurationMillis(daEntry);
                }

                if (statisticsCollectionStrategy != null) {
                    statisticsCollectionStrategy.completeStatistics(entry);
                }
            }
        } catch (Exception e) {
            logger.warn("Unable to complete StatisticsEntry: " + e.getMessage());
            if (logger.isDebugEnabled()) {
                logger.debug("Got unexpected exception: " + e.getMessage(), e);
            }
        }
    }

    /**
     * @param daEntry The entry.
     */
    protected void calculateDurationMillis(DurationAwareStatisticsEntry daEntry) {
        if (daEntry.hasCreation() && daEntry.hasCompletion()) {
            daEntry.setDurationMillis(daEntry.getCompletionMillis() - daEntry.getCreationMillis());
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * {@inheritDoc}
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setStatisticsCollectionStrategy(StatisticsCollectionStrategy statisticsCollectionStrategy) {
        this.statisticsCollectionStrategy = statisticsCollectionStrategy;
    }

    @Required
    public void setStatisticsEntryFactory(StatisticsEntryFactory statisticsEntryFactory) {
        this.statisticsEntryFactory = statisticsEntryFactory;
    }

}
