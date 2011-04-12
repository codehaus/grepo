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

package org.codehaus.grepo.query.commons.naming;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.codehaus.grepo.query.commons.annotation.GenericQuery;
import org.codehaus.grepo.query.commons.aop.QueryMethodParameterInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Looks up named queries based on the {@link GenericQuery} annotation and the methodname.
 *
 * @author dguggi
 */
public class QueryNamingStrategyImpl implements QueryNamingStrategy {
    /** SerialVersionUid. */
    private static final long serialVersionUID = 8720409194513725656L;

    /** The logger for this class. */
    private final Logger logger = LoggerFactory.getLogger(QueryNamingStrategyImpl.class); // NOPMD

    /** The pattern to match method names. */
    private Pattern methodNamePattern;

    /** Flag to indicate wether the simple entity class name should be used for query-name generation. */
    private boolean useSimpleEntityClassName = false;

    /**
     * Default constructor.
     */
    public QueryNamingStrategyImpl() {
        super();
    }

    /**
     * @param methodNamePattern The pattern to set.
     */
    public QueryNamingStrategyImpl(Pattern methodNamePattern) {
        this.methodNamePattern = methodNamePattern;
    }

    /**
     * @param methodNamePattern The pattern to set.
     * @param useSimpleEntityClassName The flag to set.
     */
    public QueryNamingStrategyImpl(Pattern methodNamePattern, boolean useSimpleEntityClassName) {
        this.methodNamePattern = methodNamePattern;
        this.useSimpleEntityClassName = useSimpleEntityClassName;
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
                queryName = getEntityClassName(qmpi) + "." + qmpi.getMethodName().substring(m.end());
            } else {
                queryName = getEntityClassName(qmpi) + "." + qmpi.getMethodName();
            }
        } else {
            // query name was specified via annotation
            queryName = annotation.queryName();
        }

        logger.debug("Resolved named-query: {}", queryName);
        return queryName;
    }

    /**
     * @param qmpi The query method parameter info.
     * @return Returns the entity class name.
     */
    private String getEntityClassName(QueryMethodParameterInfo qmpi) {
        String retVal = null;
        if (useSimpleEntityClassName) {
            retVal = qmpi.getEntityClass().getSimpleName();
        } else {
            retVal = qmpi.getEntityClass().getName();
        }
        return retVal;
    }

    public Pattern getMethodNamePattern() {
        return methodNamePattern;
    }

    public void setMethodNamePattern(Pattern methodNamePattern) {
        this.methodNamePattern = methodNamePattern;
    }

    public boolean isUseSimpleEntityClassName() {
        return useSimpleEntityClassName;
    }

    public void setUseSimpleEntityClassName(boolean useSimpleEntityClassName) {
        this.useSimpleEntityClassName = useSimpleEntityClassName;
    }

}
