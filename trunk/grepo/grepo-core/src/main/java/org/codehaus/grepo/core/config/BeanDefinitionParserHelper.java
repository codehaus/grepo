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

import org.springframework.beans.factory.support.BeanDefinitionBuilder;

/**
 * @author dguggi
 */
public final class BeanDefinitionParserHelper {

    private BeanDefinitionParserHelper() {
    }

    /**
     * @param configContext The config context.
     * @param source The source.
     * @param defaultGenericRepositoryFactoryType The default generic repository factory class type.
     * @return Returns the bean definition builder.
     */
    public static BeanDefinitionBuilder createBuilderFromConfigContext(GenericRepositoryConfigContext configContext,
            Object source, Class<?> defaultGenericRepositoryFactoryType) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();
        builder.getRawBeanDefinition().setSource(source);

        if (!configContext.hasFactory() && !configContext.hasFactoryClass()) { // NOPMD
            // neither 'factory' nor 'factory-class' attribute is set, so use default bean class...
            if (defaultGenericRepositoryFactoryType != null) {
                builder.getRawBeanDefinition().setBeanClass(defaultGenericRepositoryFactoryType);
            }
        } else {
            if (configContext.hasFactory()) {
                builder.getRawBeanDefinition().setParentName(configContext.getFactory());
            }
            if (configContext.hasFactoryClass()) {
                builder.getRawBeanDefinition().setBeanClassName(configContext.getFactoryClass());
            }
        }
        return builder;
    }
}
