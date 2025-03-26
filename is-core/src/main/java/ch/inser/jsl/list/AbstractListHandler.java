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

package ch.inser.jsl.list;

import java.io.Serializable;
import java.util.List;

import ch.inser.jsl.exceptions.ISException;

/**
 * Classe abstraite offrant les méthodes de navigation par pages dans une collection de données. Cette classe doit être héritée par les
 * classes implémentant le pattern J2EE "list handler".
 *
 * @version 1.0
 * @author INSER SA
 *
 * @see ListHandler
 */
public abstract class AbstractListHandler<T> implements ListHandler<T>, Serializable {

    // ----------------------------------------------------- Variables
    // d'instance

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 2447469118504142040L;

    /** Liste résultat provenant du DAO et géré par le list handler. */
    protected List<T> iList;

    protected String iContextName = ListHandler.CONTEXT_DEFAULT;

    /** Taille de la collection (répliqué pour +performance) */
    protected int iListSize;

    /**
     * Nombre de records retournables par la requête. Si la taille de liste est inférieur à la limite "hard", alors cette propriété est
     * égale à la taille de la liste, sinon elle sera supérieure.
     */
    protected int iNbQueryResults;

    /** Index de colonne pour le tri. */
    protected int iSortIdx;

    /** Orientation du tri (ascendant, descendant) */
    protected ListHandler.Sort iSortOrientation;

    /** Index du record pour la navigation dans la liste de résultat */
    private int iRecordIndexInList;

    // ----------------------------------------------------- Constructeur

    /** Constructeur. */
    protected AbstractListHandler() {
        // Valeurs par défaut
        iSortIdx = 1;
        iSortOrientation = ListHandler.Sort.ASCENDING;
    }

    // ----------------------------------------------------- Méthodes membres

    /**
     * Retourne la taille de la liste.
     *
     * @roseuid 3B052FB40277
     */
    @Override
    public int getSize() {
        return iListSize;
    }

    /**
     * Retourne le nombre de records correspondant à la requête. Peut être égal à la taille de la liste ou supérieur.
     */
    @Override
    public int getNbQueryResults() {
        return iNbQueryResults;
    }

    /**
     * Retourne l'index de tri
     */
    @Override
    public int getSortIndex() {
        return iSortIdx;
    }

    /**
     * Définit l'index de tri.
     */
    @Override
    public void setSortIndex(int sortIdx) {
        iSortIdx = sortIdx;
    }

    /**
     * Retourne l'orientation du tri
     */
    @Override
    public ListHandler.Sort getSortOrientation() {
        return iSortOrientation;
    }

    /**
     * Définit l'orientation du tri.
     *
     * @param sortOrientation
     *            Orientation du tri. Trois valeurs possibles:<br>
     *            - ListHandler.SORT_ORIENTATION_ASCENDING<br>
     *            - ListHandler.SORT_ORIENTATION_DESCENDING<br>
     *            - ListHandler.SORT_ORIENTATION_PERMUTE (permuter le sens)
     */
    @Override
    public void setSortOrientation(ListHandler.Sort sortOrientation) {
        if (sortOrientation == ListHandler.Sort.PERMUTE) {
            iSortOrientation = iSortOrientation == ListHandler.Sort.ASCENDING ? ListHandler.Sort.DESCENDING : ListHandler.Sort.ASCENDING;
        } else {
            iSortOrientation = sortOrientation;
        }
    }

    /**
     * Rafraîchit le set de données associé au list handler.
     *
     * @param sortIdx
     *            Index de tri selon le nombre de colonnes.
     */
    @Override
    public abstract void refresh(int sortIdx) throws ISException;

    /**
     * Fournit la collection en entier.
     */
    @Override
    public List<T> getList() {
        return iList;
    }

    /**
     * Retourne l'index de navigation du record courant dans la liste
     */
    public int getRecordIndexInList() {
        return iRecordIndexInList;
    }

    /**
     * Définit l'index de navigation du record courant dans la liste
     */
    public void setRecordIndexInList(int recordIndexInList) {
        iRecordIndexInList = recordIndexInList;
    }

    /**
     * Indique si un record précédant existe dans la liste
     */
    public boolean isPreviousRecordInList() {
        return iRecordIndexInList > 0;
    }

    /**
     * Indique si un record suivant existe dans la liste
     */
    public boolean isNextRecordInList() {
        return iRecordIndexInList < iListSize - 1;
    }

    /**
     * Retourne le pager offset.
     */
    @Override
    public int getPagerOffset(int nbrItem) {

        return getRecordIndexInList() / nbrItem;
    }

    @Override
    public String getContextName() {
        return iContextName;
    }

    public void setContextName(String string) {
        iContextName = string;
    }

    @Override
    public void refreshSize() {
        if (iList != null) {
            iListSize = iList.size();
        }
    }
}
