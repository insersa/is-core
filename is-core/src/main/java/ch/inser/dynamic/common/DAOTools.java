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

package ch.inser.dynamic.common;

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.javaunderground.jdbc.StatementFactory;

import ch.inser.dynamic.common.DynamicDAO.AttributeType;
import ch.inser.dynamic.common.DynamicDAO.Operator;
import ch.inser.jsl.tools.NumberTools;

/**
 * Classe utilitaires pour le traitement de certaines tâches statiques
 *
 * @author INSER SA
 *
 */
public abstract class DAOTools {

    /**
     * Logger
     */
    private static final Log logger = LogFactory.getLog(DAOTools.class);

    /**
     * Constructeur privé
     */
    private DAOTools() {
    }

    /**
     * Retourne la valeur d'un attribut.
     *
     * @param aProperty
     *            Le nom de la propriété à lire.
     * @param aType
     *            Le type de la propriété à lire.
     * @param aRs
     *            Le ResultSet dans le quel lire.
     *
     * @return la valeur de la propriété demandé.
     *
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données. Un erreur au niveau de la base de données.
     */
    public static Object getFromRS(String aProperty, IValueObject.Type aType, ResultSet aRs) throws SQLException {

        if (aType == null) {
            throw new UnsupportedOperationException("Type '" + aType + "' de la propriété '" + aProperty + "' non connu");
        }
        try {
            switch (aType) {
                case LIST:
                    return null;
                case STRING:
                    return aRs.getString(aProperty);
                case LONG:
                    long lg = aRs.getLong(aProperty);
                    return aRs.wasNull() ? null : Long.valueOf(lg);
                case DOUBLE:
                    double db = aRs.getDouble(aProperty);
                    return aRs.wasNull() ? null : Double.valueOf(db);
                case DATE:
                    Timestamp ts = aRs.getTimestamp(aProperty);
                    return ts == null ? null : new java.sql.Date(ts.getTime());
                case TIMESTAMP:
                    return aRs.getTimestamp(aProperty);
                case BYTES:
                    return aRs.getBytes(aProperty);
                case TIME:
                    return aRs.getTime(aProperty);
                case BOOLEAN:
                    boolean tmp = aRs.getBoolean(aProperty);
                    return aRs.wasNull() ? null : tmp;
                case BLOB:
                    return aRs.getBlob(aProperty);
                case CLOB:
                    java.sql.Clob clob = aRs.getClob(aProperty);
                    if (clob == null) {
                        return null;
                    }
                    long size = clob.length();
                    return clob.getSubString(1, (int) size);
                case SHAPE:
                    java.sql.Clob clobShape = aRs.getClob(aProperty);
                    if (clobShape == null) {
                        return null;
                    }
                    return clobShape.getSubString(1, (int) clobShape.length());
                case UUID:
                    return aRs.getObject(aProperty);
                case JSON:
                    return aRs.getObject(aProperty);
                default:
                    throw new UnsupportedOperationException("Type '" + aType + "' de la propriété '" + aProperty + "' non connu");
            }
        } catch (RuntimeException e) {
            logger.error("Erreur en lisant la propriété '" + aProperty + "' de type '" + aType + "' dans le ResultSet", e);
            throw e;
        } catch (SQLException e) {
            if (e.getErrorCode() == 17006) { // Invalid Column Name (17006)
                logger.warn("Invalid Column Name: " + aProperty + ". Return 'null' value", e);
                return null;
            }
            logger.error("Erreur en lisant la propriété '" + aProperty + "' de type '" + aType + "' dans le ResultSet", e);
            throw e;
        }
    }

