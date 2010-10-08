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

import junit.framework.Assert;

import org.junit.Test;

/**
 * Tests the {@link org.codehaus.grepo.core.converter.ResultToLongConverter} converter.
 *
 * @author dguggi
 */
public class ResultToLongConverterTest {

    /** Test conversions with various types. */
    @Test
    public void testConversions() {
        ResultToLongConverter converter = new ResultToLongConverter();
        Assert.assertNull(converter.convert(null));
        Assert.assertEquals(Long.valueOf(1), converter.convert("1"));
        Assert.assertEquals(Long.valueOf(0), converter.convert("0"));
        Assert.assertEquals(Long.valueOf(1), converter.convert(1));
        Assert.assertEquals(Long.valueOf(0), converter.convert(0));
        Assert.assertEquals(Long.valueOf(1), converter.convert(1L));
        Assert.assertEquals(Long.valueOf(0), converter.convert(0L));
    }
}
