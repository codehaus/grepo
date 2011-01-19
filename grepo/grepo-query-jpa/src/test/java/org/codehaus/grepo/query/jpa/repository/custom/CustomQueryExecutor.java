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

package org.codehaus.grepo.query.jpa.repository.custom;

import org.codehaus.grepo.query.commons.aop.QueryMethodParameterInfo;
import org.codehaus.grepo.query.jpa.TestEntity;
import org.codehaus.grepo.query.jpa.executor.AbstractJpaQueryExecutor;
import org.codehaus.grepo.query.jpa.executor.JpaQueryExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dguggi
 */
public class CustomQueryExecutor extends AbstractJpaQueryExecutor {

    /** SerialVersionUid. */
    private static final long serialVersionUID = 4115267235854411243L;

    /** The logger for this class. */
    private final Logger logger = LoggerFactory.getLogger(CustomQueryExecutor.class); // NOPMD

    /**
     * {@inheritDoc}
     */
    public Object execute(QueryMethodParameterInfo qmpi, JpaQueryExecutionContext context) {
        logger.info("doing something special");
        Long id = qmpi.getParameterByParamName("id", Long.class);
        return context.getEntityManager().find(TestEntity.class, id);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isReadOnlyOperation() {
        return false;
    }

}
