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

package org.codehaus.grepo.core.repository;

import org.aopalliance.intercept.MethodInterceptor;
import org.codehaus.grepo.core.converter.ResultConversionService;
import org.springframework.util.Assert;

/**
 * @author dguggi
 */
public class GrepoConfiguration {

    private ResultConversionService resultConversionService;
    private MethodInterceptor methodInterceptor;

    public ResultConversionService getResultConversionService() {
        return resultConversionService;
    }

    public void setResultConversionService(ResultConversionService resultConversionService) {
        this.resultConversionService = resultConversionService;
    }

    public MethodInterceptor getMethodInterceptor() {
        return methodInterceptor;
    }

    public void setMethodInterceptor(MethodInterceptor methodInterceptor) {
        this.methodInterceptor = methodInterceptor;
    }

    public void validate() {
        validateMethodInterceptor();
    }

    protected void validateMethodInterceptor() {
        Assert.notNull(methodInterceptor, "methodInterceptor must not be null");
    }
}
