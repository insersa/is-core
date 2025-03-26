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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.inser.dynamic.common.DAOParameter;
import ch.inser.dynamic.common.DAOParameter.Name;
import ch.inser.dynamic.common.DAOResult;
import ch.inser.dynamic.common.DynamicDAO.Aggregator;
import ch.inser.dynamic.common.DynamicDAO.Operator;
import ch.inser.dynamic.common.IContextManager;
import ch.inser.dynamic.common.IDAOResult;
import ch.inser.dynamic.common.IDAOResult.Status;
import ch.inser.dynamic.common.ILoggedUser;
import ch.inser.dynamic.common.IValueObject;
import ch.inser.dynamic.quality.IQualityController;
import ch.inser.dynamic.quality.IQualityTest;
import ch.inser.dynamic.quality.QCParameter;
import ch.inser.dynamic.util.ChildrenInfo;
import ch.inser.dynamic.util.Constants.EntityAction;
import ch.inser.dynamic.util.Constants.TierName;
import ch.inser.dynamic.util.DelCascadeInfo;
import ch.inser.dynamic.util.MultiselectInfo;
import ch.inser.dynamic.util.ParentInfo;
import ch.inser.dynamic.util.VOInfo;
import ch.inser.dynaplus.util.Constants.Mode;
import ch.inser.dynaplus.util.ServiceLocator;
import ch.inser.dynaplus.vo.IVOFactory;
import ch.inser.jsl.exceptions.ISException;
import ch.inser.jsl.list.ListHandler.Sort;
import ch.inser.jsl.tools.NumberTools;
import ch.inser.jsl.tools.PropertyTools;

/**
 * @author INSER SA
 * @version 1.0
 * @created 29-févr.-2008 08:53:09
 */
public abstract class AbstractBusinessObject implements IBusinessObject, Serializable {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 6302781336692548070L;

    /** Définition de la catégorie de logging */
    private static final Log logger = LogFactory.getLog(AbstractBusinessObject.class);

    /**
     * Nom de l'objet métier
     */
    private String iName;

    /**
     * Info de l'objet métier issu du fichier de configuration bo
     */
    protected VOInfo iVOInfo;

    /** DAO à disposition de l'objet métier */
    protected transient IDAODelegate iDao;

    /**
     * VOFactory à disposition de l'objet métier
     */
    private transient IVOFactory iVOFactory;

    /**
     * BOFactory à disposition de l'objet métier
     */
    private BOFactory iBOFactory;

    /**
     * ContextManager à disposition de l'objet métier
     */
    private transient IContextManager iContextManager;

    /** Tests de qualité BO avant création */
    protected String[] iTestCreate;

    /** Tests de qualité BO avant mise à jour */
    protected String[] iTestUpdate;

    /**
     * QualiytController, si injecté prend la main pour déterminer les tests à effectuer.
     */
    protected transient IQualityController iQualityController;

    /** Objet pour lancer des tests de qualité */
    protected transient IQualityTest iQualityTest;

    /**
     * BOFactory à disposition de l'objet métier
     */

    /**
     * Constructeur par défaut.
     *
     * @param name
     *            nom de l'objet métier
     * @param aVOInfo
     *            info issu de la configuration
     */
    protected AbstractBusinessObject(String name, VOInfo aVOInfo) {
        iName = name;
        iVOInfo = aVOInfo;

        // Vérifier si aVOInfo n'est pas null pour faire ces actions
        if (aVOInfo != null) {
            String tests = (String) aVOInfo.getValue("testscreate");
            if (tests != null) {
                iTestCreate = tests.split(",");
            }
            tests = (String) aVOInfo.getValue("testsupdate");
            if (tests != null) {
                iTestUpdate = tests.split(",");
            }
        }

    }

    // ---------------------------------------------------- Méthodes membres

