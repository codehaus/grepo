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

package org.codehaus.grepo.query.hibernate.validator;

import java.io.IOException;

import org.codehaus.grepo.core.context.GrepoHsqlTestContextLoaderWithDefLoc;
import org.codehaus.grepo.core.validator.TestResultValidator;
import org.codehaus.grepo.core.validator.ValidationException;
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
public class ValidatorRepositoryTest extends AbstractHibernateRepositoryTest {

    @Autowired
    private ValidatorTestRepository repo;

    @Before
    public void before() {
        TestEntity testEntity = new TestEntity("username", 1, "firstname");
        saveFlushEvict(testEntity);
        TestResultValidator.reset();
    }

    @Test
    public void testWithResultValidator() {
        repo.getByUsername("username");
    }

    @Test(expected = RuntimeException.class)
    public void testWithResultValidatorAndCompatibleException1() {
        TestResultValidator.setExceptionToBeThrown(new RuntimeException());
        repo.getByUsername("username");
    }

    @Test(expected = IOException.class)
    public void testWithResultValidatorAndCompatibleException2() throws IOException {
        TestResultValidator.setExceptionToBeThrown(new IOException());
        repo.getByUsernameThrowsIOException("username");
    }

    @Test(expected = ValidationException.class)
    public void testWithResultValidatorAndIncompatibleException() {
        TestResultValidator.setExceptionToBeThrown(new IOException());
        repo.getByUsername("username");
    }
}
