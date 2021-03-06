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

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Holds information about a method (and it's parameters) invoked via AOP.
 *
 * @author dguggi
 */
public interface MethodParameterInfo extends Serializable {

    /**
     * @return Returns the method.
     */
    Method getMethod();

    /**
     * @return Returns the method name.
     */
    String getMethodName();

    /**
     * @return Returns the method's return type.
     */
    Class<?> getMethodReturnType();

    /**
     * @return Returns the method annotations.
     */
    Annotation[] getMethodAnnotations();

    /**
     * @param <T> The annotation to check.
     * @param clazz The annotation class to check.
     * @return Returns the annotation or {@code null} if none found.
     */
    <T extends Annotation> T getMethodAnnotation(Class<T> clazz);

    /**
     * @param clazz The annotation class to check.
     * @return Returns {@code true} if the method is annotated with the given annotation and {@code false}
     *         otherwise.
     */
    boolean methodHasAnnotation(Class<? extends Annotation> clazz);

    /**
     * Checks if the method's declared exceptions are compatible with the given exception.
     *
     * @param exception The exception to check.
     * @return Returns {@code true} if compatible and {@code false} otherwise.
     */
    boolean isMethodCompatibleWithException(Throwable exception);

    /**
     * @return Returns the method parameters.
     */
    List<Object> getParameters();

    /**
     * @param index The parameter index.
     * @return Returns the parameter at the given index.
     */
    Object getParameter(int index);

    /**
     * @param <T> The parameter type.
     * @param index The parameter index.
     * @param clazz The parameter type class.
     * @return Returns the parameter at the given index.
     */
    <T> T getParameter(int index, Class<T> clazz);

    /**
     * @return Returns the parameter annotations.
     */
    Annotation[][] getParameterAnnotations();

    /**
     * @param clazz The annotation class to check.
     * @return Returns a list containing all parameters annotated with {@code clazz}.
     */
    List<Object> getAnnotatedParameters(Class<? extends Annotation> clazz);

    /**
     * @param clazz The annotation clazz to check.
     * @return Returns the <b>first</b> parameter found with the given annotation.
     */
    Object getAnnotatedParameter(Class<? extends Annotation> clazz);

    /**
     * @param <T> The parameter type.
     * @param clazz The annotation class.
     * @param type The parameter type class.
     * @return Returns the <b>first</b> parameter found with the given annotation.
     */
    <T> T getAnnotatedParameter(Class<? extends Annotation> clazz, Class<T> type);

    /**
     * @param <T> The annotation type.
     * @param paramIndex The parameter index.
     * @param clazz The annotation clazz.
     * @return Returns the annotation for the parameter at the given {@code index} or {@code null} if the
     *         parameter at the given {@code index} is not annotated with {@code clazz}.
     */
    <T extends Annotation> T getParameterAnnotation(int paramIndex, Class<T> clazz);

    /**
     * @param <T> the annotation type.
     * @param clazz The annotation class.
     * @return Returns a list containing all annotations found.
     */
    <T extends Annotation> List<T> getParameterAnnotations(Class<T> clazz);

    /**
     * @param paramIndex The parameter index.
     * @param clazz The annotation class.
     * @return Returns {@code true} if the parameter at the given {@code index} is annotated with
     *         {@code clazz} and {@code false} otherwise.
     */
    boolean parameterHasAnnotation(int paramIndex, Class<? extends Annotation> clazz);

    /**
     * Gets the parameter which is annotated with {@link org.codehaus.grepo.core.annotation.Param} and
     * {@link org.codehaus.grepo.core.annotation.Param#value()} is equals to {@code paramName}.
     *
     * @param paramName The param name.
     * @return Returns the parameter or {@code null}.
     */
    Object getParameterByParamName(String paramName);

    /**
     * gets the index of the parameter which is annotated with {@link org.codehaus.grepo.core.annotation.Param} and
     * {@link org.codehaus.grepo.core.annotation.Param#value()} is equal to {@code paramName}.
     *
     * @param paramName The param name.
     * @return Returns the index of the parameter with the given {@code paramName} or {@code -1}.
     */
    int getParameterIndexByParamName(String paramName);

    /**
     * Gets the parameter which is annotated with {@link org.codehaus.grepo.core.annotation.Param} and
     * {@link org.codehaus.grepo.core.annotation.Param#value()} is equals to {@code paramName}.
     *
     * @param <T> The parameter type.
     * @param paramName The param name.
     * @param clazz The parameter clazz.
     * @return Returns the parameter or {@code null}.
     */
    <T> T getParameterByParamName(String paramName, Class<T> clazz);

    /**
     * @return Returns the method's declaring type.
     */
    Class<?> getDeclaringClass();
}
