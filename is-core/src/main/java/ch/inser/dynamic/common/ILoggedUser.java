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

package ch.inser.dynamic.common;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ch.inser.dynamic.common.DynamicDAO.Mode;

import jakarta.json.JsonObject;

/**
 * Interface pour les implémentations de LoggedUser
 *
 * @author INSER SA
 *
 */
public interface ILoggedUser {

    /**
     * Statut de l'utilisateur
     *
     * @author INSER SA
     *
     */
    public enum Status {
        /**
         * Utilistaur valide.
         */
        VALID,
        /**
         * Utilisateur non valide (existant, mais faux mot de passe).
         */
        INVALID,
        /**
         * Utilisateur bloqué (existant, mais bloqué).
         */
        BLOCKED,
        /**
         * Utilisateur inconnu.
         */
        UNKNOWN,
        /**
         * Utilisateur résilié
         */
        TERMINATED,
        /**
         * Utilisateur résilié
         */
        UNAUTHORIZED,
        /**
         * Utilisateur avec un password initial à changer
         */
        INITIAL_LOGON,
        /**
         * Erreur du système durant le logon
         */
        ERROR
    }

    /**
     * Méthode utilitaire permettant d'avoir une représentation textuelle d'un status. Surtout utilisé pour le debug.
     *
     * @return La representation textuelle de l'état de l'utilisateur.
     */
    public abstract String printStatus();

    /**
     * Retourne l'état de l'utilisateur.
     *
     * @return L'état de l'utilisateur.
     */
    public abstract Status getStatus();

    /**
     * Configure l'état de l'utilisateur.
     *
     * @param aStatus
     *            L'état de l'utilisateur.
     */
    public abstract void setStatus(Status aStatus);

    /**
     * Retourne la localisation (langue, ...) de l'utilisateur.
     *
     * @return La localisation de l'utilisateur.
     */
    public abstract Locale getLocale();

    /**
     * Information sur la langue de l'application en cours
     *
     * @return langue en String exemple fr,de,en etc..
     */
    public String getLocalLanguage();

    /**
     * Configure la localisation (langue, ...) de l'utilisateur.
     *
     * @param aLocale
     *            La localisation de l'utilisateur.
     */
    public abstract void setLocale(Locale aLocale);

    /**
     * Retourne le nom de l'utilisateur.
     *
     * @return Le nom de l'utilisateur.
     */
    public abstract String getUsername();

    /**
     * Enregistre le nom de l'utilisateur.
     *
     * @param aName
     *            Le nom de l'utilisateur.
     */
    public abstract void setUsername(String aName);

    /**
     * Retourne l'id de l'utilisateur.
     *
     * @return L'id de l'utlisateur.
     */
    public abstract Object getUserId();

    /**
     * Enregistre l'id de l'utilisateur.
     *
     * @param aId
     *            L'id de l'utilisateur.
     */
    public abstract void setUserId(Object aId);

    /**
     * Retourne le login de l'utilisateur.
     *
     * @return Le login.
     */
    public abstract String getLogin();

    /**
     * Enregistre le login de l'utilisateur.
     *
     * @param aLogin
     *            Le login.
     */
    public abstract void setLogin(String aLogin);

    public abstract Object getUserUpdateName();

    /**
     * Retourne une clause additionnelle pour filtrer les enregistrements
     *
     * @param objectName
     *            nom de l'objet
     * @return
     */
    public abstract String getAdditionnalClause(String objectName);

    /**
     * Retourne une clause additionnelle pour filtrer les enregistrements en fonction du mode
     *
     * @param objectName
     *            nom de l'objet
     * @param aMode
     *            mode
     * @return la requête supplémentaire
     */
    public abstract String getAdditionnalClause(String objectName, Mode aMode);

    /**
     * Code de propriété des enregistrements
     *
     * @return
     */
    public abstract Object getSecu();

    public abstract void setSecu(Object secu);