    /**
     * {@inheritDoc}
     */
    @Override
    public IDAOResult getRecord(Object id, Connection con, ILoggedUser aUser, boolean aGetParent, DAOParameter... aParameters)
            throws SQLException {
        if (id == null) {
            return new DAOResult(Status.NOTHING_TODO);
        }

        // Effectue la requête d'un record et récupère le value object
        IDAOResult result = iDao.getRecord(id, aUser, con);
        if (!result.isStatusOK()) {
            return new DAOResult(Status.NOTHING_TODO);
        }

        // Do the parent job
        IValueObject vo = result.getValueObject();
        if (aGetParent) {
            for (ParentInfo parent : iVOInfo.getParents()) {
                IBusinessObject boParent = iBOFactory.getBO(parent.getName());
                IValueObject voParent = boParent.getRecord(vo.getProperty(parent.getMasterLink()), con, aUser, false).getValueObject();
                if (voParent == null) {
                    voParent = iVOFactory.getVO(parent.getName());
                }
                vo.setProperty(parent.getKeyName(), voParent);
            }
        }
        readFilter(vo.getProperties(), aUser);
        // End of parent job

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IDAOResult getRecordFull(Object id, Connection con, ILoggedUser aUser, boolean aGetParent, DAOParameter... aParameters)
            throws SQLException {
        if (id == null) {
            return new DAOResult(Status.NOTHING_TODO);
        }

        // Effectue la requête d'un record et récupère le value object
        IDAOResult result = iDao.getRecordFull(id, aUser, con);
        if (!result.isStatusOK()) {
            return new DAOResult(Status.NOTHING_TODO);
        }

        // Do the parent job
        IValueObject vo = result.getValueObject();
        if (aGetParent) {
            for (ParentInfo parent : iVOInfo.getParents()) {
                IBusinessObject boParent = iBOFactory.getBO(parent.getName());
                IValueObject voParent = boParent.getRecordFull(vo.getProperty(parent.getMasterLink()), con, aUser, false).getValueObject();
                if (voParent == null) {
                    voParent = iVOFactory.getVO(parent.getName());
                }
                vo.setProperty(parent.getKeyName(), voParent);
            }
        }
        readFilter(vo.getProperties(), aUser);
        // End of parent job

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IDAOResult update(IValueObject valueObject, Connection con, ILoggedUser user) throws SQLException {
        int rowCount = 0;

        // Lit le record qu'on est sur le point de mettre à jour
        IValueObject voOriginal = iDao.getRecord(valueObject.getId(), user, con).getValueObject();

        // Tester si le record à modifier n'a pas été trouvé
        if (voOriginal == null) {
            return new DAOResult(Status.NOT_FOUND);
        }

        // Tester si le timestamp est différent => le record a changé
        IDAOResult timestampCheckResult = checkTimestamp(voOriginal, valueObject);
        if (timestampCheckResult != null) {
            return timestampCheckResult;
        }

        // On fabrique la liste de test
        String[] testList = iTestUpdate;
        if (iQualityController != null) {
            QCParameter[] aParameters = new QCParameter[] { new QCParameter(QCParameter.Name.VALUEOBJECT, valueObject),
                    new QCParameter(QCParameter.Name.USER, user) };
            String[] testListQC = iQualityController.getRules(TierName.BO, EntityAction.UPDATE, iName, aParameters);
            if (testListQC != null) {
                testList = testListQC;
            }
        }

        // Tests de qualité
        if (testList != null && iQualityTest != null) {
            try {
                iQualityTest.check(testList, valueObject, user, con);
            } catch (ISException e) {
                throw new SQLException(e);
            }
        }

        // Comparaison avec le VO original et constitution d'une collection
        // Map des attributs à modifier.
        Map<String, Object> updateFields = voOriginal.getDiffProperties(valueObject);

        updateFields.keySet().retainAll(iDao.getListUpdateFields());

        writeFilter(updateFields, user);

        if (updateFields.isEmpty()) {
            return new DAOResult(Status.NOTHING_TODO);
        }

        // prendre valeur srid dans update fields si existant
        if (valueObject.getProperty("_srid") != null) {
            updateFields.put("_srid", valueObject.getProperty("_srid"));
        }

        // Effectue la mise à jour
        rowCount = iDao.update(updateFields, valueObject.getId(), valueObject.getTimestamp(), user, con);

        // On vérifie que l'utilisateur avait bien le droit d'effectuer cet
        // update
        IDAOResult result = iDao.getRecord(valueObject.getId(), user, con);
        if (result.getValueObject() == null) {
            // On arrive pas à le relire, il n'avait pas le droit...
            return new DAOResult(Status.NO_RIGHTS);
        }

        // Add the multiselect things
        for (MultiselectInfo multiselect : iVOInfo.getMultiselects()) {
            try {
                updateMultiselected(multiselect, valueObject.getId(), (List<?>) valueObject.getProperty(multiselect.getLinkName()), user,
                        con);
            } catch (ISException e) {
                throw (SQLException) e.getCause();
            }
        }

        logger.debug("return : " + rowCount);
        return result;
    }

    protected IDAOResult checkTimestamp(IValueObject originalValueObject, IValueObject aValueObject) {
        if (originalValueObject.getTimestamp() != null && !originalValueObject.getTimestamp().equals(aValueObject.getTimestamp())) {
            logger.debug(originalValueObject.getTimestamp() + "!=" + aValueObject.getTimestamp());
            return new DAOResult(Status.CHANGED_TIMESTAMP);
        }
        return null;
    }

    /**
     * Update utilisé pour le delete cascade, lorsqu'on désire mettre à null la foreign key. Le timestamp n'est pas contrôlé avec cette
     * méthode interne
     *
     * @param valueObject
     *            valueObject de l'update
     * @param con
     *            connection à la source de données
     * @param user
     *            utilisateur effectuant la requête
     * @param aDao
     *            la dao delegate à utiliser
     * @return le nombre de ligne de ligne updaté
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    private int update(IValueObject valueObject, Connection con, ILoggedUser user, IDAODelegate aDao) throws SQLException {
        IDAODelegate dao = aDao;
        int rowCount = 0;

        // Lit le record qu'on est sur le point de mettre à jour
        IValueObject voOriginal = dao.getRecord(valueObject.getId(), user, con).getValueObject();

        // Tester si le record à modifier n'a pas été trouvé
        if (voOriginal == null) {
            return -1;
        }

        // Comparaison avec le VO original et constitution d'une collection
        // Map des attributs à modifier.
        Map<String, Object> updateFields = voOriginal.getDiffProperties(valueObject);

        // Effectue la mise à jour
        rowCount = dao.update(updateFields, valueObject.getId(), valueObject.getTimestamp(), user, con);

        // On vérifie que l'utilisateur avait bien le droit d'effectuer cet
        // update
        if (user.getAdditionnalClause(iName, ch.inser.dynamic.common.DynamicDAO.Mode.SELECT) != null
                && !"".equals(user.getAdditionnalClause(iName, ch.inser.dynamic.common.DynamicDAO.Mode.SELECT))
                && dao.getRecord(valueObject.getId(), user, con).getValueObject() == null) {
            // On arrive pas à le relire, il n'avait pas le droit...
            return -4;
        }
        // Add the multiselect things
        for (MultiselectInfo multiselect : iVOInfo.getMultiselects()) {
            try {
                updateMultiselected(multiselect, valueObject.getId(), (List<?>) valueObject.getProperty(multiselect.getLinkName()), user,
                        con);
            } catch (ISException e) {
                throw (SQLException) e.getCause();
            }
        }
        logger.debug("return : " + rowCount);
        return rowCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int updateFieldsRow(IValueObject valueObject, Connection con, ILoggedUser user) throws SQLException {
        int rowCount = 0;

        // Lit le record qu'on est sur le point de mettre à jour
        IValueObject voOriginal = iDao.getRecord(valueObject.getId(), user, con).getValueObject();

        // Tester si le record à modifier n'a pas été trouvé
        if (voOriginal == null) {
            return -1;
        }

        // Tester si le timestamp est différent => le record a changé
        if (!voOriginal.getTimestamp().equals(valueObject.getTimestamp())) {
            logger.debug(voOriginal.getTimestamp() + "!=" + valueObject.getTimestamp());
            return -2;
        }

        // Comparaison avec le VO original et constitution d'une collection
        // Map des attributs à modifier.
        Map<String, Object> updateFields = new HashMap<>();

        for (Map.Entry<String, Object> prop : valueObject.getProperties().entrySet()) {
            if (PropertyTools.arePropertiesNotEqual(prop.getValue(), voOriginal.getProperty(prop.getKey()))) {
                updateFields.put(prop.getKey(), prop.getValue());
            }
        }

        updateFields.keySet().retainAll(iDao.getListUpdateFields());

        writeFilter(updateFields, user);

        if (updateFields.isEmpty()) {
            return -3;
        }

        // prendre valeur srid dans update fields si existant
        if (valueObject.getProperty("_srid") != null) {
            updateFields.put("_srid", valueObject.getProperty("_srid"));
        }

        // Effectue la mise à jour
        rowCount = iDao.update(updateFields, valueObject.getId(), valueObject.getTimestamp(), user, con);

        // On vérifie que l'utilisateur avait bien le droit d'effectuer cet
        // update
        if (user.getAdditionnalClause(iName, ch.inser.dynamic.common.DynamicDAO.Mode.SELECT) != null
                && !"".equals(user.getAdditionnalClause(iName, ch.inser.dynamic.common.DynamicDAO.Mode.SELECT))
                && iDao.getRecord(valueObject.getId(), user, con).getValueObject() == null) {
            // Pas nécessaire de relire si on a tous les droits!
            // On arrive pas à le relire, il n'avait pas le droit...
            return -4;
        }

        logger.debug("return : " + rowCount);
        return rowCount;
    }

    @Override

    /**
     * {@inheritDoc}
     */
    public IDAOResult update(List<IValueObject> aLstValueObject, Connection aCon, ILoggedUser aUser) throws SQLException {
        List<IValueObject> results = new ArrayList<>(aLstValueObject.size());
        for (IValueObject vo : aLstValueObject) {
            IValueObject voBackup = (IValueObject) vo.clone();
            IDAOResult result = update(vo, aCon, aUser);
            if (!result.isStatusOK()) {
                if (!result.isStatusNOTHING_TODO()) {
                    return result;
                }
            } else {
                results.add(result.getValueObject());
            }
            // Modification des enfants
            IDAOResult resultChildren = loadChildrenObjects(voBackup, vo, aCon, aUser);
            if (!resultChildren.isStatusOK() && !resultChildren.isStatusNOTHING_TODO()) {
                return resultChildren;
            }
            if (result.isStatusNOTHING_TODO() && resultChildren.isStatusOK()) {
                results.add(result.getValueObject());
            }
        }

        return new DAOResult(results);
    }

    /**
     * Va réaliser la mise à jour à null de la foreign key, utilisé par le deleteCascade, lorsque le deletecascade ne supprime pas
     * l'enfants.
     *
     * @param voIds
     *            liste des ids des enregistrements à modifier
     * @param con
     *            connection à la source de données
     * @param user
     *            utilisateur effectuant la requête
     * @param aDao
     *            dau delegate à utiliser
     * @param aDelcascade
     *            les paramètres de l'application pour le deletecascade
     * @return le nombre d'enregistrements modifiés
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    private int updateNullCascade(List<IValueObject> voIds, Connection con, ILoggedUser user, IDAODelegate aDao, DelCascadeInfo aDelcascade)
            throws SQLException {
        int ret = 0;
        // Passage sur tous les ids
        for (IValueObject voId : voIds) {
            if (ret >= 0) {
                // Mise à null de la foreign key
                IBusinessObject bo = iBOFactory.getBO(aDelcascade.getName());
                voId = bo.getRecord(voId.getId(), con, user, false).getValueObject();
                voId.setProperty(aDelcascade.getForeignKey(), null);
                // Update du champ
                int retint2 = update(voId, con, user, aDao);
                // Pour le message d'erreur si le champ n'est pas supprimé
                if (retint2 == 0) {
                    return -1;
                }
                // Addition de nombres de champ supprimés
                ret += retint2;
            }

        }
        return ret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IDAOResult create(IValueObject valueObject, Connection con, ILoggedUser user) throws SQLException {
        if (valueObject == null) {
            return new DAOResult(Status.NOTHING_TODO);
        }

        // On fabrique la liste de test
        String[] testList = iTestUpdate;
        if (iQualityController != null) {
            QCParameter[] aParameters = new QCParameter[] { new QCParameter(QCParameter.Name.VALUEOBJECT, valueObject),
                    new QCParameter(QCParameter.Name.USER, user) };
            String[] testListQC = iQualityController.getRules(TierName.BO, EntityAction.CREATE, iName, aParameters);
            if (testListQC != null) {
                testList = testListQC;
            }
        }

        // Tests de qualité
        if (testList != null && iQualityTest != null) {
            try {
                iQualityTest.check(testList, valueObject, user, con);
            } catch (ISException e) {
                throw new SQLException(e);
            }
        }

        // Get the informations for multiselect
        Map<String, List<?>> multiselects = new HashMap<>();
        for (MultiselectInfo multiselect : iVOInfo.getMultiselects()) {
            multiselects.put(multiselect.getSelectName(), (List<?>) valueObject.getProperty(multiselect.getSelectName()));
        }
        Object id = valueObject.getId();

        // Donner le ID...
        if (id == null) {
            id = iDao.getNextId(user, con);
            if (id != null) {
                valueObject.setId(id);
            }
        }

        // Crée un record
        IDAOResult result = iDao.create(valueObject, user, con);

        // On vérifie que l'utilisateur avait bien le droit d'effectuer cet
        // insert
        if (id != null) {
            IValueObject rec = iDao.getRecord(id, user, con).getValueObject();
            if (rec == null) {
                // La sécurité indique qu'il n'avait pas le droit de faire
                // cette opération
                result.setStatus(Status.NO_RIGHTS);
            }
            result.setValueObject(rec);
        }

        // Add the multiselect things
        for (MultiselectInfo multiselect : iVOInfo.getMultiselects()) {
            try {
                updateMultiselected(multiselect, id, multiselects.get(multiselect.getSelectName()), user, con);
            } catch (ISException e) {
                throw (SQLException) e.getCause();
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IDAOResult create(List<IValueObject> valueObjects, Connection con, ILoggedUser user) throws SQLException {
        List<IValueObject> results = new ArrayList<>(valueObjects.size());
        for (IValueObject valueObject : valueObjects) {
            IValueObject voBackup = (IValueObject) valueObject.clone();
            IDAOResult result = create(valueObject, con, user);
            if (!result.isStatusOK()) {
                return result;
            }
            // Modification des enfants
            IDAOResult resultChildren = loadChildrenObjects(voBackup, valueObject, con, user);
            if (!resultChildren.isStatusOK() && !resultChildren.isStatusNOTHING_TODO()) {
                return resultChildren;
            }
            results.add(result.getValueObject());
        }
        return new DAOResult(results);
    }

    /**
     * Delete interne utilisé lors du delete cascade
     *
     * @param id
     *            identifiant de l'enregistrement à modifier
     * @param timestamp
     *            timestamp de l'enregistrement à modifier
     * @param con
     *            connection à la source de données
     * @param user
     *            utilisateur effectuant la requête
     * @param aDao
     *            dao delegate à utiliser
     * @return le nombre d'enregistrements supprimés
     * @throws SQLException
     *             en cas de problème au niveau base de données
     */
    private IDAOResult delete(Object id, Timestamp timestamp, Connection con, ILoggedUser user, IDAODelegate aDao) throws SQLException {
        if (id == null) {
            return new DAOResult(Status.NOTHING_TODO);
        }

        IDAOResult result = aDao.delete(id, timestamp, user, con);
        // Add the multiselect things
        for (MultiselectInfo multiselect : iVOInfo.getMultiselects()) {
            try {
                updateMultiselected(multiselect, id, null, user, con);
            } catch (ISException e) {
                throw (SQLException) e.getCause();
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public IDAOResult deleteCascade(Object aId, Timestamp aTimestamp, Connection aConnection, ILoggedUser aUser,
            DAOParameter... aParameters) throws ISException {
        if (aId == null) {
            return new DAOResult(Status.NOTHING_TODO);
        }

        int ret = 0;
        // Effacement des champs dans les tables enfants ayant comme
        // référence id
        try {
            for (DelCascadeInfo delcascade : iVOInfo.getDelCascade()) {
                // Seulement si l'option est activé dans le fichier de config du
                // bo
                if ("true".equalsIgnoreCase(delcascade.getActivate()) && ret >= 0) {
                    // Lit le record pour trouver la valeur de la primarykey
                    IValueObject voTableMaster = iDao.getRecord(aId, aUser, aConnection).getValueObject();
                    Object primaKeyValue = voTableMaster.getProperty(delcascade.getPrimaryKey());
                    if (primaKeyValue == null) {
                        return new DAOResult(Status.KO);
                    }

                    // Demander le dao de l'enfant
                    IDAODelegate dao = (IDAODelegate) ServiceLocator.getInstance().getLocator("dao").getService(delcascade.getName());
                    // Recherche de la liste des Ids
                    IValueObject vo = iVOFactory.getVO(delcascade.getName());

                    vo.setProperty(delcascade.getForeignKey(), primaKeyValue);
                    doDocumentFields(delcascade.getName(), vo);

                    int retint = 0;
                    String deleteOrUpdate;
                    if (delcascade.getDelete() == null) {
                        deleteOrUpdate = "true";
                    } else {
                        deleteOrUpdate = delcascade.getDelete();
                    }

                    Collection<?> ids = null;
                    ids = dao.getList(vo, aUser, aConnection, new DAOParameter(Name.SORT_INDEX, 1)).getListObject();
                    while (ids != null && !ids.isEmpty()) {
                        // insertion de true si delete est null

                        if ("false".equalsIgnoreCase(deleteOrUpdate)) {
                            // mise à null de la foreign key
                            retint = updateNullCascade((List<IValueObject>) ids, aConnection, aUser, dao, delcascade);
                        } else {
                            // Effacement des multi champ avec meme id
                            IDAOResult result;
                            result = deleteMultiCascade((List<IValueObject>) ids, aConnection, aUser, dao, delcascade);
                            if (result.isStatusOK()) {
                                retint = result.getNbrRecords();
                            } else {
                                retint = result.getStatus().getNumber();
                            }
                        }

                        // Pour le message d'erreur si aucun champ n'est
                        // supprimé
                        if (retint < 0) {
                            return new DAOResult(Status.NOTHING_TODO);
                        }
                        // Addition des nombres de champ effacés
                        ret += retint;
                        ids = dao.getList(vo, aUser, aConnection, new DAOParameter(Name.SORT_INDEX, 1)).getListObject();
                    }

                } else {
                    ret = -2;
                }
            }
        } catch (SQLException e) {
            throw new ISException(e);
        }

        return new DAOResult(ret);
    }

    /**
     *
     * @param aChildrenName
     *            nom de l'objet enfant document à supprimer par cascade
     * @param aVo
     *            vo requête pour chercher les enfants
     */
    protected void doDocumentFields(String aChildrenName, IValueObject aVo) {
        if ("Document".equals(aChildrenName)) {
            aVo.setProperty("doc_obj_name", iVOInfo.getName());
        }
    }

    /**
     * Supprimer plusieurs champs sans vérification du timestamp, fonction utilisée par le delete cascade permettant de réaliser la
     * suppression sur les tables enfants ainsi que sur les enfants des enfants par récursivité
     *
     * @param voIds
     *            List des VO
     * @param con
     *            Connection à la base de données
     * @param user
     *            Information sur l'utilisateur
     * @param aDao
     *            Selection du DAO à utiliser
     * @param aDelcascade
     *            information issu du fichier de paramètre
     * @return Retourne le nombre de champ supprimé
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     * @throws ISException
     *             problème au niveau delete cascade sur les enfants
     */
    private IDAOResult deleteMultiCascade(List<IValueObject> voIds, Connection con, ILoggedUser user, IDAODelegate aDao,
            DelCascadeInfo aDelcascade) throws SQLException, ISException {
        int ret = 0;
        // Passage sur tous les ids
        for (IValueObject voId : voIds) {
            if (ret >= 0) {
                // Supprimer les liens enfants
                IBusinessObject bo = iBOFactory.getBO(aDelcascade.getName());

                IDAOResult result1 = bo.deleteCascade(voId.getId(), bo.getTimestamp(voId.getId(), con, user).getTimestamp(), con, user,
                        DAOParameter.EMPTY_PARAMETER);
                // Message d'erreur si les champs enfant ne sont pas supprimés
                if (!result1.isStatusOK() && !result1.isStatusNOTHING_TODO()) {
                    return result1;
                }

                // Suppression du champ
                IDAOResult result2 = delete(voId.getId(), bo.getTimestamp(voId.getId(), con, user).getTimestamp(), con, user, aDao);
                // Pour le message d'erreur si le champ n'est pas supprimé
                if (!result2.isStatusOK()) {
                    return result2;
                }

                // Addition de nombres de champ supprimés
                ret += result1.getNbrRecords() + result2.getNbrRecords();
            }

        }
        return new DAOResult(ret);
    }

    @Override
    public IDAOResult deleteMulti(List<Object> aIds, Connection aConnection, ILoggedUser aUser, DAOParameter... aParameters)
            throws ISException {
        int ret = 0;
        // Add the multiselect things
        for (Object id : aIds) {
            try {
                ret += delete(id, getTimestamp(id, aConnection, aUser).getTimestamp(), aConnection, aUser, aParameters).getNbrRecords();
            } catch (SQLException e) {
                throw new ISException(e);
            }
        }
        return new DAOResult(ret);
    }

    @Override
    public IDAOResult deleteMultiQuery(List<IValueObject> aVos, Connection aConnection, ILoggedUser aUser, DAOParameter... aParameters)
            throws ISException {
        try {
            int ret = 0;
            for (IValueObject vo : aVos) {
                for (IValueObject voDel : getList(vo, aUser, aConnection, new DAOParameter(Name.SORT_INDEX, 0)).getListObject()) {
                    voDel = getRecord(voDel.getId(), aConnection, aUser, false).getValueObject();
                    ret = ret + delete(voDel.getId(), voDel.getTimestamp(), aConnection, aUser, aParameters).getNbrRecords();
                }
            }
            return new DAOResult(ret);
        } catch (SQLException e) {
            throw new ISException(e);
        }
    }

    @Override
    public IDAOResult deleteQuery(IValueObject aVo, Connection aConnection, ILoggedUser aUser, DAOParameter... aParameters)
            throws ISException {
        try {
            int ret = 0;
            Long id;
            Object timestamp;
            for (Object value : getFieldsRequest(aVo, aVo.getVOInfo().getId(), aConnection).getListValue()) {
                id = NumberTools.getLong(value);
                timestamp = getField(id, aVo.getVOInfo().getTimestamp(), aConnection).getValue();
                if (timestamp != null) {
                    ret = ret + delete(id, (Timestamp) timestamp, aConnection, aUser, aParameters).getNbrRecords();
                }
            }
            return new DAOResult(ret);
        } catch (SQLException e) {
            throw new ISException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IDAOResult getTimestamp(Object id, Connection con, ILoggedUser user) throws SQLException {
        if (id == null) {
            return new DAOResult(Status.NOTHING_TODO);
        }
        return iDao.getTimestamp(id, user, con);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IDAOResult getField(Object id, String aFieldName, Connection con) throws SQLException {
        return iDao.getField(id, aFieldName, con);
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public IDAOResult getAggregateField(IValueObject aVo, String aFieldName, Aggregator aggr, Connection aConnection) throws SQLException {
        return iDao.getAggregateField(aVo, aFieldName, aggr, aConnection);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IDAOResult updateField(Object id, String aFieldName, Object aValue, Connection con) throws SQLException {
        IDAOResult result = iDao.updateField(id, aFieldName, aValue, con);
        result.setValue(getField(id, aFieldName, con).getValue());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IDAOResult updateFieldRequest(IValueObject aVo, String aFieldName, Object aValue, Connection con) throws SQLException {
        IDAOResult result = iDao.updateFieldRequest(aVo, aFieldName, aValue, con);
        result.setList(getFieldsRequest(aVo, aFieldName, con).getList());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int updateFieldsRequest(IValueObject aVo, String[] aFieldNames, Object[] aValues, Connection con) throws SQLException {
        return iDao.updateFieldsRequest(aVo, aFieldNames, aValues, con);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IDAOResult updateFields(Object id, String[] aFieldNames, Object[] aValues, Connection con) throws SQLException {
        iDao.updateField(id, aFieldNames, aValues, con);

        Object[] results = new Object[aFieldNames.length];
        for (int i = 0; i < aFieldNames.length; i++) {
            results[i] = getField(id, aFieldNames[i], con).getValue();
        }
        return new DAOResult(results);
    }

    @Override
    public IDAOResult updateFields(IValueObject aValueObject, Connection aConnection, ILoggedUser aUser) throws ISException {
        // To implement using updateFieldsRow as base and adding

        // Lit le record qu'on est sur le point de mettre à jour
        IValueObject voOriginal;
        try {
            voOriginal = iDao.getRecord(aValueObject.getId(), aUser, aConnection).getValueObject();

            // Tester si le record à modifier n'a pas été trouvé
            if (voOriginal == null) {
                return new DAOResult(Status.NOT_FOUND);
            }

            // Tester si le timestamp est différent => le record a changé
            if (!voOriginal.getTimestamp().equals(aValueObject.getTimestamp())) {
                logger.debug("Original timestamp " + voOriginal.getTimestamp() + "!=" + "update timestamp " + aValueObject.getTimestamp());
                return new DAOResult(Status.CHANGED_TIMESTAMP);
            }

            // Operator.IS_NULL
            List<String> nullFields = new ArrayList<>();
            for (Map.Entry<String, Object> prop : ((IValueObject) aValueObject.clone()).getProperties().entrySet()) {
                if (!Operator.IS_NULL.equals(prop.getValue())) {
                    continue;
                }
                if (voOriginal.getProperty(prop.getKey()) != null) {
                    nullFields.add(prop.getKey());
                }
                aValueObject.removeProperty(prop.getKey());
            }

            // Tests de qualité
            IValueObject voUpdate = (IValueObject) voOriginal.clone();
            voUpdate.putProperties(aValueObject);

            // On fabrique la liste de test
            String[] testList = iTestUpdate;
            if (iQualityController != null) {
                QCParameter[] aParameters = new QCParameter[] { new QCParameter(QCParameter.Name.VALUEOBJECT, voUpdate),
                        new QCParameter(QCParameter.Name.USER, aUser) };
                String[] testListQC = iQualityController.getRules(TierName.BO, EntityAction.PATCH, iName, aParameters);
                if (testListQC != null) {
                    testList = testListQC;
                }
            }

            if (testList != null && iQualityTest != null) {
                for (String nullField : nullFields) {
                    voUpdate.removeProperty(nullField);
                }
                iQualityTest.check(testList, voUpdate, aUser, aConnection);
            }

            // Comparaison avec le VO original et constitution d'une collection
            // Map des attributs à modifier.
            Map<String, Object> updateFields = new HashMap<>();
            for (Map.Entry<String, Object> prop : voUpdate.getProperties().entrySet()) {
                if (PropertyTools.arePropertiesNotEqual(prop.getValue(), voOriginal.getProperty(prop.getKey()))) {
                    updateFields.put(prop.getKey(), Operator.IS_NULL.equals(prop.getValue()) ? null : prop.getValue());
                }
            }
            // Ajout des valeurs NULL
            for (String nullField : nullFields) {
                if (aValueObject.getProperty(nullField) == null && voOriginal.getProperty(nullField) != null) {
                    updateFields.put(nullField, null);
                }
            }

            updateFields.keySet().retainAll(iDao.getListUpdateFields());
            writeFilter(updateFields, aUser);
            if (updateFields.isEmpty()) {
                return new DAOResult(Status.NOTHING_TODO);
            }

            // Ajoute des champs métier selon besoin
            addBusinessFields(aValueObject, updateFields, aUser);

            // prendre valeur srid dans update fields si existant
            if (aValueObject.getProperty("_srid") != null) {
                updateFields.put("_srid", aValueObject.getProperty("_srid"));
            }

            // Effectue la mise à jour
            iDao.update(updateFields, aValueObject.getId(), aValueObject.getTimestamp(), aUser, aConnection);

            // On vérifie que l'utilisateur avait bien le droit d'effectuer cet
            // update
            if (aUser.getAdditionnalClause(iName, ch.inser.dynamic.common.DynamicDAO.Mode.SELECT) != null
                    && !"".equals(aUser.getAdditionnalClause(iName, ch.inser.dynamic.common.DynamicDAO.Mode.SELECT))
                    && iDao.getRecord(aValueObject.getId(), aUser, aConnection).getValueObject() == null) {
                // Pas nécessaire de relire si on a tous les droits!
                // On arrive pas à le relire, il n'avait pas le droit...
                return new DAOResult(Status.NO_RIGHTS);
            }
            IDAOResult result = iDao.getRecord(aValueObject.getId(), aUser, aConnection);
            // ici il se peut qu'un mot de passe soit affiché dans les
            // logs! Pa exemple lorsque l'on change le mot de passe d'un
            // utilisateur (il est affiché crypté!...)
            logger.debug("return : " + result.getValueObject());
            return result;

        } catch (SQLException e) {
            throw new ISException(e);
        }
    }

    /**
     * Ajoute des champs métier
     *
     * @param aValueObject
     *            vo avec champs édités
     * @param aUpdateFields
     *            liste de champs à modifier à passer au DAO
     * @param aUser
     *            utilisateur
     */
    @SuppressWarnings("unused")
    protected void addBusinessFields(IValueObject aValueObject, Map<String, Object> aUpdateFields, ILoggedUser aUser) {
        // Implémenter dans l'objet métier selon besoin
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IDAOResult updateField(List<Object> aLstId, String aFieldName, List<Object> aLstValue, Connection aCon, ILoggedUser aUser)
            throws SQLException {
        // Vérifie si les listes sont bien de la même taille
        if (aLstId.size() != aLstValue.size()) {
            return new DAOResult(Status.DIFFERENT_SIZE);
        }

        // Parcourt la liste pour updater le champ pour chaque ligne
        List<Object> results = new ArrayList<>(aLstId.size());
        for (int i = 0; i < aLstId.size(); i++) {
            results.add(updateField(aLstId.get(i), aFieldName, aLstValue.get(i), aCon).getValue());
        }

        return new DAOResult(results);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int updateFields(List<Object> ids, String aFieldName, Object aValue, Connection con) throws SQLException {
        return iDao.updateFields(ids, aFieldName, aValue, con);
    }

    // ----------------------------------------- Méthodes d'initialisation de VO
    /**
     * {@inheritDoc}
     */
    @Override
    public IValueObject getInitVO(ILoggedUser user, Connection con, boolean getParent) throws SQLException {
        IValueObject vo = iVOFactory.getVO(iName);
        // Do the parent job
        if (getParent) {
            for (ParentInfo parent : iVOInfo.getParents()) {
                IBusinessObject boParent = iBOFactory.getBO(parent.getName());
                vo.setProperty(parent.getKeyName(), boParent.getInitVO(user, con, false));
            }
        }
        // End of parent job
        return vo;
    }

    @Override
    public IValueObject getInitVO(Mode mode, ILoggedUser user, Connection con, boolean getParent) throws SQLException {
        IValueObject vo = getInitVO(user, con, getParent);
        if (mode == Mode.search || mode == Mode.searchlist) {
            initChildrenSearch(vo);
        }
        return vo;
    }

    /**
     * Ajoute un vo vide pour chaque type d'enfant
     *
     * @param aVo
     *            le initVo du parent
     */
    private void initChildrenSearch(IValueObject aVo) {
        List<ChildrenInfo> children = iVOInfo.getChildrens();
        Map<String, IValueObject> childrenMap = new HashMap<>();

        for (ChildrenInfo child : children) {
            IValueObject voChild = getVOFactory().getVO(child.getChildrenName());
            childrenMap.put(child.getChildrenName(), voChild);
        }
        aVo.setProperty("childrenmap", childrenMap);
    }

    @Override
    public IDAOResult getList(IValueObject aVo, ILoggedUser aUser, Connection aConnection, DAOParameter... aParameters) throws ISException {
        return iDao.getList(aVo, aUser, aConnection, aParameters);
    }

    /**
     * Return the actual multiselected of a multiselected field
     *
     * @param multiselect
     *            multiselect info
     * @param voId
     *            id de l'objet métier
     * @param user
     *            utilisateur
     * @param con
     *            connexion
     * @return liste de multiselect infos
     * @throws ISException
     *             erreur de récuperation des enregistrements sélectionnées
     */
    private List<?> getMultiselected(MultiselectInfo multiselect, Object voId, ILoggedUser user, Connection con) throws ISException {
        IBusinessObject boSelected = iBOFactory.getBO(multiselect.getLinkName());
        IValueObject voSelected = iVOFactory.getVO(multiselect.getLinkName());
        voSelected.setProperty(multiselect.getMasterLink(), voId);
        Collection<?> colSelected = boSelected.getList(voSelected, user, con).getListObject();
        List<Object> selSelected = new ArrayList<>();
        if (colSelected != null && !colSelected.isEmpty()) {
            Iterator<?> itSelected = colSelected.iterator();
            for (int i = 0; i < colSelected.size(); i++) {
                IValueObject objVO = (IValueObject) itSelected.next();
                // Adding the effective selected...
                selSelected.add(objVO.getProperty(multiselect.getSelectLink()));
            }
        }
        return selSelected;
    }

    /**
     * update the multiselect informations
     *
     * @param multiselect
     *            multiselect info
     * @param voId
     *            id de l'objet métier
     * @param aList
     *            list
     * @param user
     *            utilisateur
     * @param con
     *            connexion
     * @throws SQLException
     *             erreur db
     * @throws ISException
     *             erreur de récuperation de la liste d'enregistrements
     */
    protected void updateMultiselected(MultiselectInfo multiselect, Object voId, List<?> aList, ILoggedUser user, Connection con)
            throws SQLException, ISException {
        List<?> oldList = getMultiselected(multiselect, voId, user, con);
        List<?> newList = aList;
        if (newList == null) {
            newList = new ArrayList<>();
        }
        IBusinessObject boSelected = iBOFactory.getBO(multiselect.getLinkName());
        IValueObject voSelected = iVOFactory.getVO(multiselect.getLinkName());
        // Delete operations
        for (Object id : oldList) {
            if (!newList.contains(id)) {
                voSelected.setProperty(multiselect.getMasterLink(), voId);
                voSelected.setProperty(multiselect.getSelectLink(), id);
                Collection<?> col = boSelected.getList(voSelected, user, con).getListObject();
                Iterator<?> it = col.iterator();
                while (it.hasNext()) {
                    voSelected = (IValueObject) it.next();
                    try {
                        boSelected.delete(voSelected.getId(), voSelected.getTimestamp(), con, user, DAOParameter.EMPTY_PARAMETER);
                    } catch (ISException e) {
                        throw (SQLException) e.getCause();
                    }
                }
            }
        }

        // Create operations
        for (Object obj : newList) {
            if (!oldList.contains(obj)) {
                voSelected.setProperty(multiselect.getMasterLink(), voId);
                voSelected.setProperty(multiselect.getSelectLink(), obj);
                boSelected.create(voSelected, con, user);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<?> executeMethode(String aNameMethode, List<?> aVos, ILoggedUser aUser, Connection aConnection) throws ISException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object executeMethode(String aNameMethode, Object anObject, ILoggedUser aUser, Connection aConnection) throws ISException {
        return getDao().executeMethode(aNameMethode, anObject, aUser, aConnection);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void readFilter(Map<String, Object> aValues, ILoggedUser aUser) {
        for (String str : aUser.getNoReadFields(iName)) {
            aValues.remove(str);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeFilter(Map<String, Object> aValues, ILoggedUser aUser) {
        for (String str : aUser.getNoWriteFields(iName)) {
            aValues.remove(str);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IDAOResult loadChildren(IValueObject valueObject, Connection con, ILoggedUser user) throws SQLException {
        return loadChildren(valueObject, valueObject, con, user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public IDAOResult loadChildren(IValueObject originalVo, IValueObject resultVo, Connection con, ILoggedUser user) throws SQLException {
        int ret = 0;
        if (originalVo.getVOInfo() == null) {
            return new DAOResult(Status.NOTHING_TODO);
        }

        Map<String, Object> mapChildrens = (Map<String, Object>) originalVo.getProperty("childrenlistvo");

        List<ChildrenInfo> lstchildrenInfo = originalVo.getVOInfo().getChildrens();

        // retour si pas de childrenlistvo dans vo
        if (mapChildrens == null) {
            return new DAOResult(Status.NOTHING_TODO);
        }

        // parcourir tous les enfants
        for (Map.Entry<String, Object> children : mapChildrens.entrySet()) {
            IBusinessObject bo = iBOFactory.getBO(children.getKey());
            Map<Integer, String> mapVOState = (Map<Integer, String>) ((Map<String, Object>) children.getValue()).get("mapVOState");
            List<Object> lstRemoveField = (List<Object>) ((Map<String, Object>) children.getValue()).get("lstRemoveField");
            List<?> listVO = (List<?>) ((Map<String, Object>) children.getValue()).get("ListVO");
            // Copie de la list vo (uniquement référence)
            List<IValueObject> cpListVO = new ArrayList<>();
            cpListVO.addAll((List<IValueObject>) listVO);

            // Création de différente liste pour séparer les update des
            // create
            List<IValueObject> lstVoCreate = new ArrayList<>();

            if (!mapVOState.isEmpty()) {

                // tour sur la liste des modifications
                for (Map.Entry<Integer, String> voState : mapVOState.entrySet()) {
                    // Create
                    if ("CREATE".equals(voState.getValue())) {
                        String nameId = null;
                        for (ChildrenInfo child : lstchildrenInfo) {
                            if (child.getChildrenName().equals(children.getKey())) {
                                nameId = child.getChildrenLink();
                            }
                        }

                        if (nameId != null) {
                            IValueObject vo = cpListVO.get(voState.getKey());

                            vo.setProperty(nameId, resultVo.getId());
                            lstVoCreate.add(vo);

                        }
                    }

                }

                // Exécution de la création
                ret += bo.create(lstVoCreate, con, user).getNbrRecords();
                cpListVO.removeAll(lstVoCreate);

            }

            // Modification des enfants
            for (IValueObject vo : cpListVO) {
                if (vo.getTimestamp() == null) {
                    IValueObject voBase = bo.getRecord(vo.getId(), con, user, false).getValueObject();
                    if (voBase != null) {
                        vo.setTimestamp(voBase.getTimestamp());
                    }
                }

            }
            IDAOResult updates = bo.update(cpListVO, con, user);
            if (updates.getNbrRecords() >= 0) {
                ret += updates.getNbrRecords();
            } else {
                return updates;
            }

            // Suppression des fichiers plus existant
            try {
                ret += bo.deleteMulti(lstRemoveField, con, user, DAOParameter.EMPTY_PARAMETER).getNbrRecords();
            } catch (ISException e) {
                throw (SQLException) e.getCause();
            }

        }

        logger.debug("Modification de " + ret + " éléments");
        return new DAOResult(ret);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IDAOResult loadChildrenObjects(IValueObject aVoOriginal, IValueObject aVoResult, Connection aCon, ILoggedUser aUser)
            throws SQLException {
        int res = 0;
        if (aVoOriginal.getVOInfo() == null) {
            return new DAOResult(Status.NOTHING_TODO);
        }
        if (aVoOriginal.getProperty("childrenlistObjects") == null) {
            return new DAOResult(Status.NOTHING_TODO);
        }

        for (Object obj : (List<?>) aVoOriginal.getProperty("childrenlistObjects")) {
            ChildrenlistObject children = (ChildrenlistObject) obj;
            IDAOResult result = deleteChildren(children, aCon, aUser);
            if (!result.isStatusOK() && !result.isStatusNOTHING_TODO()) {
                return result;
            }
            res += result.getNbrRecords();
            result = createChildren(children, aVoResult, aCon, aUser);
            if (!result.isStatusOK() && !result.isStatusNOTHING_TODO()) {
                return result;
            }
            res += result.getNbrRecords();
            result = updateChildren(children, aCon, aUser);
            if (!result.isStatusOK() && !result.isStatusNOTHING_TODO()) {
                return result;
            }
            res += result.getNbrRecords();
        }
        return new DAOResult(res);
    }

    /**
     * Met à jour des enfants selon updatelist dans childrenobject
     *
     * @param aChildren
     *            childrenobject
     * @param aCon
     *            connexion
     * @param aUser
     *            utilisateur
     * @return daoreult avec nbr d'enfants modifiés ou statut erreur
     * @throws SQLException
     *             erreur bd
     */
    protected IDAOResult updateChildren(ChildrenlistObject aChildren, Connection aCon, ILoggedUser aUser) throws SQLException {
        IBusinessObject bo = iBOFactory.getBO(aChildren.getObjectType());
        List<IValueObject> listUpdate = aChildren.getUpdateList();
        if (listUpdate != null) {
            for (IValueObject vo : listUpdate) {
                if (vo.getTimestamp() == null) {
                    IValueObject voBase = bo.getRecord(vo.getId(), aCon, aUser, false).getValueObject();
                    if (voBase != null) {
                        vo.setTimestamp(voBase.getTimestamp());
                    }
                }
            }
            return bo.update(listUpdate, aCon, aUser);
        }
        return new DAOResult(0);
    }

    /**
     * Crée des enfants selon createlist dans childrenobject
     *
     * @param aChildren
     *            childrenobject
     * @param aVo
     *            vo parent
     * @param aCon
     *            connexion
     * @param aUser
     *            utilisateur
     * @return nbr d'enfants créés
     * @throws SQLException
     *             erreur bd
     */
    protected IDAOResult createChildren(ChildrenlistObject aChildren, IValueObject aVo, Connection aCon, ILoggedUser aUser)
            throws SQLException {
        int count = 0;
        List<ChildrenInfo> lstchildrenInfo = aVo.getVOInfo().getChildrens();
        IBusinessObject bo = iBOFactory.getBO(aChildren.getObjectType());
        // Create
        if (aChildren.getCreateList() != null) {
            String nameId = null;
            for (ChildrenInfo child : lstchildrenInfo) {
                if (child.getChildrenName().equals(aChildren.getObjectType())) {
                    nameId = child.getChildrenLink();
                }
            }
            if (nameId != null) {
                List<IValueObject> listCreate = aChildren.getCreateList();
                for (IValueObject vo : listCreate) {
                    vo.setProperty(nameId, aVo.getId());
                }
                IDAOResult result = bo.create(listCreate, aCon, aUser);
                if (result.isStatusOK()) {
                    count += result.getNbrRecords();
                } else if (!result.isStatusNOTHING_TODO()) {
                    return result;
                }
            }
        }
        return new DAOResult(count);
    }

    /**
     * Supprime des enfants selon deletelist dans childrenobject
     *
     * @param aChildren
     *            childrenobject
     * @param aCon
     *            connexion
     * @param aUser
     *            utilisateur
     * @return nbr d'enfants supprimés
     * @throws SQLException
     *             erreur bd
     */
    protected IDAOResult deleteChildren(ChildrenlistObject aChildren, Connection aCon, ILoggedUser aUser) throws SQLException {
        int count = 0;
        if (aChildren.getDeleteList() != null) {
            try {
                IDAOResult result = iBOFactory.getBO(aChildren.getObjectType()).deleteMulti(aChildren.getDeleteList(), aCon, aUser,
                        DAOParameter.EMPTY_PARAMETER);
                if (result.isStatusOK()) {
                    count += result.getNbrRecords();
                } else if (!result.isStatusNOTHING_TODO()) {
                    return result;
                }
            } catch (ISException e) {
                throw (SQLException) e.getCause();
            }
        }
        return new DAOResult(count);
    }

    /**
     * Methode pour supprimer les enfants d'un objet métier sans passer par le delete cascade configurable. Utilisé par exemple pour des
     * suppressions logique où le méthode delete est surchargé dans chaque BO. Le delete cascade standard n'appelle pas le méthode delete du
     * BO mais du DAO.
     *
     * @param aId
     *            id du parent
     * @param aEntity
     *            nom de l'objet métier enfant
     * @param aLinkField
     *            nom du champ qui lie l'enfant au parent
     * @param aCon
     *            connexion
     * @param aUser
     *            utilisateur
     * @throws SQLException
     *             erreur de suppression
     * @throws ISException
     *             erreur de récuperation des enfants
     */
    protected void deleteChildren(Object aId, String aEntity, String aLinkField, Connection aCon, ILoggedUser aUser)
            throws SQLException, ISException {
        IBusinessObject bo = getBOFactory().getBO(aEntity);
        IValueObject qVo = getVOFactory().getVO(aEntity);
        qVo.setProperty(aLinkField, aId);
        List<?> children = bo.getList(qVo, aUser, aCon).getListObject();
        for (Object obj : children) {
            IValueObject child = (IValueObject) obj;
            IDAOResult result = bo.delete(child.getId(), child.getTimestamp(), aCon, aUser, DAOParameter.EMPTY_PARAMETER);
            if (!result.isStatusOK()) {
                throw new SQLException("Erreur de suppression de " + aEntity + ". ID:" + child.getId() + ". Res:" + result);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDefaultOrderKey() {
        return iDao.getDefaultOrderKey();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Sort getDefaultSortOrder() {
        return iDao.getDefaultSortOrder();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getDAOList() {
        return iDao.getDAOList();
    }

    /**
     * @return le dao pour mise à disposition des classes filles
     */
    @Override
    public IDAODelegate getDao() {
        return iDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDao(IDAODelegate aDao) {
        iDao = aDao;
    }

    /**
     * @return le VOFactory pour mise à disposition des classes filles
     */
    @Override
    public IVOFactory getVOFactory() {
        return iVOFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setVOFactory(IVOFactory aVOFactory) {
        iVOFactory = aVOFactory;
    }

    /**
     * @return le VOFactory pour mise à disposition des classes filles
     */
    @Override
    public BOFactory getBOFactory() {
        return iBOFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBOFactory(BOFactory aBOFactory) {
        iBOFactory = aBOFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setQualityController(IQualityController aController) {
        iQualityController = aController;
    }

    @Override
    public IDAOResult delete(Object aId, Timestamp aTimestamp, Connection aConnection, ILoggedUser aUser, DAOParameter... aParameters)
            throws ISException {
        if (aId == null) {
            return new DAOResult(Status.NOTHING_TODO);
        }

        try {
            // Lit le record qu'on est sur le point de mettre à jour
            IValueObject voOriginal = iDao.getRecord(aId, aUser, aConnection).getValueObject();

            // Tester si le record à modifier n'a pas été trouvé
            if (voOriginal == null) {
                return new DAOResult(Status.NOT_FOUND);
            }

            // Tester si le timestamp est différent => le record a changé
            if (voOriginal.getTimestamp() != null && !voOriginal.getTimestamp().equals(aTimestamp)) {
                logger.debug(voOriginal.getTimestamp() + "!=" + aTimestamp);
                return new DAOResult(Status.CHANGED_TIMESTAMP);
            }

            // Supprime le projet ce construction
            IDAOResult result = iDao.delete(aId, aTimestamp, aUser, aConnection);
            // Add the multiselect things
            for (MultiselectInfo multiselect : iVOInfo.getMultiselects()) {
                updateMultiselected(multiselect, aId, null, aUser, aConnection);
            }
            return result;
        } catch (SQLException e) {
            throw new ISException(e);
        }
    }

    /**
     * Implementé uniquement par ch.inser.dynaplus.anonymous.BOAnonymous
     */
    @Override
    public IDAOResult getRecord(String aTable, String aField, Object aValue, Connection aCon) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    /**
     * Implementé uniquement par ch.inser.dynaplus.anonymous.BOAnonymous
     */
    @Override
    public IDAOResult getList(String aTable, IValueObject aQuery, Connection aCon) throws SQLException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IDAOResult getList(String aTable, IValueObject aQuery, Integer aRowNum, Connection aCon) throws SQLException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IDAOResult getListCount(IValueObject vo, Connection connection, ILoggedUser aUser, DAOParameter... aParameters)
            throws SQLException {
        return iDao.getListCount(vo, connection, aUser, aParameters);
    }

    /**
     *
     * @return contextManager
     */
    @Override
    public IContextManager getContextManager() {
        return iContextManager;
    }

    /**
     *
     * @param aContextManager
     *            contextManager
     */
    @Override
    public void setContextManager(IContextManager aContextManager) {
        iContextManager = aContextManager;
        iQualityTest = iContextManager.getQualityTest();
    }

}