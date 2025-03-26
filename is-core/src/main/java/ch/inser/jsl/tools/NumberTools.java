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

package ch.inser.jsl.tools;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.util.Collection;
import java.util.List;

import ch.inser.dynamic.common.IDAOResult;

/**
 * Outils utilitaires pour le traitenement de nombres.
 *
 * @version 1.0
 * @author INSER SA
 */
public final class NumberTools {

    private NumberTools() {
        // Just hide constructor!
    }

    public static Long multiplyLong(Long lg, int mult) {
        if (lg == null) {
            return null;
        }
        return mult * lg.longValue();
    }

    // ********************************************************getInteger(START)
    public static Integer getInteger(Object obj) {
        if (obj instanceof BigDecimal) {
            return getInteger((BigDecimal) obj);
        }
        if (obj instanceof Long) {
            return getInteger((Long) obj);
        }
        if (obj instanceof Integer) {
            return (Integer) obj;
        }
        if (obj instanceof String) {
            return getInteger((String) obj);
        }
        if (obj instanceof IDAOResult) {
            return getInteger(((IDAOResult) obj).getValue());
        }
        return null;
    }

    public static Integer getInteger(BigDecimal bigD) {
        if (bigD != null) {
            return bigD.intValue();
        }
        return null;
    }

    public static Integer getInteger(Long aLong) {
        if (aLong != null) {
            return aLong.intValue();
        }
        return null;
    }

    public static Integer getInteger(String aString) {
        try {
            if (aString != null) {
                return Integer.valueOf(aString);
            }
        } catch (@SuppressWarnings("unused") Exception e) {// NOSONAR
            // Just do nothing!
        }
        return null;
    }

    // **********************************************************getInteger(END)

    // ***********************************************************getLong(START)
    public static Long getLong(Object obj) {
        if (obj instanceof BigDecimal) {
            return getLong((BigDecimal) obj);
        }
        if (obj instanceof Integer) {
            return getLong((Integer) obj);
        }
        if (obj instanceof String) {
            return Long.parseLong((String) obj);
        }
        if (obj instanceof Long) {
            return (Long) obj;
        }
        if (obj instanceof IDAOResult) {
            return getLong(((IDAOResult) obj).getValue());
        }
        if (obj instanceof Boolean) {
            return getLong((Boolean) obj);
        }
        if (obj instanceof BigInteger) {
            return getLong((BigInteger) obj);
        }
        return null;
    }

    public static Long getLong(BigDecimal bigD) {
        if (bigD != null) {
            return bigD.longValue();
        }
        return null;
    }

    public static Long getLong(Integer intg) {
        if (intg != null) {
            return intg.longValue();
        }
        return null;
    }

    public static Long getLong(Boolean bool) {
        if (bool != null) {
            return bool.booleanValue() ? 1L : 0L;
        }
        return null;
    }

    public static Long getLong(BigInteger bigInt) {
        if (bigInt != null) {
            return bigInt.longValue();
        }
        return null;
    }

    // *************************************************************getLong(END)

    // *********************************************************getDouble(START)

    public static Double getDouble(Object obj) {
        if (obj instanceof BigDecimal) {
            return getDouble((BigDecimal) obj);
        }
        if (obj instanceof Integer) {
            return getDouble((Integer) obj);
        }
        if (obj instanceof String) {
            return getDouble((String) obj);
        }
        if (obj instanceof Long) {
            return getDouble((Long) obj);
        }
        if (obj instanceof Float) {
            return getDouble((Float) obj);
        }
        if (obj instanceof Double) {
            return (Double) obj;
        }
        if (obj instanceof IDAOResult) {
            return getDouble(((IDAOResult) obj).getValue());
        }
        if (obj instanceof BigInteger) {
            return getDouble((BigInteger) obj);
        }
        return null;
    }

    public static Double getDouble(Integer intg) {
        if (intg != null) {
            return intg.doubleValue();
        }
        return null;
    }

    public static Double getDouble(Long lg) {
        if (lg != null) {
            return lg.doubleValue();
        }
        return null;
    }

    public static Double getDouble(Float fl) {
        if (fl != null) {
            return fl.doubleValue();
        }
        return null;
    }

    public static Double getDouble(String str) {
        if (str != null) {
            return Double.parseDouble(str);
        }
        return null;
    }

