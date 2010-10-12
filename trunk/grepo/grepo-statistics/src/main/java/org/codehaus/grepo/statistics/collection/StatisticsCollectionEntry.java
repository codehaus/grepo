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

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.codehaus.grepo.statistics.domain.StatisticsEntry;

/**
 * @author dguggi
 */
public interface StatisticsCollectionEntry extends Serializable {
    /**
     * @param entry The entry to add.
     * @param maxNumberOfRecentStatisticsEntries The number.
     * @param maxNumberOfTopDurationStatisticsEntries The number.
     */
    void addStatisticsEntry(StatisticsEntry entry, Integer maxNumberOfRecentStatisticsEntries,
            Integer maxNumberOfTopDurationStatisticsEntries);

    /**
     * @param entries The entries to add.
     * @param maxNumberOfRecentStatisticsEntries The number.
     * @param maxNumberOfTopDurationStatisticsEntries The number.
     */
    void addStatisticsEntries(Collection<StatisticsEntry> entries, Integer maxNumberOfRecentStatisticsEntries,
            Integer maxNumberOfTopDurationStatisticsEntries);

    /**
     * @return Returns a read-only list of recent {@link StatisticsEntry} objects.
     */
    List<StatisticsEntry> getRecentStatisticsEntriesReadOnly();

    /**
     * @return Returns a list of recent {@link StatisticsEntry} objects.
     */
    List<StatisticsEntry> getRecentStatisticsEntriesList();

    /**
     * @return Returns a read-only list of top duration {@link StatisticsEntry} objects.
     */
    List<StatisticsEntry> getTopDurationStatisticsEntriesReadOnly();

    /**
     * @return Returns the list of top duration {@link StatisticsEntry} objects.
     */
    List<StatisticsEntry> getTopDurationStatisticsEntriesList();

    /**
     * @return Returns the number of invocations.
     */
    long getNumberOfInvocations();

    /**
     * @return Returns the top max duration statistics entry.
     */
    StatisticsEntry getMaxDurationStatisticsEntry();

    /**
     * @return Returns the top min duration statistics entry.
     */
    StatisticsEntry getMinDurationStatisticsEntry();
}
