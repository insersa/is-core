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

package ch.inser.dynaplus.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.inser.dynamic.common.IContextManager;
import ch.inser.dynaplus.help.IApplicationHelpBean;

/**
 * Classe utilitaire pour accéder aux ressources (fichiers de configuration générique) <br/>
 * 1) Cherche le ressource dans la BD (Help) <br/>
 * 2) Si ressource existe, retourne-le, sinon continue avec (3)<br/>
 * 3) Cherche le ressource dans le fichier de configuration <br/>
 * 4) Si ressource existe, retourne-le, sinon retourne l'id (clé)
 *
 * @author INSER SA
 *
 */
public class RSRUtil {

    /** Ressources dans la BD, table Help */
    private static IApplicationHelpBean cHelp;

    /** Le context manager */
    private static IContextManager cContextManager;

    /**
     * Constructeur caché, toute les méthodes étant statiques!
     */
    private RSRUtil() {
        // Pour cacher le constructeur
    }

    /** Définition de la catégorie de logging */
    private static final Log logger = LogFactory.getLog(RSRUtil.class);

    /**
     * Recherche un ressource soit dans la BD (Help), soit dans un fichier de ressource
     *
     * @param locale
     *            locale de ressource désiré
     * @param id
     *            la clé du ressource
     * @param ressourceName
     *            le nom du fichier de config
     * @return le ressource ou la clé
     */
    public static String getRessource(Locale locale, String id, String ressourceName) {
        if (locale == null || id == null) {
            return "Null parameters!";
        }

        // Cherche dans la BD (Help)
        if (cHelp != null) {
            String ressource = cHelp.getLabelTexts(locale).get(id);
            if (ressource != null && !ressource.equals(id)) {
                return ressource;
            }
        }

        // Cherche dans un fichier de ressource
        if (ressourceName == null) {
            return "Null parameters!";
        }
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try {
            return ResourceBundle.getBundle(ressourceName, locale, loader).getString(id);
        } catch (@SuppressWarnings("unused") MissingResourceException e) {// NOSONAR
            // Don't loge the exception...it is just a warning!
            logger.warn("Problem getting ressource " + locale + "/" + id + "/" + ressourceName);
            return id;
        }
    }

    /**
     * Recherche un ressource soit dans la BD (Help), soit dans un fichier de ressource. Fait du formattage du text.
     *
     * @param locale
     *            locale de ressource désiré
     * @param id
     *            la clé du ressource
     * @param params
     *            paramètres de formattage
     * @param ressourceName
     *            nom du fichier de ressource
     * @return le ressource formatté ou la clé
     */
    public static String getRessource(Locale locale, String id, Object[] params, String ressourceName) {
        if (locale == null || id == null) {
            return "Null parameters!";
        }
        String ressource = null;

        // Cherche dans la BD (Help)
        if (cHelp != null) {
            String helpLabel = cHelp.getLabelTexts(locale).get(id);
            if (helpLabel != null && !helpLabel.equals(id)) {
                ressource = helpLabel;
            }
        }

        // Cherche dans un fichier de ressource
        if (ressourceName == null) {
            return "Null parameters!";
        }

        if (ressource == null) {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            try {
                ressource = ResourceBundle.getBundle(ressourceName, locale, loader).getString(id);

            } catch (@SuppressWarnings("unused") MissingResourceException e) {// NOSONAR
                // Don't log the exception it is just a warning
                logger.warn("Problem getting ressource " + locale + "/" + id + "/" + ressourceName);
                return id;
            }
        }

        // Formattage du text
        MessageFormat mf = new MessageFormat(ressource, locale);
        ressource = mf.format(params, new StringBuffer(), null).toString();
        return ressource;
    }

    /**
     * Prise du nom de fichier par défaut dans le fichier .properties
     *
     * @param aLocale
     *            locale du ressource désiré
     * @param aId
     *            la clé du ressource
     * @return le ressource ou la clé
     */
    public static String getRessource(Locale aLocale, String aId) {
        return getRessource(aLocale, aId, cContextManager.getProperty("resource.filename"));
    }

    /**
     * Prise du nom de fichier par défaut dans le fichier .properties. Avec formattage.
     *
     * @param aLocale
     *            locale du ressource désiré
     * @param aId
     *            la clé du ressource
     * @param params
     *            paramètres de formattage
     * @return le ressource ou la clé
     */
    public static String getRessource(Locale aLocale, String aId, Object[] params) {
        return getRessource(aLocale, aId, params, cContextManager.getProperty("resource.filename"));
    }

    /**
     * @param iContextManager
     *            the ContextManager to set
     */
    public static void setContextManager(IContextManager aContextManager) {
        cContextManager = aContextManager;
        cHelp = (IApplicationHelpBean) cContextManager.getHelpBean();
    }
}
