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

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface DynamicDAO {

    /**
     * Enumération pour les aggrégateurs SQL disponibles
     */
    public enum Aggregator {
        AVG, COUNT, MAX, MIN, SUM
    }

    /**
     * Enumération pour caractériser les opérateurs dans une requête complexe.
     *
     * @author INSER SA
     */

    public enum Operator {
        /**
         * Egalité stricte
         */
        EQU,
        /**
         * UPPER(prop) = UPPER(value) restreint aux chaînes de caractères
         */
        UPPER_EQU,
        /**
         * Résultat sur parti de texte
         */
        LIKE,
        /**
         * UPPER(prop) = UPPER(value) restreint aux chaînes de caractères
         */
        UPPER_LIKE,
        /**
         * Résultat sur parti de texte
         */
        FULL_LIKE,
        /**
         * UPPER(prop) = UPPER(value) restreint aux chaînes de caractères
         */
        UPPER_FULL_LIKE,
        /**
         * doit être différent
         */
        DIFF,
        /**
         * Plus petit
         */
        SMALLER,
        /**
         * Plus petit et égale
         */
        SMALLER_EQU,
        /**
         * Plus grand que
         */
        BIGGER,
        /**
         * Plus grand ou égale
         */
        BIGGER_EQU,
        /**
         * prop IN (value1,value2,...) caractérisé par une liste on IN(SELECT....) caractérisé par un string
         */
        IN,
        /**
         * prop NOT IN (value1,value2,...) caractérisé par une liste ou IN(SELECT....) caractérisé par un string
         */
        NOT_IN,
        /**
         * si valeur est null
         */
        IS_NULL,
        /**
         * Si valeur n'est pas null
         */
        IS_NOT_NULL,
        /**
         * prop = funct(value) la fonction et la valeur sont donnés dans un tableau de deux objets dont le premier est le string de la
         * fonction
         */
        SQL_FUNCT,
        /**
         * prop like '%'||funct(value)||'%' la fonction et la valeur sont donnés dans un tableau de deux objets dont le premier est le
         * string de la fonction
         */
        SQL_FUNCT_FULL_LIKE,
        /**
         * Egalité sur le jour
         */
        DAY_EQU,
        /**
         * Egalité sur le mois
         */
        MONTH_EQU,
        /**
         * Egalité sur l'année
         */
        YEAR_EQU,
        /**
         * OR
         */
        OR,
        /**
         * prop = (value) où value est un select valide
         */
        EQU_SELECT,
        /**
         * Test si un string contient un sous-string, ex. where contains(geb_simplesearch, 'Pully') > 0 Plus performant que full like
         */
        CONTAINS;

        /**
         * Méthode pour obtenir un in Operator
         *
         * @param aSql
         *            statement du in
         */
        public static Map<Operator, Object> getIn(String aSql) {
            Map<Operator, Object> in = new EnumMap<>(Operator.class);
            if (aSql != null) {
                in.put(Operator.IN, aSql);
            }
            return in;
        }

        public static Map<Operator, Object> getIn(StringBuilder aSql) {
            Map<Operator, Object> in = new EnumMap<>(Operator.class);
            if (aSql != null) {
                in.put(Operator.IN, aSql.toString());
            }
            return in;
        }

        /**
         * Méthode pour obtenir un in Operator
         *
         * @param aList
         *            liste du in
         */
        public static Map<Operator, Object> getIn(List<Object> aList) {
            Map<Operator, Object> in = new EnumMap<>(Operator.class);
            in.put(Operator.IN, aList);
            return in;
        }

        /**
         * Méthode pour obtenir un not in Operator
         *
         * @param aSql
         *            statement du not in
         */
        public static Map<Operator, Object> getNotIn(String aSql) {
            Map<Operator, Object> in = new EnumMap<>(Operator.class);
            if (aSql != null) {
                in.put(Operator.NOT_IN, aSql);
            }
            return in;
        }

        public static Map<Operator, Object> getNotIn(StringBuilder aSql) {
            Map<Operator, Object> in = new EnumMap<>(Operator.class);
            if (aSql != null) {
                in.put(Operator.NOT_IN, aSql.toString());
            }
            return in;
        }

        /**
         * Méthode pour obtenir un not in Operator
         *
         * @param aList
         *            liste du not in
         */
        public static Map<Operator, Object> getNotIn(List<Object> aList) {
            Map<Operator, Object> in = new EnumMap<>(Operator.class);
            in.put(Operator.NOT_IN, aList);
            return in;
        }

        /**
         * Méthode pour obtenir un EQU_SELECT Operator
         *
         * @param aString
         *            la requête select
         */
        public static Map<Operator, Object> getEquSelect(String aString) {
            Map<Operator, Object> in = new EnumMap<>(Operator.class);
            in.put(Operator.EQU_SELECT, aString);
            return in;
        }

        public static Map<Operator, Object> getIsNotNull() {
            Map<Operator, Object> isNotNull = new EnumMap<>(Operator.class);
            isNotNull.put(Operator.IS_NOT_NULL, null);
            return isNotNull;
        }

        public static Map<Operator, Object> getIsNull() {
            Map<Operator, Object> isNull = new EnumMap<>(Operator.class);
            isNull.put(Operator.IS_NULL, null);
            return isNull;
        }

        public static Map<Operator, Object> getOperator(Operator operator, Object value) {
            Map<Operator, Object> in = new EnumMap<>(Operator.class);
            in.put(operator, value);
            return in;
        }

    }

    /**
     * Information sur le type de recherche d'un attribut, permet de connaître la manière de comparaison.
     *
     * @author INSER SA
     *
     */
    public enum AttributeType {
        /**
         * la valeur doit être strictement égale au champ de recherche
         */
        EQUAL,
        /** la valeur doit être égale au champ de recherche UPPER(valeur) */
        UPPER,
        /**
         * le string contient le début identique au string de résultat like%
         */
        LIKE,
        /**
         * le string contient le début identique au string de résultat UPPER(like%)
         */
        UPPER_LIKE,
        /**
         * Recherche sur un bou de texte compris dans la base %like%
         */
        FULL_LIKE,
        /**
         * Recherche sur un bou de texte compris dans la base UPPER(%like%)
         */
        UPPER_FULL_LIKE,
        /**
         * Recherche égalité sur la journée
         */
        DAY_EQU,
        /**
         * Recherche égalité sur le mois
         */
        MONTH_EQU,
        /**
         * Recherche égalité sur l'année
         */
        YEAR_EQU,
        /**
         * Recherche sur un bou de texte compris dans la base contains(<fieldname>,<a substring>) > 0
         */
        CONTAINS;

        /**
         * Map des formats string
         */
        private static Map<String, AttributeType> iStringMap;

        /**
         * Get the enumeration item for a String value
         *
         * @param value
         *            the value
         * @return the enumeration
         */
        public static AttributeType parse(String value) {
            if (iStringMap == null) {
                iStringMap = new HashMap<>();
                for (AttributeType item : values()) {
                    iStringMap.put(item.toString(), item);
                }
            }
            return iStringMap.get(value);
        }
    }

    /**
     * afin d'obtenir l'infomation si c'est un mode de séléction ou autres (création, update)
     *
     * @author INSER SA
     */
    public enum Mode {
        /**
         * Champ pour une séléction
         */
        SELECT,

        /**
         * Champs pour création
         */
        CREATE,

        /**
         * Champs pour update
         */
        UPDATE,

        /**
         * Champs pour delete
         */
        DELETE

        // /**
        // * Champs pour création ou update
        // */
        // OTHERS
    }

}