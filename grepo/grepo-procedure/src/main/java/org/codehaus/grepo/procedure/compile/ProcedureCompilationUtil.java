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
import org.codehaus.grepo.procedure.annotation.In;
import org.codehaus.grepo.procedure.annotation.InOut;
import org.codehaus.grepo.procedure.annotation.InOutParams;
import org.codehaus.grepo.procedure.annotation.InParams;
import org.codehaus.grepo.procedure.annotation.Out;
import org.codehaus.grepo.procedure.annotation.OutParams;
import org.codehaus.grepo.procedure.aop.ProcedureMethodParameterInfo;
import org.springframework.jdbc.core.SqlInOutParameter;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;

/**
 * Utility class for procedure compilation.
 *
 * @author dguggi
 */
public final class ProcedureCompilationUtil {

    /** The logger for this class. */
    private static final Log LOG = LogFactory.getLog(ProcedureCompilationUtil.class);

    /** Warning message for duplicate param definitions. */
    private static final String DUPLICATE_PARAM_WARNING =
        "Duplicate param with name '%s' defined - please check your configuration";

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
     * @return Returns a list containing all (in, inout, out) {@link ProcedureParamDescriptor}s.
     */
    public static List<ProcedureParamDescriptor> collectParams(ProcedureMethodParameterInfo pmpi) {
        List<ProcedureParamDescriptor> list = new ArrayList<ProcedureParamDescriptor>();
        list.addAll(collectInParams(pmpi));
        list.addAll(collectInOutParams(pmpi));
        list.addAll(collectOutParams(pmpi));
        return list;
    }

    /**
     * Collects all IN params based on the given {@link ProcedureMethodParameterInfo}.
     *
     * @param pmpi The procedure method parameter info.
     * @return Returns a list containing all (in) {@link ProcedureParamDescriptor}s.
     */
    public static List<ProcedureParamDescriptor> collectInParams(ProcedureMethodParameterInfo pmpi) {
        List<ProcedureParamDescriptor> result = new ArrayList<ProcedureParamDescriptor>();
        List<String> handeledParams = new ArrayList<String>();

        In inAnnotation = pmpi.getMethodAnnotation(In.class);
        if (inAnnotation != null) {
            if (handeledParams.contains(inAnnotation.name())) {
                LOG.warn(String.format(DUPLICATE_PARAM_WARNING, inAnnotation.name()));
            } else {
                result.add(createParamDescriptor(inAnnotation));
                handeledParams.add(inAnnotation.name());
            }
        }
        InParams inParamsAnnotation = pmpi.getMethodAnnotation(InParams.class);
        if (inParamsAnnotation != null) {
            for (In in : inParamsAnnotation.value()) {
                if (handeledParams.contains(in.name())) {
                    LOG.warn(String.format(DUPLICATE_PARAM_WARNING, in.name()));
                } else {
                    result.add(createParamDescriptor(in));
                    handeledParams.add(in.name());
                }
            }
        }
        List<In> inAnnotationList = pmpi.getParameterAnnotations(In.class);
        for (In in : inAnnotationList) {
            if (handeledParams.contains(in.name())) {
                LOG.warn(String.format(DUPLICATE_PARAM_WARNING, in.name()));
            } else {
                result.add(createParamDescriptor(in));
                handeledParams.add(in.name());
            }
        }
        return result;
    }

