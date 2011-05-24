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

package org.codehaus.grepo.query.hibernate.executor; //NOPMD

import org.codehaus.grepo.core.util.ClassUtils;
import org.codehaus.grepo.query.commons.annotation.GenericQuery;
import org.codehaus.grepo.query.commons.aop.QueryMethodParameterInfo;
import org.codehaus.grepo.query.hibernate.context.HibernateQueryExecutionContext;
import org.codehaus.grepo.query.hibernate.generator.DefaultQueryGenerator;
import org.codehaus.grepo.query.hibernate.generator.HibernateCriteriaGenerator;
import org.codehaus.grepo.query.hibernate.generator.HibernateGeneratorUtils;
import org.codehaus.grepo.query.hibernate.generator.HibernateQueryGenerator;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dguggi
 */
public abstract class AbstractHibernateQueryExecutor implements HibernateQueryExecutor {

    private static final long serialVersionUID = 1138696235036750544L;

    private static final Logger logger = LoggerFactory.getLogger(AbstractHibernateQueryExecutor.class);

    /**
     * Creates a criteria using a {@link HibernateCriteriaGenerator} is one is specified.
     *
     * @param qmpi The query method parameter info.
     * @param context The context.
     * @return Returns the criteria.
     */
    protected Criteria createCriteria(QueryMethodParameterInfo qmpi, HibernateQueryExecutionContext context) {
        GenericQuery genericQuery = qmpi.getMethodAnnotation(GenericQuery.class);
        if (HibernateGeneratorUtils.hasValidCriteriaGenerator(genericQuery)) {
            @SuppressWarnings("unchecked")
            Class<? extends HibernateCriteriaGenerator> clazz =
                (Class<? extends HibernateCriteriaGenerator>)genericQuery.queryGenerator();
            HibernateCriteriaGenerator generator = ClassUtils.instantiateClass(clazz);
            logger.info("Generating criteria using criteriaGenerator: {}", clazz.getName());
            return generator.generate(qmpi, context);
        }
        return null;
    }

    /**
     * Creates a query using a {@link HibernateQueryGenerator}. If none is specified the {@link DefaultQueryGenerator}
     * will be used in order to generate a query.
     *
     * @param qmpi The query method parameter info.
     * @param context The context.
     * @return Returns the query.
     */
    protected Query createQuery(QueryMethodParameterInfo qmpi, HibernateQueryExecutionContext context) {
        GenericQuery genericQuery = qmpi.getMethodAnnotation(GenericQuery.class);

        if (HibernateGeneratorUtils.hasValidQueryGenerator(genericQuery)) {
            @SuppressWarnings("unchecked")
            Class<? extends HibernateQueryGenerator> clazz =
                (Class<? extends HibernateQueryGenerator>)genericQuery.queryGenerator();
            HibernateQueryGenerator generator = ClassUtils.instantiateClass(clazz);
            logger.info("Generating query using queryGenerator: {}", clazz.getSimpleName());
            return generator.generate(qmpi, context);
        }
        return new DefaultQueryGenerator().generate(qmpi, context);
    }

}
