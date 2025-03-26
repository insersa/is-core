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
public class BPFactory extends AbstractFactory implements Serializable {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 7519069853699838030L;

    /**
     * Objet pour les logs.
     */
    private static final Log logger = LogFactory.getLog(BPFactory.class);

    /**
     * Instance du singleton
     */
    private static BPFactory cInstance = new BPFactory();

    /**
     * Constructeur par défaut, implémentation du singleton
     */
    private BPFactory() {
        super("bp");
    }

    /**
     * @return l'instance singleton
     */
    public static BPFactory getInstance() {
        return cInstance;
    }

    @Override
    protected void initFactoryObject(VOInfo aVOInfo) {
        IBusinessProcess oldObj = (IBusinessProcess) iFactoryObjects.get(aVOInfo.getName());
        super.initFactoryObject(aVOInfo);

        // Injections
        ((IBusinessProcess) iFactoryObjects.get(aVOInfo.getName())).setContextManager(iCtx);
        if (oldObj != null) {
            IBusinessProcess newObj = (IBusinessProcess) iFactoryObjects.get(aVOInfo.getName());
            newObj.setBOFactory(oldObj.getBOFactory());
            newObj.setVOFactory(oldObj.getVOFactory());
        }
    }

    @Override
    protected Object getGenericFactoryObject(String aObjectName, VOInfo aVOInfo) {
        return new GenericBusinessProcess(aObjectName, aVOInfo);
    }

    /**
     * Ajoute un BP sans VOInfo dans le BPFActory. Utilisé pour l'objet anonyme.
     *
     * @param aName
     *            nom de l'objet
     * @param iBP
     *            un BO sans VOInfo
     */
    public void add(String aName, IBusinessProcess iBP) {
        iFactoryObjects.put(aName, iBP);
        logger.debug("BP '" + aName + "' added");
    }

    /**
     * Obtention d'un bp
     *
     * @param objName
     *            nom du bp que l'on veut obtenir
     * @return le bp demandé
     */
    public IBusinessProcess getBP(String objName) {
        return (IBusinessProcess) iFactoryObjects.get(objName);
    }

    /**
     * @return la liste des noms des process métiers livrés par cette factory
     */
    public Set<String> getBPList() {
        return iFactoryObjects.keySet();
    }

}