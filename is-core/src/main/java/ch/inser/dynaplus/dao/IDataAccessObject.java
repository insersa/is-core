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
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.inser.dynamic.common.DAOParameter;
import ch.inser.dynamic.common.DynamicDAO.Aggregator;
import ch.inser.dynamic.common.IDAOResult;
import ch.inser.dynamic.common.ILoggedUser;
import ch.inser.dynamic.common.IValueObject;
import ch.inser.dynamic.util.VOInfo;
import ch.inser.dynaplus.vo.IVOFactory;
import ch.inser.jsl.exceptions.ISException;
import ch.inser.jsl.list.ListHandler.Sort;

/**
 * @author INSER SA
 * @version 1.0
 * @created 29-févr.-2008 08:53:13
 */
public interface IDataAccessObject {

    /**
     *
     * @param voInfo
     *            vo config
     */
    public void init(VOInfo voInfo);

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
     * Méthode permettant de faire des count dur une requête
     *
     * @param vo
     *            vo requête
     * @param connection
     *            connexion
     * @param aUser
     *            utilisateur
     * @param aParameters
     *            DAOParameters
     * @return nbr d'enregistrement
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
     * @return prochain id proposé par sequence
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
     * @return nbr d'enregistrements crées
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
     *            de l'objet dont on veut le champ
     * @param aFieldName
     *            le nom du champ
     * @param aConnection
     *            connection à la base de données
     * @return la valeur du champ
     * @throws SQLException
     *             erreur bd
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
     *            DAOParameters, ex. sort, tablesnames, security clause, joins
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
     *            DAOParameters: sort, table names, joins
     * @return la liste des valeurs
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public IDAOResult getFieldsRequest(IValueObject aVo, String aFieldName, ILoggedUser aUser, Connection aConnection,
            DAOParameter... aParameters) throws SQLException;

    /**
     * Obtention de l'aggregation d'un champ sur la base de la requête fournie
     *
     * @param aVo
     *            restriction pour le calcul de l'aggrégation (uniquement les champs de la table principale)
     * @param aFieldName
     *            le nom du champ à modifier
     * @param aggr
     *            aggrégateur SQL
     * @param aConnection
     *            une connection à utiliser
     * @return le max du champ
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public IDAOResult getAggregateField(IValueObject aVo, String aFieldName, Aggregator aggr, Connection aConnection) throws SQLException;

    /**
     * Mise un jour d'un champ avec la valeur fournie
     *
     * @param id
     *            id de l'objet
     * @param aFieldName
     *            le nom du champ à modifier
     * @param aValue
     *            la valeur à modifier
     * @param aConnection
     *            une connection à utiliser
     * @return le nombre d'enregistrements modifiés
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public IDAOResult updateField(Object id, String aFieldName, Object aValue, Connection aConnection) throws SQLException;

    /**
     * Actualise la valeur d'un champ pour les enregistrements respectant la requête donnée
     *
     * @param aVo
     *            requête
     * @param aFieldName
     *            nom du champ à modifier
     * @param aValue
     *            valeur de la modification
     * @param aConnection
     *            connection à la source de données
     * @return nombre d'enregistrements modifiés
     * @throws SQLException
     *             en cas de problèmes lors de la requête
     */
    public IDAOResult updateFieldRequest(IValueObject aVo, String aFieldName, Object aValue, Connection aConnection) throws SQLException;

    /**
     * Actualise les valeurs de champs pour les enregistrements respectant la requête donnée
     *
     * @param aVo
     *            requête
     * @param aFieldName
     *            nom de champ à modifier
     * @param aValue
     *            valeurs de la modification
     * @param aConnection
     *            connection à la source de données
     * @return nombre d'enregistrements modifiés
     * @throws SQLException
     *             en cas de problèmes lors de la requête
     */
    public int updateFieldsRequest(IValueObject aVo, String[] aFieldName, Object[] aValue, Connection aConnection) throws SQLException;

    /**
     * Actualise les valeurs de plusieurs champs pour l'enregistrement donné
     *
     * @param id
     *            identifiant de l'objet
     * @param aFieldNames
     *            noms des champs à modifier
     * @param aValues
     *            valeurs à inserer
     * @param aConnection
     *            connexion
     * @return nbr de records modifiés
     * @throws SQLException
     *             erreur bd
     */
    public IDAOResult updateFields(Object id, String[] aFieldNames, Object[] aValues, Connection aConnection) throws SQLException;

    /**
     * Mise un jour d'un champ avec la valeur fournie sur un ensemble d'id.
     *
     * @param ids
     *            ids de l'objets à modifier
     * @param aFieldName
     *            le nom du champ à modifier
     * @param aValue
     *            la valeur à modifier
     * @param aConnection
     *            une connection à utiliser
     * @return le nombre d'enregistrements modifiés
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public int updateFields(Collection<Object> ids, String aFieldName, Object aValue, Connection aConnection) throws SQLException;

    /**
     * @return Retourne la clé du tri par défaut
     */
    public String getDefaultOrderKey();

    /**
     *
     * @return Retourne l'orientation du tri par défaut
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
     *             erreur bd
     */
    public Object executeMethode(String aNameMethode, Object aObject, ILoggedUser aUser, Connection aConnection) throws ISException;

    /**
     * Crée la requète SQL de l'enfant qui va fonctionner comme filtre dans le vo du parent
     *
     * @param aVo
     *            vo de l'enfant
     * @param aChildrenLink
     *            le nom du FK entre l'enfant et parent (<enf_par_id>)
     * @param aParentTable
     *            le nom de la table parent
     * @return la requète SQL "select <enf_par_id> from <enf-tables> where <enf-filtre>"
     */
    public String getChildQuery(IValueObject aVo, String aChildrenLink, String aParentTable);

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
     *
     * @param aTable
     *            nom de table
     * @param aQuery
     *            vo requête
     * @param aRownum
     *            nbr de lignes maximales dans le résultat. 0=sans limite.
     * @param aCon
     *            connexion
     * @return liste de vos
     * @throws SQLException
     *             erreur bd
     */
    public IDAOResult getList(String aTable, IValueObject aQuery, Integer aRownum, Connection aCon) throws SQLException;

    /**
     *
     * @return VO factory à disposition pour DAO
     */
    public IVOFactory getVOFactory();

    /**
     *
     * @param aVOFactory
     *            VO factory à disposition pour DAO
     */
    public void setVOFactory(IVOFactory aVOFactory);

}