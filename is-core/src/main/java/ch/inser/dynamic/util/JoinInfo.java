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
 * Class pour la gestion des informations concernant une jointure contenu dans le fichier XML-Schema "ValueObject.xsd".
 *
 * @author INSER SA
 * @version 1.0
 */
public class JoinInfo implements Serializable {

    /**
     * Serial Versio UID
     */
    private static final long serialVersionUID = -6579928961792892699L;

    /**
     * La clé du nom de la table de la joiture.
     */
    private static final String TABLE = "table";

    /**
     * La clé du com de la clause de jointure.
     */
    private static final String CLAUSE = "clause";

    /**
     * Le nom du ValueObject.
     */
    private String iVOName;

    /**
     * Le nom de la table de joiture.
     */
    private String iTable;

    /**
     * Le nom de la cause de joiture.
     */
    private String iClause;

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
    public JoinInfo(String aVOName, String aTable, String aClause) {

        iVOName = aVOName;
        iTable = aTable;
        iClause = aClause;
    }

    public JoinInfo(String aVOName, Element aElement) {

        this(aVOName, aElement.getChild(TABLE).getText(), aElement.getChild(CLAUSE).getText());
    }

    /**
     * Retourne la representation textuelle de l'objet.
     *
     * @return La representation textuelle de l'objet
     */
    @Override
    public String toString() {

        StringBuffer sb = new StringBuffer(512);
        sb.append("JoinInfo[iVOName=");
        sb.append(iVOName);
        sb.append(", iTable=");
        sb.append(iTable);
        sb.append(", iClause=");
        sb.append(iClause);
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
        JoinInfo joinInfo = (JoinInfo) aObject;
        if (!iVOName.equals(joinInfo.iVOName)) {
            return false;
        }
        if (!iTable.equals(joinInfo.iTable)) {
            return false;
        }
        if (!iClause.equals(joinInfo.iClause)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {

        return iVOName.hashCode() + iTable.hashCode() + iClause.hashCode();
    }

    public String getVOName() {
        return iVOName;
    }

    public void setVOName(String aVOName) {
        iVOName = aVOName;
    }

    public String getTable() {
        return iTable;
    }

    public void setTable(String aTable) {
        iTable = aTable;
    }

    public String getClause() {
        return iClause;
    }

    public void setClause(String aClause) {
        iClause = aClause;
    }
}