    /**
     * Ajoute une condition à la requête SQL (PreparedStatement).
     *
     * @param aAttribute
     *            Le nom de l'attribut.
     * @param aValue
     *            La valeur de la restriction, afin de pouvoir régler la notion de requête complexe
     * @param aOpString
     *            Choix de la recherche donné par la configuration de l'attribut
     * @param aSql
     *            La requête à la quelle ajouter les conditions.
     */
    public static void addClause(String aAttribute, Object aValue, AttributeType aOpString, StringBuilder aSql) {
        AttributeType opAtt = aOpString;
        if (aOpString == null) {
            opAtt = AttributeType.EQUAL;
        }
        if (aValue instanceof Map<?, ?>) {
            addMapClause(aAttribute, aValue, opAtt, aSql);
        } else {
            // Préparation des formatages du squelette de la requête SQL
            switch (opAtt) {
                // Pour les strings
                case FULL_LIKE:
                case LIKE:
                    aSql.append(aAttribute);
                    aSql.append(" LIKE ?");
                    break;
                case UPPER_LIKE:
                    aSql.append(attrPart(aAttribute, AttributeType.UPPER));
                    aSql.append(" LIKE UPPER(?) {escape '\\'}");
                    break;
                case UPPER:
                    aSql.append(attrPart(aAttribute, AttributeType.UPPER));
                    aSql.append("=UPPER(?)");
                    break;
                case UPPER_FULL_LIKE:
                    aSql.append(attrPart(aAttribute, AttributeType.UPPER));
                    aSql.append(" LIKE UPPER(?)");
                    break;
                // pour les dates
                case DAY_EQU:
                    aSql.append(attrPart(aAttribute, AttributeType.DAY_EQU));
                    aSql.append("=?");
                    break;
                case MONTH_EQU:
                    aSql.append(attrPart(aAttribute, AttributeType.MONTH_EQU));
                    aSql.append("=?");
                    break;
                case YEAR_EQU:
                    aSql.append(attrPart(aAttribute, AttributeType.YEAR_EQU));
                    aSql.append("=?");
                    break;
                case CONTAINS:
                    aSql.append("contains(");
                    aSql.append(aAttribute);
                    aSql.append(", ?) > 0");
                    break;
                default:
                    aSql.append(aAttribute);
                    aSql.append("=?");

            }
        }
    }

