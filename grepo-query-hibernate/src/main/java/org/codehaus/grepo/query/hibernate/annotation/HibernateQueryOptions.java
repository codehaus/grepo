/*
 * Copyright 2009 Grepo Committers.
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

package org.codehaus.grepo.query.hibernate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.codehaus.grepo.query.hibernate.converter.PlaceHolderResultTransformer;
import org.codehaus.grepo.query.hibernate.generator.CriteriaGenerator;
import org.codehaus.grepo.query.hibernate.generator.PlaceHolderCriteriaGenerator;

/**
 * @author dguggi
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HibernateQueryOptions {

    /** The scroll mode. */
    HibernateScrollMode scrollMode() default HibernateScrollMode.UNDEFINED;

    /** The flush mode. */
    HibernateFlushMode flushMode() default HibernateFlushMode.UNDEFINED;

    /** The cache mode. */
    HibernateCacheMode cacheMode() default HibernateCacheMode.UNDEFINED;

    /** The caching flag. */
    HibernateCaching caching() default HibernateCaching.UNDEFINED;

    /** The cache region. */
    String cacheRegion() default "";

    /** The result transformer. */
    Class<?> resultTransformer() default PlaceHolderResultTransformer.class;

    /** The critieria generator. */
    Class<? extends CriteriaGenerator> criteriaGenerator() default PlaceHolderCriteriaGenerator.class;

    /** The fetch size. */
    int fetchSize() default 0;

    /** Entity classes specified for native queries. */
    EntityClass[] entityClasses() default {};

    /** Scalars specified for native queries. */
    Scalar[] scalars() default {};

    /** Joins specified for native queries. */
    Join[] joins() default {};

    /** Query timeout is seconds. */
    int queryTimeout() default 0;

}
