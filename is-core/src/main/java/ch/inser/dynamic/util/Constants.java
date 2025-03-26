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

package ch.inser.dynamic.util;

/**
 * Classe contenant diverses constantes utilisées dans le cadre de la librairie dynamic.
 *
 * @version 1.0
 * @author INSER SA
 */
public class Constants {

    /**
     * Constructeur caché, toute les méthodes étant statiques!
     */
    protected Constants() {
        // Pour cacher le constructeur
    }

    /**
     * Constantes d'actions sur les entités
     */
    public enum EntityAction {
        DELETE, CREATE, UPDATE, PATCH
    }

    /**
     * Constante de la couche, pour déclenchement des tests
     */
    public enum TierName {
        WS, IMPORT, REST, BP, BO, DAO
    }
}