    /**
     * Clause WHERE avec un opérateur
     *
     * @param aAttribute
     *            nom de champ
     * @param aValue
     *            valeur map à transformer en clause
     * @param aOpAtt
     *            type d'attribut
     * @param aSql
     *            la requête en construction
     */
    private static void addMapClause(String aAttribute, Object aValue, AttributeType aOpAtt, StringBuilder aSql) {

        Map<?, ?> valueMap = (Map<?, ?>) aValue;
        boolean isFirst = true;
        for (Object key : valueMap.keySet()) {
            if (isFirst) {
                isFirst = false;
            } else {
                aSql.append(" AND ");
            }
            Operator op = (Operator) key;
            switch (op) {
                case EQU:
                    aSql.append(aAttribute);
                    aSql.append("=?");
                    break;
                case UPPER_EQU:
                    aSql.append(attrPart(aAttribute, AttributeType.UPPER));
                    aSql.append("=UPPER(?)");
                    break;
                case UPPER_LIKE:
                    aSql.append(attrPart(aAttribute, AttributeType.UPPER));
                    aSql.append(" LIKE UPPER(?)");
                    break;
                case SQL_FUNCT:
                    aSql.append(aAttribute);
                    aSql.append("=");
                    String funct = (String) ((Object[]) valueMap.get(key))[0];
                    aSql.append(funct);
                    aSql.append("(?)");
                    break;
                case SQL_FUNCT_FULL_LIKE:
                    aSql.append(aAttribute);
                    aSql.append(" LIKE '%'||");
                    funct = (String) ((Object[]) valueMap.get(key))[0];
                    aSql.append(funct);
                    aSql.append("(?)||'%'");
                    break;
                case DIFF:
                    aSql.append(attrPart(aAttribute, aOpAtt));
                    aSql.append("!=?");
                    break;
                case SMALLER:
                    aSql.append(attrPart(aAttribute, aOpAtt));
                    aSql.append("<?");
                    break;
                case SMALLER_EQU:
                    boolean isString = valueMap.get(key) instanceof String;
                    if (isString) {
                        aSql.append(attrPart(aAttribute, AttributeType.UPPER));
                    } else {
                        aSql.append(attrPart(aAttribute, aOpAtt));

                    }
                    aSql.append("<=?");
                    break;
                case BIGGER:
                    aSql.append(attrPart(aAttribute, aOpAtt));
                    aSql.append(">?");
                    break;
                case BIGGER_EQU:
                    aSql.append(attrPart(aAttribute, aOpAtt));
                    aSql.append(">=?");
                    break;
                case FULL_LIKE:
                case LIKE:
                    aSql.append(aAttribute);
                    aSql.append(" LIKE ?");
                    break;
                case IN:
                case NOT_IN:
                    int i = 0;
                    if (valueMap.get(key) instanceof List<?>) {
                        i = ((List<?>) valueMap.get(key)).size();
                        if (i > 0) {
                            aSql.append(aAttribute);
                            if (op == Operator.IN) {
                                aSql.append(" IN (");
                            } else {
                                aSql.append(" NOT IN (");
                            }
                            for (int j = 1; j <= i; j++) {
                                if (j < i && i >= 2) {
                                    aSql.append("?,");
                                } else {
                                    aSql.append("?)");
                                }
                            }
                        }
                    }
                    if (valueMap.get(key) instanceof String) {
                        // If we get a string its in fact a select statement
                        aSql.append(aAttribute);
                        if (op == Operator.IN) {
                            aSql.append(" IN (");
                        } else {
                            aSql.append(" NOT IN (");
                        }
                        aSql.append(valueMap.get(key));
                        aSql.append(")");
                    }
                    break;
                case IS_NULL:
                    aSql.append(aAttribute);
                    aSql.append(" IS NULL");
                    break;
                case IS_NOT_NULL:
                    aSql.append(aAttribute);
                    aSql.append(" IS NOT NULL");
                    break;
                case DAY_EQU:
                    aSql.append("TO_CHAR(");
                    aSql.append(aAttribute);
                    aSql.append(",'yyyy.mm.dd')=?");
                    break;
                case MONTH_EQU:
                    aSql.append("TO_CHAR(");
                    aSql.append(aAttribute);
                    aSql.append(",'yyyy.mm')=?");
                    break;
                case YEAR_EQU:
                    aSql.append("TO_CHAR(");
                    aSql.append(aAttribute);
                    aSql.append(",'yyyy')=?");
                    break;
                case EQU_SELECT:
                    aSql.append(aAttribute);
                    aSql.append("=(?)");
                    break;
                case OR:
                    aSql.append("(");
                    Map<?, ?> valueMapOr = (Map<?, ?>) valueMap.get(key);
                    boolean isFirstOr = true;
                    for (Object keyOr : valueMapOr.keySet()) {
                        if (isFirstOr) {
                            isFirstOr = false;
                        } else {
                            aSql.append(" OR ");
                        }
                        if (keyOr instanceof Operator) {
                            Map<Operator, Object> orClause = new EnumMap<>(Operator.class);
                            orClause.put((Operator) keyOr, valueMapOr.get(keyOr));
                            addClause(aAttribute, orClause, aOpAtt, aSql);
                        }
                    }
                    aSql.append(")");
                    break;
                case CONTAINS:
                    aSql.append("contains(");
                    aSql.append(aAttribute);
                    aSql.append(", ?) > 0");
                    break;
                default:
                    aSql.append("ERROR - OPERATOR NOT EXIST");
            }
        }
    }

