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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class de manipulation de fichier
 *
 * @author INSER SA
 *
 */
public class FileTools {

    /** Définition de la catégorie de logging */
    private static final Log logger = LogFactory.getLog(FileTools.class);

    private FileTools() {
        // Just to hide the constructor
    }

    /**
     * Ecriture d'un fichier dans le système
     *
     * @param aFile
     *            Contenu du fichier
     * @param inFiles
     *            Chemin et nom du fichier
     */
    public static boolean writeFile(File aFile, String aPath) {
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(aPath)));
            out.print(aFile);
            out.flush();
            out.close();
            logger.debug("File :" + aPath + " has been saved");
        } catch (IOException e) {
            logger.error("Error File Save :", e);
            return false;
        }
        return true;
    }
}
