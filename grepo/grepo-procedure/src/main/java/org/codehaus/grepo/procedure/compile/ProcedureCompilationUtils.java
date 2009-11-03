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

package org.codehaus.grepo.procedure.compile;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.grepo.core.exception.ConfigurationException;
import org.codehaus.grepo.core.util.ClassUtils;
import org.codehaus.grepo.procedure.annotation.In;
import org.codehaus.grepo.procedure.annotation.InOut;
import org.codehaus.grepo.procedure.annotation.InOutParams;
import org.codehaus.grepo.procedure.annotation.InParams;
import org.codehaus.grepo.procedure.annotation.Out;
import org.codehaus.grepo.procedure.annotation.OutParams;
import org.codehaus.grepo.procedure.annotation.PlaceHolderResultHandler;
import org.codehaus.grepo.procedure.aop.ProcedureMethodParameterInfo;
import org.codehaus.grepo.procedure.executor.ProcedureExecutionContext;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlInOutParameter;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnType;

/**
 * Utility class for procedure compilation.
 *
 * @author dguggi
 */
public final class ProcedureCompilationUtils {

    /** The logger for this class. */
    private static final Log LOG = LogFactory.getLog(ProcedureCompilationUtils.class);

    /** Warning message for duplicate param definitions. */
    private static final String DUPLICATE_PARAM_WARNING =
        "Duplicate param with name '%s' defined - please check your configuration";

    /** Error message for invalid result handler1. */
    private static final String INVALID_RESULTHANDLER_ERROR1 =
        "Invalid resultHandler specified (class=%s) - expected instance of '%s'";

    /** Error message for invalid result handler1. */
    private static final String INVALID_RESULTHANDLER_ERROR3 = INVALID_RESULTHANDLER_ERROR1 + ", '%s', '%s'";

    /**
     * @param list The list to check.
     * @return Returns {@code true}  if all params in the {@code list} have a valid index specified and
     *         {@code false} otherwise.
     */
    public static boolean allParamsHaveValidIndex(List<ProcedureParamDescriptor> list) {
        boolean retVal = true;
        for (ProcedureParamDescriptor desc : list) {
            if (desc.getIndex() < 0) {
                retVal = false;
                break;
            }
        }
        return retVal;
    }

    /**
     * @param list The list.
     * @param paramTypes The param Types to check.
     * @return Returns a list containing all parameters of the given type.
     */
    public static List<ProcedureParamDescriptor> getParamsWithType(List<ProcedureParamDescriptor> list,
            ProcedureParamType... paramTypes) {
        List<ProcedureParamDescriptor> result = new ArrayList<ProcedureParamDescriptor>();
        for (ProcedureParamDescriptor desc : list) {
            if (ArrayUtils.contains(paramTypes, desc.getType())) {
                result.add(desc);
            }
        }
        return result;
    }

    /**
     * @param list The list.
     * @param name The param name.
     * @return Returns the param identified by {@code name} or {@code null}.
     */
    public static ProcedureParamDescriptor getParamWithName(List<ProcedureParamDescriptor> list, String name) {
        ProcedureParamDescriptor retVal = null;
        for (ProcedureParamDescriptor desc : list) {
            if (desc.getName().equals(name)) {
                retVal = desc;
                break;
            }
        }
        return retVal;
    }

    /**
     * Collects all param descriptions based on the given {@link ProcedureMethodParameterInfo}.
     *
     * @param pmpi The procedure method parameter info.
     * @param context The procedure execution context.
     * @return Returns a list containing all (in, inout, out) {@link ProcedureParamDescriptor}s.
     */
    public static List<ProcedureParamDescriptor> collectParams(ProcedureMethodParameterInfo pmpi,
            ProcedureExecutionContext context) {
        List<ProcedureParamDescriptor> list = new ArrayList<ProcedureParamDescriptor>();
        list.addAll(collectInParams(pmpi, context));
        list.addAll(collectInOutParams(pmpi, context));
        list.addAll(collectOutParams(pmpi, context));
        return list;
    }

