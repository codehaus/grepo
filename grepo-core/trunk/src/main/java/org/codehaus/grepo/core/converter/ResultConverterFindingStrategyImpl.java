/*
 * Copyright (c) 2007 Daniel Guggi.
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

package org.codehaus.grepo.core.converter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.grepo.core.aop.MethodParameterInfo;
import org.codehaus.grepo.core.util.ClassUtility;
import org.codehaus.grepo.exception.GrepoException;

/**
 * Default implementation of {@link ResultConverterFindingStrategy}.
 *
 * @author dguggi
 */
public class ResultConverterFindingStrategyImpl implements ResultConverterFindingStrategy {

    /** The logger for this class. */
    private static final Log LOG = LogFactory.getLog(ResultConverterFindingStrategyImpl.class);

    /** The converter registry. */
    private ResultConverterRegistry converterRegistry;

    /**
     * Checks if the given <code>clazz</code> is a valid {@link ResultConverter}.
     *
     * @param clazz The class to check.
     * @return Returns <code>true</code> if valid and <code>false</code> otherwise.
     */
    public static boolean isValidResultConverter(Class<? extends ResultConverter<?>> clazz) {
        return (clazz != null && clazz != PlaceHolderResultConverter.class);
    }

    /**
     * {@inheritDoc}
     *
     * @throws GrepoException if user didn't specify a {@link ResultConverter} but one is required based on the
     *             method's returntype and the result's type.
     */
    @SuppressWarnings("PMD")
    public Class<? extends ResultConverter<?>> findConverter(Class<? extends ResultConverter<?>> specifiedConverter,
            MethodParameterInfo mpi, Object result) throws GrepoException {

        Class<? extends ResultConverter<?>> converterToUse = null;

        Class<?> methodReturnType = mpi.getMethodReturnType();
        boolean resultIsNull = (result == null);

        // true if user has defined a valid converter via annotation...
        boolean isValidUserConverter = isValidResultConverter(specifiedConverter);

        // check if conversion is required at all, no conversion is required/performed for void return-types
        if (ClassUtility.isVoidType(methodReturnType)) {
            // method has no (void) return type...
            if (isValidUserConverter) {
                // ...but user has specified a converter, so log warning
                String msg = String.format("Converter '%s' is defined, but method '%s' has 'void' "
                    + "return-type - converter will NOT be used!", specifiedConverter, mpi.getMethodName());
                LOG.warn(msg);
            }
        } else {
            // method has return-type...
            if (isValidUserConverter) {
                // ... and user has specified a converter, so just use it (trust the user)
                converterToUse = specifiedConverter;
            } else {
                // ... and user has not specified a converter...
                if (resultIsNull && methodReturnType.isPrimitive()) {
                    // ... and result is null, but methodReturnType is a primitive - this would lead to a nullpointer
                    // exception because of autoboxing, so we throw more read-/understandable conversion exception
                    String msg = String.format("No result converter specified for result 'null', but method '%s' "
                        + "has primitive return type '%s'", mpi.getMethodName(), methodReturnType);
                    throw new ConversionException(msg);
                } else if (!resultIsNull && !ClassUtility.isAssignableFrom(methodReturnType, result.getClass())) {
                    // ... and conversion is required, so try to find appropriate build-in converter from registry,
                    // and throw an exception if no converter could be found...
                    converterToUse = converterRegistry.get(methodReturnType, true);
                }
            }
        }

        if (converterToUse != null && LOG.isTraceEnabled()) {
            String msg = String.format("Found converter '%s' for conversion from '%s' (%s) to '%s'", converterToUse
                .getName(), (resultIsNull ? "null" : result.getClass().getName()), result, methodReturnType.getName());
            LOG.trace(msg);
        }

        return converterToUse;
    }

    public void setConverterRegistry(ResultConverterRegistry registry) {
        this.converterRegistry = registry;
    }

    protected ResultConverterRegistry getConverterRegistry() {
        return converterRegistry;
    }

}
