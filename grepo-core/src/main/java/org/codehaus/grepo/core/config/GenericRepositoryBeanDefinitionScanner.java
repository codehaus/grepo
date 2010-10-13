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

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Repository;

/**
 * @author dguggi
 */
public class GenericRepositoryBeanDefinitionScanner extends ClassPathScanningCandidateComponentProvider {

    /**
     * @param genericRepositoryType The generic repository interface.
     */
    public GenericRepositoryBeanDefinitionScanner(Class<?> genericRepositoryType) {
        super(false);

        if (genericRepositoryType != null) {
            // Note: we add default type filter which finds repositories which are of approriate
            // interface type and annotated with the Repository annotation
            CompositeTypeFilter typeFilter = new CompositeTypeFilter();
            typeFilter.addTypeFilters(
                new AssignableTypeFilter(genericRepositoryType),
                new AnnotationTypeFilter(Repository.class));
            addIncludeFilter(typeFilter);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        // top level classes only...
        return !beanDefinition.getMetadata().hasEnclosingClass();
    }

}
