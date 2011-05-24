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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.codehaus.grepo.core.context.GrepoOracleTestContextLoaderWithDefLoc;
import org.codehaus.grepo.core.exception.ConfigurationException;
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
public class CursorProcedureOracleTest extends AbstractProcedureRepositoryTest {

    @Autowired
    private OracleCursorProcedure repo;

    @Before
    public void before() throws FileNotFoundException, IOException {
        executeSqlFromFile("classpath:META-INF/grepo/db/oracle/GrepoTestPackage-spec.sql");
        executeSqlFromFile("classpath:META-INF/grepo/db/oracle/GrepoTestPackage-body.sql");
    }

    @After
    public void after() {
        getJdbcTemplate().execute("DROP PACKAGE grepo_test");
    }

    @Test
    public void testWithInvalidResult() {
        Map<String, List<String>> result = repo.executeWithInvalidResult("x");
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }

    @Test(expected = ConfigurationException.class)
    public void testWithInvalidResultHandler() {
        repo.executeWithInvalidResultHandler("x");
    }

    @Test(expected = ConfigurationException.class)
    public void testWithInvalidResultHandlerId() {
        repo.executeWithInvalidResultHandlerId("x");
    }

    @Test
    public void testWithRowMapper() {
        List<String> result = repo.executeWithRowMapper("abc");
        Assert.assertNotNull(result);
        Assert.assertEquals("abc", result.get(0));
    }

    @Test
    public void testWithRowMapperId() {
        List<String> result = repo.executeWithRowMapperId("abc");
        Assert.assertNotNull(result);
        Assert.assertEquals("abc", result.get(0));
    }

    @Test
    public void testWithResultsetExtractor() {
        String result = repo.executeWithResultSetExtractor("xyz");
        Assert.assertNotNull(result);
        Assert.assertEquals("xyz", result);
    }

    @Test
    public void testWithResultsetExtractorId() {
        String result = repo.executeWithResultSetExtractorId("xyz");
        Assert.assertNotNull(result);
        Assert.assertEquals("xyz", result);
    }

    @Test
    public void testWithRowCallbackHandler() {
        TestRowCallbackHandler.setInvoked(false);
        repo.executeWithRowCallbackHandler("b");
        Assert.assertTrue(TestRowCallbackHandler.isInvoked());
        TestRowCallbackHandler.setInvoked(false);
    }

    @Test
    public void testWithRowCallbackHandlerId() {
        TestRowCallbackHandler.setInvoked(false);
        repo.executeWithRowCallbackHandlerId("b");
        Assert.assertTrue(TestRowCallbackHandler.isInvoked());
        TestRowCallbackHandler.setInvoked(false);
    }
}
