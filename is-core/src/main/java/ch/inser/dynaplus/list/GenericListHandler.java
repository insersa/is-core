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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.inser.dynamic.common.DAOParameter;
import ch.inser.dynamic.common.ILoggedUser;
import ch.inser.dynamic.common.IValueObject;
import ch.inser.dynaplus.bo.BPFactory;
import ch.inser.dynaplus.bo.IBusinessProcess;
import ch.inser.jsl.exceptions.ISException;
import ch.inser.jsl.tools.PropertyTools;

public class GenericListHandler extends AbstractListHandler {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -195030267801632357L;

    private static final Log logger = LogFactory.getLog(GenericListHandler.class);

    // ----------------------------------------------------- Constructeur
    /**
     * Constructeur du list handler pour les projets de construction. Cet objet permet de manipuler une liste de projets de construction.
     * <p>
     * Le constructeur instancie le DAO correpondant aux projets de construction.
     *
     *
     * @param name
     *            Nome de l'objet métier
     * @param valueObject
     *            Value object. Contient les critères de recherche.
     * @param user
     *            Objet de l'utilisateur.
     */
    public GenericListHandler(String type, IValueObject vo, Integer aMaxRowResult, ILoggedUser user) throws ISException {
        super(type, vo, aMaxRowResult, user);
    }

    public GenericListHandler(String type, IValueObject vo, ILoggedUser user) throws ISException {
        super(type, vo, user);
    }

    public GenericListHandler(String type, ILoggedUser user) {
        super(type, user);
    }

    public GenericListHandler(String type) {
        super(type);
    }

    @Override
    public void refresh(int sortIdx) throws ISException {
        if (getQuery() == null || iUser == null) {
            return;
        }
        long startTime = 0;
        if (logger.isDebugEnabled()) {
            logger.debug("Refresh start");
            startTime = new Date().getTime();
        }
        // Recueille les paramètres
        iSortIdx = sortIdx;

        IBusinessProcess bp = BPFactory.getInstance().getBP(getType());
        // Demande la liste des objets recherchés
        // la version sont iMaxRowResult est nécessaire pour la compatibilité
        // entre projet (si le getList est redéfini dans le BP et BO)
        if (iMaxRowResult == null) {
            iList = bp.getList(getQuery(), iUser, new DAOParameter(DAOParameter.Name.SORT_INDEX, sortIdx)).getListObject();
        } else {
            iList = bp.getList(getQuery(), iUser, new DAOParameter(DAOParameter.Name.SORT_INDEX, sortIdx),
                    new DAOParameter(DAOParameter.Name.ROWNUM_MAX, iMaxRowResult)).getListObject();
        }

        iListSize = iList.size();

        if (logger.isDebugEnabled()) {
            long timeEllapsed = new Date().getTime() - startTime;
            logger.debug("Refresh end : " + timeEllapsed + "ms");
        }
    }

    @Override
    public void refresh(String sortKey) throws ISException {
        if (getQuery() == null || iUser == null) {
            return;
        }

        long startTime = 0;
        if (logger.isDebugEnabled()) {
            logger.debug("Refresh start");
            startTime = new Date().getTime();
        }

        IBusinessProcess bp = BPFactory.getInstance().getBP(getType());
        // Demande la liste ddes objets recherchés
        // la version sont iMaxRowResult est nécessaire pour la compatibilité
        // entre projet (si le getList est redéfini dans le BP et BO)
        if (iMaxRowResult == null) {
            iList = bp.getList(getQuery(), iUser, new DAOParameter(DAOParameter.Name.SORT_KEY, sortKey),
                    new DAOParameter(DAOParameter.Name.SORT_ORIENTATION, getSortOrientation())).getListObject();
        } else {
            iList = bp.getList(getQuery(), iUser, new DAOParameter(DAOParameter.Name.SORT_KEY, sortKey),
                    new DAOParameter(DAOParameter.Name.SORT_ORIENTATION, getSortOrientation()),
                    new DAOParameter(DAOParameter.Name.ROWNUM_MAX, iMaxRowResult)).getListObject();
        }

        iListSize = iList.size();

        if (logger.isDebugEnabled()) {
            long timeEllapsed = new Date().getTime() - startTime;
            logger.debug("Refresh end : " + timeEllapsed + "ms");
        }
    }

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
    @Override
    public void createCheckbox(String aNewNameProp, String aNamePropTest, Object aValuePropTest) {
        if (iList != null) {
            for (Object vo : iList) {
                if (((IValueObject) vo).getProperty(aNamePropTest) != null) {
                    if (((IValueObject) vo).getProperty(aNamePropTest).equals(aValuePropTest)) {
                        ((IValueObject) vo).setProperty(aNewNameProp, true);
                    } else {
                        ((IValueObject) vo).setProperty(aNewNameProp, false);
                    }
                } else {
                    ((IValueObject) vo).setProperty(aNewNameProp, false);
                }

            }
        }
    }

