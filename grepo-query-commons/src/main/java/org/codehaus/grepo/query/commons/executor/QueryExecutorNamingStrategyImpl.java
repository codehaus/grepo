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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.grepo.query.commons.aop.QueryMethodParameterInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default strategy for resolving names of {@link QueryExecutor}s based on a method-name pattern.
 *
 * @author dguggi
 */
public class QueryExecutorNamingStrategyImpl implements QueryExecutorNamingStrategy {
    /** The logger for this class. */
    private final Logger logger = LoggerFactory.getLogger(QueryExecutorNamingStrategyImpl.class); // NOPMD

    /** The pattern. */
    private Pattern pattern;

    /**
     * Default constructor.
     */
    public QueryExecutorNamingStrategyImpl() {
        super();
    }

    /**
     * @param pattern The pattern to set.
     */
    public QueryExecutorNamingStrategyImpl(Pattern pattern) {
        this.pattern = pattern;
    }

    /**
     * {@inheritDoc}
     */
    public String getExecutorName(QueryMethodParameterInfo qmpi) {
        String result = null;
        Matcher matcher = pattern.matcher(qmpi.getMethodName());
        if (matcher.find()) {
            result = matcher.group();
        }

        logger.debug("Resolved executor name: {}", result);
        return result;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

}
