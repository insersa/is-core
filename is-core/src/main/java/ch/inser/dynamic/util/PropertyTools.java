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

import java.text.ParseException;

import ch.inser.dynamic.common.IValueObject;

/**
 * Classe utilitaire pour la gestion des propriétés en général.
 * <p>
 * Les méthodes devront être complètement documentées avec Javadoc et respecter les formats suivants :<br>
 * -
 *
 * @author CONTRIBUTOR -
 * @version :
 * @version <Version> (incrémenter la version à chaque changement)
 */
public class PropertyTools extends ch.inser.jsl.tools.PropertyTools {
    /**
     * @param aValue
     *            valeur initiale
     * @param aType
     *            type
     * @return valeur dans le bon type
     * @throws ParseException
     *             erreur de parsing de la valeur initiale
     */
    public static Object get(Object aValue, IValueObject.Type aType) throws ParseException {

        switch (aType) {
            case STRING:
                return getString((String) aValue);
            case LONG:
                return getLong((String) aValue);
            case DOUBLE:
                return getDouble((String) aValue);
            case DATE:
                return getDate((String) aValue);
            case TIMESTAMP:
                return getTimestamp((String) aValue);
            case BOOLEAN:
                return getBoolean((String) aValue);
            case BYTES:
                return aValue;
            default:
                throw new UnsupportedOperationException("Type " + aType + " non connu");
        }
    }
}
