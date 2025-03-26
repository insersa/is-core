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

import java.util.ArrayList;
import java.util.List;

/**
 * Classe contenant des méthodes relatives au système.
 * <p>
 * Chaque méthode documente la version, les auteurs et l'historique des changements. Les méthodes devront être complètement documentées avec
 * Javadoc et respecter les formats suivants :<br>
 * - balise @author INSER SA
 * - balise @version : @version <Version> (incrémenter la version à chaque changement)
 */
public class SystemTools {
    /**
     * Indique si le JDK Java courant est en version 1.3.x.
     *
     * @return True si c'est le JDK 1.3.x, false sinon.
     *
     * @author INSER SA
     * @version 1.0
     */
    public static boolean isJDKVersion_1_3_x() {
        String version = System.getProperty("java.version");
        return version != null && "1.3".equals(version.substring(0, 3));
    }

    /**
     * Indique si le JDK Java courant est en version 1.2.2.
     *
     * @return True si c'est le JDK 1.2.2, false sinon.
     *
     * @author INSER SA
     * @version 1.0
     */
    public static boolean isJDKVersion_1_2_2() {
        String version = System.getProperty("java.version");
        return version != null && "1.2.2".equals(version.substring(0, 5));
    }

    /**
     * Retourne une List depuis un tableau de bytes. Similaire à la méthode <code>Arrays.asList(Object[])</code> excepté que cette dernière
     * n'accepte qu'un tableau d'objets et non pas un tableau de type primitif.
     * <p>
     * Cette méthode est utile pour afficher le contenu d'un tableau de bytes.
     * <p>
     * <code>
     * SystemTools.asList(new byte[] {1,2,3,4}).toString() --> "[1, 2, 3, 4]"
     * </code>
     *
     * @param byteArray
     *            Tableau de bytes
     * @return Une liste constituée des éléments de tableau
     *
     * @author INSER SA
     * @version 1.0
     */
    public static List<Byte> asList(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }

        int len = byteArray.length;
        List<Byte> list = new ArrayList<>(len);

        for (int i = 0; i < len; i++) {
            list.add(i, Byte.valueOf(byteArray[i]));
        }

        return list;
    }
}
