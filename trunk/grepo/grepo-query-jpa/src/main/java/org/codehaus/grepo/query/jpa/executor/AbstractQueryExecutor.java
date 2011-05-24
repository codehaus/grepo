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

import javax.persistence.Query;

import org.codehaus.grepo.core.util.ClassUtils;
import org.codehaus.grepo.query.commons.annotation.GenericQuery;
import org.codehaus.grepo.query.commons.aop.QueryMethodParameterInfo;
import org.codehaus.grepo.query.jpa.context.JpaQueryExecutionContext;
import org.codehaus.grepo.query.jpa.generator.DefaultQueryGenerator;
import org.codehaus.grepo.query.jpa.generator.JpaGeneratorUtils;
import org.codehaus.grepo.query.jpa.generator.JpaQueryGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract jpa query executor.
 *
 * @author dguggi
 */
public abstract class AbstractQueryExecutor implements JpaQueryExecutor {

    private static final Logger logger = LoggerFactory.getLogger(AbstractQueryExecutor.class);

    /**
     * Creates a query using a {@link JpaQueryGenerator}. If none is specified the {@link DefaultQueryGenerator}
     * will be used in order to generate a query.
     *
     * @param qmpi The query method parameter info.
     * @param context The context.
     * @return Returns the query.
     */
    protected Query createQuery(QueryMethodParameterInfo qmpi, JpaQueryExecutionContext context) {
        GenericQuery genericQuery = qmpi.getMethodAnnotation(GenericQuery.class);
        if (JpaGeneratorUtils.hasValidQueryGenerator(genericQuery)) {
            @SuppressWarnings("unchecked")
            Class<? extends JpaQueryGenerator> clazz =
                (Class<? extends JpaQueryGenerator>)genericQuery.queryGenerator();
            JpaQueryGenerator generator = ClassUtils.instantiateClass(clazz);
            logger.info("Generating query using queryGenerator: {}", clazz.getName());
            return generator.generate(qmpi, context);
        }
        return createDefaultQueryGenerator().generate(qmpi, context);
    }

    protected JpaQueryGenerator createDefaultQueryGenerator() {
        return new DefaultQueryGenerator();
    }

}
