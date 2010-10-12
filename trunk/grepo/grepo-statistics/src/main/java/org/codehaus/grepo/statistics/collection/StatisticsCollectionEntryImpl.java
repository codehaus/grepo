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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dguggi
 */
public class StatisticsCollectionEntryImpl implements StatisticsCollectionEntry {

    /** SerialVersionUid. */
    private static final long serialVersionUID = 9122661088316356000L;

    /** The logger for this class. */
    private final Logger logger = LoggerFactory.getLogger(StatisticsCollectionEntryImpl.class); // NOPMD

    /** Holds recent statistics entires. */
    private List<StatisticsEntry> recentStatisticsEntries = new ArrayList<StatisticsEntry>(10);

    /** Holds statistic entries with maximum duration. */
    private List<StatisticsEntry> topDurationStatisticsEntries = new ArrayList<StatisticsEntry>(10);

    /** The number of invocations. */
    private long numberOfInvocations = 0L; // NOPMD

    /** The max duration statistics entry. */
    private StatisticsEntry maxDurationStatisticsEntry; // NOPMD

    /** The min duration statistics entry. */
    private StatisticsEntry minDurationStatisticsEntry; // NOPMD

    /**
     * {@inheritDoc}
     */
    public void addStatisticsEntry(StatisticsEntry entry, Integer maxNumberOfRecentStatisticsEntries,
            Integer maxNumberOfTopDurationStatisticsEntries) {
        incrementNrOfInvocations(entry);

        handleMaxDurationStatisticsEntry(entry);

        handleMinDurationStatisticsEntry(entry);

        handleTopDurationStatisticsEntries(entry, maxNumberOfTopDurationStatisticsEntries);

        handleRecentStatisticsEntries(entry, maxNumberOfRecentStatisticsEntries);
    }

    /**
     * @param entry The entry.
     */
    protected void incrementNrOfInvocations(StatisticsEntry entry) {
        if (entry != null) {
            numberOfInvocations += 1;
        }
    }

    /**
     * @param entry The entry.
     */
    protected void handleMaxDurationStatisticsEntry(StatisticsEntry entry) {
        if (entry != null && entry.getDurationMillis() != null) {
            // set top max duration statistics entry...
            if (maxDurationStatisticsEntry == null) {
                maxDurationStatisticsEntry = entry;
            } else if (maxDurationStatisticsEntry.getDurationMillis() < entry.getDurationMillis()) {
                    maxDurationStatisticsEntry = entry;
            }
        }
    }

    /**
     * @param entry The entry.
     */
    protected void handleMinDurationStatisticsEntry(StatisticsEntry entry) {
        if (entry != null && entry.getDurationMillis() != null) {
            if (minDurationStatisticsEntry == null) {
                minDurationStatisticsEntry = entry;
            } else {
                if (minDurationStatisticsEntry.getDurationMillis() > entry.getDurationMillis()) {
                    minDurationStatisticsEntry = entry;
                }
            }
        }
    }

    /**
     * @param entry The entry.
     * @param maxNumberOfTopDurationStatisticsEntries The number.
     */
    protected void handleTopDurationStatisticsEntries(StatisticsEntry entry,
            Integer maxNumberOfTopDurationStatisticsEntries) {
        if (entry != null && entry.getDurationMillis() != null) {
            if (maxNumberOfTopDurationStatisticsEntries == null) {
                topDurationStatisticsEntries.add(entry);
            } else {
                if (maxNumberOfTopDurationStatisticsEntries > topDurationStatisticsEntries.size()) {
                    topDurationStatisticsEntries.add(entry);
                } else {
                    StatisticsEntry min = StatisticsCollectionUtils
                        .getMinDurationEntry(topDurationStatisticsEntries);
                    if (min != null && min.getDurationMillis() < entry.getDurationMillis()) {
                        topDurationStatisticsEntries.add(entry);

                        // ensure top duration statistics entries max-size...
                        ensureTopDurationStatisticsEntriesSize(maxNumberOfTopDurationStatisticsEntries);
                    }
                }
            }
        }
    }

