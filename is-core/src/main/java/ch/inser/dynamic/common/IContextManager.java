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

import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.cache.CacheManager;
import javax.sql.DataSource;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import ch.inser.dynamic.quality.IQualityController;
import ch.inser.dynamic.quality.IQualityTest;
import ch.inser.jsl.logger.INdc;

/**
 * @author INSER SA
 * @version 1.0
 * @created 29-févr.-2008 08:53:12
 */
public interface IContextManager {

    /**
     * Méthode fournissant un datasource permettant d'obtenir des connections.
     */
    public DataSource getDataSource();

    /**
     * Méthode fournissant un datasource à l'aide du nom de connexion
     */
    public DataSource getDataSource(String aName);

    /**
     * Indique la version de l'application
     */
    public String getApplicationVersion();

    public void setApplicationVersion(String str);

    public String getApplicationBuild();

    public void setApplicationBuild(String str);

    public String getApplicationName();

    public void setApplicationName(String str);

    /**
     * Getter pour les propriétés de l'application
     */
    public Properties getProperties();

    /**
     * Getter pour les propriétés de l'application
     */
    public void addProperties(Properties prop);

    /**
     * Getter pour une propriété de l'application
     */
    public String getProperty(String key);

    public boolean isApplicationInitOK();

    public void setApplicationInitOK(boolean boo);

    public String getDatabaseVersion();

    public void setDatabaseVersion(String version);

    public String getDatabaseDependenceVersion();

    public void setDatabaseDependenceVersion(String aDatabaseDependenceVersion);

    public Object getReportEngine();

    public void setReportEngine(Object reportEngine);

    public Object getChartEngine();

    public void setChartEngine(Object chartEngine);

    public Object getDesignEngine();

    public void setDesignEngine(Object designEngine);

    /** Give the application version information in a readable format (html) */
    public String getApplicationAbout();

    /**
     * Get the map of application about information.
     *
     * The typically keys are application.name, application.version, application.build, database.version and database.dependence
     *
     * @return the map of application about information
     */
    public Map<String, String> getApplicationAboutMap();

    public IQualityTest getQualityTest();

    public IQualityController getQualityController();

    public void setQualityTest(IQualityTest aQualityTest);

    public void setQualityController(IQualityController aQualityController);

    /** Informations sur le démarrage de l'application */
    public List<String> getMessageStartApp();

    /** Informations sur le démarrage de l'application */
    public void setMessageStartApp(List<String> aMessageStartApp);

    /**
     * Initialise un scheduler pour les processus asynchrones
     *
     * @throws SchedulerException
     */
    public void initScheduler() throws SchedulerException;

    /**
     * Initialise un scheduler pour les processus asynchrones
     *
     * @param aConfig
     *            fichier de config Quartz
     * @throws SchedulerException
     *             problème d'initialisation
     */
    public void initScheduler(String aConfig) throws SchedulerException;

    /**
     * Initialise un scheduler Quartz
     *
     * @param aProps
     *            Propriétés Quartz
     * @throws SchedulerException
     *             problème d'initilisation
     */
    public void initScheduler(Properties aProps) throws SchedulerException;

    /** Getter pour le scheduler des processus asynchrones */
    public Scheduler getScheduler();

    /** Getter pour le help bean */
    public Object getHelpBean();

    /** Set pour le help bean */
    public void setHelpBean(Object aHelpBean);

    /**
     * Retourne le cache manager de l'application
     *
     * @return
     */
    public CacheManager getCacheManager();

    /**
     * Injection du CacheManager
     *
     * @param aCacheManager
     */
    public void setCacheManager(CacheManager aCacheManager);

    /**
     * Accès uniquement au service ObjectsResource pour les objets métiers se trouvant dans la liste
     *
     * @return liste des objets métiers autorisés
     */
    public List<String> getObjectsResAutorized();

    /**
     * Configurer la liste des objets métiers autorisés par le service ObjectsResource
     *
     * @param aObjectsResAutorized
     */
    public void setObjectsResAutorized(List<String> aObjectsResAutorized);

    /**
     * @return NDC contexte pour le logging
     */
    public INdc getNdc();

    /**
     * @param aNdc
     *            injection du NDC contexte pour le logging
     */
    public void setNdc(INdc aNdc);

}