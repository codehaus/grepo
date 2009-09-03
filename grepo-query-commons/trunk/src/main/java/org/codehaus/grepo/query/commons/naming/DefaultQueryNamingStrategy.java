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

package org.codehaus.grepo.query.commons.naming;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.grepo.query.commons.annotation.GenericQuery;
import org.codehaus.grepo.query.commons.aop.QueryMethodParameterInfo;

/**
 * Looks up named queries based on the {@link GenericQuery} annotation and the methodname.
 *
 * @author dguggi
 */
public class DefaultQueryNamingStrategy implements QueryNamingStrategy {

    /** The logger for this class. */
    private static final Log LOG = LogFactory.getLog(DefaultQueryNamingStrategy.class);

    /** The pattern to match method names. */
    private Pattern methodNamePattern;

    /**
     * Default constructor.
     */
    public DefaultQueryNamingStrategy() {
        super();
    }

    /**
     * @param methodNamePattern The pattern to set.
     */
    public DefaultQueryNamingStrategy(Pattern methodNamePattern) {
        super();
        this.methodNamePattern = methodNamePattern;
    }

    /**
     * {@inheritDoc}
     */
    public String getQueryName(QueryMethodParameterInfo qmpi) {
        String queryName = null;

        GenericQuery annotation = qmpi.getMethodAnnotation(GenericQuery.class);
        if (StringUtils.isEmpty(annotation.queryName())) {
            // no query name was specified via annotation...
            Matcher m = methodNamePattern.matcher(qmpi.getMethodName());
            if (m.find()) {
                queryName = qmpi.getEntityType().getName() + "." + qmpi.getMethodName().substring(m.end());
            } else {
                queryName = qmpi.getEntityType().getName() + "." + qmpi.getMethodName();
            }
        } else {
            // query name was specified via annotation
            queryName = annotation.queryName();
        }

        if (LOG.isTraceEnabled()) {
            LOG.trace("Resolved hibernate named-query: " + queryName);
        }

        return queryName;
    }

    public Pattern getMethodNamePattern() {
        return methodNamePattern;
    }

    public void setMethodNamePattern(Pattern methodNamePattern) {
        this.methodNamePattern = methodNamePattern;
    }

}