    /**
     * Collects all IN params based on the given {@link ProcedureMethodParameterInfo}.
     *
     * @param pmpi The procedure method parameter info.
     * @param context The procedure execution context.
     * @return Returns a list containing all (in) {@link ProcedureParamDescriptor}s.
     */
    public static List<ProcedureParamDescriptor> collectInParams(ProcedureMethodParameterInfo pmpi,
            ProcedureExecutionContext context) {
        List<ProcedureParamDescriptor> result = new ArrayList<ProcedureParamDescriptor>();
        List<String> handeledParams = new ArrayList<String>();

        In inAnnotation = pmpi.getMethodAnnotation(In.class);
        if (inAnnotation != null) {
            if (handeledParams.contains(inAnnotation.name())) {
                LOG.warn(String.format(DUPLICATE_PARAM_WARNING, inAnnotation.name()));
            } else {
                result.add(createParamDescriptor(inAnnotation, context));
                handeledParams.add(inAnnotation.name());
            }
        }
        InParams inParamsAnnotation = pmpi.getMethodAnnotation(InParams.class);
        if (inParamsAnnotation != null) {
            for (In in : inParamsAnnotation.value()) {
                if (handeledParams.contains(in.name())) {
                    LOG.warn(String.format(DUPLICATE_PARAM_WARNING, in.name()));
                } else {
                    result.add(createParamDescriptor(in, context));
                    handeledParams.add(in.name());
                }
            }
        }
        List<In> inAnnotationList = pmpi.getParameterAnnotations(In.class);
        for (In in : inAnnotationList) {
            if (handeledParams.contains(in.name())) {
                LOG.warn(String.format(DUPLICATE_PARAM_WARNING, in.name()));
            } else {
                result.add(createParamDescriptor(in, context));
                handeledParams.add(in.name());
            }
        }
        return result;
    }

    /**
     * Collects all INOUT params based on the given {@link ProcedureMethodParameterInfo}.
     *
     * @param pmpi The procedure method parameter info.
     * @param context The procedure execution context.
     * @return Returns a list containing all (inout) {@link ProcedureParamDescriptor}s.
     */
    public static List<ProcedureParamDescriptor> collectInOutParams(ProcedureMethodParameterInfo pmpi,
            ProcedureExecutionContext context) {
        List<ProcedureParamDescriptor> result = new ArrayList<ProcedureParamDescriptor>();
        List<String> handeledParams = new ArrayList<String>();

        InOut inOutAnnotation = pmpi.getMethodAnnotation(InOut.class);
        if (inOutAnnotation != null) {
            if (handeledParams.contains(inOutAnnotation.name())) {
                LOG.warn(String.format(DUPLICATE_PARAM_WARNING, inOutAnnotation.name()));
            } else {
                result.add(createParamDescriptor(inOutAnnotation, context));
                handeledParams.add(inOutAnnotation.name());
            }
        }
        InOutParams inoutParamsAnnotation = pmpi.getMethodAnnotation(InOutParams.class);
        if (inoutParamsAnnotation != null) {
            for (InOut inout : inoutParamsAnnotation.value()) {
                if (handeledParams.contains(inout.name())) {
                    LOG.warn(String.format(DUPLICATE_PARAM_WARNING, inout.name()));
                } else {
                    result.add(createParamDescriptor(inout, context));
                    handeledParams.add(inout.name());
                }
            }
        }
        List<InOut> inoutAnnotationList = pmpi.getParameterAnnotations(InOut.class);
        for (InOut inout : inoutAnnotationList) {
            if (handeledParams.contains(inout.name())) {
                LOG.warn(String.format(DUPLICATE_PARAM_WARNING, inout.name()));
            } else {
                result.add(createParamDescriptor(inout, context));
                handeledParams.add(inout.name());
            }
        }
        return result;
    }

    /**
     * Collects all OUT params based on the given {@link ProcedureMethodParameterInfo}.
     *
     * @param pmpi The procedure method parameter info.
     * @param context The procedure execution context.
     * @return Returns a list containing all (out) {@link ProcedureParamDescriptor}s.
     */
    public static List<ProcedureParamDescriptor> collectOutParams(ProcedureMethodParameterInfo pmpi,
            ProcedureExecutionContext context) {
        List<ProcedureParamDescriptor> result = new ArrayList<ProcedureParamDescriptor>();
        List<String> handeledParams = new ArrayList<String>();

        Out outAnnotation = pmpi.getMethodAnnotation(Out.class);
        if (outAnnotation != null) {
            if (handeledParams.contains(outAnnotation.name())) {
                LOG.warn(String.format(DUPLICATE_PARAM_WARNING, outAnnotation.name()));
            } else {
                result.add(createParamDescriptor(outAnnotation, context));
                handeledParams.add(outAnnotation.name());
            }
        }
        OutParams outParamsAnnotation = pmpi.getMethodAnnotation(OutParams.class);
        if (outParamsAnnotation != null) {
            for (Out out : outParamsAnnotation.value()) {
                if (handeledParams.contains(out.name())) {
                    LOG.warn(String.format(DUPLICATE_PARAM_WARNING, out.name()));
                } else {
                    result.add(createParamDescriptor(out, context));
                    handeledParams.add(out.name());
                }
            }
        }
        return result;
    }

