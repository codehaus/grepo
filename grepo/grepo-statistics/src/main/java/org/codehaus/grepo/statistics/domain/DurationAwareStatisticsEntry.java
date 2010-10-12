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

/**
 * @author dguggi
 */
public interface DurationAwareStatisticsEntry extends StatisticsEntry {

    /**
     * @param completion The completion calendar.
     */
    void setCompletion(Calendar completion);

    /**
     * @return Returns the completion caleandar.
     */
    Calendar getCompletion();

    /**
     * @return Returns the completion date.
     */
    Date getCompletionDate();

    /**
     * @return Returns the completion millis.
     */
    Long getCompletionMillis();

    /**
     * @return Returns {@code true} if {@code completion} is set and {@code false} otherwise.
     */
    boolean hasCompletion();

    /**
     * @param durationMillis The duration millis.
     */
    void setDurationMillis(Long durationMillis);

    /**
     * @return Returns the duration millis.
     */
    Long getDurationMillis();

    /**
     * @return Returns {@code true} if {@code durationMillis} is set and {@code false} otherwise.
     */
    boolean hasDurationMillis();
}
