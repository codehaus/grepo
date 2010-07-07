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

import junit.framework.Assert;

import org.codehaus.grepo.core.context.GrepoHsqlTestContextLoaderWithDefLoc;
import org.codehaus.grepo.query.hibernate.AbstractHibernateRepositoryTest;
import org.codehaus.grepo.query.hibernate.TestEntity;
import org.codehaus.grepo.statistics.collection.StatisticsCollection;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author dguggi
 */
@ContextConfiguration(loader = GrepoHsqlTestContextLoaderWithDefLoc.class)
public class HibernateRepositoryStatisticsTest extends AbstractHibernateRepositoryTest {
    /** The repository to test. */
    @Autowired
    private HibernateTestRepository repo; //NOPMD

    /** The statistics collection. */
    @Autowired
    private StatisticsCollection collection;

    /** before. */
    @Before
    public void before() {
        TestEntity entity = new TestEntity("username", 1, "firstname");
        saveFlushEvict(entity);
    }

    /** Test with named query. */
    @Test
    public void testStatisticsWithNamedQuery() {
        Assert.assertNotNull(repo.getByUsername("username"));
        Assert.assertEquals(1, collection.size());
        repo.get(-1L);
        Assert.assertEquals(2, collection.size());
    }

}
