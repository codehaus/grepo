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

package org.codehaus.grepo.query.jpa;

import org.codehaus.grepo.query.jpa.config.RepositoryBeanTest;
import org.codehaus.grepo.query.jpa.config.RepositoryScan1Test;
import org.codehaus.grepo.query.jpa.config.RepositoryScan2Test;
import org.codehaus.grepo.query.jpa.config.RepositoryScan3Test;
import org.codehaus.grepo.query.jpa.converter.ConverterRepositoryTest;
import org.codehaus.grepo.query.jpa.executor.ExecutorRepositoryTest;
import org.codehaus.grepo.query.jpa.generator.GeneratorRepositoryTest;
import org.codehaus.grepo.query.jpa.repository.JpaRepositoryTest;
import org.codehaus.grepo.query.jpa.validator.ValidatorRepositoryTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author dguggi
 */
@RunWith(Suite.class)
@SuiteClasses({
    ConverterRepositoryTest.class, ExecutorRepositoryTest.class, GeneratorRepositoryTest.class,
    JpaRepositoryTest.class, ValidatorRepositoryTest.class, RepositoryBeanTest.class,
    RepositoryScan1Test.class, RepositoryScan2Test.class, RepositoryScan3Test.class })
public class AllTests {

}
