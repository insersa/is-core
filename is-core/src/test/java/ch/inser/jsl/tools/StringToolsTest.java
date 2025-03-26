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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StringToolsTest {

    @Test
    public void stripNonISO88591Test() {
        assertTrue("La/" + StringTools.stripNonISO88591("L’a"), "La".equals(StringTools.stripNonISO88591("L’a")));
        assertTrue("hjskdg/" + StringTools.stripNonISO88591("‘’hjskdg’’"), "hjskdg".equals(StringTools.stripNonISO88591("‘’hjskdg’’")));
        assertTrue("??zut/" + StringTools.stripNonISO88591("??zut"), "??zut".equals(StringTools.stripNonISO88591("??zut")));
    }

    @Test
    public void isCharSuiteTest() {
        assertTrue(StringTools.isCharSuite("tartampio12333", 3));
        assertFalse(StringTools.isCharSuite("tartampio12333", 4));
        assertFalse(StringTools.isCharSuite("tartampio1233", 3));
    }

    @Test
    public void splitTest() {
        String[] strs = StringTools.split("Une rue,12", " ,");
        assertEquals("Une", strs[0]);
        assertEquals("rue", strs[1]);
        assertEquals("12", strs[2]);
    }

}
