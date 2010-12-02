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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codehaus.grepo.statistics.domain.DurationAwareStatisticsEntry;
import org.codehaus.grepo.statistics.domain.StatisticsEntry;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * Simple class to print out information about a {@link StatisticsCollection}.
 *
 * @author dguggi
 */
@ManagedResource("Simple statistics printer")
public class SimpleStatisticsCollectionPrinter {
    /**
     * @author dguggi
     */
    public enum OutputType {
        /** The html. */
        HTML,
        /** The txt. */
        TXT,
    };

    /** NBSP. */
    private static final String NBSP = "&nbsp;";

    /** The collection. */
    private StatisticsCollection collection; // NOPMD

    /** The output type. */
    private OutputType type = OutputType.TXT;

    /** The date format. */
    private SimpleDateFormat dateFormat; // NOPMD

    /** Default constructor. */
    public SimpleStatisticsCollectionPrinter() {
        // default constructor
    }

    /**
     * @param collection The collection to set.
     */
    public SimpleStatisticsCollectionPrinter(StatisticsCollection collection) {
        this.collection = collection;
    }

    /**
     * @return Returns the summary.
     */
    @ManagedOperation(description = "Prints summary")
    public String printSummary() {
        return printSummary(false);
    }

    /**
     * @param calcAverageDuration The flag.
     * @return Returns the summary.
     */
    @ManagedOperation(description = "Prints summary")
    @ManagedOperationParameters({
        @ManagedOperationParameter(name = "calcAverageDuration",
            description = "Flag to determine whether or not to calcualate average duration")
    })
    public String printSummary(boolean calcAverageDuration) {
        return printSummary(calcAverageDuration, StatisticsCollectionEntryComparator.IDENTIFIER_ASC);
    }

    /**
     * @param calcAverageDuration The flag.
     * @param sorter The comparator string.
     * @return Returns the summary.
     */
    @ManagedOperation(description = "Prints summary")
    @ManagedOperationParameters({
        @ManagedOperationParameter(name = "calcAverageDuration",
            description = "Flag to determine whether or not to calcualate average duration"),
        @ManagedOperationParameter(name = "sorter",
            description = "optional (IDENTIFIER_ASC, IDENTIFIER_DESC, NUMER_OF_INVOCATIONS_ASC, "
                        + "NUMER_OF_INVOCATIONS_DESC, MAX_DURATION_ASC, MAX_DURATION_DESC, MIN_DURATION_ASC, "
                        + "MIN_DURATION_DESC)")
    })
    public String printSummary(boolean calcAverageDuration, String sorter) {
        StatisticsCollectionEntryComparator comparator = StatisticsCollectionEntryComparator.fromString(sorter);
        if (comparator == null) {
            return printSummary(calcAverageDuration);
        } else {
            return printSummary(calcAverageDuration, comparator);
        }
    }

    /**
     * @param calcAverageDuration The flag.
     * @param comparator The comparator.
     * @return Returns the summary.
     */
    @SuppressWarnings("PMD")
    public String printSummary(boolean calcAverageDuration, StatisticsCollectionEntryComparator comparator) {
        StringBuilder sb = new StringBuilder();
        sb.append("STATISTICS SUMMARY").append(nl(2));

        // print recent statistics...
        List<StatisticsCollectionEntry> list = StatisticsCollectionUtils.getCollectionEntries(
            collection, comparator);
        if (list.size() > 0) {
            sb.append("" + list.size() + " collection entries (sorted by " + comparator + "):");
            sb.append(nl(2));
            printStartCollectionEntryHeader(sb, calcAverageDuration);
            for (StatisticsCollectionEntry entry : list) {
                printCollectionEntryRow(entry, sb, calcAverageDuration);
            }
            printEndCollectionEntryHeader(sb);
        }

        return sb.toString();
    }

    /**
     * @param identifier The identifier.
     * @return Returns the detail.
     */
    @ManagedOperation(description = "Prints detail")
    @ManagedOperationParameters({
        @ManagedOperationParameter(name = "identifier", description = "The identifier")
    })
    public String printDetail(String identifier) {
        return printDetail(identifier, StatisticsEntryComparator.CREATION_DESC);
    }

    /**
     * @param identifier The identifier.
     * @param sorter The sorter.
     * @return Returns the detail.
     */
    @ManagedOperation(description = "Prints detail")
    @ManagedOperationParameters({
        @ManagedOperationParameter(name = "identifier", description = "The identifier"),
        @ManagedOperationParameter(name = "sorter",
            description = "optional (DURATION_MILLIS_ASC, DURATION_MILLIS_DESC, CREATION_ASC, CREATION_DESC)")
    })
    public String printDetail(String identifier, String sorter) {
        StatisticsEntryComparator comparator = StatisticsEntryComparator.fromString(sorter);
        if (comparator == null) {
            return printDetail(identifier);
        } else {
            return printDetail(identifier, comparator);
        }
    }

