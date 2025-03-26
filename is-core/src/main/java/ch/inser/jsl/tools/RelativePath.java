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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class provides functions used to generate a relative path from two absolute paths.
 * <p>
 * Sometimes in an application you will have a "home" directory associated with the application, and then some files associated with that
 * home directory. To have the files in an independent position relative to the root of the file system, you may want to use relative paths
 * to the files rather than absolute or canonical paths. If your input specifies files in absolute form, you can use the following code to
 * generate the relative path from a 'home' directory to a file, whether it resides under that directory or in some other place in the file
 * system.<br>
 * Examples:
 * <p>
 *
 * <pre>
 *  home = &quot;/a/b/c&quot;
 *  file = &quot;/a/b/c/d/e.txt&quot;
 *  relative path = &quot;d/e.txt&quot;
 *
 *  home = &quot;/a/b/c&quot;
 *  file = &quot;/a/d/f/g.txt&quot;
 *  relative path = &quot;../../d/f/g.txt&quot;
 * </pre>
 * <p>
 * Le code provient d'un Tip sur DevX, trouvé à l'adresse <a href="http://www.devx.com/free/tips/tipview.asp?content_id=2466">
 * http://www.devx.com/free/tips/tipview.asp?content_id=2466</a>.
 *
 * ATTENTION: Cette classe n'a pas passé tous les tests unitaires nécessaires. La méthode getRelativePath() devrait être examinée pour
 * éviter un plantage dans les cas de tests notés en commentaire. AL/25-mar-2002.
 *
 * @author INSER SA
 * @version 1.0
 */
public class RelativePath {

    /** Définition de la catégorie de logging */
    private static final Log logger = LogFactory.getLog(RelativePath.class);

    /**
     * Private constructor
     */
    private RelativePath() {
    }

    /**
     * Get relative path of File 'f' with respect to 'home' directory. Example :<br>
     *
     * <pre>
     *  home = /a/b/c
     *  f    = /a/d/e/x.txt
     *  s = getRelativePath(home,f) = ../../d/e/x.txt
     * </pre>
     *
     * @param home
     *            base path, should be a directory, not a file, or it doesn't make sense
     * @param f
     *            file to generate path for
     * @return path from home to f as a string
     */
    public static String getRelativePath(File home, File f) {

        List<String> homelist;
        List<String> filelist;

        homelist = getPathList(home);
        filelist = getPathList(f);
        return matchPathLists(homelist, filelist);
    }

    /**
     * Break a path down into individual elements and add to a list.<br>
     * Example : if a path is /a/b/c/d.txt, the breakdown will be [d.txt,c,b,a]
     *
     * @param f
     *            Input file
     * @return a List collection with the individual elements of the path in reverse order
     */
    private static List<String> getPathList(File f) {
        List<String> l = new ArrayList<>();
        File r;
        try {
            r = f.getCanonicalFile();

            while (r != null) {
                l.add(r.getName());
                r = r.getParentFile();
            }
        } catch (IOException e) {
            logger.error("Error getting path", e);
            l = null;
        }
        return l;
    }

    /**
     * Figure out a string representing the relative path of 'f' with respect to 'r'.
     *
     * @param r
     *            home path
     * @param f
     *            path of file
     */
    private static String matchPathLists(List<String> r, List<String> f) {
        int i;
        int j;
        String s = "";

        if (r == null || f == null) {
            return s;
        }

        // start at the beginning of the lists
        // iterate while both lists are equal
        i = r.size() - 1;
        j = f.size() - 1;

        // first eliminate common root
        while (i >= 0 && j >= 0 && r.get(i).equals(f.get(j))) {
            i--;
            j--;
        }

        // for each remaining level in the home path, add a ..
        for (; i >= 0; i--) {
            s += ".." + File.separator;
        }

        // for each level in the file path, add the path
        for (; j >= 1; j--) {
            s += f.get(j) + File.separator;
        }

        // file name
        s += f.get(j);

        return s;
    }
}