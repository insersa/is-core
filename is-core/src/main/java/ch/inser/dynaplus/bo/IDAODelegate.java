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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.inser.dynamic.common.DAOParameter;
import ch.inser.dynamic.common.DynamicDAO.Aggregator;
import ch.inser.dynamic.common.IDAOResult;
import ch.inser.dynamic.common.ILoggedUser;
import ch.inser.dynamic.common.IValueObject;
import ch.inser.dynaplus.util.IService;
import ch.inser.jsl.exceptions.ISException;
import ch.inser.jsl.list.ListHandler.Sort;

/**
 * @author INSER SA
 * @version 1.0
 * @created 29-févr.-2008 08:53:12
 */
public interface IDAODelegate extends IService {

    /**
     * Lecture d'une entité correspondante à l'identifiant spécifié depuis la base de données. Le record est retourné dans un value objet.
     *
     * @param id
     *            Identifiant du record à lire.
     * @param user
     *            Utilisateur
     * @param connection
     *            Connexion pour exécuter la requête
     *
     * @return Un value object correspondant à l'événement.
     *
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public IDAOResult getRecord(Object id, ILoggedUser user, Connection connection) throws SQLException;

    /**
     * Traite une requête de recherche et fournit une liste d'entités sous forme d'une collection.
     *
     * @param aVo
     *            Value object contenant les critères de recherche
     * @param aUser
     *            Utilisateur
     * @param aConnection
     *            Connexion pour exécuter la requête
     * @param aParameters
     *            Les paramêtres
     * @return Le resultat de la recherche
     * @throws ISException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public IDAOResult getList(IValueObject aVo, ILoggedUser aUser, Connection aConnection, DAOParameter... aParameters) throws ISException;

    /**
     * Requête de count
     *
     * @param vo
     *            Value object contenant les critères de recherche
     * @param connection
     *            Connexion pour exécuter la requête
     * @param aUser
     *            utilisateur
     * @param aParameters
     *            DAO parameters
     * @return nbr d'enregistrements
     * @throws SQLException
     *             erreur au niveau bd
     */
    public IDAOResult getListCount(IValueObject vo, Connection connection, ILoggedUser aUser, DAOParameter... aParameters)
            throws SQLException;

    /**
     * Met à jour une entité selon une collection de champs à mettre à jour et à un timestamp pour gérer la transaction longue.
     *
     * @param updateFields
     *            Collection Map des champs à mettre à jour.
     * @param id
     *            L'identifiant de l'objet à tester.
     * @param timestamp
     *            Timestamp pour la gestion de la transaction longue.
     * @param user
     *            Utilisateur.
     * @param connection
     *            Connexion pour exécuter la requête.
     *
     * @return Le nombre de records impliqués dans la mise à jour.
     *
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public int update(Map<String, Object> updateFields, Object id, Timestamp timestamp, ILoggedUser user, Connection connection)
            throws SQLException;

    /**
     * Retourne la prochaine valeur du ID de l'entité.
     *
     * @param user
     *            Utilisateur.
     * @param connection
     *            Connexion pour exécuter la requête.
     * @return prochain identifiant disponible
     *
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public Object getNextId(ILoggedUser user, Connection connection) throws SQLException;

    /**
     * Crée un nouveau record pour une entité.
     *
     * @param vo
     *            L'objet à créer.
     * @param user
     *            Utilisateur.
     * @param connection
     *            Connexion pour exécuter la requête.
     * @return nombre d'enregistrements créés
     *
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public IDAOResult create(IValueObject vo, ILoggedUser user, Connection connection) throws SQLException;

    /**
     * Suppression d'une entité.
     *
     * @param id
     *            ID du record à supprimer
     * @param timestamp
     *            Timestamp du record à supprimer pour gestion transaction longue
     * @param user
     *            Utilisateur
     * @param connection
     *            Connexion pour exécuter la requête.
     *
     * @return Le nombre de records mis à jour par la requête
     *
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public IDAOResult delete(Object id, Timestamp timestamp, ILoggedUser user, Connection connection) throws SQLException;

    /**
     * Lecture du timestamp d'une entité correspondant à l'identifiant spécifié.
     *
     * @param id
     *            Identifiant du record
     * @param user
     *            Utilisateur
     * @param connection
     *            Connexion pour exécuter la requête
     *
     * @return Un objet Timestamp ou null si pas trouvé.
     *
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public IDAOResult getTimestamp(Object id, ILoggedUser user, Connection connection) throws SQLException;

    /**
     * Retourne un champ donné pour un identifiant donné
     *
     * @param id
     *            identifiant du record dont on veut obtenir le champ
     * @param aFieldName
     *            nom du champ que lon désire obtenir
     * @param aConnection
     *            connection à la source de données
     * @return valeur du champ ou null si le record n'a pas été trouvé
     * @throws SQLException
     *             en cas de problème lors de la requête
     *
     */
    public IDAOResult getField(Object id, String aFieldName, Connection aConnection) throws SQLException;

