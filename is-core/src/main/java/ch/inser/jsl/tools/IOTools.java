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

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Classes utilitaires pour les opérations IO.
 * <p>
 * Les méthodes <code>readEntireFileXXX()</code> et <code>writeEntireFile()</code> ont été inspirée de la classe <code>HunkIO</code>
 * provenant de:
 * <p>
 * Roedy Green<br>
 * <a href="http://mindprod.com/products.html#HUNKIO">Canadian Mind Products</a> <br>
 * version 1.0
 * <p>
 * Les méthodes devront être complètement documentées avec Javadoc et respecter les formats suivants :<br>
 * - @author INSER SA
 * - @version : @version <Version> (incrémenter la version à chaque changement)
 * 
 * @author INSER SA
 * @author INSER SA
 * @version 1.0
 */
public class IOTools {

    private IOTools() {
        // Just to hide the constructor
    }

    /**
     * Get all text in a file. Presumes the default encoding.
     *
     * @param fromFile
     *            File where to get the text.
     * @return Entire file content as on big string.
     * @exception IOException
     *
     * @author INSER SA
     * @version 1.0
     */
    public static String readEntireFileToString(File fromFile) throws IOException {
        return new String(readEntireFileToChars(fromFile));
    }

    /**
     * Get all text in a file. Presumes the default encoding.
     *
     * @param fromFile
     *            File where to get the text.
     * @return Entire file content as on big string.
     * @exception IOException
     *
     * @author INSER SA
     * @version 1.0
     */
    public static char[] readEntireFileToChars(File fromFile) throws IOException {
        int size = (int) fromFile.length();
        char[] chars = new char[size];

        try (FileReader fr = new FileReader(fromFile)) {
            int count = fr.read(chars);
            if (count != size) {
                throw new IOException("Error: problems reading file " + fromFile);
            }
            return chars;
        }
    }

    /**
     * Write all the text in a file. Presumes the default encoding.
     *
     * @param toFile
     *            File where to write the text.
     * @param text
     *            Content to write.
     * @exception IOException
     *
     * @author INSER SA
     * @version 1.0
     */
    public static void writeEntireFile(File toFile, String text) throws IOException {
        try (FileWriter fw = new FileWriter(toFile);) {
            fw.write(text);
        }
    }

    /**
     * Write all the text in a file. Presumes the default encoding.
     *
     * @param toFile
     *            File where to write the text.
     * @param chars
     *            Content to write.
     * @exception IOException
     *
     * @author INSER SA
     * @version 1.0
     */
    public static void writeEntireFile(File toFile, char[] chars) throws IOException {
        try (FileWriter fw = new FileWriter(toFile)) {
            fw.write(chars);
        }
    }
}
