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

package org.codehaus.grepo.core.converter;

import org.codehaus.grepo.core.exception.GrepoException;

/**
 * Exception to be thrown if result conversion fails for some reason.
 *
 * @author dguggi
 */
public class ConversionException extends GrepoException {
    /** The serial version uid.*/
    private static final long serialVersionUID = 5090853676541241500L;

    /**
     * @param msg The message to set.
     * @param cause The cause to set.
     */
    public ConversionException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * @param msg The message to set.
     */
    public ConversionException(String msg) {
        super(msg);
    }

}
