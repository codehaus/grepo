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

package org.codehaus.grepo.core.converter;

import org.codehaus.grepo.core.aop.MethodParameterInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link ResultConversionService}.
 *
 * @author dguggi
 */
public class ResultConversionServiceImpl implements ResultConversionService {

    private static final Logger logger = LoggerFactory.getLogger(ResultConversionServiceImpl.class);

    private ResultConverterFindingStrategy converterFindingStrategy;

    /**
     * {@inheritDoc}
     */
    public Object convert(MethodParameterInfo mpi, Class<? extends ResultConverter<?>> specifiedConverter,
        Object result) {
        Class<? extends ResultConverter<?>> converter = getConverterFindingStrategy().findConverter(specifiedConverter,
            mpi, result);

        if (converter == null) {
            // no converter found (no conversion performed)...
            return result;
        } else {
            if (logger.isDebugEnabled()) {
                Object[] params = new Object[] {(result == null ? "null" : result.getClass().getName()),
                    result, converter.getName()};
                logger.debug("Doing conversion for result (class={}, value={}) with converter '{}'", params);
            }

            try {
                ResultConverter<?> rc = converter.newInstance();
                Object convertedResult = rc.convert(result);
                logger.debug("Conversion result is '{}'", convertedResult);
                return convertedResult;
            } catch (InstantiationException e) {
                String msg = String.format("Unable to create new instance of '%s': '%s'", converter.getName(), e
                    .getMessage());
                throw new ConversionException(msg, e);
            } catch (IllegalAccessException e) {
                String msg = String.format("Unable to create new instance of '%s': '%s'", converter.getName(), e
                    .getMessage());
                throw new ConversionException(msg, e);
            }
        }
    }

    public void setConverterFindingStrategy(ResultConverterFindingStrategy converterFindingStrategy) {
        this.converterFindingStrategy = converterFindingStrategy;
    }

    protected ResultConverterFindingStrategy getConverterFindingStrategy() {
        return converterFindingStrategy;
    }

}
