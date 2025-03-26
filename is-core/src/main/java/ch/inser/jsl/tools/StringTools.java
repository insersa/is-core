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

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Classe contenant des méthodes relatives aux chaînes de caractères (string).
 * <p>
 * Les méthodes devront être complètement documentées avec Javadoc et respecter les formats suivants :<br>
 * -
 *
 * @author INSER SA
 * @author INSER SA
 *         -
 * @version :
 * @version <Version> (incrémenter la version à chaque changement)
 */
public class StringTools {

    /** Définition de catégorie de logging */
    @SuppressWarnings("unused")
    private static final Log logger = LogFactory.getLog(StringTools.class);

    /**
     * Formatte une valeur numérique double selon un pattern pouvant spécifier le nombre de décimales, le groupement avec séparateurs de
     * milliers et optionnellement le format des valeurs négatives.
     * <p>
     * Cette méthode applique le Locale pour formatter la valeur. Syntaxe des patterns, voir:
     * <a href= "http://java.sun.com/docs/books/tutorial/i18n/format/numberpattern.html"> http
     * ://java.sun.com/docs/books/tutorial/i18n/format/numberpattern.html</a>
     *
     * @param value
     *            Valeur à formater.
     * @param pattern
     *            Pattern à appliquer à la valeur numérique, ex. "###,##0.00".
     * @param locale
     *            Locale à prendre en compte dans le formattage de la valeur numérique. Si null, on prend le Locale par défaut.
     * @return Un string contenant la valeur numérique formattée selon le pattern et le Locale
     *
     * @author INSER SA
     * @version 1.0
     */
    public static String formatNumber(double value, String pattern, Locale locale) {
        Locale loc = locale == null ? Locale.getDefault() : locale;

        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(loc);
        df.applyPattern(pattern);
        return df.format(value);
    }

    /**
     * Formatte une valeur numérique float selon un pattern pouvant spécifier le nombre de décimales, le groupement avec séparateurs de
     * milliers et optionnellement le format des valeurs négatives.
     * <p>
     * Le fait d'utiliser la méthode <code>formatNumber(double value, String pattern, Locale locale)</code> acceptant un <code>double</code>
     * en lui passant une valeur <code>float</code> semble être buguée la première fois qu'on l'emploie...
     * <p>
     * Cette méthode applique le Locale pour formatter la valeur. Syntaxe des patterns, voir:
     * <a href= "http://java.sun.com/docs/books/tutorial/i18n/format/numberpattern.html"> http
     * ://java.sun.com/docs/books/tutorial/i18n/format/numberpattern.html</a>
     *
     * @param value
     *            Valeur à formater.
     * @param pattern
     *            Pattern à appliquer à la valeur numérique, ex. "###,##0.00".
     * @param locale
     *            Locale à prendre en compte dans le formattage de la valeur numérique. Si null, on prend le Locale par défaut.
     * @return Un string contenant la valeur numérique formattée selon le pattern et le Locale
     * @see formatNumber(double value, String pattern, Locale locale)
     *
     * @author INSER SA
     * @version 1.0
     */
    public static String formatNumber(float value, String pattern, Locale locale) {

        return formatNumber((double) value, pattern, locale);
    }

    /**
     * Formatte une valeur numérique long selon un pattern pouvant spécifier le nombre de décimales, le groupement avec séparateurs de
     * milliers et optionnellement le format des valeurs négatives.
     * <p>
     * Cette méthode applique le Locale pour formatter la valeur. Syntaxe des patterns, voir:
     * <a href= "http://java.sun.com/docs/books/tutorial/i18n/format/numberpattern.html"> http
     * ://java.sun.com/docs/books/tutorial/i18n/format/numberpattern.html</a>
     *
     * @param value
     *            Valeur à formater.
     * @param pattern
     *            Pattern à appliquer à la valeur numérique, ex. "###,##0.00".
     * @param locale
     *            Locale à prendre en compte dans le formattage de la valeur numérique. Si null, on prend le Locale par défaut.
     * @return Un string contenant la valeur numérique formattée selon le pattern et le Locale
     *
     * @author INSER SA
     * @version 1.0
     */
    public static String formatNumber(long value, String pattern, Locale locale) {
        Locale loc = locale == null ? Locale.getDefault() : locale;

        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(loc);
        df.applyPattern(pattern);
        return df.format(value);
    }

