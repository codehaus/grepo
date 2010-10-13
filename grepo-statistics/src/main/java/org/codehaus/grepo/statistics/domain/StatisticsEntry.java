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

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * @author dguggi
 */
public interface StatisticsEntry extends Serializable {
    /**
     * @param identifier The identifier.
     */
    void setIdentifier(String identifier);

    /**
     * @return Returns the identifier.
     */
    String getIdentifier();

    /**
     * @param creation The creation calendar.
     */
    void setCreation(Calendar creation);

    /**
     * @return Returns the creation calendar.
     */
    Calendar getCreation();

    /**
     * @return Returns the creation date.
     */
    Date getCreationDate();

    /**
     * @return Returns the creation millis.
     */
    Long getCreationMillis();

    /**
     * @return Returns {@code true} if {@code creation} is set and {@code false} otherwise.
     */
    boolean hasCreation();

    /**
     * @param origin The origin.
     */
    void setOrigin(String origin);

    /**
     * @return Returns the origin.
     */
    String getOrigin();
}
