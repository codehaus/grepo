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

import org.w3c.dom.Element;

/**
 * @author dguggi
 */
public class GenericRepositoryConfigContext {

    private static final String FACTORY_ATTRIBUTE = "factory";
    private static final String FACTORY_CLASS_ATTRIBUTE = "factory-class";
    private static final String BASE_PACKAGE_ATTRIBUTE = "base-package";
    private static final String RESOURCE_PATTERN_ATTRIBUTE = "resource-pattern";
    private static final String NAME_GENERATOR_ATTRIBUTE = "name-generator";
    private static final String PROXY_CLASS_ATTRIBUTE = "proxy-class";
    private static final String CLASS_ATTRIBUTE = "class";
    public static final String PROXY_CLASS_PROPERTY = "proxyClass";
    public static final String REPOSITORY_FACTORY_ELEMENT = "repository-factory";
    public static final String REPOSITORY_ELEMENT = "repository";
    public static final String REPOSITORY_SCAN_ELEMENT = "repository-scan";

    private Element element;

    public GenericRepositoryConfigContext(Element element) {
        this.element = element;
    }

    public Element getElement() {
        return element;
    }

    public String getFactory() {
        return element.getAttribute(FACTORY_ATTRIBUTE);
    }

    /**
     * @return Returns {@code true} if {@code factory} is not empty.
     */
    public boolean hasFactory() {
        return element.hasAttribute(FACTORY_ATTRIBUTE);
    }

    public String getFactoryClass() {
        return element.getAttribute(FACTORY_CLASS_ATTRIBUTE);
    }

    /**
     * @return Returns {@code true} if {@code factory-class} is not empty.
     */
    public boolean hasFactoryClass() {
        return element.hasAttribute(FACTORY_CLASS_ATTRIBUTE);
    }

    public String getClazz() {
        return element.getAttribute(CLASS_ATTRIBUTE);
    }

    /**
     * @return Returns {@code true} if {@code class} is not empty.
     */
    public boolean hasClazz() {
        return element.hasAttribute(CLASS_ATTRIBUTE);
    }

    public String getBasePackage() {
        return element.getAttribute(BASE_PACKAGE_ATTRIBUTE);
    }

    public String getResourcePattern() {
        return element.getAttribute(RESOURCE_PATTERN_ATTRIBUTE);
    }

    /**
     * @return Returns {@code true} if {@code resource-pattern} is not empty.
     */
    public boolean hasResourcePattern() {
        return element.hasAttribute(RESOURCE_PATTERN_ATTRIBUTE);
    }

    public String getNameGenerator() {
        return element.getAttribute(NAME_GENERATOR_ATTRIBUTE);
    }

    /**
     * @return Returns {@code true} if {@code name-generator} is not empty.
     */
    public boolean hasNameGenerator() {
        return element.hasAttribute(NAME_GENERATOR_ATTRIBUTE);
    }

    public String getProxyClass() {
        return element.getAttribute(PROXY_CLASS_ATTRIBUTE);
    }
}
