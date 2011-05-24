/*
 * Copyright 2011 Grepo Committers.
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

package org.codehaus.grepo.query.jpa.generator;

import org.apache.commons.lang.StringUtils;
import org.codehaus.grepo.query.commons.annotation.GenericQuery;
import org.codehaus.grepo.query.commons.generator.GeneratorUtils;
import org.codehaus.grepo.query.jpa.annotation.JpaQueryOptions;

/**
 * @author dguggi
 */
public final class JpaGeneratorUtils {

    private JpaGeneratorUtils() {
    }

    /**
     * @param queryOptions The query options.
     * @return Returns the result class or {@code null}.
     */
    public static Class<?> getResultClass(JpaQueryOptions queryOptions) {
        Class<?> resultClass = null;
        if (queryOptions != null && hasValidResultClass(queryOptions.resultClass())) {
            resultClass = queryOptions.resultClass();
        }
        return resultClass;
    }

    /**
     * @param queryOptions The query options.
     * @param generator The query generator.
     * @return Returns the result set mapping or null if not specified.
     */
    public static String getResultSetMapping(JpaQueryOptions queryOptions) {
        String resultSetMapping = null;
        if (queryOptions != null && StringUtils.isNotEmpty(queryOptions.resultSetMapping())) {
            resultSetMapping = queryOptions.resultSetMapping();
        }
        return resultSetMapping;
    }

    private static boolean hasValidResultClass(Class<?> resultClass) {
        return (resultClass != null && resultClass != PlaceHolderResultClass.class);
    }

    public static boolean hasValidQueryGenerator(GenericQuery genericQuery) {
        return GeneratorUtils.isValidQueryGenerator(genericQuery.queryGenerator(), JpaQueryGenerator.class);
    }
}
