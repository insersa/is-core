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
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;

/**
 * Class pour la gestion des informations concernant un children contenu dans le fichier XML-Schema "ValueObject.xsd".
 *
 * @author INSER SA
 * @version 1.0
 */
public class ChildrenInfo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -5086595865493356241L;

    /**
     * Le nom du ValueObject.
     */
    private String iVOName;

    /**
     * Le nom de l'objet enfant
     */
    private String iChildrenName;

    /**
     * Le nom de define de l'enfant (facelets)
     */
    private String iChildrenDefine;

    /**
     * Le nom du fichier de config contenant les infos du children
     */
    private String iChildrenConfig;

    /**
     * Le nom du champ lié côté master
     */
    private String iMasterLink;

    /**
     * Le nom du champ lié côté children
     */
    private String iChildrenLink;

    /**
     * requête pour table de lien dans le cas d'une relation n-n
     */
    private String iLinkTable;

    /**
     * Les attributs de la liste.
     */
    private List<String> iFields;

    /**
     * Les childrens.
     */
    private List<ChildrenInfo> iChildrens;

    /**
     * Transposé?
     */
    private boolean iTransposed;

    /**
     * Avec petits enfants?
     */
    private boolean iSubChildrens;

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

    public ChildrenInfo(String aVOName, Element aElement) {
        iVOName = aVOName;
        iChildrenName = readString(aElement, "name");
        iChildrenDefine = readString(aElement, "define");
        if (iChildrenDefine == null) {
            iChildrenDefine = "children";
        }
        iChildrenConfig = readString(aElement, "config");
        iMasterLink = readString(aElement, "mlink");
        iChildrenLink = readString(aElement, "clink");
        iLinkTable = readString(aElement, "linktable");
        iFields = readFields(aElement);
        iChildrens = readChildrens(iChildrenName, aElement);
        iTransposed = readBoolean(aElement, "transposed");
        iSubChildrens = readBoolean(aElement, "subchildrens");

    }

    private static List<String> readFields(Element aElement) {

        List<Element> elements = aElement.getChildren("field");
        if (elements == null || elements.isEmpty()) {
            return null;
        }
        List<String> items = new ArrayList<>(elements.size());
        for (Element elem : elements) {
            items.add(elem.getText());
        }
        return items;
    }

    private static List<ChildrenInfo> readChildrens(String aName, Element aElement) {
        List<Element> elements = aElement.getChildren("children");
        if (elements == null || elements.isEmpty()) {
            return null;
        }
        List<ChildrenInfo> items = new ArrayList<>(elements.size());
        for (Element elem : elements) {
            items.add(new ChildrenInfo(aName, elem));
        }
        return items;
    }

    private static String readString(Element aElement, String aElementName) {
        if (aElement.getChild(aElementName) == null) {
            return null;
        }
        return aElement.getChild(aElementName).getText();
    }

    /**
     * Default is false
     *
     * @param aElement
     * @param aElementName
     * @return
     */
    private static boolean readBoolean(Element aElement, String aElementName) {
        if (aElement.getChild(aElementName) == null) {
            return false;
        }
        try {
            return Boolean.parseBoolean(aElement.getChild(aElementName).getText());
        } catch (@SuppressWarnings("unused") Exception e) {// NOSONAR
            // Something wrong...answer by false
            return false;
        }
    }

    public String getVOName() {
        return iVOName;
    }

    public String getChildrenName() {
        return iChildrenName;
    }

    public String getChildrenDefine() {
        return iChildrenDefine;
    }

    public String getChildrenConfig() {
        return iChildrenConfig;
    }

    public String getMasterLink() {
        return iMasterLink;
    }

    public String getChildrenLink() {
        return iChildrenLink;
    }

    public List<String> getFields() {
        return iFields;
    }

    public List<ChildrenInfo> getChildrens() {
        return iChildrens;
    }

    public boolean isTransposed() {
        return iTransposed;
    }

    public boolean isSubChildrens() {
        return iSubChildrens;
    }

    public String getLinkTable() {
        return iLinkTable;
    }

    public void setLinkTable(String linkTable) {
        iLinkTable = linkTable;
    }

}