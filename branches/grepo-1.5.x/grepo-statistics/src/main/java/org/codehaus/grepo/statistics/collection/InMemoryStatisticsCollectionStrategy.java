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

package org.codehaus.grepo.statistics.collection;

import org.codehaus.grepo.statistics.domain.StatisticsEntry;
import org.springframework.beans.factory.annotation.Required;

/**
 * @author dguggi
 */
public class InMemoryStatisticsCollectionStrategy implements StatisticsCollectionStrategy {
    /** The statistics collection. */
    private StatisticsCollection statisticsCollection; // NOPMD

    /**
     * {@inheritDoc}
     */
    public void completeStatistics(StatisticsEntry entry) {
        statisticsCollection.addStatisticsEntry(entry);
    }

    /**
     * {@inheritDoc}
     */
    public void startStatistics(StatisticsEntry entry) {
        // do nothing
    }


    @Required
    public void setStatisticsCollection(StatisticsCollection statisticsCollection) {
        this.statisticsCollection = statisticsCollection;
    }

}
