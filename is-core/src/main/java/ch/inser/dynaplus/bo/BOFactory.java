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

package ch.inser.dynaplus.bo;

import java.io.Serializable;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.inser.dynamic.util.VOInfo;
import ch.inser.dynaplus.util.AbstractFactory;

/**
 * @author INSER SA
 * @version 1.0
 * @created 29-févr.-2008 08:53:10
 */
public class BOFactory extends AbstractFactory implements Serializable {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -195364733571977352L;

    /**
     * Objet pour les logs.
     */
    private static final Log logger = LogFactory.getLog(BOFactory.class);

    /**
     * Instance du singleton
     */
    private static BOFactory cInstance = new BOFactory();

    /**
     * Consctructeur par défaut, implémentation du singleton
     */
    private BOFactory() {
        super("bo");
    }

    /**
     * @return l'instance singleton
     */
    public static BOFactory getInstance() {
        return cInstance;
    }

    @Override
    protected void initFactoryObject(VOInfo aVOInfo) {
        IBusinessObject oldObj = (IBusinessObject) iFactoryObjects.get(aVOInfo.getName());
        super.initFactoryObject(aVOInfo);

        // Injections
        ((IBusinessObject) iFactoryObjects.get(aVOInfo.getName())).setContextManager(iCtx);
        if (oldObj != null) {
            IBusinessObject newObj = (IBusinessObject) iFactoryObjects.get(aVOInfo.getName());
            newObj.setDao(
                    (IDAODelegate) ch.inser.dynaplus.util.ServiceLocator.getInstance().getLocator("dao").getService(aVOInfo.getName()));
            newObj.setBOFactory(oldObj.getBOFactory());
            newObj.setVOFactory(oldObj.getVOFactory());
            newObj.setContextManager(oldObj.getContextManager());
        }
    }

    @Override
    protected Object getGenericFactoryObject(String aObjectName, VOInfo aVOInfo) {
        return new GenericBusinessObject(aObjectName, aVOInfo);
    }

    /**
     * Ajoute un BO sans VOInfo dans le BOFActory. Utilisé pour l'objet anonyme.
     *
     * @param aName
     *            nom de l'objet
     * @param iBO
     *            un BO sans VOInfo
     */
    public void add(String aName, IBusinessObject iBO) {
        iFactoryObjects.put(aName, iBO);
        logger.debug("BO '" + aName + "' added");
    }

    /**
     * Obtention dun bo.
     *
     * @param objName
     *            nom du bo demandé
     * @return le bo demandé
     */
    public IBusinessObject getBO(String objName) {
        return (IBusinessObject) iFactoryObjects.get(objName);
    }

    /**
     *
     * @param objName
     *            nom de l'objet métier
     * @return bo
     */
    public IBusinessObject getBO(Enum<?> objName) {
        return (IBusinessObject) iFactoryObjects.get(objName.toString());
    }

    /**
     * @return la liste des noms des objets métiers livrés par cette factory
     */
    public Set<String> getBOList() {
        return iFactoryObjects.keySet();
    }

}