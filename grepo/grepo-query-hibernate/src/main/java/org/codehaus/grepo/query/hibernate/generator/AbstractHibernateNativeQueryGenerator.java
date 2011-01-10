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

import org.hibernate.type.Type;

/**
 * @author dguggi
 */
public abstract class AbstractHibernateNativeQueryGenerator
    extends AbstractHibernateQueryGenerator implements HibernateNativeQueryGenerator {

    /** SerialVersionUid. */
    private static final long serialVersionUID = 3986680668120013955L;

    /** The entities map. */
    private Map<Class<?>, String> entities;

    /** The joins map. */
    private Map<String, String> joins;

    /** The scalars map. */
    private Map<String, Type> scalars;

    /**
     * {@inheritDoc}
     */
    public Map<Class<?>, String> getEntities() {
        return entities;
    }

    protected void setEntities(Map<Class<?>, String> entities) {
        this.entities = entities;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getJoins() {
        return joins;
    }

    protected void setJoins(Map<String, String> joins) {
        this.joins = joins;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Type> getScalars() {
        return scalars;
    }

    protected void setScalars(Map<String, Type> scalars) {
        this.scalars = scalars;
    }

    /**
     * @param clazz The class to add.
     * @param alias The alias to add.
     */
    protected void addEntity(Class<?> clazz, String alias) {
        if (entities == null) {
            entities = new HashMap<Class<?>, String>();
        }
        entities.put(clazz, alias);
    }

    /**
     * @param clazz The class to add.
     */
    protected void addEntity(Class<?> clazz) {
        addEntity(clazz, null);
    }

    /**
     * @param alias The alias to add.
     * @param type The type to add.
     */
    protected void addScalar(String alias, Type type) {
        if (scalars == null) {
            scalars = new LinkedHashMap<String, Type>();
        }
        scalars.put(alias, type);
    }

    /**
     * @param alias The alias to add.
     */
    protected void addScalar(String alias) {
        addScalar(alias, null);
    }

    /**
     * @param alias The alias to add.
     * @param path The path to add.
     */
    protected void addJoin(String alias, String path) {
        if (joins == null) {
            joins = new HashMap<String, String>();
        }
        joins.put(alias, path);
    }

}
