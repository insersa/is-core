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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.inser.dynamic.common.ILoggedUser;
import ch.inser.dynamic.common.IValueObject;
import ch.inser.jsl.exceptions.ISException;
import ch.inser.jsl.list.ListHandler;
import ch.inser.jsl.tools.VOTools;

public abstract class AbstractListHandler extends ch.inser.jsl.list.AbstractListHandler<IValueObject> implements IListHandler {
    @SuppressWarnings("unused")
    private static final Log logger = LogFactory.getLog(AbstractListHandler.class);

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -5303110272403740760L;

    /** Information sur les modficication à faire dans la liste */
    protected Map<Integer, String> iMapVoState = new HashMap<>();

    /** Liste des item à supprimer */
    protected List<Object> iLstRemoveField = new ArrayList<>();

    /** Nombre de résultat max pour les liste */
    protected Integer iMaxRowResult = null;

    /** Nom objet métier */
    private String iType;

    private IValueObject[] iSelectedVos;

    /** Information utilisateur */
    protected ILoggedUser iUser;

    /** Critères de recherche pour liste */
    private IValueObject iQuery;

    /** insérer depuis le GUI de l'id à supprimer */
    protected String iRemoveGuiId;

    /** liste des champs à modifier du côté serveur */
    protected String iFieldsToModify;

    /** Numéro de ligne ou il y a eu la modification cliente */
    protected String iNumRowToModify;

    /**
     * Constructeur pour liste handler
     *
     * @param type
     *            nom de l'objet métier
     * @param vo
     *            Contient les critères de recherche pour la liste
     * @param aMaxRowResult
     *            Nombre maximum de résultat pour la liste
     * @param user
     *            Information sur l'utilisateur
     * @throws ISException
     */
    protected AbstractListHandler(String type, IValueObject vo, Integer aMaxRowResult, ILoggedUser user) throws ISException {
        init();

        // Récupère les arguments du contructeur
        iType = type;
        iQuery = vo;
        iUser = user;
        iMaxRowResult = aMaxRowResult;

        // Lit les données correspondant à la requête.
        refresh(1);
    }

    /**
     * Constructeur pour liste handler
     *
     * @param type
     * @param vo
     * @param user
     * @throws ISException
     */
    protected AbstractListHandler(String type, IValueObject vo, ILoggedUser user) throws ISException {
        init();

        // Récupère les arguments du contructeur
        iType = type;
        iQuery = vo;
        iUser = user;

        // Lit les données correspondant à la requête.
        refresh(1);
    }

    /**
     * Constructeur pour liste handler
     *
     * @param type
     * @param user
     */
    protected AbstractListHandler(String type, ILoggedUser user) {
        init();

        // Récupère les arguments du contructeur
        iType = type;
        iUser = user;
        // Création d'une liste vide
        iList = new ArrayList<>();
    }

    /**
     * Constructeur pour liste handler
     *
     * @param type
     */
    protected AbstractListHandler(String type) {
        init();
        // Récupère les arguments du contructeur
        iType = type;
        // Création d'une liste vide
        iList = new ArrayList<>();
    }

    public void init() {
        // Valeurs par défaut
        iSortIdx = 1;
        iSortOrientation = ListHandler.Sort.ASCENDING;
    }

    public String getType() {
        return iType;
    }

    @Override
    public void setMaxRowResult(Integer aMaxRowResult) {
        iMaxRowResult = aMaxRowResult;
    }

    @Override
    public void addAll(Collection<? extends IValueObject> aCol) {
        if (iList != null && aCol != null) {
            iList.addAll(aCol);
            iListSize = iList.size();
        }
    }

    @Override
    public void addVO(IValueObject aVo) {
        if (iList != null && aVo != null) {
            iList.add(aVo);
            iListSize = iList.size();
        }
    }

    @Override
    public List<Integer> getLstIndex(String aFieldname, Object aValue) {
        List<Integer> lstIndex = new ArrayList<>();
        if (aFieldname == null) {
            return lstIndex;
        }
        for (int index = 0; index < iList.size(); index++) {
            if (iList != null && iList.get(index) != null && iList.get(index).getProperty(aFieldname) != null
                    && iList.get(index).getProperty(aFieldname).equals(aValue)) {
                lstIndex.add(index);
            }
        }
        return lstIndex;
    }

