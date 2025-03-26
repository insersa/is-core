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

import org.jdom2.Element;

/**
 * Classe contenant les informations nécessaires à la création de popup (couche présentation) lors de la génération des pages JSF
 *
 * <pre>
 * {@code &lt;lookup&gt;}
 *       {@code &lt;objectName&gt;Objektkategorie&lt;/objectName&gt;}
 *       {@code &lt;popupId&gt;okt_id&lt;/popupId&gt;}
 *       {@code &lt;masterId&gt;otp_okt_id&lt;/masterId&gt;}
 *       {@code &lt;popupFields&gt;okt_id,okt_bezeichnung&lt;/popupFields&gt;}
 *       {@code &lt;masterFields&gt;otp_okt_id,okt_bezeichnung&lt;/masterFields&gt;}
 *     {@code &lt;/lookup&gt;}
 * </pre>
 *
 * @author INSER SA
 *
 */
public class LookupInfo implements Serializable {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -1495094125692031625L;

    /**
     * Nom de l'objet sur lequelle on effectue un lookup
     */
    private String iObjectName;

    /**
     * Nom du champ id de l'objet de lookup
     */
    private String iPopupId;

    /**
     * Nom de champ id de l'objet de déclenchement du lookup
     */
    private String iMasterId;

    /**
     * Liste des champs à ramener du popup
     */
    private String iPopupFields;

    /**
     * Liste des champs de mapping sur le master
     */
    private String iMasterFields;

    public LookupInfo(Element aElement) {
        iObjectName = readString(aElement, "objectName");
        iPopupId = readString(aElement, "popupId");
        iMasterId = readString(aElement, "masterId");
        iPopupFields = readString(aElement, "popupFields");
        iMasterFields = readString(aElement, "masterFields");
    }

    public String getObjectName() {
        return iObjectName;
    }

    public void setObjectName(String objectName) {
        iObjectName = objectName;
    }

    public String getPopupId() {
        return iPopupId;
    }

    public void setPopupId(String popupId) {
        iPopupId = popupId;
    }

    public String getMasterId() {
        return iMasterId;
    }

    public void setMasterId(String masterId) {
        iMasterId = masterId;
    }

    public String getPopupFields() {
        return iPopupFields;
    }

    public void setPopupFields(String popupFields) {
        iPopupFields = popupFields;
    }

    public String getMasterFields() {
        return iMasterFields;
    }

    public void setMasterFields(String masterFields) {
        iMasterFields = masterFields;
    }

    private static String readString(Element aElement, String aElementName) {
        if (aElement.getChild(aElementName) == null) {
            return null;
        }
        return aElement.getChild(aElementName).getText();
    }

}
