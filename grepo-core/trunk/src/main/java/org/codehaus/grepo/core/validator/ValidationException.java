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

package org.codehaus.grepo.core.validator;

import org.codehaus.grepo.exception.GrepoException;

/**
 * Validation exception thrown by {@link GenericValidationUtils}.
 *
 * @author dguggi
 */
public class ValidationException extends GrepoException {

    /** SerialVersionUid. */
    private static final long serialVersionUID = 1602871157282249875L;

    /**
     * @param msg The message.
     * @param cause The cause.
     */
    public ValidationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * @param msg The message.
     */
    public ValidationException(String msg) {
        super(msg);
    }

}
