/*
 * Copyright 2011 Grepo Committers.
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

package org.codehaus.grepo.query.hibernate.repository.custom;

import javax.annotation.Resource;

import org.codehaus.grepo.core.context.GrepoHsqlTestContextLoaderWithDefLoc;
import org.codehaus.grepo.query.hibernate.AbstractHibernateRepositoryTest;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

/**
 * Tests usage of a custom hibernate repository. That is a repository interface extending one of grepo's core interfaces
 * and providing custom implementation(s) for specific methods. <br/>
 * <br/>
 * This tests four different configuration scenarios: see {@code CustomRepositoryTest-context.xml} for details.
 *
 * @author dguggi
 */
@ContextConfiguration(loader = GrepoHsqlTestContextLoaderWithDefLoc.class)
public class CustomRepositoryTest extends AbstractHibernateRepositoryTest {

    @Resource(name = "customTestRepositoryApproach1")
    private CustomTestRepository approach1Bean;

    @Resource(name = "customTestRepositoryApproach2")
    private CustomTestRepository approach2Bean;

    @Resource(name = "customTestRepositoryApproach3")
    private CustomTestRepository approach3Bean;

    @Resource(name = "customTestRepositoryImpl")
    private CustomTestRepository approach4Bean;


    @Test
    public void testConfigurationApproach1() {
        approach1Bean.doSomethingSpecial();
    }

    @Test
    public void testConfigurationApproach2() {
        approach2Bean.doSomethingSpecial();
    }

    @Test
    public void testConfigurationApproach3() {
        approach3Bean.doSomethingSpecial();
    }

    @Test
    public void testConfigurationApproach4() {
        approach4Bean.doSomethingSpecial();
    }
}
