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

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.javaunderground.jdbc.StatementFactory;

import ch.inser.dynamic.common.DAOParameter;
import ch.inser.dynamic.common.DAOResult;
import ch.inser.dynamic.common.DynamicDAO.Aggregator;
import ch.inser.dynamic.common.DynamicDAO.Operator;
import ch.inser.dynamic.common.IDAOResult;
import ch.inser.dynamic.common.IDAOResult.Status;
import ch.inser.dynamic.common.ILoggedUser;
import ch.inser.dynamic.common.IValueObject;
import ch.inser.dynamic.util.VOInfo;
import ch.inser.dynaplus.dao.IDataAccessObject;
import ch.inser.dynaplus.util.Constants.Entity;
import ch.inser.dynaplus.vo.IVOFactory;
import ch.inser.dynaplus.vo.VOFactory;
import ch.inser.jsl.exceptions.ISException;
import ch.inser.jsl.list.ListHandler.Sort;

/**
 * DAO pour consulter des tables sans fichier de configuration
 *
 * @author INSER SA
 *
 */
public class DAOAnonymous implements IDataAccessObject {

    /**
     * Objet pour les logs.
     */
    private static final Log logger = LogFactory.getLog(DAOAnonymous.class);

    /**
     * Nomre max de lignes à lire.
     */
    private static int cResultSetMaxRows = 0;

