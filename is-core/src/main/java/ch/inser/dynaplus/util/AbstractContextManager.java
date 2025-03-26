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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.cache.CacheManager;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import ch.inser.dynamic.common.IContextManager;
import ch.inser.dynamic.quality.IQualityController;
import ch.inser.dynamic.quality.IQualityTest;
import ch.inser.dynaplus.help.IApplicationHelpBean;
import ch.inser.dynaplus.sql.ISDataSource;
import ch.inser.jsl.logger.INdc;

/**
 * @author INSER SA
 * @version 1.0
 * @created 29-févr.-2008 08:53:09
 */
public abstract class AbstractContextManager implements IContextManager, Serializable {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -5095754324393175025L;

    /** Logger. */
    private static final Log logger = LogFactory.getLog(AbstractContextManager.class);

    /** About text */
    private String cAbout = null;

    /**
     * The map of application about information.
     */
    private Map<String, String> iAboutMap = null;

    /** Version de l'application */
    private String cVersion;

    /** Build de l'application */
    private String cBuild;

    /** Nom de l'application */
    private String cName;

    /** Variables contenant les propriétés de l'applications */
    private Properties iProperties = new Properties();

    /**
     * Indique si l'initialisation de l'application s'est déroulée correctement
     */
    private boolean iApplicationInitOK = false;

    /**
     * Version de la base de données.
     */
    private String iDatabaseVersion = null;

    /**
     * Version de compatibilité entre la base de données et l'application
     */
    private String iDatabaseDependenceVersion = null;

    /**
     * Report engine
     */
    private Object iReportEngine = null;

    /**
     * Chart engine
     */
    private Object iChartEngine = null;

    /** Scheduler pour les processus asynchrones */
    private Scheduler iScheduler = null;

    /** Texts multilingues de composants JSF et messages d'erreur */
    private transient IApplicationHelpBean iHelpBean;

    /**
     * Design engine
     */
    private Object iDesignEngine = null;

    /**
     * Quality test
     */
    private IQualityTest iQualityTest = null;
    private IQualityController iQualityController = null;

    /**
     * Nested diagnostic context
     */
    private INdc iNdc;

    /**
     * Liste de messages du démarrage de l'application, permet d'informer l'utilisateur si l'application a bien démarré
     */
    private List<String> iMessageStartApp = new ArrayList<>();

    /** Values of the datasource */
    private transient Map<String, DataSource> iMapDataSource = new HashMap<>();

    /** get the datasource dependant JNDI name */
    @Override
    public DataSource getDataSource() {
        return getDataSource(null);
    }

    /**
     * Utilisation d'une autre datasource, le renseignement doit se faire au niveau des fichiers de configuration
     *
     * @param aName
     *            nom de la datasource
     */
    @Override
    public DataSource getDataSource(String aName) {

        String name;

        if (aName == null) {
            name = getProperty("datasourceName");
        } else {
            name = aName;
        }

        if (name == null) {
            return null;
        }

        // Search the datasource with the name JNDI
        if (iMapDataSource.get(name) == null) {
            Context myContext;
            try {
                myContext = new InitialContext();
                iMapDataSource.put(name, new ISDataSource((DataSource) myContext.lookup(name)));
            } catch (NamingException e) {
                iMapDataSource.remove(name);
                logger.error("Error looking up the DataSource : " + name, e);
            }
        }
        return iMapDataSource.get(name);
    }

    @Override
    public Object getReportEngine() {
        return iReportEngine;
    }

    @Override
    public void setReportEngine(Object reportEngine) {
        iReportEngine = reportEngine;
    }

    @Override
    public Object getChartEngine() {
        return iChartEngine;
    }

    @Override
    public void setChartEngine(Object chartEngine) {
        iChartEngine = chartEngine;
    }

    @Override
    public Object getDesignEngine() {
        return iDesignEngine;
    }

    @Override
    public void setDesignEngine(Object designEngine) {
        iDesignEngine = designEngine;
    }

    // Chargement du fichier propriété
    @Override
    public void addProperties(Properties prop) {
        iProperties.putAll(prop);
    }

    /**
     * Définit la version de l'application
     */
    @Override
    public void setApplicationVersion(String version) {
        cVersion = version;
    }

    /**
     * Indique la version de l'application
     */
    @Override
    public String getApplicationVersion() {
        return cVersion;
    }

    /**
     * Définit le build de l'application
     */
    @Override
    public void setApplicationBuild(String version) {
        cBuild = version;
    }

    /**
     * Indique le build de l'application
     */
    @Override
    public String getApplicationBuild() {
        return cBuild;
    }

    /**
     * Définit le nom de l'application
     */
    @Override
    public void setApplicationName(String version) {
        cName = version;
    }

    /**
     * Indique le nom de l'application
     */
    @Override
    public String getApplicationName() {
        return cName;
    }

    /**
     * Getter pour les propriétés de l'application
     */
    @Override
    public Properties getProperties() {
        return iProperties;
    }

