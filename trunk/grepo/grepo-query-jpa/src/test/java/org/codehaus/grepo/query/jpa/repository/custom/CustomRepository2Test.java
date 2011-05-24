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

package org.codehaus.grepo.query.jpa.repository.custom;

import org.codehaus.grepo.core.context.GrepoHsqlTestContextLoaderWithDefLoc;
import org.codehaus.grepo.query.jpa.AbstractJpaRepositoryTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author dguggi
 */
@ContextConfiguration(loader = GrepoHsqlTestContextLoaderWithDefLoc.class)
public class CustomRepository2Test extends AbstractJpaRepositoryTest {

    @Autowired
    private CustomRepository2 customRepository2;

    /**
     * Demonstrates how to extends one of grepo's repository interfaces and provide additional functionality (for one
     * respository bean as the {@link CustomRepository2Impl} class is annotated with {@code @Repository}).
     *
     * @see CustomRepository2
     * @see CustomRepository2Impl
     */
    @Test
    public void testDoSomethingSpecial() {
        Assert.assertNotNull(customRepository2.doSomethingSpecial());
    }
}
