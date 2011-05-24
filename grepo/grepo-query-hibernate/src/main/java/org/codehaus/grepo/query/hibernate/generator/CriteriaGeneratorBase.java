/*
 * Copyright 2011 Grepo Committers.
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

import org.codehaus.grepo.query.commons.annotation.GenericQuery;
import org.codehaus.grepo.query.commons.aop.QueryMethodParameterInfo;
import org.codehaus.grepo.query.commons.generator.GeneratorUtils;
import org.codehaus.grepo.query.hibernate.annotation.HibernateQueryOptions;
import org.codehaus.grepo.query.hibernate.context.HibernateQueryExecutionContext;
import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.transform.ResultTransformer;
import org.springframework.orm.hibernate3.SessionFactoryUtils;

/**
 * @author dguggi
 */
public abstract class CriteriaGeneratorBase implements HibernateCriteriaGenerator {

    private static final long serialVersionUID = 7484243914613809647L;

    /**
     * {@inheritDoc}
     */
    public Criteria generate(QueryMethodParameterInfo qmpi, HibernateQueryExecutionContext context) {
        CriteriaSpecification criteriaSpec = createCriteria(qmpi, context);

        Criteria criteria = null;
        if (criteriaSpec instanceof DetachedCriteria) {
            criteria = ((DetachedCriteria)criteriaSpec).getExecutableCriteria(context.getSession());
        } else {
            criteria = (Criteria)criteriaSpec;
        }

        applyCriteriaSettings(qmpi, context, criteria);

        return criteria;
    }

    /**
     * Factory method to create a criteria object.
     *
     * @param qmpi The query method parameter info.
     * @param context The context.
     * @return Returns the specification.
     */
    protected abstract CriteriaSpecification createCriteria(QueryMethodParameterInfo qmpi,
                                                            HibernateQueryExecutionContext context);

    /**
     * Applies required settings to the given {@code criteria} instance.
     *
     * @param qmpi The query method parameter info.
     * @param context The context.
     * @param criteria The criteria.
     */
    protected void applyCriteriaSettings(QueryMethodParameterInfo qmpi, HibernateQueryExecutionContext context,
                                         Criteria criteria) {
        GenericQuery genericQuery = qmpi.getMethodAnnotation(GenericQuery.class);
        HibernateQueryOptions queryOptions = qmpi.getMethodAnnotation(HibernateQueryOptions.class);

        applyResultTransformerSetting(queryOptions, criteria);
        applyFetchSizeSetting(queryOptions, context, criteria);
        applyFirstResultSetting(genericQuery, qmpi, criteria);
        appyMaxResultsSetting(genericQuery, qmpi, context, criteria);
        applyCachingSetting(queryOptions, context, criteria);
        applyTimeoutSetting(context, criteria);
    }

    protected void applyTimeoutSetting(HibernateQueryExecutionContext context, Criteria criteria) {
        SessionFactoryUtils.applyTransactionTimeout(criteria, context.getSessionFactory());
    }

    protected void applyCachingSetting(HibernateQueryOptions queryOptions, HibernateQueryExecutionContext context,
                                       Criteria criteria) {
        if (HibernateGeneratorUtils.isCachingEnabled(queryOptions, context)) {
            criteria.setCacheable(true);
            String cacheRegion = HibernateGeneratorUtils.getCacheRegion(queryOptions, context);
            if (cacheRegion != null) {
                criteria.setCacheRegion(cacheRegion);
            }
        }
    }

    protected void appyMaxResultsSetting(GenericQuery genericQuery, QueryMethodParameterInfo qmpi,
                                         HibernateQueryExecutionContext context, Criteria criteria) {
        Integer maxResults = GeneratorUtils.getMaxResults(qmpi, genericQuery, context.getMaxResults());
        if (maxResults != null) {
            criteria.setMaxResults(maxResults);
        }
    }

    protected void applyFirstResultSetting(GenericQuery genericQuery, QueryMethodParameterInfo qmpi, //
                                           Criteria criteria) {
        Integer firstResult = GeneratorUtils.getFirstResult(qmpi, genericQuery);
        if (firstResult != null) {
            criteria.setFirstResult(firstResult);
        }
    }

    protected void applyFetchSizeSetting(HibernateQueryOptions queryOptions, HibernateQueryExecutionContext context,
                                         Criteria criteria) {
        Integer fetchSize = HibernateGeneratorUtils.getFetchSize(queryOptions, context.getFetchSize());
        if (fetchSize != null) {
            criteria.setFetchSize(fetchSize);
        }
    }

    protected void applyResultTransformerSetting(HibernateQueryOptions queryOptions, Criteria criteria) {
        ResultTransformer resultTransformer = HibernateGeneratorUtils.getResultTransformer(queryOptions);
        if (resultTransformer != null) {
            criteria.setResultTransformer(resultTransformer);
        }
    }
}