    public static Double getDouble(BigDecimal bigD) {
        if (bigD != null) {
            return bigD.doubleValue();
        }
        return null;
    }

    public static Double getDouble(BigInteger bigInt) {
        if (bigInt != null) {
            return bigInt.doubleValue();
        }
        return null;
    }

    /**
     * Calcul la moyenne double de la liste, en éliminant les null!
     *
     * @param aList
     *            la liste contenant les éléments de la moyenne
     * @return la moyenne
     */
    public static Double averageOf(List<Object> aList) {
        if (aList == null) {
            return null;
        }
        double sum = 0;
        int nbr = 0;
        for (Object obj : aList) {
            Double objD = getDouble(obj);
            if (objD != null) {
                sum = sum + objD;
                nbr++;
            }
        }
        if (nbr == 0) {
            return null;
        }
        return sum / nbr;
    }

    // ***********************************************************getDouble(END)

    // *****************************************************getBigDecimal(START)
    public static BigDecimal getBigDecimal(Object obj) {
        if (obj instanceof Integer) {
            return getBigDecimal((Integer) obj);
        }
        if (obj instanceof Double) {
            return getBigDecimal((Double) obj);
        }
        if (obj instanceof BigDecimal) {
            return (BigDecimal) obj;
        }
        if (obj instanceof String) {
            return getBigDecimal((String) obj);
        }
        if (obj instanceof IDAOResult) {
            return getBigDecimal(((IDAOResult) obj).getValue());
        }
        return null;
    }

    public static BigDecimal getBigDecimal(Integer a) {
        if (a == null) {
            return null;
        }
        return BigDecimal.valueOf(a);
    }

    public static BigDecimal getBigDecimal(Double a) {
        if (a == null) {
            return null;
        }
        return BigDecimal.valueOf(a);
    }

    public static BigDecimal getBigDecimal(String a) {
        if (a == null) {
            return null;
        }
        return BigDecimal.valueOf(getDouble(a));
    }

    // *******************************************************getBigDecimal(END)

    // *****************************************************getBigInteger(START)
    public static BigInteger getBigInteger(Object obj) {
        if (obj instanceof Integer) {
            return getBigInteger((Integer) obj);
        }
        if (obj instanceof Double) {
            return getBigInteger((Double) obj);
        }
        if (obj instanceof BigInteger) {
            return (BigInteger) obj;
        }
        if (obj instanceof String) {
            return getBigInteger((String) obj);
        }
        if (obj instanceof Long) {
            return getBigInteger((Long) obj);
        }
        if (obj instanceof IDAOResult) {
            return getBigInteger(((IDAOResult) obj).getValue());
        }
        return null;
    }

    public static BigInteger getBigInteger(Integer a) {
        if (a == null) {
            return null;
        }
        return BigInteger.valueOf(a);
    }

    public static BigInteger getBigInteger(Double a) {
        if (a == null) {
            return null;
        }
        return BigInteger.valueOf(getLong(a));
    }

    public static BigInteger getBigInteger(String a) {
        if (a == null) {
            return null;
        }
        return BigInteger.valueOf(getLong(a));
    }

    public static BigInteger getBigInteger(Long a) {
        if (a == null) {
            return null;
        }
        return BigInteger.valueOf(getLong(a));
    }

    // *******************************************************getBigInteger(END)

    /**
     * Vérifie que la valeur passée se trouve dans la liste d'entier
     *
     * @param value
     * @param values
     * @return
     */
    public static boolean testIn(Integer value, int[] values) {
        if (value != null) {
            return testIn(value.intValue(), values);
        }
        return false;
    }

    public static boolean testIn(int value, int[] values) {
        for (int i : values) {
            if (value == i) {
                return true;
            }
        }
        return false;
    }

    public static boolean testIn(Long value, int[] values) {
        if (value != null) {
            return testIn(value.intValue(), values);
        }
        return false;
    }