    /**
     * Ajoute la valeur d'un attribut à un Preparated Statement.
     *
     * @param aIdx
     *            L'index de l'attribut dans le Preparated Statement.
     * @param aValue
     *            La valeur de l'attribut dans le Preparated Statement.
     * @param aType
     *            Le type de l'attribut dans le Preparated Statement.
     * @param aAttType
     *            Choix de la recherche pour un string like, like_full ou equal
     * @param aPs
     *            Le Preparated Statement.
     *
     * @return Le prochain index à utiliser
     *
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données. Un erreur au niveau de la base de données.
     */
    public static int set(int aIdx, Object aValue, IValueObject.Type aType, DynamicDAO.AttributeType aAttType, PreparedStatement aPs)
            throws SQLException {

        int idx = aIdx;
        if (aType == null) {
            RuntimeException e = new UnsupportedOperationException(
                    "Le type " + "'null' de l'attribut nr: '" + idx + "' n'est pas supporté");
            logger.error("Le type 'null' de l'attribut nr: '" + idx + "' n'est pas supporté", e);
            throw e;
        }
        if (aValue == null) {
            aPs.setNull(aIdx, aType.getSqlType());
            idx++;
        } else {
            if (aValue instanceof Map<?, ?>) {
                // Requête complexe
                Map<?, ?> valueMap = (Map<?, ?>) aValue;
                for (Object key : valueMap.keySet()) {
                    switch ((Operator) key) {
                        case OR:
                            idx = set(idx, valueMap.get(key), aType, aAttType, aPs);
                            break;
                        case IN:
                        case NOT_IN:
                            // c'est un IN
                            Object value = valueMap.get(key);
                            if (value instanceof Collection<?>) {
                                Collection<?> valueList = (Collection<?>) value;
                                for (Object obj : valueList) {
                                    idx = set(idx, obj, aType, AttributeType.EQUAL, aPs);
                                }
                            }
                            // value instanceof String
                            // Just do nothing all the informations are
                            // inside

                            break;
                        case EQU:
                            idx = set(idx, valueMap.get(key), aType, AttributeType.EQUAL, aPs);
                            break;
                        case LIKE:
                            idx = set(idx, valueMap.get(key), aType, AttributeType.LIKE, aPs);
                            break;
                        case FULL_LIKE:
                            idx = set(idx, valueMap.get(key), aType, AttributeType.FULL_LIKE, aPs);
                            break;
                        case UPPER_FULL_LIKE:
                            idx = set(idx, valueMap.get(key), aType, AttributeType.UPPER_FULL_LIKE, aPs);
                            break;
                        case UPPER_LIKE:
                            idx = set(idx, valueMap.get(key), aType, AttributeType.UPPER_LIKE, aPs);
                            break;
                        case UPPER_EQU:
                            idx = set(idx, valueMap.get(key), aType, AttributeType.UPPER, aPs);
                            break;
                        case SQL_FUNCT:
                        case SQL_FUNCT_FULL_LIKE:
                            idx = set(idx, ((Object[]) valueMap.get(key))[1], aType, AttributeType.EQUAL, aPs);
                            break;
                        case DAY_EQU:
                            idx = set(idx, valueMap.get(key), aType, AttributeType.DAY_EQU, aPs);
                            break;
                        case MONTH_EQU:
                            idx = set(idx, valueMap.get(key), aType, AttributeType.MONTH_EQU, aPs);
                            break;
                        case YEAR_EQU:
                            idx = set(idx, valueMap.get(key), aType, AttributeType.YEAR_EQU, aPs);
                            break;
                        case EQU_SELECT:
                            aPs.setString(idx, (String) valueMap.get(key));
                            break;
                        case BIGGER_EQU:
                        case SMALLER_EQU:
                        case BIGGER:
                        case SMALLER:
                        case DIFF:
                            if (aAttType == AttributeType.LIKE || aAttType == AttributeType.FULL_LIKE) {
                                // Cela pass très mal le LIKE ou FULL_LIKE en
                                // comparaison!
                                idx = set(idx, valueMap.get(key), aType, AttributeType.EQUAL, aPs);
                            } else if (aAttType == AttributeType.UPPER_LIKE || aAttType == AttributeType.UPPER_FULL_LIKE) {
                                idx = set(idx, valueMap.get(key), aType, AttributeType.UPPER, aPs);
                            } else {
                                idx = set(idx, valueMap.get(key), aType, aAttType, aPs);
                            }
                            break;
                        case CONTAINS:
                            idx = set(idx, valueMap.get(key), aType, AttributeType.CONTAINS, aPs);
                            break;
                        default:
                            // Ne pas incrémenter idx si il y a l'opérateur
                            // IS_NULL ou IS_NOT_NULL
                            if (key != Operator.IS_NULL && key != Operator.IS_NOT_NULL) {
                                idx = set(idx, valueMap.get(key), aType, aAttType, aPs);
                            }

                    }
                }

            } else {
                try {
                    switch (aType) {
                        case STRING:
                            String strv = (String) aValue;
                            if (aAttType == AttributeType.LIKE) {
                                if (strv.indexOf('%') == -1 && strv.indexOf('_') == -1) {
                                    strv += "%";
                                }
                                aPs.setString(idx, strv);
                            } else if (aAttType == AttributeType.UPPER_LIKE) {
                                if (strv.indexOf('%') == -1 && strv.indexOf('_') == -1) {
                                    strv += "%";
                                }
                                aPs.setString(idx, strv.toUpperCase());
                            } else if (aAttType == AttributeType.FULL_LIKE) {
                                if (strv.indexOf('%') == -1 && strv.indexOf('_') == -1) {
                                    strv = "%" + strv + "%";
                                }
                                aPs.setString(idx, strv);
                            } else if (aAttType == AttributeType.UPPER_FULL_LIKE) {
                                if (strv.indexOf('%') == -1 && strv.indexOf('_') == -1) {
                                    strv = "%" + strv + "%";
                                }
                                aPs.setString(idx, strv.toUpperCase());
                            } else if (aAttType == AttributeType.UPPER) {
                                aPs.setString(idx, ((String) aValue).toUpperCase());
                            } else if (aAttType == AttributeType.CONTAINS) {
                                // On veut éviter l'utilisation du not dans le
                                // CONTEXT GRAMMAR
                                strv = strv.replace('-', ' ');
                                aPs.setString(idx, strv);
                            } else {
                                aPs.setString(idx, (String) aValue);
                            }
                            break;
                        case LONG:
                            if (aAttType == AttributeType.LIKE) {
                                aPs.setString(idx, aValue.toString() + "%");
                            } else {
                                aPs.setLong(idx, NumberTools.getLong(aValue).longValue());
                            }
                            break;
                        case DOUBLE:
                            aPs.setDouble(idx, ((Double) aValue).doubleValue());
                            break;
                        case DATE:
                            // Version pour passage des heures, la fonction
                            // setDate ne permet pas la modification des heures
                            // au niveau de la bd, donc on utilise le
                            // setTimestamp
                            // aPs.setTimestamp(idx, new Timestamp(
                        case TIMESTAMP:
                            switch (aAttType) {
                                case DAY_EQU:
                                    aPs.setString(idx, new SimpleDateFormat("yyyy.MM.dd").format((Date) aValue));
                                    break;
                                case MONTH_EQU:
                                    aPs.setString(idx, new SimpleDateFormat("yyyy.MM").format((Date) aValue));
                                    break;
                                case YEAR_EQU:
                                    aPs.setString(idx, new SimpleDateFormat("yyyy").format((Date) aValue));
                                    break;
                                default:
                                    if (aValue instanceof java.sql.Date) {
                                        aPs.setTimestamp(idx, new Timestamp(((java.sql.Date) aValue).getTime()));
                                    } else {
                                        aPs.setTimestamp(idx, (java.sql.Timestamp) aValue);
                                    }

                            }

                            break;
                        case TIME:
                            aPs.setTime(idx, (java.sql.Time) aValue);
                            break;
                        case BYTES:
                            aPs.setBytes(idx, (byte[]) aValue);
                            break;
                        case BOOLEAN:
                            aPs.setBoolean(idx, (java.lang.Boolean) aValue);
                            break;
                        case BLOB:
                            java.sql.Blob blob = (java.sql.Blob) aValue;
                            InputStream inputStream = blob.getBinaryStream();
                            aPs.setBinaryStream(idx, inputStream, (int) blob.length());
                            // The former call aPs.setBlob does not work with
                            // the Oracle DB
                            // aPs.setBlob(idx, (java.sql.Blob) aValue);
                            break;
                        case CLOB:

                            if (aValue instanceof String) {
                                // Demander la base pour recevoir un clob vide
                                PreparedStatement ps = null;
                                ResultSet rs = null;
                                java.sql.Clob clob;
                                try {
                                    ps = StatementFactory.getStatement(aPs.getConnection(), "SELECT TO_CLOB('CLOB') FROM DUAL");
                                    rs = ps.executeQuery();
                                    rs.next();
                                    clob = rs.getClob(1);

                                } finally {
                                    if (rs != null) {
                                        rs.close();
                                    }
                                    if (ps != null) {
                                        ps.close();
                                    }

                                }
                                // set Clob
                                clob.setString(1, (String) aValue);
                                aPs.setClob(idx, clob);
                            } else {
                                aPs.setClob(idx, (java.sql.Clob) aValue);
                            }

                            break;
                        case SHAPE:
                            aPs.setString(idx, (String) aValue);
                            break;
                        case UUID:
                            aPs.setObject(idx, aValue, Types.OTHER);
                            break;
                        case JSON:
                            aPs.setString(idx, (String) aValue);
                            break;
                        default:
                            logger.error("Le type '" + aType + "' de l'attribut nr: '" + idx + "' n'est pas supporté");
                            throw new UnsupportedOperationException(
                                    "Le type '" + aType + "' de l'attribut nr: '" + idx + "' n'est pas supporté");
                    }
                    idx++;
                } catch (ClassCastException e) {
                    logger.error("Le type '" + AbstractDynamicVO.type(aType).getValue() + "' de l'attribut nr: '" + aIdx
                            + "' n'est pas correct (valeur de l'attribut: '" + aValue + "', classe de l'attribut: '"
                            + aValue.getClass().getName() + "')", e);
                    throw e;
                }
            }
        }
        return idx;
    }

