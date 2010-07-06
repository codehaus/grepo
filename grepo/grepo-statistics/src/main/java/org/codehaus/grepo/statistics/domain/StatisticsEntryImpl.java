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

package org.codehaus.grepo.statistics.domain;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author dguggi
 */
public class StatisticsEntryImpl implements StatisticsEntry {

    /** SerialVersionUid. */
    private static final long serialVersionUID = 588742446017759882L;

    /** The identifier. */
    private String identifier;

    /** The creation. */
    private Calendar creation;

    /** The completion. */
    private Calendar completion;

    /** The duration millis. */
    private Long durationMillis;

    /** The origin. */
    private String origin;

    /**
     * {@inheritDoc}
     */
    public String getOrigin() {
        return origin;
    }

    /**
     * {@inheritDoc}
     */
    public void setOrigin(String origin) {
        this.origin = origin;
    }

    /**
     * {@inheritDoc}
     */
    public void setCompletion(Calendar completion) {
        this.completion = completion;
    }

    /**
     * {@inheritDoc}
     */
    public Calendar getCompletion() {
        return completion;
    }

    /**
     * {@inheritDoc}
     */
    public Date getCompletionDate() {
        if (completion == null) {
            return null;
        }
        return completion.getTime();
    }

    /**
     * {@inheritDoc}
     */
    public Long getCompletionMillis() {
        if (completion == null) {
            return null;
        }
        return completion.getTimeInMillis();
    }

    public void setCreation(Calendar creation) {
        this.creation = creation;
    }

    /**
     * {@inheritDoc}
     */
    public Calendar getCreation() {
        return creation;
    }

    /**
     * {@inheritDoc}
     */
    public Date getCreationDate() {
        if (creation == null) {
            return null;
        }
        return creation.getTime();
    }

    /**
     * {@inheritDoc}
     */
    public Long getCreationMillis() {
        if (creation == null) {
            return null;
        }
        return creation.getTimeInMillis();
    }

    public void setDurationMillis(Long durationMillis) {
        this.durationMillis = durationMillis;
    }

    /**
     * {@inheritDoc}
     */
    public Long getDurationMillis() {
        return durationMillis;
    }


    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * {@inheritDoc}
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("identifier", getIdentifier())
            .append("creationDate", getCreationDate()).append("completionDate", getCompletionDate()).append(
                "durationMillis", getDurationMillis()).append("origin", getOrigin()).toString();
    }
}
