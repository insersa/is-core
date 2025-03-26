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

import java.security.Principal;

/**
 * Rôle Jaas
 *
 * @author INSER SA
 *
 */
public class RolePrincipal implements Principal {

    /** Nom du rôle */
    private String name;

    /**
     *
     * @param aName
     *            nom du rôle
     */
    public RolePrincipal(String aName) {
        super();
        name = aName;
    }

    /**
     *
     * @param aName
     *            nom du rôle
     */
    public void setName(String aName) {
        name = aName;
    }

    @Override
    public String getName() {
        return name;
    }

}