    /**
     * Obtenition de la liste des valeurs d'un champ sur la base de la requête fournie
     *
     * @param aVo
     *            restriction pour la mise à jour (uniquement les champs de la table principale)
     * @param aFieldName
     *            le nom du champ à modifier
     * @param aConnection
     *            une connection à utiliser
     * @param aParameters
     *            DAOParameters: sort key, sort orientation
     * @return la liste des valeurs
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public IDAOResult getFieldsRequest(IValueObject aVo, String aFieldName, Connection aConnection, DAOParameter... aParameters)
            throws SQLException;

    /**
     * Obtenition de la liste des valeurs d'un champ sur la base de la requête fournie
     *
     * @param aVo
     *            restriction pour la mise à jour (uniquement les champs de la table principale)
     * @param aFieldName
     *            le nom du champ à modifier
     * @param aUser
     *            utilisateur
     * @param aConnection
     *            une connection à utiliser
     * @param aParameters
     *            DAOparameters: sort key, sort orientation
     * @return la liste des valeurs
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public IDAOResult getFieldsRequest(IValueObject aVo, String aFieldName, ILoggedUser aUser, Connection aConnection,
            DAOParameter... aParameters) throws SQLException;

    /**
     * Obtention l'aggrégation d'un champ sur la base de la requête fournie
     *
     * @param aVo
     *            restriction pour le calcul de la somme (uniquement les champs de la table principale)
     * @param aFieldName
     *            le nom du champ à modifier
     * @param aggr
     *            fonction d'aggrégation
     * @param aConnection
     *            une connection à utiliser
     * @return le max du champ
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public IDAOResult getAggregateField(IValueObject aVo, String aFieldName, Aggregator aggr, Connection aConnection) throws SQLException;

    /**
     * Actualise la valeur d'un champ pour l'enregistrement donné
     *
     * @param id
     *            identifiant du record dont on veut modifier le champ
     * @param aFieldName
     *            nom du champ à modifier
     * @param aValue
     *            valeur de la modification
     * @param aCon
     *            connection à la source de données
     * @return nombre d'enregistrements modifiés
     * @throws SQLException
     *             en cas de problèmes lors de la requête
     */
    public IDAOResult updateField(Object id, String aFieldName, Object aValue, Connection aCon) throws SQLException;

    /**
     * Actualise la valeur d'un champ pour les enregistrements respectant la requête donnée
     *
     * @param aVo
     *            requête
     * @param aFieldName
     *            nom du champ à modifier
     * @param aValue
     *            valeur de la modification
     * @param aCon
     *            connection à la source de données
     * @return nombre d'enregistrements modifiés
     * @throws SQLException
     *             en cas de problèmes lors de la requête
     */
    public IDAOResult updateFieldRequest(IValueObject aVo, String aFieldName, Object aValue, Connection aCon) throws SQLException;

    /**
     * Actualise la valeur de champs pour les enregistrements respectant la requête donnée
     *
     * @param aVo
     *            requête
     * @param aFieldNames
     *            noms des champs à modifier
     * @param aValues
     *            valeurs des modification
     * @param aCon
     *            connection à la source de données
     * @return nombre d'enregistrements modifiés
     * @throws SQLException
     *             en cas de problèmes lors de la requête
     */
    public int updateFieldsRequest(IValueObject aVo, String[] aFieldNames, Object[] aValues, Connection aCon) throws SQLException;

