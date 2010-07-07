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

import java.util.Comparator;

import org.apache.commons.lang.StringUtils;

/**
 * @author dguggi
 */
public enum StatisticsCollectionEntryComparator implements Comparator<StatisticsCollectionEntry> {

    /** Sorts by identfier asc. */
    IDENTIFIER_ASC {
        /** {@inheritDoc} */
        public int compare(StatisticsCollectionEntry o1, StatisticsCollectionEntry o2) {
            String o1i = o1.getStatisticsEntriesReadOnly().get(0).getIdentifier();
            String o2i = o2.getStatisticsEntriesReadOnly().get(0).getIdentifier();
            return o1i.compareTo(o2i);
        }
    },

    /** Sorts by identifier desc. */
    IDENTIFIER_DESC {
        /** {@inheritDoc} */
        public int compare(StatisticsCollectionEntry o1, StatisticsCollectionEntry o2) {
            String o1i = o1.getStatisticsEntriesReadOnly().get(0).getIdentifier();
            String o2i = o2.getStatisticsEntriesReadOnly().get(0).getIdentifier();
            return -o1i.compareTo(o2i);
        }
    },

    /** Sorts by number of invocations asc. */
    NUMER_OF_INVOCATIONS_ASC {
        /** {@inheritDoc} */
        public int compare(StatisticsCollectionEntry o1, StatisticsCollectionEntry o2) {
            if (o1.getNumberOfInvocations() < o2.getNumberOfInvocations()) {
                return -1;
            } else if (o1.getNumberOfInvocations() > o2.getNumberOfInvocations()) {
                return 1;
            } else {
                return 0;
            }
        }
    },

    /** Sorts by number of invocations desc. */
    NUMER_OF_INVOCATIONS_DESC {
        /** {@inheritDoc} */
        public int compare(StatisticsCollectionEntry o1, StatisticsCollectionEntry o2) {
            if (o1.getNumberOfInvocations() < o2.getNumberOfInvocations()) {
                return 1;
            } else if (o1.getNumberOfInvocations() > o2.getNumberOfInvocations()) {
                return -1;
            } else {
                return 0;
            }
        }
    },

    /** Max duration asc. */
    MAX_DURATION_ASC {
        /** {@inheritDoc} */
        public int compare(StatisticsCollectionEntry o1, StatisticsCollectionEntry o2) {
            if (o1.getMaxDurationMillis() < o2.getMaxDurationMillis()) {
                return -1;
            } else if (o1.getMaxDurationMillis() > o2.getMaxDurationMillis()) {
                return 1;
            }
            return 0;
        }
    },

    /** Max duration desc. */
    MAX_DURATION_DESC {
        /** {@inheritDoc} */
        public int compare(StatisticsCollectionEntry o1, StatisticsCollectionEntry o2) {
            if (o1.getMaxDurationMillis() < o2.getMaxDurationMillis()) {
                return 1;
            } else if (o1.getMaxDurationMillis() > o2.getMaxDurationMillis()) {
                return -1;
            }
            return 0;
        }
    },

    /** Max duration asc. */
    MIN_DURATION_ASC {
        /** {@inheritDoc} */
        public int compare(StatisticsCollectionEntry o1, StatisticsCollectionEntry o2) {
            if (o1.getMinDurationMillis() < o2.getMinDurationMillis()) {
                return -1;
            } else if (o1.getMinDurationMillis() > o2.getMinDurationMillis()) {
                return 1;
            }
            return 0;
        }
    },

    /** Min duration desc. */
    MIN_DURATION_DESC {
        /** {@inheritDoc} */
        public int compare(StatisticsCollectionEntry o1, StatisticsCollectionEntry o2) {
            if (o1.getMinDurationMillis() < o2.getMinDurationMillis()) {
                return 1;
            } else if (o1.getMinDurationMillis() > o2.getMinDurationMillis()) {
                return -1;
            }
            return 0;
        }
    };

    /**
     * @param value The value.
     * @return Returns the comparator or {@code null}.
     */
    public static StatisticsCollectionEntryComparator fromString(String value) {
        if (StringUtils.isNotEmpty(value)) {
            for (StatisticsCollectionEntryComparator comperator : StatisticsCollectionEntryComparator.values()) {
                if (comperator.name().equalsIgnoreCase(value)) {
                    return comperator;
                }
            }
        }
        return null;
    }

}
