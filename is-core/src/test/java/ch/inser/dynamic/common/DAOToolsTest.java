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

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import ch.inser.dynamic.common.DynamicDAO.AttributeType;
import ch.inser.dynamic.common.DynamicDAO.Operator;

/**
 * Tests sur la construction de prepared statement
 *
 * @author INSER SA
 *
 */
public class DAOToolsTest {
    /**
     * Mocked object
     */
    PreparedStatement iPreparedStatement;

    /**
     * SQL_FUNCT,SQL_FUNCT_FULL_LIKE
     *
     * @throws SQLException
     *             erreur de construction de prepared statement
     */
    @Test
    public void testSqlFunct() throws SQLException {
        StringBuilder sql = new StringBuilder();

        Map<Operator, Object> map = new HashMap<>();
        map.put(Operator.SQL_FUNCT, new String[] { "function", "value" });
        for (AttributeType type : AttributeType.values()) {
            DAOTools.addClause("attribute", map, type, sql);
            assertTrue("type:" + type + "/" + sql.toString(),
                    "attribute=function(?)".equals(sql.toString()));
            sql.setLength(0);
        }

        map.clear();
        map.put(Operator.SQL_FUNCT_FULL_LIKE,
                new String[] { "function", "value" });
        for (AttributeType type : AttributeType.values()) {
            DAOTools.addClause("attribute", map, type, sql);
            assertTrue(sql.toString(), "attribute LIKE '%'||function(?)||'%'"
                    .equals(sql.toString()));
            sql.setLength(0);
        }

        // SQL_FUNCT,SQL_FUNCT_FULL_LIKE
        Operator[] operators = new Operator[] { Operator.SQL_FUNCT,
                Operator.SQL_FUNCT_FULL_LIKE };
        for (Operator operator : operators) {
            for (AttributeType type : AttributeType.values()) {
                initPreparedStatement();
                DAOTools.set(1,
                        Operator.getOperator(operator,
                                new String[] { "function", "valeur" }),
                        IValueObject.Type.STRING, type, iPreparedStatement);
                verify(iPreparedStatement, times(1)).setString(1, "valeur");

            }
        }

    }

    /**
     * Test de addClause avec propriété string
     */
    @Test
    public void addClauseString() {
        StringBuilder sql = new StringBuilder();
        String str = "value";
        DAOTools.addClause("attribute", str, AttributeType.EQUAL, sql);
        assertTrue(sql.toString(), "attribute=?".equals(sql.toString()));
        sql.setLength(0);
        DAOTools.addClause("attribute", str, AttributeType.UPPER, sql);
        assertTrue(sql.toString(),
                "UPPER(attribute)=UPPER(?)".equals(sql.toString()));
        sql.setLength(0);
        DAOTools.addClause("attribute", str, AttributeType.LIKE, sql);
        assertTrue(sql.toString(), "attribute LIKE ?".equals(sql.toString()));
        sql.setLength(0);

        DAOTools.addClause("attribute", str, AttributeType.UPPER_LIKE, sql);
        assertTrue(sql.toString(),
                "UPPER(attribute) LIKE UPPER(?) {escape '\\'}"
                        .equals(sql.toString()));
        sql.setLength(0);
        DAOTools.addClause("attribute", str, AttributeType.FULL_LIKE, sql);
        assertTrue(sql.toString(), "attribute LIKE ?".equals(sql.toString()));
        sql.setLength(0);
        DAOTools.addClause("attribute", str, AttributeType.UPPER_FULL_LIKE,
                sql);
        assertTrue(sql.toString(),
                "UPPER(attribute) LIKE UPPER(?)".equals(sql.toString()));
        sql.setLength(0);
    }

