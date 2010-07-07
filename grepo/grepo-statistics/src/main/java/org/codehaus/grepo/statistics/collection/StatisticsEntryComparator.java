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

import java.util.Calendar;
import java.util.Comparator;

import org.apache.commons.lang.StringUtils;
import org.codehaus.grepo.statistics.domain.StatisticsEntry;

/**
 * @author dguggi
 */
public enum StatisticsEntryComparator implements Comparator<StatisticsEntry> {
    /** Sorts by duration millis asc. */
    DURATION_MILLIS_ASC {
        /** {@inheritDoc} */
        public int compare(StatisticsEntry o1, StatisticsEntry o2) {
            long o1m = o1.getDurationMillis() == null ? 0L : o1.getDurationMillis();
            long o2m = o2.getDurationMillis() == null ? 0L : o2.getDurationMillis();
            if (o1m < o2m) {
                return -1;
            } else if (o1m > o2m) {
                return 1;
            }
            return 0;
        }
    },

    /** Sorts by duration millis desc. */
    DURATION_MILLIS_DESC {
        /** {@inheritDoc} */
        public int compare(StatisticsEntry o1, StatisticsEntry o2) {
            long o1m = o1.getDurationMillis() == null ? 0L : o1.getDurationMillis();
            long o2m = o2.getDurationMillis() == null ? 0L : o2.getDurationMillis();
            if (o1m < o2m) {
                return 1;
            } else if (o1m > o2m) {
                return -1;
            }
            return 0;
        }
    },

    /** Sorts by creation asc. */
    CREATION_ASC {
        /** {@inheritDoc} */
        public int compare(StatisticsEntry o1, StatisticsEntry o2) {
            Calendar o1c = o1.getCreation();
            Calendar o2c = o2.getCreation();
            return o1c.compareTo(o2c);
        }
    },

    /** Sorts by creation desc. */
    CREATION_DESC {
        /** {@inheritDoc} */
        public int compare(StatisticsEntry o1, StatisticsEntry o2) {
            Calendar o1c = o1.getCreation();
            Calendar o2c = o2.getCreation();
            return -o1c.compareTo(o2c);
        }
    };

    /**
     * @param value The value.
     * @return Returns the comparator or {@code null}.
     */
    public static StatisticsEntryComparator fromString(String value) {
        if (StringUtils.isNotEmpty(value)) {
            for (StatisticsEntryComparator comperator : StatisticsEntryComparator.values()) {
                if (comperator.name().equalsIgnoreCase(value)) {
                    return comperator;
                }
            }
        }
        return null;
    }
}
