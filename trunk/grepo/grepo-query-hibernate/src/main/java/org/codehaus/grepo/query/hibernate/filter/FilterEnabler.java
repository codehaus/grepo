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

package org.codehaus.grepo.query.hibernate.filter;

import java.io.Serializable;

import org.codehaus.grepo.query.commons.aop.QueryMethodParameterInfo;
import org.hibernate.Session;

/**
 * Providing hooks for enabling filters.
 *
 * @author dguggi
 */
public interface FilterEnabler extends Serializable {
    /**
     * @param name The name.
     * @param qmpi The query method parameter info.
     * @param session The session.
     */
    void enable(String name, QueryMethodParameterInfo qmpi, Session session);
}