    /**
     *
     * @param aAttribute
     *            nom de l'attribut
     * @param aOpString
     *            operateur like, full_like ou autre
     * @return la partie "attribut" de la condition attribut=valeur adapté selon operateur
     */
    private static String attrPart(String aAttribute, AttributeType aOpString) {
        AttributeType opAtt = aOpString;
        if (aOpString == null) {
            opAtt = AttributeType.EQUAL;
        }
        switch (opAtt) {
            case UPPER:
                return "UPPER(" + aAttribute + ")";
            case DAY_EQU:
                return "TO_CHAR(" + aAttribute + ",'yyyy.mm.dd')";
            case MONTH_EQU:
                return "TO_CHAR(" + aAttribute + ",'yyyy.mm')";
            case YEAR_EQU:
                return "TO_CHAR(" + aAttribute + ",'yyyy')";
            default:
                return aAttribute;
        }

    }

    /**
     * Get the list of column names.
     *
     * @param aResultSet
     *            the result set
     * @return the list of column names
     * @throws SQLException
     */
    public static List<String> getColumns(ResultSet aResultSet) throws SQLException {
        ResultSetMetaData metadata = aResultSet.getMetaData();

        List<String> result = new ArrayList<>(metadata.getColumnCount());
        for (int i = 0; i < metadata.getColumnCount(); i++) {
            result.add(metadata.getColumnName(i + 1).toLowerCase());
        }

        return result;
    }
}