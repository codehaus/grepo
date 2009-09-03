/* ***************************************************************************
 * Copyright (c) 2007 BearingPoint INFONOVA GmbH, Austria.
 *
 * This software is the confidential and proprietary information of
 * BearingPoint INFONOVA GmbH, Austria. You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with INFONOVA.
 *
 * BEARINGPOINT INFONOVA MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
 * A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. BEARINGPOINT INFONOVA SHALL
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *****************************************************************************/

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
