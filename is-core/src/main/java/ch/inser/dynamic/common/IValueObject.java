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

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.inser.dynamic.util.VOInfo;

/**
 * @author INSER SA
 * @version 1.0
 * @created 29-févr.-2008 08:53:13
 */
public interface IValueObject extends Serializable {

    /**
     * @return le nom métier du Value Object
     */
    public String getName();

    /**
     * Types de propriétés de vo
     *
     */
    public enum Type {
        /**
         * Byte
         */
        BYTES,

        /**
         * Date
         */
        DATE,

        /**
         * Document
         */
        DOCUMENT,

        /**
         * Double
         */
        DOUBLE,

        /**
         * Liste
         */
        LIST,

        /**
         * Long
         */
        LONG,

        /**
         * Integer
         */
        INTEGER,

        /**
         * Map
         */
        MAP,

        /**
         * String
         */
        STRING,

        /**
         * Timestamp
         */
        TIMESTAMP,

        /**
         * Date time
         */
        TIME,

        /**
         * Boolean
         */
        BOOLEAN,

        /**
         * Blob
         */
        BLOB,

        /**
         * Clob
         */
        CLOB,

        /**
         * Shape
         */
        SHAPE,
        /**
         * UUID
         */
        UUID,
        /**
         * JSON
         */
        JSON;

        /**
         *
         * @param aValue
         *            valeur d'une propriété, éventuellement en string
         * @return valeur dans format selon type
         */
        public Object getValueOf(Object aValue) {

            if (aValue instanceof String) {
                switch (this) {
                    case LONG:
                        return Long.valueOf((String) aValue);
                    case DOUBLE:
                        return Double.valueOf((String) aValue);
                    case BOOLEAN:
                        return Boolean.valueOf((String) aValue);
                    default:
                }
            }
            return aValue;
        }

        public int getSqlType() {
            switch (this) {
                case BLOB:
                    return java.sql.Types.BLOB;
                case BOOLEAN:
                    return java.sql.Types.VARCHAR;
                case BYTES:
                    return java.sql.Types.VARBINARY;
                case CLOB:
                    return java.sql.Types.CLOB;
                case DATE:
                    return java.sql.Types.DATE;
                case DOUBLE:
                    return java.sql.Types.DOUBLE;
                case INTEGER:
                    return java.sql.Types.INTEGER;
                case LONG:
                    return java.sql.Types.BIGINT;
                case SHAPE:
                    return java.sql.Types.VARCHAR;
                case STRING:
                    return java.sql.Types.VARCHAR;
                case TIME:
                    return java.sql.Types.TIME;
                case TIMESTAMP:
                    return java.sql.Types.TIMESTAMP;
                default:
                    throw new UnsupportedOperationException("The type '" + this + "' is not supported");
            }
        }
    }

    /**
     * Types java
     *
     * @author INSER SA
     *
     */
    public enum JavaType {
        /**
         * java.lang.Bytes
         */
        BYTES("java.lang.Bytes"),

        /**
         * java.sql.Date
         */
        DATE("java.sql.Date"),

        /**
         * org.w3c.dom.Document
         */
        DOCUMENT("org.w3c.dom.Document"),

        /**
         * java.lang.Double
         */
        DOUBLE("java.lang.Double"),

        /**
         * java.util.List
         */
        LIST("java.util.List"),

        /**
         * java.lang.Long
         */
        LONG("java.lang.Long"),

        /**
         * java.lang.Integer
         */
        INTEGER("java.lang.Integer"),

        /**
         * java.util.Map
         */
        MAP("java.util.Map"),

        /**
         * Java.lang.String
         */
        STRING("java.lang.String"),

        /**
         * java.sql.Timestamp
         */
        TIMESTAMP("java.sql.Timestamp"),

        /**
         * java.sql.Time
         */
        TIME("java.sql.Time"),

        /**
         * java.lang.Boolean
         */
        BOOLEAN("java.lang.Boolean"),

        /**
         * java.sql.Blob
         */
        BLOB("java.sql.Blob"),

        /**
         * java.sql.Clob
         */
        CLOB("java.sql.Clob"),

        /**
         * ch.inser.Shape
         */
        SHAPE("ch.inser.Shape"),

        /**
         * java.util.UUID
         */
        UUID("java.util.UUID"),

        /**
         * json
         */
        JSON("json");

        /**
         * VAleur string du java type
         */
        private String iValue;

        /**
         *
         * @param aValue
         *            valeur du java type
         */
        JavaType(String aValue) {
            iValue = aValue;
        }

        /**
         *
         * @return nom du java type
         */
        public String getValue() {
            return iValue;
        }
    }

    /**
     *
     * @return config du vo
     */
    public abstract VOInfo getVOInfo();

    /**
     * Retourne l'id de l'objet.
     *
     * @return L'id du VO.
     */
    public Object getId();

    /**
     * Assignation de l'identifiant
     *
     * @param anId
     *            valeur de l'id
     */
    public void setId(Object anId);

    /**
     * Retourne le timestamp de l'objet.
     *
     * @return Le timestamp du VO.
     */
    public Timestamp getTimestamp();

    /**
     * Modifie la valeur d'une propriété.
     *
     * @param aName
     *            Le nom de la proriété à modifier.
     * @param aValue
     *            La valeur de la proriété à modifier.
     */
    public void setProperty(String aName, Object aValue);

    /**
     * Enlève une propriété.
     *
     * @param aName
     *            La propriété à supprimer.
     * @return La valeur de la propri supprimée.
     */
    public Object removeProperty(String aName);

