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

import junit.framework.Assert;

import org.codehaus.grepo.core.AbstractSpringTest;
import org.codehaus.grepo.core.context.GrepoTestContextLoaderWithDefLoc;
import org.codehaus.grepo.statistics.collection.StatisticsCollection;
import org.codehaus.grepo.statistics.collection.StatisticsCollectionEntry;
import org.codehaus.grepo.statistics.domain.StatisticsEntry;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author dguggi
 */
@ContextConfiguration(loader = GrepoTestContextLoaderWithDefLoc.class)
public class MethodStatisticsAspectTest extends AbstractSpringTest {

    /** The test interface. */
    @Autowired
    private TestStatisticsInterface testInterface;

    /** The statistics collection. */
    @Autowired
    private StatisticsCollection collection;

    /** Tests some invocations. */
    @Test
    public void testInvocations() {
        testInterface.statsMethod1(216L);
        testInterface.statsMethod1(430L);
        testInterface.statsMethod1(10L);

        Assert.assertEquals(1, collection.getCollectionEntries().size());

        StatisticsCollectionEntry entry = collection.get(
            TestStatisticsInterface.class.getName() + ".statsMethod1");
        Assert.assertNotNull(entry);
        Assert.assertEquals(3, entry.getStatisticsEntries().size());

        for (StatisticsEntry e : entry.getStatisticsEntries()) {
            logger.info(e.toString());
        }


    }
}
