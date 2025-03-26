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

package ch.inser.dynaplus.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.inser.dynamic.common.DAOParameter;
import ch.inser.dynamic.common.DAOParameter.Name;
import ch.inser.dynamic.common.DAOResult;
import ch.inser.dynamic.common.IDAOResult;
import ch.inser.dynamic.common.ILoggedUser;
import ch.inser.dynamic.common.IValueObject;
import ch.inser.dynamic.common.IValueObject.Type;
import ch.inser.dynamic.util.ChildrenInfo;
import ch.inser.dynamic.util.JoinInfo;
import ch.inser.dynamic.util.VOInfo;
import ch.inser.jsl.exceptions.ISException;
import ch.inser.jsl.list.ListHandler.Sort;
import ch.inser.jsl.tools.StringTools;

/**
 * @author INSER SA
 * @version 1.0
 * @created 29-févr.-2008 08:53:11
 */
public class GenericDataAccessObject extends AbstractDataAcessObject {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 6337845570622426866L;

    /**
     * Objet pour les logs.
     */
    private static final Log logger = LogFactory.getLog(GenericDataAccessObject.class);

    /**
     * Le nom du DAO
     */
    private String iName;

    /**
     * vo config
     */
    private VOInfo iVOInfo;

    /**
     * Le nom de la table.
     */
    private String iTableName;

    /**
     * Les joins définis dans le fichier de configuration.
     */
    private Map<String, JoinInfo> iJoins;

    /**
     * Liste de toutes les tables, celles des joins et la principale
     */
    private Set<String> iAllTables;
    /**
     * Les conditions de toutes les jointures.
     */
    private Set<String> iAllJoinClauses;

    /**
     * Le nom de l'identifiant.
     */
    private String iIdName;

    /**
     * Les noms des ids des enregistrements.
     */
    private Set<String> iIdNames;

    /**
     * Le nom du timestamp.
     */
    private String iTimestampName;

    /**
     * Le type du timestamp.
     */
    private IValueObject.Type iTimestampType;

    /**
     * Le nom de l'auteur de la mise à jour.
     */
    private String iMajName;

    /**
     * Le nom de la sequende qui génère les id.
     */
    private String iSequence;

    /**
     * Le nom des attributs à rechercher pour créer les listes.
     */
    private List<String> iList;

    /**
     * Nom des tables des champs contenu dans les listes
     */
    private Collection<String> iListTables;

    /**
     * La liste des champs déclarés dans le fichier de config du DAO.
     */
    private Set<String> iListFields;

    /**
     * La liste des champs déclarés dans le fichier de config du DAO qui sont updatable.
     */
    private Set<String> iListUpdateFields;

    /**
     * Le nom des attributs à ne pas assigner à la création/modification d'un enregistrement.
     */
    private Set<String> iNoCreateUpdate;

    /**
     * Liste des champs pour les select, c'est utilisé lors de table conteant ces champs shap, la liste des champs sont 1) Les champs de la
     * table parent 2) Les champs des tables joins, il est nécessaire d'être correct dans le nom de la table dans les fichiers de config
     * (champ 'table'), ce champ est case sensitive
     */
    private Set<String> iSelectFields;

    /**
     * Les modes de tri disponibles.
     */
    private String[][] iSort;

    /**
     * Les orientations de tri disponibles pour les items liés à une sort key.
     */
    private Sort[][] iItemOrientations;

    /**
     * Les 'toggables' du tri pour les items liés à une sort key.
     */
    private Boolean[][] iItemToggables;

    /**
     * Mapping entre les sortKeys et leur position dans la lsite
     */
    private Map<String, Integer> iSortKeys;

    /**
     * Donne la clé du tri par défaut
     */
    private String iDefaultOrderKey;

    /**
     * Donne l'orientation du tri par défaut
     */
    private Sort iDefaultSortOrder;

    /**
     * Types des propriétés du vo
     */
    protected Map<String, Type> iTypes;

    /**
     * Constructeur de base
     *
     * @param name
     *            nom de l'objet métier
     * @param aVOInfo
     *            définition structurelle
     */
    public GenericDataAccessObject(String name, VOInfo aVOInfo) {
        iName = name;
        init(aVOInfo);
    }

