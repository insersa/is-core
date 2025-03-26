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

import java.io.File;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.inser.dynamic.common.DAOParameter;
import ch.inser.dynamic.common.DAOParameter.Name;
import ch.inser.dynamic.common.DAOResult;
import ch.inser.dynamic.common.DynamicDAO.Aggregator;
import ch.inser.dynamic.common.IContextManager;
import ch.inser.dynamic.common.IDAOResult;
import ch.inser.dynamic.common.IDAOResult.Status;
import ch.inser.dynamic.common.ILoggedUser;
import ch.inser.dynamic.common.IValueObject;
import ch.inser.dynamic.util.MultiselectInfo;
import ch.inser.dynamic.util.ShellInfo;
import ch.inser.dynamic.util.VOInfo;
import ch.inser.dynaplus.format.IFormatEngine;
import ch.inser.dynaplus.shell.GenericShellProcess;
import ch.inser.dynaplus.shell.ShellResult;
import ch.inser.dynaplus.util.Constants.Mode;
import ch.inser.dynaplus.vo.IVOFactory;
import ch.inser.jsl.exceptions.ISException;
import ch.inser.jsl.list.ListHandler.Sort;
import ch.inser.jsl.tools.StringTools;

/**
 * @author INSER SA
 * @version 1.0
 * @created 29-févr.-2008 08:53:09
 */
public abstract class AbstractBusinessProcess implements IBusinessProcess, Serializable {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 9018389946167056512L;

    /** Définition de la catégorie de logging */
    private static final Log logger = LogFactory.getLog(AbstractBusinessProcess.class);

    /**
     * Nom du business process
     */
    private String iName;

    /**
     * Paramètres applicatifs du business process
     */
    private VOInfo iVOInfo;

    /**
     * Nom connexion BD pour l'objet métier, si null db par défaut utilisé
     */
    protected String iDbObjectName = null;

    /** BOFactory à disposition du process métier métier */
    private BOFactory iBOFactory;

    /** ContextManager à disposition du process métier */
    private transient IContextManager iContextManager;

    /**
     * VOFactory à disposition du processus métier
     */
    private transient IVOFactory iVOFactory;

    /** Engine pour transformer des enregistrements en format pdf, csv etc. */
    private transient IFormatEngine iFormatEngine;

    /**
     * Constructeur par défaut
     *
     * @param aName
     *            nom du business process
     * @param aVOInfo
     *            paramètres applicatifs du business process
     */
    protected AbstractBusinessProcess(String aName, VOInfo aVOInfo) {
        iVOInfo = aVOInfo;
        iName = aName;
    }

    @Override
    public IDAOResult create(IValueObject valueObject, ILoggedUser user) throws ISException {
        IBusinessObject bo = iBOFactory.getBO(iName);
        IDAOResult result = new DAOResult(Status.KO);
        try (Connection con = iContextManager.getDataSource(iDbObjectName).getConnection()) {
            IValueObject voBackup = (IValueObject) valueObject.clone();
            result = bo.create(valueObject, con, user);
            if (!result.isStatusOK()) {
                con.rollback();
                return result;
            }
            // Modification des enfants
            if (voBackup.getProperty("childrenlistObjects") != null) {
                bo.loadChildrenObjects(voBackup, valueObject, con, user);
            } else {
                bo.loadChildren(voBackup, valueObject, con, user);
            }
            // Do the shell Job
            boolean failure = executeShell("create", true, valueObject);
            if (failure) {
                // rollback it!
                executeShell("create-rollback", false, valueObject);
                con.rollback();
                return new DAOResult(Status.KO);
            }
            // OK then commit!
            con.commit();
        } catch (SQLException e) {
            throw new ISException(e);
        }
        return result;
    }

    @Override
    public IDAOResult create(List<IValueObject> valueObjects, ILoggedUser user) throws ISException {
        IBusinessObject bo = iBOFactory.getBO(iName);
        DataSource ds = iContextManager.getDataSource(iDbObjectName);

        Connection con = null;
        boolean isOK = false;
        IDAOResult result = null;
        try {
            con = ds.getConnection();
            con.setAutoCommit(false);
            result = bo.create(valueObjects, con, user);
            con.commit();
            isOK = true;
        } catch (SQLException e) {
            throw new ISException(e);
        } finally {
            try {
                if (!isOK && con != null) {
                    // Rollback the shell Job too

                    con.rollback();
                }
            } catch (SQLException e) {
                logger.error("SQLException :", e);
            } finally {
                try {
                    if (con != null) {
                        con.close();
                    }
                } catch (SQLException e) {
                    logger.error("SQLException :", e);
                }
            }
        }
        return result;
    }

