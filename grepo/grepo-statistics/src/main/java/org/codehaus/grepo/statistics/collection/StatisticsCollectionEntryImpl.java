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

    /** Holds recent statistics entires. */
    private List<StatisticsEntry> recentStatisticsEntries = new ArrayList<StatisticsEntry>(10);

    /** Holds statistic entries with maximum duration. */
    private List<StatisticsEntry> topDurationStatisticsEntries = new ArrayList<StatisticsEntry>(10);

    /** The number of invocations. */
    private long numberOfInvocations = 0L;

    /** The max duration statistics entry. */
    private StatisticsEntry maxDurationStatisticsEntry;

    /** The min duration statistics entry. */
    private StatisticsEntry minDurationStatisticsEntry;

    /**
     * {@inheritDoc}
     */
    public void addStatisticsEntry(StatisticsEntry entry, Long maxNumberOfRecentStatsticsEntries,
            Long maxNumberOfTopDurationStatisticsEntries) {
        if (entry != null) {
            numberOfInvocations += 1;

            if (entry.getDurationMillis() != null) {
                // set top max duration statistics entry...
                if (maxDurationStatisticsEntry == null) {
                    maxDurationStatisticsEntry = entry;
                } else {
                    if (maxDurationStatisticsEntry.getDurationMillis() < entry.getDurationMillis()) {
                        maxDurationStatisticsEntry = entry;
                    }
                }

                // set top min duration statistics entry...
                if (minDurationStatisticsEntry == null) {
                    minDurationStatisticsEntry = entry;
                } else {
                    if (minDurationStatisticsEntry.getDurationMillis() > entry.getDurationMillis()) {
                        minDurationStatisticsEntry = entry;
                    }
                }

                // handle top duration statistics...
                if (maxNumberOfTopDurationStatisticsEntries == null) {
                    topDurationStatisticsEntries.add(entry);
                } else {
                    if (maxNumberOfTopDurationStatisticsEntries > topDurationStatisticsEntries.size()) {
                        topDurationStatisticsEntries.add(entry);
                    } else {
                        StatisticsEntry min = StatisticsCollectionUtils
                                                    .getMinDurationEntry(topDurationStatisticsEntries);
                        if (min != null && min.getDurationMillis() < entry.getDurationMillis()) {
                            topDurationStatisticsEntries.remove(min);
                            topDurationStatisticsEntries.add(entry);
                        }
                    }
                }
            }

            // handle recent statistics...
            if (maxNumberOfRecentStatsticsEntries == null) {
                recentStatisticsEntries.add(entry);
            } else {
                if (maxNumberOfRecentStatsticsEntries > recentStatisticsEntries.size()) {
                    recentStatisticsEntries.add(entry);
                } else {
                    recentStatisticsEntries.remove(0);
                    recentStatisticsEntries.add(entry);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addStatisticsEntries(Collection<StatisticsEntry> entries, Long maxNumberOfRecentStatsticsEntries,
            Long maxNumberOfTopDurationStatisticsEntries) {
        for (StatisticsEntry entry : entries) {
            addStatisticsEntry(entry, maxNumberOfRecentStatsticsEntries, maxNumberOfTopDurationStatisticsEntries);
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<StatisticsEntry> getRecentStatisticsEntriesReadOnly() {
        return Collections.unmodifiableList(recentStatisticsEntries);
    }

    /**
     * {@inheritDoc}
     */
    public List<StatisticsEntry> getRecentStatisticsEntriesList() {
        return new ArrayList<StatisticsEntry>(recentStatisticsEntries);
    }

    protected List<StatisticsEntry> getRecentStatisticsEntries() {
        return recentStatisticsEntries;
    }

    protected void setRecentStatisticsEntries(List<StatisticsEntry> recentStatisticsEntries) {
        this.recentStatisticsEntries = recentStatisticsEntries;
    }

    /**
     * {@inheritDoc}
     */
    public List<StatisticsEntry> getTopDurationStatisticsEntriesReadOnly() {
        return Collections.unmodifiableList(topDurationStatisticsEntries);
    }

    /**
     * {@inheritDoc}
     */
    public List<StatisticsEntry> getTopDurationStatisticsEntriesList() {
        return new ArrayList<StatisticsEntry>(topDurationStatisticsEntries);
    }

    protected List<StatisticsEntry> getTopDurationStatisticsEntries() {
        return topDurationStatisticsEntries;
    }

    protected void setTopDurationStatisticsEntries(List<StatisticsEntry> topDurationStatisticsEntries) {
        this.topDurationStatisticsEntries = topDurationStatisticsEntries;
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
    public StatisticsEntry getMaxDurationStatisticsEntry() {
        return maxDurationStatisticsEntry;
    }

    /**
     * {@inheritDoc}
     */
    public StatisticsEntry getMinDurationStatisticsEntry() {
        return minDurationStatisticsEntry;
    }

}
