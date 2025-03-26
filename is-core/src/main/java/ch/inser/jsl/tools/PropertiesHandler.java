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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author INSER SA
 * @version 1.0
 */
public class PropertiesHandler implements FileChangeListener {

    /** Définition de la catégorie de logging */
    private static final Log logger = LogFactory.getLog(PropertiesHandler.class);

    private static final PropertiesHandler cInstance = new PropertiesHandler();

    private boolean iUseClasspath = false;

    private long iReloadPropertiesInterval;

    private Properties iProperties;

    private String iFilename;

    private String iLocationSystemProperty;

    private String iLocation;

    private PropertiesHandler() {
        iProperties = new Properties();
    }

    public static PropertiesHandler getInstance() {
        return cInstance;
    }

    /**
     * Méthode à implémenter de par l'interface.
     *
     * @param fileName
     */
    @Override
    public void fileChanged(String fileName) {
        reloadProperties();
    }

    public synchronized long getReloadPropertiesInterval() {
        return iReloadPropertiesInterval;
    }

    public synchronized void setReloadPropertiesInterval(long reloadPropertiesInterval) {
        iReloadPropertiesInterval = reloadPropertiesInterval;

        String filename = iLocation != null ? iLocation : iFilename;

        // Ne plus recharger les propriétés ou intervalle à 0.
        if (reloadPropertiesInterval == 0L) {
            FileMonitor.getInstance().removeFileChangeListener(filename);
            return;
        }

        // Définit le listener de fichier
        try {
            FileMonitor.getInstance().addFileChangeListener(this, filename, iReloadPropertiesInterval);
        } catch (FileNotFoundException e) {
            logger.error("Error adding file listener", e);
        }
    }

    /**
     * Recharge les propriétés.
     */
    private void reloadProperties() {
        InputStream in = null;

        try {
            if (iUseClasspath) {
                in = getClass().getClassLoader().getResourceAsStream(iFilename);
            } else if (iLocation != null) {
                in = new FileInputStream(iLocation);
            } else {
                in = new FileInputStream(iFilename);
            }

            iProperties.load(in);
        } catch (IOException e) {
            logger.error("Error reloading properties", e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                logger.error("Error closing stream", ex);
            }
        }

        String reloadProps = iProperties.getProperty("reloadProperties", "false");
        boolean mustReload = Boolean.valueOf(reloadProps).booleanValue();

        if (mustReload == true) {
            String reloadPropertiesInterval = iProperties.getProperty("reloadPropertiesInterval", "60000");
            setReloadPropertiesInterval(Long.parseLong(reloadPropertiesInterval));
        } else {
            setReloadPropertiesInterval(0L);
        }

        // Définit les valeurs par défaut
        if (iProperties.getProperty("toolbarSize") == null) {
            iProperties.put("toolbarSize", "big");
        }
    }

    public void setFileLocation(String filename, boolean useClasspath) {
        iFilename = filename;
        iUseClasspath = useClasspath;
        reloadProperties();
    }

    public void setFileLocation(String filename, String locationSystemProperty) {
        iFilename = filename;
        iLocationSystemProperty = locationSystemProperty;

        if (iLocationSystemProperty != null) {
            iLocation = System.getProperty(iLocationSystemProperty) + File.separator + iFilename;
        }

        reloadProperties();
    }

    public String getProperty(String key) {
        return iProperties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return iProperties.getProperty(key, defaultValue);
    }

    public void setProperty(String key, String value) {
        iProperties.put(key, value);
    }
}