    /**
     * Remplace toutes les occurrences d'une chaîne de caractères dans un string par une autre chaîne.
     *
     * @param str
     *            chaîne dans laquelle une chaîne doit être remplacée. Null autorisé.
     * @param pattern
     *            chaîne à remplacer. Null autorisé.
     * @param replace
     *            chaîne de remplacement. Null autorisé.
     * @return Un string
     *
     * @author INSER SA
     * @version 1.0
     */
    public static String replace(String str, String pattern, String replace) {
        if (str == null || pattern == null) {
            return str;
        }

        int s = 0;
        int e = 0;
        int patternLength = pattern.length();

        StringBuilder result = new StringBuilder(str.length() * 2);

        while ((e = str.indexOf(pattern, s)) >= 0) {
            result.append(str.substring(s, e));
            if (replace != null) {
                result.append(replace);
            }
            s = e + patternLength;
        }

        result.append(str.substring(s));

        return result.toString();
    }

    /**
     * Remplace l'occurrence d'une chaîne de caractères au début d'un string par une autre chaîne.
     *
     * @param str
     *            chaîne dans laquelle une chaîne doit être remplacée. Null autorisé.
     * @param pattern
     *            chaîne à remplacer. Null autorisé.
     * @param replace
     *            chaîne de remplacement. Null autorisé.
     * @return Un string
     *
     * @author INSER SA
     * @author INSER SA
     * @version 1.1
     */
    public static String replaceStart(String str, String pattern, String replace) {
        if (str == null || pattern == null) {
            return str;
        }

        if (str.indexOf(pattern) != 0) {
            return str;
        }

        String substr = str.substring(pattern.length());

        if (replace == null) {
            return substr;
        }
        return replace + substr;
    }

    /**
     * Remplace toutes l'occurrences d'une chaîne de caractères en fin d'un string par une autre chaîne
     *
     * @param str
     *            chaîne dans laquelle une chaîne doit être remplacée. Null autorisé.
     * @param pattern
     *            chaîne à remplacer. Null autorisé.
     * @param replace
     *            chaîne de remplacement. Null autorisé.
     * @return Un string
     *
     * @author INSER SA
     * @author INSER SA
     * @version 2.0
     */
    public static String replaceEnd(String str, String pattern, String replace) {
        if (str == null || pattern == null) {
            return str;
        }

        int pos = str.lastIndexOf(pattern);

        if (pos != str.length() - pattern.length()) {
            return str;
        }

        String substr = str.substring(0, pos);

        if (replace == null) {
            return substr;
        }
        return substr + replace;
    }

    /**
     * Ajoute un ou plusieurs caractères spécifiés au début d'un texte (padding) pour que le texte ait une longueur spécifiée.
     * <p>
     * L'ajout se fait toujours au début du texte, quelque soit le texte. Par conséquent, cette méthode n'est pas appropriée pour faire du
     * padding sur des représentations textuelles de valeurs numériques négatives ou déjà préfixées.
     *
     * @param number
     *            objet Number à convertir à String avant de traiter. Null autorisé.
     * @param pad
     *            caractère à utiliser pour le padding
     * @param length
     *            longueur désirée pour la chaîne de caractères
     * @return La chaîne de caractère avec le padding nécesaire pour obtenir la longueur spécifiée.
     *
     * @author INSER SA
     * @version 1.0
     */
    public static String addPaddingBeforeString(Number number, char pad, int length) {
        if (number == null) {
            return null;
        }

        return addPaddingBeforeString(number.toString(), pad, length);
    }