    /**
     * Supprime des champs du vo
     *
     * @param aNames
     *            nom des champs à supprimer
     */
    public void removeProperties(Set<String> aNames);

    /**
     * Conserve uniquement les propriétés de la liste donnée
     *
     * @param aNames
     *            noms de champs à conserver
     *
     */
    public void filterProperties(Set<String> aNames);

    /**
     * Retourne la valeur d'une propriété.
     *
     * @param aName
     *            Le nom de la proriété demandée.
     * @return La valeur de la propriété demandée.
     */
    public Object getProperty(String aName);

    /**
     * Retourne la valeur d'une propriété sous forme de String. Utilise com.inser.util.tools.PropertyTools.getVOString().
     *
     * @param aName
     *            Le nom de la proriété demandée.
     * @return La valeur de la propriété demandée.
     */
    public String getStringProperty(String aName);

    /**
     * Retourne la mappe des propriétés pour des traitements dans des autres modules.
     *
     * @return La mappe des propriétés VO.
     */
    public Map<String, Object> getProperties();

    /**
     * Retourne le type d'une propriété.
     *
     * @return La mappe des types du VO.
     */
    public Map<String, Type> getTypes();

    /**
     * Retourne la liste des attributs à omettre des tests des différences.
     *
     * @return Le set des attributs à omettre des tests des différences.
     */
    public Set<String> getOmit();

    /**
     * Retourne le type d'une propriété.
     *
     * @param aName
     *            Le nom de la proprieté.
     * @return Le type de la proprieté demandée.
     */
    public Type getPropertyType(String aName);

    /**
     *
     * @param aName
     *            nom du champ
     * @return Class de la valeur du champ
     */
    public Class<?> getPropertyClass(String aName);

    /**
     * Efface tous les attributs.
     */
    public void clear();

    /**
     * Vérifie si l'objet est vide, tous les attributs sont null.
     *
     * @return true si l'objet n'a aucun attribut défini.
     */
    public boolean isEmpty();

    /**
     * Retourne un String représentant l'objet et son contenu.
     *
     * @return La representation textuelle de l'objet.
     */
    @Override
    public String toString();

    /**
     * Retourne un String construit de la même sorte qu'avec toString(), mais qui contient seulement le propriétées non nulles.
     *
     * @return Une representation textuelle de l'objet, contenant uniquement les attributs non-vides.
     */
    public String getNotNullString();

    /**
     * Vérifie si l'objet passé en paramètre est égale à l'objet courant.
     *
     * @param aObject
     *            L'objet à comparer
     * @return true si l'objet à tester est identique à l'objet courant, false sinon.
     */
    @Override
    public boolean equals(Object aObject);

    @Override
    public int hashCode();

    /**
     * @param aName
     *            nom du champ
     * @return valeur en primitive double
     * @since 1.1
     */
    public double doubleValue(String aName);

    /**
     * @param aName
     *            nom du champ
     * @param aDefault
     *            valeur par défaut
     * @return valeur du champ ou le défaut
     * @since 1.1
     */
    public double doubleValue(String aName, double aDefault);

    /**
     *
     * @param map
     *            champs à ajouter map<nom du champ, valeur>
     */
    public void putProperties(Map<String, Object> map);

    /**
     * Ajoute toutes les propriétés d'un vo
     *
     * @param vo
     *            le vo référence
     */
    public void putProperties(IValueObject vo);

    /**
     *
     * @return propriété secu
     */
    public Object getSecu();

    /**
     *
     * @param value
     *            propriété secu
     */
    public void setSecu(Object value);

    /**
     *
     * @return uilisateur de update
     */
    public Object getModifyUser();

    /**
     *
     * @param value
     *            utilisateur de update
     */
    public void setModifyUser(Object value);

    /**
     * Timestamp de l'objet, attention le type du paramètre est objet vu que certains projets utilisent un lon (millisecondes) pour stocker
     * le timestamp.
     *
     *
     * @param value
     *            timestamp de mise à jour
     */
    public void setTimestamp(Object value);

    /**
     * @return Retourne la liste représentante une requête avec l'operatuer OR
     */
    public List<Map<String, Object>> getOrList();

    /**
     * Modifie la liste qui représente une requête OR
     *
     * @param aOrList
     *            la liste à mettre comme la nouvelle requête OR
     */
    public void setOrList(List<Map<String, Object>> aOrList);

    /**
     * Vide la liste de la requête OR
     */
    public void clearOrList();

    /**
     * Retourne une copie profonde (deep copy) de cet objet.
     *
     * @return Un objet VO représentant un copie profonde de cet objet.
     */
    public Object clone();

    /**
     * Compare toutes les propriétés de cet objet avec celles du value object passé en paramètre et fournit une collection Map des
     * propriétés différentes avec le nom de la propriété comme "key" et sa nouvelle valeur comme "value".
     *
     * @param aValueObject
     *            VO faisant l'objet de la comparaison.
     *
     * @return Une collection Map des différences trouvées (peut être vide).
     */
    public Map<String, Object> getDiffProperties(IValueObject aValueObject);

    /**
     * Retourne le ou les parents
     *
     * @return le ou les parents
     */
    public Object getParent();

    /**
     *
     * @return true si un parent est initialisé dans le vo
     */
    public boolean isParentInitialised();

    /**
     *
     * @return anciens valeurs de ce vo
     */
    public IValueObject getOldValues();

    /**
     *
     * @return true si les anciens valeurs sont initialisés
     */
    public boolean isOldInitialised();
}