    @Override
    public IDAOResult deleteMulti(List<Object> aIds, ILoggedUser aUser, DAOParameter... aParameters) throws ISException {
        try (Connection con = iContextManager.getDataSource(iDbObjectName).getConnection()) {
            IDAOResult result = iBOFactory.getBO(iName).deleteMulti(aIds, con, aUser, aParameters);
            con.commit();
            return result;
        } catch (SQLException e) {
            throw new ISException(e);
        }
    }

    @Override
    public IDAOResult deleteMultiQuery(List<IValueObject> aVos, ILoggedUser aUser, DAOParameter... aParameters) throws ISException {
        IBusinessObject bo = iBOFactory.getBO(iName);
        try (Connection con = iContextManager.getDataSource(iDbObjectName).getConnection()) {
            IDAOResult result = bo.deleteMultiQuery(aVos, con, aUser, aParameters);
            con.commit();
            return result;
        } catch (SQLException e) {
            throw new ISException(e);
        }
    }

    @Override
    public IDAOResult deleteQuery(IValueObject aVo, ILoggedUser aUser, DAOParameter... aParameters) throws ISException {
        try (Connection con = iContextManager.getDataSource(iDbObjectName).getConnection()) {
            IDAOResult result = iBOFactory.getBO(iName).deleteQuery(aVo, con, aUser, aParameters);
            con.commit();
            return result;
        } catch (SQLException e) {
            throw new ISException(e);
        }
    }

    @Override
    public IDAOResult getField(Object id, String fieldName) throws ISException {
        try (Connection con = iContextManager.getDataSource(iDbObjectName).getConnection()) {
            return iBOFactory.getBO(iName).getField(id, fieldName, con);
        } catch (SQLException e) {
            throw new ISException(e);
        }
    }

    @Override
    public IDAOResult getFieldsRequest(IValueObject aVo, String aFieldName, DAOParameter... aParameters) throws ISException {
        try (Connection con = iContextManager.getDataSource(iDbObjectName).getConnection()) {
            return iBOFactory.getBO(iName).getFieldsRequest(aVo, aFieldName, con, aParameters);
        } catch (SQLException e) {
            throw new ISException(e);
        }
    }

    @Override
    public IDAOResult getFieldsRequest(IValueObject aVo, String aFieldName, ILoggedUser aUser, DAOParameter... aParameters)
            throws ISException {
        try (Connection con = iContextManager.getDataSource(iDbObjectName).getConnection()) {
            return iBOFactory.getBO(iName).getFieldsRequest(aVo, aFieldName, aUser, con, aParameters);
        } catch (SQLException e) {
            throw new ISException(e);
        }
    }

    @Override
    public IDAOResult getAggregateField(IValueObject aVo, String aFieldName, Aggregator aggr) throws ISException {
        try (Connection con = iContextManager.getDataSource(iDbObjectName).getConnection()) {
            return iBOFactory.getBO(iName).getAggregateField(aVo, aFieldName, aggr, con);
        } catch (SQLException e) {
            throw new ISException(e);
        }
    }

    @Override
    public IValueObject getInitVO(ILoggedUser user) throws ISException {
        IBusinessObject bo = iBOFactory.getBO(iName);
        IValueObject vo = null;
        try (Connection con = iContextManager.getDataSource(iDbObjectName).getConnection()) {
            vo = bo.getInitVO(user, con, true);
        } catch (SQLException e) {
            throw new ISException(e);
        }
        return vo;
    }

    @Override
    public IValueObject getInitVO(Mode mode, ILoggedUser user) throws ISException {
        IBusinessObject bo = iBOFactory.getBO(iName);
        IValueObject vo = null;
        try (Connection con = iContextManager.getDataSource(iDbObjectName).getConnection()) {
            vo = bo.getInitVO(mode, user, con, true);
        } catch (SQLException e) {
            throw new ISException(e);
        }
        return vo;
    }

