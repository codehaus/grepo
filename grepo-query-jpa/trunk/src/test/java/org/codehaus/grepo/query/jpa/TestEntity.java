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

import java.io.Serializable;

/**
 * @author dguggi
 */
public class TestEntity implements Serializable {

    /** SerialVersionUid. */
    private static final long serialVersionUID = 4751237812446784921L;

    /** The id. */
    private Long id;

    /** The username. */
    private String username;

    /** The type. */
    private Integer type;

    /** The firstname. */
    private String firstname;

    /**
     * @param username The username to set.
     * @param type The type to set.
     */
    public TestEntity(final String username, final Integer type) {
        this.username = username;
        this.type = type;
    }

    /**
     * @param username The username to set.
     * @param type The type to set.
     * @param firstname The firstname to set.
     */
    public TestEntity(final String username, final Integer type, final String firstname) {
        this(username, type);
        this.firstname = firstname;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public Integer getType() {
        return type;
    }

    public void setType(final Integer type) {
        this.type = type;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(final String firstname) {
        this.firstname = firstname;
    }

}
