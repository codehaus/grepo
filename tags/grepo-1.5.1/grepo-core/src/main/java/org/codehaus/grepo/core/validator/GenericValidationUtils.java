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

package org.codehaus.grepo.core.validator;

import org.apache.commons.lang.ArrayUtils;
import org.codehaus.grepo.core.aop.MethodParameterInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility for performing result validation.
 *
 * @author dguggi
 */
public final class GenericValidationUtils {

    /** Private constructor. */
    private GenericValidationUtils() {
    }

    /**
     * Checks if the given {@code clazz} is a valid {@link ResultValidator}.
     * @param clazz The class to check.
     * @return Returns {@code true} if valid and {@code false} otherwise.
     */
    public static boolean isValidResultValidator(Class<? extends ResultValidator> clazz) {
        return (clazz != null && clazz != PlaceHolderResultValidator.class);
    }


    /**
     * Validates the given {@code result} using the given {@link ResultValidator} {@code clazz}.
     *
     * @param mpi The method parameter info.
     * @param clazz The validator clazz (must not be null).
     * @param result The result to validate.
     * @throws Exception in case of errors (like validation errors).
     * @throws ValidationException if the given {@link ResultValidator} cannot be instantiated.
     */
    @SuppressWarnings("PMD")
    public static void validateResult(MethodParameterInfo mpi, Class<? extends ResultValidator> clazz, Object result)
            throws Exception, ValidationException {
        Logger logger = getLogger();

        if (isValidResultValidator(clazz)) {
            ResultValidator validator = null;
            try {
                logger.debug("Using result validator '{}' for validating result '{}'", clazz, result);
                validator = clazz.newInstance();
            } catch (InstantiationException e) {
                String msg = String.format("Unable to create new instance of '%s': '%s'", clazz.getName(), e
                    .getMessage());
                throw new ValidationException(msg, e);
            } catch (IllegalAccessException e) {
                String msg = String.format("Unable to create new instance of '%s': '%s'", clazz.getName(), e
                    .getMessage());
                throw new ValidationException(msg, e);
            }

            try {
                validator.validate(result);
            } catch (Exception e) {
                logger.debug("Validation error occured: {}", e.getMessage());

                if (mpi.isMethodCompatibleWithException(e)) {
                    throw e;
                } else {
                    String m = "Exception '%s' is not compatible with  method '%s' (exeptionTypes: %s) "
                        + "- exception will be wrapped in a ValidationException";
                    String msg = String.format(m, e.getClass().getName(), mpi.getMethodName(), ArrayUtils.toString(mpi
                        .getMethod().getExceptionTypes()));
                    logger.error(msg);
                    throw new ValidationException(msg);
                }
            }
        }
    }

    /**
     * @return Returns the logger for this class.
     */
    private static Logger getLogger() {
        return LoggerFactory.getLogger(GenericValidationUtils.class);
    }
}
