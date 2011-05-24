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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.grepo.statistics.domain.StatisticsEntry;

/**
 * @author dguggi
 */
public class StatisticsCollectionImpl implements StatisticsCollection {

    private static final long serialVersionUID = 8116130509981679653L;

    private Map<String, StatisticsCollectionEntry> collectionEntries = new HashMap<String, StatisticsCollectionEntry>();
    private Integer maxNumberOfTopDurationStatisticsEntries;
    private Integer maxNumberOfRecentStatisticsEntries;

    /**
     * {@inheritDoc}
     */
    public void addStatisticsEntry(StatisticsEntry entry) {
        StatisticsCollectionEntry collectionEntry = collectionEntries.get(entry.getIdentifier());
        if (collectionEntry == null) {
            collectionEntry = new StatisticsCollectionEntryImpl();
            collectionEntry.addStatisticsEntry(entry, maxNumberOfRecentStatisticsEntries,
                maxNumberOfTopDurationStatisticsEntries);
            collectionEntries.put(entry.getIdentifier(), collectionEntry);
        } else {
            collectionEntry.addStatisticsEntry(entry, maxNumberOfRecentStatisticsEntries,
                maxNumberOfTopDurationStatisticsEntries);
        }
    }

    /**
     * {@inheritDoc}
     */
    public StatisticsCollectionEntry get(String identifier) {
        return collectionEntries.get(identifier);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, StatisticsCollectionEntry> getCollectionEntriesMapReadOnly() {
        return Collections.unmodifiableMap(collectionEntries);
    }

    /**
     * {@inheritDoc}
     */
    public List<StatisticsCollectionEntry> getCollectionEntriesList() {
        return new ArrayList<StatisticsCollectionEntry>(collectionEntries.values());
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getCollectionEntryIdentifiersList() {
        return new ArrayList<String>(collectionEntries.keySet());
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        return collectionEntries.size();
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        collectionEntries.clear();
    }

    /**
     * {@inheritDoc}
     */
    public Integer getMaxNumberOfTopDurationStatisticsEntries() {
        return maxNumberOfTopDurationStatisticsEntries;
    }

    public void setMaxNumberOfTopDurationStatisticsEntries(Integer maxNumberOfTopDurationStatisticsEntries) {
        this.maxNumberOfTopDurationStatisticsEntries = maxNumberOfTopDurationStatisticsEntries;
    }

    /**
     * {@inheritDoc}
     */
    public Integer getMaxNumberOfRecentStatisticsEntries() {
        return maxNumberOfRecentStatisticsEntries;
    }

    public void setMaxNumberOfRecentStatisticsEntries(Integer maxNumberOfRecentStatisticsEntries) {
        this.maxNumberOfRecentStatisticsEntries = maxNumberOfRecentStatisticsEntries;
    }

}
