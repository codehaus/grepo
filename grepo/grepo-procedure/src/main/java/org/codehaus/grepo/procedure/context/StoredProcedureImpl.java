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

package org.codehaus.grepo.procedure.context;

import javax.sql.DataSource;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.jdbc.object.StoredProcedure;

/**
 * Default stored procedure implementation.
 *
 * @author dguggi
 */
public class StoredProcedureImpl extends StoredProcedure {

    /**
     * @param ds The datasource.
     * @param name The name.
     */
    public StoredProcedureImpl(final DataSource ds, final String name) {
        super(ds, name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("sql", getSql()).toString();
    }
}
