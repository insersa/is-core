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

package ch.inser.dynamic.common.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.inser.dynamic.common.DAOResult;
import ch.inser.dynamic.common.DAOTools;
import ch.inser.dynamic.common.IDAOResult;
import ch.inser.dynamic.common.IDAOResult.Status;
import ch.inser.dynamic.common.ILoggedUser;
import ch.inser.dynamic.common.IValueObject;
import ch.inser.dynamic.util.JoinInfo;
import ch.inser.jsl.list.ListHandler;
import ch.inser.jsl.tools.StringTools;

/**
 * Classe BusinessObject abstraite
 *
 * @version 1.0
 * @author INSER SA
 */
public abstract class AbstractDynamicDAO extends ch.inser.dynamic.common.AbstractDynamicDAO {

    /**
     * Serial UID
     */
    private static final long serialVersionUID = -2509621375386471905L;
    /** Définition de la catégorie de logging */
    private static final Log logger = LogFactory.getLog(AbstractDynamicDAO.class);

    // ---------------------------------------------------- Méthodes membres
    protected String getNextId(String aSequence, Connection aConnection) throws SQLException {

        StringBuilder sql = new StringBuilder(64);
        sql.append("UPDATE SEQUENCE set SEQ_VAL = SEQ_VALUE + 1 WHERE SEQ_NOM_TABLE='");
        sql.append(aSequence.toLowerCase());
        sql.append("'");

        String nextId = null;

        try (PreparedStatement ps = getPreparedStatement(sql, aConnection)) {
            logger.debug("Exécute SQL: " + ps.toString());
            try (ResultSet rs = ps.executeQuery()) {
                // Update de la table des séquences!
            }
        }

        sql.setLength(0);
        sql.append("SELECT SEQ_VAL FROM SEQUENCE WHERE SEQ_NOM_TABLE='");
        sql.append(aSequence.toLowerCase());
        sql.append("'");
        // Utilise un PreparedStatement de type DebuggableStatement
        try (PreparedStatement ps = getPreparedStatement(sql, aConnection)) {
            ps.setMaxRows(1);

            // Exécute le requête de recherche
            logger.debug("Exécute SQL: " + ps.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    nextId = rs.getString(1);
                }
            }
            // On force le commit pour éviter les problèmes d'accès
            // concurrents!!!
            aConnection.commit();
        }
        return nextId;
    }

    /**
     * Retourne la prochaine valeur du ID en utilisant une sequence.
     *
     * @param aSequence
     *            Le nom de la sequence.
     * @param aConnection
     *            Connexion pour exécuter la requête.
     *
     * @return La valeur du prochain ID.
     *
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données. Un erreur au niveau de la base de données.
     */

    @Override
    protected Object getNextId(String aSequence, IValueObject.Type aIdType, Connection aConnection) throws SQLException {
        return getNextId(aSequence, aIdType);
    }

    /**
     * Méthode à implémenter dans chaque projet, problème de gestion des séquences dans mySQL
     *
     * @return
     */
    protected abstract Object getNextId(String aSequence, IValueObject.Type aIdType) throws SQLException;

    /**
     * Crée un nouveau record pour une entité.
     *
     * @param aVo
     *            L'objet à créer.
     * @param aTable
     *            Le nom de la table.
     * @param aNotCreate
     *            Les attributs qu'in ne faut pas remplir.
     * @param aMajAttribute
     *            Le nom de l'attribut contenant l'auteur de la mise à jour.
     * @param aTimestampAttribute
     *            Le nom de l'attribut contenant la date de mise à jour.
     * @param aUser
     *            L'utilisateur qui demande l'ajout du record.
     * @param aConnection
     *            Connexion pour exécuter la requête.
     *
     * @return Le nombre d'enregistrements effectués.
     *
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données. Un erreur au niveau de la base de données.
     */
    @Override
    protected IDAOResult create(IValueObject aVo, String aTable, Set<String> aNotCreate, String aMajAttribute, String aTimestampAttribute,
            ILoggedUser aUser, Connection aConnection) throws SQLException {
        // enregistre les renseignemenst sur le _srid, cette valeur est
        // nécessaire
        // pour la création et la mise à jour des champs géométriques
        long srid = (Long) aVo.getProperty("_srid");

        Set<String> attributes = aVo.getProperties().keySet();
        if (aNotCreate != null) {
            attributes.removeAll(aNotCreate);
        }
        Collection<String> toRemove = aVo.getOmit();
        if (toRemove != null) {
            toRemove.remove(aMajAttribute);
            attributes.removeAll(toRemove);
        }
        if (aMajAttribute != null) {
            aVo.setProperty(aMajAttribute, aUser.getUserUpdateName());
        }
        // Garde que les attributs d'ont on connait le type.
        attributes.retainAll(aVo.getTypes().keySet());

        // On crée la requête SQL pour l'insertion
        StringBuilder sql = new StringBuilder(1024);
        sql.append("INSERT INTO ");
        sql.append(aTable);
        sql.append(" (");
        addAttributesNames(aVo, new ArrayList<>(attributes), aTimestampAttribute, sql, Mode.CREATE);
        sql.append(") VALUES (");
        addValues(aVo, attributes, sql, srid);

        // Insérer le temps selon le type du champ
        if (aTimestampAttribute != null) {
            if (aVo.getTypes().get(aTimestampAttribute).equals(IValueObject.Type.LONG)) {
                sql.append("," + new java.util.Date().getTime());
            } else {
                sql.append(", NOW()");
            }
        }

        sql.append(")");

        PreparedStatement ps = null;
        try {
            // Utilise un PreparedStatement de type DebuggableStatement
            ps = getPreparedStatement(sql, aConnection);
            ps.setMaxRows(1);

            setRecord(aVo, ps, Mode.CREATE, null);

            // Exécute la requête de mise à jour
            logger.debug("Exécute SQL: " + ps.toString());
            int rowCount = ps.executeUpdate();

            IDAOResult result = new DAOResult();
            result.setStatus(rowCount > 0 ? Status.OK : Status.KO);
            result.setNbrRecords(rowCount);
            return result;
        } catch (SQLException e) {
            String psStr = null;
            if (ps != null) {
                psStr = ps.toString();
            }
            logger.error("SqlException: " + psStr, e);

            // Construit une exception chaînée avec la requête SQL
            SQLException ex = new SQLException(psStr, null, -1001);
            e.setNextException(ex);
            throw e;
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    /**
     * Met à jour un enregistrement selon une collection de champs à mettre à jour et à un timestamp pour gérer la transaction longue.
     *
     * @param aUpdateFields
     *            Collection Map des champs à mettre à jour.
     * @param aNotUpdate
     *            Les attribut à pas mettre à jour.
     * @param aIdName
     *            Le nom de l'attribut contenant l'ID.
     * @param aIdValue
     *            La valeur de l'ID.
     * @param aTable
     *            Le nom de la table.
     * @param aTimestName
     *            Le nom de l'attribut contenant le timestamp.
     * @param aTimestValue
     *            La valeur du timestamp.
     * @param aTypes
     *            Les types des attributs de l'enregistrement.
     * @param aMajName
     *            Le nom de l'attribut contenant l'auteur de la mise à jour.
     * @param aUser
     *            Utilisateur qui demande la mise à jour.
     * @param aConnection
     *            Connexion pour exécuter la requête.
     *
     * @return Le nombre de records impliqués dans la mise à jour.
     *
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données. Un erreur au niveau de la base de données.
     */
    @Override
    protected int update(Map<String, Object> aUpdateFields, Set<String> aNotUpdate, String aIdName, Object aIdValue, String aTable,
            String aTimestName, Timestamp aTimestValue, Map<String, IValueObject.Type> aTypes, String aMajName, IValueObject.Type aMajType,
            ILoggedUser aUser, Connection aConnection) throws SQLException {
        return update(aUpdateFields, aNotUpdate, aIdName, aIdValue, aTable, aTimestName, aTimestValue, aTypes, aMajName, aMajType, aUser,
                null, aConnection);
    }

    @Override
    protected int update(Map<String, Object> aUpdateFields, Set<String> aNotUpdate, String aIdName, Object aIdValue, String aTable,
            String aTimestName, Timestamp aTimestValue, Map<String, IValueObject.Type> aTypes, String aMajName, IValueObject.Type aMajType,
            ILoggedUser aUser, String aSecurityClause, Connection aConnection) throws SQLException {

        // S'il n'y a pas de champs à mettre à jour => rien à faire
        if (aUpdateFields.isEmpty()) {
            return 0;
        }
        // On crée la requête de mise à jour
        StringBuilder sql = new StringBuilder(512);

        sql.append("UPDATE ");
        sql.append(aTable);
        sql.append(" SET ");

        // insérer le temps du dernier changement
        if (aTimestName != null && aTimestName.length() > 0) {
            sql.append(aTimestName);
            // Utilisation du ps en cas du type LONG
            if (aTypes.get(aTimestName).equals(IValueObject.Type.LONG)) {
                sql.append("=?");
            } else {
                sql.append("=NOW()");
            }

        }

        // Insérer l'utilisateur
        if (aUser != null && aUser.getUserUpdateName() != null && aMajName != null && aMajName.length() > 0) {
            sql.append(", ");
            sql.append(aMajName);
            sql.append("=?");
        }

        // On enlève les champs qui ne doivent pas être mis à jour et/ou la date
        // et le nom de l'auteur de la mise à jour.
        if (aNotUpdate != null) {
            Iterator<String> nu = aNotUpdate.iterator();
            while (nu.hasNext()) {
                aUpdateFields.remove(nu.next());
            }
        }

        Set<Map.Entry<String, Object>> set = aUpdateFields.entrySet();
        Map.Entry<String, Object> me;
        Iterator<Map.Entry<String, Object>> it = set.iterator();
        // D'abord on itère pour ajouter les champs à mettre à jour sans leur
        // valeur
        while (it.hasNext()) {
            if (aTimestName != null && aTimestName.length() > 0
                    || aUser != null && aUser.getUserUpdateName() != null && aMajName != null && aMajName.length() > 0) {
                sql.append(", ");
            }
            sql.append(it.next().getKey());
            sql.append("=?");
        }
        sql.append(" WHERE ");
        addClauseDeleteUpdate(aIdName, aTimestName, aSecurityClause, sql);

        PreparedStatement ps = null;
        int rowCount = 0;

        try {
            // Utilise un PreparedStatement de type DebuggableStatement
            ps = getPreparedStatement(sql, aConnection);
            ps.setMaxRows(1);

            set = aUpdateFields.entrySet();
            it = set.iterator();

            int idx = 1;

            // Insérer le temps de la dernière modification
            if (aTimestName != null && aTimestName.length() > 0 && aTypes.get(aTimestName).equals(IValueObject.Type.LONG)) {
                java.util.Date now = new java.util.Date();
                idx = DAOTools.set(idx, now.getTime(), IValueObject.Type.LONG, AttributeType.EQUAL, ps);
            }

            // Insérer l'utilisateur qui a fait la modification
            if (aUser != null && aUser.getUserUpdateName() != null && aMajName != null && aMajName.length() > 0) {
                idx = DAOTools.set(idx, aUser.getUserUpdateName(), aMajType, AttributeType.EQUAL, ps);
            }

            // On itère à nouveau pour placer les valeurs des champs à mettre à
            // jour
            while (it.hasNext()) {
                me = it.next();
                idx = DAOTools.set(idx, me.getValue(), aTypes.get(me.getKey()), AttributeType.EQUAL, ps);
            }
            addValuesDeleteUpdate(idx, aIdValue, aTimestValue, ps, aTypes.get(aTimestName));

            // Exécute la requête de mise à jour
            logger.debug("Exécute SQL: " + ps.toString());
            rowCount = ps.executeUpdate();

        } catch (SQLException e) {
            String psStr = null;
            if (ps != null) {
                psStr = ps.toString();
            }
            logger.error("SqlException: " + psStr, e);

            // Construit une exception chaînée avec la requête SQL
            SQLException ex = new SQLException(psStr, null, -1001);
            e.setNextException(ex);
            throw e;
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
        return rowCount;
    }

    /**
     * Réécriture pour l'identification des champs à l'aide de leur table Ajoute des conditions à la requête SQL (PreparedStatement).
     *
     * @param aVo
     *            Le VO contenant les conditions à ajouter.
     * @param aNoLikeNames
     *            Les noms des attributs pour les quelles ne pas utiliser le like.
     * @param aFirst
     *            Indique s'il s'agit de la première clause.
     * @param aSql
     *            La requête à la quelle ajouter les conditions.
     *
     * @return True si aucune la prochaine cause est la première.
     */
    protected void addClause(String aAttribute, boolean aLike, StringBuilder aSql) {

        if (aLike) {
            aSql.append("UPPER(");
            aSql.append(aAttribute);
            aSql.append(") LIKE UPPER(?) ");
        } else {
            aSql.append(aAttribute);
            aSql.append("=?");
        }
    }

    protected boolean addClause(IValueObject aVo, Set<String> aEqualNames, boolean aFirst, StringBuilder aSql) {

        boolean first = aFirst;
        Collection<String> keys = aVo.getProperties().keySet();
        keys.retainAll(aVo.getTypes().keySet());
        Iterator<String> it = keys.iterator();
        String attribute;

        if (!first && it.hasNext()) {
            aSql.append(" AND ");
        }
        if (it.hasNext()) {
            first = false;
        }

        while (it.hasNext()) {
            attribute = it.next();
            // On rajoute l'identification de la table uniquement si l'attribut
            // vient de la table principale
            String tableName = aVo.getVOInfo().getAttribute(attribute).getTable();
            String attUtil;
            if (tableName == null) {
                tableName = aVo.getVOInfo().getTable();
                attUtil = tableName + "." + attribute;
            } else {
                attUtil = StringTools.replace(attribute, "__", ".");
            }
            IValueObject.Type type = aVo.getPropertyType(attribute);
            if (type == IValueObject.Type.DATE || type == IValueObject.Type.TIMESTAMP) {
                addDateClause(attUtil, aSql);
            } else {
                addClause(attUtil, !aEqualNames.contains(attribute) && type == IValueObject.Type.STRING, aSql);
            }
            if (it.hasNext()) {
                aSql.append(" AND ");
            }
        }

        return first;
    }

    @Override
    protected IDAOResult fillRecord(IValueObject aVo, Collection<String> aAttributes, ResultSet aRs, List<String> aRsColumns)
            throws SQLException {

        Collection<String> attributes = aAttributes;
        if (attributes == null) {
            attributes = aVo.getTypes().keySet();
        }
        for (String attribute : attributes) {
            int index = attribute.indexOf("__");
            Object obj;
            if (index >= 0) {
                obj = DAOTools.getFromRS(attribute.substring(index + 2), aVo.getPropertyType(attribute), aRs);
            } else {
                obj = DAOTools.getFromRS(attribute, aVo.getPropertyType(attribute), aRs);
            }
            aVo.setProperty(attribute, obj);
        }
        return new DAOResult(aVo);
    }

    /**
     * Ajoute des noms à la requête SQL.
     *
     * @param aNames
     *            Une collection de noms à rajouter.
     * @param aSql
     *            La requête à la quelle ajouter les noms.
     */
    @Override
    protected void addNames(Collection<String> aNames, StringBuilder aSql) {

        Iterator<String> it = aNames.iterator();
        String utilString;
        while (it.hasNext()) {
            utilString = it.next();
            aSql.append(StringTools.replace(utilString, "__", "."));
            if (it.hasNext()) {
                aSql.append(", ");
            }
        }
    }

    protected void addNames(IValueObject aVo, Collection<String> aNames, StringBuilder aSql) {

        Iterator<String> it = aNames.iterator();
        String utilString;
        String tableName = aVo.getVOInfo().getTable();
        while (it.hasNext()) {
            utilString = it.next();
            if (aVo.getVOInfo().getAttribute(utilString).getTable() == null) {
                utilString = tableName + "." + utilString;
            }
            aSql.append(StringTools.replace(utilString, "__", "."));
            if (it.hasNext()) {
                aSql.append(", ");
            }
        }
    }

    /**
     * Ajoute une condition à la requête SQL (PreparedStatement).
     *
     * @param aAttribute
     *            Le nom de l'attribut.
     * @param aLike
     *            true s'il faut utiliser le like (uniquement pour les strings).
     * @param aSql
     *            La requête à la quelle ajouter les conditions.
     */
    protected void addClauseString(String aAttribute, boolean aLike, StringBuilder aSql) {

        if (aLike) {
            aSql.append("UPPER(");
            aSql.append(aAttribute);
            aSql.append(") LIKE UPPER(?)");
        } else {
            aSql.append(aAttribute);
            aSql.append("=?");
        }
    }

    protected void addDateClause(String aAttribute, StringBuilder aSql) {
        aSql.append(" DATE_FORMAT(");
        aSql.append(aAttribute);
        aSql.append(",'%D:%m:%Y') = DATE_FORMAT(?,'%D:%m:%Y')");
    }

    protected Collection<IValueObject> getList(IValueObject aVo, String aTable, Set<String> aIdNames, String[] aSort,
            ListHandler.Sort aSortOrientation, Collection<String> aAttributes, Connection aConnection) throws SQLException {
        return getList(aVo, aTable, aIdNames, aSort, aSortOrientation, aAttributes, false, null, aConnection);
    }

    protected Collection<IValueObject> getList(IValueObject aVo, String aTable, Set<String> aIdNames, String[] aSort,
            ListHandler.Sort aSortOrientation, Collection<String> aAttributes, ILoggedUser aUser, Connection aConnection)
            throws SQLException {
        return getList(aVo, aTable, aIdNames, aSort, aSortOrientation, aAttributes, false, aUser, aConnection);
    }

    protected Collection<IValueObject> getListAll(IValueObject aVo, String aTable, Set<String> aIdNames, String[] aSort,
            ListHandler.Sort aSortOrientation, Collection<String> aAttributes, Connection aConnection) throws SQLException {
        return getList(aVo, aTable, aIdNames, aSort, aSortOrientation, aAttributes, true, null, aConnection);
    }

    protected Collection<IValueObject> getListAll(IValueObject aVo, String aTable, Set<String> aIdNames, String[] aSort,
            ListHandler.Sort aSortOrientation, Collection<String> aAttributes, ILoggedUser aUser, Connection aConnection)
            throws SQLException {
        return getList(aVo, aTable, aIdNames, aSort, aSortOrientation, aAttributes, true, aUser, aConnection);
    }

    // Pour accepter la syntaxe mySQL
    private Collection<IValueObject> getList(IValueObject aVo, String aTable, Set<String> aIdNames, String[] aSort,
            ListHandler.Sort aSortOrientation, Collection<String> aAttributes, @SuppressWarnings("unused") boolean aAll, ILoggedUser aUser,
            Connection aConnection) throws SQLException {

        // On rajoute dans les listes les champs "concat"
        Map<String, Object> concat = aVo.getVOInfo().getInfos("concat");
        if (concat != null) {
            Iterator<String> it = concat.keySet().iterator();
            String key;
            while (it.hasNext()) {
                key = it.next();
                if (aAttributes.contains(key)) {
                    String[] strs = StringTools.split((String) concat.get(key), ",");
                    if (strs != null) {
                        for (int i = 0; i < strs.length; i++) {
                            aAttributes.add(strs[i]);
                        }
                    }
                }
            }

        }

        Map<String, JoinInfo> joinInfos = aVo.getVOInfo().getJoins();
        StringBuilder sql = new StringBuilder(512);
        sql.append("SELECT ");
        List<String> attributes = null;
        if (aAttributes != null) {
            attributes = new ArrayList<>(aAttributes);
        }
        addAttributesNames(aVo, attributes, null, sql);
        sql.append(" FROM ");
        sql.append(aTable);
        sql.append(" ");
        // jointures
        if (!joinInfos.isEmpty()) {
            Iterator<String> it = joinInfos.keySet().iterator();
            String key;
            while (it.hasNext()) {
                key = it.next();
                sql.append(key);
                sql.append(" ON ");
                sql.append(joinInfos.get(key).getClause());
                sql.append(" ");

            }
        }
        // Si on a des clauses (séléctions)
        String userClause = null;
        if (aUser != null) {
            userClause = aUser.getAdditionnalClause(aTable);
        }
        if (!aVo.isEmpty() || userClause != null) {
            sql.append(" WHERE ");
            boolean isFirst = addClause(aVo, aIdNames, true, sql);
            if (userClause != null && userClause.length() > 0) {
                if (!isFirst) {
                    sql.append(" AND ");
                }
                sql.append(userClause);
            }
        }
        if (aSort != null) {
            sql.append(" ORDER BY ");

            for (int i = 0; i < aSort.length; i++) {
                sql.append(aSort[i]);
                if (aSort[i].indexOf(' ') <= 0) {
                    sql.append(aSortOrientation == ListHandler.Sort.DESCENDING ? " DESC" : " ASC");
                }
                if (i < aSort.length - 1) {
                    sql.append(", ");
                }
            }

        }
        List<IValueObject> vos = new ArrayList<>();
        vos.add(aVo);
        return getList(vos, sql, aAttributes, aVo, null, aConnection);
    }

    /**
     * Retourne la liste des objets sélon les critères spécifiés dans le VO et un attribut défini dans une liste.
     *
     * @param aValues
     * @param aValuesField
     * @param aVo
     * @param aTable
     * @param aIdNames
     * @param aSort
     * @param aSortOrientation
     * @param aAttributes
     * @param aConnection
     * @return
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    protected Collection<IValueObject> getList(List<Object> aValues, String aValuesField, IValueObject aVo, String aTable, String[] aSort,
            ListHandler.Sort aSortOrientation, Collection<String> aAttributes, Connection aConnection) throws SQLException {

        // On rajoute dans les listes les champs "concat"
        Map<String, Object> concat = aVo.getVOInfo().getInfos("concat");
        if (concat != null) {
            Iterator<String> it = concat.keySet().iterator();
            String key;
            while (it.hasNext()) {
                key = it.next();
                if (aAttributes.contains(key)) {
                    String[] strs = StringTools.split((String) concat.get(key), ",");
                    if (strs != null) {
                        for (int i = 0; i < strs.length; i++) {
                            aAttributes.add(strs[i]);
                        }
                    }
                }
            }

        }

        Map<String, JoinInfo> joinInfos = aVo.getVOInfo().getJoins();
        StringBuilder sql = new StringBuilder(512);
        sql.append("SELECT ");
        addAttributesNames(aVo, aAttributes, null, sql);
        sql.append(" FROM ");
        sql.append(aTable);
        sql.append(" ");
        // jointures
        if (!joinInfos.isEmpty()) {
            Iterator<String> it = joinInfos.keySet().iterator();
            String key;
            while (it.hasNext()) {
                key = it.next();
                sql.append(key);
                sql.append(" ON ");
                sql.append(joinInfos.get(key).getClause());
                sql.append(" ");

            }
        }
        // Si on a des clauses (séléctions)
        if (!aValues.isEmpty()) {
            sql.append(" WHERE ");
            sql.append(aValuesField);
            sql.append(" IN (?");
            for (int i = 1; i < aValues.size(); i++) {
                sql.append(", ?");
            }
            sql.append(")");
        }
        if (aSort != null) {
            sql.append(" ORDER BY ");

            for (int i = 0; i < aSort.length; i++) {
                sql.append(aSort[i]);
                if (aSort[i].indexOf(' ') <= 0) {
                    sql.append(aSortOrientation == ListHandler.Sort.DESCENDING ? " DESC" : " ASC");
                }
                if (i < aSort.length - 1) {
                    sql.append(", ");
                }
            }

        }
        // On exécute la requête et on stocke le résultat dans une collection
        IValueObject voEmpty = (IValueObject) aVo.clone();
        voEmpty.clear();
        List<IValueObject> list = new ArrayList<>(getInitListSize());

        try (PreparedStatement ps = getPreparedStatement(sql, aConnection)) {
            for (int i = 0; i < aValues.size(); i++) {
                ps.setObject(i + 1, aValues.get(i));
            }

            // Exécute le reqête de recherche
            logger.debug("Exécute SQL: " + ps.toString());
            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    // Crée et remplit le Value Object pour ce record
                    // Ajoute le VO à la collection
                    list.add(fillRecord((IValueObject) voEmpty.clone(), aAttributes, rs, null).getValueObject());
                }
            }
        }
        return list;
    }

    // Pour accepter la syntaxe mySQL
    protected IDAOResult getRecord(String aIdName, Object aIdValue, String aTable, Set<String> aAttributes, IValueObject aVo,
            Connection aConnection) throws SQLException {

        if (aIdValue == null) {
            return new DAOResult(Status.NOTHING_TODO);
        }
        Map<String, JoinInfo> joinInfos = aVo.getVOInfo().getJoins();
        // On crée la requête SQL selon les critères de recherche existants
        StringBuilder sql = new StringBuilder(512);
        sql.append("SELECT ");
        List<String> attributes = null;
        if (aAttributes != null) {
            attributes = new ArrayList<>(attributes);
        }
        addAttributesNames(aVo, attributes, null, sql);
        sql.append(" FROM ");
        sql.append(aTable);
        sql.append(" ");
        if (!joinInfos.isEmpty()) {
            Iterator<String> it = joinInfos.keySet().iterator();
            String key;
            while (it.hasNext()) {
                key = it.next();
                sql.append(key);
                sql.append(" ON ");
                sql.append(joinInfos.get(key).getClause());
                sql.append(" ");

            }
        }
        sql.append(" WHERE ");
        // On rajoute l'identification de la table uniquement si l'attribut
        // vient de la table principale
        String tableName = aVo.getVOInfo().getTable();
        String attUtil = tableName + "." + aIdName;

        addClause(attUtil, false, sql);
        return getRecord(aIdName, aIdValue, sql, aAttributes, aVo, aConnection);
    }

    protected void addAttributesNames(IValueObject aVo, Collection<String> aAttributes, String aTimestamp, StringBuilder aSql) {

        if (aAttributes == null || aAttributes.isEmpty()) {
            aSql.append("*");
        } else {
            addNames(aVo, aAttributes, aSql);
        }
        if (aTimestamp != null && aTimestamp.length() > 0) {
            aSql.append(", ");
            aSql.append(aTimestamp);
        }
    }
}