    @Override
    public IDAOResult getRecord(Object id, ILoggedUser user, DAOParameter... aParameters) throws ISException {
        IDAOResult result = null;
        try {
            // Cherche l'enregistrement dans un format spécifié
            if (DAOParameter.getValue(DAOParameter.Name.RESULT_FORMAT, aParameters) != null) {
                return getFormattedRecord(id, user, aParameters);
            }

            // Cherche l'enregistrement en format VO
            try (Connection con = iContextManager.getDataSource(iDbObjectName).getConnection()) {
                result = iBOFactory.getBO(iName).getRecord(id, con, user, true, aParameters);
                return result;
            }

        } catch (SQLException e) {
            throw new ISException(e);
        }
    }

    @Override
    public IDAOResult getRecordFull(Object id, ILoggedUser user, DAOParameter... aParameters) throws ISException {
        IDAOResult result = null;
        try {
            // Cherche l'enregistrement dans un format spécifié
            if (DAOParameter.getValue(DAOParameter.Name.RESULT_FORMAT, aParameters) != null) {
                return getFormattedRecord(id, user, aParameters);
            }

            // Cherche l'enregistrement en format VO
            try (Connection con = iContextManager.getDataSource(iDbObjectName).getConnection()) {
                result = iBOFactory.getBO(iName).getRecordFull(id, con, user, true, aParameters);
                return result;
            }

        } catch (SQLException e) {
            throw new ISException(e);
        }
    }

    /**
     * Returns the record in the requested format
     *
     * @param aId
     *            record id
     * @param aUser
     *            logged user
     * @param aParameters
     *            the parameters for the requested format
     * @return DAOResult with the record in the requested format, ex. PDF
     */
    private IDAOResult getFormattedRecord(Object aId, ILoggedUser aUser, DAOParameter... aParameters) throws SQLException, ISException {

        // Vérify that format engine has been set
        if (iFormatEngine == null) {
            logger.error("No format engine has been set for business process " + iName);
            return new DAOResult(Status.KO);
        }

        // Verify that the record exists and then close the connection!
        IDAOResult result = null;
        try (Connection con = iContextManager.getDataSource(iDbObjectName).getConnection()) {
            result = iBOFactory.getBO(iName).getRecord(aId, con, aUser, false);
            if (!result.isStatusOK()) {
                return result;
            }
        }

        // Format the record
        return iFormatEngine.format(result.getValueObject(), getFormatParameters(result.getValueObject(), aUser, aParameters));
    }

    /**
     * Returns the list in the requested format
     *
     * @param aVos
     *            list of records
     *
     * @param aParameters
     *            the parameters for the requested format
     *
     * @param aUser
     *            logged user
     * @return DAOResult with the record in the requested format, ex. CSV
     */
    protected IDAOResult getFormattedList(List<IValueObject> aVos, ILoggedUser aUser, DAOParameter... aParameters) throws ISException {

        // Vérify that format engine has been set
        if (iFormatEngine == null) {
            logger.error("No format engine has been set for business process " + iName);
            return new DAOResult(Status.KO);
        }

        // Format the record
        return iFormatEngine.format(aVos, aUser, aParameters);
    }

    /**
     * Returns the parameters required for generating the record in the requested format
     *
     * @param aVo
     *            record
     * @param aUser
     *            logged user
     * @param aParameters
     *            initial parameters, ex. language
     * @return all parameters for generating the requested format, ex. user id, non-standard report file name. Specific for every business
     *         object
     */
    @SuppressWarnings("unused")
    protected DAOParameter[] getFormatParameters(IValueObject aVo, ILoggedUser aUser, DAOParameter[] aParameters) {
        return aParameters;
    }

    @Override
    public IDAOResult getTimestamp(Object id, ILoggedUser user) throws ISException {
        try (Connection con = iContextManager.getDataSource(iDbObjectName).getConnection()) {
            IDAOResult result = iBOFactory.getBO(iName).getTimestamp(id, con, user);
            return result;
        } catch (SQLException e) {
            throw new ISException(e);
        }
    }

