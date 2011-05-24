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

package org.codehaus.grepo.query.jpa.repository;

import javax.annotation.Resource;
import javax.persistence.NonUniqueResultException;

import org.codehaus.grepo.core.context.GrepoHsqlTestContextLoaderWithDefLoc;
import org.codehaus.grepo.query.jpa.AbstractJpaRepositoryTest;
import org.codehaus.grepo.query.jpa.TestEntity;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author dguggi
 */
@ContextConfiguration(loader = GrepoHsqlTestContextLoaderWithDefLoc.class)
public class ExceptionTranslationTest extends AbstractJpaRepositoryTest {

    @Resource(name = "notTranslatingTestRepository")
    private ExceptionTranslationTestRepository1 notTranslatingRepository;

    @Resource(name = "translatingTestRepository")
    private ExceptionTranslationTestRepository1 translatingRepository;

    @Autowired
    private ExceptionTranslationTestRepository2 scannedRepository;

    @Before
    public void before() {
        TestEntity entity1 = new TestEntity("username", 1, "firstname");
        TestEntity entity2 = new TestEntity("username1", 1, "firstname");
        saveFlush(entity1, entity2);
    }


    /** Tests with not translating repository. */
    @Test(expected = NonUniqueResultException.class)
    public void testNoExceptionTranslation() {
        notTranslatingRepository.getByFirstName("firstname");
    }

    /** Tests with translating repository. */
    @Test(expected = IncorrectResultSizeDataAccessException.class)
    public void testExceptionTranslation1() {
        translatingRepository.getByFirstName("firstname");
    }

    /** Tests with translating repository. */
    @Test(expected = IncorrectResultSizeDataAccessException.class)
    public void testExceptionTranslation2() {
        scannedRepository.getByFirstName("firstname");
    }
}
