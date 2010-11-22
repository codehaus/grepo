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

package org.codehaus.grepo.procedure.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.codehaus.grepo.core.converter.PlaceHolderResultConverter;
import org.codehaus.grepo.core.converter.ResultConverter;
import org.codehaus.grepo.core.validator.PlaceHolderResultValidator;
import org.codehaus.grepo.core.validator.ResultValidator;

/**
 * Used to annotate methods which should be generically invoked (means that no implmentation is required).
 *
 * @author dguggi
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GenericProcedure {

    /** The sql. */
    String sql();

    /** Flag to indicate function calls. */
    boolean function() default false;

    /** Flag to configure caching. */
    boolean cachingEnabled() default true;

    /** The cache name to use. */
    String cacheName() default "";

    /** The result converter. */
    Class<? extends ResultConverter<?>> resultConverter() default PlaceHolderResultConverter.class;

    /** The result validator class. */
    Class<? extends ResultValidator> resultValidator() default PlaceHolderResultValidator.class;

    /** The return param name. */
    String returnParamName() default "";

    /** Specifies whether or not the procedure is read-only (default is {@code false}). */
    boolean isReadOnly() default false;
}
