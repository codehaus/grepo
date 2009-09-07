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

package org.codehaus.grepo.query.jpa.executor;

import java.util.Collection;

import javax.persistence.Query;

import org.codehaus.grepo.query.commons.executor.QueryDescriptor;

/**
 * @author dguggi
 */
public class JpaQueryDescriptor extends QueryDescriptor<Query, DynamicNamedJpaParam> {
    /**
     * @param query The query.
     * @param generatedQuery The flag.
     * @param params The params.
     */
    public JpaQueryDescriptor(Query query, boolean generatedQuery, Collection<DynamicNamedJpaParam> params) {
        super(query, generatedQuery, params);
    }

    /**
     * @param query The query.
     * @param generatedQuery The flag.
     */
    public JpaQueryDescriptor(Query query, boolean generatedQuery) {
        super(query, generatedQuery);
    }

}