    @Override
    public IDAOResult update(IValueObject valueObject, ILoggedUser user) throws ISException {
        IBusinessObject bo = iBOFactory.getBO(iName);
        try (Connection con = iContextManager.getDataSource(iDbObjectName).getConnection()) {
            IDAOResult result = bo.update(valueObject, con, user);

            if (result.isStatusOK() || result.isStatusNOTHING_TODO()) {
                IDAOResult resultChildren = null;
                if (valueObject.getProperty("childrenlistObjects") != null) {
                    resultChildren = bo.loadChildrenObjects(valueObject, valueObject, con, user);
                } else {
                    resultChildren = bo.loadChildren(valueObject, con, user);
                }
                if (result.isStatusNOTHING_TODO()) {
                    result.setStatus(resultChildren.getStatus());
                }
            } else {
                // Nous avons eu un problème, rollback !
                con.rollback();
                return result;
            }
            // LE update du BO générique retourne un ValueObject dans la
            // réponse, mais c'est pas forcement le cas pour les surchargements
            if (result.getValueObject() == null) {
                result.setValueObject(getRecord(valueObject.getId(), user).getValueObject());
            }
            con.commit();
            return result;
        } catch (SQLException e) {
            throw new ISException(e);
        }
    }

    @Override
    public IDAOResult update(List<IValueObject> aRecords, List<IValueObject> aDeletes, ILoggedUser user, DAOParameter... aParameters)
            throws ISException {
        IBusinessObject bo = iBOFactory.getBO(iName);
        try (Connection con = iContextManager.getDataSource(iDbObjectName).getConnection()) {
            List<IValueObject> results;

            // Add and/or update
            if (aRecords != null) {
                results = new ArrayList<>(aRecords.size());
                for (IValueObject vo : aRecords) {
                    if (vo.getId() == null) {
                        IDAOResult result = bo.create(vo, con, user);
                        if (!result.isStatusOK()) {
                            con.rollback();
                            logger.error("Erreur de création de vo " + vo.getProperties());
                            return result;
                        }
                        results.add(result.getValueObject());
                    } else {
                        IDAOResult result = bo.update(vo, con, user);
                        if (!result.isStatusOK() && !result.isStatusNOTHING_TODO()) {
                            con.rollback();
                            logger.error("Erreur d'enregistrement de vo. Res: " + result.getStatus() + ". Vo: " + vo.getProperties());
                            return result;
                        }
                        results.add(result.getValueObject());
                    }
                }
            } else {
                results = new ArrayList<>(0);
            }

            // Delete
            if (aDeletes != null) {
                for (IValueObject vo : aDeletes) {
                    if (vo.getId() != null) {
                        IDAOResult result = bo.delete(vo.getId(), vo.getTimestamp(), con, user, aParameters);
                        if (!result.isStatusOK()) {
                            con.rollback();
                            logger.error("Error deleting the vo " + vo.getProperties());
                            return result;
                        }
                        results.add(result.getValueObject());
                    }
                }
            }

            con.commit();
            return new DAOResult(results);
        } catch (SQLException e) {
            throw new ISException(e);
        }
    }

    @Override
    public IDAOResult getList(IValueObject aVo, ILoggedUser aUser, DAOParameter... aParameters) throws ISException {
        // Remove all multiselect things
        for (MultiselectInfo multiselect : iVOInfo.getMultiselects()) {
            // All the items
            aVo.removeProperty(multiselect.getSelectName() + "_list");
            aVo.removeProperty(multiselect.getSelectName());
        }

        try (Connection con = iContextManager.getDataSource(iDbObjectName).getConnection()) {
            IDAOResult result = iBOFactory.getBO(iName).getList(aVo, aUser, con, aParameters);

            // Cherche l'enregistrement dans un format spécifié
            if (DAOParameter.getValue(DAOParameter.Name.RESULT_FORMAT, aParameters) != null) {
                return getFormattedList(result.getListObject(), aUser, aParameters);
            }
            return result;

        } catch (SQLException e) {
            throw new ISException(e);
        }
    }

    @Override
    public IDAOResult updateField(List<Object> aLstId, String aFieldName, List<Object> aLstValue, ILoggedUser aUser) throws ISException {
        try (Connection con = iContextManager.getDataSource(iDbObjectName).getConnection()) {
            IDAOResult result = iBOFactory.getBO(iName).updateField(aLstId, aFieldName, aLstValue, con, aUser);
            con.commit();
            return result;
        } catch (SQLException e) {
            throw new ISException(e);
        }
    }

    @Override
    public IDAOResult updateFields(Object id, String[] aFieldNames, Object[] aValues) throws ISException {
        try (Connection con = iContextManager.getDataSource(iDbObjectName).getConnection()) {
            IDAOResult result = iBOFactory.getBO(iName).updateFields(id, aFieldNames, aValues, con);
            if (result.getNbrRecords() >= 0 || result.isStatusNOTHING_TODO()) {
                con.commit();
            }
            return result;
        } catch (SQLException e) {
            throw new ISException(e);
        }
    }

