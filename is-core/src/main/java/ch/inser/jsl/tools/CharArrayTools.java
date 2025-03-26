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

/**
 * Classes utilitaires pour les opérations sur les tableaux de caractères.
 * <p>
 * Chaque méthode documente la version, les auteurs et l'historique des changements. Les méthodes devront être complètement documentées avec
 * Javadoc et respecter les formats suivants :<br>
 * - balise
 *
 * @author CONTRIBUTOR - balise
 * @version :
 * @version <Version> (incrémenter la version à chaque changement)
 */
public class CharArrayTools {

    /**
     * Recherche un pattern entier dans un tableau de caractères.
     *
     * @param cBuf
     *            Tableau de caractères
     * @param cPattern
     *            Pattern à chercher sous forme d'un tableau de caractères.
     * @param offset
     *            Offset de cBuf à partir duquel il faut chercher
     * @return L'index dans le tableau de caractères cBuf où commence le pattern, ou -1 si pas trouvé ou que le pattern est null.
     * @see lastIndexOf
     *
     * @author INSER SA
     * @version 1.0
     */
    public static int indexOf(char[] cBuf, char[] cPattern, int aOffset) {
        int offset = aOffset;
        if (cBuf == null) {
            throw new IllegalArgumentException("cBuf is null");
        }

        if (cPattern == null) {
            return -1; // pas trouvé
        }

        int sizeBuf = cBuf.length;
        int sizePattern = cPattern.length;

        if (offset < 0) {
            offset = 0; // corrige offset
        } else if (offset >= sizeBuf) {
            if (sizeBuf == 0 && sizePattern == 0 && offset == 0) {
                return 0; // il y a un string vide dans un string vide à
                // l'offset 0
            }
            return -1; // pas trouvé
        }

        if (sizePattern == 0) {
            return offset;
        }

        int idxBuf = offset;
        int idxPattern = 0;
        int sizePatternMinusOne = sizePattern - 1;

        while (idxBuf < sizeBuf) {
            if (cBuf[idxBuf] != cPattern[idxPattern]) {
                ++idxBuf;
                idxPattern = 0;
                continue;
            }

            if (idxPattern == sizePatternMinusOne) {
                return idxBuf - sizePatternMinusOne;
            }

            ++idxBuf;
            ++idxPattern;
        }

        return -1;
    }

    /**
     * Recherche un pattern entier dans un tableau de caractères.
     *
     * @param cBuf
     *            Tableau de caractères
     * @param pattern
     *            Pattern à chercher sous forme d'un String
     * @param offset
     *            Offset de cBuf à partir duquel il faut chercher
     * @return L'index dans le tableau de caractères où commence le pattern, ou -1 si pas trouvé ou que le pattern est null.
     * @see lastIndexOf
     *
     * @author INSER SA
     * @version 1.0
     */
    public static int indexOf(char[] cBuf, String pattern, int offset) {
        if (pattern == null) {
            return -1;
        }

        return indexOf(cBuf, pattern.toCharArray(), offset);
    }

    /**
     * Recherche un pattern entier dans un tableau de caractères en arrière par rapport à l'offset donné.
     *
     * @param cBuf
     *            Tableau de caractères
     * @param cPattern
     *            Pattern à chercher sous forme d'un tableau de caractères
     * @param offset
     *            Offset de cBuf à partir duquel il faut chercher en arrière
     * @return L'index dans le tableau de caractères où commence le pattern, ou -1 si pas trouvé ou que le pattern est null.
     * @see indexOf
     *
     * @author INSER SA
     * @version 1.0
     */
    public static int lastIndexOf(char[] cBuf, char[] cPattern, int aOffset) {
        int offset = aOffset;
        if (cBuf == null) {
            throw new IllegalArgumentException("cBuf is null");
        }

        if (cPattern == null) {
            return -1;
        }

        int sizeBuf = cBuf.length;
        int sizePattern = cPattern.length;
        int rightIndex = sizeBuf - sizePattern;

        if (offset > rightIndex) {
            offset = rightIndex;
        }

        if (offset < 0) {
            return -1;
        }

        if (sizePattern == 0) {
            return offset; // Empty string always matches
        }

        int idxBuf = offset;

        int sizePatternMinusOne = sizePattern - 1;
        int idxPattern = sizePatternMinusOne;

        while (idxBuf >= 0) {
            if (cBuf[idxBuf] != cPattern[idxPattern]) {
                --idxBuf;
                idxPattern = sizePatternMinusOne;
                continue;
            }

            if (idxPattern == 0) {
                return idxBuf;
            }

            --idxBuf;
            --idxPattern;
        }

        return -1;
    }

    /**
     * Recherche un pattern entier dans un tableau de caractères en arrière par rapport à l'offset donné.
     *
     * @param cBuf
     *            Tableau de caractères
     * @param pattern
     *            Pattern à chercher sous forme d'un String
     * @param offset
     *            Offset de cBuf à partir duquel il faut chercher en arrière
     * @return L'index dans le tableau de caractères où commence le pattern, ou -1 si pas trouvé ou que le pattern est null.
     * @see indexOf
     *
     * @author INSER SA
     * @version 1.0
     */
    public static int lastIndexOf(char[] cBuf, String pattern, int offset) {
        if (pattern == null) {
            return -1;
        }

        return lastIndexOf(cBuf, pattern.toCharArray(), offset);
    }
}
