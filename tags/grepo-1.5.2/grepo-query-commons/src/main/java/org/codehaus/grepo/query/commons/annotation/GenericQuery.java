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

package org.codehaus.grepo.query.commons.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.codehaus.grepo.core.converter.PlaceHolderResultConverter;
import org.codehaus.grepo.core.converter.ResultConverter;
import org.codehaus.grepo.core.validator.PlaceHolderResultValidator;
import org.codehaus.grepo.core.validator.ResultValidator;
import org.codehaus.grepo.query.commons.executor.PlaceHolderQueryExecutor;
import org.codehaus.grepo.query.commons.executor.QueryExecutor;
import org.codehaus.grepo.query.commons.generator.PlaceHolderQueryGenerator;
import org.codehaus.grepo.query.commons.generator.QueryGenerator;

/**
 * This annotation is used to execute queries via AOP.
 *
 * @author dguggi
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GenericQuery {
    /**
     * Specifies the query to use.
     */
    String query() default "";

    /**
     * Specifies whether or not the query is native. This attribute is only considered
     * if the {@code query} property is not empty.
     */
    boolean isNativeQuery() default false;

    /**
     * Specifies the name of the associated named-query. If empty than default query-name resolution is
     * performed.
     */
    String queryName() default "";

    /**
     * The query executor.
     */
    Class<? extends QueryExecutor<?>> queryExecutor() default PlaceHolderQueryExecutor.class;

    /**
     * The result converter.
     */
    Class<? extends ResultConverter<?>> resultConverter() default PlaceHolderResultConverter.class;

    /**
     * The query generator.
     */
    Class<? extends QueryGenerator<?>> queryGenerator() default PlaceHolderQueryGenerator.class;

    /**
     * The result validator class.
     */
    Class<? extends ResultValidator> resultValidator() default PlaceHolderResultValidator.class;

    /** Default first result value. */
    int firstResult() default -1;

    /** Default max results value. */
    int maxResults() default -1;
}
