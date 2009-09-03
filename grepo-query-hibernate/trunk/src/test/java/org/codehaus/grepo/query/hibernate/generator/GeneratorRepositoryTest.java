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

package org.codehaus.grepo.query.hibernate.generator;

import junit.framework.Assert;

import org.codehaus.grepo.core.context.GrepoHsqlTestContextLoaderWithDefLoc;
import org.codehaus.grepo.query.hibernate.AbstractHibernateRepositoryTest;
import org.codehaus.grepo.query.hibernate.TestEntity;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author dguggi
 */
@ContextConfiguration(loader = GrepoHsqlTestContextLoaderWithDefLoc.class)
public class GeneratorRepositoryTest extends AbstractHibernateRepositoryTest {
    /** The dao to test. */
    @Autowired
    private GeneratorTestRepository repo; //NOPMD

    /** before. */
    @Before
    public void before() {
        TestEntity entity = new TestEntity("username", 1, "firstname");  //NOPMD
        saveFlushEvict(entity);
    }

    /** Tests with hql generator. */
    @Test
    public void testWithHQLGenerator() {
        Assert.assertNotNull(repo.getWithHQLGenerator("username"));
    }

    /**
     * Tests with hql generator and dynamic params.
     */
    @Test
    public void testWithHQLGeneratorUsingDynParams() {
        Assert.assertNotNull(repo.getWithHQLGeneratorUsingDynParams());
    }

    /**
     * Tests with native generator.
     */
    @Test
    public void testWithNativeGenerator() {
        Assert.assertNotNull(repo.getWithNativeGenerator("username"));
    }

    /**
     * Tests with native generator using dynamic params.
     */
    @Test
    public void testWithNatvieGeneratorUsingDynParams() {
        Assert.assertNotNull(repo.getWithNativeGeneratorUsingDynParams());
    }

    /**
     * Tests with criteria generator.
     */
    @Test
    public void testWithCriteriaGenerator() {
        Assert.assertNotNull(repo.getWithCriteriaGenerator("username", "xyz"));
    }
}
