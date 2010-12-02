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

import org.apache.commons.lang.StringUtils;
import org.codehaus.grepo.core.aop.MethodParameterInfo;
import org.codehaus.grepo.statistics.annotation.MethodStatistics;

/**
 * @author dguggi
 */
public class StatisticsEntryIdentifierGenerationStrategyImpl implements StatisticsEntryIdentifierGenerationStrategy {

    /**
     * {@inheritDoc}
     */
    public String getIdentifier(MethodParameterInfo mpi, MethodStatistics annotation) {
        String result = null;

        MethodStatistics annotationToUse = null;
        if (annotation == null) {
            // try to resolve annotation via mpi...
            annotationToUse = mpi.getMethodAnnotation(MethodStatistics.class);
        } else {
            annotationToUse = annotation;
        }

        if (annotationToUse != null && StringUtils.isNotEmpty(annotationToUse.identifier())) {
            result = annotationToUse.identifier();
        } else {
            result = generateIdentifier(mpi);
        }

        return result;
    }

    /**
     * @param mpi The method parameter info.
     * @return Returns the generated identifier.
     */
    protected String generateIdentifier(MethodParameterInfo mpi) {
        return mpi.getDeclaringClass().getName() + "." + mpi.getMethodName();
    }
}
