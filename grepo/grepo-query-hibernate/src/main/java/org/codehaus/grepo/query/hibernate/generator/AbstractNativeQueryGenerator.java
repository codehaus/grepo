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

package org.codehaus.grepo.query.hibernate.generator;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.grepo.query.commons.annotation.GenericQuery;
import org.codehaus.grepo.query.hibernate.annotation.HibernateQueryOptions;
import org.hibernate.SQLQuery;
import org.hibernate.type.Type;

/**
 * @author dguggi
 */
public abstract class AbstractNativeQueryGenerator extends AbstractQueryGenerator {

    private static final long serialVersionUID = 3986680668120013955L;

    private Map<Class<?>, String> entities;
    private Map<String, String> joins;
    private Map<String, Type> scalars;

    /**
     * {@inheritDoc}
     */
    @Override
    protected final boolean isNativeQuery(GenericQuery genericQuery) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void applyNativeQuerySettings(HibernateQueryOptions queryOptions, SQLQuery query) {
        super.applyNativeQuerySettings(queryOptions, query);

        if (hasEntities()) {
            for (Entry<Class<?>, String> entry : entities.entrySet()) {
                applyAddEntitySetting(entry.getValue(), entry.getKey(), query);
            }
        }
        if (hasScalars()) {
            for (Entry<String, Type> entry : scalars.entrySet()) {
                applyAddScalarSetting(entry.getKey(), entry.getValue(), query);
            }
        }
        if (hasJoins()) {
            for (Entry<String, String> entry : joins.entrySet()) {
                applyAddJoinSetting(entry.getKey(), entry.getValue(), query);
            }
        }
    }

    protected void addEntity(Class<?> clazz, String alias) {
        if (entities == null) {
            entities = new HashMap<Class<?>, String>();
        }
        entities.put(clazz, alias);
    }

    protected void addEntity(Class<?> clazz) {
        addEntity(clazz, null);
    }

    protected void addScalar(String alias, Type type) {
        if (scalars == null) {
            scalars = new LinkedHashMap<String, Type>();
        }
        scalars.put(alias, type);
    }

    protected void addScalar(String alias) {
        addScalar(alias, null);
    }

    protected void addJoin(String alias, String path) {
        if (joins == null) {
            joins = new HashMap<String, String>();
        }
        joins.put(alias, path);
    }

    protected boolean hasEntities() {
        return this.entities != null && !entities.isEmpty();
    }

    protected boolean hasJoins() {
        return this.joins != null && !joins.isEmpty();
    }

    protected boolean hasScalars() {
        return this.scalars != null && !scalars.isEmpty();
    }

}
