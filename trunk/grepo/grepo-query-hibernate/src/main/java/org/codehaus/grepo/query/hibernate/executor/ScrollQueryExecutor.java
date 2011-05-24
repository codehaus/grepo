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

package org.codehaus.grepo.query.hibernate.executor;

import org.codehaus.grepo.query.commons.aop.QueryMethodParameterInfo;
import org.codehaus.grepo.query.hibernate.annotation.GScrollMode;
import org.codehaus.grepo.query.hibernate.annotation.HibernateQueryOptions;
import org.codehaus.grepo.query.hibernate.annotation.HibernateScrollMode;
import org.codehaus.grepo.query.hibernate.context.HibernateQueryExecutionContext;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This executor is used to execute generic "scroll" queries.
 *
 * @author dguggi
 */
public class ScrollQueryExecutor extends AbstractQueryExecutor {

    private static final Logger logger = LoggerFactory.getLogger(ScrollQueryExecutor.class);

    /**
     * {@inheritDoc}
     */
    public Object execute(QueryMethodParameterInfo qmpi, HibernateQueryExecutionContext context) {
        HibernateQueryOptions queryOptions = qmpi.getMethodAnnotation(HibernateQueryOptions.class);
        ScrollMode scrollMode = getScrollMode(qmpi, queryOptions);

        Criteria criteria = createCriteria(qmpi, context);
        if (criteria == null) {
            Query query = createQuery(qmpi, context);
            if (scrollMode == null) {
                return query.scroll();
            }
            return query.scroll(scrollMode);
        }
        if (scrollMode == null) {
            return criteria.scroll();
        }
        return criteria.scroll(scrollMode);
    }

    /**
     * @param qmpi The query method parameter info.
     * @param queryOptions The annotation.
     * @return Returns the scroll mode to use or {@code null} if not specified.
     */
    private ScrollMode getScrollMode(QueryMethodParameterInfo qmpi, HibernateQueryOptions queryOptions) {
        ScrollMode retVal = null;

        if (queryOptions != null && queryOptions.scrollMode() != HibernateScrollMode.UNDEFINED) {
            retVal = queryOptions.scrollMode().value();
        }

        Object gsm = qmpi.getAnnotatedParameter(GScrollMode.class);
        if (gsm != null) {
            if (gsm instanceof HibernateScrollMode) {
                HibernateScrollMode hsm = (HibernateScrollMode)gsm;
                if (hsm != HibernateScrollMode.UNDEFINED) {
                    retVal = hsm.value();
                }
            } else if (gsm instanceof ScrollMode) {
                retVal = (ScrollMode)gsm;
            } else {
                String msg = String.format("Invalid @GScrollMode parameter. The supplied type is not "
                    + "of type 'ScrollMode' or 'HibernateScrollMode' (value='%s', class='%s', method='%s')",
                    gsm, gsm.getClass().getName(), qmpi.getMethodName());
                throw new InvalidScrollModeParameterException(msg);
            }
        }

        if (retVal != null) {
            logger.debug("Using scrollMode: {}", retVal);
        }

        return retVal;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isReadOnlyOperation() {
        return true;
    }
}
