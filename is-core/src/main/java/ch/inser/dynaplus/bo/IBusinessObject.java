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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import ch.inser.dynamic.common.DAOParameter;
import ch.inser.dynamic.common.DynamicDAO.Aggregator;
import ch.inser.dynamic.common.IContextManager;
import ch.inser.dynamic.common.IDAOResult;
import ch.inser.dynamic.common.ILoggedUser;
import ch.inser.dynamic.common.IValueObject;
import ch.inser.dynamic.quality.IQualityController;
import ch.inser.dynaplus.util.Constants.Mode;
import ch.inser.dynaplus.vo.IVOFactory;
import ch.inser.jsl.exceptions.ISException;
import ch.inser.jsl.list.ListHandler.Sort;

/**
 * @author INSER SA
 * @version 1.0
 * @created 29-févr.-2008 08:53:12
 */
public interface IBusinessObject {
    // ---------------------------------------------------- Méthodes membres
    /**
     * Lit un enregistrement correspondant à l'ID spécifié.
     *
     * @param id
     *            Identifiant
     * @param con
     *            connection à la source de données
     * @param user
     *            Utilisateur
     * @param getParent
     * @param aParameters
     *            paramètres pour la récuperation de l'enregistrement
     * @return Un value object contenant l'enregistrement demandé, ou selon paramètres fournis, un autre format récuperable par getValue()
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public IDAOResult getRecord(Object id, Connection con, ILoggedUser user, boolean getParent, DAOParameter... aParameters)
            throws SQLException;

    /**
     *
     * getRecord avec la liste des champs à la place de *. Utilisé quand la liste de champs contient des champs qui ne figurent pas dans la
     * table, par example des champs calculés.
     *
     * @param id
     *            Identifiant
     * @param con
     *            connection à la source de données
     * @param user
     *            Utilisateur
     * @param getParent
     * @param aParameters
     *            paramètres pour la récuperation de l'enregistrement
     * @return Un value object contenant l'enregistrement demandé, ou selon paramètres fournis, un autre format récuperable par getValue()
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public IDAOResult getRecordFull(Object id, Connection con, ILoggedUser user, boolean getParent, DAOParameter... aParameters)
            throws SQLException;

    /**
     * Met à jour un enregistrement. D'abord, le value object passé en paramètre doit être comparé avec celui de référence dans cet objet
     * métier afin de déterminer les attributs qui ont été modifiés. Une collection de ces attributs doit être constituée et passée à la
     * méthode de mise à jour dans le DAO correspondant.
     *
     * @param valueObject
     *            Value object contenant l'enregistrement modifié par l'utilisateur.
     * @param con
     *            connection à la source de données
     * @param user
     *            utilisateur effectuant la requête
     *
     * @return Le nombre de records impliqués par la mise à jour si positif, sinon:<br>
     *         0 : aucun record mis à jour <br>
     *         -1 : pas trouvé le record à modifier <br>
     *         -2 : le record a changé entre temps <br>
     *         -3 : rien n'a été modifié par le user, pas de update (court-circuit)<br>
     *         -4 : les droits d'accès n'autorisent pas cette modif
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public IDAOResult update(IValueObject valueObject, Connection con, ILoggedUser user) throws SQLException;

    /**
     * Création ou mise à jour d'une liste de vo
     *
     * @param aLstValueObject
     *            liste contenant tous les vos à insérer dans la bd
     * @param aCon
     *            connection à la source de données
     * @param aUser
     *            l'utilisateur effectuant la requête
     * @return Le nombre de records impliqués par la mise à jour si positif, sinon:<br>
     *         0 : aucun record mis à jour <br>
     *         -1 : pas trouvé le record à modifier <br>
     *         -2 : le record a changé entre temps <br>
     *         -3 : rien n'a été modifié par le user, pas de update (court-circuit)<br>
     *         -4 : les droits d'accès n'autorisent pas cette modif
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public IDAOResult update(List<IValueObject> aLstValueObject, Connection aCon, ILoggedUser aUser) throws SQLException;

    /**
     * Crée un nouvel enregistrement.
     *
     * @param valueObject
     *            Value object contenant l'enregistrement à créer.
     * @param con
     *            connection à la source de données
     * @param user
     *            utilisateur effectuant l'action
     * @return Le ID du record nouvellement créé
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public IDAOResult create(IValueObject valueObject, Connection con, ILoggedUser user) throws SQLException;

    /**
     * Création multiples de nouveaux enregistrements.
     *
     * @param valueObject
     *            liste de value objects contenant l'enregistrement à créer.
     * @param con
     *            connection à la source de données
     * @param user
     *            utilisateur effectuant l'action
     * @return liste des ID des records nouvellement créés
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public IDAOResult create(List<IValueObject> valueObject, Connection con, ILoggedUser user) throws SQLException;

    /**
     * Supprime un enregistrement.
     *
     * @param aId
     *            L'identifiant de l'objet à supprimer.
     * @param aTimestamp
     *            Timestamp de l'objet d'origine
     * @param aConnection
     *            connection à la source de données
     * @param aUser
     *            Utilisateur
     * @param aParameters
     *            Les paramêtres
     * @return Le resultat de l'effacement
     * @throws ISException
     */
    public IDAOResult delete(Object aId, Timestamp aTimestamp, Connection aConnection, ILoggedUser aUser, DAOParameter... aParameters)
            throws ISException;

