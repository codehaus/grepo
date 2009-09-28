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

package org.codehaus.grepo.procedure.repository;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.grepo.procedure.annotation.GenericProcedure;
import org.codehaus.grepo.procedure.aop.ProcedureMethodParameterInfo;
import org.codehaus.grepo.procedure.aop.ProcedureMethodParameterInfoImpl;

/**
 * Intercepts method calls of methods which are annotated with {@link GenericProcedure}.
 *
 * @author dguggi
 */
public class GenericProcedureMethodInterceptor implements MethodInterceptor {

    /** The logger for this class. */
    private static final Log LOG = LogFactory.getLog(GenericProcedureMethodInterceptor.class);

    /**
     * {@inheritDoc}
     */
    public Object invoke(MethodInvocation invocation) throws Throwable {
        StopWatch watch = null;
        Object result = null;

        GenericProcedureRepository executor = (GenericProcedureRepository)invocation.getThis();
        ProcedureMethodParameterInfo pmpi = new ProcedureMethodParameterInfoImpl(invocation.getMethod(), invocation
            .getArguments());

        if (LOG.isTraceEnabled()) {
            watch = new StopWatch();
            watch.start();
            LOG.trace(String.format("Invoking method '%s'", pmpi.getMethodName()));
        }

        try {
            GenericProcedure annotation = pmpi.getMethodAnnotation(GenericProcedure.class);
            if (annotation == null) {
                // no GenericProcedure annotation present, so do not invoke via aop...
                if (LOG.isTraceEnabled()) {
                    String msg = String.format("Method '%s' is not annotated with @GenericQuery"
                        + " - invocation will proceed to implementation '%s'",
                        pmpi.getMethodName(), executor.getClass().getName());
                    LOG.trace(msg);
                }
                result = invocation.proceed();
            } else {
                result = executor.execute(pmpi, annotation);
            }
        } finally {
            if (LOG.isTraceEnabled()) {
                watch.stop();
                LOG.trace(String.format("Invocation of method '%s' took '%s'", pmpi.getMethodName(), watch));
            }
        }

        return result;
    }

}
