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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.inser.dynamic.common.AbstractDynamicDAO;
import ch.inser.dynamic.common.DAOParameter;
import ch.inser.dynamic.common.DAOParameter.Name;
import ch.inser.dynamic.common.IValueObject;
import ch.inser.jsl.list.ListHandler;

/**
 * Classe d'accès à la base de données pour les objets "Version de la base de données".
 *
 * @author INSER SA
 * @version 1.0
 */
public class DAOVersionDB extends AbstractDynamicDAO {

    private static final long serialVersionUID = 7610536417190037321L;

    /**
     * Objet pour les logs.
     */
    private static final Log logger = LogFactory.getLog(DAOVersionDB.class);

    /**
     * Instance singleton
     */
    private static DAOVersionDB cInstance = new DAOVersionDB();

    // ------------------------------------------- Constructeur et getInstance()
    /**
     * Constructeur privé pour permettre le mécanisme de singleton
     */
    private DAOVersionDB() {

        cList = new HashSet<>(6);
        cList.add("ver_id");
        cList.add("ver_label");
        cList.add("ver_version");
        cList.add("ver_compatibilite");
        cList.add("ver_description");
        cList.add("ver_date");

        logger.info("Initialised successfully");
    }

    /**
     * Méthode fournissant une instance singleton de cette classe. La méthode de synchronisation est simple et n'utilise pas le système
     * "double-check" qui n'est pas éprouvé.
     * <p>
     * Pour créer une instance de cette classe, faire:
     * <p>
     *
     * <pre>
     * BOGebaeude bo = BOGebaeude.getInstance();
     * </pre>
     *
     * @return Une instance de cette classe.
     */
    public static DAOVersionDB getInstance() {
        return cInstance;
    }

    // ---------------------------------------------------- Variables d'instance

    /**
     * Le nom de la table.
     */
    public static final String cTableName = "is_version_db";

    /**
     * Le nom de l'id des enregistrement.
     */
    public static final String cIdName = "ver_id";

    /**
     * Le nom du timestamp.
     */
    public static final String cTimestampName = "ver_date";

    /**
     * Le nom des attributs à rechercher pour créer les listes.
     */
    public final Set<String> cList;

    /**
     * Le modes de tri.
     */
    public static final String cSort = "ver_version";

    /**
     * Lit la dernière verion de la base de données.
     *
     * @param aName
     *            nom de la version
     * @param connection
     *            Connexion pour exécuter la requête
     *
     * @return Un value object correspondant à la version de la base de données.
     *
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public VOVersionDB getVersion(String aName, Connection connection) throws SQLException {

        VOVersionDB vo = new VOVersionDB();
        vo.setProperty("ver_label", aName);

        // Prepare parameters
        List<DAOParameter> params = new ArrayList<>();
        Set<String> tableNames = new HashSet<>();
        tableNames.add(cTableName);
        params.add(new DAOParameter(Name.TABLE_NAMES, tableNames));
        params.add(new DAOParameter(Name.ATTRIBUTES, cList));
        String[] fields = new String[] { cSort };
        params.add(new DAOParameter(Name.SORT_FIELDS, fields));
        params.add(new DAOParameter(Name.SORT_ORIENTATION, ListHandler.Sort.DESCENDING));

        Collection<IValueObject> col = getList(vo, connection, params.toArray(new DAOParameter[params.size()]));

        return col.isEmpty() ? null : (VOVersionDB) col.iterator().next();
    }
}