    /**
     * Supprime un enregistrement et ses enfants (configuration dans fichier de config du BO).
     *
     * @param aId
     *            L'identifiant de l'objet à supprimer
     * @param aTimestamp
     *            Timestamp de l'objet d'origine
     * @param aConnection
     *            connection à la source de données
     * @param aUser
     *            Utilisateur
     * @param aParameters
     *            Les paramêtres
     * @return Le resultat de l'effacement
     * @throws ISException
     *             en cas de problème au niveau de la requête à la base de données
     */
    public IDAOResult deleteCascade(Object aId, Timestamp aTimestamp, Connection aConnection, ILoggedUser aUser,
            DAOParameter... aParameters) throws ISException;

    /**
     * Supprime les enregistrements donnés par la liste
     *
     * ATTENTION: cette méthode est non-sécurisé au niveau concurrence. Le timestamp n'est pas considéré, les enregistrements sont supprimés
     * de force.
     *
     * @param aIds
     *            liste des identifiants à supprimer
     * @param aConnection
     *            connection à la source de données
     * @param aUser
     *            utilisateur effectuant la requête
     * @param aParameters
     *            Les paramêtres
     * @return Le resultat de l'effacement
     * @throws ISException
     *             en cas de problème au niveau de la requête à la base de données
     */
    public IDAOResult deleteMulti(List<Object> aIds, Connection aConnection, ILoggedUser aUser, DAOParameter... aParameters)
            throws ISException;

    /**
     * Supprime les enregistrements donnés par le query vo, attention le timestamp n'est pas considéré, les enregistrements sont supprimés
     * de force.
     *
     * @param aVo
     *            contient le query de suppression
     * @param aConnection
     *            connection à la source de données
     * @param aUser
     *            utilisateur effectuant la requête
     * @param aParameters
     *            Les paramêtres
     * @return Le resultat de l'effacement
     * @throws ISException
     *             en cas de problème au niveau de la requête à la base de données
     */
    public IDAOResult deleteQuery(IValueObject aVo, Connection aConnection, ILoggedUser aUser, DAOParameter... aParameters)
            throws ISException;

    /**
     * Supprime les enregistrements donnés par l'addition des query vos, attention le timestamp n'est pas considéré, les enregistrements
     * sont supprimés de force.
     *
     * @param aVos
     *            liste de valueobjects contenant les queries de sélection
     * @param aConnection
     *            connection à la source de données
     * @param aUser
     *            utilisateur effectuant la requête
     * @param aParameters
     *            Les paramêtres
     * @return Le resultat de l'effacement
     * @throws ISException
     *             en cas de problème au niveau de la requête à la base de données
     */
    public IDAOResult deleteMultiQuery(List<IValueObject> aVos, Connection aConnection, ILoggedUser aUser, DAOParameter... aParameters)
            throws ISException;

