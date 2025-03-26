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

import java.sql.Connection;
import java.sql.SQLException;

import ch.inser.dynamic.common.AbstractDynamicDAO;
import ch.inser.dynamic.common.IDAOResult;
import ch.inser.dynamic.common.ILoggedUser;
import ch.inser.dynamic.common.IValueObject;
import ch.inser.dynaplus.vo.IVOFactory;
import ch.inser.jsl.exceptions.ISException;

/**
 * @author INSER SA
 * @version 1.0
 * @created 29-févr.-2008 08:53:09
 */
public abstract class AbstractDataAcessObject extends AbstractDynamicDAO implements IDataAccessObject {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -7371871631314266578L;

    /**
     * Optimisation des listes par limitation des joins dans les recherches selon:
     *
     * [IS dynaplus 0000145] [DAO] Ne pas effectuer des JOIN inutiles lors de la recherche
     */
    private static boolean iListOptimized = false;

    /**
     * VOFActory
     */
    private IVOFactory iVOFactory;

    // Si nécessaire implémentation du comportement générique

    /**
     * setter pour le paramètre d'optimisation des jointure sur les reuqête de recherche
     *
     * @param aListoptimized
     *            paramètre d'optimisation
     */
    public static void setListOptimized(boolean aListoptimized) {
        AbstractDataAcessObject.iListOptimized = aListoptimized;
    }

    /**
     * getter pour le paramètre d'optimisation des jointure sur les reuqête de recherche
     *
     * @return true si les recherches doivent être optimisées
     */
    public static boolean isListOptimized() {
        return iListOptimized;
    }

    @Override
    public Object executeMethode(String aNameMethode, Object aObject, ILoggedUser aUser, Connection aConnection) throws ISException {
        throw new ISException(new UnsupportedOperationException());
    }

    /**
     * Implementé uniquement par ch.inser.dynaplus.anonymous.DAOAnonymous
     */

    @Override
    public IDAOResult getRecord(String aTable, String aField, Object aValue, Connection aCon) throws SQLException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    /**
     * Implementé uniquement par ch.inser.dynaplus.anonymous.DAOAnonymous
     */

    @Override
    public IDAOResult getList(String aTable, IValueObject aQuery, Connection aCon) throws SQLException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IDAOResult getList(String aTable, IValueObject aQuery, Integer aRownum, Connection aCon) throws SQLException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public IVOFactory getVOFactory() {
        return iVOFactory;
    }

    @Override
    public void setVOFactory(IVOFactory aVOFactory) {
        iVOFactory = aVOFactory;
    }

}