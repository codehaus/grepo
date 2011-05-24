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

package org.codehaus.grepo.query.jpa.generator;

import org.apache.commons.lang.StringUtils;
import org.codehaus.grepo.query.commons.annotation.GenericQuery;
import org.codehaus.grepo.query.jpa.annotation.JpaQueryOptions;

/**
 * @author dguggi
 */
public abstract class AbstractNativeQueryGenerator extends AbstractQueryGenerator {

    private static final long serialVersionUID = -3322881922411313223L;

    private Class<?> resultClass;

    private String resultSetMapping;

    /**
     * {@inheritDoc}
     */
    @Override
    protected final boolean isNativeQuery(GenericQuery genericQuery) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<?> getResultClass(JpaQueryOptions queryOptions) {
        if (resultClass != null) {
            return resultClass;
        }
        return super.getResultClass(queryOptions);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getResultSetMapping(JpaQueryOptions queryOptions) {
        if (StringUtils.isNotEmpty(resultSetMapping)) {
            return resultSetMapping;
        }
        return super.getResultSetMapping(queryOptions);
    }

    protected void setResultClass(Class<?> resultClass) {
        this.resultClass = resultClass;
    }

    protected void setResultSetMapping(String resultSetMapping) {
        this.resultSetMapping = resultSetMapping;
    }

}
