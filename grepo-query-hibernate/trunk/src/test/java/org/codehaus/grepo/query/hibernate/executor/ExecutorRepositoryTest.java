/*
 * Copyright (c) 2007 Daniel Guggi.
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

package org.codehaus.grepo.query.hibernate.executor;

import java.util.Iterator;
import java.util.List;

import org.codehaus.grepo.core.context.GrepoHsqlTestContextLoaderWithDefLoc;
import org.codehaus.grepo.query.hibernate.AbstractRepositoryTest;
import org.codehaus.grepo.query.hibernate.TestEntity;
import org.codehaus.grepo.query.hibernate.annotation.HibernateScrollMode;
import org.hibernate.NonUniqueResultException;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author dguggi
 */
@ContextConfiguration(loader = GrepoHsqlTestContextLoaderWithDefLoc.class)
public class ExecutorRepositoryTest extends AbstractRepositoryTest {
    /** The repo to test. */
    @Autowired
    private ExecutorRepository repo; //NOPMD

    /** before. */
    @Before
    public void before() {
        TestEntity testEntity = new TestEntity("username", 1, "firstname"); // NOPMD
        saveFlushEvict(testEntity);
    }

    /**
     * Tests the "find" executor.
     */
    @Test
    public void testFindExecutor() {
        List<TestEntity> list = repo.findByUsername("username");
        Assert.assertEquals(1, list.size());

        list = repo.findByUsername("notExisting");  //NOPMD
        Assert.assertEquals(0, list.size());
    }

    /**
     * Tests the "get" executor.
     */
    @Test
    public void testGetExecutor() {
        Assert.assertNotNull(repo.getByUsername("username"));
        Assert.assertNull(repo.getByUsername("notExisting"));

        Assert.assertTrue(repo.isExistingUsername("username"));
        Assert.assertTrue(repo.hasExistingUsername("username"));
        Assert.assertFalse(repo.isExistingUsername("notExisting"));
        Assert.assertFalse(repo.hasExistingUsername("notExisting"));
    }

    /**
     * Tests the "get" executor with non unique result.
     */
    @Test(expected = NonUniqueResultException.class)
    public void testGetExecutorNonUniqueResult() {
        saveFlushEvict(new TestEntity("dg", 1, "daniel"));
        repo.getByType(1);
    }

    /**
     * Tests the "load" executor.
     */
    @Test
    public void testLoadExecutor() {
        Assert.assertNotNull(repo.loadByUsername("username"));
    }

    /**
     * Tests the "load" executor with non unique result.
     */
    @Test(expected = NonUniqueResultException.class)
    public void testLoadExecutorNonUniqueResult() {
        saveFlushEvict(new TestEntity("dg", 1, "daniel"));
        repo.loadByType(1);
    }

    /**
     * Tests the "load" executor with entity not found.
     */
    @Test(expected = EntityNotFoundException.class)
    public void testLoadExecutorEntityNotFound() {
        repo.loadByUsername("notExisting");
    }

    /**
     * Tests the "iterate" executor.
     */
    @Test
    public void testIterateExecutor() {
        Iterator<TestEntity> it = repo.iterateByUsername("notExisting");
        Assert.assertNotNull(it);
        Assert.assertFalse(it.hasNext());

        it = repo.iterateByUsername("username");
        Assert.assertNotNull(it);
        Assert.assertTrue(it.hasNext());
    }

    /**
     * Tests the "scroll" executor.
     */
    @Test
    public void testScrollExecutor() {
        ScrollableResults sr = repo.scrollByUsername("username");
        Assert.assertTrue(sr.next());

        sr = repo.scrollByUsernameWithStaticScrollMode("username");
        Assert.assertTrue(sr.next());

        sr = repo.scrollByUsernameWithDynamicScrollMode1("username", HibernateScrollMode.FORWARD_ONLY);
        Assert.assertTrue(sr.next());

        sr = repo.scrollByUsernameWithDynamicScrollMode2("username", ScrollMode.FORWARD_ONLY);
        Assert.assertTrue(sr.next());
    }

    /**
     * Tests the "update" executor.
     */
    @Test
    public void testUpdateExecutor() {
        int updated = repo.updateTypeByUsername(1, "username");
        Assert.assertEquals(1, updated);

        int deleted = repo.deleteByUsername("username");
        Assert.assertEquals(1, deleted);
    }
}
