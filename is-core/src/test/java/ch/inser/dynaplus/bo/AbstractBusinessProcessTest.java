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

import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Matchers;

import ch.inser.dynamic.common.DAOResult;
import ch.inser.dynamic.common.IContextManager;
import ch.inser.dynamic.common.ILoggedUser;
import ch.inser.dynamic.common.IValueObject;
import ch.inser.dynamic.util.VOInfo;
import ch.inser.dynaplus.util.Constants;
import ch.inser.jsl.exceptions.ISException;

/**
 * @author INSER SA
 *
 */
public class AbstractBusinessProcessTest {

    /**
     * Mocked object
     */
    IContextManager iContextManager;
    /**
     * Mocked object
     */
    DataSource iDataSource;
    /**
     * Mocked object
     */
    Connection iConnection;
    /**
     * Mocked object
     */
    VOInfo iVOInfo;
    /**
     * Mocked object
     */
    IValueObject iValueObject;
    /**
     * Mocked object
     */
    BOFactory iBOFactory;
    /**
     * Mocked object
     */
    IBusinessObject iBusinessObject;
    /**
     * Mocked object
     */
    ILoggedUser iUser;

    /**
     * Initialisation des mock
     *
     * @throws SQLException
     *             en cas de problèmes
     */
    @Before
    public void initMock() throws SQLException {
        initContextManagerMock();
        initVOInfoMock();
        initBOFactoryMock();
        initLoggedUserMock();
        initValueObjectMock();
    }

    /**
     * Test method for
     * {@link ch.inser.dynaplus.bo.AbstractBusinessProcess#create(ch.inser.dynamic.common.IValueObject, ch.inser.dynamic.common.ILoggedUser)}
     * .
     *
     * @throws SQLException
     *             en cas de problème avec la base de données
     */
    @Test
    public void testCreate() throws ISException, SQLException {
        IBusinessProcess bp = new GenericBusinessProcess("test", iVOInfo);
        bp.setContextManager(iContextManager);
        bp.setBOFactory(iBOFactory);

        when(iBusinessObject.create(iValueObject, iConnection, iUser))
                .thenReturn(new DAOResult());
        bp.create(iValueObject, iUser);
        verify(iBusinessObject, times(1)).create(iValueObject, iConnection,
                iUser);
        verify(iBusinessObject, times(0)).loadChildren(
                Matchers.any(IValueObject.class), eq(iValueObject),
                eq(iConnection), eq(iUser));
        verify(iConnection, times(1)).close();

        initMock();
        // Création avec retour not null
        when(iBusinessObject.create(iValueObject, iConnection, iUser))
                .thenReturn(new DAOResult(iValueObject));
        bp = new GenericBusinessProcess("test", iVOInfo);
        bp.setContextManager(iContextManager);
        bp.setBOFactory(iBOFactory);
        bp.create(iValueObject, iUser);
        verify(iBusinessObject, times(1)).create(iValueObject, iConnection,
                iUser);
        verify(iBusinessObject, times(1)).loadChildren(
                Matchers.any(IValueObject.class), eq(iValueObject),
                eq(iConnection), eq(iUser));
        verify(iConnection, times(1)).close();
    }

    /**
     * Test method for
     * {@link ch.inser.dynaplus.bo.AbstractBusinessProcess#createMulti(java.util.List, ch.inser.dynamic.common.ILoggedUser)}
     * .
     */
    @Ignore
    @Test
    public void testCreateMulti() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link ch.inser.dynaplus.bo.AbstractBusinessProcess#delete(java.lang.Object, java.sql.Timestamp, ch.inser.dynamic.common.ILoggedUser)}
     * .
     */
    @Ignore
    @Test
    public void testDelete() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link ch.inser.dynaplus.bo.AbstractBusinessProcess#deleteCascade(java.lang.Object, java.sql.Timestamp, ch.inser.dynamic.common.ILoggedUser)}
     * .
     */
    @Ignore
    @Test
    public void testDeleteCascade() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link ch.inser.dynaplus.bo.AbstractBusinessProcess#deleteMulti(java.util.List, ch.inser.dynamic.common.ILoggedUser)}
     * .
     */
    @Ignore
    @Test
    public void testDeleteMulti() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link ch.inser.dynaplus.bo.AbstractBusinessProcess#deleteMultiQuery(java.util.List, ch.inser.dynamic.common.ILoggedUser)}
     * .
     */
    @Ignore
    @Test
    public void testDeleteMultiQuery() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link ch.inser.dynaplus.bo.AbstractBusinessProcess#deleteQuery(ch.inser.dynamic.common.IValueObject, ch.inser.dynamic.common.ILoggedUser)}
     * .
     */
    @Ignore
    @Test
    public void testDeleteQuery() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link ch.inser.dynaplus.bo.AbstractBusinessProcess#getField(java.lang.Object, java.lang.String)}
     * .
     *
     * @throws SQLException
     *             en cas de problèmes
     */
    @Test
    public void testGetField() throws ISException, SQLException {
        IBusinessProcess bp = new GenericBusinessProcess("test", iVOInfo);
        bp.setContextManager(iContextManager);
        bp.setBOFactory(iBOFactory);
        bp.getField(100l, "field_test");
        verify(iBusinessObject, times(1)).getField(100l, "field_test",
                iConnection);
        verify(iConnection, times(1)).close();
    }

