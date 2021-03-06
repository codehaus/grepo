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

package org.codehaus.grepo.query.hibernate.generator;

import org.codehaus.grepo.query.commons.aop.QueryMethodParameterInfo;
import org.codehaus.grepo.query.hibernate.TestEntity;
import org.codehaus.grepo.query.hibernate.context.HibernateQueryExecutionContext;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

/**
 * @author dguggi
 */
public class TestCriteriaGenerator extends CriteriaGeneratorBase {

    private static final long serialVersionUID = 7433111191916322566L;

    /**
     * {@inheritDoc}
     */
    @Override
    protected CriteriaSpecification createCriteria(QueryMethodParameterInfo qmpi, //
                                                   HibernateQueryExecutionContext context) {
        String[] usernames = (String[])qmpi.getParameterByParamName("usernames");
        DetachedCriteria criteria = DetachedCriteria.forClass(TestEntity.class);
        criteria.add(Restrictions.in("username", usernames));
        return criteria;
    }

}