    /**
     * Ajoute un ou plusieurs caractères spécifiés au début d'un texte (padding) pour que le texte ait une longueur spécifiée.
     * <p>
     * L'ajout se fait toujours au début du texte, quelque soit le texte. Par conséquent, cette méthode n'est pas appropriée pour faire du
     * padding sur des représentations textuelles de valeurs numériques négatives ou déjà préfixées.
     *
     * @param str
     *            chaîne à traiter. Null autorisé.
     * @param pad
     *            caractère à utiliser pour le padding.
     * @param length
     *            longueur désirée pour la chaîne de caractères
     * @return La chaîne de caractère avec le padding nécesaire pour obtenir la longueur spécifiée.
     *
     * @author INSER SA
     * @version 1.0
     */
    public static String addPaddingBeforeString(String str, char pad, int length) {
        if (str == null) {
            return str;
        }

        int nbrPads = length - str.length();
        if (nbrPads <= 0) {
            return str;
        }

        char[] padding = new char[nbrPads];
        for (int i = 0; i < nbrPads; ++i) {
            padding[i] = pad;
        }

        StringBuilder result = new StringBuilder(length);
        result.append(padding);
        result.append(str);

        return result.toString();
    }

    /**
     * Split un string contenant des éléments délimité par un caractère délimiteur particulier en un tableau de String. Cette méthode
     * retourne aussi les strings vides entre deux délimiteurs consécutifs. N'utilise pas StringTokenizer.
     *
     * @param str
     *            Le string à traiter
     * @param delimiter
     *            Caractère délimiteur séparant les tokens. Le délimiteurs lui-même n'est pas inclus dans le résultat. Ne doit pas être < 0.
     * @return Un tableau de String contenant les différentes parties, ou le string original si aucun délimiteur n'a été trouvé, ou null si
     *         str est null.
     *
     * @author INSER SA
     * @version 1.0
     */
    public static String[] split(String str, int delimiter) {
        if (delimiter < 0) {
            throw new IllegalArgumentException("delimiter < 0 (" + delimiter + ")");
        }

        if (str == null) {
            return null;
        }

        // Compte le nombre de délimiteurs en parcourant le string
        int count = 0;
        for (int i = 0, n = str.length(); i < n; i++) {
            if (str.charAt(i) == delimiter) {
                ++count;
            }
        }

        // Si aucun délimiteur trouvé, on retourne le string original
        if (count == 0) {
            return new String[] { str };
        }

        String[] result = new String[count + 1];
        count = 0;
        int from = 0;
        int idx;

        while ((idx = str.indexOf(delimiter, from)) >= 0) {
            result[count++] = str.substring(from, idx);
            // saute le délimiteur déjà trouvé
            from = idx + 1;
        }

        result[count] = str.substring(from);
        return result;
    }

    /**
     * Split un string contenant des éléments délimités par un ensemble de caractères délimitateurs en un tableau de String. Cette méthode
     * ne retourne que les string non vides entre deux délimiteurs
     *
     * @param str
     *            Le string à traiter. Null autorisé.
     * @param delimiters
     *            Caractères délimitateurs pour séparer les tokens. Les délimitateurs eux-mêmes ne sont pas inclus dans les tokens. Null
     *            autorisé.
     * @return Un tableau de String contenant tous les tokens du string original, ou null si str est null.
     *
     * @author INSER SA
     * @version 1.0
     */
    public static String[] split(String str, String delimiters) {
        if (str == null) {
            return null;
        }

        if (delimiters == null) {
            return new String[] { str };
        }

        StringTokenizer st = new StringTokenizer(str, delimiters);

        String[] result = new String[st.countTokens()];

        int i = 0;
        while (st.hasMoreTokens()) {
            result[i++] = st.nextToken();
        }

        return result;
    }

