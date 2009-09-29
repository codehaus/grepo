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

package org.codehaus.grepo.query.jpa.converter;

import org.codehaus.grepo.core.context.GrepoHsqlTestContextLoaderWithDefLoc;
import org.codehaus.grepo.core.converter.ConversionException;
import org.codehaus.grepo.core.converter.TestResultConverter;
import org.codehaus.grepo.core.registry.RegistryException;
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
public class ConverterRepositoryTest extends AbstractJpaRepositoryTest {
    /** The repo to test. */
    @Autowired
    private ConverterTestRepository repo;  //NOPMD

    /** before. */
    @Before
    public void before() {
        TestEntity te = new TestEntity("username", 1, "firstname"); //NOPMD
        saveFlush(te);

        TestResultConverter.reset();
    }

    /** Test with implicit conversion. */
    @Test
    public void testWithImplicitConversion() {
        Assert.assertTrue(repo.isExistingUsername("username"));
        Assert.assertFalse(repo.isExistingUsername("xyz"));
    }

    /** Test with implicit conversion and specified converter. */
    @Test
    public void testWithSpecifiedConverter() {
        TestResultConverter.setReturnValue(Boolean.TRUE);
        Assert.assertTrue(repo.isExistingUsernameWithSpecifiedConverter("username"));
        TestResultConverter.setReturnValue(Boolean.FALSE);
        Assert.assertFalse(repo.isExistingUsernameWithSpecifiedConverter("username"));
    }

    /** Test with primitive return types. */
    @Test(expected = ConversionException.class)
    public void testWithPrimitveReturnType() {
        Assert.assertEquals(1, repo.getTypeByUsername("username"));

        // throws a conversion exception, because query result is null
        // and method's return type is a primitive (int)
        repo.getTypeByUsername("xyz");
    }

    /** Test with no valid result converter found. */
    @Test(expected = RegistryException.class)
    public void testNoValidConverterNotFound() {
        // query returns type TestEntity, but method
        // has String return type and no converter specified
        // implicit conversion not possible -> RegistryException
        repo.getByUsername("username");
    }
}