    /**
     * Retourne le timestamp
     *
     * @param id
     *            Identifiant de l'enregistrement dont on veut le timestamp
     * @param con
     *            connection à la source de données
     * @param user
     *            Utilisateur effectuant la requête
     * @return Le timestamp de l'objet
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public IDAOResult getTimestamp(Object id, Connection con, ILoggedUser user) throws SQLException;

    /**
     * Retourne la valeur d'un champ pour l'enregistrement donné
     *
     * @param id
     *            identifiant de l'objet dont on veut le champ
     * @param fieldName
     *            nom du champ que l'on veut obtenir
     * @param con
     *            connection à la source de données
     * @return valeur du champ, ou null sir l'enregistrement n'existe pas
     * @throws SQLException
     *             si un problème survient au niveau base de données
     */
    public IDAOResult getField(Object id, String fieldName, Connection con) throws SQLException;

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
     *            paramètres optionnels pour la construction de la requête
     * @return la liste des valeurs
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public IDAOResult getFieldsRequest(IValueObject aVo, String aFieldName, Connection aConnection, DAOParameter... aParameters)
            throws SQLException;

    /**
     * Obtention de la liste des valeurs d'un champ sur la base de la requête fournie
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
     *            paramètres optionnels pour la construction de la requête
     * @return la liste des valeurs
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public IDAOResult getFieldsRequest(IValueObject aVo, String aFieldName, ILoggedUser aUser, Connection aConnection,
            DAOParameter... aParameters) throws SQLException;

    /**
     * Obtention de l'aggrégation d'un champ sur la base de la requête fournie
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
     *            identifiant de l'objet dont on veut modifier le champ
     * @param aFieldName
     *            nom du champ que l'on veut modifier
     * @param aValue
     *            nouvelle valeur du champ
     * @param aCon
     *            connection à la source de données
     * @return nombre d'enregistrements modifiés
     * @throws SQLException
     *             si un problème survient au niveau base de données
     */
    public IDAOResult updateField(Object id, String aFieldName, Object aValue, Connection aCon) throws SQLException;

    /**
     * Actualise les valeurs de plusieurs champs pour l'enregistrement donné
     *
     * @param id
     *            identifiant de l'objet
     * @param aFieldNames
     *            noms des champs à modifier
     * @param aValues
     *            valeurs à inserer
     * @param con
     *            connexion
     * @return nbr de records modifiés
     * @throws SQLException
     *             si un problème survient au niveau base de données
     */
    public IDAOResult updateFields(Object id, String[] aFieldNames, Object[] aValues, Connection con) throws SQLException;

    /**
     * Update some values from a value object.
     *
     * @param aValueObject
     *            the value object, must contain the object ID and timestamp to find and check the object to update, if a field value is
     *            <code>null</code> it's unchanged, if it's <code>Operator.IS_NULL</code> it's removed
     * @param aConnection
     *            the connection
     * @param aUser
     *            the user
     * @return the full updated <code>IValueObject</code>
     * @throws ISException
     *             is a problem was found
     */
    public IDAOResult updateFields(IValueObject aValueObject, Connection aConnection, ILoggedUser aUser) throws ISException;

    /**
     * Actualise la valeur d'un champ pour les enregistrements donnés
     *
     * @param ids
     *            identifiant des objets dont on veut modifier le champ
     * @param aFieldName
     *            nom du champ que l'on veut modifier
     * @param aValue
     *            nouvelle valeur du champ
     * @param aCon
     *            connection à la source de données
     * @return nombre d'enregistrements modifiés
     * @throws SQLException
     *             si un problème survient au niveau base de données
     */
    public int updateFields(List<Object> ids, String aFieldName, Object aValue, Connection aCon) throws SQLException;

    /**
     * Mise à jour de plusieurs champs sur une ligne, champ obligatoire timestamp et id, ensuite mettre dans le vo les champs à modifier
     *
     * @param valueObject
     *            vo possédant les champs à modifier
     * @param con
     *            connection à la source de données
     * @param user
     *            utilisateur effectuant la requête
     * @return nombre d'enregistrements modifiés
     * @throws SQLException
     *             erreur bd
     */
    public int updateFieldsRow(IValueObject valueObject, Connection con, ILoggedUser user) throws SQLException;

    // ----------------------------------------- Méthodes d'initialisation de VO
    /**
     * Méthode retournant un VO avec valeures initiales
     *
     * @param user
     *            utilisateur effectuant la requête
     * @param con
     *            connection à la source de données
     *
     * @param getParent
     *            récupère le parent? Pour éviter les problèmes de récursions...
     * @return un value object initialisé
     * @throws SQLException
     *             en cas de problème au niveau base de données
     */
    public IValueObject getInitVO(ILoggedUser user, Connection con, boolean getParent) throws SQLException;

