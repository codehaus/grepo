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

/**
 * @author dguggi
 */
public class StatisticsEntryFactoryImpl implements StatisticsEntryFactory {

    /**
     * {@inheritDoc}
     */
    public StatisticsEntry createStatisticsEntry(String identifier, Calendar creation) {
        return createStatisticsEntry(identifier, creation, null);
    }

    /**
     * {@inheritDoc}
     */
    public StatisticsEntry createStatisticsEntry(String identifier, Calendar creation, String origin) {
        StatisticsEntry entry = new StatisticsEntryImpl();
        entry.setIdentifier(identifier);
        entry.setCreation(creation);
        entry.setOrigin(origin);
        return entry;
    }

}
