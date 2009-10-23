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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.grepo.query.commons.annotation.GenericQuery;
import org.codehaus.grepo.query.commons.aop.QueryMethodParameterInfo;
import org.codehaus.grepo.query.hibernate.annotation.GScrollMode;
import org.codehaus.grepo.query.hibernate.annotation.HibernateQueryOptions;
import org.codehaus.grepo.query.hibernate.annotation.HibernateScrollMode;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.ScrollMode;

/**
 * This executor is used to execute generic "scroll" queries.
 *
 * @author dguggi
 */
public class ScrollQueryExecutor extends AbstractHibernateQueryExecutor {
    /** The logger for this class. */
    private static final Log LOG = LogFactory.getLog(ScrollQueryExecutor.class);

    /**
     * {@inheritDoc}
     */
    public Object execute(QueryMethodParameterInfo qmpi, HibernateQueryExecutionContext context) {
        GenericQuery genericQuery = qmpi.getMethodAnnotation(GenericQuery.class);
        HibernateQueryOptions queryOptions = qmpi.getMethodAnnotation(HibernateQueryOptions.class);

        ScrollMode scrollMode = getScrollMode(qmpi, queryOptions);

        Object result = null;
        if (hasValidCriteriaGenerator(queryOptions)) {
            Criteria criteria = prepareCriteria(genericQuery, qmpi, context);
            if (scrollMode == null) {
                result = criteria.scroll();
            } else {
                result = criteria.scroll(scrollMode);
            }
        } else {
            Query query = prepareQuery(genericQuery, qmpi, context);
            if (scrollMode == null) {
                result = query.scroll();
            } else {
                result = query.scroll(scrollMode);
            }
        }

        return result;
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

        if (retVal != null && LOG.isTraceEnabled()) {
            LOG.trace("Using scrollMode " + retVal);
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
