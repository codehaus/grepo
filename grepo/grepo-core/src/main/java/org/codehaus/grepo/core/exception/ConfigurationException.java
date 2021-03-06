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

package org.codehaus.grepo.core.exception;

import org.apache.commons.lang.StringUtils;

/**
 * General exception to be thrown when configuration errors are detected.
 *
 * @author dguggi
 */
public class ConfigurationException extends GrepoException {

    /** SerialVersionUid. */
    private static final long serialVersionUID = -5989062586748291072L;

    /**
     * @param msg The message.
     * @param cause The cause.
     */
    public ConfigurationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * @param msg The message.
     */
    public ConfigurationException(String msg) {
        super(msg);
    }

    /**
     * Creates an instantiaten exception.
     *
     * @param clazz The clazz.
     * @param t The throwable.
     * @return Returns the newly created exception.
     */
    public static ConfigurationException instantiateException(Class<?> clazz, Throwable t) {
        String msg = String.format("Unable to instantiate class of type '%s', message='%s'", clazz.getName(), t
            .getMessage());
        return new ConfigurationException(msg, t);
    }

    /**
     * Creates an access exception.
     *
     * @param clazz The clazz.
     * @param t The throwable.
     * @return Returns the newly created exception.
     */
    public static ConfigurationException accessException(Class<?> clazz, Throwable t) {
        String msg = String.format("Illegal access while instantiating class of type '%s', message='%s'", clazz
            .getName(), t.getMessage());
        return new ConfigurationException(msg, t);
    }

    /**
     * Creates a not-assignable exception.
     *
     * @param from The from class.
     * @param toClasses The to classes.
     * @return Returns {@code true} if assignable and {@code false} otherwise.
     */
    public static ConfigurationException notAssignableException(Class<?> from, Class<?>[] toClasses) {
        String toClassesString = "";
        for (Class<?> toClass : toClasses) {
            if (StringUtils.isNotEmpty(toClassesString)) {
                toClassesString += ",";
            }
            toClassesString += toClass.getName();
        }
        String msg = String.format("None of '%s' is assignable from '%s'", toClassesString, from.getName());
        return new ConfigurationException(msg);
    }
}
