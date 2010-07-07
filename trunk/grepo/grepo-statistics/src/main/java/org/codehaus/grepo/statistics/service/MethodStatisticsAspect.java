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
import org.springframework.beans.factory.annotation.Required;

/**
 * @author dguggi
 */
@Aspect
public class MethodStatisticsAspect {

    /** The logger for this class. */
    private final Logger logger = LoggerFactory.getLogger(MethodStatisticsAspect.class);

    /** The statistics manager. */
    private StatisticsManager statisticsManager;

    /** The identifier naming strategy. */
    private StatisticsEntryIdentifierGenerationStrategy statisticsIdentifierNamingStrategy;

    /**
     * @param pjp The join point.
     * @param annotation The annotation.
     * @return Returns the result.
     * @throws Throwable in case of erros.
     */
    @Around("@annotation(org.codehaus.grepo.statistics.annotation.MethodStatistics) && @annotation(annotation)")
    public Object methodStatistics(ProceedingJoinPoint pjp, MethodStatistics annotation) throws Throwable {
        StatisticsEntry entry = createEntry(pjp);
        try {
            Object result = pjp.proceed();
            return result;
        } finally {
            completeEntry(entry);
        }
    }

    /**
     * @param pjp The proceeding join point.
     * @return Returns the entry.
     */
    private StatisticsEntry createEntry(ProceedingJoinPoint pjp) {
        StatisticsEntry entry = null;
        try {
            MethodSignature methodSig = (MethodSignature)pjp.getSignature();
            Method method = methodSig.getMethod();
            MethodParameterInfo mpi = new MethodParameterInfoImpl(
                method, pjp.getArgs());

            String identifier = statisticsIdentifierNamingStrategy.getIdentifier(mpi);


            entry = statisticsManager.createStatisticsEntry(identifier);
        } catch (Exception e) {
            logger.error("Unable to create StatisticsEntry: " + e.getMessage(), e);
        }
        return entry;
    }

    /**
     * @param entry The entry to complete.
     */
    private void completeEntry(StatisticsEntry entry) {
        if (entry != null) {
            try {
                statisticsManager.completeStatisticsEntry(entry);
            } catch (Exception e) {
                logger.warn("Unable to complete StatisticsEntry: " + e.getMessage());
            }
        }
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
