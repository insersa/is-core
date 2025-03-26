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
import java.sql.Timestamp;

/**
 * Business Delegate de base pour des ValueObject dynamiques.
 *
 * @version 1.0
 * @author INSER SA
 */
public abstract class AbstractDynamicBD implements BusinessDelegate, DynamicBD {

    /**
     * Message quand méthode n'est pas implémenté
     */
    private static final String NOT_SUPPORTED = "Method is not supported";

    /**
     * Crée un nouvel enregistrement.
     *
     * @param aValueObject
     *            Value object contenant les données à enregistrer.
     * @param aUser
     *            L'utilisateur qui demande l'enregistrement.
     *
     * @return Le ID du record nouvellement créé ou null en cas de problème
     *
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données. Un erreur au niveau de la base de données.
     */
    @Override
    public Object create(IValueObject aValueObject, ILoggedUser aUser) throws SQLException {

        throw new java.lang.UnsupportedOperationException(NOT_SUPPORTED);
    }

    /**
     * Lit un enregistrement correspondant à l'ID spécifié.
     *
     * @param aId
     *            L'identifiant de l'enregistrement à lire.
     * @param aUser
     *            L'utilisateur qui demande l'enregistrement.
     *
     * @return Un value object contenant l'enregistrement demandé.
     *
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données. Un erreur au niveau de la base de données.
     */
    @Override
    public IValueObject getRecord(Object aId, ILoggedUser aUser) throws SQLException {

        throw new UnsupportedOperationException(NOT_SUPPORTED);
    }

    /**
     * Supprime un enregistrement.<br>
     *
     * @param aId
     *            L'identifiant de l'objet à supprimer.
     * @param aTimestamp
     *            Timestamp de l'objet d'origine
     * @param aUser
     *            L'utilisateur qui demande la suppression.
     *
     * @return Nombre de lignes concernées par l'opération.
     *
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données. Un erreur au niveau de la base de données.
     */
    @Override
    public int delete(Object aId, Timestamp aTimestamp, ILoggedUser aUser) throws SQLException {

        throw new UnsupportedOperationException(NOT_SUPPORTED);
    }

    /**
     * Retourne le timestamp d'un record.
     *
     * @param aId
     *            Identifiant du record.
     * @param aUser
     *            L'utilisateur qui demande le timestamp.
     *
     * @return Le timestamp de l'objet.
     *
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données. Un erreur au niveau de la base de données.
     */
    @Override
    public Timestamp getTimestamp(Object aId, ILoggedUser aUser) throws SQLException {

        throw new UnsupportedOperationException(NOT_SUPPORTED);
    }

    /**
     *
     * @param mode
     * @param user
     * @return
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    @Override
    public IValueObject getInitVO(String aMode, ILoggedUser aUser) throws SQLException {

        throw new UnsupportedOperationException(NOT_SUPPORTED);
    }
}