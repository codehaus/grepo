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
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * @author dguggi
 */
@ManagedResource("Statistics controller")
public class StatisticsControllerImpl implements StatisticsController {

    /** The statistics manager. */
    private StatisticsManager statisticsManager;

    /** The collection. */
    private StatisticsCollection statisticsCollection;

    /**
     * {@inheritDoc}
     */
    @ManagedAttribute(description = "Enables/Disables statistics")
    public void setStatisticsEnabled(boolean statisticsEnabled) {
        statisticsManager.setEnabled(statisticsEnabled);
    }

    /**
     * {@inheritDoc}
     */
    @ManagedAttribute
    public boolean getStatisticsEnabled() {
        return statisticsManager.isEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @ManagedAttribute(description = "Sets the max number of recent statistics entries")
    public void setMaxNumberOfRecentStatisticsEntries(Long maxNumberOfRecentStatsticsEntries) {
        statisticsCollection.setMaxNumberOfRecentStatisticsEntries(maxNumberOfRecentStatsticsEntries);
    }

    /**
     * {@inheritDoc}
     */
    @ManagedAttribute
    public Long getMaxNumberOfRecentStatisticsEntries() {
        return statisticsCollection.getMaxNumberOfRecentStatisticsEntries();
    }

    /**
     * {@inheritDoc}
     */
    @ManagedAttribute
    public Long getMaxNumberOfTopDurationStatisticsEntries() {
        return statisticsCollection.getMaxNumberOfTopDurationStatisticsEntries();
    }

    /**
     * {@inheritDoc}
     */
    @ManagedAttribute(description = "Sets the max number of top duration statistics entries")
    public void setMaxNumberOfTopDurationStatisticsEntries(Long maxNumberOfTopDurationStatisticsEntries) {
        statisticsCollection.setMaxNumberOfTopDurationStatisticsEntries(maxNumberOfTopDurationStatisticsEntries);
    }

    /**
     * {@inheritDoc}
     */
    @ManagedOperation(description = "Clears statistics")
    public void clearStatistics() {
        statisticsCollection.clear();
    }

    @Required
    public void setStatisticsManager(StatisticsManager statisticsManager) {
        this.statisticsManager = statisticsManager;
    }

    protected StatisticsManager getStatisticsManager() {
        return statisticsManager;
    }

    @Required
    public void setStatisticsCollection(StatisticsCollection statisticsCollection) {
        this.statisticsCollection = statisticsCollection;
    }

    protected StatisticsCollection getStatisticsCollection() {
        return statisticsCollection;
    }


}
