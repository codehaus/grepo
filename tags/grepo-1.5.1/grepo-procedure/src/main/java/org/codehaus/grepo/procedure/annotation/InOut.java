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

/**
 * This annotation is used to configure a Method annotated with {@link GenericProcedure}. The annotation may be used for
 * methods or parameters.
 *
 * @author dguggi
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.PARAMETER })
public @interface InOut {

    /** The param name. */
    String name();

    /** The param sql type. */
    int sqlType();

    /** The index. */
    int index() default -1;

    /** The param scale. */
    int scale() default -1;

    /** The param typeName. */
    String typeName() default "";

    /** The result handler class. */
    Class<?> resultHandler() default PlaceHolderResultHandler.class;

    /** The result handler spring bean id. */
    String resultHandlerId() default "";
}