    /**
     * Méthode utilitaire pour indiquer s'il l'utilisateur est logué correctement.
     *
     * @return
     */
    public abstract boolean isLogged();

    /**
     * Méthode indiquant l'authorisation d'utiliser un menu, le nom du menu est défini au niveau de chaque application.
     *
     * @param menuName
     * @return
     */
    public abstract boolean isAuthMenu(String menuName);

    /**
     * Méthode indiquant l'authorisation d'utiliser une action, le nom du menu et de l'action sont définis au niveau de chaque application.
     *
     * @param menuName
     * @param actionName
     * @return
     */
    public abstract boolean isAuthAction(String menuName, String actionName);

    /**
     * Méthode indiquant l'authorisation de lire un champ, le nom de l'objet et du champ sont définis au niveau de chaque application.
     *
     * @param objectName
     * @param fieldName
     * @return
     */
    public abstract boolean isReadField(String objectName, String fieldName);

    /**
     * Retourne la liste des champs sans autorisation de lecture
     *
     * @param objectName
     * @param fieldName
     * @return
     */
    public abstract List<String> getNoReadFields(String objectName);

    /**
     * Méthode indiquant l'authorisation d'écrire un champ, le nom de l'objet et du champ sont définis au niveau de chaque application.
     *
     * @param objectName
     * @param fieldName
     * @return
     */
    public abstract boolean isWriteField(String objectName, String fieldName);

    /**
     * Retourne la liste des champs sans autorisation d'écriture
     *
     * @param objectName
     * @param fieldName
     * @return
     */
    public abstract List<String> getNoWriteFields(String objectName);

    /**
     * Retourne la page par défaut lors du login de l'utilisateur
     *
     * @return the defaultPage
     */
    public abstract String getDefaultHomePage();

    /**
     * Insère la nouvelle page par défaut de l'utilisateur
     *
     * @param aDefaultPage
     *            the defaultPage to set
     */
    public abstract void setDefaultHomePage(String aDefaultHomePage);

    /**
     * @return the parametersHomePage
     */
    public abstract Map<String, Object> getParametersPage();

    /**
     * Retourne les paramètres selon la page, configuration spécialisée selon projet
     *
     * @return the parametersHomePage
     */
    public abstract Map<String, Object> getParametersPage(String aNamePage);

    /**
     * @param aParametersHomePage
     *            the parametersHomePage to set
     */
    public abstract void setParametersPage(Map<String, Object> aParametersPage);

    /**
     *
     * @return map de structure entity->field->1=write, 0=read
     */
    public Map<String, Map<String, Integer>> getMapAuthFields();

    /**
     *
     * @return map de structure menu->action->true/false
     */
    public Map<String, Map<String, Boolean>> getMapAuthAction();

    /**
     *
     * @param aMapAuthAction
     *            map de structure menu->action->true/false
     */
    public void setMapAuthAction(Map<String, Map<String, Boolean>> aMapAuthAction);

    /**
     *
     * @param aMapAuthFields
     *            map de structure entity->field->1=write,0=read
     */
    public void setMapAuthFields(Map<String, Map<String, Integer>> aMapAuthFields);

    /**
     *
     * @return map de droits aux menus
     */
    public Map<String, Boolean> getMapAuthMenu();

    /**
     *
     * @param aMapAuthMenu
     *            map de droits aux menus
     */
    public void setMapAuthMenu(Map<String, Boolean> aMapAuthMenu);

    /**
     * @return la liste des usergroup IDs
     */
    public Collection<Object> getUserGroupIds();

    /**
     * Réception de droit en format JSON (utilisation par PermResource REST)
     *
     * @return informations sur la sécurité
     */
    public JsonObject getPermissions();

    /**
     * Configurer la sécurité en format JSON (utilisation par PermResource REST)
     *
     * @param aPermissions
     *            données de sécurité
     */
    public void setPermissions(JsonObject aPermissions);

}