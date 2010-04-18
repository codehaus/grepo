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

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AspectJTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author dguggi
 */
public class GenericRepositoryScanBeanDefinitionParser implements BeanDefinitionParser {

    /** The logger for this class. */
    private static final Log LOG = LogFactory.getLog(GenericRepositoryScanBeanDefinitionParser.class);

    /** The exclude-filter element. */
    private static final String EXCLUDE_FILTER_ELEMENT = "exclude-filter";

    /** The include-filter element. */
    private static final String INCLUDE_FILTER_ELEMENT = "include-filter";

    /** The type attribute. */
    private static final String FILTER_TYPE_ATTRIBUTE = "type";

    /** The expression attribute. */
    private static final String FILTER_EXPRESSION_ATTRIBUTE = "expression";

    /** The bean name generator. */
    private BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator(); // NOPMD

    /** The required generic repository type. */
    private Class<?> requiredGenericRepositoryType;   // NOPMD

    /** The default generic repository factory type. */
    private Class<?> defaultGenericRepositoryFactoryType; // NOPMD

    /**
     * @param requiredGenericRepositoryType The generic repository type.
     * @param defaultGenericRepositoryFactoryType The generic repository factory type.
     */
    public GenericRepositoryScanBeanDefinitionParser(Class<?> requiredGenericRepositoryType,
            Class<?> defaultGenericRepositoryFactoryType) {
        this.requiredGenericRepositoryType = requiredGenericRepositoryType;
        this.defaultGenericRepositoryFactoryType = defaultGenericRepositoryFactoryType;
    }

    /**
     * {@inheritDoc}
     */
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        Object source = parserContext.extractSource(element);
        GenericRepositoryConfigContext configContext = new GenericRepositoryConfigContext(element);
        BeanDefinitionParserDelegate delegate = new BeanDefinitionParserDelegate(parserContext.getReaderContext());

        GenericRepositoryBeanDefinitionScanner scanner = configureScanner(configContext, parserContext);

        parseBeanNameGenerator(configContext, parserContext);