    /**
     * @param in The annotation.
     * @param context The procedure execution context.
     * @return Returns the created descriptor.
     */
    public static ProcedureParamDescriptor createParamDescriptor(In in, ProcedureExecutionContext context) {
        SqlParameter sp = null;
        if (in.scale() >= 0) {
            sp = new SqlParameter(in.name(), in.sqlType(), in.scale());
        } else if (StringUtils.isNotEmpty(in.typeName())) {
            sp = new SqlParameter(in.name(), in.sqlType(), in.typeName());
        } else {
            sp = new SqlParameter(in.name(), in.sqlType());
        }

        return new ProcedureParamDescriptor(sp.getName(), ProcedureParamType.IN, sp, in.index());
    }

    /**
     * @param inout The annotation.
     * @param context The procedure execution context.
     * @return Returns the created descriptor.
     * @throws ConfigurationException in case of errors.
     */
    public static ProcedureParamDescriptor createParamDescriptor(InOut inout, ProcedureExecutionContext context)
        throws ConfigurationException {
        SqlParameter sp = null;
        if (inout.scale() >= 0) {
            sp = new SqlInOutParameter(inout.name(), inout.sqlType(), inout.scale());
        } else if (StringUtils.isNotEmpty(inout.typeName())) {
            if (isResultHandlerSpecified(inout.resultHandlerId(), inout.resultHandler())) {
                // we have a result handler specified...
                Object resultHandler = retrieveResultHandler(context, inout.resultHandlerId(), inout.resultHandler());
                if (ClassUtils.isAssignableFrom(SqlReturnType.class, resultHandler.getClass())) {
                    sp = new SqlInOutParameter(inout.name(), inout.sqlType(), inout.typeName(),
                                (SqlReturnType) resultHandler);
                } else {
                    // unsupported/invalid result handler...
                    throw new ConfigurationException(String.format(INVALID_RESULTHANDLER_ERROR1,
                        resultHandler.getClass(), SqlReturnType.class));
                }
            } else {
                sp = new SqlInOutParameter(inout.name(), inout.sqlType(), inout.typeName());
            }
        } else {
            if (isResultHandlerSpecified(inout.resultHandlerId(), inout.resultHandler())) {
                // we have a result handler specified...
                Object resultHandler = retrieveResultHandler(context, inout.resultHandlerId(), inout.resultHandler());
                if (ClassUtils.isAssignableFrom(RowMapper.class, resultHandler.getClass())) {
                    sp = new SqlInOutParameter(inout.name(), inout.sqlType(), (RowMapper) resultHandler);
                } else if (ClassUtils.isAssignableFrom(RowCallbackHandler.class, resultHandler.getClass())) {
                    sp = new SqlInOutParameter(inout.name(), inout.sqlType(), (RowCallbackHandler) resultHandler);
                } else if (ClassUtils.isAssignableFrom(ResultSetExtractor.class, resultHandler.getClass())) {
                    sp = new SqlInOutParameter(inout.name(), inout.sqlType(), (ResultSetExtractor) resultHandler);
                } else {
                    // unsupported/invalid result handler...
                    throw new ConfigurationException(String.format(INVALID_RESULTHANDLER_ERROR3,
                        resultHandler.getClass(), RowMapper.class, RowCallbackHandler.class, ResultSetExtractor.class));
                }
            } else {
                sp = new SqlInOutParameter(inout.name(), inout.sqlType());
            }
        }
        return new ProcedureParamDescriptor(sp.getName(), ProcedureParamType.INOUT, sp, inout.index());
    }