    /**
     * Test de addClause avec propriété timestamp
     */
    @Test
    public void addClauseTIMESTAMP() {
        StringBuilder sql = new StringBuilder();
        Timestamp timestamp = Timestamp
                .valueOf("2007-12-23 09:01:06.000000003");
        DAOTools.addClause("attribute", timestamp, AttributeType.EQUAL, sql);
        assertTrue(sql.toString(), "attribute=?".equals(sql.toString()));

        sql.setLength(0);
        DAOTools.addClause("attribute", timestamp, AttributeType.DAY_EQU, sql);
        assertTrue(sql.toString(),
                "TO_CHAR(attribute,'yyyy.mm.dd')=?".equals(sql.toString()));

        sql.setLength(0);
        DAOTools.addClause("attribute", timestamp, AttributeType.MONTH_EQU,
                sql);
        assertTrue(sql.toString(),
                "TO_CHAR(attribute,'yyyy.mm')=?".equals(sql.toString()));

        sql.setLength(0);
        DAOTools.addClause("attribute", timestamp, AttributeType.YEAR_EQU, sql);
        assertTrue(sql.toString(),
                "TO_CHAR(attribute,'yyyy')=?".equals(sql.toString()));

        Map<Operator, Object> map = new HashMap<>();

        map.put(Operator.BIGGER_EQU, timestamp);
        sql.setLength(0);
        DAOTools.addClause("attribute", map, null, sql);
        assertTrue(sql.toString(), "attribute>=?".equals(sql.toString()));

        sql.setLength(0);
        DAOTools.addClause("attribute", map, AttributeType.EQUAL, sql);
        assertTrue(sql.toString(), "attribute>=?".equals(sql.toString()));

        sql.setLength(0);
        DAOTools.addClause("attribute", map, AttributeType.DAY_EQU, sql);
        assertTrue(sql.toString(),
                "TO_CHAR(attribute,'yyyy.mm.dd')>=?".equals(sql.toString()));

        sql.setLength(0);
        DAOTools.addClause("attribute", map, AttributeType.MONTH_EQU, sql);
        assertTrue(sql.toString(),
                "TO_CHAR(attribute,'yyyy.mm')>=?".equals(sql.toString()));

        sql.setLength(0);
        DAOTools.addClause("attribute", map, AttributeType.YEAR_EQU, sql);
        assertTrue(sql.toString(),
                "TO_CHAR(attribute,'yyyy')>=?".equals(sql.toString()));

        map.clear();
        map.put(Operator.BIGGER, timestamp);
        sql.setLength(0);
        DAOTools.addClause("attribute", map, null, sql);
        assertTrue(sql.toString(), "attribute>?".equals(sql.toString()));

        sql.setLength(0);
        DAOTools.addClause("attribute", map, AttributeType.EQUAL, sql);
        assertTrue(sql.toString(), "attribute>?".equals(sql.toString()));

        sql.setLength(0);
        DAOTools.addClause("attribute", map, AttributeType.DAY_EQU, sql);
        assertTrue(sql.toString(),
                "TO_CHAR(attribute,'yyyy.mm.dd')>?".equals(sql.toString()));

        sql.setLength(0);
        DAOTools.addClause("attribute", map, AttributeType.MONTH_EQU, sql);
        assertTrue(sql.toString(),
                "TO_CHAR(attribute,'yyyy.mm')>?".equals(sql.toString()));

        sql.setLength(0);
        DAOTools.addClause("attribute", map, AttributeType.YEAR_EQU, sql);
        assertTrue(sql.toString(),
                "TO_CHAR(attribute,'yyyy')>?".equals(sql.toString()));

        map.clear();
        map.put(Operator.SMALLER_EQU, timestamp);
        sql.setLength(0);
        DAOTools.addClause("attribute", map, null, sql);
        assertTrue(sql.toString(), "attribute<=?".equals(sql.toString()));
        sql.setLength(0);
        DAOTools.addClause("attribute", map, AttributeType.EQUAL, sql);
        assertTrue(sql.toString(), "attribute<=?".equals(sql.toString()));

        sql.setLength(0);
        DAOTools.addClause("attribute", map, AttributeType.DAY_EQU, sql);
        assertTrue(sql.toString(),
                "TO_CHAR(attribute,'yyyy.mm.dd')<=?".equals(sql.toString()));

        sql.setLength(0);
        DAOTools.addClause("attribute", map, AttributeType.MONTH_EQU, sql);
        assertTrue(sql.toString(),
                "TO_CHAR(attribute,'yyyy.mm')<=?".equals(sql.toString()));

        sql.setLength(0);
        DAOTools.addClause("attribute", map, AttributeType.YEAR_EQU, sql);
        assertTrue(sql.toString(),
                "TO_CHAR(attribute,'yyyy')<=?".equals(sql.toString()));

        map.clear();
        map.put(Operator.SMALLER, timestamp);
        sql.setLength(0);
        DAOTools.addClause("attribute", map, null, sql);
        assertTrue(sql.toString(), "attribute<?".equals(sql.toString()));
        sql.setLength(0);
        DAOTools.addClause("attribute", map, AttributeType.EQUAL, sql);
        assertTrue(sql.toString(), "attribute<?".equals(sql.toString()));

        sql.setLength(0);
        DAOTools.addClause("attribute", map, AttributeType.DAY_EQU, sql);
        assertTrue(sql.toString(),
                "TO_CHAR(attribute,'yyyy.mm.dd')<?".equals(sql.toString()));

        sql.setLength(0);
        DAOTools.addClause("attribute", map, AttributeType.MONTH_EQU, sql);
        assertTrue(sql.toString(),
                "TO_CHAR(attribute,'yyyy.mm')<?".equals(sql.toString()));

        sql.setLength(0);
        DAOTools.addClause("attribute", map, AttributeType.YEAR_EQU, sql);
        assertTrue(sql.toString(),
                "TO_CHAR(attribute,'yyyy')<?".equals(sql.toString()));

        map.clear();
        map.put(Operator.DIFF, timestamp);
        sql.setLength(0);
        DAOTools.addClause("attribute", map, null, sql);
        assertTrue(sql.toString(), "attribute!=?".equals(sql.toString()));
        sql.setLength(0);
        DAOTools.addClause("attribute", map, AttributeType.EQUAL, sql);
        assertTrue(sql.toString(), "attribute!=?".equals(sql.toString()));

        sql.setLength(0);
        DAOTools.addClause("attribute", map, AttributeType.DAY_EQU, sql);
        assertTrue(sql.toString(),
                "TO_CHAR(attribute,'yyyy.mm.dd')!=?".equals(sql.toString()));

        sql.setLength(0);
        DAOTools.addClause("attribute", map, AttributeType.MONTH_EQU, sql);
        assertTrue(sql.toString(),
                "TO_CHAR(attribute,'yyyy.mm')!=?".equals(sql.toString()));

        sql.setLength(0);
        DAOTools.addClause("attribute", map, AttributeType.YEAR_EQU, sql);
        assertTrue(sql.toString(),
                "TO_CHAR(attribute,'yyyy')!=?".equals(sql.toString()));

    }

