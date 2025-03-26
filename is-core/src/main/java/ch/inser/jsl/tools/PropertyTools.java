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

import java.sql.Blob;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Classe utilitaire pour la gestion des propriétés en général.
 * <p>
 * Les méthodes devront être complètement documentées avec Javadoc et respecter les formats suivants :<br>
 * -
 *
 * @author INSER SA
 * @author INSER SA
 *         -
 * @version :
 * @version <Version> (incrémenter la version à chaque changement)
 */
public class PropertyTools {
    /** Définition de la catégorie de logging */
    private static final Log logger = LogFactory.getLog(PropertyTools.class);

    /**
     * Retourne un objet Integer à partir d'une propriété de l'action form qui vient d'être reçu suite à un submit d'un formulaire.
     *
     * @param value
     *            Propriété de l'action form (String).
     * @return Un objet Integer ou null si la propriété est null ou vide.
     *
     * @author INSER SA
     * @version 1.0
     * @throws NumberFormatException
     *             si le sring n'est pas un entier
     */
    public static Integer getInteger(String value) throws NumberFormatException {
        if (value == null || value.length() == 0) {
            return null;
        }

        return Integer.valueOf(value.trim(), 10);
    }

    /**
     * Retourne un objet String à partir d'une propriété de l'action form qui vient d'être reçu suite à un submit d'un formulaire.
     *
     * @param value
     *            Propriété de l'action form (String).
     * @return Un objet String ou null si la propriété est null ou vide.
     *
     * @author INSER SA
     * @version 1.0
     */
    public static String getString(String value) {
        if (value == null || value.length() == 0) {
            return null;
        }

        return value;
    }

    /**
     * Retourne un objet Long à partir d'une propriété de l'action form qui vient d'être reçu suite à un submit d'un formulaire.
     *
     * @param value
     *            Propriété de l'action form (String).
     * @return Un objet Long ou null si la propriété est null ou vide.
     *
     * @author INSER SA
     * @version 1.0
     */
    public static Long getLong(String value) {
        if (value == null || value.length() == 0) {
            return null;
        }

        return Long.valueOf(value.trim(), 10);
    }

    /**
     * Retourne un objet Double à partir d'une propriété de l'action form qui vient d'être reçu suite à un submit d'un formulaire.
     *
     * @param value
     *            Propriété de l'action form (String).
     * @return Un objet Long ou null si la propriété est null ou vide.
     */
    public static Double getDouble(String value) {
        if (value == null || value.length() == 0) {
            return null;
        }

        return Double.valueOf(value.trim());
    }

    /**
     * Retourne un objet Timestamp à partir d'une propriété de l'action form qui vient d'être reçu suite à un submit d'un formulaire.
     *
     * @param value
     *            Propriété de l'action form (String).
     * @return Un objet Timestamp ou null si la propriété est null ou vide.
     *
     * @author INSER SA
     * @version 1.0
     */
    public static Timestamp getTimestamp(String value) {
        if (value == null || value.length() == 0) {
            return null;
        }

        return Timestamp.valueOf(value);
    }

    public static Boolean getBoolean(String value) {
        if (value == null || value.length() == 0) {
            return null;
        }

        return Boolean.valueOf(value);
    }

    /**
     * Retourne un objet Date à partir d'une propriété de l'action form qui vient d'être reçu suite à un submit d'un formulaire.
     *
     * @param value
     *            Propriété de l'action form (String).
     * @return Un objet Date ou null si la propriété est null ou vide.
     * @throws ParseException
     *
     * @author INSER SA
     * @author INSER SA
     * @version 1.1
     */
    public static Date getDate(String value) throws ParseException {
        if (value == null || value.length() == 0) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        sdf.setLenient(false);
        return new java.sql.Date(sdf.parse(value).getTime());
    }

    /**
     * Retourne un String depuis un attribut d'un value object. Si la valeur est elle-même un String, on retourne une copie. Utilisé par la
     * méthode d'importation d'un VO dans un action form.
     *
     * @param value
     *            Object du value object à partir duquel on désire un String.
     * @param Un
     *            représentation String d'un objet ou un copie si l'objet est un String.
     *
     * @author INSER SA
     * @author INSER SA
     * @version 1.1
     */
    public static String getVOString(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof String) {
            return (String) value;
        }

        if (value instanceof Date) {
            return new SimpleDateFormat("dd.MM.yyyy").format(value);
        }

