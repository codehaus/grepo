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

package org.codehaus.grepo.query.hibernate.generator;

import org.codehaus.grepo.query.commons.generator.QueryParam;
import org.hibernate.type.Type;

/**
 * @author dguggi
 */
public class HibernateQueryParam extends QueryParam {
    /** SerialVersionUid. */
    private static final long serialVersionUID = 6261649644283946867L;

    /** The type. */
    private Type type;

    /**
     * @param name The name to set.
     * @param value The value to set.
     */
    public HibernateQueryParam(String name, Object value) {
        super(name, value);
    }

    /**
     * @param name The name to set.
     * @param value The value to set.
     * @param type The type to set.
     */
    public HibernateQueryParam(String name, Object value, Type type) {
        super(name, value);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

}
