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

package demo.service;

import org.codehaus.grepo.statistics.collection.SimpleStatisticsCollectionPrinter;
import org.codehaus.grepo.statistics.collection.StatisticsCollection;
import org.codehaus.grepo.statistics.collection.StatisticsCollectionEntryComparator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

/**
 * Tests the {@link UserRepository}.
 *
 * @author dguggi
 */
@SuppressWarnings("PMD")
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class })
@ContextConfiguration(locations = "classpath:META-INF/spring/application-context.xml")
public class MyServiceTest {

    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /** The service to test. */
    @Autowired
    private MyService myService;

    /** The collection printer. */
    @Autowired
    private SimpleStatisticsCollectionPrinter collectionPrinter;

    /** The statistics collection. */
    @Autowired
    private StatisticsCollection statisticsCollection;

    /** Setup. */
    @Before
    public void before() {
        // clear statistics...
        statisticsCollection.clear();
    }

    /**
     * Tests {@link MyService#doSomething1()}.
     */
    @Test
    public void testDoSomething1() {
        for (int i = 0; i < 10; i++) {
            myService.doSomething1();
        }

        // print summary...
        logger.info("\n" + collectionPrinter.printSummary(true));
        // print detail for identifier of doSomething1...
        logger.info("\n" + collectionPrinter.printDetail("demo.service.MyService.doSomething1"));
    }

    /**
     * Tests {@link MyService#doSomething2()}.
     */
    @Test
    public void testDoSomething2() {
        for (int i = 0; i < 10; i++) {
            myService.doSomething2();
        }

        // print summary...
        logger.info("\n" + collectionPrinter.printSummary(true));
        // print detail for identifier of doSomething2...
        logger.info("\n" + collectionPrinter.printDetail("customIdentifierForDoSomething2"));
    }

    /** Tests several invokations of {@code doSomething1} and {@code doSomething2}. */
    @Test
    public void testSeveralInvokations() {
        for (int i = 0; i < 7; i++) {
            myService.doSomething1();
        }
        for (int i = 0; i < 10; i++) {
            myService.doSomething2();
        }
        // print summary...
        logger.info("\n" + collectionPrinter.printSummary(true, StatisticsCollectionEntryComparator.MAX_DURATION_DESC));
    }
}
