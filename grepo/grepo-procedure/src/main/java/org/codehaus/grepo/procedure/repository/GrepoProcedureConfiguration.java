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

package org.codehaus.grepo.procedure.repository;

import org.codehaus.grepo.procedure.cache.ProcedureCachingStrategy;
import org.codehaus.grepo.procedure.compile.ProcedureCompilationStrategy;
import org.codehaus.grepo.procedure.input.ProcedureInputGenerationStrategy;
import org.codehaus.grepo.statistics.repository.GrepoStatisticsConfiguration;
import org.springframework.util.Assert;

/**
 * @author dguggi
 */
public class GrepoProcedureConfiguration extends GrepoStatisticsConfiguration {

    private ProcedureCachingStrategy cachingStrategy;
    private ProcedureCompilationStrategy compilationStrategy;
    private ProcedureInputGenerationStrategy inputGenerationStrategy;


    public ProcedureCachingStrategy getCachingStrategy() {
        return cachingStrategy;
    }

    public void setCachingStrategy(ProcedureCachingStrategy cachingStrategy) {
        this.cachingStrategy = cachingStrategy;
    }

    public ProcedureCompilationStrategy getCompilationStrategy() {
        return compilationStrategy;
    }

    public void setCompilationStrategy(ProcedureCompilationStrategy compilationStrategy) {
        this.compilationStrategy = compilationStrategy;
    }

    public ProcedureInputGenerationStrategy getInputGenerationStrategy() {
        return inputGenerationStrategy;
    }

    public void setInputGenerationStrategy(ProcedureInputGenerationStrategy inputGenerationStrategy) {
        this.inputGenerationStrategy = inputGenerationStrategy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        super.validate();
        Assert.notNull(inputGenerationStrategy, "inputGenerationStrategy must not be null");
        Assert.notNull(compilationStrategy, "compilationStrategy must not be null");
        Assert.notNull(cachingStrategy, "cachingStrategy must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateMethodInterceptor() {
        super.validateMethodInterceptor();
        Assert.isInstanceOf(GenericProcedureMethodInterceptor.class, getMethodInterceptor());
    }

}
