/*
 * Copyright 2010 Grepo Committers.
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

package org.codehaus.grepo.query.hibernate.config;

import org.codehaus.grepo.query.hibernate.TestEntity;
import org.codehaus.grepo.query.hibernate.repository.ReadWriteHibernateRepositoryImpl;
import org.springframework.stereotype.Repository;

/**
 * This repository will be generated with default bean name 'testRepository4Impl'.
 *
 * @author dguggi
 */
@Repository
public class ScanTestRepository4Impl extends ReadWriteHibernateRepositoryImpl<TestEntity, Long>
    implements ScanTestRepository4 {

    /** SerialVersionUid. */
    private static final long serialVersionUID = 7087665762060165724L;

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("PMD")
    public void doSomethingSpecial() {
    }

}
