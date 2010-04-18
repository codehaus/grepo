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

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.grepo.query.commons.annotation.GenericQuery;
import org.codehaus.grepo.query.commons.aop.QueryMethodParameterInfo;
import org.codehaus.grepo.query.commons.aop.QueryMethodParameterInfoImpl;

/**
 * Connects the Spring AOP magic with the Hibernate DAO magic.
 *
 * @author dguggi
 */
public class GenericQueryMethodInterceptor implements MethodInterceptor {

    /** The logger for this class. */
    private static final Log LOG = LogFactory.getLog(GenericQueryMethodInterceptor.class);

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
        if (LOG.isTraceEnabled()) {
            watch = new StopWatch();
            watch.start();
        }

        GenericQueryRepository<?> repo = (GenericQueryRepository<?>)invocation.getThis();
        QueryMethodParameterInfo qmpi = new QueryMethodParameterInfoImpl(invocation.getMethod(), invocation
            .getArguments(), repo.getEntityClass());

        if (LOG.isTraceEnabled()) {
            LOG.trace(String.format("Invoking method '%s'", qmpi.getMethodName()));
        }

        try {
            GenericQuery annotation = qmpi.getMethodAnnotation(GenericQuery.class);
            if (annotation == null) {
                // no GenericQuery annotation present, so do not invoke via aop...
                if (LOG.isTraceEnabled()) {
                    String msg = String.format("Method '%s' is not annotated with @GenericQuery"
                        + " - invocation will proceed to implementation '%s'", qmpi.getMethodName(), repo
                        .getClass().getName());
                    LOG.trace(msg);
                }
                result = invocation.proceed();
            } else {
                result = repo.executeGenericQuery(qmpi, annotation);
            }
        } finally {
            if (LOG.isTraceEnabled()) {
                watch.stop();
                LOG.trace(String.format("Invocation of method '%s' took '%s'", qmpi.getMethodName(), watch));
            }
        }

        return result;
    }

}
