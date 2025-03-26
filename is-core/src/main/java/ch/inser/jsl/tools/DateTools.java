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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Outils utilitaires concernant les dates.
 * <p>
 * Les méthodes devront être complètement documentées avec Javadoc et respecter les formats suivants :<br>
 * - @author INSER SA
 * - @version : @version <Version> (incrémenter la version à chaque changement)
 */
public class DateTools {

    /** Valeur nulle pour un type primitif "int" */
    public static final int NULL_INT = -999999;

    /**
     * Retourne l'heure (format 24h)
     * 
     * @param date
     *            La date.
     * @return L'heure de la date passée en paramètre ou la constant NULL_INT si on a passé un null
     * 
     * @author INSER SA
     * @version 1.0
     */
    public static int getHourOfDay(Date date) {
        if (date == null) {
            return NULL_INT;
        }

        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * Retourne le jour de la semaine.
     * 
     * @param date
     *            La date.
     * @return Le jour de la semaine de la date passée en paramètre ou la constant NULL_INT si on a passé un null
     * 
     * @author INSER SA
     * @version 1.0
     */
    public static int getDayOfWeek(Date date) {
        if (date == null) {
            return NULL_INT;
        }

        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * Retourne le jour du mois.
     * 
     * @param date
     *            La date dont on désire le jour
     * @return Le jour du mois, entre 1 et 31, ou NULL_INT si la date est null
     * 
     * @author INSER SA
     * @version 1.0
     */
    public static int getDayOfMonth(Date date) {
        if (date == null) {
            return NULL_INT;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Retourne le mois d'une date.
     * 
     * @param date
     *            La date.
     * @return Le mois de la date passée en paramètre, entre 1 et 12, ou NULL_INT si la date est null
     * 
     * @author INSER SA
     * @author INSER SA
     * @version 2.0
     */
    public static int getMonth(Date date) {
        if (date == null) {
            return NULL_INT;
        }

        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        return cal.get(Calendar.MONTH) + 1;
    }

    /**
     * Retourne l'année d'une date.
     * 
     * @param date
     *            La date.
     * @return L'année de la date passée en paramètre ou NULL_INT si la date est null
     * 
     * @author INSER SA
     * @version 1.0
     */
    public static int getYear(Date date) {
        if (date == null) {
            return NULL_INT;
        }

        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    /**
     * Retourne l'année courante de la date système.
     * 
     * @return L'année de la date système
     * 
     * @author INSER SA
     * @author INSER SA
     * @version 1.1
     */
    public static int getYear() {
        return getYear(new Date());
    }

    /**
     * Retourne la date courante dans le format spécifique "yyMMddHHmmss".
     * 
     * @return Une représentation string de la date au format "yyMMddHHmmss".
     * 
     * @author INSER SA
     * @version 1.0
     */
    public static String getSysDateyyMMddHHmmss() {
        return new SimpleDateFormat("yyMMddHHmmss").format(new Date()).toString();
    }

    /**
     * Retourne une date en passant l'année, le mois et le jour comme entiers.
     * 
     * @param year
     *            Année
     * @param month
     *            Mois (janvier = 1, ..., décembre = 12)
     * @param day
     *            Jour
     * @return Une SQL Date (avec millisecondes).
     * 
     * @author INSER SA
     * @author INSER SA
     *         le mois de janvier = 1, et non plus 0
     * @version 2.0
     */
    public static java.sql.Date getDate(int year, int month, int day) {
        Calendar cal = new GregorianCalendar(year, month - 1, day);
        cal.setLenient(false);

        return new java.sql.Date(cal.getTime().getTime());
    }

    /**
     * Retourne une date passé en tant que string. Le format par défaut est "dd.MM.yyyy".
     * 
     * @param date
     *            Date à traiter.
     * @return Une SQL Date (avec millisecondes).
     * 
     * @author INSER SA
     * @author INSER SA
     * @version 2.0
     */
    public static java.sql.Date getDate(String date) throws ParseException {
        return getDate(date, "dd.MM.yyyy");
    }

    /**
     * Retourne une date passé en tant que string.
     * 
     * @param date
     *            Date à traiter.
     * @param pattern
     *            Format de parsing du string de date.
     * @return Une SQL Date (avec millisecondes).
     * 
     * @author INSER SA
     * @version 1.0
     */
    public static java.sql.Date getDate(String date, String pattern) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setLenient(false);
        return new java.sql.Date(sdf.parse(date).getTime());
    }

    /**
     * Retourne le string d'une date en passant l'année, le mois et le jour comme entiers. Le pattern de formatage par défaut est
     * "dd.MM.yyyy".
     * 
     * @param year
     *            Année
     * @param month
     *            Mois (janvier = 1, ..., décembre = 12)
     * @param day
     *            Jour
     * @return Un string représentant la date formatée selon le pattern par défaut.
     * 
     * @author INSER SA
     * @version 1.0
     */
    public static String getDateString(int year, int month, int day) {
        return getDateString(year, month, day, "dd.MM.yyyy");
    }

    /**
     * Retourne le string d'une date en passant l'année, le mois et le jour comme entiers. Le pattern de formatage par défaut est
     * "dd.MM.yyyy".
     * 
     * @param year
     *            Année
     * @param month
     *            Mois (janvier = 1, ..., décembre = 12)
     * @param day
     *            Jour
     * @param pattern
     *            Format de parsing du string de date.
     * @return Un string représentant la date formatée selon le pattern par défaut.
     * 
     * @author INSER SA
     * @version 1.0
     */
    public static String getDateString(int year, int month, int day, String pattern) {
        Calendar cal = new GregorianCalendar(year, month - 1, day);
        cal.setLenient(false);

        return new SimpleDateFormat(pattern).format(cal.getTime());
    }

    /**
     * Shift d'années sur les dates
     */
    public static Date addYear(Date aDate, int aShift) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(aDate);
        cal.add(Calendar.YEAR, aShift);
        return cal.getTime();
    }

    /**
     * Shift de mois sur les dates
     */
    public static Date addMonth(Date aDate, int aShift) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(aDate);
        cal.add(Calendar.MONTH, aShift);
        return cal.getTime();
    }

    /**
     * Shift de jours sur les dates
     */
    public static Date addDay(Date aDate, int aShift) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(aDate);
        cal.add(Calendar.DAY_OF_MONTH, aShift);
        return cal.getTime();
    }

    /**
     * Shift d'années,mois et jours sur les dates
     */
    public static Date addAll(Date aDate, int aYearShift, int aMonthShift, int aDayShift) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(aDate);
        cal.add(Calendar.YEAR, aYearShift);
        cal.add(Calendar.MONTH, aMonthShift);
        cal.add(Calendar.DAY_OF_MONTH, aDayShift);
        return cal.getTime();
    }
}
