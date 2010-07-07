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

package org.codehaus.grepo.statistics.service;

import java.util.List;

import org.codehaus.grepo.statistics.collection.StatisticsCollection;
import org.codehaus.grepo.statistics.collection.StatisticsCollectionEntry;
import org.codehaus.grepo.statistics.collection.StatisticsCollectionEntryComparator;
import org.codehaus.grepo.statistics.collection.StatisticsCollectionUtils;
import org.codehaus.grepo.statistics.collection.StatisticsEntryComparator;
import org.codehaus.grepo.statistics.domain.StatisticsEntry;
import org.springframework.beans.factory.annotation.Required;
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

    /** Default constructor. */
    public SimpleStatisticsCollectionPrinter() {
    }

    /**
     * @param collection The collection to set.
     */
    public SimpleStatisticsCollectionPrinter(StatisticsCollection collection) {
        this.collection = collection;
    }

    /** The collection. */
    private StatisticsCollection collection;

    /** The output type. */
    private OutputType type = OutputType.TXT;

    /**
     * @return Returns the summary.
     */
    @ManagedOperation(description = "Prints summary")
    public String printSummary() {
        return printSummary(StatisticsCollectionEntryComparator.IDENTIFIER_ASC);
    }

    /**
     * @param sorter The comparator string.
     * @return Returns the summary.
     */
    @ManagedOperation(description = "Prints summary")
    @ManagedOperationParameters({
        @ManagedOperationParameter(name = "sorter",
            description = "optional (IDENTIFIER_ASC, IDENTIFIER_DESC, NUMER_OF_INVOCATIONS_ASC, "
                        + "NUMER_OF_INVOCATIONS_DESC, MAX_DURATION_ASC, MAX_DURATION_DESC, MIN_DURATION_ASC, "
                        + "MIN_DURATION_DESC)")
    })
    public String printSummary(String sorter) {
        StatisticsCollectionEntryComparator comparator = StatisticsCollectionEntryComparator.fromString(sorter);
        if (comparator == null) {
            return printSummary();
        } else {
            return printSummary(comparator);
        }
    }

    /**
     * @param comparator The comparator.
     * @return Returns the summary.
     */
    public String printSummary(StatisticsCollectionEntryComparator comparator) {
        List<StatisticsCollectionEntry> list = StatisticsCollectionUtils.getCollectionEntries(
                collection, comparator);
        StringBuilder sb = new StringBuilder();
        sb.append("STATISTICS SUMMARY").append(nl()).append(nl());
        sb.append("Number of collection entries: " + list.size()).append(" (sorted: " + comparator + ")");
        sb.append(nl()).append(nl());

        if (isHtml()) {
            sb.append("<table border=\"1\"><tr><td><b>identifier</b></td>");
            sb.append("<td><b>numberOfInvocations</b></td>");
            sb.append("<td><b>minDurationMillis</b></td>");
            sb.append("<td><b>maxDurationMillis</b></td></tr>");
        }

        for (StatisticsCollectionEntry entry : list) {
            if (isHtml()) {
                sb.append("<tr>");
                sb.append("<td>").append(entry.getStatisticsEntriesReadOnly().get(0).getIdentifier()).append("</td>");
                sb.append("<td>").append(entry.getNumberOfInvocations()).append("</td>");
                sb.append("<td>").append(entry.getMinDurationMillis()).append("</td>");
                sb.append("<td>").append(entry.getMaxDurationMillis()).append("</td>");
                sb.append("</tr>");
            } else {
                sb.append(entry.getStatisticsEntriesReadOnly().get(0).getIdentifier()).append(":");
                sb.append(" numberOfInvocations=").append(entry.getNumberOfInvocations());
                sb.append(" minDurationMillis=").append(entry.getMinDurationMillis());
                sb.append(" maxDurationMillis=").append(entry.getMaxDurationMillis());
                sb.append(nl());
            }
        }

        if (isHtml()) {
            sb.append("</table>");
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
        return printDetail(identifier, StatisticsEntryComparator.CREATION_ASC);
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
            description = "optional (DURATION_MILLIS_AS, DURATION_MILLIS_DESC, CREATION_ASC, CREATION_DESC)")
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
    public String printDetail(String identifier,StatisticsEntryComparator comparator) {
        StringBuilder sb = new StringBuilder();
        StatisticsCollectionEntry entry = collection.get(identifier);
        if (entry == null) {
            sb.append("identifier '" + identifier + "' not found");
        } else {
            sb.append("STATISTICS DETAIL").append(nl()).append(nl());
            sb.append("identifier: ").append(identifier).append(nl());
            sb.append("numberOfInvocations: " + entry.getNumberOfInvocations()).append(nl());
            sb.append("minDurationMillis: " + entry.getMinDurationMillis()).append(nl());
            sb.append("maxDurationMillis: " + entry.getMaxDurationMillis()).append(nl());
            sb.append(nl());

            List<StatisticsEntry> list = StatisticsCollectionUtils.getStatisticEntries(
                entry, comparator);
            sb.append("" + list.size() + " invocations (sorted: " + comparator + ")").append(nl()).append(nl());

            if (isHtml()) {
                sb.append("<table border=\"1\">");
                sb.append("<tr><td><b>durationMillis</b></td>");
                sb.append("<td><b>creation</b></td>");
                sb.append("<td><b>completion</b></td>");
                sb.append("<td><b>origin</b></td></tr>");
            }

            for (StatisticsEntry se : list) {
                if (isHtml()) {
                    sb.append("<tr>");
                    sb.append("<td>").append(se.getDurationMillis()).append("</td>");
                    sb.append("<td>").append(se.getCreationDate()).append("</td>");
                    sb.append("<td>").append(se.getCompletionDate()).append("</td>");
                    sb.append("<td>").append(se.getOrigin()).append("</td>");
                    sb.append("</tr>");
                } else {
                    sb.append(" durationMillis: " + se.getDurationMillis());
                    sb.append(" creation: " + se.getCreationDate());
                    sb.append(" completion: " + se.getCompletionDate());
                    sb.append(" origin: " + se.getOrigin()).append(nl());
                }
            }

            if (isHtml()) {
                sb.append("</table>");
            }

        }
        return sb.toString();
    }

    /**
     * @return Returns the newline.
     */
    private String nl() {
        return (isTxt() ? "\n" : "<br/>");
    }

    @Required
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
}
