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
import java.util.List;
import java.util.Map;

import org.codehaus.grepo.statistics.domain.StatisticsEntry;

/**
 * @author dguggi
 */
public interface StatisticsCollection extends Serializable {

    /**
     * @param entry The {@link StatisticsEntry} to add.
     */
    void addStatisticsEntry(StatisticsEntry entry);

    /**
     * @param identifier The identifier.
     * @return Returns the {@link StatisticsCollectionEntry}.
     */
    StatisticsCollectionEntry get(String identifier);

    /**
     * @return Returns a read only map of {@link StatisticsCollectionEntry} objects.
     */
    Map<String, StatisticsCollectionEntry> getCollectionEntriesMapReadOnly();

    /**
     * @return Returns a read only list of {@link StatisticsCollectionEntry} objects.
     */
    List<StatisticsCollectionEntry> getCollectionEntriesList();

    /**
     * @return Returns a read only list of (distinct) collection entry identifiers.
     */
    List<String> getCollectionEntryIdentifiersList();

    /**
     * @return Returns the size of this collection.
     */
    int size();

    /**
     * Clears the collection.
     */
    void clear();

    /**
     * @param maxStatisticsEntries The max number of statistics entries.
     */
    void setMaxStatisticsEntries(Long maxStatisticsEntries);

    /**
     * @return Returns the max number of statistics entries.
     */
    Long getMaxStatisticsEntries();

}
