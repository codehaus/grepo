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

package org.codehaus.grepo.query.hibernate.repository;

import org.codehaus.grepo.query.commons.repository.GenericQueryRepository;

/**
 * Marker interface for hibernate generic repositories.
 *
 * @author dguggi
 *
 * @param <T>  The main entity type.
 */
public interface HibernateRepository<T> extends GenericQueryRepository<T> {

}