    /**
     * Test sur prepared statement avec champ de type string et ses operateurs
     *
     * @throws SQLException
     *             erreur sql
     */
    @Test
    public void setString() throws SQLException {
        // set avec opérateur par défaut par défaut
        initPreparedStatement();
        DAOTools.set(1, "toto", IValueObject.Type.STRING, AttributeType.EQUAL,
                iPreparedStatement);
        verify(iPreparedStatement, times(1)).setString(1, "toto");
        initPreparedStatement();
        DAOTools.set(1, "toto", IValueObject.Type.STRING, AttributeType.LIKE,
                iPreparedStatement);
        verify(iPreparedStatement, times(1)).setString(1, "toto%");
        initPreparedStatement();
        DAOTools.set(1, "tot%o", IValueObject.Type.STRING, AttributeType.LIKE,
                iPreparedStatement);
        verify(iPreparedStatement, times(1)).setString(1, "tot%o");
        initPreparedStatement();
        DAOTools.set(1, "tot_o", IValueObject.Type.STRING, AttributeType.LIKE,
                iPreparedStatement);
        verify(iPreparedStatement, times(1)).setString(1, "tot_o");
        initPreparedStatement();
        DAOTools.set(1, "toto", IValueObject.Type.STRING,
                AttributeType.FULL_LIKE, iPreparedStatement);
        verify(iPreparedStatement, times(1)).setString(1, "%toto%");
        initPreparedStatement();
        DAOTools.set(1, "tot%o", IValueObject.Type.STRING,
                AttributeType.FULL_LIKE, iPreparedStatement);
        verify(iPreparedStatement, times(1)).setString(1, "tot%o");
        initPreparedStatement();
        DAOTools.set(1, "tot_o", IValueObject.Type.STRING,
                AttributeType.FULL_LIKE, iPreparedStatement);
        verify(iPreparedStatement, times(1)).setString(1, "tot_o");
        initPreparedStatement();
        DAOTools.set(1, "toto", IValueObject.Type.STRING, AttributeType.UPPER,
                iPreparedStatement);
        verify(iPreparedStatement, times(1)).setString(1, "TOTO");
        initPreparedStatement();
        DAOTools.set(1, "toto", IValueObject.Type.STRING,
                AttributeType.UPPER_LIKE, iPreparedStatement);
        verify(iPreparedStatement, times(1)).setString(1, "TOTO%");
        initPreparedStatement();
        DAOTools.set(1, "tot%o", IValueObject.Type.STRING,
                AttributeType.UPPER_LIKE, iPreparedStatement);
        verify(iPreparedStatement, times(1)).setString(1, "TOT%O");
        initPreparedStatement();
        DAOTools.set(1, "tot_o", IValueObject.Type.STRING,
                AttributeType.UPPER_LIKE, iPreparedStatement);
        verify(iPreparedStatement, times(1)).setString(1, "TOT_O");
        initPreparedStatement();
        DAOTools.set(1, "toto", IValueObject.Type.STRING,
                AttributeType.UPPER_FULL_LIKE, iPreparedStatement);
        verify(iPreparedStatement, times(1)).setString(1, "%TOTO%");
        initPreparedStatement();
        DAOTools.set(1, "tot%o", IValueObject.Type.STRING,
                AttributeType.UPPER_FULL_LIKE, iPreparedStatement);
        verify(iPreparedStatement, times(1)).setString(1, "TOT%O");
        initPreparedStatement();
        DAOTools.set(1, "tot_o", IValueObject.Type.STRING,
                AttributeType.UPPER_FULL_LIKE, iPreparedStatement);
        verify(iPreparedStatement, times(1)).setString(1, "TOT_O");

        // EQU
        String[] strs = new String[] { "toto", "toto%", "tot_o" };
        for (String str : strs) {
            for (AttributeType type : AttributeType.values()) {
                initPreparedStatement();
                DAOTools.set(1, Operator.getOperator(Operator.EQU, str),
                        IValueObject.Type.STRING, type, iPreparedStatement);
                verify(iPreparedStatement, times(1)).setString(1, str);
            }
        }

        // LIKE
        for (String str : strs) {
            for (AttributeType type : AttributeType.values()) {
                initPreparedStatement();
                DAOTools.set(1, Operator.getOperator(Operator.LIKE, str),
                        IValueObject.Type.STRING, type, iPreparedStatement);
                if ("toto".equals(str)) {
                    verify(iPreparedStatement, times(1)).setString(1,
                            str + "%");
                } else {
                    verify(iPreparedStatement, times(1)).setString(1, str);
                }
            }
        }
        // FULL_LIKE
        for (String str : strs) {
            for (AttributeType type : AttributeType.values()) {
                initPreparedStatement();
                DAOTools.set(1, Operator.getOperator(Operator.FULL_LIKE, str),
                        IValueObject.Type.STRING, type, iPreparedStatement);
                if ("toto".equals(str)) {
                    verify(iPreparedStatement, times(1)).setString(1,
                            "%" + str + "%");
                } else {
                    verify(iPreparedStatement, times(1)).setString(1, str);
                }
            }
        }
        // UPPER_LIKE
        for (String str : strs) {
            for (AttributeType type : AttributeType.values()) {
                initPreparedStatement();
                DAOTools.set(1, Operator.getOperator(Operator.UPPER_LIKE, str),
                        IValueObject.Type.STRING, type, iPreparedStatement);
                if ("toto".equals(str)) {
                    verify(iPreparedStatement, times(1)).setString(1,
                            str.toUpperCase() + "%");
                } else {
                    verify(iPreparedStatement, times(1)).setString(1,
                            str.toUpperCase());
                }
            }
        }
        // FULL_UPPER_LIKE
        for (String str : strs) {
            for (AttributeType type : AttributeType.values()) {
                initPreparedStatement();
                DAOTools.set(1,
                        Operator.getOperator(Operator.UPPER_FULL_LIKE, str),
                        IValueObject.Type.STRING, type, iPreparedStatement);
                if ("toto".equals(str)) {
                    verify(iPreparedStatement, times(1)).setString(1,
                            "%" + str.toUpperCase() + "%");
                } else {
                    verify(iPreparedStatement, times(1)).setString(1,
                            str.toUpperCase());
                }
            }
        }
        // UPPER_EQU
        for (String str : strs) {
            for (AttributeType type : AttributeType.values()) {
                initPreparedStatement();
                DAOTools.set(1, Operator.getOperator(Operator.UPPER_EQU, str),
                        IValueObject.Type.STRING, type, iPreparedStatement);
                verify(iPreparedStatement, times(1)).setString(1,
                        str.toUpperCase());
            }
        }

        // DIFF,BIGGER,BIGGER_EQU,SMALLER_EQU,SMALLER
        Operator[] operators = new Operator[] { Operator.DIFF, Operator.BIGGER,
                Operator.BIGGER_EQU, Operator.SMALLER, Operator.SMALLER_EQU };
        for (Operator operator : operators) {
            for (String str : strs) {
                for (AttributeType type : AttributeType.values()) {
                    initPreparedStatement();
                    DAOTools.set(1, Operator.getOperator(operator, str),
                            IValueObject.Type.STRING, type, iPreparedStatement);
                    if (type == AttributeType.UPPER
                            || type == AttributeType.UPPER_LIKE
                            || type == AttributeType.UPPER_FULL_LIKE) {
                        verify(iPreparedStatement, times(1)).setString(1,
                                str.toUpperCase());
                    } else {
                        verify(iPreparedStatement, times(1)).setString(1, str);
                    }
                }
            }
        }

    }

