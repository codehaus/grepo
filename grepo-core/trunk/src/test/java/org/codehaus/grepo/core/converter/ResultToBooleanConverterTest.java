/* ***************************************************************************
 * Copyright (c) 2007 BearingPoint INFONOVA GmbH, Austria.
 *
 * This software is the confidential and proprietary information of
 * BearingPoint INFONOVA GmbH, Austria. You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with INFONOVA.
 *
 * BEARINGPOINT INFONOVA MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
 * A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. BEARINGPOINT INFONOVA SHALL
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *****************************************************************************/

package org.codehaus.grepo.core.converter;

import java.math.BigDecimal;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Tests the {@link ResultToBooleanConverter} converter.
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
