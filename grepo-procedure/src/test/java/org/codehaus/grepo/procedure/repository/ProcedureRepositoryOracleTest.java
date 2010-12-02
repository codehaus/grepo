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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.codehaus.grepo.core.context.GrepoOracleTestContextLoaderWithDefLoc;
import org.codehaus.grepo.procedure.AbstractProcedureRepositoryTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author dguggi
 */
@ContextConfiguration(loader = GrepoOracleTestContextLoaderWithDefLoc.class)
public class ProcedureRepositoryOracleTest extends AbstractProcedureRepositoryTest {
    /** The repo to test. */
    @Autowired
    private ProcedureTestRepository repo;    //NOPMD

    /**
     * @throws IOException  in case of errors.
     * @throws FileNotFoundException in case of errors.
     */
    @Before
    public void before() throws FileNotFoundException, IOException {
        executeSqlFromFile("classpath:META-INF/grepo/db/oracle/GrepoTestPackage-spec.sql");
        executeSqlFromFile("classpath:META-INF/grepo/db/oracle/GrepoTestPackage-body.sql");
    }

    /** after. */
    @After
    public void after() {
        getJdbcTemplate().execute("DROP PACKAGE grepo_test");
    }

    /** Tests repo with complex configuration. */
    @Test
    public void testSimpleProcWithComplexConfig() {
        repo.executeSimpleProcWithComplexConfig1("value", 42);  //NOPMD
        repo.executeSimpleProcWithComplexConfig1(null, null);
        repo.executeSimpleProcWithComplexConfig2("value", 42);
        repo.executeSimpleProcWithComplexConfig2(null, null);
        repo.executeSimpleProcWithComplexConfig3(42, "value");
        repo.executeSimpleProcWithComplexConfig3(null, null);
    }

    /** Tests repo with simple configuration. */
    @Test
    public void testSimpleProcWithSimpleConfig() {
        repo.executeSimpleProcWithSimpleConfig("value", 42);
        repo.executeSimpleProcWithSimpleConfig(null, null);
        repo.executeSimpleProcWithSimpleConfig("value", 42);
        repo.executeSimpleProcWithSimpleConfig(null, null);
    }

    /** Tests repo with map result. */
    @Test
    public void testSimpleProcWithMapResult() {
        Map<String,String> map = repo.executeSimpleProcWithMapResult("value", 42);
        Assert.assertNotNull(map);
        Assert.assertTrue(map.containsKey("p_result"));
        Assert.assertEquals("p1=value p2=42", map.get("p_result"));
    }

    /** Tests repo with return param name. */
    @Test
    public void testSimpleProcWithReturnParamName() {
        String result = repo.executeSimpleProcWithReturnParamName("value", 42);
        Assert.assertNotNull(result);
        Assert.assertEquals("p1=value p2=42", result);
    }

    /** Tests repo with inout param. */
    @Test
    public void testSimpleProcWithInOutParam() {
        Integer result = repo.executeSimpleProcWithInOutParam("value", 42);
        Assert.assertNotNull(result);
        Assert.assertEquals(99, result.intValue());
    }

    /** Tests simple function. */
    @Test
    public void testSimpleFunction() {
        String result = repo.executeSimpleFunction("value", 42);
        Assert.assertNotNull(result);
        Assert.assertEquals("p1=value p2=42", result);
    }
}