    /**
     * @param identifier The identifier.
     * @param comparator The comparator.
     * @return Returns the detail.
     */
    @SuppressWarnings("PMD")
    public String printDetail(String identifier, StatisticsEntryComparator comparator) {
        StringBuilder sb = new StringBuilder();
        StatisticsCollectionEntry entry = collection.get(identifier);
        if (entry == null) {
            sb.append("identifier '" + identifier + "' not found");
        } else {
            sb.append("STATISTICS DETAIL").append(nl(2));

            String minDurationMillis = "";
            String minDurationDate = "";
            DurationAwareStatisticsEntry minEntry = entry.getMinDurationStatisticsEntry();
            if (minEntry != null) {
                if (minEntry.hasDurationMillis()) {
                    minDurationMillis = String.valueOf(minEntry.getDurationMillis());
                }
                minDurationDate = formatDate(minEntry.getCreationDate());
            }

            String maxDurationMillis = "";
            String maxDurationDate = "";
            DurationAwareStatisticsEntry maxEntry = entry.getMaxDurationStatisticsEntry();
            if (maxEntry != null) {
                if (maxEntry.hasDurationMillis()) {
                    maxDurationMillis = String.valueOf(maxEntry.getDurationMillis());
                }
                maxDurationDate = formatDate(maxEntry.getCreationDate());
            }

            // calculate average duration millis
            String avgDuration = "";
            Long avgDurationValue = StatisticsCollectionUtils.getAverageDuration(
                entry.getRecentStatisticsEntriesReadOnly());
            if (avgDuration != null) {
                avgDuration = String.valueOf(avgDurationValue);
            }

            sb.append("identifier: " + identifier + nl());
            sb.append("invocations: " + entry.getNumberOfInvocations() + nl());
            sb.append("minDuration: " + minDurationMillis + " (" + minDurationDate + ")" + nl());
            sb.append("maxDuration: " + maxDurationMillis + " (" + maxDurationDate + ")" + nl());
            sb.append("avgDuration: " + avgDuration + nl());
            sb.append(nl());

            List<DurationAwareStatisticsEntry> topDurationList = StatisticsCollectionUtils.
                    getTopDurationStatisticsEntries(entry, StatisticsEntryComparator.DURATION_MILLIS_DESC);
            if (topDurationList.size() > 0) {
                sb.append("" + topDurationList.size() + " top durations (sorted by "
                    + StatisticsEntryComparator.DURATION_MILLIS_DESC + "):");
                sb.append(nl(2));
                printStartStatisticsEntryHeader(sb);
                for (StatisticsEntry e : topDurationList) {
                    printStatisticsEntryRow(e, sb);
                }
                printEndStatisticsEntryHeader(sb);
                sb.append(nl(2));

            }

            List<StatisticsEntry> recentList = StatisticsCollectionUtils.getRecentStatisticsEntries(
                entry, comparator);
            if (recentList.size() > 0) {
                sb.append("" + recentList.size() + " recent invocations (sorted by " + comparator + "):");
                sb.append(nl(2));
                printStartStatisticsEntryHeader(sb);
                for (StatisticsEntry e : recentList) {
                    printStatisticsEntryRow(e, sb);
                }
                printEndStatisticsEntryHeader(sb);
                sb.append(nl(2));
            }

        }
        return sb.toString();
    }

    /**
     * @param sb The string builder.
     * @param calcAverageDuration The flag.
     */
    private void printStartCollectionEntryHeader(StringBuilder sb, boolean calcAverageDuration) {
        if (isHtml()) {
            sb.append("<table border=\"1\" cellpadding=\"2px\"><tr>");
            sb.append("<td><b>identifier</b></td>");
            sb.append("<td><b>invocations</b></td>");
            sb.append("<td><b>minDuration</b></td>");
            sb.append("<td><b>maxDuration</b></td>");
            if (calcAverageDuration) {
                sb.append("<td><b>avgDuration</b></td>");
            }
            sb.append("</tr>");
        }
    }

    /**
     * @param sb The string builder.
     */
    private void printStartStatisticsEntryHeader(StringBuilder sb) {
        if (isHtml()) {
            sb.append("<table border=\"1\" cellpadding=\"2px\">");
            sb.append("<tr><td><b>duration</b></td>");
            sb.append("<td><b>creation</b></td>");
            sb.append("<td><b>completion</b></td>");
            sb.append("<td><b>origin</b></td></tr>");
        }
    }

    /**
     * @param sb The string builder.
     */
    private void printEndStatisticsEntryHeader(StringBuilder sb) {
        if (isHtml()) {
            sb.append("</table>");
        }
    }


    /**
     * @param sb The string builder.
     */
    private void printEndCollectionEntryHeader(StringBuilder sb) {
        if (isHtml()) {
            sb.append("</table>");
        }
    }

