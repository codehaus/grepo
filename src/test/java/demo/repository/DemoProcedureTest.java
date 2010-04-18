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

package demo.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

/**
 * Tests the {@link DemoProcedure}.
 *
 * @author dguggi
 */
@SuppressWarnings("PMD")
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({
    DependencyInjectionTestExecutionListener.class,
    TransactionalTestExecutionListener.class })
@Transactional
@ContextConfiguration(locations = "classpath:META-INF/spring/application-context.xml")
public class DemoProcedureTest {

    /** Repository to test. */
    @Autowired
    private DemoProcedure repo;

    /** Tests {@link DemoProcedure#executeDemoProcedure(String, int)}. */
    @Test
    public void testExecuteDemoProcedure() {
        repo.executeDemoProcedure("test", 1);
    }

    /** Tests {@link DemoProcedure#executeDemoFunction(String, int)}. */
    @Test
    public void testExecuteDemoFunction() {
        repo.executeDemoFunction("test", 1);
    }

}
