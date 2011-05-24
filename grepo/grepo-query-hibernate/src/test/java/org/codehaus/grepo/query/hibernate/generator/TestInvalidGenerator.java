/*
 * Copyright 2011 Grepo Committers.
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

package org.codehaus.grepo.query.hibernate.generator;

import org.codehaus.grepo.query.commons.aop.QueryMethodParameterInfo;
import org.codehaus.grepo.query.commons.context.PlaceHolderQueryExecutionContext;
import org.codehaus.grepo.query.commons.generator.QueryGenerator;

/**
 * @author dguggi
 */
public class TestInvalidGenerator implements QueryGenerator<String, PlaceHolderQueryExecutionContext> {

    private static final long serialVersionUID = 3624308566143251909L;

    /**
     * {@inheritDoc}
     */
    public String generate(QueryMethodParameterInfo qmpi, PlaceHolderQueryExecutionContext context) {
        return null;
    }

}
