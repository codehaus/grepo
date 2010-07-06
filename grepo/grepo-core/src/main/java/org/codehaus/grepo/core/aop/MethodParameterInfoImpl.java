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

package org.codehaus.grepo.core.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.codehaus.grepo.core.annotation.Param;

/**
 * Default Implementation of {@link MethodParameterInfo}.
 *
 * @author dguggi
 */
public class MethodParameterInfoImpl implements MethodParameterInfo {

    /** The method. */
    private Method method;

    /** The method parameters. */
    private List<Object> parameters = new ArrayList<Object>();

    /**
     * @param method The method.
     * @param parameters The method parameters.
     */
    public MethodParameterInfoImpl(Method method, Collection<Object> parameters) {
        this.method = method;
        if (parameters != null && !parameters.isEmpty()) {
            this.parameters.addAll(parameters);
        }
    }

    /**
     * @param method The method.
     * @param parameters The parameters.
     */
    public MethodParameterInfoImpl(Method method, Object[] parameters) {
        this.method = method;
        if (parameters != null && parameters.length > 0) {
            this.parameters.addAll(Arrays.asList(parameters));
        }
    }

    /**
     * {@inheritDoc}
     */
    public Method getMethod() {
        return method;
    }

    protected void setMethod(Method method) {
        this.method = method;
    }

    /**
     * {@inheritDoc}
     */
    public String getMethodName() {
        return method.getName();
    }

    /**
     * {@inheritDoc}
     */
    public Class<?> getMethodReturnType() {
        return method.getReturnType();
    }

    /**
     * {@inheritDoc}
     */
    public Annotation[] getMethodAnnotations() {
        return method.getAnnotations();
    }

    /**
     * {@inheritDoc}
     */
    public <T extends Annotation> T getMethodAnnotation(Class<T> clazz) {
        return method.getAnnotation(clazz);
    }

    /**
     * {@inheritDoc}
     */
    public boolean methodHasAnnotation(Class<? extends Annotation> clazz) {
        return (getMethodAnnotation(clazz) != null);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isMethodCompatibleWithException(Throwable exception) {
        if (exception == null || exception instanceof RuntimeException) {
            return true;
        }

        Class<?>[] methodExceptions = method.getExceptionTypes();
        for (Class<?> type : methodExceptions) {
            if (type.isAssignableFrom(exception.getClass())) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public List<Object> getParameters() {
        return parameters;
    }

    protected void setParameters(List<Object> parameters) {
        this.parameters = parameters;
    }

    /**
     * {@inheritDoc}
     */
    public Object getParameter(int index) {
        Object retVal = null;
        if (index < parameters.size()) {
            retVal = parameters.get(index);
        }
        return retVal;
    }

    /**
     * {@inheritDoc}
     */
    public <T> T getParameter(int index, Class<T> clazz) {
        T retVal = null;
        Object paramValue = getParameter(index);
        if (paramValue != null) {
            retVal = clazz.cast(paramValue);
        }
        return retVal;
    }

    /**
     * {@inheritDoc}
     */
    public Annotation[][] getParameterAnnotations() {
        return method.getParameterAnnotations();
    }

    /**
     * {@inheritDoc}
     */
    public List<Object> getAnnotatedParameters(Class<? extends Annotation> clazz) {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < parameters.size(); i++) {
            for (Annotation a : getParameterAnnotations()[i]) {
                if (clazz.isAssignableFrom(a.getClass())) {
                    list.add(parameters.get(i));
                }
            }
        }
        return list;
    }

    /**
     * {@inheritDoc}
     */
    public Object getAnnotatedParameter(Class<? extends Annotation> clazz) {
        for (int i = 0; i < parameters.size(); i++) {
            for (Annotation a : getParameterAnnotations()[i]) {
                if (clazz.isAssignableFrom(a.getClass())) {
                    return parameters.get(i);
                }
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public <T> T getAnnotatedParameter(Class<? extends Annotation> clazz, Class<T> type) {
        return type.cast(getAnnotatedParameter(clazz));
    }

    /**
     * {@inheritDoc}
     */
    public <T extends Annotation> List<T> getParameterAnnotations(Class<T> clazz) {
        List<T> list = new ArrayList<T>();

        for (int i = 0; i < parameters.size(); i++) {
            T annotation = getParameterAnnotation(i, clazz);
            if (annotation != null) {
                list.add(annotation);
            }
        }
        return list;
    }

    /**
     * {@inheritDoc}
     */
    public <T extends Annotation> T getParameterAnnotation(int paramIndex, Class<T> clazz) {
        for (Annotation a : getParameterAnnotations()[paramIndex]) {
            if (clazz.isAssignableFrom(a.getClass())) {
                return clazz.cast(a);
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean parameterHasAnnotation(int paramIndex, Class<? extends Annotation> clazz) {
        return (getParameterAnnotation(paramIndex, clazz) != null);
    }

    /**
     * {@inheritDoc}
     */
    public Object getParameterByParamName(String paramName) {
        for (int i = 0; i < getParameters().size(); i++) {
            for (Annotation a : getParameterAnnotations()[i]) {
                if (a instanceof Param && ((Param)a).value().equals(paramName)) {
                    return getParameters().get(i);
                }
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public <T> T getParameterByParamName(String paramName, Class<T> clazz) {
        return clazz.cast(getParameterByParamName(paramName));
    }

    /**
     * {@inheritDoc}
     */
    public int getParameterIndexByParamName(String paramName) {
        for (int i = 0; i < getParameters().size(); i++) {
            for (Annotation a : getParameterAnnotations()[i]) {
                if (a instanceof Param && ((Param)a).value().equals(paramName)) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    public Class<?> getDeclaringClass() {
        return method.getDeclaringClass();
    }
}
