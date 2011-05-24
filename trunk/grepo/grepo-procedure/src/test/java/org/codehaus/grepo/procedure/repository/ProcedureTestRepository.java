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

package org.codehaus.grepo.procedure.repository;

import java.sql.Types;
import java.util.Map;

import org.codehaus.grepo.core.annotation.Param;
import org.codehaus.grepo.procedure.annotation.GenericProcedure;
import org.codehaus.grepo.procedure.annotation.In;
import org.codehaus.grepo.procedure.annotation.InOut;
import org.codehaus.grepo.procedure.annotation.InParams;
import org.codehaus.grepo.procedure.annotation.Out;
import org.codehaus.grepo.procedure.annotation.OutParams;

/**
 * @author dguggi
 */
public interface ProcedureTestRepository extends GenericProcedureRepository {

    @GenericProcedure(sql = "grepo_test.simple_proc1")
    @InParams({
        @In(name = "p_string", sqlType = Types.VARCHAR),
        @In(name = "p_integer", sqlType = Types.INTEGER) })
    @Out(name = "p_result", sqlType = Types.VARCHAR)
    void executeSimpleProcWithComplexConfig1(
            @Param("p_string") String p1,
            @Param("p_integer") Integer p2);


    @GenericProcedure(sql = "grepo_test.simple_proc1")
    @InParams({
        @In(name = "p_string", sqlType = Types.VARCHAR),
        @In(name = "p_integer", sqlType = Types.INTEGER) })
    @OutParams({
        @Out(name = "p_result", sqlType = Types.VARCHAR) })
    void executeSimpleProcWithComplexConfig2(
            @Param("p_string") String p1,
            @Param("p_integer") Integer p2);


    @GenericProcedure(sql = "grepo_test.simple_proc1")
    @Out(name = "p_result", sqlType = Types.VARCHAR, index = 3)
    @InParams({
        @In(name = "p_integer", sqlType = Types.INTEGER, index = 2),
        @In(name = "p_string", sqlType = Types.VARCHAR, index = 1) })
    void executeSimpleProcWithComplexConfig3(
            @Param("p_integer") Integer p2,
            @Param("p_string") String p1);


    @GenericProcedure(sql = "grepo_test.simple_proc1")
    @Out(name = "p_result", sqlType = Types.VARCHAR)
    void executeSimpleProcWithSimpleConfig(
            @In(name = "p_string", sqlType = Types.VARCHAR) String p1,
            @In(name = "p_integer", sqlType = Types.INTEGER) Integer p2);


    @GenericProcedure(sql = "grepo_test.simple_proc1")
    @Out(name = "p_result", sqlType = Types.VARCHAR)
    Map<String, String> executeSimpleProcWithMapResult(
            @In(name = "p_string", sqlType = Types.VARCHAR) String p1,
            @In(name = "p_integer", sqlType = Types.INTEGER) Integer p2);


    @GenericProcedure(sql = "grepo_test.simple_proc1", returnParamName = "p_result")
    @Out(name = "p_result", sqlType = Types.VARCHAR)
    String executeSimpleProcWithReturnParamName(
            @In(name = "p_string", sqlType = Types.VARCHAR) String p1,
            @In(name = "p_integer", sqlType = Types.INTEGER) Integer p2);


    @GenericProcedure(sql = "grepo_test.simple_proc2", returnParamName = "p_integer")
    Integer executeSimpleProcWithInOutParam(
            @In(name = "p_string", sqlType = Types.VARCHAR) String p1,
            @InOut(name = "p_integer", sqlType = Types.INTEGER) Integer p2);


    @GenericProcedure(sql = "grepo_test.simple_function", function = true,
        returnParamName = "p_result")
    @Out(name = "p_result", sqlType = Types.VARCHAR)
    String executeSimpleFunction(
            @In(name = "p_string", sqlType = Types.VARCHAR) String p1,
            @In(name = "p_integer", sqlType = Types.INTEGER) Integer p2);

}
