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

package org.codehaus.grepo.procedure.cursor;

import java.util.List;
import java.util.Map;

import oracle.jdbc.OracleTypes;

import org.codehaus.grepo.procedure.annotation.GenericProcedure;
import org.codehaus.grepo.procedure.annotation.In;
import org.codehaus.grepo.procedure.annotation.Out;
import org.codehaus.grepo.procedure.repository.GenericProcedureRepository;
import org.hsqldb.Types;

/**
 * @author dguggi
 */
public interface OracleCursorProcedure extends GenericProcedureRepository {

    /**
     * This is an invalid procedure configuration because the sql type is set to {@code OracleTypes.CURSOR}
     * and no {@code rowMapper}, {@code RowCallbackHandler} or {@code ResultSetExtractor} is definied. This
     * is actually not a grepo error, because spring behaves like this. If you invoke a procedure like
     * that the result map is always empty.
     *
     * @param param The parameter.
     * @return Returns the result.
     */
    @GenericProcedure(sql = "grepo_test.cursor_proc")
    @Out(name = "p_result", sqlType = OracleTypes.CURSOR)
    Map<String, List<String>> executeWithInvalidResult(
            @In(name = "p_string", sqlType = Types.VARCHAR) String param);


    @GenericProcedure(sql = "grepo_test.cursor_proc", returnParamName = "p_result")
    @Out(name = "p_result", sqlType = OracleTypes.CURSOR, resultHandler = String.class)
    void executeWithInvalidResultHandler(
            @In(name = "p_string", sqlType = Types.VARCHAR) String param);


    @GenericProcedure(sql = "grepo_test.cursor_proc", returnParamName = "p_result")
    @Out(name = "p_result", sqlType = OracleTypes.CURSOR, resultHandlerId = "unknownId")
    void executeWithInvalidResultHandlerId(
            @In(name = "p_string", sqlType = Types.VARCHAR) String param);


    @GenericProcedure(sql = "grepo_test.cursor_proc", returnParamName = "p_result")
    @Out(name = "p_result", sqlType = OracleTypes.CURSOR, resultHandler = TestRowMapper.class)
    List<String> executeWithRowMapper(
            @In(name = "p_string", sqlType = Types.VARCHAR) String param);


    @GenericProcedure(sql = "grepo_test.cursor_proc", returnParamName = "p_result")
    @Out(name = "p_result", sqlType = OracleTypes.CURSOR, resultHandlerId = "testRowMapper")
    List<String> executeWithRowMapperId(
            @In(name = "p_string", sqlType = Types.VARCHAR) String param);


    @GenericProcedure(sql = "grepo_test.cursor_proc", returnParamName = "p_result")
    @Out(name = "p_result", sqlType = OracleTypes.CURSOR, resultHandler = TestResultSetExtractor.class)
    String executeWithResultSetExtractor(
            @In(name = "p_string", sqlType = Types.VARCHAR) String param);


    @GenericProcedure(sql = "grepo_test.cursor_proc", returnParamName = "p_result")
    @Out(name = "p_result", sqlType = OracleTypes.CURSOR, resultHandlerId = "testResultSetExtractor")
    String executeWithResultSetExtractorId(
            @In(name = "p_string", sqlType = Types.VARCHAR) String param);


    @GenericProcedure(sql = "grepo_test.cursor_proc")
    @Out(name = "p_result", sqlType = OracleTypes.CURSOR, resultHandler = TestRowCallbackHandler.class)
    void executeWithRowCallbackHandler(
            @In(name = "p_string", sqlType = Types.VARCHAR) String param);


    @GenericProcedure(sql = "grepo_test.cursor_proc")
    @Out(name = "p_result", sqlType = OracleTypes.CURSOR, resultHandlerId = "testRowCallbackHandler")
    void executeWithRowCallbackHandlerId(
           @In(name = "p_string", sqlType = Types.VARCHAR) String param);

}
