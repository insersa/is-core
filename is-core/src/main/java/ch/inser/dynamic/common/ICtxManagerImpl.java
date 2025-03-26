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

import java.net.MalformedURLException;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import ch.inser.dynamic.quality.IQualityController;
import ch.inser.dynamic.quality.IQualityTest;

/**
 * Interface de l'implémentation d'un ContextManager
 *
 * @version 1.0
 * @author INSER SA
 */
public interface ICtxManagerImpl {

    // ---------------------------------------------------------- Méthodes
    // membre

    // Obtient un path complet à partir d'un relatif donné en paramètre
    public String getPath(String relative) throws MalformedURLException;

    /**
     * Méthode fournissant un datasource permettant d'obtenir des connections.
     */
    public DataSource getDataSource();

    /**
     * Méthode fournissant un datasource permettant d'obtenir des connections avec timout long .
     */
    public DataSource getLongDataSource();

    /**
     * Retourne le nombre maximum d'enregistrement autorisé pour un ResultSet. Il s'agit d'une limite "hard" définie au niveau de
     * l'application et qui doit servir à configurer chaque objet Statement de la façon suivante:<br>
     * <code>
     *       Statement st = connection.createStatement(...);
     *       st.setMaxRows(ContextManager.getInstance().getResultSetMaxRows());
     *  </code>
     *
     * @return Nombre maximum d'enregistrements autorisé pour un ResultSet.
     */
    public int getResultSetMaxRows();

    /**
     * Retourne le nombre maximum d'éléments par page pour une liste. Cette valeur définie au niveau de l'application est utilisée pour
     * limiter le nombre d'éléments/records figurant dans une liste.
     * <p>
     * Un mécanisme permet de naviguer dans la page précédante ou suivante de la liste complète.
     *
     * @return Le nombre maximum d'éléments par page pour une liste.
     */
    public int getListPageMaxItems();

    /**
     * Indique le comportement spécifique de l'application.
     *
     * @return "dev" (développement), "test" ou "prod"
     */
    public String getApplicationBehavior();

    /**
     * Indique la version de l'application
     */
    public String getApplicationVersion();

    /**
     * Getter pour les propriétés de l'application
     */
    public Properties getProperties();

    /**
     * Getter pour une propriété de l'application
     */
    public String getProperty(String key);

    public boolean isApplicationInitOK();

    /**
     * Test la disponibilité de l'application en comparant l'heure actuelle avec les intervalles définis dans iDisponibility.
     *
     * @return
     */
    public boolean isDisponibilityOK();

    public String getDatabaseVersion();

    public void addProperties(Properties aArg0);

    public String getApplicationAbout();

    /**
     * Get the map of application about information.
     *
     * The typically keys are application.name, application.version, application.build, database.version and database.dependence
     *
     * @return the map of application about information
     */
    public Map<String, String> getApplicationAboutMap();

    public String getApplicationBuild();

    public String getApplicationName();

    public Object getChartEngine();

    public Object getDesignEngine();

    public Object getReportEngine();

    public void setApplicationBuild(String aArg0);

    public void setApplicationInitOK(boolean aArg0);

    public void setApplicationName(String aArg0);

    public void setApplicationVersion(String aArg0);

    public void setChartEngine(Object aArg0);

    public void setDatabaseVersion(String aArg0);

    public void setDesignEngine(Object aArg0);

    public void setReportEngine(Object aArg0);

    public String getDatabaseDependenceVersion();

    public void setDatabaseDependenceVersion(String aDatabaseDependenceVersion);

    public IQualityTest getQualityTest();

    public void setQualityTest(IQualityTest aQuality);

    public IQualityController getQualityController();

    public void setQualityController(IQualityController aQuality);

    /**
     * Initialise un scheduler pour les processus asynchrones
     *
     * @throws SchedulerException
     */
    public void initScheduler() throws SchedulerException;

    public void initScheduler(String aConfig) throws SchedulerException;

    public void initScheduler(Properties aConfig) throws SchedulerException;

    /** Getter pour le scheduler des processus asynchrones */
    public Scheduler getScheduler();

}