    @Override
    public IDAOResult updateFields(IValueObject aValueObject, ILoggedUser aUser) throws ISException {
        try (Connection con = iContextManager.getDataSource(iDbObjectName).getConnection()) {
            IDAOResult result = iBOFactory.getBO(iName).updateFields(aValueObject, con, aUser);
            if (result.getNbrRecords() >= 0 || result.isStatusNOTHING_TODO()) {
                con.commit();
            }
            return result;
        } catch (SQLException e) {
            throw new ISException(e);
        }
    }

    @Override
    public IDAOResult updateFieldRequest(IValueObject aVo, String aFieldName, Object aValue) throws ISException {
        IBusinessObject bo = iBOFactory.getBO(iName);
        try (Connection con = iContextManager.getDataSource(iDbObjectName).getConnection()) {
            IDAOResult result = bo.updateFieldRequest(aVo, aFieldName, aValue, con);
            if (result.getNbrRecords() >= 0 || result.isStatusNOTHING_TODO()) {
                con.commit();
            }
            return result;
        } catch (SQLException e) {
            throw new ISException(e);
        }
    }

    /**
     *
     * @param aType
     *            nom de l'objet métier
     * @param checkResult
     *            true s'il faut tester le résultat
     * @param valueObject
     *            vo de l'objet métier
     * @return true si failure
     */
    protected boolean executeShell(String aType, boolean checkResult, IValueObject valueObject) {
        if (aType == null) {
            return true;
        }
        boolean failure = false;
        String dir = iContextManager.getProperty("shell.dir") + File.separator;
        StringBuilder sb = new StringBuilder();
        for (ShellInfo info : iVOInfo.getShellInfo()) {
            if (info.getTypeShell().equals(aType)) {
                sb.setLength(0);
                sb.append(dir);
                sb.append(info.getNameShell());
                if (info.getTypeParameters() != null) {
                    for (String str : StringTools.split(info.getTypeParameters(), ",")) {
                        sb.append(" ");
                        sb.append(valueObject.getProperty(str));
                    }
                }
                try {
                    ShellResult result = new GenericShellProcess(sb.toString(), null).execute();
                    if (checkResult && result.getExitValue() != 0) {
                        failure = true;
                        break;
                    }
                } catch (Exception e) {
                    logger.error("Catching exception executing: " + sb, e);
                    if (checkResult) {
                        failure = true;
                        break;
                    }
                    Thread.currentThread().interrupt();
                }
            }
        }
        return failure;
    }

    @Override
    public void downloadBlob(Object aId, ILoggedUser aUser, DAOParameter... aParameters) throws ISException {
        // Implémenter dans la classe spécialisé
    }

    @Override
    public Collection<?> executeMethode(String aNameMethode, List<?> aVos, ILoggedUser aUser) throws ISException {
        IBusinessObject bo = iBOFactory.getBO(iName);
        DataSource ds = iContextManager.getDataSource(iDbObjectName);
        Collection<?> ret = null;
        try (Connection con = ds.getConnection()) {
            ret = bo.executeMethode(aNameMethode, aVos, aUser, con);
            con.commit();
        } catch (SQLException e) {
            throw new ISException(e);
        }
        return ret;
    }

    @Override
    public Object executeMethode(String aNameMethode, Object anObject, ILoggedUser aUser) throws ISException {
        IBusinessObject bo = iBOFactory.getBO(iName);
        DataSource ds = iContextManager.getDataSource(iDbObjectName);
        Object ret = null;
        try (Connection con = ds.getConnection()) {
            ret = bo.executeMethode(aNameMethode, anObject, aUser, con);
            con.commit();
        } catch (SQLException e) {
            throw new ISException(e);
        }
        return ret;
    }

    /**
     * Retourne la clé du tri par défaut
     */
    @Override
    public String getDefaultOrderKey() {
        return iBOFactory.getBO(iName).getDefaultOrderKey();
    }

    /**
     * Retourne l'orientation du tri par défaut
     */
    @Override
    public Sort getDefaultSortOrder() {
        return iBOFactory.getBO(iName).getDefaultSortOrder();
    }

