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

package ch.inser.dynaplus.dao.postgresql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.javaunderground.jdbc.StatementFactory;

import ch.inser.dynamic.common.DAOParameter;
import ch.inser.dynamic.common.DAOParameter.Name;
import ch.inser.dynamic.common.DAOResult;
import ch.inser.dynamic.common.DAOTools;
import ch.inser.dynamic.common.IDAOResult;
import ch.inser.dynamic.common.IDAOResult.Status;
import ch.inser.dynamic.common.ILoggedUser;
import ch.inser.dynamic.common.IValueObject;
import ch.inser.dynamic.common.IValueObject.Type;
import ch.inser.dynamic.util.JoinInfo;
import ch.inser.dynamic.util.VOInfo;

/**
 * To implement specific PostgreSQL code.
 *
 * @author INSER SA
 */
public class GenericDataAccessObject extends ch.inser.dynaplus.dao.GenericDataAccessObject {

    /**
     * The UID
     */
    private static final long serialVersionUID = 6434496451212815344L;

    /**
     * Objet pour les logs.
     */
    private static final Log logger = LogFactory.getLog(GenericDataAccessObject.class);

    /**
     * Generic constructor using the VOInfo name as object name.
     *
     * @param aVOInfo
     *            the VOInfo
     */
    public GenericDataAccessObject(VOInfo aVOInfo) {
        super(aVOInfo.getName(), aVOInfo);
    }

    /**
     * Add the slice statement to a SQL statement for PostgreSQL databases, Oracle 12c (and others standard databases), not supported ob
     * Oracle 11g or older.
     *
     * More information: https://docs.oracle.com/database/121/SQLRF/statements_10002.htm#BABHFGAA
     * https://oracle-base.com/articles/12c/row-limiting-clause-for-top-n-queries-12cr1
     *
     * @param aSql
     *            The SQL statement
     * @param aParameters
     *            additional parameters with start and end row numbers
     * @return statement limiting the result using the ROWNUM_START and ROWNUM_ENDparameters
     */
    @Override
    protected StringBuilder getSliceStatement(StringBuilder aSql, DAOParameter[] aParameters) {
        Object start = null;
        Object end = null;
        for (int j = 0; j < aParameters.length; j++) {
            if (aParameters[j].getName() == Name.ROWNUM_START) {
                start = aParameters[j].getValue();
            } else if (aParameters[j].getName() == Name.ROWNUM_END) {
                end = aParameters[j].getValue();
            }
        }
        if (start == null || end == null) {
            return aSql;
        }

        long startLong = ((Long) start).longValue();
        long endLong = ((Long) end).longValue();

        aSql.append(" OFFSET ");
        aSql.append(startLong - 1);
        aSql.append(" ROWS FETCH NEXT ");
        aSql.append(endLong - startLong + 1);
        aSql.append(" ROWS ONLY");

        return aSql;
    }

    @Override
    protected String getDateTimeFunction() {
        return "NOW()";
    }

    @Override
    protected String getNextIdQuery() {
        return "SELECT nextval('%1$s') as next";
    }

    /**
     * Surcharge pour avoir des jointures standars, la config doit être
     *
     * <join>
     * <table>
     * LEFT JOIN personne
     * </table>
     * <clause>cli_id_id=cli_per_id</clause> </join>
     *
     * @param aVo
     *            vo requête
     * @param aAttributes
     *            noms de champs pour la clause select
     * @param aParameters
     *            paramètres dao
     * @return statement select avec where, group by et order by
     */

    @Override
    @SuppressWarnings("unchecked")
    protected StringBuilder getListStatement(IValueObject aVo, DAOParameter... aParameters) {
        StringBuilder sql = new StringBuilder(STRING_BUFFER_SIZE);
        if (aVo.getProperty("DISTINCT") != null && (Boolean) aVo.getProperty("DISTINCT")) {
            sql.append("SELECT DISTINCT ");
        } else {
            sql.append("SELECT ");
        }
        addAttributesNames(aVo, (List<String>) DAOParameter.getValue(Name.ATTRIBUTES, aParameters), null, sql, Mode.SELECT);
        sql.append(" FROM ");
        // ici les jointure sont ajoutées à la Oracle
        sql.append(getJoinTables(getTableName(), getJoins()));
        sql.append(getListWhere(aVo, aParameters));
        sql.append(getListGroupBy(aParameters));
        sql.append(getListSort(aParameters));
        return sql;
    }

