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

import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.codehaus.grepo.query.commons.annotation.GenericQuery;
import org.codehaus.grepo.query.commons.aop.QueryMethodParameterInfo;
import org.codehaus.grepo.query.jpa.context.JpaQueryExecutionContext;

/**
 * @author dguggi
 */
public final class DefaultQueryGenerator extends QueryGeneratorBase {

    private static final long serialVersionUID = 5140350252527622671L;

    /**
     * {@inheritDoc}
     */
    @Override
    protected Query createQuery(QueryMethodParameterInfo qmpi, JpaQueryExecutionContext context) {
        GenericQuery genericQuery = qmpi.getMethodAnnotation(GenericQuery.class);

        final Query query;
        if (StringUtils.isNotEmpty(genericQuery.query())) {
            if (isNativeQuery(genericQuery)) {
                query = createNativeQuery(genericQuery.query(), qmpi, context);
            } else {
                query = context.getEntityManager().createQuery(genericQuery.query());
            }
        } else {
            String queryName = context.getQueryNamingStrategy().getQueryName(qmpi);
            query = context.getEntityManager().createNamedQuery(queryName);
        }

        applyQueryParameters(qmpi, context, query);
        return query;
    }

}
