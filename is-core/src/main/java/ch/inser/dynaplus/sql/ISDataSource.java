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

package ch.inser.dynaplus.sql;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * Datasource propre permettant de faire appel à la connection ISConnection, connection avec un autocommit à false et un rollback
 * automatique à la fermeture.
 *
 *
 *
 * @author INSER SA
 *
 */
public class ISDataSource implements DataSource {

    /** DataSource encapsulé */
    protected DataSource iDs;

    /**
     * Encapsulation du DataSource fourni par le système
     * 
     * @param aDs
     */
    public ISDataSource(DataSource aDs) {
        iDs = aDs;
    }

    /**
     * Obtention d'une connection de type ISConnection
     */
    @Override
    public Connection getConnection() throws SQLException {
        return new ISConnection(iDs.getConnection());
    }

    /**
     * Obtention d'une connection de type ISConnection
     */
    @Override
    public Connection getConnection(String aUsername, String aPassword) throws SQLException {
        return new ISConnection(iDs.getConnection(aUsername, aPassword));
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return iDs.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter aOut) throws SQLException {
        iDs.setLogWriter(aOut);
    }

    @Override
    public void setLoginTimeout(int aSeconds) throws SQLException {
        iDs.setLoginTimeout(aSeconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return iDs.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return iDs.getParentLogger();
    }

    @Override
    public boolean isWrapperFor(java.lang.Class<?> iface) throws SQLException {
        return iDs.isWrapperFor(iface);
    }

    @Override
    public <T> T unwrap(java.lang.Class<T> iface) throws SQLException {
        return iDs.unwrap(iface);
    }
}
