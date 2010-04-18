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

package org.codehaus.grepo.query.jpa.config;

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
public class RepositoryScan1Test extends AbstractJpaRepositoryTest {
    /** This is a special repository with custom implementation. */
    @Autowired
    private ScanTestRepository4 repo4;  //NOPMD

    /**
     * Tests scan result.
     */
    @Test
    public void testScanResult() {
        Assert.assertTrue(getBeanFactory().containsBean("scanTestRepository1"));
        Assert.assertTrue(getBeanFactory().containsBean("scanTestRepository2CustomName"));
        Assert.assertFalse(getBeanFactory().containsBean("scanTestRepository3"));
        Assert.assertTrue(getBeanFactory().containsBean("scanTestRepository4Impl"));
        repo4.doSomethingSpecial();
    }
}
