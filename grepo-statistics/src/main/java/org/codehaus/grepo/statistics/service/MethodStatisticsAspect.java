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

import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.codehaus.grepo.core.aop.MethodParameterInfo;
import org.codehaus.grepo.core.aop.MethodParameterInfoImpl;
import org.codehaus.grepo.statistics.annotation.MethodStatistics;
import org.codehaus.grepo.statistics.domain.StatisticsEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author dguggi
 */
@Aspect
public class MethodStatisticsAspect implements ApplicationContextAware {

    /** The logger for this class. */
    private final Logger logger = LoggerFactory.getLogger(MethodStatisticsAspect.class); // NOPMD

    /** The statistics manager. */
    private StatisticsManager statisticsManager; // NOPMD

    /** The identifier naming strategy. */
    private StatisticsEntryIdentifierGenerationStrategy statisticsIdentifierNamingStrategy; // NOPMD

    /** The application context. */
    private ApplicationContext applicationContext; // NOPMD

    /**
     * @param pjp The join point.
     * @param annotation The annotation.
     * @return Returns the result.
     * @throws Throwable in case of erros.
     */
    @Around("@annotation(org.codehaus.grepo.statistics.annotation.MethodStatistics) && @annotation(annotation)")
    public Object methodStatistics(ProceedingJoinPoint pjp, MethodStatistics annotation) throws Throwable {
        StatisticsEntry entry = createEntry(pjp, annotation.manager(), annotation.origin());
        try {
            return pjp.proceed();
        } finally {
            completeEntry(entry, annotation.manager());
        }
    }

    /**
     * @param pjp The proceeding join point.
     * @param managerName The statistics manager name to use.
     * @param origin The origin.
     * @return Returns the entry.
     */
    private StatisticsEntry createEntry(ProceedingJoinPoint pjp, String managerName, String origin) {
        StatisticsEntry entry = null;
        try {
            MethodSignature methodSig = (MethodSignature)pjp.getSignature();
            Method method = methodSig.getMethod();
            MethodParameterInfo mpi = new MethodParameterInfoImpl(
                method, pjp.getArgs());

            String identifier = statisticsIdentifierNamingStrategy.getIdentifier(mpi);

            entry = getStatisticsManager(managerName).createStatisticsEntry(identifier, origin);

        } catch (Exception e) {
            logger.error("Unable to create StatisticsEntry: " + e.getMessage(), e);
        }
        return entry;
    }

    /**
     * @param entry The entry to complete.
     * @param managerName The statistics manager name to use.
     */
    private void completeEntry(StatisticsEntry entry, String managerName) {
        if (entry != null) {
            try {
                getStatisticsManager(managerName).completeStatisticsEntry(entry);
            } catch (Exception e) {
                logger.warn("Unable to complete StatisticsEntry: " + e.getMessage());
            }
        }
    }

    /**
     * @param managerName The name of the manager to use.
     * @return Returns the {@link StatisticsManager} instance to use.
     */
    protected StatisticsManager getStatisticsManager(String managerName) {
        if (StringUtils.isNotEmpty(managerName)) {
            // use statistics manager configured via annotation...
            return (StatisticsManager) applicationContext.getBean(managerName, StatisticsManager.class);
        } else {
            // use default statistics manager...
            return statisticsManager;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Required
    public void setStatisticsManager(StatisticsManager statisticsManager) {
        this.statisticsManager = statisticsManager;
    }

    @Required
    public void setStatisticsIdentifierNamingStrategy(
            StatisticsEntryIdentifierGenerationStrategy statisticsIdentifierNamingStrategy) {
        this.statisticsIdentifierNamingStrategy = statisticsIdentifierNamingStrategy;
    }


}