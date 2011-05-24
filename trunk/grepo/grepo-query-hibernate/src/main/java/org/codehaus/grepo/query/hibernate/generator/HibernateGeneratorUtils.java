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

package org.codehaus.grepo.query.hibernate.generator;

import org.apache.commons.lang.StringUtils;
import org.codehaus.grepo.core.util.ClassUtils;
import org.codehaus.grepo.query.commons.annotation.GenericQuery;
import org.codehaus.grepo.query.commons.generator.GeneratorUtils;
import org.codehaus.grepo.query.hibernate.annotation.HibernateCaching;
import org.codehaus.grepo.query.hibernate.annotation.HibernateQueryOptions;
import org.codehaus.grepo.query.hibernate.context.HibernateQueryExecutionContext;
import org.codehaus.grepo.query.hibernate.converter.PlaceHolderResultTransformer;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.ToListResultTransformer;
import org.hibernate.transform.Transformers;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dguggi
 */
public final class HibernateGeneratorUtils {

    private static Logger logger = LoggerFactory.getLogger(HibernateGeneratorUtils.class);

    private HibernateGeneratorUtils() {
    }

    /**
     * @param queryOptions The query options.
     * @return Returns the transformer or {@code null}.
     */
    public static ResultTransformer getResultTransformer(HibernateQueryOptions queryOptions) {
        ResultTransformer transformer = null;
        if (hasValidResultTransformer(queryOptions)) {
            if (ToListResultTransformer.class.isAssignableFrom(queryOptions.resultTransformer())) {
                transformer = Transformers.TO_LIST;
            } else if (AliasToEntityMapResultTransformer.class.isAssignableFrom(queryOptions.resultTransformer())) {
                transformer = Transformers.ALIAS_TO_ENTITY_MAP;
            } else if (ResultTransformer.class.isAssignableFrom(queryOptions.resultTransformer())) {
                transformer = (ResultTransformer)ClassUtils.instantiateClass(queryOptions.resultTransformer());
            } else {
                transformer = Transformers.aliasToBean(queryOptions.resultTransformer());
            }
        }

        if (transformer != null) {
            logger.debug("Using resultTransformer: {}", transformer.getClass());
        }
        return transformer;
    }

    /**
     * @param queryOptions The queryoptions.
     * @return Returns true if valid result tranformer is set.
     */
    private static boolean hasValidResultTransformer(HibernateQueryOptions queryOptions) {
        if (queryOptions != null) {
            return queryOptions.resultTransformer() != null
                && queryOptions.resultTransformer() != PlaceHolderResultTransformer.class;
        }
        return false;
    }

    /**
     * @param queryOptions The query options.
     * @param defaultValue The default value.
     * @return Returns the fetch size.
     */
    public static Integer getFetchSize(HibernateQueryOptions queryOptions, Integer defaultValue) {
        Integer retVal = null;

        if (defaultValue != null && defaultValue > 0) {
            retVal = defaultValue;
        }
        if (queryOptions != null && queryOptions.fetchSize() > 0) {
            retVal = queryOptions.fetchSize();
        }

        if (retVal != null) {
            logger.debug("Using fetchSize: {}", retVal);
        }
        return retVal;
    }

    /**
     * @param queryOptions The query options.
     * @param context The context.
     * @return Returns {@code true} if caching is enabled and {@code false} otherwise.
     */
    public static boolean isCachingEnabled(HibernateQueryOptions queryOptions, HibernateQueryExecutionContext context) {
        boolean retVal = (context.getCaching() != null && context.getCaching() == HibernateCaching.ENABLED);
        // check value from annotation
        if (queryOptions != null && queryOptions.caching() != HibernateCaching.UNDEFINED) {
            retVal = (queryOptions.caching() == HibernateCaching.ENABLED);
        }

        if (retVal && logger.isDebugEnabled()) {
            logger.debug("Chaching is enabled");
        }
        return retVal;
    }

    /**
     * @param queryOptions The query options.
     * @param context The context.
     * @return Returns the cache region or {@code null}.
     */
    public static String getCacheRegion(HibernateQueryOptions queryOptions, HibernateQueryExecutionContext context) {
        String retVal = null;
        if (StringUtils.isNotEmpty(context.getCacheRegion())) {
            retVal = context.getCacheRegion();
        }
        if (queryOptions != null && StringUtils.isNotEmpty(queryOptions.cacheRegion())) {
            retVal = queryOptions.cacheRegion();
        }

        if (retVal != null && logger.isDebugEnabled()) {
            logger.debug("Using cacheRegion: {}", retVal);
        }
        return retVal;
    }

    public static boolean hasValidCriteriaGenerator(GenericQuery genericQuery) {
        return GeneratorUtils.isValidQueryGenerator(genericQuery.queryGenerator(), HibernateCriteriaGenerator.class);
    }

    public static boolean hasValidQueryGenerator(GenericQuery genericQuery) {
        return GeneratorUtils.isValidQueryGenerator(genericQuery.queryGenerator(), HibernateQueryGenerator.class);
    }

    public static boolean isValidScalarType(Class<? extends Type> typeClass) {
        return (typeClass != null && typeClass != PlaceHolderType.class);
    }
}
