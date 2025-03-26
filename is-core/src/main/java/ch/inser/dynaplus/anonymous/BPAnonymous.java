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

package ch.inser.dynaplus.anonymous;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

import ch.inser.dynamic.common.DAOParameter;
import ch.inser.dynamic.common.DAOResult;
import ch.inser.dynamic.common.DynamicDAO.Aggregator;
import ch.inser.dynamic.common.IContextManager;
import ch.inser.dynamic.common.IDAOResult;
import ch.inser.dynamic.common.ILoggedUser;
import ch.inser.dynamic.common.IValueObject;
import ch.inser.dynaplus.bo.BOFactory;
import ch.inser.dynaplus.bo.IBusinessProcess;
import ch.inser.dynaplus.format.IFormatEngine;
import ch.inser.dynaplus.util.Constants.Entity;
import ch.inser.dynaplus.util.Constants.Mode;
import ch.inser.dynaplus.vo.IVOFactory;
import ch.inser.jsl.exceptions.ISException;
import ch.inser.jsl.list.ListHandler.Sort;

/**
 * BP pour consulter une table qui n'a pas de fichier de configuration
 *
 * @author INSER SA
 *
 */
public class BPAnonymous implements IBusinessProcess, Serializable {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 6692385963522950131L;

    /** BOFactory à disposition du process métier */
    private BOFactory iBOFactory;

    /** ContextManager à disposition du process métier */
    private IContextManager iContextManager;

    /** VOFactory à disposition du process métier */
    private IVOFactory iVOFactory;

    @Override
    public void setBOFactory(BOFactory aBOFactory) {
        iBOFactory = aBOFactory;
    }

    @Override
    public void setContextManager(IContextManager aContextManager) {
        iContextManager = aContextManager;
    }

    @Override
    public void setVOFactory(IVOFactory aVOFactory) {
        iVOFactory = aVOFactory;
    }

    @Override
    public IDAOResult getRecord(String aTable, String aField, Object aValue) throws ISException {
        try (Connection con = iContextManager.getDataSource().getConnection()) {
            IDAOResult result = iBOFactory.getBO(Entity.anonymous.toString()).getRecord(aTable, aField, aValue, con);
            con.commit();
            return result;
        } catch (SQLException e) {
            throw new ISException(e);
        }
    }

    @Override
    public IDAOResult getList(String aTable, IValueObject aQuery) throws ISException {
        try (Connection con = iContextManager.getDataSource().getConnection()) {
            IDAOResult result = iBOFactory.getBO(Entity.anonymous.toString()).getList(aTable, aQuery, con);
            con.commit();
            return result;
        } catch (SQLException e) {
            throw new ISException(e);
        }
    }

    @Override
    public IDAOResult getRecord(Object aId, ILoggedUser aUser, DAOParameter... aParameters) throws ISException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult getRecordFull(Object aId, ILoggedUser aUser, DAOParameter... aParameters) throws ISException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult update(IValueObject aValueObject, ILoggedUser aUser) throws ISException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult update(List<IValueObject> aRecords, List<IValueObject> aAdeletes, ILoggedUser aUser, DAOParameter... aParameters)
            throws ISException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult create(IValueObject aValueObject, ILoggedUser aUser) throws ISException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult create(List<IValueObject> aValueObject, ILoggedUser aUser) throws ISException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult delete(Object aId, Timestamp aTimestamp, ILoggedUser aUser, DAOParameter... aParameters) throws ISException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult deleteCascade(Object aId, Timestamp aTimestamp, ILoggedUser aUser, DAOParameter... aParameters) throws ISException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult deleteMulti(List<Object> aIds, ILoggedUser aUser, DAOParameter... aParameters) throws ISException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult deleteQuery(IValueObject aVo, ILoggedUser aUser, DAOParameter... aParameters) throws ISException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult deleteMultiQuery(List<IValueObject> aVos, ILoggedUser aUser, DAOParameter... aParameters) throws ISException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult getTimestamp(Object aId, ILoggedUser aUser) throws ISException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult getField(Object aId, String aFieldName) throws ISException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IValueObject getInitVO(ILoggedUser aUser) throws ISException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IValueObject getInitVO(Mode aMode, ILoggedUser aUser) throws ISException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult getList(IValueObject aVo, ILoggedUser aUser, DAOParameter... aParameters) throws ISException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult getListCount(IValueObject aVo, ILoggedUser aUser, DAOParameter... aParameters) throws ISException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult updateField(List<Object> aLstIds, String aFieldName, List<Object> aLstValues, ILoggedUser aUser) throws ISException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult updateFieldRequest(IValueObject aVo, String aFieldName, Object aValue) throws ISException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public void downloadBlob(Object aId, ILoggedUser aUser, DAOParameter... aParameters) throws ISException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public Collection<?> executeMethode(String aNameMethode, List<?> aVos, ILoggedUser aUser) throws ISException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public Object executeMethode(String aNameMethode, Object aAnObject, ILoggedUser aUser) throws ISException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public String getDefaultOrderKey() {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public List<String> getDAOList() {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public Sort getDefaultSortOrder() {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public DAOResult updateFields(Object aId, String[] aFieldNames, Object[] aValues) throws ISException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult updateFields(IValueObject aValueObject, ILoggedUser aUser) throws ISException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult getAggregateField(IValueObject aVo, String aFieldName, Aggregator agr) throws ISException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult getFieldsRequest(IValueObject aVo, String aFieldName, DAOParameter... aParameters) throws ISException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult getFieldsRequest(IValueObject aVo, String aFieldName, ILoggedUser aUser, DAOParameter... aParameters)
            throws ISException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public void setFormatEngine(IFormatEngine aEngine) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IVOFactory getVOFactory() {
        return iVOFactory;
    }

    @Override
    public BOFactory getBOFactory() {
        return iBOFactory;
    }

    @Override
    public IContextManager getContextManager() {
        return iContextManager;
    }
}