        String[] basePackages = StringUtils.commaDelimitedListToStringArray(configContext.getBasePackage());
        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidates = scanner.findCandidateComponents(basePackage);
            for (BeanDefinition candidate : candidates) {
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

                delegate.parsePropertyElements(configContext.getElement(), builder.getRawBeanDefinition());

                builder.addPropertyValue(GenericRepositoryConfigContext.PROXY_CLASS_PROPERTY,
                        candidate.getBeanClassName());

                String beanName = beanNameGenerator.generateBeanName(candidate, parserContext.getRegistry());
                parserContext.registerBeanComponent(
                    new BeanComponentDefinition(builder.getBeanDefinition(), beanName)); // NOPMD
                if (LOG.isTraceEnabled()) {
                    String msg = "Registered generic repository bean '%s'";
                    LOG.trace(String.format(msg, beanName));
                }
            }
        }

        return null;
    }

    /**
     * @param parserContext The parser context.
     * @param configContext The config context.
     * @return Returns the newly created scanner.
     */
    protected GenericRepositoryBeanDefinitionScanner configureScanner(GenericRepositoryConfigContext configContext,
            ParserContext parserContext) {
        GenericRepositoryBeanDefinitionScanner scanner = new GenericRepositoryBeanDefinitionScanner(
            requiredGenericRepositoryType); // NOPMD
        scanner.setResourceLoader(parserContext.getReaderContext().getResourceLoader());

        if (configContext.hasResourcePattern()) {
            scanner.setResourcePattern(configContext.getResourcePattern());
        }

        parseTypeFilters(configContext.getElement(), scanner, parserContext.getReaderContext());

        return scanner;
    }


    /**
     * @param element The element.
     * @param scanner The scanner.
     * @param readerContext The reader context.
     */
    protected void parseTypeFilters(Element element, GenericRepositoryBeanDefinitionScanner scanner,
            XmlReaderContext readerContext) {

        // Parse exclude and include filter elements.
        ClassLoader classLoader = scanner.getResourceLoader().getClassLoader();
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                String localName = node.getLocalName();
                try {
                    if (INCLUDE_FILTER_ELEMENT.equals(localName)) {
                        // Note: that we use the composite type filter for include-filter,
                        // because we always want repositories of appropriate interface type...
                        CompositeTypeFilter typeFilter = new CompositeTypeFilter(); // NOPMD
                        typeFilter.addTypeFilter(new AssignableTypeFilter(requiredGenericRepositoryType)); // NOPMD
                        typeFilter.addTypeFilter(createTypeFilter((Element)node, classLoader));
                        scanner.addIncludeFilter(typeFilter);
                    } else if (EXCLUDE_FILTER_ELEMENT.equals(localName)) {
                        TypeFilter typeFilter = createTypeFilter((Element)node, classLoader);
                        scanner.addExcludeFilter(typeFilter);
                    }
                } catch (Exception ex) {
                    readerContext.error(ex.getMessage(), readerContext.extractSource(element), ex.getCause());
                }
            }
        }
    }

    /**
     * @param element The element.
     * @param classLoader The class loader.
     * @return Returns the type filter.
     */
    @SuppressWarnings("unchecked")
    protected TypeFilter createTypeFilter(Element element, ClassLoader classLoader) {
        String filterType = element.getAttribute(FILTER_TYPE_ATTRIBUTE);
        String expression = element.getAttribute(FILTER_EXPRESSION_ATTRIBUTE);
        try {
            if ("annotation".equals(filterType)) {
                return new AnnotationTypeFilter((Class<Annotation>)classLoader.loadClass(expression));
            } else if ("assignable".equals(filterType)) {
                return new AssignableTypeFilter(classLoader.loadClass(expression));
            } else if ("aspectj".equals(filterType)) {
                return new AspectJTypeFilter(expression, classLoader);
            } else if ("regex".equals(filterType)) {
                return new RegexPatternTypeFilter(Pattern.compile(expression));
            } else if ("custom".equals(filterType)) {
                Class filterClass = classLoader.loadClass(expression);
                if (!TypeFilter.class.isAssignableFrom(filterClass)) {
                    throw new IllegalArgumentException("Class is not assignable to [" + TypeFilter.class.getName()
                        + "]: " + expression);
                }
                return (TypeFilter)BeanUtils.instantiateClass(filterClass);
            } else {
                throw new IllegalArgumentException("Unsupported filter type: " + filterType);
            }
        } catch (ClassNotFoundException ex) {
            throw new FatalBeanException("Type filter class not found: " + expression, ex);
        }
    }

    /**
     * @param configContext The config context.
     * @param parserContext The parser context.
     */
    protected void parseBeanNameGenerator(GenericRepositoryConfigContext configContext, ParserContext parserContext) {
        XmlReaderContext readerContext = parserContext.getReaderContext();
        try {
            if (configContext.hasNameGenerator()) {
                BeanNameGenerator generator = (BeanNameGenerator)instantiateUserDefinedStrategy(
                    configContext.getNameGenerator(), BeanNameGenerator.class,
                    parserContext.getReaderContext().getResourceLoader().getClassLoader());
                setBeanNameGenerator(generator);
            }
        } catch (Exception ex) {
            readerContext.error(ex.getMessage(), readerContext.extractSource(configContext.getElement()),
                ex.getCause());
        }
    }

    /**
     * @param className The class name.
     * @param strategyType The strategy type.
     * @param classLoader The class loader.
     * @return Returns the newly created instance.
     */
    @SuppressWarnings("unchecked")
    private Object instantiateUserDefinedStrategy(String className, Class strategyType, ClassLoader classLoader) {
        Object result = null;
        try {
            result = classLoader.loadClass(className).newInstance();
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException("Class [" + className + "] for strategy [" + strategyType.getName()
                + "] not found", ex);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Unable to instantiate class [" + className + "] for strategy ["
                + strategyType.getName() + "]. A zero-argument constructor is required", ex);
        }

        if (!strategyType.isAssignableFrom(result.getClass())) {
            throw new IllegalArgumentException("Provided class name must be an implementation of " + strategyType);
        }
        return result;
    }

    /**
     * Set the BeanNameGenerator to use for detected bean classes. Default is a {@link AnnotationBeanNameGenerator}.
     *
     * @param beanNameGenerator The bean name generator.
     */
    public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
        this.beanNameGenerator = (beanNameGenerator == null ? new AnnotationBeanNameGenerator() : beanNameGenerator);
    }

    public Class<?> getRequiredGenericRepositoryType() {
        return requiredGenericRepositoryType;
    }

    public Class<?> getDefaultGenericRepositoryFactoryType() {
        return defaultGenericRepositoryFactoryType;
    }

}