    /**
     * @param out The annotation.
     * @param context The procedure execution context.
     * @return Returns the created descriptor.
     * @throws ConfigurationException in case of errors.
     */
    public static ProcedureParamDescriptor createParamDescriptor(Out out, ProcedureExecutionContext context)
        throws ConfigurationException {
        SqlParameter sp = null;
        if (out.scale() >= 0) {
            sp = new SqlOutParameter(out.name(), out.sqlType(), out.scale());
        } else if (StringUtils.isNotEmpty(out.typeName())) {
            if (isResultHandlerSpecified(out.resultHandlerId(), out.resultHandler())) {
                // we have a result handler specified...
                Object resultHandler = retrieveResultHandler(context, out.resultHandlerId(), out.resultHandler());
                if (ClassUtils.isAssignableFrom(SqlReturnType.class, resultHandler.getClass())) {
                    sp = new SqlOutParameter(out.name(), out.sqlType(), out.typeName(),
                                (SqlReturnType) resultHandler);
                } else {
                    // unsupported/invalid result handler...
                    throw new ConfigurationException(String.format(INVALID_RESULTHANDLER_ERROR1,
                        resultHandler.getClass(), SqlReturnType.class));
                }
            } else {
                sp = new SqlOutParameter(out.name(), out.sqlType(), out.typeName());
            }
        } else {
            if (isResultHandlerSpecified(out.resultHandlerId(), out.resultHandler())) {
                // we have a result handler specified...
                Object resultHandler = retrieveResultHandler(context, out.resultHandlerId(), out.resultHandler());
                if (ClassUtils.isAssignableFrom(RowMapper.class, resultHandler.getClass())) {
                    sp = new SqlOutParameter(out.name(), out.sqlType(), (RowMapper) resultHandler);
                } else if (ClassUtils.isAssignableFrom(RowCallbackHandler.class, resultHandler.getClass())) {
                    sp = new SqlOutParameter(out.name(), out.sqlType(), (RowCallbackHandler) resultHandler);
                } else if (ClassUtils.isAssignableFrom(ResultSetExtractor.class, resultHandler.getClass())) {
                    sp = new SqlOutParameter(out.name(), out.sqlType(), (ResultSetExtractor) resultHandler);
                } else {
                    // unsupported/invalid result handler...
                    throw new ConfigurationException(String.format(INVALID_RESULTHANDLER_ERROR3,
                        resultHandler.getClass(), RowMapper.class, RowCallbackHandler.class, ResultSetExtractor.class));
                }
            } else {
                sp = new SqlOutParameter(out.name(), out.sqlType());
            }
        }

        return new ProcedureParamDescriptor(sp.getName(), ProcedureParamType.OUT, sp, out.index());
    }

    /**
     * Checks if a result handler is specified either via {@code beanId} or {@code clazz}.
     *
     * @param beanId The spring bean id.
     * @param clazz The class to instantiate.
     * @return Returns {@code true} if a result handler is specified and {@code false} otherwise.
     */
    public static boolean isResultHandlerSpecified(String beanId, Class<?> clazz) {
        return (StringUtils.isNotEmpty(beanId) || isValidResultHandler(clazz));
    }

    /**
     * @param clazz The class to check.
     * @return Returns {@code true} if the given {@code clazz} is valid.
     */
    public static boolean isValidResultHandler(Class<?> clazz) {
        return (clazz != null && clazz != PlaceHolderResultHandler.class);
    }

    /**
     * Retrieves the result handler either from the spring context via {@code beanId} or
     * by instantiating the given {@code clazz}.
     *
     * @param context The execution context.
     * @param beanId The spring bean id.
     * @param clazz The class to instantiate.
     * @return Returns the result handler instance.
     * @throws ConfigurationException in case of errors.
     */
    public static Object retrieveResultHandler(ProcedureExecutionContext context, String beanId, Class<?> clazz)
        throws ConfigurationException {
        Object result = null;
        if (StringUtils.isNotEmpty(beanId)) {
            if (context.getApplicationContext().containsBean(beanId)) {
                result = context.getApplicationContext().getBean(beanId);
            } else {
                String msg = "Invalid resultHandlerId '%s'";
                throw new ConfigurationException(String.format(msg, beanId));
            }
        } else if (isValidResultHandler(clazz)) {
            result = ClassUtils.instantiateClass(clazz);
        } else {
            String msg = "Invalid resultHandler configuration (beanId=%s, class=%s)";
            throw new ConfigurationException(String.format(msg, beanId, clazz));
        }
        return result;
    }

}
