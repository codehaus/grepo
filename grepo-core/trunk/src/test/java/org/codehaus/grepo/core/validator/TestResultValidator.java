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

package org.codehaus.grepo.core.validator;

/**
 * @author dguggi
 */
public class TestResultValidator implements ResultValidator {

    /** Exception which should be thrown. */
    private static Exception exceptionToBeThrown;

    public static Exception getExceptionToBeThrown() {
        return exceptionToBeThrown;
    }

    public static void setExceptionToBeThrown(Exception exceptionToBeThrown) {
        TestResultValidator.exceptionToBeThrown = exceptionToBeThrown;
    }

    /**
     * Reset validator.
     */
    public static void reset() {
        setExceptionToBeThrown(null);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("PMD")
    public void validate(Object result) throws Exception {
        if (getExceptionToBeThrown() != null) {
            throw getExceptionToBeThrown();
        }
    }

}
