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

import java.util.List;

import org.codehaus.grepo.core.AbstractSpringTest;
import org.codehaus.grepo.core.context.GrepoTestContextLoader;
import org.codehaus.grepo.statistics.domain.StatisticsEntry;
import org.codehaus.grepo.statistics.service.StatisticsManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author dguggi
 */
@ContextConfiguration(loader = GrepoTestContextLoader.class)
public class StatisticsCollectionUtilsTest extends AbstractSpringTest {

    /** The statistics manager. */
    @Autowired
    private StatisticsManager manager; // NOPMD

    /** The collection. */
    @Autowired
    private StatisticsCollection collection; // NOPMD

    /** after. */
    @After
    public void after() {
        collection.clear();
    }

    /**
     * Fills the collection.
     */
    private void fillCollection() {
        StatisticsEntry entry = manager.createStatisticsEntry("a");
        manager.completeStatisticsEntry(entry);
        entry = manager.createStatisticsEntry("a");
        manager.completeStatisticsEntry(entry);
        entry = manager.createStatisticsEntry("b");
        manager.completeStatisticsEntry(entry);
    }

    /**
     * Tests {@link StatisticsCollectionUtils#getCollectionEntries(StatisticsCollection,
     *          StatisticsCollectionEntryComparator)}.
     */
    @Test
    public void testGetCollectionEntriesSortedByNumberOfInvocations() {
        fillCollection();
        List<StatisticsCollectionEntry> list = StatisticsCollectionUtils.getCollectionEntries(collection,
            StatisticsCollectionEntryComparator.NUMBER_OF_INVOCATIONS_ASC);
        Assert.assertEquals(2, list.size());
        Assert.assertEquals(1, list.get(0).getNumberOfInvocations());
        Assert.assertEquals(2, list.get(1).getNumberOfInvocations());

        list = StatisticsCollectionUtils.getCollectionEntries(collection,
            StatisticsCollectionEntryComparator.NUMBER_OF_INVOCATIONS_DESC);
        Assert.assertEquals(2, list.size());
        Assert.assertEquals(2, list.get(0).getNumberOfInvocations());
        Assert.assertEquals(1, list.get(1).getNumberOfInvocations());
    }
}
