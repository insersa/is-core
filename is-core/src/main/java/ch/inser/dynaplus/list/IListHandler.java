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

package ch.inser.dynaplus.list;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import ch.inser.dynamic.common.IValueObject;
import ch.inser.jsl.exceptions.ISException;

/**
 * Interface IListHandler dans dynaplus, sera supprimée à terme de JSL
 *
 * @author INSER SA
 *
 */
public interface IListHandler extends ch.inser.jsl.list.ListHandler<IValueObject> {

    /**
     * Etat du vo, le traitement de la liste lors de l'enregistrement sera modifié dépendant de l'état.
     *
     * @author INSER SA
     *
     */
    public enum State {

        /**
         * Création de l'objet
         */
        CREATE,
        /**
         * Modification de l'objet
         */
        UPDATE,
        /**
         * Conservation de l'original malgrè une modification
         */
        ORIGINAL
    }

    /**
     * Retourne le vo en fonction de l'ID
     *
     * @param aId
     *            id de l'objet
     * @return l'objet ou null si pas trouvé
     */
    public IValueObject getVO(Object aId);

    /**
     * Retourne la liste de vo ou un champ (aFieldname) est égale à une valeur (aValue)
     *
     * @param aFieldname
     *            nom du champ pour le test
     * @param aValue
     *            valeur pour le test
     * @return liste de vo à traiter
     */
    public List<IValueObject> getLstVo(String aFieldname, Object aValue);

    /**
     * Retourne la liste des indexes des vo ou un champ (aFieldname) est égale à une valeur (aValue)
     *
     * @param aFieldname
     *            nom du champ pour le test
     * @param aValue
     *            valeur pour le test
     * @return liste des indexes
     */
    public List<Integer> getLstIndex(String aFieldname, Object aValue);

    /**
     * Suppression d'un vo de la liste selon l'id du vo
     *
     * @param aId
     *            identifiant du VO à supprimer
     * @return vrai si l'enregistrement a été supprimé
     */
    public Boolean removeVO(Object aId);

    /**
     * Suppression d'un vo de la liste selon l'index du vo
     *
     * @param anIndex
     *            index du VO à supprimer
     * @return l'enregistrement supprimé, null si l'index n'est pas correct
     */
    public IValueObject removeIndex(int anIndex);

    /**
     * Modification ou création d'un vo dans la liste.
     *
     * @param aVo
     *            vo avec modification
     * @return le vo modifié
     */
    public IValueObject setVO(IValueObject aVo);

    /**
     * Ajout d'un vo dans la liste.
     *
     * @param aVo
     *            nouveau vo
     */
    public void addVO(IValueObject aVo);

    /**
     * Modification de l'état du vo
     *
     * @param aId
     *            l'identifiant de l'objet
     * @param aState
     *            statut de l'objet
     * @return true si l'état de l'objet a été modifié
     */
    public Boolean setState(Object aId, State aState);

    /**
     * Création de checkbox, le checkbox est à true si la valeur du champ aNamePropTest est égale à aValuePropTest
     *
     * @param aNewNameProp
     *            Nom du champ de la checkbox
     * @param aNamePropTest
     *            Nom du champ de la liste qui va être testé
     * @param aValuePropTest
     *            Valeur avec laquelle le champ va être testé
     */
    public void createCheckbox(String aNewNameProp, String aNamePropTest, Object aValuePropTest);

    public Map<Integer, String> getMapVoState();

    public List<Object> getLstRemoveField();

    /**
     * Définit le nombre mximum de résultat. 0 indique pas de max null indique max par défaut (configuration)
     *
     * @param anInteger
     */
    public void setMaxRowResult(Integer anInteger);

    /**
     * Modification d'une propriété pour tous les vo de la liste
     *
     * @param aName
     *            nom de la propriété
     * @param aValue
     *            valeur de la propriété
     */
    public void setProperties(String aName, Object aValue);

    /**
     * Récupère la liste des valeurs des propriétés données, a priori cette collection peut avoir une taille plus petite que la taille de la
     * liste (implémentation avec unicité des valeus)
     *
     * @param aName
     * @return
     */
    public Collection<Object> getProperties(String aName);

    /**
     * Test le contenu de la liste pour déterminer si un élément vérifie la condition d'égalité (ou d'inégalité) posée
     *
     * @param names
     *            liste des noms de paramètres
     * @param values
     *            listes des valeurs testées
     * @equals égalité (true), inégalité (false)
     * @return
     */
    public boolean contains(String[] names, Object[] values, boolean[] equals);

    /**
     * Test le contenu de la liste pour déterminer si un élément vérifie la condition d'égalité posée
     *
     * @param names
     *            liste des noms de paramètres
     * @param values
     *            listes des valeurs testées
     * @return
     */
    public boolean contains(String[] names, Object[] values);

    /**
     * Obtient un sous listhandler en suivant les conditions d'égalité posée
     *
     * @param names
     * @param values
     * @return
     */
    public IListHandler getFilteredSubList(String[] names, Object[] values);

    /**
     * Obtient un sous listhandler en suivant les conditions d'égalité posée
     *
     * @param name
     * @param value
     * @return
     */
    public IListHandler getFilteredSubList(String name, Object value);

    /**
     * Refresh de la liste (a priori retour sur la base de donnée) avec une clé de tri.
     *
     * @param sortKey
     *            clé de tri
     * @throws ISException
     *             si un problème survient
     */
    public void refresh(String sortKey) throws ISException;

    /**
     * Ajout d'une collection de VO à la liste
     *
     * @param aList
     *            collection de VO
     */
    public void addAll(Collection<? extends IValueObject> aList);

    /**
     * Modification du query de la liste
     *
     * @param aVo
     *            contient le query à utiliser
     */
    public void setQuery(IValueObject aVo);

    public IValueObject getQuery();

    /**
     * Retourne la liste de champs sélectionnée par utilisateur
     *
     * @return
     */
    public IValueObject[] getSelectedVos();

    /**
     * Modification de la liste sélectionnée par l'utilisateur
     *
     * @param aSelectedVos
     */
    public void setSelectedVos(IValueObject[] aSelectedVos);

    /**
     * Création d'un liste Handler de même implémentation
     *
     * @param type
     * @return
     */
    public IListHandler createListHandler(String type);

}
