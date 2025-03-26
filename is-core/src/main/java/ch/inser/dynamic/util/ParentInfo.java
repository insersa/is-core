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

import org.jdom2.Element;

/**
 * Class pour la gestion des informations concernant un parent.
 *
 * @author INSER SA
 * @version 1.0
 */
public class ParentInfo {

    /**
     * Le nom du ValueObject.
     */
    private String iVOName;

    /**
     * Le nom de l'objet parent
     */
    private String iName;

    /**
     * Le nom du champ lié côté master
     */
    private String iMasterLink;

    /**
     * nom de la clé du parent dans le vo enfant
     */
    private String iKeyName;

    /**
     * Constructeur basée sur la mappe générée par SchemaReader.
     */

    public ParentInfo(String aVOName, Element aElement) {
        iVOName = aVOName;
        iName = readString(aElement, "name");
        iMasterLink = readString(aElement, "mlink");
        iKeyName = readString(aElement, "keyname");
        if (iKeyName == null) {
            iKeyName = iName;
        }
    }

    private static String readString(Element aElement, String aElementName) {
        if (aElement.getChild(aElementName) == null) {
            return null;
        }
        return aElement.getChild(aElementName).getText();
    }

    public String getVOName() {
        return iVOName;
    }

    public String getName() {
        return iName;
    }

    /**
     * Get the attribute name of the link on the master side.
     *
     * @return
     */
    public String getMasterLink() {
        return iMasterLink;
    }

    /**
     * Get the name of the object resolving de n-n relationship.
     *
     * @return
     */
    public String getKeyName() {
        return iKeyName;
    }
}