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

package org.codehaus.grepo.core.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests {@link org.codehaus.grepo.core.util.ClassUtils}.
 *
 * @author dguggi
 */
public class ClassUtilsTest {

    /** Tests {@link org.codehaus.grepo.core.util.ClassUtils#isAssignableFrom(Class, Class)}. */
    @Test
    public void testIsAssignableFrom() {
        Assert.assertTrue(ClassUtils.isAssignableFrom(Boolean.class, boolean.class));
        Assert.assertTrue(ClassUtils.isAssignableFrom(boolean.class, Boolean.class));
        Assert.assertTrue(ClassUtils.isAssignableFrom(Long.class, long.class));
        Assert.assertTrue(ClassUtils.isAssignableFrom(long.class, Long.class));
        Assert.assertTrue(ClassUtils.isAssignableFrom(Integer.class, int.class));
        Assert.assertTrue(ClassUtils.isAssignableFrom(int.class, Integer.class));
        Assert.assertTrue(ClassUtils.isAssignableFrom(Float.class, float.class));
        Assert.assertTrue(ClassUtils.isAssignableFrom(float.class, Float.class));
        Assert.assertTrue(ClassUtils.isAssignableFrom(Double.class, double.class));
        Assert.assertTrue(ClassUtils.isAssignableFrom(double.class, Double.class));
        Assert.assertTrue(ClassUtils.isAssignableFrom(Character.class, char.class));
        Assert.assertTrue(ClassUtils.isAssignableFrom(char.class, Character.class));
        Assert.assertTrue(ClassUtils.isAssignableFrom(Void.class, void.class));
        Assert.assertTrue(ClassUtils.isAssignableFrom(void.class, Void.class));
        Assert.assertTrue(ClassUtils.isAssignableFrom(Short.class, short.class));
        Assert.assertTrue(ClassUtils.isAssignableFrom(short.class, Short.class));
        Assert.assertTrue(ClassUtils.isAssignableFrom(Byte.class, byte.class));
        Assert.assertTrue(ClassUtils.isAssignableFrom(byte.class, Byte.class));
    }

    /** Tests {@link org.codehaus.grepo.core.util.ClassUtils#isVoidType(Class)}. */
    @Test
    public void testIsVoidType() {
        Assert.assertTrue(ClassUtils.isVoidType(void.class));
        Assert.assertTrue(ClassUtils.isVoidType(Void.class));
        Assert.assertFalse(ClassUtils.isVoidType(String.class));
    }
}
