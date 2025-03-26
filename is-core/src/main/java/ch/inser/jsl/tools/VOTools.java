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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.inser.dynamic.common.IValueObject;

/**
 * Classe utilitaire pour la manipulation de ValueObject et ou de listes de ValueObjects
 *
 * @author INSER SA
 *
 */
public class VOTools {

    private VOTools() {
        // Pour cacher le contrsucteur
    }

    /**
     * Retourne une collection de valeurs d'un paramètre pour la liste de VO
     */
    public static Collection<Object> getProperties(String aName, Collection<IValueObject> aList) {
        Set<Object> col = new HashSet<>();
        if (aList != null && aName != null) {
            for (IValueObject vo : aList) {
                col.add(vo.getProperty(aName));
            }
        }
        return col;
    }

    /**
     * Retourne une collection de valeurs d'un paramètre pour la liste de VO
     */
    public static <T> Collection<T> getProperties(String aName, Collection<IValueObject> aList, Class<T> cls) {
        Set<T> col = new HashSet<>();
        for (IValueObject vo : aList) {
            col.add(cls.cast(vo.getProperty(aName)));
        }
        return col;
    }

    /**
     * Retourne une liste de valeurs d'un paramètre pour la liste de VO
     */
    public static <T> List<T> getProperties(String aName, List<IValueObject> aList, Class<T> cls) {
        List<T> col = new ArrayList<>();
        for (IValueObject vo : aList) {
            col.add(cls.cast(vo.getProperty(aName)));
        }
        return col;
    }

    /**
     * Modifie une propriété avec une valeur sur l'ensemble de la liste
     *
     * @param aName
     * @param aValue
     */
    public static void setProperties(String aName, Object aValue, Collection<IValueObject> aList) {
        for (IValueObject vo : aList) {
            vo.setProperty(aName, aValue);
        }
    }

}
