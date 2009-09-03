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

package org.codehaus.grepo.exception;

import org.springframework.dao.DataAccessException;

/**
 * Base exception for grepo framwork.
 *
 * @author dguggi
 */
public abstract class GrepoException extends DataAccessException {

    /** SerialVersionUid. */
    private static final long serialVersionUID = -8770148023354000352L;

    /**
     * @param msg The message.
     * @param cause The cause.
     */
    public GrepoException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * @param msg The message.
     */
    public GrepoException(String msg) {
        super(msg);
    }

}
