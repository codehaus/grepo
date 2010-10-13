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

package org.codehaus.grepo.core.config;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * @author dguggi
 */
public class GenericRepositoryFactoryBeanDefinitionParser extends AbstractBeanDefinitionParser {

    /** The default generic repository factory type. */
    private Class<?> defaultGenericRepositoryFactoryType; // NOPMD

    /**
     * @param defaultGenericRepositoryFactoryType The generic repository factory type.
     */
    public GenericRepositoryFactoryBeanDefinitionParser(
            Class<?> defaultGenericRepositoryFactoryType) {
        this.defaultGenericRepositoryFactoryType = defaultGenericRepositoryFactoryType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        Object source = parserContext.extractSource(element);
        GenericRepositoryConfigContext configContext = new GenericRepositoryConfigContext(element);

        // init bean defintion parse delegate...
        BeanDefinitionParserDelegate delegate = new BeanDefinitionParserDelegate(parserContext.getReaderContext());
        delegate.initDefaults(element.getOwnerDocument().getDocumentElement());

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition().setAbstract(true);
        builder.getRawBeanDefinition().setSource(source);

        if (configContext.hasClazz()) {
            builder.getRawBeanDefinition().setBeanClassName(configContext.getClazz());
        } else {
            // no 'class' attribute is set, so use default bean class...
            builder.getRawBeanDefinition().setBeanClass(defaultGenericRepositoryFactoryType);
        }

        delegate.parsePropertyElements(element, builder.getRawBeanDefinition());

        return builder.getBeanDefinition();
    }

    public Class<?> getDefaultGenericRepositoryFactoryType() {
        return defaultGenericRepositoryFactoryType;
    }
}
