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

import java.io.FileNotFoundException;
import java.io.IOException;

import org.codehaus.grepo.core.context.GrepoOracleTestContextLoaderWithDefLoc;
import org.codehaus.grepo.core.converter.TestResultConverter;
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
public class ConverterRepositoryOracleTest extends AbstractProcedureRepositoryTest {
    /** The repo to test. */
    @Autowired
    private ConverterTestRepository repo;    //NOPMD

    /**
     * @throws IOException  in case of errors.
     * @throws FileNotFoundException in case of errors.
     */
    @Before
    public void before() throws FileNotFoundException, IOException {
        executeSqlFromFile("classpath:META-INF/spring/db/oracle/GrepoTestPackage-spec.sql");
        executeSqlFromFile("classpath:META-INF/spring/db/oracle/GrepoTestPackage-body.sql");
    }

    /** after. */
    @After
    public void after() {
        getJdbcTemplate().execute("DROP PACKAGE grepo_test");

        TestResultConverter.reset();
    }

    /** Test repo with implicit conversion. */
    @Test
    public void testWithImplicitConverter() {
        boolean retVal = repo.executeSimpleFunctionWithImplicitConversion("value", 42);
        Assert.assertFalse(retVal);
    }

    /** Test repo with specified converter. */
    @Test
    public void testWithSpecifiedConverter() {
        TestResultConverter.setReturnValue(Boolean.TRUE);
        boolean retVal = repo.executeSimpleFunctionWithCustomConverter("value", 42);
        Assert.assertTrue(retVal);
    }
}
