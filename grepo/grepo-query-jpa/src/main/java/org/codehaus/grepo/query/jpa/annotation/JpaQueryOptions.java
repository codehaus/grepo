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

package org.codehaus.grepo.query.jpa.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.persistence.QueryHint;

import org.codehaus.grepo.query.jpa.executor.PlaceHolderResultClass;

/**
 * @author dguggi
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface JpaQueryOptions {
    /** The result class to use for native queries. */
    Class<?> resultClass() default PlaceHolderResultClass.class;

    /** The result set mapping to use for native queries. */
    String resultSetMapping() default "";

    /** The query hints to use. */
    QueryHint[] queryHints() default { };

    /** The flush mode to use. */
    JpaFlushMode flushMode() default JpaFlushMode.UNDEFINED;
}
