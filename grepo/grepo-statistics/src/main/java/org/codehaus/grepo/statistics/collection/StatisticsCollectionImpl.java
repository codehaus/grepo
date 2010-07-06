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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.grepo.statistics.domain.StatisticsEntry;

/**
 * @author dguggi
 */
public class StatisticsCollectionImpl implements StatisticsCollection {

    /** SerialVersionUid. */
    private static final long serialVersionUID = 8116130509981679653L;

    /** The collection entries. */
    private Map<String, StatisticsCollectionEntry> collectionEntries = new HashMap<String, StatisticsCollectionEntry>();

    /** The max statistics entries to hold in memory (per identifier). */
    private Long maxStatisticsEntries;

    /**
     * {@inheritDoc}
     */
    public void addStatisticsEntry(StatisticsEntry entry) {
        StatisticsCollectionEntry collectionEntry = collectionEntries.get(entry.getIdentifier());
        if (collectionEntry == null) {
            collectionEntry = new StatisticsCollectionEntryImpl();
            collectionEntry.addStatisticsEntry(entry, maxStatisticsEntries);
            collectionEntries.put(entry.getIdentifier(), collectionEntry);
        } else {
            collectionEntry.addStatisticsEntry(entry);
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
    public Map<String, StatisticsCollectionEntry> getCollectionEntriesMap() {
        return Collections.unmodifiableMap(collectionEntries);
    }

    /**
     * {@inheritDoc}
     */
    public List<StatisticsCollectionEntry> getCollectionEntriesList() {
        return Collections.unmodifiableList(new ArrayList<StatisticsCollectionEntry>(collectionEntries.values()));
    }

    /**
     * {@inheritDoc}
     */
    public Collection<StatisticsCollectionEntry> getCollectionEntries() {
        return Collections.unmodifiableCollection(collectionEntries.values());
    }

    /**
     * {@inheritDoc}
     */
    public Collection<String> getCollectionEntryIdentifiers() {
        return Collections.unmodifiableCollection(collectionEntries.keySet());
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getCollectionEntryIdentifiersList() {
        return Collections.unmodifiableList(new ArrayList<String>(collectionEntries.keySet()));
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        return collectionEntries.size();
    }

    public void setMaxStatisticsEntries(Long maxStatisticsEntries) {
        this.maxStatisticsEntries = maxStatisticsEntries;
    }

}
