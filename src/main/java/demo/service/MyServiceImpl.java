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

import java.util.Random;

import org.codehaus.grepo.statistics.annotation.MethodStatistics;
import org.codehaus.grepo.statistics.domain.StatisticsEntry;
import org.codehaus.grepo.statistics.service.StatisticsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author dguggi
 */
@Service
public class MyServiceImpl implements MyService {

    /** The logger for this class. */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /** The statistics manager. */
    private StatisticsManager statisticsManager;

    /** Random in order to simulate execution durations. */
    private Random random = new Random();

    /**
     * This method uses grepo's {@code MethodStatistics} annotation in order to track method-statistics.
     */
    @MethodStatistics
    public void doSomething1() {
        sleepRandomMillis();
    }

    /**
     * This method used the {@code statisticsManager} directly.
     */
    public void doSomething2() {
        StatisticsEntry entry = null;
        try {
            entry = statisticsManager.createStatisticsEntry("customIdentifierForDoSomething2");
            // business logic here...
            sleepRandomMillis();
        } finally {
            statisticsManager.completeStatisticsEntry(entry);
        }
    }

    @Autowired
    public void setStatisticsManager(StatisticsManager statisticsManager) {
        this.statisticsManager = statisticsManager;
    }


    /**
     * Thread sleeps 0 - 500 milliseconds.
     */
    private void sleepRandomMillis() {
        int sleepTime = random.nextInt(500);
        try {
            if (sleepTime > 0) {
                Thread.sleep(Integer.valueOf(sleepTime).longValue());
            }
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
