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

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.inser.dynamic.common.IContextManager;

/**
 * Utilitaire pour générer un mot de passe. Un sel et de valider le mot de passe
 *
 * Sources : https://howtodoinjava.com/security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/#PBKDF2WithHmacSHA1
 * https://blog.octo.com/comment-conserver-les-mots-de-passe-de-ses-utilisateurs-en-2019/
 * https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html
 *
 * @author INSER SA
 */
public class SecurityUtil {

    /**
     * Constante pour la longueur des tableaux
     */
    private final static int TABLE_LENGTH = 16;
    /**
     * Valeur par défaut pour le nb d'itération
     */
    private final static int DEFAULT_ITERATION = 1000;
    /**
     * Valeur par défaut pour l'algorythme de hachage
     */
    private final static String DEFAULT_HASHALGO = "PBKDF2WithHmacSHA512";
    /**
     * Valeur par défaut pour le salt
     */
    private final static String DEFAULT_RNGALGO = "SHA1PRNG";
    /**
     * Logger
     */
    private static final Log logger = LogFactory.getLog(SecurityUtil.class);

    private SecurityUtil() {
        // Private constructor
    }

    /**
     * Transforme un string en une séquence de string encrypté préalablement selon l'algorythme de hachage, les itérations ainsi que
     * l'algorythme RNG
     *
     * @param aPassword
     *            le string
     * @param aContextManager
     *            Le context manager si les éléments ont été définis dans le fichier .properties du projet (Algo de hachage, itération et
     *            algo RNG)
     * @return Un string contenant [Itération:Sel:Password]
     * @throws NoSuchAlgorithmException
     *             Erreur lié à l'algorithme
     * @throws InvalidKeySpecException
     *             Erreur de clé spécifié invalide
     */
    public static String generatePassword(String aPassword, IContextManager aContextManager)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        String algo = aContextManager.getProperty("security.hashAlgo");
        int iteration = Integer.parseInt(aContextManager.getProperty("security.iteration"));
        String rngAlgo = aContextManager.getProperty("security.rngAlgo");
        return generatePassword(aPassword, algo, iteration, rngAlgo);
    }

    /**
     * Transforme un string en une séquence de string encrypté préalablement selon l'algorythme de hashage, les itérations ainsi que
     * l'algorythme RNG pour le sel. Set par défaut Algorythme hachage : PBKDF2WithHmacSHA512 Itération : 1000 Algorythme RNG : SHA1PRNG
     *
     * @param aPassword
     *            le string
     * @return Un string contenant [Itération:Sel:Password]
     * @throws NoSuchAlgorithmException
     *             Erreur lié à l'algorithme
     * @throws InvalidKeySpecException
     *             Erreur de clé spécifié invalide
     */
    public static String generatePassword(String aPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return generatePassword(aPassword, DEFAULT_HASHALGO, DEFAULT_ITERATION, DEFAULT_RNGALGO);
    }

    /**
     * Transforme un string en une séquence de string encrypté préalablement selon l'algorythme de hashage, les itérations ainsi que
     * l'algorythme RNG pour le sel.
     *
     * @param aPassword
     *            String que vous souhaitez hacher
     * @param aHashAlgo
     *            Algo de hachage
     * @param aIteration
     *            Itération
     * @param aRngAlgo
     *            Algo RNG
     * @return Un string contenant [Itération:Sel:Password]
     * @throws NoSuchAlgorithmException
     *             Erreur lié à l'algorithme
     * @throws InvalidKeySpecException
     *             Erreur de clé spécifié invalide
     */
    public static String generatePassword(String aPassword, String aHashAlgo, int aIteration, String aRngAlgo)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        char[] chars = aPassword.toCharArray();
        byte[] salt = generateSalt(aRngAlgo);

        PBEKeySpec spec = new PBEKeySpec(chars, salt, aIteration, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(aHashAlgo);
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return aIteration + ":" + toHex(salt) + ":" + toHex(hash);
    }

    /**
     * Validation du mot de passe avec algorythme de hachage paramètrer dans le fichier de properties
     *
     * @param aPassword
     *            mdp qu'il faut tester (Par exemple provenant d'un champs de formulaire)
     * @param aStoredPassword
     *            mdp stocker (Par exemple se trouvant dans la BD)
     * @return Boolean
     * @throws NoSuchAlgorithmException
     *             Erreur lié à l'algorithme
     * @throws InvalidKeySpecException
     *             Erreur de clé spécifié invalide
     */
    public static boolean validatePassword(String aPassword, String aStoredPassword, IContextManager aContextManager)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        String algo = aContextManager.getProperty("security.hashAlgo");
        return validatePassword(aPassword, aStoredPassword, algo);
    }

    /**
     * Validation du mot de passe avec algorythme de hachage paramètrer dans le fichier .properties du projet
     *
     * @param aPassword
     *            mdp qu'il faut tester (Par exemple provenant d'un champs de formulaire)
     * @param aStoredPassword
     *            mdp stocker (Par exemple se trouvant dans la BD)
     * @return Boolean
     * @throws NoSuchAlgorithmException
     *             Erreur lié à l'algorithme
     * @throws InvalidKeySpecException
     *             Erreur de clé spécifié invalide
     */
    public static boolean validatePassword(String aPassword, String aStoredPassword)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        return validatePassword(aPassword, aStoredPassword, DEFAULT_HASHALGO);
    }

    /**
     * Validation du mot de passe
     *
     * @param aPassword
     *            mdp qu'il faut tester (Par exemple provenant d'un champs de formulaire)
     * @param aStoredPassword
     *            mdp stocker (Par exemple se trouvant dans la BD)
     * @return Boolean
     * @throws NoSuchAlgorithmException
     *             Erreur lié à l'algorithme
     * @throws InvalidKeySpecException
     *             Erreur de clé spécifié invalide
     */
    public static boolean validatePassword(String aPassword, String aStoredPassword, String aHashAlgo)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (aStoredPassword == null) {
            logger.warn("Mot de passe stocké est null");
            return false;
        }
        String[] parts = aStoredPassword.split(":");
        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = fromHex(parts[1]);
        byte[] hash = fromHex(parts[2]);

        PBEKeySpec spec = new PBEKeySpec(aPassword.toCharArray(), salt, iterations, hash.length * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(aHashAlgo);
        byte[] testHash = skf.generateSecret(spec).getEncoded();

        int diff = hash.length ^ testHash.length;
        for (int i = 0; i < hash.length && i < testHash.length; i++) {
            diff |= hash[i] ^ testHash[i];
        }
        return diff == 0;
    }

    /**
     * Génère un sel selon l'algorythme RNG
     *
     * @param aRngAlgo
     *            algorythme RNG
     * @return Un tableau de byte contanant le résultat de l'encryptage
     * @throws NoSuchAlgorithmException
     *             Erreur lié à l'algorithme
     */
    private static byte[] generateSalt(String aRngAlgo) throws NoSuchAlgorithmException {
        SecureRandom secureRandom = SecureRandom.getInstance(aRngAlgo);
        byte[] salt = new byte[TABLE_LENGTH];
        secureRandom.nextBytes(salt);
        return salt;
    }

    /**
     * Tranforme un tableau de byte en string
     *
     * @param aArray
     *            Un tableau de byte
     * @return Un string
     */
    private static String toHex(byte[] aArray) {
        BigInteger bigInteger = new BigInteger(1, aArray);
        String hex = bigInteger.toString(TABLE_LENGTH);
        int paddingLength = aArray.length * 2 - hex.length();
        if (paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hex;
        }
        return hex;
    }

    /**
     * Transforme un string en un tableau de byte
     *
     * @param aHex
     *            Un string
     * @return Un tableau de byte
     */
    private static byte[] fromHex(String aHex) {
        byte[] bytes = new byte[aHex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(aHex.substring(2 * i, 2 * i + 2), TABLE_LENGTH);
        }
        return bytes;
    }
}