    /**
     * Méthode retournant un VO avec valeures initiales
     *
     * @param mode
     *            le mode d'initialisation @see Mode
     *
     * @param user
     *            utilisateur effectuant la requête
     * @param con
     *            connection à la source de données
     *
     * @param getParent
     *            récupère le parent? Pour éviter les problèmes de récursions...
     * @return un value object initialisé
     * @throws SQLException
     *             en cas de problème au niveau base de données
     */
    public IValueObject getInitVO(Mode mode, ILoggedUser user, Connection con, boolean getParent) throws SQLException;

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
     * @return nbr d'enregistrements
     * @throws SQLException
     *             erreur bd
     */
    public IDAOResult getListCount(IValueObject vo, Connection connection, ILoggedUser aUser, DAOParameter... aParameters)
            throws SQLException;

    /**
     * Permet de modifier un champ sur chaque ligne d'une liste, la valeur à inserér pour le changement se trouve dans aLstValue.
     *
     * @param aLstId
     *            list des ids à modifier
     * @param aFieldName
     *            nom du champ à modifier dans la db
     * @param aLstValue
     *            valeur à modifier, insérer dans le même ordre que aLstIds
     * @param aCon
     *            paramètre de connexion
     * @param aUser
     *            paramètre de l'utilisateur
     * @return nombre d'enregistrements modifiés
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public IDAOResult updateField(List<Object> aLstId, String aFieldName, List<Object> aLstValue, Connection aCon, ILoggedUser aUser)
            throws SQLException;

    /**
     * Permet de modifier un champ pour chaque enregistrement repectant la requête donnée.
     *
     * @param aVo
     *            requête
     * @param aFieldNames
     *            nom du champ à modifier dans la db
     * @param aValues
     *            valeurs à modifier dans le même ordre que aFielNames
     * @param aCon
     *            paramètre de connexion
     * @return nombre d'enregistrements modifiés
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public int updateFieldsRequest(IValueObject aVo, String[] aFieldNames, Object[] aValues, Connection aCon) throws SQLException;

    /**
     * Permet de modifier des champs pour chaque enregistrement repectant la requête donnée.
     *
     * @param aVo
     *            requête
     * @param aFieldNames
     *            nom des champs à modifier dans la db
     * @param aValues
     *            valeurs à modifier, insérer dans le même ordre que aLstIds
     * @param aCon
     *            paramètre de connexion
     * @return nombre d'enregistrements modifiés
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public IDAOResult updateFieldRequest(IValueObject aVo, String aFieldNames, Object aValues, Connection aCon) throws SQLException;

    /**
     * Exécution d'une méthode spécialisée dans le BO
     *
     * @param aNameMethode
     *            nom de la méthode à exécuter
     * @param aVos
     *            liste des value objects nécessaires à l'exécution
     * @param aUser
     *            utilisateur exécutant la méthode
     * @param aConnection
     *            connection à la source de données
     * @return une collection
     * @throws ISException
     *             en cas de problème lors de l'appel de la méthode.
     */
    public Collection<?> executeMethode(String aNameMethode, List<?> aVos, ILoggedUser aUser, Connection aConnection) throws ISException;

    /**
     * Exécution d'une méthode spécialisée dans le BO
     *
     * @param aNameMethode
     *            nom de la méthode à exécuter
     * @param anObject
     *            un objet nécessaires à l'exécution
     * @param aUser
     *            utilisateur exécutant la méthode
     * @param aConnection
     *            connection à la source de données
     * @return un objet de résultat
     * @throws ISException
     *             en cas de problème lors de l'exécution de la méthode.
     */
    public Object executeMethode(String aNameMethode, Object anObject, ILoggedUser aUser, Connection aConnection) throws ISException;

    /**
     * Filtre des champs en autorisation de lecture
     *
     * @param aValues
     *            map de champs,valeurs à filtrer
     * @param aUser
     *            utilisateur exécutant la méthode
     */
    public void readFilter(Map<String, Object> aValues, ILoggedUser aUser);

    /**
     * Filtre des champs en autorisation d'écriture
     *
     * @param aValues
     *            map de champs,valeurs à filtrer
     * @param aUser
     *            utilisateur exécutant la méthode
     *
     */
    public void writeFilter(Map<String, Object> aValues, ILoggedUser aUser);

