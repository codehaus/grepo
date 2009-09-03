/*
 * Copyright (c) 2007 Daniel Guggi.
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


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.grepo.query.commons.aop.QueryMethodParameterInfo;

/**
 * Default strategy for resolving names of {@link QueryExecutor}s based on a method-name pattern.
 *
 * @author dguggi
 */
public class DefaultQueryExecutorNamingStrategy implements QueryExecutorNamingStrategy {
    /** The logger for this class. */
    private static final Log LOG = LogFactory.getLog(DefaultQueryExecutorNamingStrategy.class);

    /** The pattern. */
    private Pattern pattern;

    /**
     * Default constructor.
     */
    public DefaultQueryExecutorNamingStrategy() {
        super();
    }

    /**
     * @param pattern The pattern to set.
     */
    public DefaultQueryExecutorNamingStrategy(Pattern pattern) {
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

        if (LOG.isTraceEnabled()) {
            LOG.trace("Resolved executor name: " + result);
        }

        return result;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

}
