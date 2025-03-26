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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom2.Element;

import ch.inser.jsl.list.ListHandler.Sort;

/**
 * Class pour la gestion des informations concernant un critère de tri contenu dans le fichier XML-Schema "ValueObject.xsd".
 *
 * @author INSER SA
 * @version 1.0
 */
public class SortInfo {

    private static final Log logger = LogFactory.getLog(SortInfo.class);

    /**
     * Le nom du ValueObject.
     */
    private String iVOName;

    /**
     * L'Id du sort.
     */
    private String iId;

    /**
     * Les attributs du critère de tri.
     */
    private List<String> iItems;

    /**
     * Les orientations des items du critère de tri.
     */
    private List<Sort> iOrientations;

    /**
     * Les 'toggable' des items du critère de tri. (Toggable: l'orientation peut être changé dans le GUI)
     */
    private List<Boolean> iToggables;

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
    @SuppressWarnings("unchecked")
    public SortInfo(String aVOName, Map<String, List<?>> aItemMap, String id) {
        iVOName = aVOName;
        iItems = (List<String>) aItemMap.get("items");
        iOrientations = (List<Sort>) aItemMap.get("orientations");
        iToggables = (List<Boolean>) aItemMap.get("toggables");
        iId = id;
    }

    public SortInfo(String aVOName, Element aElement) {
        this(aVOName, readItems(aElement), getIdElement(aElement));
    }

    private static String getIdElement(Element aElement) {
        List<Element> list = aElement.getChildren("id");
        if (list == null || list.isEmpty() || list.size() > 1) {
            return null;
        }
        return list.get(0).getText();
    }

    /**
     *
     * @param aElement
     *            sort avec items. Un item est un element complex avec trois enfants: nom, orientation et toggable
     * @return map avec les items, orientations, et toggables
     */
    private static Map<String, List<?>> readItems(Element aElement) {

        List<Element> elements = aElement.getChildren("item");
        List<String> items = new ArrayList<>(elements.size());
        List<Sort> orientations = new ArrayList<>(elements.size());
        List<Boolean> toggables = new ArrayList<>(elements.size());
        for (Element elem : elements) {

            // Name
            items.add(elem.getChildText("name"));

            // Sort
            Sort sort = Sort.ASCENDING;
            if (elem.getChildText("sort") != null) {
                try {
                    sort = Sort.valueOf(elem.getChildText("sort"));
                } catch (Exception e) {
                    logger.warn("Sort configuration not correct", e);
                }
            }
            orientations.add(sort);

            // Toggable
            Boolean toggable = true;
            if (elem.getChildText("toggable") != null) {
                try {
                    toggable = Boolean.valueOf(elem.getChildText("toggable"));
                } catch (Exception e) {
                    logger.warn("Toggable configuration not correct", e);
                }
            }
            toggables.add(toggable);
        }
        Map<String, List<?>> itemsMap = new HashMap<>();
        itemsMap.put("items", items);
        itemsMap.put("orientations", orientations);
        itemsMap.put("toggables", toggables);
        return itemsMap;
    }

    /**
     * Retourne la representation textuelle de l'objet.
     *
     * @return La representation textuelle de l'objet
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(512);
        sb.append("SortInfo[iVOName=");
        sb.append(iVOName);
        sb.append(", iItems=");
        sb.append(iItems);
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

        if (!(aObject instanceof JoinInfo)) {
            return false;
        }
        SortInfo sortInfo = (SortInfo) aObject;
        if (!iVOName.equals(sortInfo.iVOName)) {
            return false;
        }
        if (!iItems.equals(sortInfo.iItems)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {

        return iVOName.hashCode() + iItems.hashCode();
    }

    public String getVOName() {
        return iVOName;
    }

    public void setVOName(String aVOName) {
        iVOName = aVOName;
    }

    public List<String> getItems() {
        return iItems;
    }

    public void setItems(List<String> aItems) {
        iItems = aItems;
    }

    public String getId() {
        return iId;
    }

    public void setId(String aId) {
        iId = aId;
    }

    public List<Sort> getOrientations() {
        return iOrientations;
    }

    public void setOrientations(List<Sort> orientations) {
        iOrientations = orientations;
    }

    public List<Boolean> getToggables() {
        return iToggables;
    }

    public void setToggables(List<Boolean> toggables) {
        iToggables = toggables;
    }

}