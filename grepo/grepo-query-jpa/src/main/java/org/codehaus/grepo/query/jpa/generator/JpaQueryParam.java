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

package org.codehaus.grepo.query.jpa.generator;

import javax.persistence.TemporalType;

import org.codehaus.grepo.query.commons.generator.QueryParam;

/**
 * @author dguggi
 */
public class JpaQueryParam extends QueryParam {

    private static final long serialVersionUID = 7614639774056968108L;

    private TemporalType temporalType;

    /**
     * @param name The name.
     * @param value The value.
     */
    public JpaQueryParam(String name, Object value) {
        super(name, value);
    }

    /**
     * @param name The name.
     * @param value The value.
     * @param temporalType The temporal type.
     */
    public JpaQueryParam(String name, Object value, TemporalType temporalType) {
        this(name, value);
        this.temporalType = temporalType;
    }

    public TemporalType getTemporalType() {
        return temporalType;
    }

    public void setTemporalType(TemporalType temporalType) {
        this.temporalType = temporalType;
    }

}
