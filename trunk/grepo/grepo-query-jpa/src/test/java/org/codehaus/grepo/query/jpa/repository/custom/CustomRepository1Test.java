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
public class CustomRepository1Test extends AbstractJpaRepositoryTest {

    /** The custom test repository1. */
    @Autowired
    private CustomRepository1 customRepository1;;  //NOPMD

    /**
     * Demonstrates the use a custom repository implementation for (grepo) repository instances. In this example the
     * {@code customRepository1} will have a method {@code doSomethingUseful} as defined by
     * {@link CustomReadWriteJpaRepository} and {@link CustomReadWriteJpaRespositoryImpl}.
     */
    @Test
    public void testSome() {
        Assert.assertEquals(CustomReadWriteJpaRespositoryImpl.RESULT, customRepository1.doSomethingUseful());
    }
}
