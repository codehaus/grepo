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

import java.io.FileNotFoundException;
import java.io.IOException;

import org.codehaus.grepo.core.context.GrepoOracleTestContextLoaderWithDefLoc;
import org.codehaus.grepo.core.exception.ConfigurationException;
import org.codehaus.grepo.procedure.AbstractProcedureRepositoryTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author dguggi
 */
@ContextConfiguration(loader = GrepoOracleTestContextLoaderWithDefLoc.class)
public class CompilationErrorRepositoryOracleTest extends AbstractProcedureRepositoryTest {

    @Autowired
    private CompilationErrorTestRepository repo;

    @Before
    public void before() throws FileNotFoundException, IOException {
        executeSqlFromFile("classpath:META-INF/grepo/db/oracle/GrepoTestPackage-spec.sql");
        executeSqlFromFile("classpath:META-INF/grepo/db/oracle/GrepoTestPackage-body.sql");
    }

    @After
    public void after() {
        getJdbcTemplate().execute("DROP PACKAGE grepo_test");
    }

    @Test(expected = BadSqlGrammarException.class)
    public void testInvalidConfig1() {
        repo.simpleProcInvalidConfig1("value", 42);
    }

    @Test(expected = ConfigurationException.class)
    public void testInvalidConfig2() {
        repo.simpleProcInvalidConfig2("value", 42);
    }

    @Test(expected = ConfigurationException.class)
    public void testInvalidConfig3() {
        repo.simpleProcInvalidConfig3("value", 42);
    }

    @Test(expected = BadSqlGrammarException.class)
    public void testInvalidConfig4() {
        repo.simpleProcInvalidConfig4("value", 42);
    }
}
