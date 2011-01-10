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

package org.codehaus.grepo.query.commons.repository;

import java.io.Serializable;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang.time.StopWatch;
import org.codehaus.grepo.query.commons.annotation.GenericQuery;
import org.codehaus.grepo.query.commons.aop.QueryMethodParameterInfo;
import org.codehaus.grepo.query.commons.aop.QueryMethodParameterInfoImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Connects the Spring AOP magic with the Hibernate DAO magic.
 *
 * @author dguggi
 */
public class GenericQueryMethodInterceptor implements MethodInterceptor, Serializable {
    /** SerialVersionUid. */
    private static final long serialVersionUID = 4474558816402823169L;

    /** The logger for this class. */
    private final Logger logger = LoggerFactory.getLogger(GenericQueryMethodInterceptor.class); // NOPMD

    /**
     * {@inheritDoc}
     *
     * @param invocation Invokes methods dynamically.
     * @return Returns the result of the method invocation.
     * @throws Throwable in case of errors.
     */
    public Object invoke(MethodInvocation invocation) throws Throwable {
        StopWatch watch = null;
        Object result = null;
        if (logger.isDebugEnabled()) {
            watch = new StopWatch();
            watch.start();
        }

        GenericQueryRepository<?> repo = (GenericQueryRepository<?>)invocation.getThis();
        QueryMethodParameterInfo qmpi = new QueryMethodParameterInfoImpl(invocation.getMethod(), invocation
            .getArguments(), repo.getEntityClass());

        logger.debug("Invoking method '{}'", qmpi.getMethodName());

        try {
            GenericQuery annotation = qmpi.getMethodAnnotation(GenericQuery.class);
            if (annotation == null) {
                // no GenericQuery annotation present, so do not invoke via aop...
                logger.debug("Method '{}' is not annotated with @GenericQuery - "
                    + "invocation will proceed to implementation '{}'", qmpi.getMethodName(),
                        repo.getClass().getName());
                result = invocation.proceed();
            } else {
                result = repo.executeGenericQuery(qmpi, annotation);
            }
        } finally {
            if (logger.isDebugEnabled()) {
                watch.stop();
                logger.debug("Invocation of method '{}' took '{}'", qmpi.getMethodName(), watch);
            }
        }

        return result;
    }

}