    /**
     * Modifie, crée et supprime les enfants du vo.
     *
     * Les enfants se trouvent sous forme d'une structure fixée sous:
     * <li>valueObject.getProperty("childrenlistvo")</li> <br/>
     * on touve dans cet objet les parties suivantes pour chaque type d'enfant:
     * <li>"mapVOState" : donne le type de modification pour les enfants après update (CREATE,UPDATE)</li>
     * <li>"lstRemoveField" : contient les enfants qui sont à supprimer.</li>
     * <li>"ListVO" : contient la liste des value object que l'on désire avoir après update</li> <br/>
     * Les modifications de l'action sur les enfants sont stockés dans le valueObject.
     *
     * @param valueObject
     *            vo du parent
     * @param con
     *            connection à la source de données
     * @param user
     *            utilisateur effectuant l'appel
     * @return le nombre de modification
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public IDAOResult loadChildren(IValueObject valueObject, Connection con, ILoggedUser user) throws SQLException;

    /**
     * Modifie, crée et supprime les enfants du vo. La différence avec la méthode précédante est le fait que les modifications de l'action
     * sur les enfants sont stockés dans le resultVo
     *
     * @param originalVo
     *            vo du parent
     * @param resultVo
     *            vo modifié par l'action
     * @param con
     *            connection à la source de données
     * @param user
     *            utilisateur effectuant l'appel
     * @return le nombre de modifications
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public IDAOResult loadChildren(IValueObject originalVo, IValueObject resultVo, Connection con, ILoggedUser user) throws SQLException;

    /**
     * Crée/modifie/supprime les enfants qui sont stockés dans le propriété "childrenlistObjects". Ce propriété est utilisé quand les
     * enfants sont manipulés sans aller par JSF
     *
     * @param aVoOriginal
     *            vo du parent avec enfants avant la création/màj du parent
     * @param aVoResult
     *            vo du parent crée/màj avec un id mais sans enfants
     * @param aCon
     *            connexion
     * @param aUser
     *            utilisateur
     * @return nbr d'enfants crées/modifiés/supprimés
     * @throws SQLException
     *             problème au niveau base de données
     */
    public IDAOResult loadChildrenObjects(IValueObject aVoOriginal, IValueObject aVoResult, Connection aCon, ILoggedUser aUser)
            throws SQLException;

    /**
     * Retourne la clé du tri par défaut
     *
     * @return la clé du tri par défaut
     */
    public String getDefaultOrderKey();

    /**
     *
     * @return l'orientation du tri par défaut
     */
    public Sort getDefaultSortOrder();

    /**
     *
     * @return liste des propriétés avec configuration DAOlist=true
     */
    public List<String> getDAOList();

    /**
     * @param aDao
     *            dao mis à disposition de l'objet métier lors de l'initialisation
     */
    public void setDao(IDAODelegate aDao);

    /**
     * @param aVOFactory
     *            VOFactory mis à disposition de l'objet métier lors de l'initialisation
     */
    public void setVOFactory(IVOFactory aVOFactory);

    /**
     * @param aBOFactory
     *            BOFactory mis à disposition de l'objet métier lors de l'initialisation
     */
    public void setBOFactory(BOFactory aBOFactory);

    /**
     * @return BOFactory mis à disposition de l'objet métier lors de l'initialisation
     */
    public BOFactory getBOFactory();

    /**
     * @return VOFactory mis à disposition de l'objet métier lors de l'initialisation
     */
    public IVOFactory getVOFactory();

    /**
     * @return DAODelegate mis à disposition de l'objet métier lors de l'initialisation
     */
    public IDAODelegate getDao();

    /**
     *
     * @param aContextManager
     *            ContextManager mis à disposition de l'objet métier lors de l'initialisation
     */
    public void setContextManager(IContextManager aContextManager);

    /**
     *
     * @return ContextManager mis à disposition de l'objet métier lors de l'initialisation
     */
    public IContextManager getContextManager();

    /**
     * Injection du controller quality
     *
     * @param aController
     */
    public void setQualityController(IQualityController aController);

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
     * @param aRowNum
     *            nbr de lignes maximales dans le résultat. 0 = sans limite.
     * @param aCon
     *            connexion
     * @return liste de vos
     * @throws SQLException
     *             erreur bd
     */
    public IDAOResult getList(String aTable, IValueObject aQuery, Integer aRowNum, Connection aCon) throws SQLException;

}