    /**
     * @param entry The entry.
     * @param maxNumberOfRecentStatisticsEntries The number.
     */
    protected void handleRecentStatisticsEntries(StatisticsEntry entry, Integer maxNumberOfRecentStatisticsEntries) {
        if (entry != null) {
            if (maxNumberOfRecentStatisticsEntries == null) {
                recentStatisticsEntries.add(entry);
            } else {
                if (maxNumberOfRecentStatisticsEntries > recentStatisticsEntries.size()) {
                    recentStatisticsEntries.add(entry);
                } else {
                    recentStatisticsEntries.add(entry);

                    // ensure recent statistics entries max-size...
                    ensureRecentStatisticsEntriesSize(maxNumberOfRecentStatisticsEntries);
                }
            }
        }
    }

    /**
     * This method ensures that {@code topDurationStatisticsEntries} does not exceed the given {@code
     * maxNumberOfTopDurationStatisticsEntries} by removing the appropriate items from {@code
     * topDurationStatisticsEntries}.<br/>
     * <br/>
     * <b>Note:</b> This method does not perform any synchronization - subclasses may to override this method
     * and provide desired synchronization mechanisms.
     *
     * @param maxNumberOfTopDurationStatisticsEntries The max number of top duration statistics entries.
     */
    protected void ensureTopDurationStatisticsEntriesSize(int maxNumberOfTopDurationStatisticsEntries) {
        try {
            if (topDurationStatisticsEntries.size() > maxNumberOfTopDurationStatisticsEntries) {
                // list contains too many entries...
                int nrOfEntriesToRemove = Math.max(0,
                        (topDurationStatisticsEntries.size() - maxNumberOfTopDurationStatisticsEntries));
                StatisticsEntry min = null;
                for (int i = 0; i < nrOfEntriesToRemove; i++) {
                    min = StatisticsCollectionUtils.getMinDurationEntry(topDurationStatisticsEntries);
                    if (min != null) {
                        topDurationStatisticsEntries.remove(min);
                    }

                    // Note: we use this, because there is no synchronization, just to ensure
                    // we do not delete too much entries from list...
                    if (topDurationStatisticsEntries.size() <= maxNumberOfTopDurationStatisticsEntries) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Unable to ensureTopDurationStatisticsEntriesSize: " + e.getMessage(), e);
        }
    }

    /**
     * This method ensures that {@code recentStatisticsEntries} does not exceed the given {@code
     * maxNumberOfRecentStatisticsEntries} by removing the appropriate items from {@code
     * recentStatisticsEntries}.<br/>
     * <br/>
     * <b>Note:</b> This method does not perform any synchronization - subclasses may to override this method
     * and provide desired synchronization mechanisms.
     *
     * @param maxNumberOfRecentStatisticsEntries The max number of top recent entries.
     */
    protected void ensureRecentStatisticsEntriesSize(int maxNumberOfRecentStatisticsEntries) {
        try {
            if (recentStatisticsEntries.size() > maxNumberOfRecentStatisticsEntries) {
                // list contains too many entries...
                int nrOfEntriesToRemove = Math.max(0,
                        (recentStatisticsEntries.size() - maxNumberOfRecentStatisticsEntries));
                StatisticsEntry oldest = null;
                for (int i = 0; i < nrOfEntriesToRemove; i++) {
                    if (!recentStatisticsEntries.isEmpty()) {
                        oldest = recentStatisticsEntries.get(0);
                    }
                    if (oldest != null) {
                        recentStatisticsEntries.remove(oldest);
                    }

                    // Note: we use this, because there is no synchronization, just to ensure
                    // we do not delete too much entries from list...
                    if (recentStatisticsEntries.size() <= maxNumberOfRecentStatisticsEntries) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Unable to ensureRecentStatisticsEntriesSize: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addStatisticsEntries(Collection<StatisticsEntry> entries, Integer maxNumberOfRecentStatsticsEntries,
            Integer maxNumberOfTopDurationStatisticsEntries) {
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