    /**
     * @return liste des propriétés avec configuration DAOlist=true
     */
    @Override
    public List<String> getDAOList() {
        return iBOFactory.getBO(iName).getDAOList();
    }

    @Override
    public void setBOFactory(BOFactory aBOFactory) {
        iBOFactory = aBOFactory;
    }

    /**
     * @return le BOFactory pour mise à disposition des classes filles
     */
    @Override
    public BOFactory getBOFactory() {
        return iBOFactory;
    }

    /**
     * @return le Name pour mise à disposition des classes filles
     */
    public String getName() {
        return iName;
    }

    @Override
    public void setContextManager(IContextManager contextManager) {
        iContextManager = contextManager;

        // Prendre l'information de la connexion si elle est différente de la
        // connexion par défaut
        if (iVOInfo.getValue("connection") != null) {
            iDbObjectName = iContextManager.getProperty((String) iVOInfo.getValue("connection"));
            logger.info(iVOInfo.getName() + " datasource: " + iDbObjectName);
        }
    }

    /**
     * @return le contextmanager pour mise à disposition des classes filles
     */
    @Override
    public IContextManager getContextManager() {
        return iContextManager;
    }

    /**
     * @return le VOFactory pour mise à disposition des classes filles
     */
    @Override
    public IVOFactory getVOFactory() {
        return iVOFactory;
    }

    @Override
    public void setVOFactory(IVOFactory aVOFactory) {
        iVOFactory = aVOFactory;
    }

    @Override
    public IDAOResult delete(Object aId, Timestamp aTimestamp, ILoggedUser aUser, DAOParameter... aParameters) throws ISException {
        try (Connection con = iContextManager.getDataSource(iDbObjectName).getConnection()) {
            IDAOResult result = iBOFactory.getBO(iName).delete(aId, aTimestamp, con, aUser, aParameters);
            con.commit();
            return result;
        } catch (SQLException e) {
            throw new ISException(e);
        }
    }

    @Override
    public IDAOResult deleteCascade(Object aId, Timestamp aTimestamp, ILoggedUser aUser, DAOParameter... aParameters) throws ISException {
        IBusinessObject bo = iBOFactory.getBO(iName);
        IDAOResult result2 = new DAOResult();
        try (Connection con = iContextManager.getDataSource(iDbObjectName).getConnection()) {
            IDAOResult result1 = bo.deleteCascade(aId, aTimestamp, con, aUser, aParameters);

            // Message d'erreur sur les champs enfants
            if (!result1.isStatusOK() && !result1.isStatusNOTHING_TODO()) {
                return new DAOResult(Status.KO);
            }

            result2 = bo.delete(aId, aTimestamp, con, aUser, new DAOParameter(Name.TRANSACTION_ID, aParameters));
            con.commit();

            // Message d'erreur sur le champ maitre
            if (!result2.isStatusOK()) {
                return new DAOResult(Status.KO_PARENT);
            }

            // Retourne le nombre de champ supprimé
            return new DAOResult(result1.getNbrRecords() + result2.getNbrRecords());
        } catch (SQLException e) {
            throw new ISException(e);
        }
    }

    /**
     * Implementé uniquement par ch.inser.dynaplus.anonymous.BPAnonymous
     */

    @Override
    public IDAOResult getRecord(String aTable, String aField, Object aValue) throws ISException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    /**
     * Implementé uniquement par ch.inser.dynaplus.anonymous.BPAnonymous
     */

    @Override
    public IDAOResult getList(String aTable, IValueObject aQuery) throws ISException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    /**
     * Requête de count
     *
     * @param vo
     *            Value object contenant les critères de recherche
     * @return nbr d'enregistrement
     * @throws ISException
     *             erreur bd
     */
    @Override
    public IDAOResult getListCount(IValueObject vo, ILoggedUser aUser, DAOParameter... aParameters) throws ISException {
        try (Connection con = iContextManager.getDataSource(iDbObjectName).getConnection()) {
            return iBOFactory.getBO(iName).getListCount(vo, con, aUser, aParameters);
        } catch (SQLException e) {
            throw new ISException(e);
        }
    }

    /**
     *
     * @param aFormatEngine
     *            engine pour transformer des enregistrement dans un autre format
     */
    @Override
    public void setFormatEngine(IFormatEngine aFormatEngine) {
        iFormatEngine = aFormatEngine;
    }

}