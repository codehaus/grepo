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

package org.codehaus.grepo.query.jpa.executor;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.codehaus.grepo.core.context.GrepoHsqlTestContextLoaderWithDefLoc;
import org.codehaus.grepo.query.jpa.AbstractJpaRepositoryTest;
import org.codehaus.grepo.query.jpa.TestEntity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author dguggi
 */
@ContextConfiguration(loader = GrepoHsqlTestContextLoaderWithDefLoc.class)
public class ExecutorRepositoryTest extends AbstractJpaRepositoryTest {
    /** The repo to test. */
    @Autowired
    private ExecutorTestRepository repo;    //NOPMD

    /** before. */
    @Before
    public void before() {
        TestEntity entity = new TestEntity("username", 1, "firstname");  //NOPMD
        saveFlush(entity);
    }

    /** Test the "find" executor. */
    @Test
    public void testFindExecutor() {
        List<TestEntity> list = repo.findByUsername("username");
        Assert.assertEquals(1, list.size());

        list = repo.findByUsername("notExisting");  // NOPMD
        Assert.assertEquals(0, list.size());
    }

    /** Tests the "get" executor. */
    @Test
    public void testGetExecutor() {
        Assert.assertNotNull(repo.getByUsername("username"));
        Assert.assertNull(repo.getByUsername("notExisting"));

        Assert.assertTrue(repo.isExistingUsername("username"));
        Assert.assertTrue(repo.hasExistingUsername("username"));
        Assert.assertFalse(repo.isExistingUsername("notExisting"));
        Assert.assertFalse(repo.hasExistingUsername("notExisting"));
    }

    /** Tests the "get" executor with non unique result. */
    @Test(expected = NonUniqueResultException.class)
    public void testGetExecutorNonUniqueResult() {
        saveFlush(new TestEntity("dg", 1, "daniel"));
        repo.getByType(1);
    }

    /** Tests the "load" executor. */
    @Test
    public void testLoadExecutor() {
        Assert.assertNotNull(repo.loadByUsername("username"));
    }

    /** Tests the "load" executor with non unique result. */
    @Test(expected = NonUniqueResultException.class)
    public void testLoadExecutorNonUniqueResult() {
        saveFlush(new TestEntity("dg", 1, "daniel"));
        repo.loadByType(1);
    }

    /** Tests the "load" executor with no result. */
    @Test(expected = NoResultException.class)
    public void testLoadExecutorNoResult() {
        repo.loadByUsername("notExisting");
    }

    /** Tests the "update" executor. */
    @Test
    public void testUpdateExecutor() {
        int updated = repo.updateTypeByUsername(1, "username");
        Assert.assertEquals(1, updated);

        int deleted = repo.deleteByUsername("username");
        Assert.assertEquals(1, deleted);
    }
}
