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

import ch.inser.dynaplus.dao.IDataAccessObject;

/**
 * @author INSER SA
 * @version 1.0
 * @created 29-févr.-2008 08:53:11
 */
public class GenericDAODelegate extends AbstractDAODelegate {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 7837653234084259977L;

    /**
     * Constructeur avec assignation du délégué
     *
     * @param aDao
     *            dao de délégation
     */
    public GenericDAODelegate(IDataAccessObject aDao) {
        iDao = aDao;
    }
}