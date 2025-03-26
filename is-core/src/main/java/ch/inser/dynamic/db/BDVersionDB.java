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

package ch.inser.dynamic.db;

import java.sql.Connection;
import java.sql.SQLException;

import ch.inser.dynamic.common.AbstractDynamicBD;
import ch.inser.dynamic.common.IValueObject;

/**
 * Business delegate pour la version de la base de données.
 *
 * @version 1.0
 * @author INSER SA
 */
public class BDVersionDB extends AbstractDynamicBD {

    // ---------------------------------------------------- Variables d'instance

    /**
     * Objet métier pour le lieu de prélèvement.
     */
    private BOVersionDB iBOVersionDB;

    // ----------------------------------------------------------- Constructeurs

    /**
     * Constructeur du Business Delegate donnant directement le Business Object représenté. Evite un accès à la base de données.
     *
     * @param aBo
     *            Un BO à réutiliser.
     */
    public BDVersionDB(BOVersionDB aBo) {

        iBOVersionDB = aBo;
    }

    /**
     * Constructeur à utiliser pour la création d'une nouvelle version de la base de données.
     */
    public BDVersionDB() {

        iBOVersionDB = BOVersionDB.getInstance();
    }

    // -------------------------------------------------------- Méthodes membres

    /**
     * Méthode retournant un VO avec valeures initiales
     *
     * @return Un VO de type version de la base de données.
     */
    public VOVersionDB getInitVO() {

        return iBOVersionDB.getInitVO();
    }

    /**
     * Lit la dernière verion de la base de données.
     *
     * @param aName
     *            nom de la version de la base de données.
     * @param aConnection
     *            connexiion
     *
     * @return Un value object contenant l'enregistrement demandé.
     *
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données. Un erreur au niveau de la base de données.
     */
    public IValueObject getVersion(String aName, Connection aConnection) throws SQLException {

        return iBOVersionDB.getVersion(aName, aConnection);
    }

}
