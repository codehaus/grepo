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

package org.codehaus.grepo.query.hibernate.executor;

import org.codehaus.grepo.query.commons.aop.QueryMethodParameterInfo;

/**
 * This executor is used to execute generic "load" queries.
 *
 * @author dguggi
 */
public class LoadQueryExecutor extends GetQueryExecutor {

    /** SerialVersionUid. */
    private static final long serialVersionUID = 2346247169430374822L;

    /**
     * {@inheritDoc}
     */
    public Object execute(QueryMethodParameterInfo qmpi, HibernateQueryExecutionContext context) {
        Object result = super.execute(qmpi, context);

        if (result == null) {
            String msg = String.format("Unable to load entity of type '%s' (method='%s')", qmpi.getEntityClass()
                .getName(), qmpi.getMethodName());
            throw new NoResultException(msg, qmpi.getParameters());
        }
        return result;
    }

}