        return value.toString();
    }

    /**
     * Indique si deux propriétés sont différentes. Utilisé dans les value objects par la méthode <code>getDiffProperties()</code> pour
     * comparer chacune des propriétés du VO avec les propriétés correspondantes d'un VO passé en paramètre.
     * <p>
     * On considère les deux propriétés comme différentes, si:<br>
     * - prop1 est null et prop2 est non null<br>
     * - prop1 est non null et prop2 est null - prop1 et prop2 sont non null, et <code>equals()</code> retourne false
     * <p>
     * On considère les deux propriétés comme identiques, si:<br>
     * - prop1 et prop2 sont null - prop1 et prop2 sont non null, et <code>equals()</code> retourne true
     *
     * @param prop1
     *            Première propriété
     * @param prop2
     *            Seconde propriété
     * @return True si les deux propriétés sont différentes, false si elles sont identiques
     *
     * @author INSER SA
     * @version 1.0
     */
    @SuppressWarnings("null")
    public static boolean arePropertiesNotEqual(Object prop1, Object prop2) {
        if (prop1 == null && prop2 != null || prop1 != null && prop2 == null) {
            return true;
        }

        if (prop1 == null && prop2 == null) {
            return false;
        }

        // Comparision of two Blob objects by comparing their byte arrays
        if (prop1 instanceof Blob && prop2 instanceof Blob) {
            Blob blob1 = (Blob) prop1;
            Blob blob2 = (Blob) prop2;

            try {
                return !Arrays.equals(blob1.getBytes(1, (int) blob1.length()), blob2.getBytes(1, (int) blob1.length()));
            } catch (SQLException e) {
                logger.error("SQLException", e);
            }
        }

        return !prop1.equals(prop2);
    }

    // Méthodes pour effectuer un deep copy de propriétés dans un VO
    // --------------------------------------------------------------------------
    // -

    /**
     * Retourne une copie de l'objet passé. Utilisé dans un value object par la méthode <code>clone()</code> pour effectuer un deep copy de
     * ses propriétés.
     *
     * @param obj
     *            Object à copier.
     * @return Un objet Integer ou null si la propriété est null.
     *
     * @author INSER SA
     * @version 1.0
     */
    public static Integer getObjectCopy(Integer obj) {
        return obj == null ? null : obj.intValue();
    }

    /**
     * Retourne une copie de l'objet passé. Utilisé dans un value object par la méthode <code>clone()</code> pour effectuer un deep copy de
     * ses propriétés.
     *
     * @param obj
     *            Object à copier.
     * @return Un objet String ou null si la propriété est null.
     *
     * @author INSER SA
     * @version 1.0
     */
    public static String getObjectCopy(String obj) {
        return obj == null ? null : new String(obj);
    }

    /**
     * Retourne une copie de l'objet passé. Utilisé dans un value object par la méthode <code>clone()</code> pour effectuer un deep copy de
     * ses propriétés.
     *
     * @param obj
     *            Object à copier.
     * @return Un objet Long ou null si la propriété est null.
     *
     * @author INSER SA
     * @version 1.0
     */
    public static Long getObjectCopy(Long obj) {
        return obj == null ? null : obj.longValue();
    }

    public static Double getObjectCopy(Double obj) {
        return obj == null ? null : obj.doubleValue();
    }

    /**
     * Retourne une copie de l'objet passé. Utilisé dans un value object par la méthode <code>clone()</code> pour effectuer un deep copy de
     * ses propriétés.
     *
     * @param obj
     *            Object à copier.
     * @return Un objet Date ou null si la propriété est null.
     *
     * @author INSER SA
     * @version 1.0
     */
    public static Date getObjectCopy(Date obj) {
        return obj == null ? null : new Date(obj.getTime());
    }

    /**
     * Retourne une copie de l'objet passé. Utilisé dans un value object par la méthode <code>clone()</code> pour effectuer un deep copy de
     * ses propriétés.
     *
     * @param obj
     *            Object à copier.
     * @return Un objet Timestamp ou null si la propriété est null.
     *
     * @author INSER SA
     * @author INSER SA
     * @version 1.0
     */
    public static Timestamp getObjectCopy(Timestamp obj) {
        return obj == null ? null : (Timestamp) obj.clone();
    }

    /**
     * Retourne une copie de l'objet passé. Utilisé dans un value object par la méthode <code>clone()</code> pour effectuer un deep copy de
     * ses propriétés.
     *
     * @param obj
     *            Object à copier.
     * @return Un tableau de bytes ou null si la propriété est null.
     *
     * @author INSER SA
     * @version 1.0
     */
    public static byte[] getObjectCopy(byte[] obj) {
        if (obj == null) {
            return null;
        }

        byte[] result = new byte[obj.length];
        System.arraycopy(obj, 0, result, 0, obj.length);

        return result;
    }

    public static Boolean getObjectCopy(Boolean boo) {
        return boo == null ? null : boo.booleanValue();
    }

    /**
     * Transfomer un blob en byte array
     *
     * @param aBlob
     *            blob à fournir
     * @return
     */
    public static byte[] getByte(Blob aBlob) {
        byte[] fileContent = null;
        if (aBlob != null) {
            try {
                int lengthFile = (int) aBlob.length();
                fileContent = aBlob.getBytes(1l, lengthFile);
            } catch (@SuppressWarnings("unused") SQLException e) {
                logger.debug("Copy file in byte array");
            }

        }
        return fileContent;
    }

}
