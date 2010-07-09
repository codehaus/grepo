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

import org.codehaus.grepo.core.AbstractSpringTest;
import org.codehaus.grepo.core.context.GrepoTestContextLoaderWithDefLoc;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author dguggi
 */
@ContextConfiguration(loader = GrepoTestContextLoaderWithDefLoc.class)
public class SimpleStatisticsCollectionPrinterTest extends AbstractSpringTest {

    /** The printer to test. */
    @Autowired
    private SimpleStatisticsCollectionPrinter printer;

    /** The test interface. */
    @Autowired
    private TestStatisticsInterface testInterface;

    /** Test print summary. */
    @Test
    public void testPrintSummary() {
        for (int i = 0; i < 50; i++) {
            testInterface.statsMethod1(i);
        }
        logger.info(printer.printSummary(true));
    }

    /** Test print detail. */
    @Test
    public void testPrintDetail() {
        for (int i = 0; i < 50; i++) {
            testInterface.statsMethod1(i);
        }
        logger.info(printer.printDetail(TestStatisticsInterface.class.getName() + ".statsMethod1"));
    }
}
