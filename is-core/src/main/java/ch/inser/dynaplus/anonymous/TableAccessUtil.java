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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.javaunderground.jdbc.StatementFactory;

import ch.inser.dynamic.common.IValueObject;
import ch.inser.jsl.exceptions.ISException;

/**
 * Création et suppression des tables temporaires
 *
 * @author INSER SA
 *
 */
public class TableAccessUtil {

    /** Définition de la catégorie de logging */
    private static final Log logger = LogFactory.getLog(TableAccessUtil.class);

    /**
     * Private constructor
     */
    private TableAccessUtil() {
    }

    /**
     * Crée une table à partir d'un nom de table et une liste de champs et leurs types SQL
     *
     * @param aVO
     *            vo avec nom de table et champs à créer
     * @param aCon
     *            connexion
     * @throws ISException
     *             erreur de création de la table
     *
     */
    public static void createTable(VOAnonymous aVO, Connection aCon) throws ISException {
        if (aVO.getTable() == null || aVO.getFieldTypes() == null || aVO.getFieldTypes().isEmpty()) {
            StringBuilder error = new StringBuilder("Les informations pour créer la table manques. Table: ");
            error.append(aVO.getTable());
            if (aVO.getFieldTypes() != null) {
                error.append("Fields: ");
                error.append(aVO.getFieldTypes().keySet());
            }
            logger.error(error);
            throw new ISException(error.toString());
        }
        String sql = prepareCreateStatement(aVO);
        logger.debug("Executing SQL: " + sql);
        try (PreparedStatement pstmt = aCon.prepareStatement(sql.toString())) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new ISException(e);
        }
    }

    /**
     * @param aVO
     *            vo avec le nom de la table et les champs
     * @return create table statement
     */
    private static String prepareCreateStatement(VOAnonymous aVO) {
        StringBuilder sql = new StringBuilder("CREATE TABLE ");
        sql.append(aVO.getTable());
        sql.append(" (");
        Set<String> fields = aVO.getFieldTypes().keySet();
        boolean first = true;
        for (String field : fields) {
            if (!first) {
                sql.append(",");
            }
            sql.append(field);
            sql.append(" ");
            sql.append(aVO.getType(field));
            first = false;
        }
        sql.append(")");
        if (aVO.getTablespace() != null) {
            sql.append(" TABLESPACE ");
            sql.append(aVO.getTablespace());
        }
        return sql.toString();
    }

    /**
     * Supprime une table avec un DROP TABLE statement
     *
     * @param aTableName
     *            nom de la table
     * @param aCon
     *            connexion
     * @throws ISException
     *             erreur de suppression de la table
     *
     */
    public static void dropTable(String aTableName, Connection aCon) throws ISException {
        if (aTableName == null || aTableName.length() == 0) {
            logger.error("Le nom de la table n'est pas défini");
            throw new ISException("Le nom de la table n'est pas défini");
        }
        StringBuilder sql = new StringBuilder("DROP TABLE ");
        sql.append(aTableName);
        logger.debug("Executing SQL: " + sql);
        try (PreparedStatement pstmt = aCon.prepareStatement(sql.toString())) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new ISException(e);
        }
    }

    /**
     * Crée une enregistrement dans la table temporaire
     *
     * @param aTablename
     *            nom de la table
     *
     * @param aVo
     *            le record à enregistrer dans la table temporaire
     * @param aCon
     *            connexion
     * @throws SQLException
     *             probleme d'enregistrement
     */
    public static void insertRecord(String aTablename, IValueObject aVo, Connection aCon) throws SQLException {
        StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append(aTablename);
        sql.append("(");
        StringBuilder valueHolders = new StringBuilder();
        boolean first = true;
        for (String field : aVo.getProperties().keySet()) {
            if (!first) {
                sql.append(",");
                valueHolders.append(",");
            }
            sql.append(field);
            valueHolders.append("?");
            first = false;
        }
        sql.append(") values (");
        sql.append(valueHolders);
        sql.append(")");
        try (PreparedStatement ps = StatementFactory.getStatement(aCon, sql.toString())) {
            int idx = 1;
            for (String field : aVo.getProperties().keySet()) {
                ps.setObject(idx, aVo.getProperty(field));
                idx++;
            }

            // exécute SQL
            logger.trace("exécute SQL: " + ps);
            ps.executeUpdate();
        } catch (SQLException e) {
            aCon.rollback();
            throw e;
        }
    }
}
