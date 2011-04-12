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

import java.io.Serializable;

import org.codehaus.grepo.core.converter.ResultConversionService;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author dguggi
 */
public class GenericRepositorySupport implements Serializable {

    /** SerialVersionUid. */
    private static final long serialVersionUID = -7232606145031376090L;

    /** The application context. */
    private transient ApplicationContext applicationContext;

    /** The transaction template to use. */
    private TransactionTemplate transactionTemplate;

    /** The read-only transaction template to use (optional). */
    private TransactionTemplate readOnlyTransactionTemplate;

    /** The optional result conversion service (may be auto-detected). */
    private ResultConversionService resultConversionService;

    /** The proxy interface. */
    private Class<?> proxyInterface;

    /** Default constructor. */
    protected GenericRepositorySupport() {
        // protected in order to ensure that this class cannot be instantiated directly.
    }

    protected ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    protected TransactionTemplate getTransactionTemplate() {
        return transactionTemplate;
    }

    protected TransactionTemplate getReadOnlyTransactionTemplate() {
        return readOnlyTransactionTemplate;
    }

    public void setReadOnlyTransactionTemplate(TransactionTemplate readOnlyTransactionTemplate) {
        this.readOnlyTransactionTemplate = readOnlyTransactionTemplate;
    }

    protected ResultConversionService getResultConversionService() {
        return resultConversionService;
    }

    public void setResultConversionService(ResultConversionService resultConversionService) {
        this.resultConversionService = resultConversionService;
    }

    protected Class<?> getProxyInterface() {
        return proxyInterface;
    }

    public void setProxyInterface(Class<?> proxyInterface) {
        this.proxyInterface = proxyInterface;
    }
}
