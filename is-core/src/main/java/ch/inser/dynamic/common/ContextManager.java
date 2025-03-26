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

import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import ch.inser.dynamic.quality.IQualityController;
import ch.inser.dynamic.quality.IQualityTest;
import ch.inser.jsl.logger.INdc;

/**
 * Classe de méthod statique
 *
 * @version 1.0
 * @author INSER SA
 */
public abstract class ContextManager implements IContextManager, Serializable {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -2645319955354239975L;

    /**
     * Année d'enquête en cours. Un valeure égale à 0 indique qu'il n'y a pas d'enquête en cours.
     */
    private static ICtxManagerImpl iImpl;

    /**
     * Liste de messages du démarrage de l'application, permet d'informer l'utilisateur si l'application a bien démarré
     */
    private List<String> iMessageStartApp = new ArrayList<>();

    /**
     * Nested diagnostic context
     */
    private INdc iNdc;

    public void setImplementation(ICtxManagerImpl anImpl) {
        iImpl = anImpl;
    }

    public ICtxManagerImpl getImplementation() {
        return iImpl;
    }

    /** Obtient un path complet à partir d'un relatif donné en paramètre */
    public String getPath(String relative) throws MalformedURLException {
        return iImpl.getPath(relative);
    }

    /**
     * Méthode fournissant un datasource permettant d'obtenir des connections.
     */
    @Override
    public DataSource getDataSource() {
        return iImpl.getDataSource();
    }

    /**
     * Méthode fournissant un datasource permettant d'obtenir des connections avec timout long .
     */
    public DataSource getLongDataSource() {
        return iImpl.getLongDataSource();
    }

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
    public int getResultSetMaxRows() {
        return iImpl.getResultSetMaxRows();
    }

    /**
     * Retourne le nombre maximum d'éléments par page pour une liste. Cette valeur définie au niveau de l'application est utilisée pour
     * limiter le nombre d'éléments/records figurant dans une liste.
     * <p>
     * Un mécanisme permet de naviguer dans la page précédante ou suivante de la liste complète.
     *
     * @return Le nombre maximum d'éléments par page pour une liste.
     */
    public int getListPageMaxItems() {
        return iImpl.getListPageMaxItems();
    }

    /**
     * Indique le comportement spécifique de l'application.
     *
     * @return "dev" (développement), "test" ou "prod"
     */
    public String getApplicationBehavior() {
        return iImpl.getApplicationBehavior();
    }

    /**
     * Indique la version de l'application
     */
    @Override
    public String getApplicationVersion() {
        return iImpl.getApplicationVersion();
    }

    /**
     * Getter pour les propriétés de l'application
     */
    @Override
    public Properties getProperties() {
        return iImpl.getProperties();
    }

    /**
     * Getter pour une propriété de l'application
     */
    @Override
    public String getProperty(String key) {
        return iImpl.getProperty(key);
    }

    @Override
    public boolean isApplicationInitOK() {
        return iImpl.isApplicationInitOK();
    }

    /**
     * Test la disponibilité de l'application en comparant l'heure actuelle avec les intervalles définis dans iDisponibility.
     *
     * @return
     */
    public boolean isDisponibilityOK() {
        return iImpl.isDisponibilityOK();
    }

    @Override
    public String getDatabaseVersion() {
        return iImpl.getDatabaseVersion();
    }

    @Override
    public void addProperties(Properties aArg0) {
        iImpl.addProperties(aArg0);
    }

    @Override
    public String getApplicationAbout() {
        return iImpl.getApplicationAbout();
    }

    @Override
    public Map<String, String> getApplicationAboutMap() {
        return iImpl.getApplicationAboutMap();
    }

    @Override
    public String getApplicationBuild() {
        return iImpl.getApplicationBuild();
    }

    @Override
    public String getApplicationName() {
        return iImpl.getApplicationName();
    }

    @Override
    public Object getChartEngine() {
        return iImpl.getChartEngine();
    }

    @Override
    public Object getDesignEngine() {
        return iImpl.getDesignEngine();
    }

    @Override
    public void setApplicationBuild(String aArg0) {
        iImpl.setApplicationBuild(aArg0);
    }

    @Override
    public void setApplicationInitOK(boolean aArg0) {
        iImpl.setApplicationInitOK(aArg0);
    }

    @Override
    public void setApplicationName(String aArg0) {
        iImpl.setApplicationName(aArg0);
    }

    @Override
    public void setApplicationVersion(String aArg0) {
        iImpl.setApplicationVersion(aArg0);
    }

    @Override
    public void setChartEngine(Object aArg0) {
        iImpl.setChartEngine(aArg0);
    }

    @Override
    public void setDatabaseVersion(String aArg0) {
        iImpl.setDatabaseVersion(aArg0);
    }

    @Override
    public void setDesignEngine(Object aArg0) {
        iImpl.setDesignEngine(aArg0);
    }

    @Override
    public void setReportEngine(Object aArg0) {
        iImpl.setReportEngine(aArg0);
    }

    @Override
    public Object getReportEngine() {
        return iImpl.getReportEngine();
    }

    @Override
    public String getDatabaseDependenceVersion() {
        return iImpl.getDatabaseDependenceVersion();
    }

    @Override
    public void setDatabaseDependenceVersion(String aDatabaseDependenceVersion) {
        iImpl.setDatabaseDependenceVersion(aDatabaseDependenceVersion);
    }

    @Override
    public IQualityTest getQualityTest() {
        return iImpl.getQualityTest();
    }

    @Override
    public void setQualityTest(IQualityTest aQuality) {
        iImpl.setQualityTest(aQuality);
    }

    @Override
    public IQualityController getQualityController() {
        return iImpl.getQualityController();
    }

    @Override
    public void setQualityController(IQualityController aQuality) {
        iImpl.setQualityController(aQuality);
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
        iImpl.initScheduler();
    }

    @Override
    public void initScheduler(String aConfig) throws SchedulerException {
        iImpl.initScheduler(aConfig);
    }

    @Override
    public void initScheduler(Properties aConfig) throws SchedulerException {
        iImpl.initScheduler(aConfig);
    }

    /** Getter pour le scheduler des processus asynchrones */
    @Override
    public Scheduler getScheduler() {
        return iImpl.getScheduler();
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