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

import org.codehaus.grepo.statistics.annotation.MethodStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author dguggi
 */
@Component
public class TestStatisticsInterfaceImpl implements TestStatisticsInterface {

    /** The logger for this class. */
    private final Logger logger = LoggerFactory.getLogger(TestStatisticsInterfaceImpl.class); // NOPMD

    /**
     * {@inheritDoc}
     */
    @MethodStatistics
    public void statsMethod1(long sleepTime) {
        if (sleepTime > 0) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

}
