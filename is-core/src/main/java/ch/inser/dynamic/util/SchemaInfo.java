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

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;

/**
 * Class pour la gestion des informations contenues dans le fichier XML-Schema "ValueObject.xsd".
 *
 * @author INSER SA
 * @version 1.0
 */
public class SchemaInfo {

    public static final Namespace XS = Namespace.getNamespace("xs", "http://www.w3.org/2001/XMLSchema");

    /**
     * Définition du Logger utilisé pour le logging.
     */
    @SuppressWarnings("unused")
    private static final Log logger = LogFactory.getLog(SchemaInfo.class);

    /**
     * Contient l'information sur ce schema.
     */
    private Map<String, Object> iInfo;

    public SchemaInfo(URL aSchemaURL) throws JDOMException, IOException {

        iInfo = new HashMap<>();
        SAXBuilder builder = new SAXBuilder();
        builder.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        builder.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        Document doc = builder.build(aSchemaURL);
        List<?> list = doc.getRootElement().getChild("element", XS).getChild("complexType", XS).getChild("sequence", XS)
                .getChildren("element", XS);
        Iterator<?> it = list.iterator();
        while (it.hasNext()) {
            put(new VOInfo((Element) it.next()));
        }
    }

    private void put(VOInfo aVOInfo) {

        iInfo.put(aVOInfo.getName(), aVOInfo);
    }

    /**
     * Retourne l'information concernat un ValueObject.
     *
     * @param aVOName
     *            Le nom complet (package.class) du ValueObject recherché.
     *
     * @return L'information concernat le ValueObject recherché.
     */
    public VOInfo getVOInfo(String aVOName) {

        return (VOInfo) iInfo.get(aVOName);
    }

    public VOInfo getVOInfo(String aProjectPackage, String aObjectPackage, String aObjectName) {

        return getVOInfo(aProjectPackage + "." + aObjectPackage + ".business.VO" + aObjectName);
    }

    /**
     * Retourne l'information concernat un ValueObject.
     *
     * @param aVOName
     *            Le classe du ValueObject recherché.
     *
     * @return L'information concernat le ValueObject recherché.
     */
    public VOInfo getVOInfo(Class<?> aVOClass) {

        return getVOInfo(aVOClass.getName());
    }

    /**
     * Retourne le <code>Set</code> des noms des ValueObjects.
     *
     * @return Le <code>Set</code> des noms des ValueObjects.
     */
    public Set<String> getVONameSet() {

        return iInfo.keySet();
    }

    /**
     * Retourne la representation textuelle de l'objet.
     *
     * @return La representation textuelle de l'objet
     */
    @Override
    public String toString() {

        StringBuffer sb = new StringBuffer(512);
        sb.append("SchemaInfo[");
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

        if (!(aObject instanceof SchemaInfo)) {
            return false;
        }
        SchemaInfo schemaInfo = (SchemaInfo) aObject;
        return iInfo.equals(schemaInfo.iInfo);
    }

    @Override
    public int hashCode() {

        return iInfo.hashCode();
    }
}