    /**
     * Split un string contenant des éléments délimités par un ensemble de caractères délimitateurs en un tableau de String. Cette méthode
     * retourne les string vides entre deux délimiteurs.
     *
     * <strong>Attention</strong> :<br>
     * <code>splitWithEmpty()</code> ne fonctionne pas sur le même principe que <code>split</code>. En effet, delimiters est testé en
     * entier, tandis que dans <code>split()</code>, on utilise chacun des caractères du string delimiters comme délimitateur à la façon de
     * <code>StringTokenizer()</code>. (AL)
     *
     * @param str
     *            Le string à traiter. Null autorisé.
     * @param delimiters
     *            Caractères délimitateurs pour séparer les tokens. Les délimitateurs eux-mêmes ne sont pas inclus dans les tokens. Null
     *            autorisé.
     * @return Un tableau de String contenant tous les tokens du string original.
     * @todo AL/20-mar-2002: appliquer "best pratices" de performance pour cette méthode.
     *
     * @author INSER SA
     * @version 1.0
     */
    public static String[] splitWithEmpty(String aStr, String delimiters) {
        String str = aStr;
        if (str == null) {
            return null;
        }

        if (delimiters == null) {
            return new String[] { str };
        }

        int i = 0;
        List<String> result = new ArrayList<>();

        while ((i = str.indexOf(delimiters)) >= 0) {
            result.add(str.substring(0, i));

            if (i >= str.length()) {
                break;
            }

            str = str.substring(i + 1);
        }

        result.add(str);
        String[] resultStr = new String[result.size()];

        for (int j = 0; j < result.size(); j++) {
            resultStr[j] = result.get(j);
        }

        return resultStr;
    }

