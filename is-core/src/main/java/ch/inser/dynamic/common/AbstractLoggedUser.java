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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ch.inser.dynamic.common.DynamicDAO.Mode;

import jakarta.json.JsonObject;

/**
 * Classe stockant toutes les informations nécessaires au traitement de la session pour un utilisateur, c'est-à-dire identification,
 * sécurité, etc.
 * <p>
 * Cet objet implémente <code>HttpSessionBindingListener</code> pour être notifié lorsque l'objet est lié/délié à une session. Ceci permet
 * de maintenir une liste des utilisateurs connectés à tout moment.
 *
 * @author INSER SA
 * @version 1.0
 */
public abstract class AbstractLoggedUser implements ILoggedUser, Serializable {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -7538433128091265579L;

    private Object iSecu;

    /**
     * Le statut de l'utilistaur.
     */
    private Status iStatus = Status.ERROR;

    /**
     * La langue de l'utilisateur.
     */
    private Locale iLocale;

    /**
     * Le nom d'utilisateur.
     */
    private String iLogin = null;

    /**
     * Le nom d'utilisateur.
     */
    private String iUsername = null;

    /**
     * Le prénom de l'utilisateur
     */
    private String iPrenom = null;

    /**
     * Le prénom de l'utilisateur
     */
    private String iNom = null;

    /**
     * Le ID de l'utilisateur.
     */
    private Object iUserId;

    /**
     * Page par défaut selon utilisateur
     */
    protected String iDefaultHomePage = "";

    /**
     * Page courant du BB
     */
    protected String iCurrentPage = "";

    /**
     * Paramètres à inserer en plus comme paramètre initial.
     */
    protected Map<String, Object> iParametersPage = new HashMap<>();

    /**
     * Les droits d'acces aux actions (create, delete, etc.) selon structure: menu->action->true/false. Utilisé par JSF. Example:
     * rendered="#{LoggedUser.MapAuthAction.Entreprise.CREATE}"
     */
    private Map<String, Map<String, Boolean>> iMapAuthAction = new HashMap<>();

    /**
     * Les droits d'acces aux champs (read, write) selon structure: entity->field->1=write,0=read. Utilisé par PermResource REST. Example:
     */
    private Map<String, Map<String, Integer>> iMapAuthFields = new HashMap<>();

    /**
     * Les droits d'acces aux menus Utilisé par JSF. Example: rendered="#{LoggedUser.MapAuthMenu.Competence}"
     */
    private Map<String, Boolean> iMapAuthMenu = new HashMap<>();

    /**
     * Droits d'accès en format JSON
     */
    private transient JsonObject iPermissions;

    /**
     * Constructeur d'un utilisateur.
     *
     * @param aId
     *            L'id de l'utilisateur.
     * @param aLogin
     *            Le login de l'utilisateur.
     */
    protected AbstractLoggedUser(Object aId, String aLogin) {

        iUserId = aId;
        iLogin = aLogin;
    }