    /**
     * Test timestamp
     *
     * @throws SQLException
     *             erreur de requête sql
     */
    @Test
    public void setTimestamp() throws SQLException {
        // set avec opérateur par défaut par défaut
        Timestamp timestamp = Timestamp
                .valueOf("2007-12-23 09:01:06.000000003");
        initPreparedStatement();
        DAOTools.set(1, timestamp, IValueObject.Type.TIMESTAMP,
                AttributeType.EQUAL, iPreparedStatement);
        verify(iPreparedStatement, times(1)).setTimestamp(1, timestamp);
        initPreparedStatement();
        DAOTools.set(1, timestamp, IValueObject.Type.TIMESTAMP,
                AttributeType.DAY_EQU, iPreparedStatement);
        verify(iPreparedStatement, times(1)).setString(1, "2007.12.23");
        initPreparedStatement();
        DAOTools.set(1, timestamp, IValueObject.Type.TIMESTAMP,
                AttributeType.MONTH_EQU, iPreparedStatement);
        verify(iPreparedStatement, times(1)).setString(1, "2007.12");
        initPreparedStatement();
        DAOTools.set(1, timestamp, IValueObject.Type.TIMESTAMP,
                AttributeType.YEAR_EQU, iPreparedStatement);
        verify(iPreparedStatement, times(1)).setString(1, "2007");

        // EQU
        for (AttributeType type : AttributeType.values()) {
            initPreparedStatement();
            DAOTools.set(1, Operator.getOperator(Operator.EQU, timestamp),
                    IValueObject.Type.TIMESTAMP, type, iPreparedStatement);
            verify(iPreparedStatement, times(1)).setTimestamp(1, timestamp);
        }

        // DAY_EQU
        for (AttributeType type : AttributeType.values()) {
            initPreparedStatement();
            DAOTools.set(1, Operator.getOperator(Operator.DAY_EQU, timestamp),
                    IValueObject.Type.TIMESTAMP, type, iPreparedStatement);
            verify(iPreparedStatement, times(1)).setString(1, "2007.12.23");
        }
        // MONTH_EQU
        for (AttributeType type : AttributeType.values()) {
            initPreparedStatement();
            DAOTools.set(1, Operator.getOperator(Operator.MONTH_EQU, timestamp),
                    IValueObject.Type.TIMESTAMP, type, iPreparedStatement);
            verify(iPreparedStatement, times(1)).setString(1, "2007.12");
        }
        // YEAR_EQU
        for (AttributeType type : AttributeType.values()) {
            initPreparedStatement();
            DAOTools.set(1, Operator.getOperator(Operator.YEAR_EQU, timestamp),
                    IValueObject.Type.TIMESTAMP, type, iPreparedStatement);
            verify(iPreparedStatement, times(1)).setString(1, "2007");
        }

        // DIFF,BIGGER,BIGGER_EQU,SMALLER_EQU,SMALLER
        Operator[] operators = new Operator[] { Operator.DIFF, Operator.BIGGER,
                Operator.BIGGER_EQU, Operator.SMALLER, Operator.SMALLER_EQU };
        for (Operator operator : operators) {
            for (AttributeType type : AttributeType.values()) {
                initPreparedStatement();
                DAOTools.set(1, Operator.getOperator(operator, timestamp),
                        IValueObject.Type.TIMESTAMP, type, iPreparedStatement);
                if (type == AttributeType.DAY_EQU) {
                    verify(iPreparedStatement, times(1)).setString(1,
                            "2007.12.23");
                } else if (type == AttributeType.MONTH_EQU) {
                    verify(iPreparedStatement, times(1)).setString(1,
                            "2007.12");
                } else if (type == AttributeType.YEAR_EQU) {
                    verify(iPreparedStatement, times(1)).setString(1, "2007");
                } else {
                    verify(iPreparedStatement, times(1)).setTimestamp(1,
                            timestamp);
                }
            }
        }

    }

