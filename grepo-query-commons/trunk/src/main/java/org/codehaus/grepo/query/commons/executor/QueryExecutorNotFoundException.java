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

package org.codehaus.grepo.query.commons.executor;

import org.codehaus.grepo.exception.GrepoException;

/**
 * @author dguggi
 */
public class QueryExecutorNotFoundException extends GrepoException {

    /** SerialVersionUid. */
    private static final long serialVersionUID = -1101587541496738135L;

    /**
     * @param msg The message.
     * @param cause The cause.
     */
    public QueryExecutorNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * @param msg The message.
     */
    public QueryExecutorNotFoundException(String msg) {
        super(msg);
    }

}
