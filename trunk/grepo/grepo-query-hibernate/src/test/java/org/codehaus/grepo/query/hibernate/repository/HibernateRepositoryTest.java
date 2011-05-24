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

package org.codehaus.grepo.query.hibernate.repository;

import java.util.ArrayList;
import java.util.List;

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
public class HibernateRepositoryTest extends AbstractHibernateRepositoryTest {

    @Autowired
    private HibernateTestRepository repo;

    private void createTestEntities(int totalCount) {
        List<TestEntity> list = new ArrayList<TestEntity>();
        for (int i = 0; i < totalCount; i++) {
            TestEntity te = new TestEntity("username" + i, (i >= (totalCount / 2) ? 1 : 0));
            list.add(te);
        }
        saveFlushEvict(list.toArray());
    }

    @Before
    public void before() {
        TestEntity entity = new TestEntity("username", 1, "firstname");
        saveFlushEvict(entity);
    }


    @Test
    public void testWithNamedQuery() {
        Assert.assertNotNull(repo.getByUsername("username"));
    }

    @Test
    public void testWithSpecifiedQuery() {
        Assert.assertNotNull(repo.getByUsernameWithSpecifiedQuery("username"));
    }

    @Test
    public void testWithSpecifiedNativeQuery() {
        Assert.assertNotNull(repo.getByUsernameWithSpecifiedNativeQuery("username"));
    }

    @Test
    public void testWithNamedQueryUsingNamedParams() {
        Assert.assertNotNull(repo.getByUsernameUsingNamedParams("username"));
    }

    @Test
    public void testWithNamedNativeQuery() {
        Assert.assertNotNull(repo.getByUsernameWithNamedNativeQuery("username"));
    }

    @Test
    public void testWithNamedNativeQueryUsingNamedParams() {
        Assert.assertNotNull(repo.getByUsernameWithNamedNativeQueryUsingNamedParams("username"));
    }

    @Test
    public void testWithSpecifiedQueryName() {
        Assert.assertNotNull(repo.getByUsernameWithSpecifiedQueryName("username"));
    }

    @Test
    public void testPaging() {
        createTestEntities(20);

        int usernameCounter = -1;
        for (int i = 0; i < 5; i++) {
            List<TestEntity> list = repo.findByType(0, i * 2, 2);
            Assert.assertEquals(2, list.size());
            Assert.assertEquals("username" + (++usernameCounter), list.get(0).getUsername());
            Assert.assertEquals("username" + (++usernameCounter), list.get(1).getUsername());
        }
        Assert.assertEquals(0, repo.findByType(0, 10, 2).size());
    }

    @Test
    public void testDefaultPaging() {
        createTestEntities(10);
        Assert.assertNotNull(repo.getByTypeWithDefaultPaging(0));
    }

    @Test
    public void testFindByUsernamesWithList() {
        List<String> list = new ArrayList<String>();
        list.add("username");
        list.add("xyz");
        List<TestEntity> result = repo.findByUsernames(list);
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void testFindByUsernamesWithVarArgs() {
        List<TestEntity> result = repo.findByUsernames("username", "xyz");
        Assert.assertEquals(1, result.size());
    }

}
