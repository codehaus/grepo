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

/**
 * @author dguggi
 */
public interface StatisticsController {

    /**
     * @param statisticsEnabled The flag to set.
     */
    void setStatisticsEnabled(boolean statisticsEnabled);

    /**
     * @return Returns the statistics enabled flag.
     */
    boolean getStatisticsEnabled();

    /**
     * @param maxStatisticsEntries The max entries to set.
     */
    void setMaxStatisticsEntries(Long maxStatisticsEntries);

    /**
     * @return Returns the max entries.
     */
    Long getMaxStatisticsEntries();

    /**
     * Clears statistics.
     */
    void clearStatistics();
}