    /** Indique le précédant Id du record dans la liste pour la navigation */
    public Object getPreviousRecordIdInList() {
        int index = getRecordIndexInList() - 1;
        setRecordIndexInList(index);
        return iList.get(index).getId();
    }

    /** Indique le prochain Id du record dans la liste pour la navigation */
    public Object getNextRecordIdInList() {
        int index = getRecordIndexInList() + 1;
        setRecordIndexInList(index);
        return iList.get(index).getId();
    }

    @Override
    public Boolean setState(Object aId, State aState) {

        for (int index = 0; index < iList.size(); index++) {
            if (iList.get(index).getId().equals(aId)) {
                iMapVoState.put(index, aState.toString());
                return true;
            }
        }

        return false;

    }

    @Override
    public IValueObject setVO(IValueObject aVo) {

        if (aVo.getId() != null) {
            // Cherche le vo pour modification
            for (int index = 0; index < iList.size(); index++) {
                if (iList.get(index).getId().equals(aVo.getId())) {
                    iList.set(index, aVo);
                    iMapVoState.put(index, State.UPDATE.toString());
                    return iList.get(index);
                }
            }
        } else {
            // Si le champs n'existe pas, on réalise la création du champ
            iList.add(iList.size(), aVo);
            iMapVoState.put(iListSize++, State.CREATE.toString());
            return iList.get(iListSize - 1);
        }
        return null;
    }

    @Override
    public IValueObject getVO(Object aId) {
        // Recherche d'un clone du vo, selon id
        for (int index = 0; index < iList.size(); index++) {
            if (iList.get(index).getId().equals(aId)) {
                return (IValueObject) iList.get(index).clone();
            }
        }

        return null;
    }

    /**
     * Suppression d'un champ à partir du GUI
     *
     * @return
     */
    public String removeGui() {
        logger.debug("Remove id :" + iRemoveGuiId);
        // Recherche de l'id en format integer
        int id = PropertyTools.getInteger(iRemoveGuiId);

        // suppression du champ
        if (getList().get(id).getId() != null) {
            // si id existant données dans la db donc suppression std
            removeVO(getList().get(id).getId());
        } else {
            // suppression pas encore dans la db donc simple suppression
            iList.remove(id);
            iListSize--;
            iMapVoState.remove(id);
            // refaire le MapVoState
            Map<Integer, String> mapTampon = new HashMap<>();

            for (Map.Entry<Integer, String> val : iMapVoState.entrySet()) {
                if (val.getKey() > id) {
                    mapTampon.put(val.getKey() - 1, val.getValue());
                } else {
                    mapTampon.put(val.getKey(), val.getValue());
                }
            }

            iMapVoState = mapTampon;

        }
        return null;
    }

    public String getRemoveGuiId() {
        return iRemoveGuiId;
    }

    public void setRemoveGuiId(String aRemoveGuiId) {
        iRemoveGuiId = aRemoveGuiId;
    }

    public void modifyRowInLst() {
        logger.debug("iNumRowToModify : " + iNumRowToModify);
        logger.debug("iFieldsToModify : " + iFieldsToModify);

        // Contrôle si la dernière valeur est vide, si c'est le cas le string
        // termine par ";"
        if (iFieldsToModify.charAt(iFieldsToModify.length() - 1) == ';') {
            iFieldsToModify = iFieldsToModify + " ";
        }

        // faire la séparation des split
        String[] fields = iFieldsToModify.split(";");

        // Vérification que chaque paramètre dispose d'une valeur
        if (fields.length % 2 != 0) {
            logger.error("nombre de variable et des paramètres doivent être pair");
            return;
        }
        // Recherche du vo
        IValueObject vo = iList.get(Integer.parseInt(iNumRowToModify));

        // Parcourir tous les paramètres
        for (int i = 0; i < fields.length; i = i + 2) {
            if ("".equals(fields[i + 1]) || " ".equals(fields[i + 1])) {
                // Supprimer le champ si il est null
                vo.removeProperty(fields[i]);
            } else {
                // modifier les valeurs des champs selon leur type (int, long
                // etc..)
                vo.setProperty(fields[i], vo.getPropertyType(fields[i]).getValueOf(fields[i + 1]));
            }

        }

    }

    public String getFieldsToModify() {
        return iFieldsToModify;
    }

    public void setFieldsToModify(String aFieldsToModify) {
        iFieldsToModify = aFieldsToModify;
    }

    public String getNumRowToModify() {
        return iNumRowToModify;
    }

    public void setNumRowToModify(String aNumRowToModify) {
        iNumRowToModify = aNumRowToModify;
    }

    @Override
    public IListHandler createListHandler(String aType) {
        return new GenericListHandler(aType);
    }

}
