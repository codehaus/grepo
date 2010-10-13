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

import org.springframework.jdbc.core.SqlParameter;

/**
 * Describes a procedure parameter.
 *
 * @author dguggi
 */
public class ProcedureParamDescriptor {

    /** The name for the parameter. */
    private String name;

    /** The sql parameter object used for declaring the param. */
    private SqlParameter sqlParameter;

    /** The parameter index. */
    private int index;

    /** the parameter type. */
    private ProcedureParamType type;

    /**
     * @param name The name.
     * @param type The type.
     * @param sqlParameter The sql parameter.
     */
    public ProcedureParamDescriptor(String name, ProcedureParamType type, SqlParameter sqlParameter) {
        this.name = name;
        this.type = type;
        this.sqlParameter = sqlParameter;
    }

    /**
     * @param name The name.
     * @param type The type.
     * @param sqlParameter The sql parameter.
     * @param index The index.
     */
    public ProcedureParamDescriptor(String name, ProcedureParamType type, SqlParameter sqlParameter, int index) {
        this(name, type, sqlParameter);
        this.index = index;
    }

    public SqlParameter getSqlParameter() {
        return sqlParameter;
    }

    public void setSqlParameter(SqlParameter sqlParameter) {
        this.sqlParameter = sqlParameter;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProcedureParamType getType() {
        return type;
    }

    public void setType(ProcedureParamType type) {
        this.type = type;
    }

}