    @Override
    public void init(VOInfo voInfo) {
        iVOInfo = voInfo;
        iTableName = voInfo.getTable();
        iJoins = voInfo.getJoins();
        iAllTables = voInfo.getTables();
        iAllJoinClauses = voInfo.getJoinsClauses();
        iIdName = voInfo.getId();
        iTimestampName = voInfo.getTimestamp();
        iTimestampType = voInfo.getTypes().get(iTimestampName);
        iMajName = voInfo.getUpdateUser();
        iSequence = (String) voInfo.getValue("sequence");
        iNoCreateUpdate = voInfo.getNoCreateUpdate();
        iSelectFields = voInfo.getSelectFields();
        iSort = voInfo.getSortArray();
        iItemOrientations = voInfo.getItemOrientationArray();
        iItemToggables = voInfo.getItemToggableArray();
        iSortKeys = voInfo.getSortKeys();
        iDefaultOrderKey = voInfo.getDefaultOrderKey();
        iDefaultSortOrder = voInfo.getDefaultSortOrder();
        iList = voInfo.getListsList();
        iListTables = voInfo.getTables(iList);
        iListTables.add(voInfo.getTable());
        iTypes = voInfo.getTypes();
        iIdNames = voInfo.getNames("search", "equal");
        iIdNames.add(iIdName);
        iListFields = voInfo.getNames();

        iListUpdateFields = new HashSet<>(iListFields);
        iListUpdateFields.removeAll(iNoCreateUpdate);

        setAttributeNameDBNameMaps(voInfo);
    }

    // ---PROTECTED GETTERS
    protected String getTableName() {
        return iTableName;
    }

    protected String getIdName() {
        return iIdName;
    }

    protected String getName() {
        return iName;
    }

    protected Map<String, JoinInfo> getJoins() {
        return iJoins;
    }

    protected Set<String> getSelectFields() {
        return iSelectFields;
    }

    // ---PROTECTED GETTERS END

    @Override
    public IDAOResult getRecord(Object id, ILoggedUser user, Connection connection) throws SQLException {
        String securityClause = user.getAdditionnalClause(iName, Mode.SELECT);
        if ("".equals(securityClause)) {
            securityClause = null;
        }

        // Utilisation des champs de sélection pour un getrecord avec shape
        if (iTypes.containsValue(Type.SHAPE)) {
            return getRecord(iIdName, id, iAllTables, iAllJoinClauses, iSelectFields, getVOFactory().getVO(iName), securityClause,
                    connection);
        }

        // getRecord * sans champ de sélection
        return getRecord(iIdName, id, iAllTables, iAllJoinClauses, null, getVOFactory().getVO(iName), securityClause, connection);
    }

    /**
     * getRecord avec la liste des champs à la place de *
     *
     * @param id
     *            primary key
     * @param user
     *            user
     * @param connection
     *            db connection
     * @return select statement avec les noms de champs au lieu de *
     * @throws SQLException
     *             erreur bd
     */
    @Override
    public IDAOResult getRecordFull(Object id, ILoggedUser user, Connection connection) throws SQLException {
        String securityClause = user.getAdditionnalClause(iName);
        if ("".equals(securityClause)) {
            securityClause = null;
        }

        // getRecord sans * sans champ de sélection
        return getRecord(iIdName, id, iAllTables, iAllJoinClauses, iSelectFields, getVOFactory().getVO(iName), securityClause, connection);
    }

    @Override
    public IDAOResult getList(IValueObject aVo, ILoggedUser aUser, Connection aConnection, DAOParameter... aParameters) throws ISException {
        // Prepare children
        prepareChildren(aVo);

        // Prepare parameters
        List<DAOParameter> params = new ArrayList<>();
        if (aParameters != null && aParameters.length > 0) {
            params.addAll(Arrays.asList(aParameters));
        }
        params.add(new DAOParameter(Name.USER, aUser));
        Set<String> tableNames = getTableNames(aVo);
        params.add(new DAOParameter(Name.TABLE_NAMES, tableNames));
        params.add(new DAOParameter(Name.JOIN_CLAUSES, getJoinClauses(tableNames)));
        params.add(new DAOParameter(Name.SECURITY_CLAUSE, getSecurityClause(aUser)));
        params.add(new DAOParameter(Name.ATTRIBUTES, iList));

        // Sort fields
        Integer sortIndex = getSortIndex(aParameters);
        params.add(new DAOParameter(Name.SORT_FIELDS, iSort[sortIndex - 1]));

        // Sort orientation
        if (DAOParameter.getValue(Name.SORT_ORIENTATION, aParameters) == null) {
            params.add(new DAOParameter(Name.SORT_ORIENTATION, getOrientation(sortIndex, aParameters)));
        }
        params.addAll(getSortItemParameters(sortIndex));

        try {
            return new DAOResult(getList(aVo, aConnection, params.toArray(new DAOParameter[params.size()])));
        } catch (SQLException e) {
            throw new ISException(e);
        }
    }

