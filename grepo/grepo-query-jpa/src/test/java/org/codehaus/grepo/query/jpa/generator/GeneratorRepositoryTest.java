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

package org.codehaus.grepo.query.jpa.generator;

import junit.framework.Assert;

import org.codehaus.grepo.core.context.GrepoHsqlTestContextLoaderWithDefLoc;
import org.codehaus.grepo.core.exception.ConfigurationException;
import org.codehaus.grepo.query.jpa.AbstractJpaRepositoryTest;
import org.codehaus.grepo.query.jpa.TestEntity;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author dguggi
 */
@ContextConfiguration(loader = GrepoHsqlTestContextLoaderWithDefLoc.class)
public class GeneratorRepositoryTest extends AbstractJpaRepositoryTest {

    @Autowired
    private GeneratorTestRepository repo;

    @Before
    public void before() {
        TestEntity entity = new TestEntity("username", 1, "firstname");
        saveFlush(entity);
    }

    @Test
    public void testWithJPQLGenerator() {
        Assert.assertNotNull(repo.getWithJPQLGenerator("username"));
    }

    @Test
    public void testWithJPQLGeneratorUsingDynParams() {
        Assert.assertNotNull(repo.getWithJPQLGeneratorUsingDynParams());
    }

    @Test
    public void testWithNativeGenerator() {
        Assert.assertNotNull(repo.getWithNativeGenerator("username"));
    }

    @Test
    public void testWithSQLGeneratorUsingDynParams() {
        Assert.assertNotNull(repo.getWithNativeGeneratorUsingDynParams());
    }

    @Test(expected = ConfigurationException.class)
    public void testWithInvalidGenerator() {
        repo.getWithInvalidGenerator();
    }

    @Test
    public void testWithQueryDslQueryGenerator() {
        Assert.assertNotNull(repo.getWithQueryDslQueryGenerator("firstname"));
    }
}
