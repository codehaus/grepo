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

package org.codehaus.grepo.query.commons.aop;

import java.lang.reflect.Method;
import java.util.Collection;

import org.codehaus.grepo.statistics.aop.StatisticsMethodParameterInfoImpl;

/**
 * Implementation of {@link QueryMethodParameterInfo}.
 *
 * @author dguggi
 */
public class QueryMethodParameterInfoImpl extends StatisticsMethodParameterInfoImpl //
                implements QueryMethodParameterInfo {

    /** The entityClass of the target repository. */
    private Class<?> entityClass;

    /**
     * @param method The method.
     * @param parameters The parameters.
     * @param entityClass The entity class.
     */
    public QueryMethodParameterInfoImpl(Method method, Collection<Object> parameters, Class<?> entityClass) {
        super(method, parameters);
        this.entityClass = entityClass;
    }

    /**
     * @param method The method.
     * @param parameters The parameters.
     * @param entityClass The entity class.
     */
    public QueryMethodParameterInfoImpl(Method method, Object[] parameters, Class<?> entityClass) {
        super(method, parameters);
        this.entityClass = entityClass;
    }

    /**
     * {@inheritDoc}
     */
    public Class<?> getEntityClass() {
        return entityClass;
    }

    protected void setEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

}