    /**
     * Récupere l'orientation de tri d'un champ
     *
     * @param aSortIndex
     *            index du champ de tri
     * @param aParameters
     *            dao parameters
     * @return objet Sort (par défaut ASCENDING)
     */
    private Sort getOrientation(int aSortIndex, DAOParameter... aParameters) {
        Sort orientation = (Sort) DAOParameter.getValue(Name.SORT_ORIENTATION, aParameters);
        if (orientation == null) {
            if (iItemOrientations != null && iItemOrientations[aSortIndex - 1][0] != null) {
                // Prise par défaut du 1er tri
                orientation = iItemOrientations[aSortIndex - 1][0];
            } else {
                // Si aucun tri n'existe
                orientation = Sort.ASCENDING;
            }
        }
        return orientation;
    }

    /**
     * Récupere l'index du champ de tri
     *
     * @param aParameters
     *            dao parameters
     * @return sort index, par défaut 1
     */
    private Integer getSortIndex(DAOParameter... aParameters) {
        Integer sortIndex = null;
        String paramKey = (String) DAOParameter.getValue(Name.SORT_KEY, aParameters);
        if (paramKey != null) {
            sortIndex = iSortKeys.get(paramKey);
            if (sortIndex == null) {
                logger.warn("Sortname doesn't exist in config! (" + paramKey + ")");
            }
        } else {
            Integer paramIndex = (Integer) DAOParameter.getValue(Name.SORT_INDEX, aParameters);
            if (paramIndex != null) {
                sortIndex = paramIndex;
            }
        }
        if (sortIndex == null || sortIndex > iSort.length || sortIndex < 1) {
            sortIndex = 1;
        }
        return sortIndex;
    }

    /**
     *
     * @param aSortType
     *            type de tri (n° de la clé de tri)
     * @return paramètres de tri (orientations et toggables de sort items) à la base de sort type
     */
    private List<DAOParameter> getSortItemParameters(int aSortType) {
        List<DAOParameter> params = new ArrayList<>();

        // Add sort item orientations as optional parameter
        if (iItemOrientations != null && iItemOrientations[aSortType - 1] != null) {
            params.add(new DAOParameter(Name.SORT_ORIENTATIONS, iItemOrientations[aSortType - 1]));
        }

        // Add sort item toggables as optional parameter
        if (iItemToggables != null && iItemToggables[aSortType - 1] != null) {
            params.add(new DAOParameter(Name.SORT_TOGGABLES, iItemToggables[aSortType - 1]));
        }
        return params;
    }

    /**
     *
     * @param aTableNames
     *            noms des tables dans la clause FROM
     * @return les jointures à mettre dans la clause WHERE
     */
    private Set<String> getJoinClauses(Set<String> aTableNames) {
        Set<String> joinClauses = iAllJoinClauses;
        if (isListOptimized()) {
            joinClauses = new HashSet<>();
            for (String str : aTableNames) {
                if (iJoins.get(str) != null) {
                    joinClauses.add(iJoins.get(str).getClause());
                }
            }
        }
        return joinClauses;
    }

    /**
     *
     * @param aVo
     *            vo pour construire un sql statement
     * @return noms de tables à mettre dans la clause FROM
     */
    private Set<String> getTableNames(IValueObject aVo) {
        Set<String> tableNames = iAllTables;
        if (isListOptimized()) {
            tableNames = new HashSet<>();
            tableNames.addAll(iVOInfo.getTables(aVo.getProperties().keySet()));
            tableNames.addAll(iListTables);
        }
        return tableNames;
    }

