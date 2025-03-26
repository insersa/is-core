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

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.inser.dynamic.common.DAOParameter;
import ch.inser.dynamic.common.DynamicDAO.Aggregator;
import ch.inser.dynamic.common.IDAOResult;
import ch.inser.dynamic.common.ILoggedUser;
import ch.inser.dynamic.common.IValueObject;
import ch.inser.dynaplus.dao.IDataAccessObject;
import ch.inser.jsl.exceptions.ISException;
import ch.inser.jsl.list.ListHandler.Sort;

/**
 * @author INSER SA
 * @version 1.0
 * @created 29-févr.-2008 08:53:09
 */
public abstract class AbstractDAODelegate implements IDAODelegate, Serializable {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 6526468462733498077L;
    // En cas de besoin les comportements génériques

    /**
     * Handle sur le dao de délégation
     */
    protected IDataAccessObject iDao;

    @Override
    public IDAOResult create(IValueObject vo, ILoggedUser user, Connection connection) throws SQLException {
        return iDao.create(vo, user, connection);
    }

    @Override
    public IDAOResult delete(Object id, Timestamp timestamp, ILoggedUser user, Connection connection) throws SQLException {
        return iDao.delete(id, timestamp, user, connection);
    }

    @Override
    public IDAOResult getField(Object id, String fieldName, Connection connection) throws SQLException {
        return iDao.getField(id, fieldName, connection);
    }

    @Override
    public IDAOResult getFieldsRequest(IValueObject aVo, String aFieldName, Connection aConnection, DAOParameter... aParameters)
            throws SQLException {
        return iDao.getFieldsRequest(aVo, aFieldName, aConnection, aParameters);
    }

    @Override
    public IDAOResult getFieldsRequest(IValueObject aVo, String aFieldName, ILoggedUser aUser, Connection aConnection,
            DAOParameter... aParameters) throws SQLException {
        return iDao.getFieldsRequest(aVo, aFieldName, aUser, aConnection, aParameters);
    }

    @Override
    public IDAOResult getAggregateField(IValueObject aVo, String aFieldName, Aggregator aggr, Connection aConnection) throws SQLException {
        return iDao.getAggregateField(aVo, aFieldName, aggr, aConnection);
    }

    @Override
    public IDAOResult getList(IValueObject aVo, ILoggedUser aUser, Connection aConnection, DAOParameter... aParameters) throws ISException {
        return iDao.getList(aVo, aUser, aConnection, aParameters);
    }

    @Override
    public Object getNextId(ILoggedUser user, Connection connection) throws SQLException {
        return iDao.getNextId(user, connection);
    }

    @Override
    public IDAOResult getRecord(Object id, ILoggedUser user, Connection connection) throws SQLException {
        return iDao.getRecord(id, user, connection);
    }

    @Override
    public IDAOResult getRecordFull(Object id, ILoggedUser user, Connection connection) throws SQLException {
        return iDao.getRecordFull(id, user, connection);
    }

    @Override
    public IDAOResult getTimestamp(Object id, ILoggedUser user, Connection connection) throws SQLException {
        return iDao.getTimestamp(id, user, connection);
    }

    @Override
    public int update(Map<String, Object> updateFields, Object id, Timestamp timestamp, ILoggedUser user, Connection connection)
            throws SQLException {
        return iDao.update(updateFields, id, timestamp, user, connection);
    }

    @Override
    public IDAOResult updateField(Object aId, String aFieldName, Object aValue, Connection aCon) throws SQLException {
        return iDao.updateField(aId, aFieldName, aValue, aCon);
    }

    @Override
    public IDAOResult updateField(Object aId, String[] aFieldNames, Object[] aValues, Connection aCon) throws SQLException {
        return iDao.updateFields(aId, aFieldNames, aValues, aCon);
    }

    @Override
    public int updateFields(List<Object> aIds, String aFieldName, Object aValue, Connection aCon) throws SQLException {
        return iDao.updateFields(aIds, aFieldName, aValue, aCon);
    }

    /**
     *
     * @return liste des propriétés avec configuration DAOlist=true
     */
    @Override
    public List<String> getDAOList() {
        return iDao.getDAOList();
    }

    /**
     * Retourne la clé du tri par défaut
     */
    @Override
    public String getDefaultOrderKey() {
        return iDao.getDefaultOrderKey();
    }

    /**
     * Retourne l'orientation du tri par défaut
     */
    @Override
    public Sort getDefaultSortOrder() {
        return iDao.getDefaultSortOrder();
    }

    @Override
    public Object executeMethode(String aNameMethode, Object aObject, ILoggedUser aUser, Connection aConnection) throws ISException {
        return iDao.executeMethode(aNameMethode, aObject, aUser, aConnection);
    }

    @Override
    public Set<String> getListUpdateFields() {
        return iDao.getListUpdateFields();
    }

    @Override
    public IDAOResult getRecord(String aTable, String aField, Object aValue, Connection aCon) throws SQLException {
        return iDao.getRecord(aTable, aField, aValue, aCon);
    }

    @Override
    public IDAOResult getList(String aTable, IValueObject aQuery, Connection aCon) throws SQLException {
        return iDao.getList(aTable, aQuery, aCon);
    }

    @Override
    public IDAOResult getList(String aTable, IValueObject aQuery, Integer aRowNum, Connection aCon) throws SQLException {
        return iDao.getList(aTable, aQuery, aRowNum, aCon);
    }

    @Override
    public IDAOResult updateFieldRequest(IValueObject aVo, String aFieldName, Object aValue, Connection aCon) throws SQLException {
        return iDao.updateFieldRequest(aVo, aFieldName, aValue, aCon);
    }

    @Override
    public int updateFieldsRequest(IValueObject aVo, String[] aFieldNames, Object[] aValues, Connection aCon) throws SQLException {
        return iDao.updateFieldsRequest(aVo, aFieldNames, aValues, aCon);
    }

    @Override
    public IDAOResult getListCount(IValueObject aVo, Connection aConnection, ILoggedUser aUser, DAOParameter... aParameters)
            throws SQLException {
        return iDao.getListCount(aVo, aConnection, aUser, aParameters);
    }

}