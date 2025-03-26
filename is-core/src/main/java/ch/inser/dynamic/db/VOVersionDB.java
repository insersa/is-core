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

package ch.inser.dynamic.db;

import java.sql.Timestamp;
import java.util.Map;
import java.util.Set;

import ch.inser.dynamic.common.AbstractDynamicVO;
import ch.inser.dynamic.common.IValueObject;

/**
 * Classe de transport des données des objets métier "Version de la base de données".
 *
 * @author INSER SA
 * @version 1.0
 */
public class VOVersionDB extends AbstractDynamicVO {

    // ------------------------------------------------------ Variable de classe

    /**
     * UID
     */
    private static final long serialVersionUID = -4310060473874363959L;
    /**
     * Liste des attributs et de leur type.
     */
    private static final Map<String, IValueObject.Type> TYPES = new java.util.HashMap<>(6, 1);

    static {
        TYPES.put("ver_id", IValueObject.Type.LONG);
        TYPES.put("ver_label", IValueObject.Type.STRING);
        TYPES.put("ver_version", IValueObject.Type.LONG);
        TYPES.put("ver_compatibilite", IValueObject.Type.LONG);
        TYPES.put("ver_description", IValueObject.Type.STRING);
        TYPES.put("ver_date", IValueObject.Type.DATE);
    }

    /**
     * Liste des attributs à omettre des tests des différences.
     */
    private static final Set<String> OMIT = new java.util.HashSet<>(0);

    // ------------------------------------------------------- Méthodes membres

    /**
     * Retourne l'id de l'objet.
     *
     * @return L'id du VO.
     */
    @Override
    public Object getId() {

        return getProperty("ver_version");
    }

    /**
     * Retourne le timestamp de l'objet.
     *
     * @return Le timestamp du VO.
     */
    @Override
    public Timestamp getTimestamp() {

        return (Timestamp) getProperty("ver_date");
    }

    /**
     * Retourne la liste des attributs et de leur type.
     *
     * @return La mappe des types des VOs de type version de la base de données.
     */
    @Override
    public Map<String, IValueObject.Type> getTypes() {

        return getStaticTypes();
    }

    /**
     * Retourne la liste des attributs et de leur type.
     *
     * @return La mappe des types des VOs de type version de la base de données.
     */
    public static Map<String, IValueObject.Type> getStaticTypes() {

        return TYPES;
    }

    /**
     * Retourne la liste des attributs à omettre des tests des différences.
     *
     * @return Le set des attributs à omettre des tests des différences.
     */
    @Override
    public Set<String> getOmit() {

        return getStaticOmit();
    }

    /**
     * Retourne la liste des attributs à omettre des tests des différences.
     *
     * @return Le set des attributs à omettre des tests des différences.
     */
    public static Set<String> getStaticOmit() {

        return OMIT;
    }

    /**
     * Retourne une copie profonde (deep copy) de cet objet.
     *
     * @return Une copie du VO.
     */
    @Override
    public Object clone() {

        return clone(new VOVersionDB());
    }

    @Override
    public String getName() {
        return "VERSION";
    }
}