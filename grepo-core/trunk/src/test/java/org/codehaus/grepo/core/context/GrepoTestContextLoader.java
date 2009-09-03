/*
 * Copyright (c) 2007 Daniel Guggi.
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

package org.codehaus.grepo.core.context;

import java.io.IOException;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.test.context.support.GenericXmlContextLoader;

/**
 * Base loader for grepo tests.
 *
 * @author dguggi
 */
public class GrepoTestContextLoader extends GenericXmlContextLoader {

    /** The logger. */
    private static final Log LOG = LogFactory.getLog(GrepoTestContextLoader.class);

    /**
     * {@inheritDoc}
     */
    @Override
    protected BeanDefinitionReader createBeanDefinitionReader(GenericApplicationContext context) {
        BeanDefinitionReader bdr = super.createBeanDefinitionReader(context);
        ResourcePatternResolver rpr = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = rpr.getResources("classpath*:/META-INF/spring/grepo-testcontext.xml");
            if (LOG.isDebugEnabled()) {
                String entries = ArrayUtils.toString(resources);
                LOG.debug(String.format("Found grepo-testcontexts: [%s]", entries));
            }
            bdr.loadBeanDefinitions(resources);

            String addPattern = getAdditionalConfigPattern();
            if (!StringUtils.isEmpty(addPattern)) {
                Resource[] addResources = rpr.getResources(addPattern);
                if (addResources != null) {
                    if (LOG.isDebugEnabled()) { // NOPMD
                        LOG.debug(String.format("Found additional spring-configs: %s", ArrayUtils
                            .toString(addResources)));
                    }
                    bdr.loadBeanDefinitions(addResources);
                }
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return bdr;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isGenerateDefaultLocations() {
        return false;
    }

    /**
     * This method is supposed to be overwritten by subclass in order to provide additonal config patterns.
     *
     * @return Returns the addtional config pattern.
     */
    protected String getAdditionalConfigPattern() {
        return null;
    }

}
