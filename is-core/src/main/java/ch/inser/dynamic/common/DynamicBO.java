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

import java.sql.SQLException;

/**
 * C'est dans les méthode définies dans cette interface que l'on implémentera les divers ponts entre objets.
 *
 * @version 1.0
 * @author INSER SA
 */
public interface DynamicBO extends BusinessObject {

    /**
     * Enregistre un nouveau record.
     *
     * @param aValueObject
     *            Value object contenant le record à créer.
     * @param aUser
     *            L'utilisateur qui demande l'enregistrement.
     *
     * @return Le ID du record nouvellement créé ou null en cas de problème
     *
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données. Un erreur au niveau de la base de données.
     */
    @Override
    public Object create(IValueObject aValueObject, ILoggedUser aUser) throws SQLException;

    /**
     *
     * @param aMode
     *            mode
     * @param aUser
     *            utilisateur
     * @return vo initiale
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public IValueObject getInitVO(String aMode, ILoggedUser aUser) throws SQLException;
}
