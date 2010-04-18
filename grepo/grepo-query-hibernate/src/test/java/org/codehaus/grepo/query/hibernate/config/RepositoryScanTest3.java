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

package org.codehaus.grepo.query.hibernate.config;

import org.codehaus.grepo.core.context.GrepoHsqlTestContextLoaderWithDefLoc;
import org.codehaus.grepo.query.hibernate.AbstractHibernateRepositoryTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author dguggi
 */
@ContextConfiguration(loader = GrepoHsqlTestContextLoaderWithDefLoc.class)
public class RepositoryScanTest3 extends AbstractHibernateRepositoryTest {
    /**
     * Tests scan result. {@link ScanTestRepository1} should have been ignored because of exclude-filter.
     */
    @Test
    public void testScanResult() {
        Assert.assertFalse(getBeanFactory().containsBean("scanTestRepository1"));
        Assert.assertTrue(getBeanFactory().containsBean("scanTestRepository2CustomName"));
        Assert.assertFalse(getBeanFactory().containsBean("scanTestRepository3"));
        Assert.assertTrue(getBeanFactory().containsBean("scanTestRepository4Impl"));
    }
}
