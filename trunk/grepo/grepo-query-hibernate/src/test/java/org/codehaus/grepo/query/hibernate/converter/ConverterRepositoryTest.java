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

package org.codehaus.grepo.query.hibernate.converter;

import java.util.List;
import java.util.Map;

import org.codehaus.grepo.core.context.GrepoHsqlTestContextLoaderWithDefLoc;
import org.codehaus.grepo.core.converter.ConversionException;
import org.codehaus.grepo.core.converter.TestResultConverter;
import org.codehaus.grepo.core.registry.RegistryException;
import org.codehaus.grepo.query.hibernate.AbstractHibernateRepositoryTest;
import org.codehaus.grepo.query.hibernate.TestEntity;
import org.codehaus.grepo.query.hibernate.TestEntityDto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author dguggi
 */
@ContextConfiguration(loader = GrepoHsqlTestContextLoaderWithDefLoc.class)
public class ConverterRepositoryTest extends AbstractHibernateRepositoryTest {

    @Autowired
    private ConverterTestRepository repo;

    @Before
    public void before() {
        TestEntity te = new TestEntity("username", 1, "firstname");
        saveFlushEvict(te);
        TestResultConverter.reset();
    }

    @Test
    public void testWithImplicitConversion() {
        Assert.assertTrue(repo.isExistingUsername("username"));
        Assert.assertFalse(repo.isExistingUsername("xyz"));
    }

    @Test
    public void testWithSpecifiedConverter() {
        TestResultConverter.setReturnValue(Boolean.TRUE);
        Assert.assertTrue(repo.isExistingUsernameWithSpecifiedConverter("username"));
        TestResultConverter.setReturnValue(Boolean.FALSE);
        Assert.assertFalse(repo.isExistingUsernameWithSpecifiedConverter("username"));
    }

    @Test(expected = ConversionException.class)
    public void testWithPrimitveReturnType() {
        Assert.assertEquals(1, repo.getTypeByUsername("username"));

        // throws a conversion exception, because query result is null
        // and method's return type is a primitive (int)
        repo.getTypeByUsername("xyz");
    }

    @Test(expected = RegistryException.class)
    public void testNoValidConverterNotFound() {
        // query returns type TestEntity, but method
        // has String return type and no converter specified
        // implicit conversion not possible -> RegistryException
        repo.getByUsername("username");
    }

    @Test
    public void testWithToListResultTransformer() {
        List<List<Object>> list = repo.findAllWithToListResultTransformer();
        Assert.assertTrue(list.get(0) instanceof List<?>);
        Assert.assertEquals("username", list.get(0).get(0));
        Assert.assertEquals("firstname", list.get(0).get(1));
    }

    @Test
    public void testWithAliasToEntityMapResultTransformer() {
        List<Map<String, Object>> list = repo.findAllWithAliasToEntityMapResultTransformer();
        Assert.assertTrue(list.get(0) instanceof Map<?, ?>);
        Map<String, Object> map = list.get(0);
        Assert.assertEquals("username", map.get("un"));
        Assert.assertEquals("firstname", map.get("fn"));
    }

    @Test
    public void testWithAliasToBeanResultTransformer() {
        List<TestEntityDto> list = repo.findAllWithAliasToBeanResultTransformer();
        Assert.assertEquals("username", list.get(0).getUn());
        Assert.assertEquals("firstname", list.get(0).getFn());
    }

    @Test
    public void testWithAliasToBeanResultTransformerAndNativeQuery() {
        List<TestEntityDto> list = repo.findAllWithAliasToBeanResultTransformerAndNativeQuery();
        Assert.assertEquals("username", list.get(0).getUn());
        Assert.assertEquals("firstname", list.get(0).getFn());
    }

    @Test
    public void testWithAliasToBeanResultTransformerAndSpecifiedNativeQuery() {
        List<TestEntityDto> list = repo.findAllWithAliasToBeanResultTransformerAndSpecifiedNativeQuery();
        Assert.assertEquals("username", list.get(0).getUn());
        Assert.assertEquals("firstname", list.get(0).getFn());
    }
}
