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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Outils utilitaires concernant la sécurité.
 * <p>
 * Les méthodes devront être complètement documentées avec Javadoc et respecter les formats suivants :<br>
 * - @author INSER SA
 * - @version : @version <Version> (incrémenter la version à chaque changement)
 */
public class SecurityTools {

    /**
     * Transforme un string en une séquence de bytes encryptés selon le encoding character set par défaut "ISO-8859-1".
     * 
     * @param str
     *            String à encoder
     * @return Un tableau de bytes contenant le résultat de l'encryptage.
     * @throws UnsupportedEncodingException
     *             , NoSuchAlgorithmException, DigestException
     * 
     * @author INSER SA
     * @version 1.0
     */
    public static byte[] encryptString(String str) throws UnsupportedEncodingException, NoSuchAlgorithmException, DigestException {
        return encryptString(str, -1, "ISO-8859-1");
    }

    /**
     * Transforme un string en une séquence de bytes encryptés selon le encoding character set par défaut "ISO-8859-1".
     * 
     * @param str
     *            String à encoder
     * @param length
     *            Longueur de résultat (est-ce que c'est utilisé?) -1 pour ne pas définir de longueur.
     * @return Un tableau de bytes contenant le résultat de l'encryptage.
     * @throws UnsupportedEncodingException
     *             , NoSuchAlgorithmException, DigestException
     * 
     * @author INSER SA
     * @version 1.0
     */
    public static byte[] encryptString(String str, int length)
            throws UnsupportedEncodingException, NoSuchAlgorithmException, DigestException {
        return encryptString(str, length, "ISO-8859-1");
    }

    /**
     * Transforme un string en une séquence de bytes encodés. L'algorithme d'encryptage utilisé est le "SHA-1".
     * 
     * @param str
     *            String à encoder selon le encoding character défini.
     * @param length
     *            Longueur de résultat (est-ce que c'est utilisé?). -1 pour ne pas définir de longueur.
     * @param encoding
     *            Encoding character set utilisé pour le cryptage.
     * @return Un tableau de bytes contenant le résultat de l'encryptage.
     * @throws UnsupportedEncodingException
     *             , NoSuchAlgorithmException, DigestException
     * 
     * @author INSER SA
     * @version 1.0
     */
    public static byte[] encryptString(String str, int length, String encoding)
            throws UnsupportedEncodingException, NoSuchAlgorithmException, DigestException {
        if (str == null) {
            throw new IllegalArgumentException("encryptString: argument 'str' cannot be null");
        }

        byte[] buf = str.getBytes(encoding); // => UnsupportedEncodingException

        MessageDigest algorithm = MessageDigest.getInstance("SHA-1"); // =>
        // NoSuchAlgorithmException
        algorithm.reset();
        algorithm.update(buf);

        if (length == -1) {
            return algorithm.digest();
        }

        byte[] result = new byte[length];
        java.util.Arrays.fill(result, (byte) 0);

        algorithm.digest(result, 0, length); // => DigestException
        return result;
    }

    public static String toHex(byte[] aValue) {
        StringBuffer str = new StringBuffer(40);
        String tmp;
        for (int i = 0; i < aValue.length; i++) {
            tmp = Long.toHexString(aValue[i]);
            if (tmp.length() > 2) {
                str.append(tmp.substring(14));
            } else {
                if (tmp.length() == 1) {
                    str.append('0');
                }
                str.append(tmp);
            }
        }
        return str.toString().toUpperCase();
    }
}
