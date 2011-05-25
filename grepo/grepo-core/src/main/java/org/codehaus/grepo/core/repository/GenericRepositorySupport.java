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

package org.codehaus.grepo.core.repository;

import org.codehaus.grepo.core.converter.ResultConversionService;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author dguggi
 */
public class GenericRepositorySupport {

    /** The application context. */
    private ApplicationContext applicationContext;

    /** The transaction template to use (optional). */
    private TransactionTemplate transactionTemplate;

    /** The read-only transaction template to use (optional). */
    private TransactionTemplate readOnlyTransactionTemplate;

    /** The mandatory proxy interface. */
    private Class<?> proxyInterface;

    private GrepoConfiguration configuration;

    /** Default constructor. */
    protected GenericRepositorySupport() {
        // protected in order to ensure that this class cannot be instantiated directly.
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    public TransactionTemplate getTransactionTemplate() {
        return transactionTemplate;
    }

    public TransactionTemplate getReadOnlyTransactionTemplate() {
        return readOnlyTransactionTemplate;
    }

    public void setReadOnlyTransactionTemplate(TransactionTemplate readOnlyTransactionTemplate) {
        this.readOnlyTransactionTemplate = readOnlyTransactionTemplate;
    }

    public Class<?> getProxyInterface() {
        return proxyInterface;
    }

    public void setProxyInterface(Class<?> proxyInterface) {
        this.proxyInterface = proxyInterface;
    }

    public void setConfiguration(GrepoConfiguration configuration) {
        this.configuration = configuration;
    }

    public GrepoConfiguration getConfiguration() {
        return configuration;
    }

    protected ResultConversionService getResultConversionService() {
        return getConfiguration().getResultConversionService();
    }

}
