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

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.inser.dynamic.common.IContextManager;
import ch.inser.dynamic.util.SchemaInfo;
import ch.inser.dynamic.util.VOInfo;

public class AbstractFactory {

    /**
     * Logger
     */
    private static final Log logger = LogFactory.getLog(AbstractFactory.class);

    /**
     * Container de l'ensemble des objects du factory, ex. business objects, data access objects
     */
    protected Map<String, Object> iFactoryObjects = new HashMap<>();

    /** ContextManager */
    protected IContextManager iCtx;

    /** Nom de factory: "dao", "bo", "bp", "vo" */
    private String iName;

    /**
     * Constructor for allowing deserialization of serializable classes that extend this class. Needed for asynchronous jobs such as BPM.
     */
    public AbstractFactory() {
        // Do nothing
    }

    /**
     *
     * @param aName
     *            nom de config: dao, bo, bp, vo
     */
    public AbstractFactory(String aName) {
        iName = aName;
    }

    /**
     * Créé ou remplace les objets du factory en initialisant les propriétés des objets à partir des fichiers de configuration
     *
     * @param aConfigDir
     *            chemin de tous les fichiers de config
     * @return true si le factory a été initialisé correctement pour tous les objets métier
     */
    public boolean init() {
        String configDir = iCtx.getProperty("configDir");
        if (configDir == null) {
            logger.info("Répértoire de config manque. Pas d'initialisation de DAOs");
            return true;
        }
        File file = new File(configDir + File.separator + iName);
        if (file.list() == null) {
            logger.info("No " + iName + "'s");
            return true;
        }
        boolean configValid = true;
        for (String str : file.list()) {
            if (!str.endsWith(".xsd")) {
                continue;
            }
            try {
                initFactoryObjects(new SchemaInfo(new URL(getUrlPath(configDir) + iName + File.separator + str)));
            } catch (Exception e) {
                logger.error("Problem by initialisation of " + iName + " : " + str, e);
                iCtx.getMessageStartApp().add("Initialisation " + iName + " '" + str + "' : FAILED");
                configValid = false;
            }
        }
        return configValid;
    }

    /**
     * Création ou remplacement de tous les objets contenu dans ce SchemaInfo
     *
     * @param aSchema
     *            le schéma à ajouter
     */
    protected void initFactoryObjects(SchemaInfo aSchema) {
        for (Object objName : aSchema.getVONameSet()) {
            initFactoryObject(aSchema.getVOInfo((String) objName));
            logger.debug(iName.toUpperCase() + " '" + objName + "' added");
        }
    }

    /**
     *
     * @param aVOInfo
     */
    protected void initFactoryObject(VOInfo aVOInfo) {
        if (aVOInfo.getValue("classname") != null) {
            Class<?> cl;
            try {
                cl = Class.forName((String) aVOInfo.getValue("classname"));
                Constructor<?> constr = cl.getConstructor(VOInfo.class);
                iFactoryObjects.put(aVOInfo.getName(), constr.newInstance(aVOInfo));
            } catch (Exception e) {
                logger.warn("Error instantiating Class : " + aVOInfo.getValue("classname"), e);
            }
        } else {
            iFactoryObjects.put(aVOInfo.getName(), getGenericFactoryObject(aVOInfo.getName(), aVOInfo));
        }
    }

    /**
     *
     * @param aObjectName
     *            nom de l'objet métier
     * @param aVOInfo
     *            vo info de l'objet métier
     * @return factory object générique, ex. GenericDataAccessObject, GenericBusinessObject
     */
    @SuppressWarnings("unused")
    protected Object getGenericFactoryObject(String aObjectName, VOInfo aVOInfo) {
        throw new UnsupportedOperationException("Implement in the factory class!");
    }

    private String getUrlPath(String aDir) {
        if (aDir.charAt(0) == '/' || aDir.charAt(0) == '.') {
            // URL pour UNIX
            return "file://" + aDir + File.separator;
        }
        // URL pour windows
        return "file:///" + aDir + File.separator;
    }

    /**
     *
     * @param aCtx
     *            context manager
     */
    public void setContextManager(IContextManager aCtx) {
        iCtx = aCtx;
    }
}