    /**
     * @param entry The entry.
     * @param sb The string builder.
     * @param calcAverageDuration The flag.
     */
    @SuppressWarnings("PMD")
    private void printCollectionEntryRow(StatisticsCollectionEntry entry, StringBuilder sb,
            boolean calcAverageDuration) {
        if (entry != null) {
            String minDurationMillis = "";
            String minDurationDate = "";
            String identifier = "";
            DurationAwareStatisticsEntry minEntry = entry.getMinDurationStatisticsEntry();
            if (minEntry != null) {
                identifier = StringUtils.defaultString(minEntry.getIdentifier());
                if (minEntry.hasDurationMillis()) {
                    minDurationMillis = String.valueOf(minEntry.getDurationMillis());
                }
                minDurationDate = formatDate(minEntry.getCreationDate());
            }

            String maxDurationMillis = "";
            String maxDurationDate = "";
            DurationAwareStatisticsEntry maxEntry = entry.getMaxDurationStatisticsEntry();
            if (maxEntry != null) {
                if (maxEntry.hasDurationMillis()) {
                    maxDurationMillis = String.valueOf(maxEntry.getDurationMillis());
                }
                maxDurationDate = formatDate(maxEntry.getCreationDate());
            }

            // calculate average duration millis
            String avgDuration = "";
            if (calcAverageDuration) {
                Long avgDurationValue = StatisticsCollectionUtils.getAverageDuration(
                        entry.getRecentStatisticsEntriesReadOnly());
                if (avgDuration != null) {
                    avgDuration = String.valueOf(avgDurationValue);
                }
            }

            if (isHtml()) {
                sb.append("<tr>");
                sb.append("<td>" + identifier + "</td>");
                sb.append("<td>" + entry.getNumberOfInvocations() + "</td>");
                sb.append("<td>" + minDurationMillis + " (" + minDurationDate + ")" + "</td>");
                sb.append("<td>" + maxDurationMillis + " (" + maxDurationDate + ")" + "</td>");
                if (calcAverageDuration) {
                    sb.append("<td>" + avgDuration + "</td>");
                }
                sb.append("</tr>");
            } else {
                sb.append(identifier).append(":");
                sb.append(" invocations=" + entry.getNumberOfInvocations());
                sb.append(" minDuration=" + minDurationMillis + " (" + minDurationDate + ")");
                sb.append(" maxDuration=" + maxDurationMillis + " (" + maxDurationDate + ")");
                if (calcAverageDuration) {
                    sb.append(" avgDuration=" + avgDuration);
                }
                sb.append(nl());
            }
        }
    }

    /**
     * @param entry The entry.
     * @param sb The string builder.
     */
    @SuppressWarnings("PMD")
    private void printStatisticsEntryRow(StatisticsEntry entry, StringBuilder sb) {
        if (entry != null) {
            String duration = "";
            Long durationValue = StatisticsCollectionUtils.getDurationMillis(entry);
            if (durationValue != null) {
                duration = durationValue.toString();
            }

            String creationDate = formatDate(entry.getCreationDate());
            String completionDate = formatDate(StatisticsCollectionUtils.getCompletionDate(entry));

            String origin = StringUtils.defaultString(entry.getOrigin());

            if (isHtml()) {
                sb.append("<tr>");
                sb.append("<td>" + (StringUtils.isEmpty(duration) ? NBSP : duration) + "</td>");
                sb.append("<td>" + (StringUtils.isEmpty(creationDate) ? NBSP : creationDate) + "</td>");
                sb.append("<td>" + (StringUtils.isEmpty(completionDate) ? NBSP : completionDate) + "</td>");
                sb.append("<td>" + (StringUtils.isEmpty(origin) ? NBSP : origin) + "</td>");
                sb.append("</tr>");
            } else {
                sb.append(" duration: " + duration);
                sb.append(" creation: " + creationDate);
                sb.append(" completion: " + completionDate);
                sb.append(" origin: " + origin + nl());
            }
        }
    }

    /**
     * @param date The date to format.
     * @return Returns the date as string.
     */
    private String formatDate(Date date) {
        String result = "";
        if (date != null) {
            result = (dateFormat == null ? date.toString() : dateFormat.format(date));
        }
        return result;
    }

    /**
     * @return Returns the newline.
     */
    @SuppressWarnings("PMD")
    private String nl() {
        return (isTxt() ? "\n" : "<br/>");
    }

    /**
     * @param count The count.
     * @return Returns the newline.
     */
    @SuppressWarnings("PMD")
    private String nl(int count) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < count; i++) {
            result.append(nl());
        }
        return result.toString();
    }

    public void setCollection(StatisticsCollection collection) {
        this.collection = collection;
    }

    public OutputType getType() {
        return type;
    }

    public void setType(OutputType type) {
        this.type = type;
    }

    public boolean isHtml() {
        return (type == OutputType.HTML);
    }

    public boolean isTxt() {
        return (type == OutputType.TXT);
    }

    public void setDateFormat(String pattern) {
        dateFormat = new SimpleDateFormat(pattern); // NOPMD
    }

}
