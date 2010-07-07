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

import org.codehaus.grepo.statistics.collection.StatisticsCollection;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * @author dguggi
 */
@ManagedResource("Statistics controller")
public class StatisticsController {

    /** The statistics manager. */
    private StatisticsManager statisticsManager;

    /** The collection. */
    private StatisticsCollection statisticsCollection;

    /**
     * @param statisticsEnabled The flag to set.
     */
    @ManagedAttribute(description = "Enables/Disables statistics")
    public void setStatisticsEnabled(boolean statisticsEnabled) {
        statisticsManager.setEnabled(statisticsEnabled);
    }

    /**
     * @return Returns the flag.
     */
    @ManagedAttribute
    public boolean getStatisticsEnabled() {
        return statisticsManager.isEnabled();
    }

    /**
     * @param maxStatisticsEntries The number to set.
     */
    @ManagedAttribute(description = "Sets the max number of statistics entries")
    public void setMaxStatisticsEntries(Long maxStatisticsEntries) {
        statisticsCollection.setMaxStatisticsEntries(maxStatisticsEntries);
    }

    /**
     * @return Returns the max number of statistics entries.
     */
    @ManagedAttribute
    public Long getMaxStatisticsEntries() {
        return statisticsCollection.getMaxStatisticsEntries();
    }

    /**
     * Clears statistics.
     */
    @ManagedOperation(description = "Clears statistics")
    public void clearStatistics() {
        statisticsCollection.clear();
    }

    public void setStatisticsManager(StatisticsManager statisticsManager) {
        this.statisticsManager = statisticsManager;
    }

    public void setStatisticsCollection(StatisticsCollection statisticsCollection) {
        this.statisticsCollection = statisticsCollection;
    }

}