    @Override
    public List<IValueObject> getLstVo(String aFieldname, Object aValue) {
        List<IValueObject> lstVo = new ArrayList<>();
        if (aFieldname == null) {
            return lstVo;
        }
        for (int index = 0; index < iList.size(); index++) {
            if (iList != null && iList.get(index) != null && iList.get(index).getProperty(aFieldname) != null
                    && iList.get(index).getProperty(aFieldname).equals(aValue)) {
                lstVo.add(iList.get(index));
            }
        }
        return lstVo;
    }

    @Override
    public IValueObject removeIndex(int anIndex) {
        if (anIndex >= iList.size()) {
            return null;
        }
        if (getRecordIndexInList() <= anIndex) {
            setRecordIndexInList(getRecordIndexInList() - 1);
        }
        iListSize--;
        Map<Integer, String> mapTampon = new HashMap<>();

        for (Map.Entry<Integer, String> val : iMapVoState.entrySet()) {
            if (val.getKey() > anIndex) {
                mapTampon.put(val.getKey() - 1, val.getValue());
            } else if (val.getKey() < anIndex) {
                mapTampon.put(val.getKey(), val.getValue());
            }
        }
        iMapVoState = mapTampon;
        return iList.remove(anIndex);
    }

    @Override
    public Boolean removeVO(Object aId) {
        int index = 0;
        boolean exist = false;
        if (aId != null) {
            for (index = 0; index < iList.size() && !exist; index++) {
                if (iList.get(index).getId() != null && iList.get(index).getId().equals(aId)) {
                    exist = true;
                    index--;
                }
            }
        }
        if (exist) {
            // Suppression du champ
            iLstRemoveField.add(aId);
            iList.remove(index);
            iListSize--;
            Map<Integer, String> mapTampon = new HashMap<>();

            for (Map.Entry<Integer, String> val : iMapVoState.entrySet()) {
                if (val.getKey() > index) {
                    mapTampon.put(val.getKey() - 1, val.getValue());
                }
            }
            iMapVoState = mapTampon;
            return true;
        }

        return false;
    }

    @Override
    public List<Object> getLstRemoveField() {
        return iLstRemoveField;
    }

    @Override
    public Map<Integer, String> getMapVoState() {
        return iMapVoState;
    }

    @Override
    public void setProperties(String aName, Object aValue) {
        VOTools.setProperties(aName, aValue, getList());
    }

    @Override
    public Collection<Object> getProperties(String aName) {
        return VOTools.getProperties(aName, getList());
    }

    @Override
    public boolean contains(String[] aNames, Object[] values, boolean[] equals) {
        for (IValueObject vo : getList()) {
            boolean partial = true;
            int i = 0;
            while (i < aNames.length && partial) {
                partial = values[i].equals(vo.getProperty(aNames[i])) == equals[i];
                i++;
            }
            if (partial) {
                // On en a trouvé un qui respecte tout!
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean contains(String[] aNames, Object[] values) {
        for (IValueObject vo : getList()) {
            boolean partial = true;
            int i = 0;
            while (i < aNames.length && partial) {
                partial = values[i].equals(vo.getProperty(aNames[i]));
                i++;
            }
            if (partial) {
                // On en a trouvé un qui respecte tout!
                return true;
            }
        }
        return false;
    }

    @Override
    public IValueObject[] getSelectedVos() {
        return iSelectedVos;
    }

    @Override
    public void setSelectedVos(IValueObject[] aSelectedVos) {
        iSelectedVos = aSelectedVos;
    }

    @Override
    public IListHandler getFilteredSubList(String[] aNames, Object[] values) {
        IListHandler subList = createListHandler(getType());
        for (IValueObject vo : getList()) {
            boolean partial = true;
            int i = 0;
            while (i < aNames.length && partial) {
                partial = values[i] == null && vo.getProperty(aNames[i]) == null
                        || values[i] != null && values[i].equals(vo.getProperty(aNames[i]));
                i++;
            }
            if (partial) {
                subList.addVO(vo);
            }
        }
        return subList;
    }

    @Override
    public IListHandler getFilteredSubList(String aName, Object value) {
        IListHandler subList = createListHandler(getType());
        for (IValueObject vo : getList()) {
            if (value == null && vo.getProperty(aName) == null) {
                subList.addVO(vo);
            } else {
                if (value != null && value.equals(vo.getProperty(aName))) {
                    subList.addVO(vo);
                }
            }
        }
        return subList;
    }

    @Override
    public void setQuery(IValueObject aQuery) {
        iQuery = aQuery;
    }

    @Override
    public IValueObject getQuery() {
        return iQuery;
    }

}
