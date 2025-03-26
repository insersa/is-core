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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Outils utilitaires concernant les zip.
 * <p>
 * Chaque méthode documente la version, les auteurs et l'historique des changements. Les méthodes devront être complètement documentées avec
 * Javadoc et respecter les formats suivants :<br>
 * - balise @author INSER SA
 * - balise @version : @version <Version> (incrémenter la version à chaque changement)
 */
public class ZipTools {

    private static final int BUFFER_LEN = 4096;

    /**
     * Création d'un fichier ZIP. Un seul niveau de compression est implémenté: DEFLATED
     *
     * @param outFile
     *            Fichier zip à créer
     * @param inFiles
     *            Tableau de fichiers à archiver dans le fichier zip
     * @param mustDeleteSources
     *            True si les fichiers inFiles doivent être supprimés après archivage
     * @throws IOException
     *
     * @author INSER SA
     * @author INSER SA
     * @author INSER SA
     * @version 1.2
     */
    public static void createZipFile(File zipFile, File[] sourceFiles, boolean mustDeleteSourceFiles) throws IOException {

        boolean[] delete = new boolean[sourceFiles.length];
        for (int i = 0; i < sourceFiles.length; i++) {
            delete[i] = mustDeleteSourceFiles;
        }
        createZipFile(zipFile, sourceFiles, delete);
    }

    /**
     *
     * @param zipFile
     * @param deleteFiles
     * @param addFiles
     * @throws IOException
     */

    public static void createZipFile(File zipFile, File[] aDeleteFiles, File[] aAddFiles) throws IOException {

        File[] deleteFiles = aDeleteFiles;
        File[] addFiles = aAddFiles;
        if (deleteFiles == null) {
            deleteFiles = new File[0];
        }
        if (addFiles == null) {
            addFiles = new File[0];
        }
        File[] sourceFiles = new File[deleteFiles.length + addFiles.length];
        boolean[] delete = new boolean[sourceFiles.length];
        int counter = 0;
        for (int i = 0; i < deleteFiles.length; i++) {
            delete[counter] = true;
            sourceFiles[counter] = deleteFiles[i];
            counter++;
        }
        for (int i = 0; i < addFiles.length; i++) {
            delete[counter] = false;
            sourceFiles[counter] = addFiles[i];
            counter++;
        }
        createZipFile(zipFile, sourceFiles, delete);
    }

    /**
     * Création d'un fichier ZIP. Un seul niveau de compression est implémenté: DEFLATED
     *
     * @param outFile
     *            Fichier zip à créer
     * @param inFiles
     *            Tableau de fichiers à archiver dans le fichier zip
     * @param mustDeleteSources
     *            Tableau, True si les fichiers inFiles doivent être supprimés après archivage
     * @throws IOException
     *
     * @author INSER SA
     * @author INSER SA
     * @author INSER SA
     * @version 1.2
     */
    public static void createZipFile(File zipFile, File[] sourceFiles, boolean[] mustDeleteSourceFiles) throws IOException {

        if (sourceFiles == null) {
            return;
        }

        int size = sourceFiles.length;
        if (size == 0) {
            return;
        }

        byte data[] = new byte[BUFFER_LEN];
        try (ZipOutputStream target = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)))) {

            target.setMethod(ZipOutputStream.DEFLATED);
            // target.setLevel(Deflater.BEST_COMPRESSION); // not implemented!!

            for (int i = 0; i < size; i++) {
                try (BufferedInputStream source = new BufferedInputStream(new FileInputStream(sourceFiles[i]), BUFFER_LEN)) {

                    ZipEntry entry = new ZipEntry(sourceFiles[i].getName());
                    target.putNextEntry(entry);

                    int count;
                    while ((count = source.read(data, 0, BUFFER_LEN)) != -1) {
                        target.write(data, 0, count);
                    }

                }

                // Delete file if requested
                if (mustDeleteSourceFiles[i]) {
                    Files.delete(sourceFiles[i].toPath());
                }
            }
        }

    }

    /**
     * Création d'un zip à partir de byte array et retour d'un zip dans un byte array
     *
     *
     * @param aLstFilenames
     *            liste contenant les listes des noms des fichiers
     * @param aLstContentBytes
     *            contenu du fichier
     * @return le contenu du zip dans un byte array
     * @throws IOException
     */
    public static byte[] createZipFile(List<String> aLstFilenames, List<byte[]> aLstContentBytes) throws IOException {
        if (aLstFilenames.size() != aLstContentBytes.size()) {
            return null;
        }

        int size = aLstFilenames.size();
        if (size == 0) {
            return null;
        }

        // Création du fichier zip
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream target = new ZipOutputStream(baos);

        target.setMethod(ZipOutputStream.DEFLATED);

        // Insérer chaque fichier
        for (int i = 0; i < size; i++) {

            ZipEntry entry = new ZipEntry(aLstFilenames.get(i));
            target.putNextEntry(entry);

            target.write(aLstContentBytes.get(i));

        }
        target.close();

        return baos.toByteArray();

    }
}
