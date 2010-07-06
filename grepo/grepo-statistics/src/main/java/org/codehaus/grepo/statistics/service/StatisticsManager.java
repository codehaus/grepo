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

import org.codehaus.grepo.statistics.domain.StatisticsEntry;

/**
 * @author dguggi
 */
public interface StatisticsManager {

    /**
     * @param identifier The identifier.
     * @return The statistics entry.
     */
    StatisticsEntry createStatisticsEntry(String identifier);

    /**
     * @param entry The statistics entry.
     */
    void completeStatisticsEntry(StatisticsEntry entry);

    /**
     * @return Returns {@code true} is manager is enabled and {@code false} otherwise.
     */
    boolean isEnabled();

    /**
     * @param enabled The flag to set.
     */
    void setEnabled(boolean enabled);
}
