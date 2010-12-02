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

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.codehaus.grepo.statistics.domain.DurationAwareStatisticsEntry;
import org.codehaus.grepo.statistics.domain.StatisticsEntry;

/**
 * @author dguggi
 */
public class StatisticsCollectionUtils {

    /**
     * @param collection The collection.
     * @return Returns a sorted list of Strings.
     */
    public static List<String> getCollectionEntryIdentifiersSorted(StatisticsCollection collection) {
        List<String> list = collection.getCollectionEntryIdentifiersList();
        Collections.sort(list);
        return list;
    }

    /**
     * @param collection The collection.
     * @param comparator The comparator.
     * @return Returns a sorted list of {@link StatisticsCollectionEntry} objects.
     */
    public static List<StatisticsCollectionEntry> getCollectionEntries(StatisticsCollection collection,
            StatisticsCollectionEntryComparator comparator) {
        List<StatisticsCollectionEntry> list = collection.getCollectionEntriesList();
        Collections.sort(list, comparator);
        return list;

    }

    /**
     * @param entry The entry.
     * @param comparator The comparator.
     * @return Returns a sorted list of recent {@link StatisticsEntry} objects.
     */
    public static List<StatisticsEntry> getRecentStatisticsEntries(StatisticsCollectionEntry entry,
            StatisticsEntryComparator comparator) {
        List<StatisticsEntry> list = entry.getRecentStatisticsEntriesList();
        Collections.sort(list, comparator);
        return list;
    }

    /**
     * @param entry The entry.
     * @param comparator The comparator.
     * @return Returns a sorted list of top duration {@link StatisticsEntry} objects.
     */
    public static List<DurationAwareStatisticsEntry> getTopDurationStatisticsEntries(StatisticsCollectionEntry entry,
        StatisticsEntryComparator comparator) {
        List<DurationAwareStatisticsEntry> list = entry.getTopDurationStatisticsEntriesList();
        Collections.sort(list, comparator);
        return list;
    }

    /**
     * @param list The list.
     * @return Returns the {@link StatisticsEntry} with the minimum duration.
     */
    public static DurationAwareStatisticsEntry getMinDurationEntry(List<DurationAwareStatisticsEntry> list) {
        DurationAwareStatisticsEntry minDuration = null;
        for (DurationAwareStatisticsEntry entry : list) {
            if (entry != null && entry.hasDurationMillis()) {
                if (minDuration == null) {
                    minDuration = entry;
                } else if (minDuration.getDurationMillis() > entry.getDurationMillis()) {
                    minDuration = entry;
                }
            }
        }
        return minDuration;
    }

    /**
     * @param list The list.
     * @return Returns the {@link StatisticsEntry} with the maximum duration.
     */
    public static DurationAwareStatisticsEntry getMaxDurationEntry(List<DurationAwareStatisticsEntry> list) {
        DurationAwareStatisticsEntry maxDuration = null;
        for (DurationAwareStatisticsEntry entry : list) {
            if (entry != null && entry.hasDurationMillis()) {
                if (maxDuration == null) {
                    maxDuration = entry;
                } else if (maxDuration.getDurationMillis() < entry.getDurationMillis()) {
                    maxDuration = entry;
                }
            }
        }
        return maxDuration;
    }

    /**
     * @param list The list.
     * @return Returns the average duration.
     */
    public static Long getAverageDuration(List<StatisticsEntry> list) {
        long nrOfEntries = 0L;
        long sumMillis = 0L;
        for (StatisticsEntry entry : list) {
            Long duration = getDurationMillis(entry);
            if (duration != null) {
                sumMillis += duration;
                nrOfEntries += 1;
            }
        }

        if (nrOfEntries > 0) {
            return sumMillis / nrOfEntries;
        }
        return null;
    }

    /**
     * @param entry The entry.
     * @return Returns the duratin millis or {@code null}.
     */
    public static Long getDurationMillis(StatisticsEntry entry) {
        Long millis = null;
        if (entry instanceof DurationAwareStatisticsEntry) {
            millis = ((DurationAwareStatisticsEntry)entry).getDurationMillis();
        }
        return millis;
    }

    /**
     * @param entry The entry.
     * @return Returns the completion date or {@code null}.
     */
    public static Date getCompletionDate(StatisticsEntry entry) {
        Date date = null;
        if (entry instanceof DurationAwareStatisticsEntry) {
            date = ((DurationAwareStatisticsEntry)entry).getCompletionDate();
        }
        return date;
    }
}
