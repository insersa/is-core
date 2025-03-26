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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import ch.inser.dynamic.common.DAOParameter;
import ch.inser.dynamic.common.DAOResult;
import ch.inser.dynamic.common.DynamicDAO.Aggregator;
import ch.inser.dynamic.common.IContextManager;
import ch.inser.dynamic.common.IDAOResult;
import ch.inser.dynamic.common.IDAOResult.Status;
import ch.inser.dynamic.common.ILoggedUser;
import ch.inser.dynamic.common.IValueObject;
import ch.inser.dynamic.quality.IQualityController;
import ch.inser.dynaplus.bo.BOFactory;
import ch.inser.dynaplus.bo.IBusinessObject;
import ch.inser.dynaplus.bo.IDAODelegate;
import ch.inser.dynaplus.util.Constants.Mode;
import ch.inser.dynaplus.vo.IVOFactory;
import ch.inser.jsl.exceptions.ISException;
import ch.inser.jsl.list.ListHandler.Sort;

/**
 * BO pour consulter une table qui n'a pas de fichier de configuration
 *
 * @author INSER SA
 *
 */
public class BOAnonymous implements IBusinessObject {

    /** DAO à disposition de l'objet métier */
    private IDAODelegate iDao;

    /** ContextManager à disposition de l'objet métier */
    // private IContextManager iContextManager;

    @Override
    public IDAOResult getRecord(String aTable, String aField, Object aValue, Connection aCon) throws SQLException {
        if (aTable == null || aField == null || aValue == null) {
            return new DAOResult(Status.NOTHING_TODO);
        }
        // Effectue la requête d'un record et récupère le value object
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
    public void setDao(IDAODelegate aDao) {
        iDao = aDao;
    }

    /**
     * @return le dao pour mise à disposition des classes filles
     */
    @Override
    public IDAODelegate getDao() {
        return iDao;
    }

    @Override
    public void setVOFactory(IVOFactory aVOFactory) {
        // Just do nothing
    }

    @Override
    public IDAOResult getRecord(Object aId, Connection aCon, ILoggedUser aUser, boolean aGetParent, DAOParameter... aParameters)
            throws SQLException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult getRecordFull(Object aId, Connection aCon, ILoggedUser aUser, boolean aGetParent, DAOParameter... aParameters)
            throws SQLException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult update(IValueObject aValueObject, Connection aCon, ILoggedUser aUser) throws SQLException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult update(List<IValueObject> aLstValueObject, Connection aCon, ILoggedUser aUser) throws SQLException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public DAOResult create(IValueObject aValueObject, Connection aCon, ILoggedUser aUser) throws SQLException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult create(List<IValueObject> aValueObject, Connection aCon, ILoggedUser aUser) throws SQLException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult delete(Object aId, Timestamp aTimestamp, Connection aConnection, ILoggedUser aUser, DAOParameter... aParameters)
            throws ISException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult deleteCascade(Object aId, Timestamp aTimestamp, Connection aConnection, ILoggedUser aUser,
            DAOParameter... aParameters) throws ISException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult deleteMulti(List<Object> aIds, Connection aConnection, ILoggedUser aUser, DAOParameter... aParameters)
            throws ISException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult deleteQuery(IValueObject aVo, Connection aConnection, ILoggedUser aUser, DAOParameter... aParameters)
            throws ISException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult deleteMultiQuery(List<IValueObject> aVos, Connection aConnection, ILoggedUser aUser, DAOParameter... aParameters)
            throws ISException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult getTimestamp(Object aId, Connection aCon, ILoggedUser aUser) throws SQLException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult getField(Object aId, String aFieldName, Connection aCon) throws SQLException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult updateField(Object aId, String aFieldName, Object aValue, Connection aCon) throws SQLException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult updateFieldRequest(IValueObject aVo, String aFieldName, Object aValue, Connection aCon) throws SQLException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult updateFields(Object aId, String[] aFieldNames, Object[] aValues, Connection aCon) throws SQLException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult updateFields(IValueObject aValueObject, Connection aConnection, ILoggedUser aUser) throws ISException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public int updateFields(List<Object> aIds, String aFieldName, Object aValue, Connection aCon) throws SQLException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public int updateFieldsRow(IValueObject aValueObject, Connection aCon, ILoggedUser aUser) throws SQLException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IValueObject getInitVO(ILoggedUser aUser, Connection aCon, boolean aGetParent) throws SQLException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IValueObject getInitVO(Mode aMode, ILoggedUser aUser, Connection aCon, boolean aGetParent) throws SQLException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult getList(IValueObject aVo, ILoggedUser aUser, Connection aConnection, DAOParameter... aParameters) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult updateField(List<Object> aLstId, String aFieldName, List<Object> aLstValue, Connection aCon, ILoggedUser aUser)
            throws SQLException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public Collection<?> executeMethode(String aNameMethode, List<?> aVos, ILoggedUser aUser, Connection aConnection) throws ISException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public Object executeMethode(String aNameMethode, Object aAnObject, ILoggedUser aUser, Connection aConnection) throws ISException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public void readFilter(Map<String, Object> aValues, ILoggedUser aUser) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public void writeFilter(Map<String, Object> aValues, ILoggedUser aUser) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult loadChildren(IValueObject aValueObject, Connection aCon, ILoggedUser aUser) throws SQLException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult loadChildren(IValueObject aOriginalVo, IValueObject aResultVo, Connection aCon, ILoggedUser aUser)
            throws SQLException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult loadChildrenObjects(IValueObject aVoOriginal, IValueObject aVoResult, Connection aCon, ILoggedUser aUser)
            throws SQLException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public String getDefaultOrderKey() {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public Sort getDefaultSortOrder() {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public List<String> getDAOList() {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult getAggregateField(IValueObject aVo, String aFieldName, Aggregator agr, Connection aConnection) throws SQLException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public int updateFieldsRequest(IValueObject aVo, String[] aFieldNames, Object[] aValues, Connection aCon) throws SQLException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public void setBOFactory(BOFactory aBOFactory) {
        // Just do nothing
    }

    @Override
    public void setContextManager(IContextManager aContextManager) {
        // iContextManager = aContextManager;
        // Just do nothing...NOT USE

    }

    @Override
    public IDAOResult getListCount(IValueObject aVo, Connection aConnection, ILoggedUser aUser, DAOParameter... aParameters)
            throws SQLException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult getFieldsRequest(IValueObject aVo, String aFieldName, Connection aConnection, DAOParameter... aParameters)
            throws SQLException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult getFieldsRequest(IValueObject aVo, String aFieldName, ILoggedUser aUser, Connection aConnection,
            DAOParameter... aParameters) throws SQLException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public BOFactory getBOFactory() {
        // Just do nothing
        return null;
    }

    @Override
    public IVOFactory getVOFactory() {
        // Just do nothing
        return null;
    }

    @Override
    public IContextManager getContextManager() {
        // Just do nothing
        return null;
    }

    @Override
    public void setQualityController(IQualityController aController) {
        throw new UnsupportedOperationException("Not implemented!");
    }
}
