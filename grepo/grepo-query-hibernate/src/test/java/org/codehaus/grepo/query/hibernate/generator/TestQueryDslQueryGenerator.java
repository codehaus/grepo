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

package org.codehaus.grepo.query.hibernate.generator;

import org.codehaus.grepo.query.commons.aop.QueryMethodParameterInfo;
import org.codehaus.grepo.query.hibernate.QTestEntity;
import org.codehaus.grepo.query.hibernate.context.HibernateQueryExecutionContext;
import org.hibernate.Query;

import com.mysema.query.jpa.hibernate.HibernateQuery;

/**
 * @author dguggi
 */
public class TestQueryDslQueryGenerator extends QueryGeneratorBase {

    private static final long serialVersionUID = 4486421659339255586L;

    /**
     * {@inheritDoc}
     */
    @Override
    protected Query createQuery(QueryMethodParameterInfo qmpi, HibernateQueryExecutionContext context) {
        String firstname = qmpi.getParameterByParamName("firstname", String.class);
        QTestEntity entity = QTestEntity.testEntity;

        return new HibernateQuery(context.getSession()).from(entity) //
            .where(entity.firstname.eq(firstname)).createQuery(entity);
    }

}