    /**
     * Getter pour une propriété de l'application
     */
    @Override
    public String getProperty(String key) {

        return iProperties.getProperty(key);
    }

    @Override
    public boolean isApplicationInitOK() {
        return iApplicationInitOK;
    }

    @Override
    public void setApplicationInitOK(boolean applicationInitOK) {
        iApplicationInitOK = applicationInitOK;
    }

    @Override
    public String getDatabaseVersion() {
        return iDatabaseVersion;
    }

    @Override
    public void setDatabaseVersion(String version) {
        iDatabaseVersion = version;
    }

    @Override
    public String getDatabaseDependenceVersion() {
        return iDatabaseDependenceVersion;
    }

    @Override
    public void setDatabaseDependenceVersion(String aDatabaseDependenceVersion) {
        iDatabaseDependenceVersion = aDatabaseDependenceVersion;
    }

    @Override
    public String getApplicationAbout() {

        if (cAbout == null) {
            StringBuffer sb = new StringBuffer();
            sb.append("<br />");
            sb.append("-APPLICATION");
            sb.append("<br />Version : ");
            sb.append(getApplicationVersion());
            sb.append("<br />Build : ");
            sb.append(getApplicationBuild());
            sb.append("<br /><br />-DATABASE");
            sb.append("<br />Version script : ");
            sb.append(getDatabaseVersion());
            sb.append("<br />Version compatibilité : ");
            sb.append(getDatabaseDependenceVersion());
            sb.append("<br /><br />");
            sb.append("Application implémentée par Inser S.A.");
            cAbout = sb.toString();
        }
        return cAbout;
    }

    @Override
    public Map<String, String> getApplicationAboutMap() {
        if (iAboutMap == null) {
            Map<String, String> map = new HashMap<>(5);
            map.put("application.name", cName);
            map.put("application.version", cVersion);
            map.put("application.build", cBuild);
            map.put("database.version", iDatabaseVersion);
            map.put("database.dependence", iDatabaseDependenceVersion);

            iAboutMap = map;
        }
        return iAboutMap;
    }

    @Override
    public IQualityTest getQualityTest() {
        return iQualityTest;
    }

    @Override
    public void setQualityTest(IQualityTest aQuality) {
        iQualityTest = aQuality;
    }

    @Override
    public IQualityController getQualityController() {
        return iQualityController;
    }

    @Override
    public void setQualityController(IQualityController aQuality) {
        iQualityController = aQuality;
    }

    @Override
    public List<String> getMessageStartApp() {
        return iMessageStartApp;
    }

    @Override
    public void setMessageStartApp(List<String> aMessageStartApp) {
        iMessageStartApp = aMessageStartApp;
    }

    @Override
    public void initScheduler() throws SchedulerException {
        iScheduler = StdSchedulerFactory.getDefaultScheduler();
        iScheduler.start();
    }

    /**
     * Initialisation d'un scheduler selon un fichier de config customisé
     *
     * @param aConfig
     *            fichier de config Quartz
     * @throws SchedulerException
     *             problème de initialisation
     */
    @Override
    public void initScheduler(String aConfig) throws SchedulerException {
        iScheduler = new StdSchedulerFactory(aConfig).getScheduler();
        iScheduler.start();
    }

    @Override
    public void initScheduler(Properties aProps) throws SchedulerException {
        iScheduler = new StdSchedulerFactory(aProps).getScheduler();
        iScheduler.start();
    }

    @Override
    public Scheduler getScheduler() {
        return iScheduler;
    }

    /**
     * Set the scheduler.
     *
     * @param aScheduler
     *            the scheduler
     */
    public void setScheduler(Scheduler aScheduler) {
        iScheduler = aScheduler;
    }

    /**
     * @return the helpBean
     */
    @Override
    public Object getHelpBean() {
        return iHelpBean;
    }

    /**
     * @param aHelpBean
     *            the helpBean to set
     */
    @Override
    public void setHelpBean(Object aHelpBean) {
        iHelpBean = (IApplicationHelpBean) aHelpBean;
    }

    @Override
    public CacheManager getCacheManager() {
        throw new UnsupportedOperationException("Method CacheManager not yet implemented.");

    }

    @Override
    public void setCacheManager(CacheManager aCacheManager) {
        throw new UnsupportedOperationException("Method CacheManager not yet implemented.");
    }

    @Override
    public List<String> getObjectsResAutorized() {
        throw new UnsupportedOperationException("Method getObjectsResAutorized not yet implemented.");
    }

    @Override
    public void setObjectsResAutorized(List<String> aObjectsResAutorized) {
        throw new UnsupportedOperationException("Method setObjectsResAutorized not yet implemented.");
    }

    /**
     * @return NDC contexte pour le logging
     */
    @Override
    public INdc getNdc() {
        return iNdc;
    }

    /**
     * @param aNdc
     *            injection du NDC contexte pour le logging
     */
    @Override
    public void setNdc(INdc aNdc) {
        iNdc = aNdc;
    }

}