    /**
     * Test method for
     * {@link ch.inser.dynaplus.bo.AbstractBusinessProcess#getInitVO(ch.inser.dynamic.common.ILoggedUser)}
     * .
     *
     * @throws SQLException
     *             en cas de problèmes
     * @throws ISException
     */
    @Test
    public void testGetInitVOILoggedUser() throws SQLException, ISException {
        IBusinessProcess bp = new GenericBusinessProcess("test", iVOInfo);
        bp.setContextManager(iContextManager);
        bp.setBOFactory(iBOFactory);
        bp.getInitVO(iUser);
        verify(iBusinessObject, times(1)).getInitVO(iUser, iConnection, true);
        verify(iConnection, times(1)).close();
    }

    /**
     * Test method for
     * {@link ch.inser.dynaplus.bo.AbstractBusinessProcess#getInitVO(ch.inser.dynaplus.util.Constants.Mode, ch.inser.dynamic.common.ILoggedUser)}
     * .
     *
     * @throws SQLException
     *             en cas de problèmes
     * @throws ISException
     */
    @Test
    public void testGetInitVOModeILoggedUser()
            throws SQLException, ISException {
        IBusinessProcess bp = new GenericBusinessProcess("test", iVOInfo);
        bp.setContextManager(iContextManager);
        bp.setBOFactory(iBOFactory);
        bp.getInitVO(Constants.Mode.create, iUser);
        verify(iBusinessObject, times(1)).getInitVO(Constants.Mode.create,
                iUser, iConnection, true);
        verify(iConnection, times(1)).close();
    }

    /**
     * Test method for
     * {@link ch.inser.dynaplus.bo.AbstractBusinessProcess#getRecord(java.lang.Object, ch.inser.dynamic.common.ILoggedUser)}
     * .
     */
    @Ignore
    @Test
    public void testGetRecord() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link ch.inser.dynaplus.bo.AbstractBusinessProcess#getTimestamp(java.lang.Object, ch.inser.dynamic.common.ILoggedUser)}
     * .
     *
     * @throws SQLException
     *             en cas de problèmes
     * @throws ISException
     */
    @Test
    public void testGetTimestamp() throws SQLException, ISException {
        IBusinessProcess bp = new GenericBusinessProcess("test", iVOInfo);
        bp.setContextManager(iContextManager);
        bp.setBOFactory(iBOFactory);
        when(iBusinessObject.getTimestamp(100l, iConnection, iUser))
                .thenReturn(new DAOResult(new Timestamp(0)));
        bp.getTimestamp(100l, iUser);
        verify(iBusinessObject, times(1)).getTimestamp(100l, iConnection,
                iUser);
        verify(iConnection, times(1)).close();
    }

