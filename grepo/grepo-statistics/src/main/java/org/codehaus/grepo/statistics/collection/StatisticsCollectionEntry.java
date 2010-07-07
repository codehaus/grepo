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
     */
    void addStatisticsEntry(StatisticsEntry entry);

    /**
     * @param entry The entry to add.
     * @param maxEntries The max entries configuration.
     */
    void addStatisticsEntry(StatisticsEntry entry, Long maxEntries);

    /**
     * @param entries The entries to add.
     */
    void addStatisticsEntries(Collection<StatisticsEntry> entries);

    /**
     * @param entries The entries to add.
     * @param maxEntries The max entries configuration.
     */
    void addStatisticsEntries(Collection<StatisticsEntry> entries, Long maxEntries);

    /**
     * @return Returns a read-only list of {@link StatisticsEntry} objects.
     */
    List<StatisticsEntry> getStatisticsEntriesReadOnly();

    /**
     * @return Returns the list of {@link StatisticsEntry} objects.
     */
    List<StatisticsEntry> getStatisticsEntriesList();

    /**
     * @return Returns the number of invocations.
     */
    long getNumberOfInvocations();

    /**
     * @return Returns the max duration in millis.
     */
    long getMaxDurationMillis();

    /**
     * @return Returns the min duration in millis.
     */
    long getMinDurationMillis();
}
