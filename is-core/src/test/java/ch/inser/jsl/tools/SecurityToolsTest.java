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

import java.io.UnsupportedEncodingException;
import java.security.DigestException;
import java.security.NoSuchAlgorithmException;

import org.junit.Ignore;
import org.junit.Test;

public class SecurityToolsTest {

    @Test
    @Ignore // Used only to produce a new password
    public void testEncryptStringString() throws UnsupportedEncodingException,
            NoSuchAlgorithmException, DigestException {
        System.out.println(
                SecurityTools.toHex(SecurityTools.encryptString("password")));
    }

}
