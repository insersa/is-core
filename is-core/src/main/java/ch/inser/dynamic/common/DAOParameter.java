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

package ch.inser.dynamic.common;

/**
 * Classe qui permet de passer det paramètres optionales aux méthodes de DAO, par example le user
 *
 * @author INSER SA
 *
 */
public class DAOParameter {

    /**
     * Enumeration de noms de paramètres
     *
     */
    public enum Name {
        /**
         * Objet de type ILoggedUser
         */
        USER,

        /** Champs (sort items) à trier. Objet de type String[] */
        SORT_FIELDS,

        /**
         * Orientations de sort items. Objet de type Sort[]
         */
        SORT_ORIENTATIONS,

        /**
         * Attribut "toggable" de sort items. Objet de type Boolean[]
         */
        SORT_TOGGABLES,

        /**
         * Index de tri
         */
        SORT_INDEX,

        /**
         * Clé de tri
         */
        SORT_KEY,

        /**
         * Ordre de tri.
         */
        SORT_ORIENTATION,

        /**
         * N° de ligne de début d'une tranche de résultat (Int)
         */
        ROWNUM_START,

        /**
         * N° de ligne de fin d'une tranche de résultat (Int)
         */
        ROWNUM_END,

        /**
         * Nombre maximum de lignes à retourner
         */
        ROWNUM_MAX,

        /**
         * The transaction id.
         */
        TRANSACTION_ID,

        /** Format du résultat (pdf, csv etc.) */
        RESULT_FORMAT,

        /** Langue du résultat (pour pdf, csv, etc.) */
        RESULT_LANG,

        /** Noms des champs pour un résultat de type liste (csv) */
        RESULT_FIELDS,

        /** Clés pour les headers d'une liste (csv) */
        RESULT_LABEL_KEYS,

        /** Noms de tables pour la clause from. Objet de type Set<String>. */
        TABLE_NAMES,

        /** Clauses à mettre dans WHERE pour joindre des tables. Objet de type Set<String> */
        JOIN_CLAUSES,

        /** Filtre de sécurité à mettre dans la clause WHERE */
        SECURITY_CLAUSE,

        /** Le nom des attributs à rechercher pour créer les listes. Objet de type List<String> */
        ATTRIBUTES,

        /**
         * Key to add a "GROUP BY" statement
         */
        GROUP_BY,

        /**
         * Other parameter, must be identified by the iOtherName
         */
        OTHER,

        /**
         * Empty parameter only to bypass deprecated methods.
         */
        EMPTY
    }

    /**
     * An empty parameter only to bypass deprecated methods.
     */
    public static final DAOParameter EMPTY_PARAMETER;

    /**
     * Static constructor.
     */
    static {
        EMPTY_PARAMETER = new DAOParameter();
        EMPTY_PARAMETER.iName = Name.EMPTY;
    }

    /**
     * Get the parameter in an array of parameters.
     *
     * @param aName
     *            the parameter name
     * @param aParameters
     *            an array of parameters
     * @return the parameter or <code>null</code> if not found
     */
    public static DAOParameter getParameter(Name aName, DAOParameter[] aParameters) {
        if (aParameters == null) {
            return null;
        }
        for (int i = 0; i < aParameters.length; i++) {
            if (aParameters[i].iName == aName) {
                return aParameters[i];
            }
        }
        return null;
    }

    /**
     * Get the other parameter in an array of parameters.
     *
     * @param aOtherName
     *            the other parameter name
     * @param aParameters
     *            an array of parameters
     * @return the parameter or <code>null</code> if not found
     */
    private static DAOParameter getParameter(String aOtherName, DAOParameter[] aParameters) {
        for (int i = 0; i < aParameters.length; i++) {
            if (aParameters[i].iName == Name.OTHER && aParameters[i].iOtherName.equals(aOtherName)) {
                return aParameters[i];
            }
        }

        return null;
    }

    /**
     * Get the value of a parameter in an array of parameters.
     *
     * @param aName
     *            the parameter name
     * @param aParameters
     *            an array of parameters
     * @return the value of the parameter or <code>null</code> if not found
     */
    public static Object getValue(Name aName, DAOParameter[] aParameters) {
        DAOParameter parameter = getParameter(aName, aParameters);
        return parameter != null ? parameter.getValue() : null;
    }

    /**
     * Get the value of an other parameter in an array of parameters.
     *
     * @param aOtherName
     *            the other parameter name
     * @param aParameters
     *            an array of parameters
     * @return the value of the parameter or <code>null</code> if not found
     */
    public static Object getValue(String aOtherName, DAOParameter[] aParameters) {
        DAOParameter parameter = getParameter(aOtherName, aParameters);
        return parameter != null ? parameter.getValue() : null;
    }

    /**
     * Add some new parameters to an existing array of parameters
     *
     * @param aParameters
     *            the existing array of parameters
     * @param aNewParameters
     *            the new parameters
     * @return a new array combining the existing and new parameters
     */
    public static DAOParameter[] add(DAOParameter[] aParameters, DAOParameter... aNewParameters) {
        DAOParameter[] result = new DAOParameter[aParameters.length + aNewParameters.length];
        for (int i = 0; i < aParameters.length; i++) {
            result[i] = aParameters[i];
        }
        for (int i = 0; i < aNewParameters.length; i++) {
            result[i + aParameters.length] = aNewParameters[i];
        }
        return result;
    }

    /**
     * Nom du paramètre
     */
    private Name iName;

    /**
     * Identify <cod>OTHER</code> parameters.
     */
    private String iOtherName;

    /**
     * Valeur du paramètre
     */
    private Object iValue;

    /**
     * Constructeur de base
     */
    public DAOParameter() {
    }

    /**
     *
     * @param aName
     *            nom du paramètre
     * @param aValue
     *            valeur du paramètre
     */
    public DAOParameter(Name aName, Object aValue) {
        iName = aName;
        iValue = aValue;
    }

    /**
     *
     * @param anOtherName
     *            nom du paramètre en format String si pas représenté dans l'enum Name
     * @param aValue
     *            valeur du paramètre
     */
    public DAOParameter(String anOtherName, Object aValue) {
        iName = Name.OTHER;
        iOtherName = anOtherName;
        iValue = aValue;
    }

    @Override
    public String toString() {
        return String.format("%s=%s", iName != Name.OTHER ? iName : iOtherName, iValue);
    }

    /**
     *
     * @return nom du paramètre
     */
    public Name getName() {
        return iName;
    }

    /**
     *
     * @param aName
     *            nom du paramètre
     */
    public void setName(Name aName) {
        iName = aName;
    }

    /**
     *
     * @return nom du paramètre
     */
    public String getOtherName() {
        return iOtherName;
    }

    /**
     *
     * @param anOtherName
     *            nom du paramètre (si pas représenté dans enum Name)
     */
    public void setOtherName(String anOtherName) {
        iOtherName = anOtherName;
    }

    /**
     *
     * @return valeur du paramètre
     */
    public Object getValue() {
        return iValue;
    }

    /**
     *
     * @param aValue
     *            valeur du paramètre
     */
    public void setValue(Object aValue) {
        iValue = aValue;
    }
}
