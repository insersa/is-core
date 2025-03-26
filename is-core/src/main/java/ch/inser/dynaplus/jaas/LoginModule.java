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

package ch.inser.dynaplus.jaas;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Accès à la base pour vérifier le user/mot de passe
 *
 * @author INSER SA
 *
 */
public abstract class LoginModule implements javax.security.auth.spi.LoginModule {

    /**
     * Nom de l'application
     */
    private String iApp;

    /**
     * Nom du champs BD pour l'id du groupe de l'utilisateur
     */
    protected String iUserGroupId;

    /**
     * Constructeur de LoginModule pour le login
     *
     * @param aApp
     *            Nom de l'application
     * @param aUserGroupId
     *            Nom du champs BD pour l'id du groupe de l'utilisateur
     */
    protected LoginModule(String aApp, String aUserGroupId) {
        iApp = aApp;
        iUserGroupId = aUserGroupId;
    }

    /**
     * Récuperation de login et mot de passe
     */
    private CallbackHandler iHandler;

    /**
     * Rôles Jaas
     */
    private Subject iSubject;

    /**
     * User Jaas
     */
    private UserPrincipal iUserPrincipal;

    /**
     * Rôle Jaas
     */
    private RolePrincipal iRolePrincipal;

    /**
     * Login
     */
    protected String iLogin;

    /**
     * Rôles EBI
     */
    protected List<String> iUserGroups;

    /** Nombre de champs saisis pour l'authentification */
    private static final int NBR_AUTH_FIELDS = 2;

    /**
     * Logger
     */
    private static final Log logger = LogFactory.getLog(LoginModule.class);

    @Override
    public void initialize(Subject aSubject, CallbackHandler aCallbackHandler, Map<String, ?> aSharedState, Map<String, ?> aOptions) {

        iHandler = aCallbackHandler;
        iSubject = aSubject;
    }

    @Override
    public boolean login() throws LoginException {

        Callback[] callbacks = new Callback[NBR_AUTH_FIELDS];
        callbacks[0] = new NameCallback("login");
        callbacks[1] = new PasswordCallback("password", true);

        try {
            iHandler.handle(callbacks);
            String name = ((NameCallback) callbacks[0]).getName();
            String password = String.valueOf(((PasswordCallback) callbacks[1]).getPassword());

            return isValidLogin(name, password);

        } catch (IOException | UnsupportedCallbackException e) {
            logger.error("Erreur de login", e);
            throw new LoginException(e.getMessage());
        }

    }

    /**
     * Vérifie que le login et mot de passe sont valides
     *
     * @param aName
     *            login
     * @param aPassword
     *            mot de passe
     * @return true si le login et mot de passe sont valides
     * @throws LoginException
     *             erreur de vérification
     */
    protected abstract boolean isValidLogin(String aName, String aPassword) throws LoginException;

    @Override
    public boolean commit() throws LoginException {

        iUserPrincipal = new UserPrincipal(iLogin);
        iSubject.getPrincipals().add(iUserPrincipal);

        if (iUserGroups != null && !iUserGroups.isEmpty()) {
            for (String groupName : iUserGroups) {
                iRolePrincipal = new RolePrincipal(groupName);
                iSubject.getPrincipals().add(iRolePrincipal);
            }
        }
        // Accès à l'appli
        iRolePrincipal = new RolePrincipal(iApp);
        iSubject.getPrincipals().add(iRolePrincipal);

        return true;
    }

    @Override
    public boolean abort() throws LoginException {
        return false;
    }

    @Override
    public boolean logout() throws LoginException {
        iSubject.getPrincipals().remove(iUserPrincipal);
        iSubject.getPrincipals().remove(iRolePrincipal);
        return true;
    }

}
