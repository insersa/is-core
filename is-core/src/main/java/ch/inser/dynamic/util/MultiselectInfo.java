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
 * Class pour la gestion des informations concernant un children contenu dans le fichier XML-Schema "ValueObject.xsd".
 *
 * @author INSER SA
 * @version 1.0
 */
public class MultiselectInfo {

    /**
     * Le nom du ValueObject.
     */
    private String iVOName;

    /**
     * Le nom de l'objet multiselect
     */
    private String iSelectName;

    /**
     * Le nom du champ lié côté master
     */
    private String iMasterLink;

    /**
     * Le nom du champ lié côté children
     */
    private String iSelectLink;

    /**
     * Nom de l'attribut de l'objet mutliselect utilisé pour l'affichage
     */
    private String iDisplayName;

    /**
     * requête pour table de lien dans le cas d'une relation n-n
     */
    private String iLinkName;

    /**
     * Constructeur basée sur la mappe générée par SchemaReader.
     *
     * @param aVOName
     *            Le nom complet (package.class) de ce type (classe) de ValueObject.
     * @param aTable
     *            Le nom de la table de jointure.
     * @param aClause
     *            La clause de joiture.
     */

    public MultiselectInfo(String aVOName, Element aElement) {
        iVOName = aVOName;
        iSelectName = readString(aElement, "name");
        iMasterLink = readString(aElement, "mlink");
        iSelectLink = readString(aElement, "slink");
        iLinkName = readString(aElement, "link");
        iDisplayName = readString(aElement, "display");
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

    /**
     * Get the name of the object base of the multiselect.
     *
     * @return
     */
    public String getSelectName() {
        return iSelectName;
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
     * Get the attribute name of the link on the multiselect side.
     *
     * @return
     */
    public String getSelectLink() {
        return iSelectLink;
    }

    /**
     * Get the name of the object resolving de n-n relationship.
     *
     * @return
     */
    public String getLinkName() {
        return iLinkName;
    }

    /**
     * Get the name of the attribute of multiselect object used to display.
     *
     * @return
     */
    public String getDisplayName() {
        return iDisplayName;
    }

}