    /**
     *
     * @param aUser
     *            utilisateur
     * @return clause de sécurité de l'utilisateur (optionnel)
     */
    private String getSecurityClause(ILoggedUser aUser) {
        if (aUser == null) {
            return null;
        }
        String securityClause = aUser.getAdditionnalClause(iName, Mode.SELECT);
        if ("".equals(securityClause)) {
            securityClause = null;
        }
        return securityClause;
    }

    /**
     * Méthode de conout
     */

    @Override
    public IDAOResult getListCount(IValueObject vo, Connection connection, ILoggedUser aUser, DAOParameter... aParameters)
            throws SQLException {
        prepareChildren(vo);
        // Prepare parameters
        List<DAOParameter> params = new ArrayList<>();
        if (aParameters != null && aParameters.length > 0) {
            params.addAll(Arrays.asList(aParameters));
        }
        params.add(new DAOParameter(Name.USER, aUser));
        Set<String> tableNames = getTableNames(vo);
        params.add(new DAOParameter(Name.TABLE_NAMES, tableNames));
        params.add(new DAOParameter(Name.JOIN_CLAUSES, getJoinClauses(tableNames)));
        params.add(new DAOParameter(Name.SECURITY_CLAUSE, getSecurityClause(aUser)));
        return super.getListCount(vo, connection, params.toArray(new DAOParameter[params.size()]));
    }

    /**
     * Crée la requète SQL de l'enfant qui va fonctionner comme filtre dans le vo du parent
     *
     * @param aVo
     *            vo de l'enfant
     * @param aChildrenLink
     *            le nom du FK entre l'enfant et parent (<enf_par_id>)
     * @param aParentTable
     *            le nom de la table parent
     * @return la requète SQL "select <enf_par_id> from <enf-tables> where <enf-filtre>"
     */
    @Override
    public String getChildQuery(IValueObject aVo, String aChildrenLink, String aParentTable) {

        StringBuilder sql = new StringBuilder(512);

        sql.append("SELECT " + aChildrenLink + " FROM ");

        // Tables
        Set<String> tables = new HashSet<>(iAllTables);
        tables.remove(aParentTable);
        addNames(tables, sql);

        sql.append(" WHERE ");

        // Search criteria
        addClause(aVo, true, sql);

        // Additional statement
        if (aVo.getProperty("ADDITIONAL_STATEMENT") != null) {
            sql.append(" AND ");
            sql.append(aVo.getProperty("ADDITIONAL_STATEMENT"));
            aVo.removeProperty("ADDITIONAL_STATEMENT");
        }

        // Joins (without parent)
        Set<String> joins = new HashSet<>(iAllJoinClauses);
        if (!joins.isEmpty()) {

            JoinInfo parent = iJoins.get(aParentTable);
            if (parent != null) {
                joins.remove(parent.getClause());
            }

            if (!joins.isEmpty()) {
                sql.append(" AND ");
                sql.append(joinClause(joins));
            }
        }

        return sql.toString();
    }

    @Override
    public int update(Map<String, Object> updateFields, Object id, Timestamp timestamp, ILoggedUser user, Connection connection)
            throws SQLException {

        String securityClause = user.getAdditionnalClause(iName, Mode.UPDATE);
        if ("".equals(securityClause)) {
            securityClause = null;
        }
        return update(updateFields, iListFields, iNoCreateUpdate, iIdName, id, iTableName, iTimestampName, timestamp, iTypes, iMajName,
                iTypes.get(iMajName), user, securityClause, connection);
    }

    @Override
    public Object getNextId(ILoggedUser user, Connection connection) throws SQLException {

        return getNextId(iSequence, iTypes.get(iIdName), connection);
    }

    @Override
    public IDAOResult create(IValueObject vo, ILoggedUser user, Connection connection) throws SQLException {
        return create(vo, iTableName, iListFields, iNoCreateUpdate, iMajName, iTimestampName, user, connection);
    }

    @Override
    public IDAOResult delete(Object id, Timestamp timestamp, ILoggedUser user, Connection connection) throws SQLException {

        String securityClause = user.getAdditionnalClause(iName, Mode.DELETE);
        if ("".equals(securityClause)) {
            securityClause = null;
        }

        return delete(iTableName, iIdName, id, iTimestampName, timestamp, iTimestampType, securityClause, connection);

    }

