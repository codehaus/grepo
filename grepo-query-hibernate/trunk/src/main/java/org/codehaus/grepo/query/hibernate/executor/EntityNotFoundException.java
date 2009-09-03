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

package org.codehaus.grepo.query.hibernate.executor;

import java.util.List;

import org.codehaus.grepo.exception.GrepoException;

/**
 * @author dguggi
 */
public class EntityNotFoundException extends GrepoException {

    /** SerialVersionUid. */
    private static final long serialVersionUID = 3703482089206783842L;

    /** A list of query parameters. */
    private List<Object> parameters;

    /**
     * @param msg The message to set.
     * @param parameters The parameters to set.
     */
    public EntityNotFoundException(String msg, List<Object> parameters) {
        super(msg);
        this.parameters = parameters;
    }

    public List<Object> getParameters() {
        return parameters;
    }

    protected void setParameters(List<Object> parameters) {
        this.parameters = parameters;
    }

}
