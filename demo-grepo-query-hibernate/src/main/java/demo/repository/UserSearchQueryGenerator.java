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

package demo.repository;

import org.apache.commons.lang.StringUtils;
import org.codehaus.grepo.query.commons.aop.QueryMethodParameterInfo;
import org.codehaus.grepo.query.hibernate.context.HibernateQueryExecutionContext;
import org.codehaus.grepo.query.hibernate.generator.AbstractQueryGenerator;

/**
 * @author dguggi
 */
public class UserSearchQueryGenerator extends AbstractQueryGenerator {

    private static final long serialVersionUID = -4836138440579849746L;

    /**
     * {@inheritDoc}
     */
    @Override
    protected String createQueryString(QueryMethodParameterInfo qmpi, HibernateQueryExecutionContext context) {
        String firstname = qmpi.getParameterByParamName("fn", String.class);
        String lastname = qmpi.getParameterByParamName("ln", String.class);

        StringBuilder query = new StringBuilder("from User where 1=1");
        if (StringUtils.isNotEmpty(firstname)) {
            query.append(" AND firstname = :fn");
        }
        if (StringUtils.isNotEmpty(lastname)) {
            query.append(" AND lastname = :ln");
        }

        return query.toString();
    }

}