    /**
     * Collects all INOUT params based on the given {@link ProcedureMethodParameterInfo}.
     *
     * @param pmpi The procedure method parameter info.
     * @return Returns a list containing all (inout) {@link ProcedureParamDescriptor}s.
     */
    public static List<ProcedureParamDescriptor> collectInOutParams(ProcedureMethodParameterInfo pmpi) {
        List<ProcedureParamDescriptor> result = new ArrayList<ProcedureParamDescriptor>();
        List<String> handeledParams = new ArrayList<String>();

        InOut inOutAnnotation = pmpi.getMethodAnnotation(InOut.class);
        if (inOutAnnotation != null) {
            if (handeledParams.contains(inOutAnnotation.name())) {
                LOG.warn(String.format(DUPLICATE_PARAM_WARNING, inOutAnnotation.name()));
            } else {
                result.add(createParamDescriptor(inOutAnnotation));
                handeledParams.add(inOutAnnotation.name());
            }
        }
        InOutParams inoutParamsAnnotation = pmpi.getMethodAnnotation(InOutParams.class);
        if (inoutParamsAnnotation != null) {
            for (InOut inout : inoutParamsAnnotation.value()) {
                if (handeledParams.contains(inout.name())) {
                    LOG.warn(String.format(DUPLICATE_PARAM_WARNING, inout.name()));
                } else {
                    result.add(createParamDescriptor(inout));
                    handeledParams.add(inout.name());
                }
            }
        }
        List<InOut> inoutAnnotationList = pmpi.getParameterAnnotations(InOut.class);
        for (InOut inout : inoutAnnotationList) {
            if (handeledParams.contains(inout.name())) {
                LOG.warn(String.format(DUPLICATE_PARAM_WARNING, inout.name()));
            } else {
                result.add(createParamDescriptor(inout));
                handeledParams.add(inout.name());
            }
        }
        return result;
    }

    /**
     * Collects all OUT params based on the given {@link ProcedureMethodParameterInfo}.
     *
     * @param pmpi The procedure method parameter info.
     * @return Returns a list containing all (out) {@link ProcedureParamDescriptor}s.
     */
    public static List<ProcedureParamDescriptor> collectOutParams(ProcedureMethodParameterInfo pmpi) {
        List<ProcedureParamDescriptor> result = new ArrayList<ProcedureParamDescriptor>();
        List<String> handeledParams = new ArrayList<String>();

        Out outAnnotation = pmpi.getMethodAnnotation(Out.class);
        if (outAnnotation != null) {
            if (handeledParams.contains(outAnnotation.name())) {
                LOG.warn(String.format(DUPLICATE_PARAM_WARNING, outAnnotation.name()));
            } else {
                result.add(createParamDescriptor(outAnnotation));
                handeledParams.add(outAnnotation.name());
            }
        }
        OutParams outParamsAnnotation = pmpi.getMethodAnnotation(OutParams.class);
        if (outParamsAnnotation != null) {
            for (Out out : outParamsAnnotation.value()) {
                if (handeledParams.contains(out.name())) {
                    LOG.warn(String.format(DUPLICATE_PARAM_WARNING, out.name()));
                } else {
                    result.add(createParamDescriptor(out));
                    handeledParams.add(out.name());
                }
            }
        }
        return result;
    }

    /**
     * @param in The annotation.
     * @return Returns the created descriptor.
     */
    public static ProcedureParamDescriptor createParamDescriptor(In in) {
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
     * @return Returns the created descriptor.
     */
    public static ProcedureParamDescriptor createParamDescriptor(InOut inout) {
        SqlParameter sp = null;
        if (inout.scale() >= 0) {
            sp = new SqlInOutParameter(inout.name(), inout.sqlType(), inout.scale());
        } else if (StringUtils.isNotEmpty(inout.typeName())) {
            sp = new SqlInOutParameter(inout.name(), inout.sqlType(), inout.typeName());
        } else {
            sp = new SqlInOutParameter(inout.name(), inout.sqlType());
        }
        return new ProcedureParamDescriptor(sp.getName(), ProcedureParamType.INOUT, sp, inout.index());
    }

    /**
     * @param out The annotation.
     * @return Returns the created descriptor.
     */
    public static ProcedureParamDescriptor createParamDescriptor(Out out) {
        SqlParameter sp = null;
        if (out.scale() >= 0) {
            sp = new SqlOutParameter(out.name(), out.sqlType(), out.scale());
        } else if (StringUtils.isNotEmpty(out.typeName())) {
            sp = new SqlOutParameter(out.name(), out.sqlType(), out.typeName());
        } else {
            sp = new SqlOutParameter(out.name(), out.sqlType());
        }

        return new ProcedureParamDescriptor(sp.getName(), ProcedureParamType.OUT, sp, out.index());
    }
}
