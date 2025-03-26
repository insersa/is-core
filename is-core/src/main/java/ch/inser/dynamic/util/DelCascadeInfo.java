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
 * Class pour la gestion des informations concernant le delete en cascade contenu dans le fichier XML-Schema "ValueObject.xsd" du BO.
 *
 * @author INSER SA
 * @version 1.0
 */
public class DelCascadeInfo {

    /**
     * Le nom du ValueObject.
     */
    private String iVOName;

    /**
     * Le nom de l'objet delcascade
     */
    private String iName;

    /**
     * Lien sur la Primary key de la table source
     */
    private String iPrimaryKey;

    /**
     * Lien sur la Foreign Key de la table enfant
     */
    private String iForeignKey;

    /**
     * Permission de realiser un delete en cascade
     */
    private String iActivate;

    /**
     * Valeur permettant de choisir si on désire supprimer l'enfant ou le mettre à null (valeur par défaut true)
     */
    private String iDelete;

    /**
     * Constructeur basée sur la mappe générée par SchemaReader.
     *
     * @param aVOName
     *            Le nom complet (package.class) de ce type (classe) de ValueObject.
     * @param aElement
     *            Le nom de l element (DOM du fichier xsd)
     */

    public DelCascadeInfo(String aVOName, Element aElement) {
        iVOName = aVOName;
        iName = readString(aElement, "name");
        iPrimaryKey = readString(aElement, "primarykey");
        iForeignKey = readString(aElement, "foreignkey");
        iActivate = readString(aElement, "activate");
        iDelete = readString(aElement, "delete");
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
    public String getName() {
        return iName;
    }

    /**
     * Get the attribute name of the primary key (parent table)
     *
     * @return
     */
    public String getPrimaryKey() {
        return iPrimaryKey;
    }

    /**
     * Get the attribute name of the foreign key (children table)
     *
     * @return
     */
    public String getForeignKey() {
        return iForeignKey;
    }

    /**
     * Get if the user can do a delete cascade (true = you can delete cascade)
     *
     * @return
     */
    public String getActivate() {
        return iActivate;
    }

    /**
     * Get if the children row have to be to remove or if the foreign key is setting to null
     *
     * @return
     */
    public String getDelete() {
        return iDelete;
    }

}
