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

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Connection propre qui assure le autocommit à false et qui effectue un autorollback à la fermeture
 *
 * @author INSER SA
 *
 */
public class ISConnection implements Connection {

    /** Définition de la catégorie de logging */
    private static final Log logger = LogFactory.getLog(ISConnection.class);

    /** Connection encapsulée */
    private Connection iCon;

    /**
     * Constructeur permettant d'encapsuler une connection fournie par le système et d'assurer le autocommit false
     *
     * @param aCon
     */
    public ISConnection(Connection aCon) {
        iCon = aCon;
        try {
            iCon.setAutoCommit(false);
        } catch (SQLException e) {
            logger.error("Error setting autocommit to false", e);
        }
        if (logger.isDebugEnabled()) {
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            logger.debug("Create : " + this + "/" + stackTraceElements[2] + " / " + stackTraceElements[3]);
        }
    }

    @Override
    public <T> T unwrap(Class<T> aIface) throws SQLException {
        return iCon.unwrap(aIface);
    }

    @Override
    public boolean isWrapperFor(Class<?> aIface) throws SQLException {
        return iCon.isWrapperFor(aIface);
    }

    @Override
    public Statement createStatement() throws SQLException {
        return iCon.createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String aSql) throws SQLException {
        return iCon.prepareStatement(aSql);
    }

    @Override
    public CallableStatement prepareCall(String aSql) throws SQLException {
        return iCon.prepareCall(aSql);
    }

    @Override
    public String nativeSQL(String aSql) throws SQLException {
        return iCon.nativeSQL(aSql);
    }

    @Override
    public void setAutoCommit(boolean aAutoCommit) throws SQLException {
        iCon.setAutoCommit(aAutoCommit);

    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return iCon.getAutoCommit();
    }

    @Override
    public void commit() throws SQLException {
        iCon.commit();
    }

    @Override
    public void rollback() throws SQLException {
        if (iCon.isClosed()) {
            logger.warn("Already closed for rallback");
            return;
        }
        iCon.rollback();
    }

    /**
     * Réécriture pour permettre un système de "Autorollack"
     */
    @Override
    public void close() throws SQLException {
        if (logger.isDebugEnabled()) {
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            logger.debug("close : " + this + "/" + stackTraceElements[2] + " / " + stackTraceElements[3]);
        }
        if (iCon.isClosed()) {
            logger.warn("Was already closed!!");
            return;
        }
        if (!iCon.getAutoCommit()) {
            iCon.rollback();
        }
        iCon.close();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return iCon.isClosed();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return iCon.getMetaData();
    }

    @Override
    public void setReadOnly(boolean aReadOnly) throws SQLException {
        iCon.setReadOnly(aReadOnly);

    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return iCon.isReadOnly();
    }

    @Override
    public void setCatalog(String aCatalog) throws SQLException {
        iCon.setCatalog(aCatalog);

    }

    @Override
    public String getCatalog() throws SQLException {
        return iCon.getCatalog();
    }

    @Override
    public void setTransactionIsolation(int aLevel) throws SQLException {
        iCon.setTransactionIsolation(aLevel);

    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return iCon.getTransactionIsolation();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return iCon.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        iCon.clearWarnings();

    }

    @Override
    public Statement createStatement(int aResultSetType, int aResultSetConcurrency) throws SQLException {
        return iCon.createStatement(aResultSetType, aResultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String aSql, int aResultSetType, int aResultSetConcurrency) throws SQLException {
        return iCon.prepareStatement(aSql, aResultSetType, aResultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String aSql, int aResultSetType, int aResultSetConcurrency) throws SQLException {
        return iCon.prepareCall(aSql, aResultSetType, aResultSetConcurrency);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return iCon.getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> aMap) throws SQLException {
        iCon.setTypeMap(aMap);

    }

    @Override
    public void setHoldability(int aHoldability) throws SQLException {
        iCon.setHoldability(aHoldability);

    }

    @Override
    public int getHoldability() throws SQLException {
        return iCon.getHoldability();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return iCon.setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String aName) throws SQLException {
        return iCon.setSavepoint(aName);
    }

    @Override
    public void rollback(Savepoint aSavepoint) throws SQLException {
        iCon.rollback(aSavepoint);

    }

    @Override
    public void releaseSavepoint(Savepoint aSavepoint) throws SQLException {
        iCon.releaseSavepoint(aSavepoint);
    }

    @Override
    public Statement createStatement(int aResultSetType, int aResultSetConcurrency, int aResultSetHoldability) throws SQLException {
        return iCon.createStatement(aResultSetType, aResultSetConcurrency, aResultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String aSql, int aResultSetType, int aResultSetConcurrency, int aResultSetHoldability)
            throws SQLException {
        return iCon.prepareStatement(aSql, aResultSetType, aResultSetConcurrency, aResultSetHoldability);
    }

    @Override
    public CallableStatement prepareCall(String aSql, int aResultSetType, int aResultSetConcurrency, int aResultSetHoldability)
            throws SQLException {
        return iCon.prepareCall(aSql, aResultSetType, aResultSetConcurrency, aResultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String aSql, int aAutoGeneratedKeys) throws SQLException {
        return iCon.prepareStatement(aSql, aAutoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(String aSql, int[] aColumnIndexes) throws SQLException {
        return iCon.prepareStatement(aSql, aColumnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(String aSql, String[] aColumnNames) throws SQLException {
        return iCon.prepareStatement(aSql, aColumnNames);
    }

    @Override
    public Clob createClob() throws SQLException {
        return iCon.createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
        return iCon.createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        return iCon.createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return iCon.createSQLXML();
    }

    @Override
    public boolean isValid(int aTimeout) throws SQLException {
        return iCon.isValid(aTimeout);
    }

    @Override
    public void setClientInfo(String aName, String aValue) throws SQLClientInfoException {
        iCon.setClientInfo(aName, aValue);
    }

    @Override
    public void setClientInfo(Properties aProperties) throws SQLClientInfoException {
        iCon.setClientInfo(aProperties);
    }

    @Override
    public String getClientInfo(String aName) throws SQLException {
        return iCon.getClientInfo(aName);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return iCon.getClientInfo();
    }

    @Override
    public Array createArrayOf(String aTypeName, Object[] aElements) throws SQLException {
        return iCon.createArrayOf(aTypeName, aElements);
    }

    @Override
    public Struct createStruct(String aTypeName, Object[] aAttributes) throws SQLException {
        return iCon.createStruct(aTypeName, aAttributes);
    }

    @Override
    public void setSchema(String aSchema) throws SQLException {
        iCon.setSchema(aSchema);
    }

    @Override
    public String getSchema() throws SQLException {
        return iCon.getSchema();
    }

    @Override
    public void abort(Executor aExecutor) throws SQLException {
        iCon.abort(aExecutor);
    }

    @Override
    public void setNetworkTimeout(Executor aExecutor, int aMilliseconds) throws SQLException {
        iCon.setNetworkTimeout(aExecutor, aMilliseconds);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return iCon.getNetworkTimeout();
    }

    @Override
    public String toString() {
        if (iCon != null) {
            return iCon.toString();
        }
        return "internal con null";
    }
}