    @Override
    public IDAOResult getRecord(String aTable, String aField, Object aValue, Connection aCon) throws SQLException {
        if (aValue == null) {
            return new DAOResult(Status.NOTHING_TODO);
        }
        // On crée la requête SQL selon les critères de recherche existants
        StringBuffer sql = new StringBuffer(512);
        sql.append("SELECT * from ");
        sql.append(aTable);
        sql.append(" WHERE ");
        sql.append(aField);
        sql.append(" = ?");

        // Utilise un PreparedStatement de type DebuggableStatement
        try (PreparedStatement ps = StatementFactory.getStatement(aCon, sql.toString())) {
            ps.setMaxRows(cResultSetMaxRows);
            if (aValue instanceof Long) {
                ps.setLong(1, (Long) aValue);
            } else if (aValue instanceof BigDecimal) {
                ps.setBigDecimal(1, (BigDecimal) aValue);
            } else if (aValue instanceof String) {
                ps.setString(1, (String) aValue);
            } else {
                throw new UnsupportedOperationException("GetRecord with datatype " + aValue.getClass().toString() + " is not implemented");
            }

            // Exécute le reqête de recherche
            logger.debug("Exécute SQL: " + ps.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return new DAOResult(Status.NOTHING_TODO);
                }
                return new DAOResult(fillRecord(rs));
            }
        }

    }

    @Override
    public IDAOResult getList(String aTable, IValueObject aQuery, Connection aCon) throws SQLException {
        return getList(aTable, aQuery, null, aCon);
    }

    @Override
    public IDAOResult getList(String aTable, IValueObject aQuery, Integer aRowNum, Connection aCon) throws SQLException {
        // On crée la requête SQL selon les critères de recherche existants
        StringBuffer sql = new StringBuffer(512);
        sql.append("SELECT * FROM ");
        sql.append(aTable);
        Set<String> fields = aQuery.getProperties().keySet();
        if (!fields.isEmpty()) {
            sql.append(" WHERE ");
            boolean first = true;
            if (aQuery.getProperty("ADDITIONAL_STATEMENT") != null) {
                first = false;
                sql.append(aQuery.getProperty("ADDITIONAL_STATEMENT"));
                aQuery.removeProperty("ADDITIONAL_STATEMENT");
            }

            for (String field : fields) {
                if (!first) {
                    sql.append(" AND ");
                }
                if (aQuery.getProperty(field) instanceof Map) {
                    addToClause(sql, field, aQuery.getProperty(field));
                } else {
                    sql.append(field);
                    sql.append(" = ?");
                }
                first = false;
            }
        }

        // Utilise un PreparedStatement de type DebuggableStatement
        try (PreparedStatement ps = StatementFactory.getStatement(aCon, sql.toString())) {
            if (aRowNum != null) {
                ps.setMaxRows(aRowNum);
            } else {
                ps.setMaxRows(cResultSetMaxRows);
            }
            int index = 1;
            for (String field : fields) {
                if (!(aQuery.getProperty(field) instanceof Map)) {
                    ps.setObject(index, aQuery.getProperty(field));
                    index++;
                }
            }

            // Exécute le requête de recherche
            logger.debug("Exécute SQL: " + ps.toString());
            try (ResultSet rs = ps.executeQuery()) {

                // Mettre le résultat dans une liste
                List<IValueObject> res = new ArrayList<>();
                while (rs.next()) {
                    res.add(fillRecord(rs));
                }
                return new DAOResult(res);
            }
        }
    }

    /**
     * Ajoute un condition avec un opérateur spéciale au clause WHERE
     *
     * @param aSql
     *            la requete sql
     * @param aField
     *            le nom du champ
     * @param aProperty
     *            la condition avec un opérateur spéciale (IS NULL, IS NOT NULL, IN etc.)
     */
    private void addToClause(StringBuffer aSql, String aField, Object aProperty) {
        if (aProperty instanceof Map<?, ?>) {
            Set<?> keys = ((Map<?, ?>) aProperty).keySet();
            for (Object key : keys) {
                if (key instanceof Operator) {
                    Operator op = (Operator) key;
                    if (op.equals(Operator.IS_NULL)) {
                        aSql.append(aField);
                        aSql.append(" IS NULL ");
                    } else if (op.equals(Operator.IS_NOT_NULL)) {
                        aSql.append(aField);
                        aSql.append(" IS NOT NULL ");
                    } else if (op.equals(Operator.IN)) {
                        aSql.append(aField);
                        aSql.append(" IN (");
                        aSql.append(((Map<?, ?>) aProperty).get(key));
                        aSql.append(") ");
                    } else if (op.equals(Operator.NOT_IN)) {
                        aSql.append(aField);
                        aSql.append(" NOT IN (");
                        aSql.append(((Map<?, ?>) aProperty).get(key));
                        aSql.append(") ");
                    } else if (op.equals(Operator.DIFF)) {
                        aSql.append(aField);
                        aSql.append("!=");
                        Object value = ((Map<?, ?>) aProperty).get(key);
                        if (value instanceof Long) {
                            aSql.append(value);
                        } else if (value instanceof String) {
                            aSql.append("'");
                            aSql.append(value);
                            aSql.append("'");
                        } else {
                            throw new UnsupportedOperationException("Operator DIFF not implemented for the datatype of value: " + value);
                        }
                    }
                }
            }
        }
    }

    /**
     * Met le résultat de la requête sql dans un vo
     *
     * @param aRs
     *            le résultat de la requête sql
     * @return vo avec tous les noms de champs et valeurs
     * @throws SQLException
     *             problème d'accès au contenu du result set
     */
    private IValueObject fillRecord(ResultSet aRs) throws SQLException {
        IValueObject vo = VOFactory.getInstance().getVO(Entity.anonymous.toString());
        int nbr = aRs.getMetaData().getColumnCount();
        for (int i = 1; i <= nbr; i++) {
            // Cas spéciale DATE: on utilise getDate() au lieu de getObject()
            // pour éviter qu'on reçoit un Timestamp (ma 10.01.2013)
            if ("DATE".equals(aRs.getMetaData().getColumnTypeName(i))) {
                vo.setProperty(aRs.getMetaData().getColumnName(i), aRs.getDate(i));
            } else {
                vo.setProperty(aRs.getMetaData().getColumnName(i), aRs.getObject(i));
            }
        }
        return vo;
    }

    /**
     * Défini le nombre maximale de lignes à lire.
     *
     * @param aResultSetMaxRows
     *            Le nombre maximale de lignes à lire.
     */
    public static void setResultSetMaxRows(int aResultSetMaxRows) {

        cResultSetMaxRows = aResultSetMaxRows;
    }

    @Override
    public void init(VOInfo aVoInfo) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult getRecord(Object aId, ILoggedUser aUser, Connection aConnection) throws SQLException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult getRecordFull(Object aId, ILoggedUser aUser, Connection aConnection) throws SQLException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult getList(IValueObject aVo, ILoggedUser aUser, Connection aConnection, DAOParameter... aParameters) throws ISException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult getListCount(IValueObject aVo, Connection aConnection, ILoggedUser aUser, DAOParameter... aParameters)
            throws SQLException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public int update(Map<String, Object> aUpdateFields, Object aId, Timestamp aTimestamp, ILoggedUser aUser, Connection aConnection)
            throws SQLException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public Object getNextId(ILoggedUser aUser, Connection aConnection) throws SQLException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult create(IValueObject aVo, ILoggedUser aUser, Connection aConnection) throws SQLException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult delete(Object aId, Timestamp aTimestamp, ILoggedUser aUser, Connection aConnection) throws SQLException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult getTimestamp(Object aId, ILoggedUser aUser, Connection aConnection) throws SQLException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult getField(Object aId, String aFieldName, Connection aConnection) throws SQLException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult updateFields(Object aId, String[] aFieldNames, Object[] aValues, Connection aConnection) throws SQLException {
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
    public Set<String> getListUpdateFields() {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public List<String> getDAOList() {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public Object executeMethode(String aNameMethode, Object aObject, ILoggedUser aUser, Connection aConnection) throws ISException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public String getChildQuery(IValueObject aVo, String aChildrenLink, String aParentTable) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult getAggregateField(IValueObject aVo, String aFieldName, Aggregator agr, Connection aConnection) throws SQLException {
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
    public IDAOResult updateField(Object aId, String aFieldName, Object aValue, Connection aConnection) throws SQLException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult updateFieldRequest(IValueObject aVo, String aFieldName, Object aValue, Connection aConnection) throws SQLException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public int updateFields(Collection<Object> aIds, String aFieldName, Object aValue, Connection aConnection) throws SQLException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public int updateFieldsRequest(IValueObject aVo, String[] aFieldName, Object[] aValue, Connection aConnection) throws SQLException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IVOFactory getVOFactory() {
        return null;
    }

    @Override
    public void setVOFactory(IVOFactory aVOFactory) {
        // Do nothing
    }
}
