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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.codehaus.grepo.statistics.domain.StatisticsEntry;

/**
 * @author dguggi
 */
public class StatisticsCollectionEntryImpl implements StatisticsCollectionEntry {

    /** SerialVersionUid. */
    private static final long serialVersionUID = 9122661088316356000L;

    /** The statistics entires. */
    private List<StatisticsEntry> statisticsEntries = new ArrayList<StatisticsEntry>(10);

    /** The number of invocations. */
    private long numberOfInvocations = 0L;

    /** The max duration in millis. */
    private Long maxDurationMillis;

    /** The min duration in millis. */
    private Long minDurationMillis;

    /**
     * {@inheritDoc}
     */
    public void addStatisticsEntry(StatisticsEntry entry) {
        addStatisticsEntry(entry, null);
    }

    /**
     * {@inheritDoc}
     */
    public void addStatisticsEntry(StatisticsEntry entry, Long maxEntries) {
        if (entry != null) {
            numberOfInvocations += 1;

            if (maxDurationMillis == null) {
                maxDurationMillis = entry.getDurationMillis();
            } else {
                maxDurationMillis = Math.max(maxDurationMillis, entry.getDurationMillis());
            }

            if (minDurationMillis == null) {
                minDurationMillis = entry.getDurationMillis();
            } else {
                minDurationMillis = Math.min(minDurationMillis, entry.getDurationMillis());
            }

            if (maxEntries != null && statisticsEntries.size() >= maxEntries) {
                statisticsEntries.remove(0);
            }
            statisticsEntries.add(entry);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addStatisticsEntries(Collection<StatisticsEntry> entries) {
        addStatisticsEntries(entries, null);
    }

    /**
     * {@inheritDoc}
     */
    public void addStatisticsEntries(Collection<StatisticsEntry> entries, Long maxEntries) {
        for (StatisticsEntry entry : entries) {
            addStatisticsEntry(entry, maxEntries);
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<StatisticsEntry> getStatisticsEntries() {
        return Collections.unmodifiableList(statisticsEntries);
    }

    protected void setStatisticsEntries(List<StatisticsEntry> statisticsEntries) {
        this.statisticsEntries = statisticsEntries;
    }

    /**
     * {@inheritDoc}
     */
    public long getNumberOfInvocations() {
        return numberOfInvocations;
    }

    /**
     * {@inheritDoc}
     */
    public long getMaxDurationMillis() {
        return maxDurationMillis;
    }

    /**
     * {@inheritDoc}
     */
    public long getMinDurationMillis() {
        return minDurationMillis;
    }

}
