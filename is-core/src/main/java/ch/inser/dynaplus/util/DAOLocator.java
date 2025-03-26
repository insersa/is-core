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

import java.util.HashMap;
import java.util.Map;

import ch.inser.dynaplus.bo.GenericDAODelegate;
import ch.inser.dynaplus.bo.IDAODelegate;
import ch.inser.dynaplus.dao.DAOFactory;
import ch.inser.dynaplus.dao.IDataAccessObject;

/**
 * @author INSER SA
 * @version 1.0
 * @created 29-févr.-2008 08:53:10
 */
public class DAOLocator implements ILocator {

    /**
     * Les DAODelegates demandés
     */
    Map<String, GenericDAODelegate> iDelegates = new HashMap<>();

    /**
     *
     * @param name
     *            nom de l'objet dont on veut un accès au dao
     */
    @Override
    public IDAODelegate getService(String name) {
        if (iDelegates.get(name) == null) {
            IDataAccessObject dao = DAOFactory.getInstance().getDAO(name);
            if (dao == null) {
                iDelegates.put(name, null);
            } else {
                iDelegates.put(name, new GenericDAODelegate(dao));
            }
        }
        return iDelegates.get(name);
    }

}