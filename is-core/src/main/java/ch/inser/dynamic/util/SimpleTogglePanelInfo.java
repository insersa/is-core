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
 * Class de gestion des toggles panels par défaut au niveau de l'objet métier
 *
 * @author INSER SA
 *
 */
public class SimpleTogglePanelInfo {

    /**
     * Le nom du ValueObject.
     */
    private String iVOName;

    /**
     * Le nom du panel.
     */
    private String iName;

    /**
     * Le nom de l'objet enfant
     */
    private Boolean iOpen;

    public SimpleTogglePanelInfo(String aVOName, Element aElement) {
        iVOName = aVOName;

        iName = readString(aElement, "name");
        iOpen = readBoolean(aElement, "open");
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

    public String getName() {
        return iName;
    }

    public Boolean getOpen() {
        return iOpen;
    }

}
