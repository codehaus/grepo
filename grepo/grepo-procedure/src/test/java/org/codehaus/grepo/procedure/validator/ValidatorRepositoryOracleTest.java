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

package org.codehaus.grepo.procedure.validator;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.codehaus.grepo.core.context.GrepoOracleTestContextLoaderWithDefLoc;
import org.codehaus.grepo.core.validator.TestResultValidator;
import org.codehaus.grepo.core.validator.ValidationException;
import org.codehaus.grepo.procedure.AbstractProcedureRepositoryTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author dguggi
 */
@ContextConfiguration(loader = GrepoOracleTestContextLoaderWithDefLoc.class)
public class ValidatorRepositoryOracleTest extends AbstractProcedureRepositoryTest {

    @Autowired
    private ValidatorTestRepository repo;

    @Before
    public void before() throws FileNotFoundException, IOException {
        executeSqlFromFile("classpath:META-INF/grepo/db/oracle/GrepoTestPackage-spec.sql");
        executeSqlFromFile("classpath:META-INF/grepo/db/oracle/GrepoTestPackage-body.sql");
    }

    @After
    public void after() {
        getJdbcTemplate().execute("DROP PACKAGE grepo_test");
        TestResultValidator.reset();
    }

    @Test
    public void testWithResultValidator() {
        repo.executeSimpleFunction("value", 42);
    }

    @Test(expected = RuntimeException.class)
    public void testWithResultValidatorAndCompatibleException1() {
        TestResultValidator.setExceptionToBeThrown(new RuntimeException());
        repo.executeSimpleFunction("value", 42);
    }

    /**
     * Tests with result validator and compatible checked exceptions.
     * @throws IOException in case of errors.
     */
    @Test(expected = IOException.class)
    public void testWithResultValidatorAndCompatibleException2() throws IOException {
        TestResultValidator.setExceptionToBeThrown(new IOException());
        repo.executeSimpleFunctionThrowsIOException("value", 42);
    }

    /**
     * Tests with result validator and incompatible exceptions.
     */
    @Test(expected = ValidationException.class)
    public void testWithResultValidatorAndIncompatibleException() {
        TestResultValidator.setExceptionToBeThrown(new IOException());
        repo.executeSimpleFunction("value", 42);
    }
}
