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

package org.codehaus.grepo.core;

import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Simple test class used for spring based tests.
 *
 * @author dguggi
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class AbstractSpringTest implements BeanFactoryAware {

    /** The bean factory for this test. */
    private BeanFactory beanFactory;

    /**
     * {@inheritDoc}
     */
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    protected BeanFactory getBeanFactory() {
        return beanFactory;
    }

}