    /**
     * Actualise les valeurs de plusieurs champs pour l'enregistrement donné
     *
     * @param id
     *            identifiant du record dont on veut modifier les champs
     * @param aFieldNames
     *            noms des champs à modifier
     * @param aValues
     *            valeurs à inserer
     * @param aCon
     *            connexion
     * @return nbr d'enregistrements modifiés
     * @throws SQLException
     *             en cas de problèmes lors de la requête
     */
    public IDAOResult updateField(Object id, String[] aFieldNames, Object[] aValues, Connection aCon) throws SQLException;

    /**
     * Actualise la valeur d'un champ pour les enregistrements donnés
     *
     * @param ids
     *            identifiant des records dont on veut modifier le champ
     * @param aFieldName
     *            nom du champ à modifier
     * @param aValue
     *            valeur de la modification
     * @param aCon
     *            connection à la source de données
     * @return nombre d'enregistrements modifiés
     * @throws SQLException
     *             en cas de problèmes lors de la requête
     */
    public int updateFields(List<Object> ids, String aFieldName, Object aValue, Connection aCon) throws SQLException;

    /**
     * Retourne la clé du tri par défaut
     *
     * @return nom de la clé par défaut
     */
    public String getDefaultOrderKey();

    /**
     *
     * @return orientation du tri par défaut
     */
    public Sort getDefaultSortOrder();

    /**
     * @return la liste des champs défini dans le fichier de configuration DAO qui sont updatable
     */
    public Set<String> getListUpdateFields();

    /**
     *
     * @return liste des propriétés avec configuration DAOlist=true
     */
    public List<String> getDAOList();

    /**
     * Exécution de fonctionnalité spécialisé, exemple exécution d'une fonction PL/SL
     *
     * @param aNameMethode
     *            nom de la méthode
     * @param aObject
     *            données désirant fournir à la fonction
     * @param aUser
     *            droit utilisateur
     * @param aConnection
     *            connexion DB
     * @return données de la db
     * @throws ISException
     *             en cas de problème lors de l'exécution
     */
    public Object executeMethode(String aNameMethode, Object aObject, ILoggedUser aUser, Connection aConnection) throws ISException;

    /**
     * Lit un enregistrement dans la table spécifiée correspondant à l'ID spécifié
     *
     * @param aTable
     *            nom de la table
     * @param aField
     *            nom du champ id
     * @param aValue
     *            valeur de l'id
     * @param aCon
     *            connexion
     * @return Un value object contenant l'enregistrement demandé
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public IDAOResult getRecord(String aTable, String aField, Object aValue, Connection aCon) throws SQLException;

    /**
     * getRecord avec la liste des champs à la place de *. Utilisé quand la liste de champs contient des champs qui ne figurent pas dans la
     * table, par example des champs calculés.
     *
     * @param id
     *            Identifiant du record à lire.
     * @param user
     *            Utilisateur
     * @param connection
     *            Connexion pour exécuter la requête
     * @return Un value object correspondant à l'événement.
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public IDAOResult getRecordFull(Object id, ILoggedUser user, Connection connection) throws SQLException;

    /**
     * Traite une requête de recherche à la table spécifiée et fournit une liste d'entités sous forme d'une collection.
     *
     * @param aTable
     *            nom de la table
     * @param aQuery
     *            la requête
     * @param aCon
     *            connexion
     * @return Une collection contenant les value object pour les différents records ou une collection vide (non nulle) s'il n'y a aucun
     *         résultat.
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public IDAOResult getList(String aTable, IValueObject aQuery, Connection aCon) throws SQLException;

    /**
     * @param aTable
     *            nom de table
     * @param aQuery
     *            requête vo
     * @param aRowNum
     *            nbr de lignes maximales dans le résultat. 0 = aucun limitation-
     * @param aCon
     *            connexion
     * @return liste de vos
     * @throws SQLException
     *             en cas de problèmes lors de la requête
     */
    public IDAOResult getList(String aTable, IValueObject aQuery, Integer aRowNum, Connection aCon) throws SQLException;

}