    /**
     * Test clause OR
     *
     * @throws SQLException
     *             erreur de requête sql
     */
    @Test
    public void addClauseOR() throws SQLException {

        StringBuilder sql = new StringBuilder();
        Timestamp timestamp = Timestamp
                .valueOf("2007-12-23 09:01:06.000000003");

        Map<DynamicDAO.Operator, Object> mapDatbis = new HashMap<>();
        Map<DynamicDAO.Operator, Object> map = new HashMap<>();
        map.put(DynamicDAO.Operator.IS_NULL, null);
        map.put(DynamicDAO.Operator.BIGGER_EQU, timestamp);
        mapDatbis.put(DynamicDAO.Operator.OR, map);
        DAOTools.addClause("attribute", mapDatbis, AttributeType.DAY_EQU, sql);
        assertTrue(sql.toString(),
                "(attribute IS NULL OR TO_CHAR(attribute,'yyyy.mm.dd')>=?)"
                        .equals(sql.toString())
                        || "(TO_CHAR(attribute,'yyyy.mm.dd')>=? OR attribute IS NULL)"
                                .equals(sql.toString()));
        sql.setLength(0);

        initPreparedStatement();
        DAOTools.set(1, timestamp, IValueObject.Type.TIMESTAMP,
                AttributeType.DAY_EQU, iPreparedStatement);
        verify(iPreparedStatement, times(1)).setString(1, "2007.12.23");

        // Avec type EQUAL
        DAOTools.addClause("attribute", mapDatbis, AttributeType.EQUAL, sql);
        assertTrue(sql.toString(),
                "(attribute IS NULL OR attribute>=?)".equals(sql.toString())
                        || "(attribute>=? OR attribute IS NULL)"
                                .equals(sql.toString()));
        sql.setLength(0);

        initPreparedStatement();
        DAOTools.set(1, timestamp, IValueObject.Type.TIMESTAMP,
                AttributeType.EQUAL, iPreparedStatement);
        verify(iPreparedStatement, times(1)).setTimestamp(1, timestamp);

    }

    /**
     * Mock prepared statement
     */
    private void initPreparedStatement() {
        iPreparedStatement = mock(PreparedStatement.class);
    }

}
