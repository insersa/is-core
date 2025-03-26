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

import java.util.Map;

import ch.inser.dynaplus.vo.IVOFactory;

/**
 * Représentation du UserGroup dans la sécurité applicative. Un utilisateur peut être lié à un ou plusieurs UserGroup, ce sont les UserGroup
 * qui sont responsables de définir les autorisations. Les droits sont additifs sure les UesrGroup.
 *
 * @author INSER SA
 *
 */
public interface IUserGroup {

    /**
     * @return l'identifiant du UserGroup dans la base de données
     */
    public Object getId();

    /**
     * Méthode indiquant l'autorisation d'utiliser un menu, le nom du menu est défini au niveau de chaque application.
     *
     * @param menuName
     *            clé métier du menu
     * @return autorisation
     */
    public boolean isAuthMenu(String menuName);

    /**
     * To set an authorization on a menu
     *
     * @param menuName
     *            clé métier du menu
     * @param right
     *            autorisation
     */
    public void setAuthMenu(String menuName, boolean right);

    /**
     *
     * @return map for menus with key Menu
     */
    public Map<String, Boolean> getAuthMenus();

    /**
     * Clone des autorisations pour les menus
     *
     * @return Map menu + accès (true et false)
     */
    public Map<String, Boolean> cloneAuthMenus();

    /**
     * Clone des autorisations pour les champs
     *
     * @return Map champs + accès (1=write, 0=read)
     */
    public Map<String, Map<String, Integer>> cloneAuthFields();

    /**
     * Méthode indiquant l'autorisation d'utiliser une action, le nom du menu et de l'action sont définis au niveau de chaque application.
     *
     * @param menuName
     *            clé métier du menu
     * @param actionName
     *            clé métier de l'action
     * @return autorisation
     */
    public boolean isAuthAction(String menuName, String actionName);

    /**
     * To set an authorization on an action
     *
     * @param menuName
     *            clé métier du menu
     * @param actionName
     *            clé métier de l'action
     * @param right
     *            autorisation
     */
    public void setAuthAction(String menuName, String actionName, boolean right);

    /**
     * @return map for actions with key Menu->Action
     */
    public Map<String, Map<String, Boolean>> getAuthActions();

    /**
     * Clone des autorisations pour les liens actions et les menus
     *
     * @return la map correspondance entre nom des menus et des actions
     */
    public Map<String, Map<String, Boolean>> cloneAuthActions();

    /**
     * Méthode indiquant l'autorisation de lire un champ, le nom de l'objet et du champ sont définis au niveau de chaque application.
     *
     * @param objectName
     *            nom de l'objet métier
     * @param fieldName
     *            nom du champ
     * @return autorisation de lecture
     */
    public boolean isReadField(String objectName, String fieldName);

    /**
     * Méthode indiquant l'autorisation d'écrire un champ, le nom de l'objet et du champ sont définis au niveau de chaque application.
     *
     * @param objectName
     *            nom de l'objet métier
     * @param fieldName
     *            nom du champ
     * @return autorisation d'écriture
     */
    public boolean isWriteField(String objectName, String fieldName);

    /**
     * To set an authorization on an fields
     *
     * @param objectName
     *            nom de l'objet métier
     * @param fieldName
     *            nom du champ
     * @param right
     *            o->readonly, 1->write/read
     */
    public void setAuthField(String objectName, String fieldName, int right);

    /**
     * Injection directe de dépendance
     *
     * @param vOFactory
     *            la factory mise à disposition
     */
    public void setVOFactory(IVOFactory vOFactory);
}
