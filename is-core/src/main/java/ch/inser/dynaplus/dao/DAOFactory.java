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

package ch.inser.dynaplus.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.inser.dynamic.util.VOInfo;
import ch.inser.dynaplus.util.AbstractFactory;

/**
 * Singleton gérant la totalité des DAO, il les charge en mémoire et les fournit sur demande.
 *
 * @author INSER SA
 * @version 1.0
 * @created 29-févr.-2008 08:53:10
 */
public final class DAOFactory extends AbstractFactory implements Serializable {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 7310838064741778282L;

    /**
     * Logger
     */
    private static final Log logger = LogFactory.getLog(DAOFactory.class);

    /**
     * Instance du singleton
     */
    private static DAOFactory cInstance = new DAOFactory();

    /**
     * Constructeur pour implémentation du singleton
     */
    private DAOFactory() {
        super("dao");
    }

    /**
     * @return le singleton
     */
    public static DAOFactory getInstance() {
        return cInstance;
    }

    @Override
    protected void initFactoryObject(VOInfo aVOInfo) {
        IDataAccessObject oldObj = (IDataAccessObject) iFactoryObjects.get(aVOInfo.getName());
        super.initFactoryObject(aVOInfo);

        // Injections
        if (oldObj != null) {
            IDataAccessObject newObj = (IDataAccessObject) iFactoryObjects.get(aVOInfo.getName());
            newObj.setVOFactory(oldObj.getVOFactory());
        }
    }

    @Override
    protected Object getGenericFactoryObject(String aObjectName, VOInfo aVOInfo) {
        return new GenericDataAccessObject(aObjectName, aVOInfo);
    }

    /**
     * Ajoute un DAO sans VOInfo dans le DAOFActory. Utilisé pour l'objet anonyme.
     *
     * @param aName
     *            nom de l'objet
     * @param iDAO
     *            un DAO sans VOInfo
     */
    public void add(String aName, IDataAccessObject iDAO) {
        iFactoryObjects.put(aName, iDAO);
        logger.debug("DAO '" + aName + "' added");
    }

    /**
     * @param objName
     *            ID of the object
     * @return the DAO identified by the objectName.
     *
     */
    public IDataAccessObject getDAO(String objName) {
        return (IDataAccessObject) iFactoryObjects.get(objName);
    }

    /**
     *
     * @param objName
     *            object type
     * @return the DAO identified by the object type
     */
    public IDataAccessObject getDAO(Enum<?> objName) {
        return (IDataAccessObject) iFactoryObjects.get(objName.toString());
    }

    /**
     * @return all names for IDataAccessObjects
     */
    public Set<String> getAllDAONames() {
        return iFactoryObjects.keySet();
    }

    /**
     *
     * @return list of DAOs
     */
    public List<IDataAccessObject> getDAOList() {
        List<IDataAccessObject> daolist = new ArrayList<>();
        for (String daoname : iFactoryObjects.keySet()) {
            daolist.add((IDataAccessObject) iFactoryObjects.get(daoname));
        }
        return daolist;
    }

}