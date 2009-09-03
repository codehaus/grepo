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

/**
 * @author dguggi
 */
public class TestResultConverter implements ResultConverter<Object> {

    /** The return value. */
    private static Object returnValue;

    public static void setReturnValue(Object returnValue) {
        TestResultConverter.returnValue = returnValue;
    }

    public static Object getReturnValue() {
        return returnValue;
    }

    /**
     * Reset converter.
     */
    public static void reset() {
        setReturnValue(null);
    }

    /**
     * {@inheritDoc}
     */
    public Object convert(Object o) {
        return getReturnValue();
    }

}