    /**
     * Fusionne un tableau de strings en un string unique comportant les éléments délimités par un string donné.
     *
     * @param aItems
     *            Les éléments à fusionner dans un string.
     * @param aDelimiter
     *            String délimitateur pour séparer les éléments, ou null.
     * @return Un String contenant tous les items délimités.
     *
     * @author INSER SA
     * @version 1.0
     */
    public static String merge(String[] aItems, String aDelimiter) {
        if (aItems == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(256);
        append(aItems, aDelimiter, builder);
        return builder.toString();
    }

    /**
     * Ajoute la fusion d'un tableau de strings en un string unique comportant les éléments délimités par un string donné.
     *
     * @param aItems
     *            Les éléments à fusionner dans un string.
     * @param aDelimiter
     *            String délimitateur pour séparer les éléments, ou null.
     * @param builder
     *            Le <code>StringBuilder</code> au quel rajouter le text.
     */
    public static void append(Object[] aItems, String aDelimiter, StringBuilder builder) {
        if (aItems == null) {
            return;
        }

        boolean first = true;
        for (Object item : aItems) {
            if (first) {
                first = false;
            } else if (aDelimiter != null) {
                builder.append(aDelimiter);
            }
            builder.append(item);
        }
    }

    /**
     * Enlève les caractères spécifiés d'un string.
     *
     * @param str
     *            String à traiter. Null autorisé.
     * @param chars
     *            String contenant les caractères à supprimer de la chaîne de caractères. Null autorisé.
     * @return Si aucun caractère à ôter n'a été trouvé, retourne une référence sur le string original, sinon retourne un string sans les
     *         caractères spécifiés.
     *
     * @author INSER SA
     * @version 1.0
     */
    public static String stripChars(String str, String chars) {
        if (str == null || chars == null) {
            return str;
        }

        boolean mustStrip;
        boolean isStripped = false;
        char[] strip = chars.toCharArray();
        int stripLen = strip.length;
        int strLen = str.length();
        StringBuilder result = new StringBuilder(strLen);

        for (int i = 0; i < strLen; i++) {
            mustStrip = false;
            char ch = str.charAt(i);

            for (int j = 0; j < stripLen; j++) {
                if (ch == strip[j]) {
                    mustStrip = true;
                    isStripped = true;
                    break;
                }
            }

            if (!mustStrip) {
                result.append(ch);
            }
        }

        return isStripped ? result.toString() : str;
    }

    /**
     * Enlève les caractères non numérique d'un string.
     *
     * @param str
     *            String à traiter.
     * @return Si aucun caractère à ôter n'a été trouvé, retourne une référence sur le string original, sinon retourne un string sans les
     *         caractères non numériques.
     *
     * @author INSER SA
     * @version 1.0
     */
    public static String stripNonNumericChars(String str) {
        if (str == null) {
            return null;
        }

        boolean isStripped = false;
        int strLen = str.length();
        StringBuilder result = new StringBuilder(strLen);

        for (int i = 0; i < strLen; i++) {
            char ch = str.charAt(i);

            if (ch >= '0' && ch <= '9') {
                result.append(ch);
            } else {
                isStripped = true;
            }
        }

        return isStripped ? result.toString() : str;
    }

    /**
     * Enlève les caractères dans les intervalle 0-31 and 127-159 (non ISO-8859-1)
     *
     * @param str
     *            String à traiter.
     * @return Si aucun caractère à ôter n'a été trouvé, retourne une référence sur le string original, sinon retourne un string sans les
     *         caractères non numériques.
     *
     * @author INSER SA
     * @version 1.0
     */
    public static String stripNonISO88591(String str) {
        return stripNonISO88591(str, false);
    }

    /**
     * Possibilité de conserver les retours chariots
     *
     * @param str
     * @param keepReturn
     * @return
     */
    public static String stripNonISO88591(String str, boolean keepReturn) {
        if (str == null) {
            return null;
        }

        boolean isStripped = false;
        int strLen = str.length();
        StringBuilder result = new StringBuilder(strLen);

        for (int i = 0; i < strLen; i++) {
            char ch = str.charAt(i);
            if (keepReturn && ch == '\n') {
                result.append(ch);
            } else {

                if (ch >= 32 && ch <= 126 || ch >= 160 && ch <= 255 || ch == '\n') {
                    result.append(ch);
                } else {
                    isStripped = true;
                }
            }
        }
        return isStripped ? result.toString() : str;
    }

    /**
     * Enlève les caractères spéciaux dans un string json. Certaines caractères spéciaux tels que "\t" ont été transformés en sous-strings
     * de deux caractères, ex. "\" + "t", donc la méthode stripNonISO88591 qui parcours le string char par char ne capture pas ces
     * caractères spéciaux
     *
     * \b Backspace (ascii code 08)
     *
     * \f Form feed (ascii code 0C)
     *
     * \n New line
     *
     * \r Carriage return
     *
     * \t Tab
     *
     * Possibilité de conserver les retours chariots \n
     *
     * @param aJsonString
     *            String représentant un objet JSON
     * @param keepReturn
     *            true s'il faut garder les \n
     * @return le json string sans les caractères spéciaux
     */
    public static String stripSpecialCharsJson(String aJsonString, boolean aKeepReturn) {
        if (aJsonString == null) {
            return null;
        }

        String[] specialChars = new String[] { "\\\\n", "\\\\b", "\\\\f", "\\\\r", "\\\\t" };
        if (aKeepReturn) {
            specialChars = new String[] { "\\\\b", "\\\\f", "\\\\r", "\\\\t" };
        }
        String strippedStr = aJsonString;

        for (String specialChar : specialChars) {
            strippedStr = strippedStr.replaceAll(specialChar, "");
        }
        return strippedStr;
    }

    /**
     * Formate un no de téléphone au format numérique en un string au format "iii nnn nn nn" ou "ii nnn nn nn" où "i" représente
     * l'indicatif.
     * <p>
     * Exemple:<br>
     * 17568811 => "01 756 88 11" (8 car.) 216437711 => "021 643 77 11" (9 car.) 41014256312 => "0041014256312" (11 car.) 410274256312 =>
     * "00410274256312" (12 car.) 3905211234567 => "3905211234567" (13 car.)
     * <p>
     * Les numéros long (> 9 car.) non suisses (ne commançant pas par '410' ne sont pas formatés.
     *
     * @param phoneNumber
     *            Numéro de téléphone sous forme d'un objet Long
     * @return Un string représentant le no de téléphone formaté.
     *
     * @author INSER SA
     * @author INSER SA
     * @version 1.1
     */
    public static String formatPhoneNumber(Long phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }

        String str = phoneNumber.toString();
        int strLen = str.length();
        StringBuilder result = new StringBuilder(strLen + 16);

        // Longueur de l'indicatif
        int prefixLen = 0;

        switch (strLen) {
            case 8:
                prefixLen = 1;
                break;
            case 9:
                prefixLen = 2;
                break;
            case 11:
            case 12:
                if ("410".equals(str.substring(0, 3))) {
                    // no suisse avec
                    // indicatif du pays
                    result.append("00");
                    result.append(str);
                    return result.toString();
                }
                return str;

            default:
                return str;
        }

        // Indicatif
        result.append("0");
        result.append(str.substring(0, prefixLen));

        // 1ère partie numéro
        result.append(" ");
        result.append(str.substring(prefixLen, prefixLen + 3));

        // 2ème partie numéro
        result.append(" ");
        result.append(str.substring(prefixLen + 3, prefixLen + 5));

        // 3ème partie numéro
        result.append(" ");
        result.append(str.substring(prefixLen + 5));

        return result.toString();
    }

