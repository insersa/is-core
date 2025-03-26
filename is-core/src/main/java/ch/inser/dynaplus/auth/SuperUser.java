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

import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.inser.dynamic.common.AbstractLoggedUser;

/**
 * Utilisateur ayant tous les droits
 *
 * @author INSER SA
 */

public class SuperUser extends AbstractLoggedUser {

    /**
     * UID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Définition de la catégorie de logging
     */
    private static final Log logger = LogFactory.getLog(SuperUser.class);

    private boolean iUpdateUsername = true;

    /**
     * Default constructor.
     */
    public SuperUser() {
        this(true);
    }

    /**
     * Constructor configuring the <code>getUserUpdateName()</code>.
     *
     * @param aUpdateUsername
     */
    public SuperUser(boolean aUpdateUsername) {
        this("superuser", aUpdateUsername);
    }

    /**
     * Constructor using a specific user name and configuring the <code>getUserUpdateName()</code>.
     *
     * @param aName
     *            the username
     * @param aUpdateUsername
     *            <code>true</code> to return the user name, <code>false</code> to return the user id, for <code>getUserUpdateName()</code>
     */
    public SuperUser(String aName, boolean aUpdateUsername) {
        super(Long.valueOf(0), aName);
        iUpdateUsername = aUpdateUsername;

        logger.debug("Loggin " + aName);
        setUsername(aName);
        setLocale(Locale.FRENCH);
        setStatus(Status.VALID);
    }

    @Override
    public Object getUserUpdateName() {
        return iUpdateUsername ? getUsername() : getUserId();
    }

    @Override
    public boolean isAuthMenu(String menuName) {
        return true;
    }

    @Override
    public boolean isAuthAction(String menuName, String actionName) {
        return true;
    }

    @Override
    public boolean isReadField(String objectName, String fieldName) {
        return true;
    }

    @Override
    public boolean isWriteField(String objectName, String fieldName) {
        return true;
    }

    @Override
    public boolean isLogged() {
        return getStatus() == Status.VALID;
    }

    @Override
    public Map<String, Map<String, Boolean>> getMapAuthAction() {
        return null;
    }

    @Override
    public void setMapAuthAction(Map<String, Map<String, Boolean>> aMapAuthAction) {
        // in dynaplus
    }

    @Override
    public Map<String, Boolean> getMapAuthMenu() {
        return null;
    }

    @Override
    public void setMapAuthMenu(Map<String, Boolean> aMapAuthMenu) {
        // in dynaplus
    }
}
