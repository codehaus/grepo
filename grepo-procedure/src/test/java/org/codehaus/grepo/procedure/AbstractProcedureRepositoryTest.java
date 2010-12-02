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

package org.codehaus.grepo.procedure;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.codehaus.grepo.core.AbstractTransactionalSpringTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author dguggi
 */
public class AbstractProcedureRepositoryTest extends AbstractTransactionalSpringTest {

    /** The jdbc tempalte. */
    @Autowired
    private JdbcTemplate jdbcTemplate;

    protected JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    protected void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * @param fileName The file name.
     * @throws FileNotFoundException in case of errors.
     * @throws IOException in case of errors.
     */
    protected void executeSqlFromFile(String fileName) throws FileNotFoundException, IOException {
        DefaultResourceLoader drl = new DefaultResourceLoader();
        Resource resource = drl.getResource(fileName);
        BufferedReader br = new BufferedReader(new FileReader(resource.getFile()));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        br.close();
        getJdbcTemplate().execute(sb.toString());
    }
}
