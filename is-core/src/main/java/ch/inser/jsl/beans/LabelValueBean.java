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

package ch.inser.jsl.beans;

/**
 * Simple javabean représentant les paires "label-value" pour être utilisé dans les collections utilisées par le tag
 * <code>&lt;html:options&gt;</code> tag.
 *
 * @version 1.0
 * @author INSER SA
 * @author INSER SA
 */
public class LabelValueBean {
    // ------------------------------------------------------- Variables
    // d'instance

    /** Le label à afficher à l'utilisateur. */
    protected String iLabel = null;

    /** La valeur à retourner au serveur. */
    protected String iValue = null;
    protected String iLabelsup = null;

    // ------------------------------------------------------- Constructeur

    /**
     * Construit un nouveau LabelValueBean avec les valeurs spécifiées.
     *
     * @param label
     *            Le label à afficher à l'utilisateur.
     * @param value
     *            La valeur à retourner au serveur.
     */
    public LabelValueBean(String label, String value) {
        iLabel = label;
        iValue = value;
    }

    public LabelValueBean(String label, String labelsup, String value) {
        iLabelsup = labelsup;
        iLabel = label;
        iValue = value;
    }

    // --------------------------------------------------------- Propriétés

    public String getLabel() {
        return iLabel;
    }

    public String getValue() {
        return iValue;
    }

    public String getLabelsup() {
        return iLabelsup;
    }

    // --------------------------------------------------------- Méthodes
    // membres

    /**
     * Retourne une représentation string de cet objet.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("LabelValueBean[");
        sb.append(iLabel);
        sb.append(", ");
        sb.append(iValue);
        sb.append("]");
        return sb.toString();
    }
}