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

package org.codehaus.grepo.query.hibernate.context;

import org.codehaus.grepo.query.commons.context.QueryExecutionContext;
import org.codehaus.grepo.query.hibernate.annotation.HibernateCaching;
import org.codehaus.grepo.query.hibernate.type.ArgumentTypeFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Context for execution of hibernate queries.
 *
 * @author dguggi
 */
public interface HibernateQueryExecutionContext extends QueryExecutionContext {
    /**
     * @return Returns the hibernate session used for this exection.
     */
    Session getSession();

    /**
     * @return Returns the caching property.
     */
    HibernateCaching getCaching();

    /**
     * @return Returns the cache region.
     */
    String getCacheRegion();

    /**
     * @return Returns the hibernate session factory.
     */
    SessionFactory getSessionFactory();

    /**
     * @return Returns the fetch size.
     */
    Integer getFetchSize();

    /**
     * @return Returns the argument type factory.
     */
    ArgumentTypeFactory getArgumentTypeFactory();
}
