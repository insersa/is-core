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

package ch.inser.dynaplus.bo;

import java.sql.Connection;
import java.sql.SQLException;

import ch.inser.dynamic.common.ILoggedUser;

/**
 * @author INSER SA
 * @version 1.0
 * @created 12-juin 2009 09:50:00
 */
public interface IBOProcedure {
    /**
     * Execute a process
     *
     * @param number
     *            number of items to select during process
     * @param con
     *            connection
     * @param user
     *            logged user
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public void execute(int number, Connection con, ILoggedUser user) throws SQLException;
}
