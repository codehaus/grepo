/*
 * Copyright 2011 Grepo Committers.
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

import org.codehaus.grepo.core.repository.GrepoConfiguration;
import org.codehaus.grepo.statistics.service.StatisticsEntryIdentifierGenerationStrategy;
import org.codehaus.grepo.statistics.service.StatisticsManager;
import org.springframework.util.Assert;


public class GrepoStatisticsConfiguration extends GrepoConfiguration {

    private boolean statisticsEnabled = false;
    private StatisticsManager statisticsManager;
    private StatisticsEntryIdentifierGenerationStrategy statisticsEntryIdentifierGenerationStrategy;

    public boolean isStatisticsEnabled() {
        return statisticsEnabled;
    }

    public void setStatisticsEnabled(boolean statisticsEnabled) {
        this.statisticsEnabled = statisticsEnabled;
    }

    public StatisticsManager getStatisticsManager() {
        return statisticsManager;
    }

    public void setStatisticsManager(StatisticsManager statisticsManager) {
        this.statisticsManager = statisticsManager;
    }

    public StatisticsEntryIdentifierGenerationStrategy getStatisticsEntryIdentifierGenerationStrategy() {
        return statisticsEntryIdentifierGenerationStrategy;
    }

    public void setStatisticsEntryIdentifierGenerationStrategy(
        StatisticsEntryIdentifierGenerationStrategy statisticsEntryIdentifierGenerationStrategy) {
        this.statisticsEntryIdentifierGenerationStrategy = statisticsEntryIdentifierGenerationStrategy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        super.validate();
        if (statisticsEnabled) {
            Assert.notNull(statisticsManager, "statisticsManager must not be null if statistics is enabled");
            Assert.notNull(statisticsEntryIdentifierGenerationStrategy, //
                "statisticsEntryIdentifierGenerationStrategy must not be null if statistics is enabled");
        }
    }

}
