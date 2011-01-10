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

package org.codehaus.grepo.query.jpa.generator;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.grepo.query.commons.generator.AbstractQueryGenerator;

/**
 * @author dguggi
 */
public abstract class AbstractJpaQueryGenerator
    extends AbstractQueryGenerator<JpaQueryParam> implements JpaQueryGenerator {

    /** SerialVersionUid. */
    private static final long serialVersionUID = 2978960650840073418L;

    /** The hints. */
    private Map<String, Object> hints;

    public Map<String, Object> getHints() {
        return hints;
    }

    /**
     * @param key The key.
     * @param value The value.
     */
    protected void addHint(String key, Object value) {
        if (hints == null) {
            hints = new HashMap<String, Object>();
        }
        hints.put(key, value);
    }

    protected void setHints(Map<String, Object> hints) {
        this.hints = hints;
    }

}
