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

import junit.framework.Assert;

import org.junit.Test;

/**
 * Tests the {@link ResultToIntegerConverter} converter.
 *
 * @author dguggi
 */
public class ResultToIntegerConverterTest {

    /** Test conversions with various types. */
    @Test
    public void testConversions() {
        ResultToIntegerConverter converter = new ResultToIntegerConverter();
        Assert.assertNull(converter.convert(null));
        Assert.assertEquals(Integer.valueOf(1), converter.convert("1"));
        Assert.assertEquals(Integer.valueOf(0), converter.convert("0"));
        Assert.assertEquals(Integer.valueOf(1), converter.convert(1));
        Assert.assertEquals(Integer.valueOf(0), converter.convert(0));
        Assert.assertEquals(Integer.valueOf(1), converter.convert(1L));
        Assert.assertEquals(Integer.valueOf(0), converter.convert(0L));
    }
}
