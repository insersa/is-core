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

package ch.inser.dynaplus.util;

import static org.junit.Assert.assertTrue;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.junit.Test;

/**
 * Tests de génération et validation de mots de passe
 *
 * @author INSER SA
 *
 */
public class SecurityUtilTest {

    /**
     * Used only to produce a new password
     *
     * @throws NoSuchAlgorithmException
     *             l'algoritme n'est pas disponible dans le système
     * @throws InvalidKeySpecException
     *             la clé n'est pas bonne
     */
    @Test
    public void testGeneratePasswordString() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String password = "test2019";
        System.out.println(ch.inser.dynaplus.util.SecurityUtil.generatePassword(password));
        assertTrue(true);
    }

    @Test // Used only to produce a new password
    public void testGeneratePasswordQueryString() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String password = "A6wA7y8v";
        String passwordHash = ch.inser.dynaplus.util.SecurityUtil.generatePassword(password);
        String email = "planinglongterme";
        String prenom = "planing";
        String nom = "longterme";
        String typeUser = "gast";
        String query = "INSERT INTO t_user (use_user, use_pwd, use_nom, use_prenom, use_ugr_id, use_email, use_cod_status, use_update_user) \n"
                + "    VALUES ('" + email + "','" + passwordHash + "', \n" + "    '" + nom + "', '" + prenom
                + "', (SELECT ugr_id FROM t_usergroup WHERE ugr_name = '" + typeUser + "'), '" + email + "', 1, 0);";
        System.out.println(query);
    }

    /**
     * Test password policy regex
     */
    @Test
    public void testPasswordPolicy() {
        // Min 8 caractères, max 15 caractères 1 majuscule, 1 minuscule, 1 chiffre
        String policy = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,15}$";

        // Valid passwords
        String[] validPasswords = { "AlphaRomeo4c", "F@rd1coSports", "Suzuki@lpha2016", "!@#$%^&*Aa1", "myDream1@$$", "Hello World@001",
                "<RRPhantom@16>" };
        for (String password : validPasswords) {
            assertTrue(password + "est valide", password.matches(policy));
        }

        // Invalid passwords
        String[] invalidPasswords = { "mypassword", "00000000", "!vwvento2015", "fiatlinea2014", "F@rd1co", "MyPassword2000LongerThan15" };
        for (String password : invalidPasswords) {
            assertTrue(password + "est invalide", !password.matches(policy));
        }
    }
}