    /**
     * Méthode utilitaire permettant d'avoir une représentation textuelle d'un status. Surtout utilisé pour le debug.
     *
     * @param aStatus
     *            L'état de l'utilisateur.
     *
     * @return La representation textuelle de l'état de l'utilisateur.
     */
    public static String printStatus(Status aStatus) {

        switch (aStatus) {
            case VALID:
                return "valid";
            case INVALID:
                return "invalid";
            case BLOCKED:
                return "blocked";
            case UNKNOWN:
                return "unknown";
            case TERMINATED:
                return "terminated";
            case UNAUTHORIZED:
                return "unauthorized";
            case INITIAL_LOGON:
                return "initial logon";
            case ERROR:
                return "error";
            default:
                return "(unknown status)";
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see ch.inser.dynamic.common.ILoggedUser#printStatus()
     */
    @Override
    public String printStatus() {

        return printStatus(iStatus);
    }

    /*
     * (non-Javadoc)
     *
     * @see ch.inser.dynamic.common.ILoggedUser#getStatus()
     */
    @Override
    public Status getStatus() {

        return iStatus;
    }

    /*
     * (non-Javadoc)
     *
     * @see ch.inser.dynamic.common.ILoggedUser#setStatus(ch.inser.dynamic.common .AbstractLoggedUser.Status)
     */
    @Override
    public void setStatus(Status aStatus) {

        iStatus = aStatus;
    }

    /*
     * (non-Javadoc)
     *
     * @see ch.inser.dynamic.common.ILoggedUser#getLocale()
     */
    @Override
    public Locale getLocale() {

        return iLocale;
    }

    /*
     * (non-Javadoc)
     *
     * @see ch.inser.dynamic.common.ILoggedUser#getLocalLanguage()
     */
    @Override
    public String getLocalLanguage() {
        return iLocale.getLanguage();
    }

    /*
     * (non-Javadoc)
     *
     * @see ch.inser.dynamic.common.ILoggedUser#setLocale(java.util.Locale)
     */
    @Override
    public void setLocale(Locale aLocale) {

        iLocale = aLocale;
    }

    /*
     * (non-Javadoc)
     *
     * @see ch.inser.dynamic.common.ILoggedUser#getUsername()
     */
    @Override
    public String getUsername() {

        return iUsername;
    }

    /*
     * (non-Javadoc)
     *
     * @see ch.inser.dynamic.common.ILoggedUser#setUsername(java.lang.String)
     */
    @Override
    public void setUsername(String aName) {

        iUsername = aName;
    }

    /*
     * (non-Javadoc)
     *
     * @see ch.inser.dynamic.common.ILoggedUser#getUserId()
     */
    @Override
    public Object getUserId() {

        return iUserId;
    }

    /*
     * (non-Javadoc)
     *
     * @see ch.inser.dynamic.common.ILoggedUser#setUserId(java.lang.Object)
     */
    @Override
    public void setUserId(Object aId) {

        iUserId = aId;
    }

    /*
     * (non-Javadoc)
     *
     * @see ch.inser.dynamic.common.ILoggedUser#getLogin()
     */
    @Override
    public String getLogin() {
        return iLogin;
    }

    /*
     * (non-Javadoc)
     *
     * @see ch.inser.dynamic.common.ILoggedUser#setLogin(java.lang.String)
     */
    @Override
    public void setLogin(String aLogin) {
        iLogin = aLogin;
    }

    /*
     * (non-Javadoc)
     *
     * @see ch.inser.dynamic.common.ILoggedUser#getUserUpdateName()
     */
    @Override
    public Object getUserUpdateName() {

        return Long.valueOf(-1L);
    }

    /*
     * (non-Javadoc)
     *
     * @see ch.inser.dynamic.common.ILoggedUser#getAdditionnalClause(java.lang.String )
     */
    @Override
    public String getAdditionnalClause(String objectName) {
        return null;
    }

    @Override
    public String getAdditionnalClause(String objectName, Mode aMode) {
        return getAdditionnalClause(objectName);
    }

    /*
     * (non-Javadoc)
     *
     * @see ch.inser.dynamic.common.ILoggedUser#getSecu()
     */
    @Override
    public Object getSecu() {
        return iSecu;
    }

    /*
     * (non-Javadoc)
     *
     * @see ch.inser.dynamic.common.ILoggedUser#setSecu(java.lang.Object)
     */
    @Override
    public void setSecu(Object secu) {
        if (secu == null) {
            return;
        }
        iSecu = secu;
    }

    /*
     * (non-Javadoc)
     *
     * @see ch.inser.dynamic.common.ILoggedUser#isLogged()
     */
    @Override
    public boolean isLogged() {
        return getStatus() == Status.VALID || getStatus() == Status.INITIAL_LOGON;
    }

    /*
     * (non-Javadoc)
     *
     * @see ch.inser.dynamic.common.ILoggedUser#isAuthMenu(java.lang.String)
     */
    @Override
    public boolean isAuthMenu(String menuName) {
        throw new java.lang.UnsupportedOperationException("Method isAuthMenu(String menuName) is not supported");
    }

    /*
     * (non-Javadoc)
     *
     * @see ch.inser.dynamic.common.ILoggedUser#isAuthAction(java.lang.String, java.lang.String)
     */
    @Override
    public boolean isAuthAction(String menuName, String actionName) {
        throw new java.lang.UnsupportedOperationException("Method isAuthAction(String menuName, String actionName) is not supported");
    }

    /*
     * (non-Javadoc)
     *
     * @see ch.inser.dynamic.common.ILoggedUser#isReadField(java.lang.String, java.lang.String)
     */
    @Override
    public boolean isReadField(String objectName, String fieldName) {
        throw new java.lang.UnsupportedOperationException("Method isReadField(String objectName, String fieldName) is not supported");
    }

    @Override
    public List<String> getNoReadFields(String aObjectName) {
        return new ArrayList<>();
    }

    /*
     * (non-Javadoc)
     *
     * @see ch.inser.dynamic.common.ILoggedUser#isWriteField(java.lang.String, java.lang.String)
     */
    @Override
    public boolean isWriteField(String objectName, String fieldName) {
        throw new java.lang.UnsupportedOperationException("Method isWriteField(String objectName, String fieldName) is not supported");
    }

    @Override
    public List<String> getNoWriteFields(String aObjectName) {
        return new ArrayList<>();
    }

    /*
     * (non-Javadoc)
     *
     * @see ch.inser.dynamic.common.ILoggedUser#getDefaultHomePage()
     */
    @Override
    public String getDefaultHomePage() {
        return iDefaultHomePage;
    }

    /*
     * (non-Javadoc)
     *
     * @see ch.inser.dynamic.common.ILoggedUser#setDefaultHomePage(java.lang.String)
     */
    @Override
    public void setDefaultHomePage(String aDefaultHomePage) {
        iDefaultHomePage = aDefaultHomePage;
    }

    /*
     * (non-Javadoc)
     *
     * @see ch.inser.dynamic.common.ILoggedUser#getParametersPage()
     */
    @Override
    public Map<String, Object> getParametersPage() {
        return iParametersPage;
    }

    /*
     * (non-Javadoc)
     *
     * @see ch.inser.dynamic.common.ILoggedUser#getParametersPage(java.lang.String)
     */
    @Override
    public Map<String, Object> getParametersPage(String aNamePage) {
        iCurrentPage = aNamePage;
        return getParametersPage();
    }

    /*
     * (non-Javadoc)
     *
     * @see ch.inser.dynamic.common.ILoggedUser#setParametersPage(java.util.Map)
     */
    @Override
    public void setParametersPage(Map<String, Object> aParametersPage) {
        iParametersPage = aParametersPage;
    }

    @Override
    public Map<String, Map<String, Boolean>> getMapAuthAction() {
        return iMapAuthAction;
    }

    @Override
    public Map<String, Map<String, Integer>> getMapAuthFields() {
        return iMapAuthFields;
    }

    @Override
    public void setMapAuthAction(Map<String, Map<String, Boolean>> aMapAuthAction) {
        iMapAuthAction = aMapAuthAction;
    }

    @Override
    public void setMapAuthFields(Map<String, Map<String, Integer>> aMapAuthFields) {
        iMapAuthFields = aMapAuthFields;
    }

    @Override
    public Map<String, Boolean> getMapAuthMenu() {
        return iMapAuthMenu;
    }

    @Override
    public void setMapAuthMenu(Map<String, Boolean> aMapAuthMenu) {
        iMapAuthMenu = aMapAuthMenu;
    }

    /*
     * (non-Javadoc)
     *
     * @see ch.inser.dynamic.common.ILoggedUser#getUserGroupIds()
     */
    @Override
    public Collection<Object> getUserGroupIds() {
        throw new java.lang.UnsupportedOperationException("Method getUserGroupIds() is not supported");
    }

    /*
     * (non-Javadoc)
     *
     * @see ch.inser.dynamic.common.ILoggedUser#getPermissions()
     */
    @Override
    public JsonObject getPermissions() {
        return iPermissions;
    }

    /*
     * (non-Javadoc)
     *
     * @see ch.inser.dynamic.common.ILoggedUser#setPermissions(org.json.JSONObject)
     */
    @Override
    public void setPermissions(JsonObject aPermissions) {
        iPermissions = aPermissions;
    }

    /**
     *
     * @return prénom
     */
    public String getPrenom() {
        return iPrenom;
    }

    /**
     *
     * @param aPrenom
     *            prénom
     */
    public void setPrenom(String aPrenom) {
        iPrenom = aPrenom;
    }

    /**
     *
     * @return nom de famille
     */
    public String getNom() {
        return iNom;
    }

    /**
     *
     * @param aNom
     *            nom de famille
     */
    public void setNom(String aNom) {
        iNom = aNom;
    }

}