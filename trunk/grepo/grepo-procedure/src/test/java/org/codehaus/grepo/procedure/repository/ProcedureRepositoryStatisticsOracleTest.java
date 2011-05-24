/*
 * Copyright 2010 Grepo Committers.
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

import org.codehaus.grepo.core.context.GrepoOracleTestContextLoaderWithDefLoc;
import org.codehaus.grepo.procedure.AbstractProcedureRepositoryTest;
import org.codehaus.grepo.statistics.collection.StatisticsCollection;
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
public class ProcedureRepositoryStatisticsOracleTest extends AbstractProcedureRepositoryTest {

    @Autowired
    private ProcedureTestRepository repo;

    @Autowired
    private StatisticsCollection collection;

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
    public void testSimpleProcWithSimpleConfig() {
        repo.executeSimpleProcWithSimpleConfig("value", 42);
        Assert.assertEquals(1, collection.size());
        Assert.assertEquals(1, collection.size());
        repo.executeSimpleProcWithSimpleConfig(null, null);
        Assert.assertEquals(1, collection.size());
        Assert.assertEquals(2, collection.getCollectionEntriesList().get(0) //
            .getRecentStatisticsEntriesReadOnly().size());
    }
}
