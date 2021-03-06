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

import java.math.BigDecimal;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Tests the {@link org.codehaus.grepo.core.converter.ResultToBooleanConverter} converter.
 *
 * @author dguggi
 */
public class ResultToBooleanConverterTest {

    /** Test conversions with various types. */
    @Test
    public void testConversions() {
        ResultToBooleanConverter converter = new ResultToBooleanConverter();
        Assert.assertFalse(converter.convert(null));
        Assert.assertTrue(converter.convert(Boolean.TRUE));
        Assert.assertFalse(converter.convert(Boolean.FALSE));

        Assert.assertTrue(converter.convert("t"));
        Assert.assertTrue(converter.convert("true"));
        Assert.assertTrue(converter.convert("yes"));
        Assert.assertTrue(converter.convert("T"));
        Assert.assertTrue(converter.convert("TRUE"));
        Assert.assertTrue(converter.convert("YES"));
        Assert.assertTrue(converter.convert("1"));
        Assert.assertFalse(converter.convert("no"));
        Assert.assertFalse(converter.convert("false"));

        Assert.assertTrue(converter.convert(1L));
        Assert.assertTrue(converter.convert(100L));
        Assert.assertFalse(converter.convert(0L));
        Assert.assertFalse(converter.convert(-10L));

        Assert.assertTrue(converter.convert(1));
        Assert.assertTrue(converter.convert(100));
        Assert.assertFalse(converter.convert(0));
        Assert.assertFalse(converter.convert(-10));

        Assert.assertTrue(converter.convert(new BigDecimal(100)));
        Assert.assertTrue(converter.convert(new BigDecimal("100")));
        Assert.assertTrue(converter.convert(new BigDecimal(100L)));
        Assert.assertFalse(converter.convert(new BigDecimal(-1)));
    }
}
