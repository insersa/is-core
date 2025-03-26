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

package ch.inser.dynaplus.anonymous;

import java.util.HashMap;
import java.util.Map;

/**
 * VO anonyme simplifiée sans VO Info mais avec les infos basiques: nom de la table, map de champs et leurs types.
 *
 * @author INSER SA
 *
 */
public class VOAnonymous {

    /** Nom de la table */
    private String iTable;

    /** Map de champs et leurs types SQL */
    private Map<String, String> iFieldTypes = new HashMap<>();

    /** Nom du tablespace */
    private String iTablespace;

    /**
     * Constructeur
     *
     * @param aTable
     *            nom de la table
     */
    public VOAnonymous(String aTable) {
        iTable = aTable;
    }

    /**
     * Donne le type SQL d'un champ
     *
     * @param aField
     *            le nom du champ
     * @return le type SQL en format String, ex. "VARCHAR(30)"
     */
    public String getType(String aField) {
        return iFieldTypes.get(aField);
    }

    /**
     *
     * @return nom de la table
     */
    public String getTable() {
        return iTable;
    }

    /**
     *
     * @param aTable
     *            nom de la table
     */
    public void setTable(String aTable) {
        iTable = aTable;
    }

    /**
     *
     * @return nom des champs et leurs types sql
     */
    public Map<String, String> getFieldTypes() {
        return iFieldTypes;
    }

    /**
     *
     * @param aFieldTypes
     *            nom des champs et leurs types sql
     */
    public void setFieldTypes(Map<String, String> aFieldTypes) {
        iFieldTypes = aFieldTypes;
    }

    /**
     *
     * @return nom de table space
     */
    public String getTablespace() {
        return iTablespace;
    }

    /**
     *
     * @param aTablespace
     *            nom de table space
     */
    public void setTablespace(String aTablespace) {
        iTablespace = aTablespace;
    }

}