    /**
     * Test method for
     * {@link ch.inser.dynaplus.bo.AbstractBusinessProcess#update(ch.inser.dynamic.common.IValueObject, ch.inser.dynamic.common.ILoggedUser)}
     * .
     */
    @Ignore
    @Test
    public void testUpdateIValueObjectILoggedUser() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link ch.inser.dynaplus.bo.AbstractBusinessProcess#update(ch.inser.dynaplus.jsf.core.IListHandler, ch.inser.dynamic.common.ILoggedUser)}
     * .
     */
    @Ignore
    @Test
    public void testUpdateIListHandlerILoggedUser() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link ch.inser.dynaplus.bo.AbstractBusinessProcess#getList(ch.inser.dynamic.common.IValueObject, int, ch.inser.jsl.list.ListHandler.Sort, ch.inser.dynamic.common.ILoggedUser)}
     * .
     */
    @Ignore
    @Test
    public void testGetListIValueObjectIntSortILoggedUser() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link ch.inser.dynaplus.bo.AbstractBusinessProcess#getList(ch.inser.dynamic.common.IValueObject, int, ch.inser.jsl.list.ListHandler.Sort, java.lang.Integer, ch.inser.dynamic.common.ILoggedUser)}
     * .
     */
    @Ignore
    @Test
    public void testGetListIValueObjectIntSortIntegerILoggedUser() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link ch.inser.dynaplus.bo.AbstractBusinessProcess#getList(ch.inser.dynamic.common.IValueObject, int, ch.inser.dynamic.common.ILoggedUser)}
     * .
     */
    @Ignore
    @Test
    public void testGetListIValueObjectIntILoggedUser() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link ch.inser.dynaplus.bo.AbstractBusinessProcess#getList(ch.inser.dynamic.common.IValueObject, int, java.lang.Integer, ch.inser.dynamic.common.ILoggedUser)}
     * .
     */
    @Ignore
    @Test
    public void testGetListIValueObjectIntIntegerILoggedUser() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link ch.inser.dynaplus.bo.AbstractBusinessProcess#getList(ch.inser.dynamic.common.IValueObject, java.lang.String, ch.inser.jsl.list.ListHandler.Sort, ch.inser.dynamic.common.ILoggedUser)}
     * .
     */
    @Ignore
    @Test
    public void testGetListIValueObjectStringSortILoggedUser() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link ch.inser.dynaplus.bo.AbstractBusinessProcess#getList(ch.inser.dynamic.common.IValueObject, java.lang.String, ch.inser.jsl.list.ListHandler.Sort, java.lang.Integer, ch.inser.dynamic.common.ILoggedUser)}
     * .
     */
    @Ignore
    @Test
    public void testGetListIValueObjectStringSortIntegerILoggedUser() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link ch.inser.dynaplus.bo.AbstractBusinessProcess#getList(ch.inser.dynamic.common.IValueObject, java.lang.String, ch.inser.dynamic.common.ILoggedUser)}
     * .
     */
    @Ignore
    @Test
    public void testGetListIValueObjectStringILoggedUser() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link ch.inser.dynaplus.bo.AbstractBusinessProcess#getList(ch.inser.dynamic.common.IValueObject, java.lang.String, java.lang.Integer, ch.inser.dynamic.common.ILoggedUser)}
     * .
     */
    @Ignore
    @Test
    public void testGetListIValueObjectStringIntegerILoggedUser() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link ch.inser.dynaplus.bo.AbstractBusinessProcess#updateField(java.util.Collection, java.lang.String, java.util.Collection, ch.inser.dynamic.common.ILoggedUser)}
     * .
     *
     * @throws SQLException
     *             en cas de problèmes
     * @throws ISException
     */
    @Test
    public void testUpdateField() throws SQLException, ISException {
        IBusinessProcess bp = new GenericBusinessProcess("test", iVOInfo);
        bp.setContextManager(iContextManager);
        bp.setBOFactory(iBOFactory);
        List<Object> idCollection = new ArrayList<>();
        List<Object> valueCollection = new ArrayList<>();
        bp.updateField(idCollection, "filed_test", valueCollection, iUser);
        verify(iBusinessObject, times(1)).updateField(idCollection,
                "filed_test", valueCollection, iConnection, iUser);
        verify(iConnection, times(1)).close();
    }

    /**
     * Test method for
     * {@link ch.inser.dynaplus.bo.AbstractBusinessProcess#executeMethode(java.lang.String, java.util.List, ch.inser.dynamic.common.ILoggedUser)}
     * .
     */
    @Ignore
    @Test
    public void testExecuteMethodeStringListOfQILoggedUser() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link ch.inser.dynaplus.bo.AbstractBusinessProcess#executeMethode(java.lang.String, java.lang.Object, ch.inser.dynamic.common.ILoggedUser)}
     * .
     */
    @Ignore
    @Test
    public void testExecuteMethodeStringObjectILoggedUser() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link ch.inser.dynaplus.bo.AbstractBusinessProcess#getDefaultOrderKey()}
     * .
     */
    @Ignore
    @Test
    public void testGetDefaultOrderKey() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link ch.inser.dynaplus.bo.AbstractBusinessProcess#getDAOList()}.
     */
    @Ignore
    @Test
    public void testGetDAOList() {
        fail("Not yet implemented");
    }

    // **********************************************************MOCKING work!!!
    /**
     * initialisation du mockcontextManager
     *
     * @throws SQLException
     *             en cas de problème dans le mocking du datasource
     */
    private void initContextManagerMock() throws SQLException {
        iContextManager = mock(IContextManager.class);
        iDataSource = mock(DataSource.class);
        iConnection = mock(Connection.class);

        // return values for iContextMAnager
        when(iContextManager.getDataSource(anyString()))
                .thenReturn(iDataSource);
        when(iContextManager.getDataSource()).thenReturn(iDataSource);

        // return values for iDataSource
        when(iDataSource.getConnection()).thenReturn(iConnection);
    }

    /**
     * Initialisation du mock de VOInfo
     */
    private void initVOInfoMock() {
        iVOInfo = mock(VOInfo.class);
    }

    /**
     * initialisation du mock de BOFactory
     */
    private void initBOFactoryMock() {
        iBOFactory = mock(BOFactory.class);
        iBusinessObject = mock(IBusinessObject.class);
        when(iBOFactory.getBO(anyString())).thenReturn(iBusinessObject);
    }

    /**
     * initialisation du mock de ILoggedUser
     */
    private void initLoggedUserMock() {
        iUser = mock(ILoggedUser.class);
    }

    /**
     * initialisation du mock ValueObject
     */
    private void initValueObjectMock() {
        iValueObject = mock(IValueObject.class);
        IValueObject cloneVO = mock(IValueObject.class);
        when(iValueObject.clone()).thenReturn(cloneVO);
    }

}
