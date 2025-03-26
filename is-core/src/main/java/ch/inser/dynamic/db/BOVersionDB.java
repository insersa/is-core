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

import ch.inser.dynamic.common.IValueObject;

/**
 * Objet métier pour la version de la base de données.
 *
 * @author INSER SA
 * @version 1.0
 */
public class BOVersionDB {

    // --------------------------------------------------- Variables d'instances

    /**
     * Instance singleton
     */
    private static BOVersionDB cInstance = new BOVersionDB();

    // ------------------------------------------- Constructeur et getInstance()

    /**
     * Constructeur privé pour permettre le mécanisme de singleton
     */
    private BOVersionDB() {
        // singleton
    }

    /**
     * Méthode fournissant une instance singleton de cette classe. La méthode de synchronisation est simple et n'utilise pas le système
     * "double-check" qui n'est pas éprouvé.
     * <p>
     * Pour créer une instance de cette classe, faire:
     * <p>
     *
     * <pre>
     * BOLieu bo = BOLieu.getInstance();
     * </pre>
     *
     * @return Une instance de cette classe.
     */
    protected static BOVersionDB getInstance() {

        return cInstance;
    }

    // ----------------------------------------- Méthodes d'initialisation de VO

    /**
     * Méthode retournant un VO avec valeures initiales
     *
     * @return Un VO de type version de la base de données.
     */
    protected VOVersionDB getInitVO() {

        return new VOVersionDB();
    }

    // -------------------------------------------------------- Méthodes membres

    /**
     * Lit la dernière verion de la base de données.
     *
     * @param aName
     *            Nom de la version de la base de données.
     *
     * @return Un value object contenant l'enregistrement demandé.
     *
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données. Un erreur au niveau de la base de données.
     */
    protected IValueObject getVersion(String aName, Connection aConnection) throws SQLException {
        return DAOVersionDB.getInstance().getVersion(aName, aConnection);
    }
}
