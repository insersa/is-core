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

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jdom2.Element;
import org.jdom2.Namespace;

import ch.inser.dynamic.common.DynamicDAO.AttributeType;

/**
 * Class pour la gestion des informations concernant un Attribut contenu dans le fichier XML-Schema "ValueObject.xsd".
 *
 * @author INSER SA
 * @version 1.0
 */
public class AttributeInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final Namespace XS = Namespace.getNamespace("xs", "http://www.w3.org/2001/XMLSchema");

    /**
     * Clé du nom de l'attribut.
     */
    private static final String NAME = "name";

    /**
     * Clé du nom de la table contenant l'attribut.
     */
    public static final String TABLE = "table";

    /**
     * Clé de l'ordre de l'attribut dans le fichier "ValueObject.xsd".
     */
    public static final String POSITION = "position";

    /**
     * Clé du type de l'attribut.
     */
    public static final String TYPE = "type";

    public static final String REQUIRED = "required";

    public static final String READONLY = "readonly";

    public static final String MIN = "min";

    public static final String MAX = "max";

    public static final String PATTERN = "pattern";

    /**
     * Clé de la propriété "caché" de l'attribut.
     */
    public static final String HIDDEN = "hidden";

    /**
     * Clé de la propriété "liste" de l'attribut.
     */
    public static final String LIST = "list";

    /**
     * Clé de la propriété "nom de champs dans la base de donnée" de l'attribut.
     */
    public static final String DB_NAME = "dbname";

    /**
     * Clé de la longur de l'attribut.
     */
    public static final String LENGTH = "length";

    /**
     * Clé de la propriété "recherche" de l'attribut.
     */
    public static final String SEARCH = "search";

    /**
     * Valeur <code>true</code>
     */
    public static final String TRUE = "true";

    private static final String PASSWORD = "password";

    /**
     * Boolean pour savoir si le défaut de recherche d'un string est EQUAL ou LIKE
     */
    private static boolean isEqualDefault;

    /**
     * Le nom du valueObject.
     */
    private String iVOName;

    /**
     * Le nom de l'attribut.
     */
    private String iName;

    /**
     * Les informations de l'attribut.
     */
    private Map<String, Object> iInfo;

    static {
        isEqualDefault = false;
    }

    public AttributeInfo(String aVOName, int aPosition, Element aElement) {

        iVOName = aVOName;
        iName = aElement.getAttributeValue(NAME);
        iInfo = new HashMap<>();
        iInfo.put(POSITION, Integer.valueOf(aPosition));
        iInfo.put(TYPE, aElement.getAttributeValue(TYPE));
        Element element = aElement.getChild("annotation", XS);
        if (element != null) {
            readAppinfo(element.getChild("appinfo", XS));
        }
    }

    private void readAppinfo(Element aElement) {

        Iterator<?> it = aElement.getChildren().iterator();
        Element element;
        while (it.hasNext()) {
            element = (Element) it.next();
            if ("lookup".equals(element.getName())) {
                iInfo.put(element.getName(), new LookupInfo(element));
            } else {
                iInfo.put(element.getName(), element.getText());
            }
        }
    }

    /**
     * Retourne la valeur brute d'un information de l'attribut.
     *
     * @param aInfoName
     *            Le nom de l'information.
     *
     * @return La valeur brute d'un information de l'attribut.
     */
    public Object getValue(String aInfoName) {

        return iInfo.get(aInfoName);
    }

    /**
     * Modifie la valeur brute d'un information de l'attribut.
     *
     * @param aInfoName
     *            Le nom de l'information.
     * @param aInfoValue
     *            La valeur de l'information.
     *
     * @return L'ancienne valeur de l'information.
     */
    public String setValue(String aInfoName, String aInfoValue) {

        return (String) iInfo.put(aInfoName, aInfoValue);
    }

    /**
     * Retourne l'information concernant l'attribut.
     *
     * @param aInfoName
     *            Le nom de l'information.
     *
     * @return L'information concernant l'attribut.
     */
    public Object getInfo(String aInfoName) {

        Object value = getValue(aInfoName);
        if (Arrays.asList(REQUIRED, HIDDEN, LIST, PASSWORD).contains(aInfoName)) {
            return getBoolean(value);
        }
        if (Arrays.asList(MIN, MAX).contains(aInfoName)) {
            return getInteger(value);
        }

        if (SEARCH.equals(aInfoName)) {
            if (value != null) {
                return AttributeType.valueOf(((String) value).toUpperCase()).toString();
            }

            if (isEqualDefault) {
                return AttributeType.EQUAL.toString();
            }
            return AttributeType.UPPER_FULL_LIKE.toString();
        }
        return value;
    }

    /**
     * Retourne le nom de la table contenant l'attribut.
     *
     * @return Le nom de la table contenant l'attribut.
     */
    public String getTable() {

        return (String) getValue(TABLE);
    }

    /**
     * Retourne la position dans le fichier "ValueObject.xsd" de l'attribut.
     *
     * @return La position dans le fichier "ValueObject.xsd" de l'attribut.
     */
    public int getPosition() {

        return ((Integer) getValue(POSITION)).intValue();
    }

    /**
     * Retourne le type de l'attribut.
     *
     * @return Le type de l'attribut.
     */
    public String getType() {

        return (String) getValue(TYPE);
    }

    /**
     * Indique si l'attribut est caché.
     *
     * @return <code>true</code> si l'attribut est caché, <code>false</code> sinon.
     */
    public boolean isHidden() {

        return ((Boolean) getInfo(HIDDEN)).booleanValue();
    }

    public boolean isRequired() {

        return ((Boolean) getInfo(REQUIRED)).booleanValue();
    }

    /**
     * Indique si l'attribut est fait partie de la liste de résultat.
     *
     * @return <code>true</code> si l'attribut fait partie de la liste de résultat, <code>false</code> sinon.
     */
    public boolean isList() {

        return ((Boolean) getInfo(LIST)).booleanValue();
    }

    /**
     * Retourne la longeur de l'attribut dans la base de données.
     *
     * @return La longeur de l'attribut dans la base de données.
     */
    public int getLength() {
        if (getValue(LENGTH) == null) {
            return 0;
        }
        return Integer.valueOf((String) getValue(LENGTH));
    }

    /**
     * Retourne le type de recherche à utiliser pour l'attribut. Soit <code>equal</code>, soit <code>like</code>.
     *
     * @return Le type de recherche à utiliser pour l'attribut.
     */
    public String getSearch() {

        return getInfo(SEARCH).toString();
    }

    /**
     * Indique si l'attribut doit être recherché avec une égalité stricte.
     *
     * @return <code>true</code> si l'attribut doit être recherché avec une égalité stricte, <code>false</code> sinon.
     */
    public boolean isEqual() {

        Object value = getInfo(SEARCH);
        if (AttributeType.EQUAL.toString().equals(value)) {
            return true;
        }
        return false;
    }

    /**
     * Indique si l'attribut doit être recherché avec un "LIKE".
     *
     * @return <code>true</code> si l'attribut doit être recherché avec un "LIKE", <code>false</code> sinon.
     */
    public boolean isLike() {

        return !isEqual();
    }

    /**
     * Indique si l'attribut est un mot de passe. Si l'attribut est de type java.lang.Baytes et est indiqué comme être un mot de passe,
     * alors au niveau de l'export dans l'ActionForm le String est crypté avec <code>com.inser.util.tools.SecurityTools</code>.
     *
     * @return <code>true</code> si l'attribut est un mot de passe, <code>false</code> sinon.
     */
    public boolean isPassword() {
        return ((Boolean) getInfo(PASSWORD)).booleanValue();
    }

    /**
     * Traduit on objet de classe <code>java.lang.String</code> en un objet de classe <code>java.lang.Boolean</code>. Le résultat est
     * <code>Boolean.TRUE</code> si <code>aValue.equal("true")</code>, <code>Boolean.FALSE</code> sinon.
     *
     * @param aValue
     *            L'objet à traduire.
     *
     * @return <code>Boolean.TRUE</code> si <code>aValue.equal("true")</code>, <code>Boolean.FALSE</code> sinon
     */
    private static Boolean getBoolean(Object aValue) {

        if (TRUE.equals(aValue)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * classe <code>java.lang.String</code> en un objet de classe <code>java.lang.Integer</code>.
     *
     * @param aValue
     *            valeur en string
     * @return valeur en Integer
     */
    private static Integer getInteger(Object aValue) {
        if (aValue == null || ((String) aValue).isEmpty()) {
            return null;
        }
        return Integer.parseInt((String) aValue);
    }

    /**
     * Retourne le nom du valueObject.
     *
     * @return Le nom du valueObject.
     */
    public String getVOName() {
        return iVOName;
    }

    /**
     * Retourne le nom de l'attribut.
     *
     * @return Le nom de l'attribut.
     */
    public String getName() {
        return iName;
    }

    /**
     * Retourne la representation textuelle de l'objet.
     *
     * @return La representation textuelle de l'objet
     */
    @Override
    public String toString() {

        StringBuffer sb = new StringBuffer(512);
        sb.append("AttributeInfo[iVOName=");
        sb.append(iVOName);
        sb.append(", iAttributeName=");
        sb.append(iName);
        sb.append(", iInfo=");
        if (iInfo == null) {
            sb.append("null");
        } else {
            sb.append(iInfo.toString());
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     *
     * @param aObject
     * @return
     */
    @Override
    public boolean equals(Object aObject) {

        if (!(aObject instanceof AttributeInfo)) {
            return false;
        }
        AttributeInfo attributeInfo = (AttributeInfo) aObject;
        if (!iName.equals(attributeInfo.iName)) {
            return false;
        }
        if (!iVOName.equals(attributeInfo.iVOName)) {
            return false;
        }
        if (!iInfo.equals(attributeInfo.iInfo)) {
            return false;
        }
        return iInfo.equals(attributeInfo.iInfo);
    }

    @Override
    public int hashCode() {

        return iName.hashCode() + iVOName.hashCode() + iInfo.hashCode();
    }

    public static void setEqualDefault(boolean equalDefault) {
        isEqualDefault = equalDefault;
    }
}