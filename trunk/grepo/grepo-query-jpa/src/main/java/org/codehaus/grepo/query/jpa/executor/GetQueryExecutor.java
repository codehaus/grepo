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

package org.codehaus.grepo.query.jpa.executor;

import java.util.List;

import javax.persistence.NonUniqueResultException;

import org.codehaus.grepo.query.commons.aop.QueryMethodParameterInfo;
import org.codehaus.grepo.query.jpa.context.JpaQueryExecutionContext;

/**
 * @author dguggi
 */
public class GetQueryExecutor extends ListQueryExecutor {

    private static final long serialVersionUID = 1363362435873145991L;

    /**
     * {@inheritDoc}
     */
    public Object execute(QueryMethodParameterInfo qmpi, JpaQueryExecutionContext context) {
        @SuppressWarnings("unchecked")
        List<Object> list = (List<Object>)super.execute(qmpi, context);
        if (list.size() == 0) {
            return null;
        } else if (list.size() == 1) {
            return list.get(0);
        } else {
            String msg = "Too many rows found: " + list.size();
            throw new NonUniqueResultException(msg);
        }
    }
}