    @Override
    public IDAOResult getTimestamp(Object id, ILoggedUser user, Connection connection) throws SQLException {
        return getTimestamp(iTimestampName, iTimestampType, iIdName, id, iTypes.get(iIdName), iTableName, connection);
    }

    @Override
    public IDAOResult getField(Object id, String aFieldName, Connection aConnection) throws SQLException {

        if (!iHasAttributeNamesMappedToDBNames) {
            return getField(iIdName, id, iTableName, aFieldName, iTypes.get(iIdName), iTypes.get(aFieldName), aConnection);
        }

        Type aTypeForFieldName = iTypes.get(getDBName(aFieldName));
        String aFieldNameDBName = getDBName(aFieldName);
        return getField(iIdName, id, iTableName, aFieldNameDBName, iTypes.get(iIdName), aTypeForFieldName, aConnection);
    }

    @Override
    public IDAOResult updateField(Object id, String aFieldName, Object aValue, Connection aConnection) throws SQLException {

        if (!iHasAttributeNamesMappedToDBNames) {
            return updateField(iIdName, id, iTypes.get(iIdName), iTableName, aFieldName, aValue, iTypes.get(aFieldName), aConnection);
        }

        String aFieldNameDBName = getDBName(aFieldName);
        return updateField(iIdName, id, iTypes.get(iIdName), iTableName, aFieldNameDBName, aValue, iTypes.get(aFieldNameDBName),
                aConnection);
    }

    @Override
    public IDAOResult updateFieldRequest(IValueObject aVo, String aFieldName, Object aValue, Connection aConnection) throws SQLException {
        String aFieldNameDBName = getDBName(aFieldName);
        return updateFieldRequest(aVo, iTableName, aFieldNameDBName, aValue, iTypes.get(aFieldNameDBName), aConnection);
    }

    @Override
    public int updateFieldsRequest(IValueObject aVo, String[] aFieldNames, Object[] aValues, Connection aConnection) throws SQLException {
        IValueObject.Type[] fieldTypes = new IValueObject.Type[aFieldNames.length];
        for (int i = 0; i < aFieldNames.length; i++) {
            fieldTypes[i] = iTypes.get(aFieldNames[i]);
        }
        return updateFieldsRequest(aVo, iTableName, aFieldNames, aValues, fieldTypes, aConnection);

    }

    /**
     * Actualise les valeurs de plusieurs champs pour l'enregistrement donné
     *
     * @param id
     *            identifiant de l'objet
     * @param aFieldNames
     *            noms des champs à modifier
     * @param aValues
     *            valeurs à inserer
     * @param aConnection
     *            connexion
     * @return nbr de records modifiés
     * @throws SQLException
     *             erreur bd
     */
    @Override
    public IDAOResult updateFields(Object id, String[] aFieldNames, Object[] aValues, Connection aConnection) throws SQLException {
        IValueObject.Type[] fieldTypes = new IValueObject.Type[aFieldNames.length];
        for (int i = 0; i < aFieldNames.length; i++) {
            fieldTypes[i] = iTypes.get(aFieldNames[i]);
        }
        return updateFields(iIdName, id, iTypes.get(iIdName), iTableName, aFieldNames, aValues, fieldTypes, aConnection);
    }

    @Override
    public int updateFields(Collection<Object> ids, String aFieldName, Object aValue, Connection aConnection) throws SQLException {

        if (!iHasAttributeNamesMappedToDBNames) {
            return updatFields(iIdName, ids, iTypes.get(iIdName), iTableName, aFieldName, aValue, iTypes.get(aFieldName), aConnection);
        }

        String aFieldNameDBName = getDBName(aFieldName);
        return updatFields(iIdName, ids, iTypes.get(iIdName), iTableName, aFieldNameDBName, aValue, iTypes.get(aFieldNameDBName),
                aConnection);
    }