    /**
     * Formatte des valeurs selon un pattern.
     * <p>
     * Cette méthode applique le Locale pour formatter la valeur. Syntaxe des patterns, voir:
     * <a href= "http://java.sun.com/docs/books/tutorial/i18n/format/numberpattern.html"> http
     * ://java.sun.com/docs/books/tutorial/i18n/format/numberpattern.html</a>
     *
     * @param aParams
     *            Les valeurs à formatter.
     * @param aPattern
     *            Pattern à applique, ex. "###,##0.00".
     * @param aLocale
     *            Locale à prendre en compte dans le formattage.
     *
     * @return Un string contenant les valeurs formattés selon le pattern et le Locale et le Locale
     *
     * @author INSER SA
     * @version 1.0
     */
    public static String format(Object[] aParams, String aPattern, Locale aLocale) {

        MessageFormat mf = new MessageFormat(aPattern);
        mf.setLocale(aLocale);
        return mf.format(aParams);
    }

    /**
     * Construit une clause LIKE pour une requête SQL qui suit les règles suivantes:<br>
     * - on force la valeur en majuscule - si la valeur ne contient ni "%", ni "_" alors on fait un "LIKE '%<value>%'" - dans le cas
     * contraire, on fait un "LIKE <value>" en prenant la valeur telle quelle.
     *
     * @param attribute
     *            Le nom de l'attribut
     * @param value
     *            La valeur de l'attribut
     * @return Un String contenant une clause de type "LIKE ...", ou null si la valeur ou l'attribut sont nuls.
     *
     * @author INSER SA
     * @version 1.0
     */
    public static String buildLikeClause(String attribute, String value) {
        if (attribute == null || value == null) {
            return null;
        }

        String upperValue = value.toUpperCase();
        upperValue = replace(upperValue, "'", "''"); // double les
        // apostrophes

        StringBuilder sb = new StringBuilder(64);
        sb.append("UPPER(");
        sb.append(attribute);
        sb.append(") LIKE '");

        // Si pas de "%" trouvé, on fait LIKE '%valeur%'
        if (value.indexOf('%') == -1 && value.indexOf('_') == -1) {
            sb.append("%");
            sb.append(upperValue);
            sb.append("%");
        } else {
            // Si trouvé un "%" ou "_", on prend telle quelle la valeur initiale
            sb.append(upperValue);
        }

        sb.append("' {escape '\\'}");
        return sb.toString();
    }

    /**
     * Indique s'il existe une suite de n caractères identiques dans un string.
     *
     * @param str
     *            String testé
     * @param charCount
     *            Nombre de caractères de la suite
     * @return True si une suite de charCount caractères a été trouvée dans le string
     *
     * @author INSER SA
     * @version 1.0
     */
    public static boolean isCharSuite(String str, int charCount) {
        int pos = indexOfCharSuite(str, charCount);
        return pos != -1 ? true : false;
    }

    /**
     * Localise une suite de n caractères identiques dans un string.
     *
     * @param str
     *            String testé
     * @param charCount
     *            Nombre de caractères de la suite
     * @return La position dans le string du début de la suite de n caractères.
     *
     * @author INSER SA
     * @version 1.0
     */
    public static int indexOfCharSuite(String str, int charCount) {
        if (str == null || charCount <= 0) {
            return -1;
        }

        if (charCount == 1) {
            return 0;
        }

        StringBuilder pattern = new StringBuilder(charCount);
        int max = str.length() - charCount + 1;
        int pos;

        for (int i = 0; i < max; i++) {
            pattern.setLength(0);
            char ch = str.charAt(i);

            for (int j = 0; j < charCount; j++) {
                pattern.append(ch);
            }

            if ((pos = str.indexOf(pattern.toString(), i)) != -1) {
                return pos;
            }
        }

        return -1;
    }
}