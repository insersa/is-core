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

package ch.inser.dynaplus.auth;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import ch.inser.dynaplus.vo.IVOFactory;

/**
 * Representation of the authorization for a Group
 *
 * @author INSER SA
 *
 */
public abstract class AbstractUserGroup implements IUserGroup, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 8857693498254442964L;

    /**
     * Identifiant du usergroup dans la base de données.
     */
    private Object iId;

    /**
     * Contain the authorization for action with key Menu->Action
     */
    protected Map<String, Map<String, Boolean>> iAuthActions = new HashMap<>();

    /**
     * Contain the authorization for menu with key Menu
     */
    protected Map<String, Boolean> iAuthMenus = new HashMap<>();

    /**
     * Contain the authorization for field 0=readonly, 1=read/write
     */
    protected Map<String, Map<String, Integer>> iAuthFields = new HashMap<>();

    /**
     * VOFactory à disposition de l'objet métier
     */
    private IVOFactory iVOFactory;

    /**
     * Constructeur d'un usergroup par sont identifiant base de données
     *
     * @param anId
     *            identifiant dans la base de données.
     */
    protected AbstractUserGroup(Object anId) {
        iId = anId;
    }

    @Override
    public boolean isAuthAction(String menuName, String actionName) {
        return iAuthActions.get(menuName) != null && iAuthActions.get(menuName).get(actionName) != null
                && iAuthActions.get(menuName).get(actionName);
    }

    @Override
    public void setAuthAction(String menuName, String actionName, boolean right) {
        Map<String, Boolean> menu = iAuthActions.get(menuName);
        if (menu == null) {
            menu = new HashMap<>();
            iAuthActions.put(menuName, menu);
        }
        menu.put(actionName, right);
    }

    /**
     *
     * @return map for actions with key Menu->Action
     */

    @Override
    public Map<String, Map<String, Boolean>> getAuthActions() {
        return iAuthActions;
    }

    @Override
    public Map<String, Map<String, Boolean>> cloneAuthActions() {
        // Ajouter les droits sur les actions
        Map<String, Map<String, Boolean>> actionAuthMap = new HashMap<>();
        for (String menu : getAuthActions().keySet()) {
            for (String action : getAuthActions().get(menu).keySet()) {
                if (actionAuthMap.get(menu) == null) {
                    actionAuthMap.put(menu, new HashMap<>());
                }
                if (actionAuthMap.get(menu).get(action) == null || !actionAuthMap.get(menu).get(action)) {
                    actionAuthMap.get(menu).put(action, getAuthActions().get(menu).get(action));
                }
            }
        }

        return actionAuthMap;

    }

    @Override
    public boolean isAuthMenu(String menuName) {
        return iAuthMenus.get(menuName) != null && iAuthMenus.get(menuName);
    }

    @Override
    public void setAuthMenu(String menuName, boolean right) {
        iAuthMenus.put(menuName, right);
    }

    @Override
    public Map<String, Boolean> getAuthMenus() {
        return iAuthMenus;
    }

    @Override
    public Map<String, Boolean> cloneAuthMenus() {
        Map<String, Boolean> menuAuthMap = new HashMap<>();
        for (String menu : getAuthMenus().keySet()) {
            if (menuAuthMap.get(menu) == null || !menuAuthMap.get(menu)) {
                menuAuthMap.put(menu, getAuthMenus().get(menu));
            }
        }

        return menuAuthMap;
    }

    @Override
    public boolean isReadField(String objectName, String fieldName) {
        return iAuthFields.get(objectName) != null && iAuthFields.get(objectName).get(fieldName) != null;
    }

    @Override
    public boolean isWriteField(String objectName, String fieldName) {
        Integer i = null;
        if (iAuthFields.get(objectName) != null) {
            i = iAuthFields.get(objectName).get(fieldName);
        }
        return i != null && i > 0;
    }

    @Override
    public void setAuthField(String objectName, String fieldName, int right) {
        Map<String, Integer> object = iAuthFields.get(objectName);
        if (object == null) {
            object = new HashMap<>();
            iAuthFields.put(objectName, object);
        }
        object.put(fieldName, right);
    }

    @Override
    public Map<String, Map<String, Integer>> cloneAuthFields() {
        // Ajouter les droits sur les champs
        Map<String, Map<String, Integer>> fieldsAuthMap = new HashMap<>();
        for (String entity : iAuthFields.keySet()) {
            fieldsAuthMap.put(entity, new HashMap<>());
            for (String field : iAuthFields.get(entity).keySet()) {
                fieldsAuthMap.get(entity).put(field, iAuthFields.get(entity).get(field));
            }
        }
        return fieldsAuthMap;
    }

    @Override
    public Object getId() {
        return iId;
    }

    @Override
    public void setVOFactory(IVOFactory aVOFactory) {
        iVOFactory = aVOFactory;
    }

    /**
     * Mise à disposition du VOFactory pour les classes filles
     *
     * @return VOFactory
     */
    protected IVOFactory getVOFactory() {
        return iVOFactory;
    }

}
