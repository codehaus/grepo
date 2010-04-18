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

import java.util.Collection;
import java.util.List;

import org.codehaus.grepo.core.annotation.Param;
import org.codehaus.grepo.query.commons.annotation.GenericQuery;
import org.codehaus.grepo.query.hibernate.annotation.EntityClass;
import org.codehaus.grepo.query.hibernate.annotation.HibernateQueryOptions;
import org.codehaus.grepo.query.hibernate.repository.ReadWriteHibernateRepository;

import demo.domain.User;

/**
 * This is the repository for the {@link User} entity.
 *
 * @author dguggi
 */
public interface UserRepository extends ReadWriteHibernateRepository<User, Long> {

    /**
     * Gets an user by username using named hibernate query.
     *
     * @param username The username.
     * @return Returns the user.
     */
    @GenericQuery
    User getByUsername(String username);

    /**
     * Loads an user by username using named hibernate query. Note that
     * getByUsername and loadByUsername use the same query.
     *
     * @param username The username.
     * @return Returns the user.
     */
    @GenericQuery
    User loadByUsername(String username);

    /**
     * Gets an user by username using a specified sql (native) query.
     *
     * @param username The username.
     * @return Returns the user or null.
     */
    @GenericQuery(query = "select * from USERS where username = ?", isNativeQuery = true)
    @HibernateQueryOptions(entityClasses = @EntityClass(clazz = User.class))
    User getByUsernameUsingSQLQuery(String username);

    /**
     * Finds users by usernames using specified hql query with named-parameters.
     * @param usernames The usernames.
     * @return Returns a list of users or an empty list.
     */
    @GenericQuery(query = "from User where username IN (:list)")
    List<User> findByUsernames(@Param("list") Collection<String> usernames);

    /**
     * Finds users by name using a hql query generator.
     *
     * @param firstname The firstname.
     * @param lastname The lastname.
     * @return Returns a list of users or an empty list.
     */
    @GenericQuery(queryGenerator = UserSearchQueryGenerator.class)
    List<User> findUsersByName(@Param("fn") String firstname, @Param("ln") String lastname);

    /**
     * Finds users by name using a criteria generator.
     *
     * @param firstname The firstname.
     * @param lastname The lastname.
     * @return Returns a list of users or an empty list.
     */
    @GenericQuery
    @HibernateQueryOptions(criteriaGenerator = UserSearchCriteriaGenerator.class)
    List<User> findUsersByNameUsingCriteria(@Param("fn") String firstname, @Param("ln") String lastname);

    /**
     * Checks if a given email address already exists in database.
     *
     * @param email The email to check.
     * @return Returns {@code true} if email exists and {@code false} otherwise.
     */
    @GenericQuery
    boolean isExistingEmail(String email);
}