    /**
     * Apply attribute and DB name mapping to all properties with attribute name values
     *
     * @param attributeDBNameMappingOfAllTablesMap
     *            Map containing the different attribute and DB name values of all tables
     */
    public void applyMapping(Map<String, String> attributeDBNameMappingOfAllTablesMap) {
        iAllJoinClauses = applyAttributeNameDBNameMappingForEqualExpression(iAllJoinClauses);

        iIdName = applyAttributeNameDBNameMappingForAttribute(attributeDBNameMappingOfAllTablesMap, iIdName);

        iTimestampName = applyAttributeNameDBNameMappingForAttribute(attributeDBNameMappingOfAllTablesMap, iTimestampName);

        iMajName = applyAttributeNameDBNameMappingForAttribute(attributeDBNameMappingOfAllTablesMap, iMajName);

        iNoCreateUpdate = applyAttributeNameDBNameMappingForSet(attributeDBNameMappingOfAllTablesMap, iNoCreateUpdate);

        iSort = applyAttributeNameDBNameMappingForSort(attributeDBNameMappingOfAllTablesMap, iSort);

        iList = applyAttributeNameDBNameMappingForList(attributeDBNameMappingOfAllTablesMap, iList);

        iSelectFields = applyAttributeNameDBNameMappingForSet(attributeDBNameMappingOfAllTablesMap, iSelectFields);

        iTypes = applyAttributeNameDBNameMappingForTypes(attributeDBNameMappingOfAllTablesMap, iTypes);

        iIdNames = applyAttributeNameDBNameMappingForSet(attributeDBNameMappingOfAllTablesMap, iIdNames);

    }

    /**
     * Set map with all different attribute and database field names and map with all different database field name and attribute names
     *
     * @param voInfo
     *            information about value object
     */
    private void setAttributeNameDBNameMaps(VOInfo voInfo) {
        iAttributeNameDBNameMap = voInfo.getAttributeNameDBNameMap();
        iDBNameAttributeNameMap = new HashMap<>();

        for (Entry<String, String> attributeNameDBNameEntry : iAttributeNameDBNameMap.entrySet()) {
            String attributeName = attributeNameDBNameEntry.getKey();
            String dbName = attributeNameDBNameEntry.getValue();

            iDBNameAttributeNameMap.put(dbName, attributeName);
        }
    }

    /**
     * Update information about attribute name and DB field name mapping, map of attribute name and database field name and map for database
     * field name and atribute name for current DAO
     *
     * @param attributeDBNameMappingOfAllTablesMap
     *            map with different attribute and DB name values for all DAOs
     * @param attributeName
     *            attribute name
     */
    private void updateAttributeNameAndDBNameMappingInfo(Map<String, String> attributeDBNameMappingOfAllTablesMap, String attributeName) {
        if (attributeDBNameMappingOfAllTablesMap.containsKey(attributeName)) {
            iHasAttributeNamesMappedToDBNames = true;

            String dbName = attributeDBNameMappingOfAllTablesMap.get(attributeName);
            iAttributeNameDBNameMap.computeIfAbsent(attributeName, k -> dbName);
            iDBNameAttributeNameMap.computeIfAbsent(dbName, k -> attributeName);
        }
    }

    /**
     * Apply attribute and DB field name mapping for an attribute
     *
     * @param attributeDBNameMappingOfAllTablesMap
     *            map with different attribute and DB name values for all DAOs
     * @param attributeName
     *            attribute name
     * @return attribute name resp. database field name
     */
    private String applyAttributeNameDBNameMappingForAttribute(Map<String, String> attributeDBNameMappingOfAllTablesMap,
            String attributeName) {
        updateAttributeNameAndDBNameMappingInfo(attributeDBNameMappingOfAllTablesMap, attributeName);

        return getDBName(attributeName);
    }

    /**
     * Apply attribute and DB field name mapping for a list of attribute names
     *
     * @param attributeDBNameMappingOfAllTablesMap
     *            map with different attribute and DB name values for all DAOs
     * @param attributeList
     *            list of attribute names
     * @return List of attribute names resp. database field names
     */
    private List<String> applyAttributeNameDBNameMappingForList(Map<String, String> attributeDBNameMappingOfAllTablesMap,
            List<String> attributeList) {
        List<String> newAttributeList = new ArrayList<>();

        for (String attributeName : attributeList) {
            updateAttributeNameAndDBNameMappingInfo(attributeDBNameMappingOfAllTablesMap, attributeName);

            String dbName = getDBName(attributeName);
            newAttributeList.add(dbName);
        }

        return newAttributeList;
    }

