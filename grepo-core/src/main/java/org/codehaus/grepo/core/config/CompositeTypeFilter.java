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

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

/**
 * @author dguggi
 */
public class CompositeTypeFilter implements TypeFilter {

    /** A list of type filters. */
    private List<TypeFilter> typeFilters = new LinkedList<TypeFilter>(); // NOPMD

    /**
     * @param filters The filters to add.
     */
    public void addTypeFilters(TypeFilter... filters) {
        if (filters != null && filters.length > 0) {
            for (TypeFilter tf : filters) {
                typeFilters.add(tf);
            }
        }
    }

    /**
     * @param filter The filter to add.
     */
    public void addTypeFilter(TypeFilter filter) {
        addTypeFilters(filter);
    }

    /**
     * {@inheritDoc}
     */
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
            throws IOException {
        boolean matches = true;
        for (TypeFilter tf : typeFilters) {
            if (!tf.match(metadataReader, metadataReaderFactory)) {
                matches = false;
                break;
            }
        }
        return matches;
    }

}
