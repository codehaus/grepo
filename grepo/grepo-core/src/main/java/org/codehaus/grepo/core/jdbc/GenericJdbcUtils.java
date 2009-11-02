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

package org.codehaus.grepo.core.jdbc;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;

/**
 * @author dguggi
 */
public final class GenericJdbcUtils {

    /**
     * Checks if the given {@code clazz} is a valid {@code RowMapper}.
     *
     * @param clazz The class to check.
     * @return Returns {@code true} if valid and {@code false} otherwise.
     */
    public static boolean isValidRowMapper(Class<? extends RowMapper> clazz) {
        return (clazz != null && clazz != PlaceHolderRowMapper.class);
    }

    /**
     * Checks if the given {@code clazz} is a valid {@code RowCallbackHandler}.
     *
     * @param clazz The class to check.
     * @return Returns {@code true} if valid and {@code false} otherwise.
     */
    public static boolean isValidRowCallbackHandler(Class<? extends RowCallbackHandler> clazz) {
        return (clazz != null && clazz != PlaceHolderRowCallbackHandler.class);
    }

    /**
     * Checks if the given {@code clazz} is a valid {@code ResultSetExtractor}.
     *
     * @param clazz The class to check.
     * @return Returns {@code true} if valid and {@code false} otherwise.
     */
    public static boolean isValidResultSetExtractor(Class<? extends ResultSetExtractor> clazz) {
        return (clazz != null && clazz != PlaceHolderResultSetExtractor.class);
    }
}