    /**
     * Apply attribute and DB field name mapping for a set of attribute names
     *
     * @param attributeDBNameMappingOfAllTablesMap
     *            map with different attribute and DB name values for all DAOs
     * @param attributeSet
     *            set of attribute names
     * @return Set of attribute names resp. database field names
     */
    private Set<String> applyAttributeNameDBNameMappingForSet(Map<String, String> attributeDBNameMappingOfAllTablesMap,
            Set<String> attributeSet) {
        Set<String> newAttributeSet = new TreeSet<>();

        for (String attributeName : attributeSet) {
            updateAttributeNameAndDBNameMappingInfo(attributeDBNameMappingOfAllTablesMap, attributeName);

            String dbName = getDBName(attributeName);
            if (dbName != null) {
                newAttributeSet.add(dbName);
            }
        }
        return newAttributeSet;
    }

    /**
     * Apply attribute and DB field name mapping for iSort property
     *
     * @param attributeDBNameMappingOfAllTablesMap
     *            map with different attribute and DB name values for all DAOs
     * @param sortArray
     *            array (iSort property)
     * @return iSort property with attribute names resp. database field names
     */
    private String[][] applyAttributeNameDBNameMappingForSort(Map<String, String> attributeDBNameMappingOfAllTablesMap,
            String[][] sortArray) {
        for (int i = 0; i < sortArray.length; i++) {

            for (int j = 0; j < sortArray[i].length; j++) {
                String string = sortArray[i][j];
                String[] tokens = StringTools.split(string, ", ");
                StringBuilder resultingValue = new StringBuilder();
                if (tokens != null) {
                    for (int k = 0; k < tokens.length; k++) {
                        updateAttributeNameAndDBNameMappingInfo(attributeDBNameMappingOfAllTablesMap, tokens[k]);

                        String dbName = getDBName(tokens[k]);
                        resultingValue.append(dbName);

                        if (k != tokens.length - 1) {
                            resultingValue.append(",");
                        }
                    }
                    sortArray[i][j] = resultingValue.toString();
                }
            }
        }

        return sortArray;
    }

    /**
     * Apply attribute and DB field name mapping for iTypes property
     *
     * @param attributeDBNameMappingOfAllTablesMap
     *            map with different attribute and DB name values for all DAOs
     * @param attributeTypeMap
     *            map with attribute name and corresponding data type
     * @return iTypes property with attribute names resp. database field names
     */
    private Map<String, Type> applyAttributeNameDBNameMappingForTypes(Map<String, String> attributeDBNameMappingOfAllTablesMap,
            Map<String, Type> attributeTypeMap) {
        Map<String, Type> newAttributeTypeMap = new HashMap<>();
        Set<Entry<String, Type>> attributeTypeSet = attributeTypeMap.entrySet();

        for (Entry<String, Type> attributeTypeEntry : attributeTypeSet) {
            String key = attributeTypeEntry.getKey();
            Type value = attributeTypeEntry.getValue();

            updateAttributeNameAndDBNameMappingInfo(attributeDBNameMappingOfAllTablesMap, key);
            String dbName = getDBName(key);
            newAttributeTypeMap.put(dbName, value);
        }

        return newAttributeTypeMap;
    }

    /**
     * Apply attribute and DB field name mapping for property having attribute names and equal expressions
     *
     * @param equalsExpressionSet
     *            Set with equal expressions
     * @return Set with attribute names resp. database field names for properties having attribute names with equal expressions
     */
    private Set<String> applyAttributeNameDBNameMappingForEqualExpression(Set<String> equalsExpressionSet) {
        Set<String> newEqualsExpressionSet = new TreeSet<>();

        for (String string : equalsExpressionSet) {
            newEqualsExpressionSet.add(string);
        }

        return newEqualsExpressionSet;
    }

    @Override
    public String getDefaultOrderKey() {
        return iDefaultOrderKey;
    }

    /**
     * Retourne l'orientation du tri par défaut
     */
    @Override
    public Sort getDefaultSortOrder() {
        return iDefaultSortOrder;
    }

    @Override
    public Set<String> getListUpdateFields() {
        return iListUpdateFields;
    }

    /**
     *
     * @return liste des propriétés avec configuration DAOlist=true
     */
    @Override
    public List<String> getDAOList() {
        return iList;
    }