    @Override
    public IDAOResult getFieldsRequest(IValueObject aVo, String aFieldName, Connection aConnection, DAOParameter... aParameters)
            throws SQLException {
        List<Object> list = new ArrayList<>();
        // On crée la requête de mise à jour
        StringBuilder sql = new StringBuilder(STRING_BUFFER_SIZE);

        sql.append("select ");
        if (aVo.getProperty("DISTINCT") != null) {
            sql.append("DISTINCT ");
        }
        sql.append(aFieldName);
        sql.append(" from ");
        sql.append(getJoinTables(getTableName(), getJoins()));
        sql.append(getListWhere(aVo, aParameters));
        sql.append(getListSort(aParameters));

        try (PreparedStatement ps = StatementFactory.getStatement(aConnection, sql.toString())) {

            setRecord(aVo, ps, Mode.SELECT, null);

            // Exécute la requête de mise à jour
            logger.debug("Exécute SQL: " + ps.toString());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(DAOTools.getFromRS(aFieldName, aVo.getPropertyType(aFieldName), rs));
                }
            }
        }
        return new DAOResult(list);
    }

    /**
     * Ajout de la clause de jointure selon le concepte sql standard
     *
     * @param join
     * @param isFirst
     * @param where
     * @return
     */
    @Override
    protected StringBuilder getJoinWhere(Set<String> join, boolean isFirst) {
        return new StringBuilder();
    }

    /**
     * Ajout des tables de jointure selon le concepte sql standard
     *
     */
    private StringBuilder getJoinTables(String aTable, Map<String, JoinInfo> aJoin) {
        StringBuilder sql = new StringBuilder();
        sql.append(aTable);
        sql.append(" ");
        // jointures
        if (!aJoin.isEmpty()) {
            Iterator<String> it = aJoin.keySet().iterator();
            String key;
            while (it.hasNext()) {
                key = it.next();
                sql.append(key);
                sql.append(" ON ");
                sql.append(aJoin.get(key).getClause());
                sql.append(" ");

            }
        }

        return sql;
    }

    /**
     * Pour assurer les jointures selon sql standar
     */
    @Override
    public IDAOResult getRecord(Object id, ILoggedUser user, Connection connection) throws SQLException {
        String securityClause = user.getAdditionnalClause(getName(), Mode.SELECT);
        if ("".equals(securityClause)) {
            securityClause = null;
        }

        // Utilisation des champs de sélection pour un getrecord avec shape
        if (iTypes.containsValue(Type.SHAPE)) {
            return getRecord(getIdName(), id, getTableName(), getJoins(), getSelectFields(), getVOFactory().getVO(getName()),
                    securityClause, connection);
        }

        // getRecord * sans champ de sélection
        return getRecord(getIdName(), id, getTableName(), getJoins(), null, getVOFactory().getVO(getName()), securityClause, connection);
    }

    @Override
    public IDAOResult getRecordFull(Object id, ILoggedUser user, Connection connection) throws SQLException {
        String securityClause = user.getAdditionnalClause(getName());
        if ("".equals(securityClause)) {
            securityClause = null;
        }

        // getRecord sans * sans champ de sélection
        return getRecord(getIdName(), id, getTableName(), getJoins(), getSelectFields(), getVOFactory().getVO(getName()), securityClause,
                connection);
    }

    /**
     * Pour assurer les jointures selon sql standard
     */
    protected IDAOResult getRecord(String aIdName, Object aIdValue, String aTable, Map<String, JoinInfo> aJoin, Set<String> aAttributes,
            IValueObject aVo, String aSecurityClause, Connection aConnection) throws SQLException {
        if (aIdValue == null) {
            return new DAOResult(Status.NOTHING_TODO);
        }
        // On crée la requête SQL selon les critères de recherche existants
        StringBuilder sql = new StringBuilder(STRING_BUFFER_SIZE);
        sql.append("SELECT ");
        List<String> attributes = null;
        if (aAttributes != null) {
            attributes = new ArrayList<>(aAttributes);
        }
        // ajout des attributs
        addAttributesNames(aVo, attributes, null, sql, Mode.SELECT);
        sql.append(" FROM ");
        sql.append(getJoinTables(aTable, aJoin));
        sql.append(" WHERE ");
        DAOTools.addClause(aIdName, null, AttributeType.EQUAL, sql);
        if (aSecurityClause != null) {
            sql.append(" AND ");
            sql.append(aSecurityClause);
        }
        return getRecord(aIdName, aIdValue, sql, aAttributes, aVo, aConnection);
    }

    /**
     * Pour assurer les jointures selon sql standard
     */
    @Override
    protected StringBuilder getCountStatement(IValueObject aVo, DAOParameter... aParameters) {
        StringBuilder sql = new StringBuilder(STRING_BUFFER_SIZE);
        sql.append("SELECT COUNT(*) FROM ");
        sql.append(getJoinTables(getTableName(), getJoins()));
        sql.append(getListWhere(aVo, aParameters));
        return sql;
    }

    @Override
    public String getChildQuery(IValueObject aVo, String aChildrenLink, String aParentTable) {
        StringBuilder sql = new StringBuilder(512);

        sql.append("SELECT " + aChildrenLink + " FROM ");

        // Tables
        sql.append(getJoinTables(getTableName(), getJoins()));
        sql.append(" WHERE ");

        // Search criteria
        addClause(aVo, true, sql);

        // Additional statement
        if (aVo.getProperty("ADDITIONAL_STATEMENT") != null) {
            sql.append(" AND ");
            sql.append(aVo.getProperty("ADDITIONAL_STATEMENT"));
            aVo.removeProperty("ADDITIONAL_STATEMENT");
        }
        return sql.toString();

    }

}
