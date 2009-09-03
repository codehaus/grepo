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

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.dom4j.Node;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.Mapping;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.type.ForeignKeyDirection;
import org.hibernate.type.Type;

/**
 * @author dguggi
 */
@SuppressWarnings("PMD")
public final class PlaceHolderType implements Type {

    /** SerialVersionUid. */
    private static final long serialVersionUID = -8902019743730289360L;

    /**
     * {@inheritDoc}
     */
    public Object assemble(Serializable cached, SessionImplementor session, Object owner) throws HibernateException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void beforeAssemble(Serializable cached, SessionImplementor session) {
    }

    /**
     * {@inheritDoc}
     */
    public int compare(Object x, Object y, EntityMode entityMode) {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public Object deepCopy(Object value, EntityMode entityMode, SessionFactoryImplementor factory)
            throws HibernateException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Serializable disassemble(Object value, SessionImplementor session, Object owner) throws HibernateException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public int getColumnSpan(Mapping mapping) throws MappingException {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public int getHashCode(Object x, EntityMode entityMode) throws HibernateException {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public int getHashCode(Object x, EntityMode entityMode, SessionFactoryImplementor factory)
            throws HibernateException {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Class<?> getReturnedClass() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Type getSemiResolvedType(SessionFactoryImplementor factory) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Object hydrate(ResultSet rs, String[] names, SessionImplementor session, Object owner)
            throws HibernateException, SQLException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAnyType() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAssociationType() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isCollectionType() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isComponentType() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDirty(Object old, Object current, SessionImplementor session) throws HibernateException {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session)
            throws HibernateException {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEntityType() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEqual(Object x, Object y, EntityMode entityMode) throws HibernateException {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEqual(Object x, Object y, EntityMode entityMode, SessionFactoryImplementor factory)
            throws HibernateException {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isModified(Object oldHydratedState, Object currentState, boolean[] checkable,
            SessionImplementor session) throws HibernateException {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isMutable() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSame(Object x, Object y, EntityMode entityMode) throws HibernateException {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isXMLElement() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
            throws HibernateException, SQLException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Object nullSafeGet(ResultSet rs, String name, SessionImplementor session, Object owner)
            throws HibernateException, SQLException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
            throws HibernateException, SQLException {
    }

    /**
     * {@inheritDoc}
     */
    public void nullSafeSet(PreparedStatement st, Object value, int index, boolean[] settable,
            SessionImplementor session) throws HibernateException, SQLException {

    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Object replace(Object original, Object target, SessionImplementor session, Object owner, Map copyCache)
            throws HibernateException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Object replace(Object original, Object target, SessionImplementor session, Object owner, Map copyCache,
            ForeignKeyDirection foreignKeyDirection) throws HibernateException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Object resolve(Object value, SessionImplementor session, Object owner) throws HibernateException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Object semiResolve(Object value, SessionImplementor session, Object owner) throws HibernateException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void setToXMLNode(Node node, Object value, SessionFactoryImplementor factory) throws HibernateException {
    }

    /**
     * {@inheritDoc}
     */
    public int[] sqlTypes(Mapping mapping) throws MappingException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean[] toColumnNullness(Object value, Mapping mapping) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String toLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {
        return null;
    }

}
