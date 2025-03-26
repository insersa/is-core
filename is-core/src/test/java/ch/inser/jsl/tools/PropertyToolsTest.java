/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */

package ch.inser.jsl.tools;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author INSER SA
 *
 */
public class PropertyToolsTest {

    /**
     * Test method for
     * {@link ch.inser.jsl.tools.PropertyTools#getInteger(java.lang.String)}.
     */
    @Test
    public void testGetInteger() {
        assertTrue(PropertyTools.getInteger(null) == null);
        assertTrue(PropertyTools.getInteger("") == null);
        Integer integer = PropertyTools.getInteger("123");
        assertTrue(integer == 123);
    }

    /**
     * Test method for
     * {@link ch.inser.jsl.tools.PropertyTools#getInteger(java.lang.String)}.
     */
    @Test(expected = NumberFormatException.class)
    public void testGetIntegerNumberFormatException1() {
        PropertyTools.getInteger("abc");
    }

    /**
     * Test method for
     * {@link ch.inser.jsl.tools.PropertyTools#getInteger(java.lang.String)}.
     */
    @Test(expected = NumberFormatException.class)
    public void testGetIntegerNumberFormatException2() {
        PropertyTools.getInteger("123456789123456789");
    }

    /**
     * Test method for
     * {@link ch.inser.jsl.tools.PropertyTools#getString(java.lang.String)}.
     */
    @Test
    public void testGetString() {
        // TODO
    }

    /**
     * Test method for
     * {@link ch.inser.jsl.tools.PropertyTools#getLong(java.lang.String)}.
     */
    @Test
    public void testGetLong() {
        // TODO
    }

    /**
     * Test method for
     * {@link ch.inser.jsl.tools.PropertyTools#getDouble(java.lang.String)}.
     */
    @Test
    public void testGetDouble() {
        // TODO
    }

    /**
     * Test method for
     * {@link ch.inser.jsl.tools.PropertyTools#getTimestamp(java.lang.String)}.
     */
    @Test
    public void testGetTimestamp() {
        // TODO
    }

    /**
     * Test method for
     * {@link ch.inser.jsl.tools.PropertyTools#getBoolean(java.lang.String)}.
     */
    @Test
    public void testGetBoolean() {
        // TODO
    }

    /**
     * Test method for
     * {@link ch.inser.jsl.tools.PropertyTools#getDate(java.lang.String)}.
     */
    @Test
    public void testGetDate() {
        // TODO
    }

    /**
     * Test method for
     * {@link ch.inser.jsl.tools.PropertyTools#getVOString(java.lang.Object)}.
     */
    @Test
    public void testGetVOString() {
        // TODO
    }

    /**
     * Test method for
     * {@link ch.inser.jsl.tools.PropertyTools#arePropertiesNotEqual(java.lang.Object, java.lang.Object)}
     * .
     */
    @Test
    public void testArePropertiesNotEqual() {
        // TODO
    }

    /**
     * Test method for
     * {@link ch.inser.jsl.tools.PropertyTools#getObjectCopy(java.lang.Integer)}
     * .
     */
    @Test
    public void testGetObjectCopyInteger() {
        // TODO
    }

    /**
     * Test method for
     * {@link ch.inser.jsl.tools.PropertyTools#getObjectCopy(java.lang.String)}.
     */
    @Test
    public void testGetObjectCopyString() {
        // TODO
    }

    /**
     * Test method for
     * {@link ch.inser.jsl.tools.PropertyTools#getObjectCopy(java.lang.Long)}.
     */
    @Test
    public void testGetObjectCopyLong() {
        // TODO
    }

    /**
     * Test method for
     * {@link ch.inser.jsl.tools.PropertyTools#getObjectCopy(java.lang.Double)}.
     */
    @Test
    public void testGetObjectCopyDouble() {
        // TODO
    }

    /**
     * Test method for
     * {@link ch.inser.jsl.tools.PropertyTools#getObjectCopy(java.sql.Date)}.
     */
    @Test
    public void testGetObjectCopyDate() {
        // TODO
    }

    /**
     * Test method for
     * {@link ch.inser.jsl.tools.PropertyTools#getObjectCopy(java.sql.Timestamp)}
     * .
     */
    @Test
    public void testGetObjectCopyTimestamp() {
        // TODO
    }

    /**
     * Test method for
     * {@link ch.inser.jsl.tools.PropertyTools#getObjectCopy(byte[])}.
     */
    @Test
    public void testGetObjectCopyByteArray() {
        // TODO
    }

    /**
     * Test method for
     * {@link ch.inser.jsl.tools.PropertyTools#getObjectCopy(java.lang.Boolean)}
     * .
     */
    @Test
    public void testGetObjectCopyBoolean() {
        // TODO
    }

    /**
     * Test method for
     * {@link ch.inser.jsl.tools.PropertyTools#getByte(java.sql.Blob)}.
     */
    @Test
    public void testGetByte() {
        // TODO
    }

}