    @Override
    public IDAOResult getFieldsRequest(IValueObject aVo, String aFieldName, Connection aConnection, DAOParameter... aParameters)
            throws SQLException {
        List<DAOParameter> params = new ArrayList<>(Arrays.asList(aParameters));
        if (DAOParameter.getParameter(Name.TABLE_NAMES, aParameters) == null) {
            Set<String> tableNames = new HashSet<>();
            tableNames.add(iTableName);
            params.add(new DAOParameter(Name.TABLE_NAMES, tableNames));
        }
        return super.getFieldsRequest(aVo, aFieldName, aConnection, params.toArray(new DAOParameter[params.size()]));
    }

    @Override
    public IDAOResult getFieldsRequest(IValueObject aVo, String aFieldName, ILoggedUser aUser, Connection aConnection,
            DAOParameter... aParameters) throws SQLException {
        prepareChildren(aVo);

        // Prepare parameters
        List<DAOParameter> params = new ArrayList<>();
        if (aParameters != null && aParameters.length > 0) {
            params.addAll(Arrays.asList(aParameters));
        }
        params.add(new DAOParameter(Name.USER, aUser));
        Set<String> tableNames = getTableNames(aVo);
        params.add(new DAOParameter(Name.TABLE_NAMES, tableNames));
        params.add(new DAOParameter(Name.JOIN_CLAUSES, getJoinClauses(tableNames)));
        params.add(new DAOParameter(Name.SECURITY_CLAUSE, getSecurityClause(aUser)));

        // Sort fields
        Integer sortIndex = getSortIndex(aParameters);
        params.add(new DAOParameter(Name.SORT_FIELDS, iSort[sortIndex - 1]));

        // Sort orientation
        if (DAOParameter.getValue(Name.SORT_ORIENTATION, aParameters) == null) {
            params.add(new DAOParameter(Name.SORT_ORIENTATION, getOrientation(sortIndex, aParameters)));
        }
        params.addAll(getSortItemParameters(sortIndex));
        return getFieldsRequest(aVo, aFieldName, aConnection, params.toArray(new DAOParameter[params.size()]));
    }

    @Override
    public IDAOResult getAggregateField(IValueObject aVo, String aFieldName, Aggregator aggr, Connection aConnection) throws SQLException {
        return getAggregateField(aVo, iTableName, aFieldName, aggr, aConnection);
    }

    /**
     * Préparation de clauses where sur les enfants
     *
     * @param voInOut
     *            vo requête avec un childrenmap
     */
    @SuppressWarnings("unchecked")
    protected void prepareChildren(IValueObject voInOut) {
        if (voInOut.getProperty("childrenmap") == null) {
            return;
        }
        // Prepare children clauses in the order that they appear in the
        // childrenmap
        // The same order will be expected when filling the prepared statement
        // later
        Map<String, String> mapIn = new HashMap<>();
        for (Entry<String, IValueObject> entry : ((Map<String, IValueObject>) voInOut.getProperty("childrenmap")).entrySet()) {
            ChildrenInfo child = getChildrenInfo(entry.getKey());
            IValueObject voChild = entry.getValue();
            if (child != null && voChild != null && !voChild.isEmpty()) {
                // Get child query
                IDataAccessObject dao = DAOFactory.getInstance().getDAO(entry.getKey());
                String sql = dao.getChildQuery(voChild, child.getChildrenLink(), iTableName);
                if (mapIn.get(child.getMasterLink()) == null) {
                    mapIn.put(child.getMasterLink(), sql);
                } else {
                    String sqlIn = mapIn.get(child.getMasterLink());
                    sqlIn += " union " + sql;
                    mapIn.put(child.getMasterLink(), sqlIn);
                }
            }
        }
        if (!mapIn.isEmpty()) {
            for (Entry<String, String> inClause : mapIn.entrySet()) {
                voInOut.setProperty(inClause.getKey(), Operator.getOperator(Operator.IN, inClause.getValue()));
                voInOut.setProperty("hasChildren", true);
            }
        }
    }

    /**
     *
     * @param aName
     *            le nom de l'objet métier d'un vo enfant
     * @return la configuration ChildrenInfo du vo enfant
     */
    private ChildrenInfo getChildrenInfo(String aName) {
        for (ChildrenInfo child : iVOInfo.getChildrens()) {
            if (child.getChildrenName().equals(aName)) {
                return child;
            }
        }
        return null;
    }

}