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

package demo.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import demo.domain.User;

/**
 * Tests the {@link UserRepository}.
 *
 * @author dguggi
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners( {DependencyInjectionTestExecutionListener.class, TransactionalTestExecutionListener.class })
@Transactional
@ContextConfiguration(locations = "classpath:META-INF/spring/application-context.xml")
public class UserRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private UserRepository repo;

    private void saveFlush(Object... entities) {
        for (Object entity : entities) {
            entityManager.persist(entity);
        }
        entityManager.flush();
    }

    @Before
    public void before() {
        User u = new User();
        u.setUsername("max");
        u.setFirstname("max");
        u.setLastname("mustermann");
        u.setEmail("max@mustermann.com");
        saveFlush(u);
    }

    @Test
    public void testGetByUsername() {
        Assert.assertNotNull(repo.getByUsername("max"));
        Assert.assertNull(repo.getByUsername("noexistingusername"));
    }

    @Test(expected = NoResultException.class)
    public void testLoadByUsername() {
        Assert.assertNotNull(repo.loadByUsername("max"));
        // this throws exception
        repo.loadByUsername("notexistingusername");
    }

    @Test
    public void testGetByUsernameUsingSQLQuery() {
        Assert.assertNotNull(repo.getByUsernameUsingSQLQuery("max"));
        Assert.assertNull(repo.getByUsernameUsingSQLQuery("notexists"));
    }

    @Test
    public void testFindByUsernames() {
        List<String> usernames = new ArrayList<String>();
        usernames.add("max");

        List<User> list = repo.findByUsernames(usernames);
        Assert.assertEquals(1, list.size());
    }

    @Test
    public void testFindUsersByName() {
        List<User> list = repo.findUsersByName("max", null);
        Assert.assertEquals(1, list.size());

        list = repo.findUsersByName(null, "mustermann");
        Assert.assertEquals(1, list.size());

        list = repo.findUsersByName("max", "mustermann");
        Assert.assertEquals(1, list.size());

        list = repo.findUsersByName("notexists", "notexists");
        Assert.assertTrue(list.isEmpty());
    }

    @Test
    public void testIsExistingEmail() {
        Assert.assertTrue(repo.isExistingEmail("max@mustermann.com"));
        Assert.assertFalse(repo.isExistingEmail("notexists"));
    }
}
