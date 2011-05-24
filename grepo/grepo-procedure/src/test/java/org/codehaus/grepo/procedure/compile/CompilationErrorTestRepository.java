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

import java.sql.Types;

import org.codehaus.grepo.core.annotation.Param;
import org.codehaus.grepo.procedure.annotation.GenericProcedure;
import org.codehaus.grepo.procedure.annotation.In;
import org.codehaus.grepo.procedure.annotation.InParams;
import org.codehaus.grepo.procedure.annotation.Out;
import org.codehaus.grepo.procedure.repository.GenericProcedureRepository;

/**
 * @author dguggi
 */
public interface CompilationErrorTestRepository extends GenericProcedureRepository {

    /**
     * Invalid config (inparams have invalid order).
     * @param p1 Procedure param 1.
     * @param p2 Procedure param 2.
     */
    @GenericProcedure(sql = "grepo_test.simple_proc1")
    @InParams({
        @In(name = "p_integer", sqlType = Types.INTEGER),
        @In(name = "p_string", sqlType = Types.VARCHAR) })
    @Out(name = "p_result", sqlType = Types.VARCHAR)
    void simpleProcInvalidConfig1(
            @Param("p_string") String p1,
            @Param("p_integer") Integer p2);

    /**
     * Invalid config (unknown return param name).
     * @param p1 Procedure param 1.
     * @param p2 Procedure param 2.
     * @return Returns a string.
     */
    @GenericProcedure(sql = "grepo_test.simple_proc1", returnParamName = "unknown")
    @InParams({
        @In(name = "p_string", sqlType = Types.VARCHAR),
        @In(name = "p_integer", sqlType = Types.INTEGER) })
    @Out(name = "p_result", sqlType = Types.VARCHAR)
    String simpleProcInvalidConfig2(
            @Param("p_string") String p1,
            @Param("p_integer") Integer p2);

    /**
     * Invalid config (unknown IN/INOUT param).
     * @param p1 Procedure param 1.
     * @param p2 Procedure param 2.
     */
    @GenericProcedure(sql = "grepo_test.simple_proc1")
    @InParams({
        @In(name = "p_string", sqlType = Types.VARCHAR),
        @In(name = "p_integer", sqlType = Types.INTEGER) })
    @Out(name = "p_result", sqlType = Types.VARCHAR)
    void simpleProcInvalidConfig3(
            @Param("unknown") String p1,
            @Param("p_integer") Integer p2);

    /**
     * Invalid config (out param not defined).
     * @param p1 Procedure param 1.
     * @param p2 Procedure param 2.
     * @return Returns a string.
     */
    @GenericProcedure(sql = "grepo_test.simple_proc1")
    @InParams({
        @In(name = "p_string", sqlType = Types.VARCHAR),
        @In(name = "p_integer", sqlType = Types.INTEGER) })
    String simpleProcInvalidConfig4(
            @Param("p_string") String p1,
            @Param("p_integer") Integer p2);
}
