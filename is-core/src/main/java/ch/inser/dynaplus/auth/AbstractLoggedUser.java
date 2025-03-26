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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * AbstractLoggedUser propre à dynaplus
 *
 * @author INSER SA
 *
 */
public abstract class AbstractLoggedUser extends ch.inser.dynamic.common.AbstractLoggedUser {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -4947452554435081747L;

    /** Définition de la catégorie de logging */
    @SuppressWarnings("unused")
    private static final Log logger = LogFactory.getLog(AbstractLoggedUser.class);

    /**
     * Liste des groupes utilisateurs, l'appartenance à un groupe donne accès aux actions ou menus de l'application selon configuration
     */
    protected List<IUserGroup> iUserGroups = new ArrayList<>();

    /**
     * Constructeur par défaut, pour extension de la construction de la mère.
     */
    protected AbstractLoggedUser() {
        super(Long.valueOf(0L), "anonymous");
    }

    /**
     * Constructeur d'un utilisateur.
     *
     * @param aId
     *            L'id de l'utilisateur.
     * @param aLogin
     *            Le login de l'utilisateur.
     */
    protected AbstractLoggedUser(Object aId, String aLogin) {
        super(aId, aLogin);
    }

    /**
     * Ajout un groupe d'utilisateur
     *
     * @param group
     *            Usergroup à ajouter
     */
    public void addUserGroup(IUserGroup group) {
        iUserGroups.add(group);
    }

    /**
     *
     * @return la liste des usergroup IDs
     */
    @Override
    public Collection<Object> getUserGroupIds() {
        List<Object> userGroupIds = new ArrayList<>();

        for (IUserGroup ugr : iUserGroups) {
            userGroupIds.add(ugr.getId());
        }

        return userGroupIds;
    }

}