    /**
     * Vérifie qu'au moins une valeur de la collection se trouve dans la liste d'entier
     *
     * @param col
     * @param values
     * @return
     */
    public static boolean testIn(Collection<Object> col, long[] values) {
        if (col != null) {
            for (long i : values) {
                if (col.contains(i)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Test l'égalité entre un objet de classe Integer et une valeur de type int.
     *
     * @param value1
     *            L'objet.
     * @param value2
     *            La valeur de type int.
     *
     * @return true si la valeur de l'objet est égale à la valeur passée en parametre et différent de null.
     */
    public static boolean areEquals(Integer value1, int value2) {
        return value1 != null && value1 == value2;
    }

    public static boolean areEquals(Long value1, long value2) {
        return value1 != null && value1 == value2;
    }

    public static boolean areEquals(Long value1, Integer value2) {
        return value1 == null && value2 == null || value1 != null && value2 != null && value1.longValue() == value2.longValue();
    }

    /**
     * Test la difference entre un objet de classe Integer et une valeur de type int.
     *
     * @param value1
     *            L'objet.
     * @param value2
     *            La valeur de type int.
     *
     * @return true si la valeur de l'objet est differente à la valeur pass�e en parametre, ou si au l'objet est null.
     */
    public static boolean different(Integer value1, int value2) {
        return value1 != null ? value1 != value2 : true;
    }

    public static boolean different(Long value1, long value2) {
        return value1 != null ? value1 != value2 : true;
    }

    public static boolean different(Integer value1, Integer value2) {
        return !areEquals(value1, value2);
    }

    /**
     * Test si un objet de classe Integer est plus grand d'une valeur de type int.
     *
     * @param value1
     *            L'objet.
     * @param value2
     *            La valeur de type int.
     *
     * @return true si la valeur de l'objet est plus grande de la valeur pass�e en parametre ou si au moin un des deux objets est null.
     */
    public static boolean bigger(Integer value1, int value2) {
        return value1 == null || value1 > value2;
    }

    public static boolean bigger(Long value1, long value2) {
        return value1 == null || value1 > value2;
    }

    public static boolean bigger(Integer value1, Integer value2) {
        return value1 == null || value2 == null || value1 > value2;
    }

    /**
     * Test si un objet de classe Integer est plus petit d'un autre objet de classe Integer.
     *
     * @param value1
     *            Le premier objet.
     * @param value2
     *            Le deuxi�me objet.
     *
     * @return true si la valeur du premier objet est plus petite de la valeur du deuxi�me objet, ou si au moins un des objets est null.
     */
    public static boolean smaller(Integer value1, Integer value2) {
        return value1 == null || value2 == null || value1 < value2;
    }

    public static boolean smaller(Integer value1, int value2) {
        return value1 == null || value1 < value2;
    }

    public static boolean smaller(Long value1, long value2) {
        return value1 == null || value1 < value2;
    }

    /**
     * Test si un objet de classe Date est plus grand ou �gale d'un autre objet de classe Date.
     *
     * @param value1
     *            Le premier objet.
     * @param value2
     *            Le deuxi�me objet.
     *
     * @return true si la valeur du premier objet est plus grande ou �gale de la valeur du deuxi�me objet, ou si au moins un des objets est
     *         null.
     */
    public static boolean biggerEquals(Date value1, Date value2) {
        return value1 == null || value2 == null || value1.after(value2) || value1.equals(value2);
    }

    public static boolean biggerEquals(Long value1, Long value2) {
        return value1 == null || value2 == null || value1 >= value2;
    }

    public static boolean biggerEquals(Integer value1, Integer value2) {
        return value1 == null || value2 == null || value1 >= value2;
    }

    public static boolean biggerEquals(Integer value1, int value2) {
        return value1 == null || value1 >= value2;
    }

    /**
     * Test si un objet de classe Integer est plus petit ou �gale d'un autre objet de classe Integer.
     *
     * @param value1
     *            Le premier objet.
     * @param value2
     *            Le deuxi�me objet.
     *
     * @return true si la valeur du premier objet est plus petite ou égale de la valeur du deuxi�me objet, ou si au moins un des objets est
     *         null.
     */
    public static boolean smallerEquals(Integer value1, Integer value2) {
        return value1 == null || value2 == null || value1 <= value2;
    }

    public static boolean smallerEquals(Integer value1, int value2) {
        return value1 == null || value1 <= value2;
    }

    public static boolean smallerEquals(Long value1, long value2) {
        return value1 == null || value1 <= value2;
    }

    public static boolean smallerEquals(Long value1, Long value2) {
        return value1 == null || value2 == null || value1 <= value2;
    }

}
