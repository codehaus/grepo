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

package demo.repository;

import java.sql.Types;

import org.codehaus.grepo.procedure.annotation.GenericProcedure;
import org.codehaus.grepo.procedure.annotation.In;
import org.codehaus.grepo.procedure.annotation.Out;

/**
 * This is the procedure repository for the {@code GREPO_DEMO} package.
 *
 * @author dguggi
 */
public interface DemoProcedure {

    /**
     * Executes {@code GREPO_DEMO.DEMO_PROCEDURE}.
     *
     * @param param1 The p_string param.
     * @param param2 The p_integer param.
     * @return Returns the p_result.
     */
    @GenericProcedure(sql = "grepo_demo.demo_procedure", returnParamName = "p_result")
    @Out(name = "p_result", sqlType = Types.VARCHAR)
    String executeDemoProcedure(
        @In(name = "p_string", sqlType = Types.VARCHAR) String param1,
        @In(name = "p_integer", sqlType = Types.INTEGER) int param2);

    /**
     * Executes {@code GREPO_DEMO.DEMO_FUNCTION}.
     *
     * @param param1 The p_string param.
     * @param param2 The p_integer param.
     * @return Returns the p_result.
     */
    @GenericProcedure(sql = "grepo_demo.demo_function", returnParamName = "p_result", function = true)
    @Out(name = "p_result", sqlType = Types.VARCHAR)
    String executeDemoFunction(
        @In(name = "p_string", sqlType = Types.VARCHAR) String param1,
        @In(name = "p_integer", sqlType = Types.INTEGER) int param2);
}
