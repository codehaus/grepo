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

import org.codehaus.grepo.statistics.collection.StatisticsCollectionStrategy;
import org.codehaus.grepo.statistics.domain.StatisticsEntry;
import org.codehaus.grepo.statistics.domain.StatisticsEntryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

/**
 * @author dguggi
 */
public class StatisticsManagerImpl implements StatisticsManager {

    /** The logger for this class. */
    private final Logger logger = LoggerFactory.getLogger(StatisticsManagerImpl.class);

    /** The statistics entry factory. */
    private StatisticsEntryFactory statisticsEntryFactory;

    /** The collection strategy (optional). */
    private StatisticsCollectionStrategy statisticsCollectionStrategy;

    /** The enabled flag. */
    private boolean enabled = true;

    /**
     * {@inheritDoc}
     */
    public void completeStatisticsEntry(StatisticsEntry entry) {
        try {
            if (isEnabled()) {
                entry.setCompletion(Calendar.getInstance());
                long duration = entry.getCompletionMillis() - entry.getCreationMillis();
                entry.setDurationMillis(duration);

                if (statisticsCollectionStrategy != null) {
                    statisticsCollectionStrategy.collectStatistics(entry);
                }
            }
        } catch (Exception e) {
            logger.warn("Unable to complete StatisticsEntry: " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    public StatisticsEntry createStatisticsEntry(String identifier) {
        StatisticsEntry entry = null;
        try {
            if (isEnabled()) {
                Assert.hasText(identifier, "identifier must not be empty");
                entry = statisticsEntryFactory.createStatisticsEntry(identifier, Calendar.getInstance());
            }
        } catch (Exception e) {
            logger.warn("Unable to create StatisticsEntry: " + e.getMessage());
        }
        return entry;
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