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

package org.codehaus.grepo.procedure.converter;

import java.sql.Types;

import org.codehaus.grepo.core.converter.TestResultConverter;
import org.codehaus.grepo.procedure.annotation.GenericProcedure;
import org.codehaus.grepo.procedure.annotation.In;
import org.codehaus.grepo.procedure.annotation.Out;
import org.codehaus.grepo.procedure.repository.GenericProcedureRepository;

/**
 * @author dguggi
 */
public interface ConverterTestRepository extends GenericProcedureRepository {

    /**
     * To test implicit conversion (from int to boolean).
     * @param p1 Procedure param1.
     * @param p2 Procedure param2.
     * @return Returns a boolean.
     */
    @GenericProcedure(sql = "grepo_test.simple_function", function = true,
        returnParamName = "p_result")
    @Out(name = "p_result", sqlType = Types.VARCHAR)
    boolean executeSimpleFunctionWithImplicitConversion(
            @In(name = "p_string", sqlType = Types.VARCHAR) String p1,
            @In(name = "p_integer", sqlType = Types.INTEGER) Integer p2);

    /**
     * To test implicit conversion (from int to boolean).
     * @param p1 Procedure param1.
     * @param p2 Procedure param2.
     * @return Returns a boolean.
     */
    @GenericProcedure(sql = "grepo_test.simple_function", function = true,
        returnParamName = "p_result", resultConverter = TestResultConverter.class)
    @Out(name = "p_result", sqlType = Types.VARCHAR)
    boolean executeSimpleFunctionWithCustomConverter(
            @In(name = "p_string", sqlType = Types.VARCHAR) String p1,
            @In(name = "p_integer", sqlType = Types.INTEGER) Integer p2);
}
