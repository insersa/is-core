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

import java.util.List;

import ch.inser.jsl.exceptions.ISException;

/**
 * Interface offrant les méthodes de navigation par pages dans une collection de données. Cette interface doit être implémentée par les
 * classes implémentant le pattern J2EE "list handler" ou bien par la méthode abstraite AbstractListHandler.
 *
 * @version 1.0
 * @author INSER SA
 *
 * @see AbstractListHandler
 */
public interface ListHandler<T> {

    /**
     * Orientation du tri
     */
    public enum Sort {
        ASCENDING, DESCENDING, PERMUTE
    }

    /** Contexte par défaut */
    public static final String CONTEXT_DEFAULT = "default";

    /**
     * Définit l'index de tri.
     */
    public void setSortIndex(int sortIdx);

    /**
     * Returne l'index de tri
     */
    public int getSortIndex();

    /**
     * Retourne l'orientation du tri
     */
    public Sort getSortOrientation();

    /**
     * Définit l'orientation du tri
     */
    public void setSortOrientation(Sort sortOrientation);

    /**
     * Retourne la taille de la liste.
     *
     * @roseuid 3AFA624100E4
     */
    public int getSize();

    public String getContextName();

    /**
     * Retourne le nombre de records correspondant à la requête. Peut être égal à la taille de la liste ou supérieur.
     */
    public int getNbQueryResults();

    /**
     * Rafraîchit le set de données associé au list handler.
     *
     * @param sortIdx
     *            Index de tri selon le nombre de colonnes.
     */
    public void refresh(int sortIdx) throws ISException;

    /**
     * Fournit la collection en entier.
     */
    public List<T> getList();

    /**
     * Retourne le pager offset.
     */
    public int getPagerOffset(int nbrItem);

    /**
     * Recalcule la taille en fonction de la taille de la liste
     */
    public void refreshSize();
}
