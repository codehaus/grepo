/*
 * Copyright 2011 Grepo Committers.
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

package org.codehaus.grepo.query.commons.generator;

import org.apache.commons.lang.ArrayUtils;
import org.codehaus.grepo.core.exception.ConfigurationException;
import org.codehaus.grepo.query.commons.annotation.FirstResult;
import org.codehaus.grepo.query.commons.annotation.GenericQuery;
import org.codehaus.grepo.query.commons.annotation.MaxResults;
import org.codehaus.grepo.query.commons.aop.QueryMethodParameterInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dguggi
 */
public final class GeneratorUtils {

    private static Logger logger = LoggerFactory.getLogger(GeneratorUtils.class);

    private GeneratorUtils() {
    }

    /**
     * @param clazz The clazz to check.
     * @return Returns {@code true} if the given {@code clazz} is valid and {@code false} otherwise.
     */
    public static boolean isValidQueryGenerator(Class<? extends QueryGenerator<?, ?>> clazz,
                                                Class<? extends QueryGenerator<?, ?>> expectedType) {
        if (isUserDefinedQueryGenerator(clazz)) {
            // use has specified a generator, let's check expected type...
            return expectedType.isAssignableFrom(clazz);
        }
        return false;
    }

    /**
     * @param clazz The clazz to check.
     * @param to The desired type.
     * @return Returns {@code true} if the given {@code clazz} is valid and {@code false} otherwise.
     */
    public static boolean validateQueryGenerator(Class<? extends QueryGenerator<?, ?>> clazz,
                                                 Class<? extends QueryGenerator<?, ?>>... expectedTypes) {
        if (isUserDefinedQueryGenerator(clazz)) {
            if (ArrayUtils.isNotEmpty(expectedTypes)) {
                boolean isAssignable = false;
                for (Class<? extends QueryGenerator<?, ?>> expectedType : expectedTypes) {
                    if (expectedType.isAssignableFrom(clazz)) {
                        isAssignable = true;
                        break;
                    }
                }
                if (!isAssignable) {
                    throw ConfigurationException.notAssignableException(clazz, expectedTypes);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * @param qmpi The query method parameter info.
     * @param genericQuery The generic query.
     * @return Returns the first result or {@code null}.
     */
    public static Integer getFirstResult(QueryMethodParameterInfo qmpi, GenericQuery genericQuery) {
        Integer retVal = null;
        if (genericQuery.firstResult() != -1) {
            retVal = genericQuery.firstResult();
        }

        Object firstResult = qmpi.getAnnotatedParameter(FirstResult.class);
        if (firstResult != null) {
            if (!Number.class.isAssignableFrom(firstResult.getClass())) {
                String msg =
                    String.format(
                        "Invalid @FirstResult parameter. The supplied type is not assignable to a Number (value='%s'"
                            + ", class='%s'" + ", method='%s')", firstResult, firstResult.getClass().getName(), qmpi
                            .getMethodName());
                throw new InvalidFirstResultParameterException(msg);
            }
            retVal = (Integer)firstResult;
        }

        if (retVal != null && logger.isDebugEnabled()) {
            logger.debug("Using firstResult: {}", retVal);
        }
        return retVal;
    }

    /**
     * @param qmpi The query method parameter info.
     * @param genericQuery The generic query.
     * @param defaultValue The default value.
     * @return Returns teh max results or {@code null}.
     */
    public static Integer getMaxResults(QueryMethodParameterInfo qmpi, GenericQuery genericQuery, //
                                        Integer defaultValue) {
        Integer retVal = (defaultValue == null ? null : defaultValue);
        if (genericQuery.maxResults() != -1) {
            retVal = genericQuery.maxResults();
        }

        Object maxResults = qmpi.getAnnotatedParameter(MaxResults.class);
        if (maxResults != null) {
            if (!Number.class.isAssignableFrom(maxResults.getClass())) {
                String msg =
                    String.format("Invalid @MaxResults parameter. The type is not assignable to a Number (value='%s"
                        + ", class='%s', method='%s'", maxResults, maxResults.getClass().getName(), qmpi
                        .getMethodName());
                throw new InvalidMaxResultsParameterException(msg);
            }
            retVal = ((Number)maxResults).intValue();
        }

        if (retVal != null && logger.isDebugEnabled()) {
            logger.debug("Using maxResults: {} ", retVal);
        }
        return retVal;
    }

    private static boolean isUserDefinedQueryGenerator(Class<? extends QueryGenerator<?, ?>> clazz) {
        return (clazz != null && clazz != PlaceHolderQueryGenerator.class);
    }
}
