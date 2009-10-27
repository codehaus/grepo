/*
 * Copyright 2009 Grepo Committers.
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

package org.codehaus.grepo.query.commons.executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.grepo.core.exception.ConfigurationException;
import org.codehaus.grepo.query.commons.annotation.FirstResult;
import org.codehaus.grepo.query.commons.annotation.GenericQuery;
import org.codehaus.grepo.query.commons.annotation.MaxResults;
import org.codehaus.grepo.query.commons.aop.QueryMethodParameterInfo;
import org.codehaus.grepo.query.commons.generator.PlaceHolderQueryGenerator;
import org.codehaus.grepo.query.commons.generator.QueryGenerator;
import org.codehaus.grepo.query.commons.naming.QueryNamingStrategy;

/**
 * Abstract base class which may be used for query executor implementations.
 *
 * @author dguggi
 * @param <T> The type.
 */
public abstract class AbstractQueryExecutor<T extends QueryExecutionContext> implements QueryExecutor<T> {

    /** The logger for this class. */
    private static final Log LOG = LogFactory.getLog(AbstractQueryExecutor.class);

    /** The query naming strategy. */
    private QueryNamingStrategy queryNamingStrategy;

    public void setQueryNamingStrategy(QueryNamingStrategy queryNamingStrategy) {
        this.queryNamingStrategy = queryNamingStrategy;
    }

    protected QueryNamingStrategy getQueryNamingStrategy() {
        return queryNamingStrategy;
    }

    /**
     * Returns the first-result parameter (if there is one configured).
     *
     * @param qmpi The query method parameter info.
     * @param genericQuery The {@link GenericQuery} annotation.
     * @return Returns the first-result or null.
     */
    protected Integer getFirstResult(QueryMethodParameterInfo qmpi, GenericQuery genericQuery) {
        Integer retVal = null;
        if (genericQuery.firstResult() != -1) {
            retVal = genericQuery.firstResult();
        }

        Object firstResult = qmpi.getAnnotatedParameter(FirstResult.class);
        if (firstResult != null) {
            if (!Number.class.isAssignableFrom(firstResult.getClass())) {
                String msg = String.format(
                    "Invalid @FirstResult parameter. The supplied type is not assignable to a Number (value='%s'"
                        + ", class='%s'" + ", method='%s')", firstResult, firstResult.getClass().getName(), qmpi
                        .getMethodName());
                throw new InvalidFirstResultParameterException(msg);
            }
            retVal = (Integer)firstResult;
        }

        if (retVal != null && LOG.isTraceEnabled()) {
            LOG.trace("Found firstResult parameter: " + retVal);
        }

        return retVal;
    }

    /**
     * Returns the max-results parameter (if there is one configured).
     *
     * @param qmpi The query method parameter info.
     * @param genericQuery The {@link GenericQuery} annotation.
     * @return Returns the max-results or null.
     */
    protected Integer getMaxResults(QueryMethodParameterInfo qmpi, GenericQuery genericQuery) {
        Integer retVal = null;
        if (genericQuery.maxResults() != -1) {
            retVal = genericQuery.maxResults();
        }

        Object maxResults = qmpi.getAnnotatedParameter(MaxResults.class);
        if (maxResults != null) {
            if (!Number.class.isAssignableFrom(maxResults.getClass())) {
                String msg = String.format(
                    "Invalid @MaxResults parameter. The type is not assignable to a Number (value='%s"
                        + ", class='%s', method='%s'", maxResults, maxResults.getClass().getName(), qmpi
                        .getMethodName());
                throw new InvalidMaxResultsParameterException(msg);
            }
            retVal = ((Number)maxResults).intValue();
        }

        if (retVal != null && LOG.isTraceEnabled()) {
            LOG.trace("Found maxResults parameter: " + retVal);
        }

        return retVal;
    }

    /**
     * @param clazz The clazz to check.
     * @return Returns {@code true} if the given {@code clazz} is valid and {@code false} otherwise.
     */
    protected static boolean isValidQueryGenerator(Class<? extends QueryGenerator<?>> clazz) {
        return (clazz != null && clazz != PlaceHolderQueryGenerator.class);
    }

    /**
     * @param clazz The clazz to check.
     * @param to The desired type.
     * @return Returns {@code true} if the given {@code clazz} is valid and {@code false} otherwise.
     */
    protected static boolean validateQueryGenerator(Class<? extends QueryGenerator<?>> clazz,
            Class<? extends QueryGenerator<?>> to) {
        boolean retVal = false;
        if (isValidQueryGenerator(clazz)) {
            // user specified generator...
            if (to.isAssignableFrom(clazz)) {
                retVal = true;
            } else {
                ConfigurationException.notAssignableException(clazz, to);
            }
        }
        return retVal;
    }
}
