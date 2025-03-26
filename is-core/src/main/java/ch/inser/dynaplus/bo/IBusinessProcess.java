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

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

import ch.inser.dynamic.common.DAOParameter;
import ch.inser.dynamic.common.DynamicDAO.Aggregator;
import ch.inser.dynamic.common.IContextManager;
import ch.inser.dynamic.common.IDAOResult;
import ch.inser.dynamic.common.ILoggedUser;
import ch.inser.dynamic.common.IValueObject;
import ch.inser.dynaplus.format.IFormatEngine;
import ch.inser.dynaplus.util.Constants.Mode;
import ch.inser.dynaplus.vo.IVOFactory;
import ch.inser.jsl.exceptions.ISException;
import ch.inser.jsl.list.ListHandler.Sort;

/**
 * @author INSER SA
 * @version 1.0
 * @created 29-févr.-2008 08:53:12
 */
public interface IBusinessProcess {
    /**
     * Lit un enregistrement correspondant à l'ID spécifié.
     *
     * @param id
     *            Identifiant
     * @param user
     *            Utilisateur
     * @param aParameters
     *            paramètres pour la récuperation de l'enregistrement
     * @return Un value object contenant l'enregistrement demandé, ou selon paramètres fournis, un autre format récuperable par getValue()
     * @throws ISException
     *             en cas de problème au niveau de la requête à la base de données.<br>
     *             <br>
     *             <img src="{@docRoot}/doc-files/getRecord.bmp">
     */
    public IDAOResult getRecord(Object id, ILoggedUser user, DAOParameter... aParameters) throws ISException;

    /**
     *
     * getRecord avec la liste des champs à la place de *. Utilisé quand la liste de champs contient des champs qui ne figurent pas dans la
     * table, par example des champs calculés.
     *
     * @param id
     *            Identifiant
     * @param user
     *            Utilisateur
     * @param aParameters
     *            paramètres pour la récuperation de l'enregistrement
     * @return Un value object contenant l'enregistrement demandé, ou selon paramètres fournis, un autre format récuperable par getValue()
     * @throws ISException
     *             en cas de problème au niveau de la requête à la base de données.<br>
     *             <br>
     *             <img src="{@docRoot}/doc-files/getRecord.bmp">
     */
    public IDAOResult getRecordFull(Object id, ILoggedUser user, DAOParameter... aParameters) throws ISException;

    /**
     * Met à jour un enregistrement. D'abord, le value object passé en paramètre doit être comparé avec celui de référence dans cet objet
     * métier afin de déterminer les attributs qui ont été modifiés. Une collection de ces attributs doit être constituée et passée à la
     * méthode de mise à jour dans le DAO correspondant.
     *
     * @param valueObject
     *            Value object contenant l'enregistrement modifié par l'utilisateur.
     * @param user
     *            utilisateur effectuant la requête
     * @return DAOResult contenant le VO de l'enregistrement et le nombre de records impliqués par la mise à jour si positif, sinon:<br>
     *         0 : aucun record mis à jour <br>
     *         -1 : pas trouvé le record à modifier <br>
     *         -2 : le record a changé entre temps <br>
     *         -3 : rien n'a été modifié par le user, pas de update (court-circuit)<br>
     *         -4 : les droits d'accès n'autorisent pas cette modif
     *
     * @throws ISException
     *             en cas de problème au niveau de la requête à la base de données. <br>
     *             <br>
     *             <img src="{@docRoot}/doc-files/update.bmp">
     */
    public IDAOResult update(IValueObject valueObject, ILoggedUser user) throws ISException;

    /**
     * Update and delete the records using the business process.
     *
     * @param aRecords
     *            the records to add or/and update if necessary
     * @param aDeletes
     *            the records to delete
     * @param aLoggedUser
     *            the logged user
     * @param aParameter
     *            the parameters
     * @return the business process result
     * @throws ISException
     *             for any exception
     */
    public IDAOResult update(List<IValueObject> aRecords, List<IValueObject> aDeletes, ILoggedUser user, DAOParameter... aParameters)
            throws ISException;

    /**
     * Crée un nouvel enregistrement.
     *
     * @param valueObject
     *            Value object contenant l'enregistrement à créer.
     * @param user
     *            utilisateur effectuant la requête
     * @return Le record nouvellement créé
     * @throws ISException
     *             en cas de problème au niveau de la requête à la base de données. <br>
     *             <br>
     *             <img src="{@docRoot}/doc-files/create.bmp">
     */
    public IDAOResult create(IValueObject valueObject, ILoggedUser user) throws ISException;

    /**
     * Crée de nouveaux enregistrements.
     *
     * @param valueObject
     *            Liste de value object contenant les enregistrements à créer.
     * @param user
     *            utilisateur effectuant la requête
     * @return Le resultat de la création
     * @throws ISException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public IDAOResult create(List<IValueObject> valueObject, ILoggedUser user) throws ISException;

    /**
     * Supprime un enregistrement.
     *
     * @param aId
     *            L'identifiant de l'objet à supprimer.
     * @param aTimestamp
     *            Timestamp de l'objet d'origine
     * @param aUser
     *            Utilisateur
     * @param aParameters
     *            Les paramêtres
     * @return Le resultat de l'effacement
     * @throws ISException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public IDAOResult delete(Object aId, Timestamp aTimestamp, ILoggedUser aUser, DAOParameter... aParameters) throws ISException;

    /**
     * Suppression d'un enregistrement, ainsi que les enfants (configuration dans le fichier de config du BO).
     *
     * @param aId
     *            L'identifiant de l'objet à supprimer.
     * @param aTimestamp
     *            Timestamp de l'objet d'origine
     * @param aUser
     *            Utilisateur
     * @param aParameters
     *            Les paramêtres
     * @return Le resultat de l'effacement
     * @throws ISException
     *             en cas de problème au niveau de la requête à la base de données<br>
     *             <br>
     *             <img src="{@docRoot}/doc-files/deleteCascade.bmp">
     */
    public IDAOResult deleteCascade(Object aId, Timestamp aTimestamp, ILoggedUser aUser, DAOParameter... aParameters) throws ISException;

    /**
     * Supprime les enregistrements donnés par la liste, attention le timestamp n'est pas considéré, les enregistrements sont supprimés de
     * force.
     *
     * @param aIds
     *            liste des identifiants des enregistrements à suppromer
     * @param aUser
     *            utilisateur effectuant la requête
     * @param aParameters
     *            Les paramêtres
     * @return Le resultat de l'effacement
     * @throws ISException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public IDAOResult deleteMulti(List<Object> aIds, ILoggedUser aUser, DAOParameter... aParameters) throws ISException;

    /**
     * Supprime les enregistrements donnés par le query vo, attention le timestamp n'est pas considéré, les enregistrements sont supprimés
     * de force.
     *
     * @param aVo
     *            contient le query de suppression
     * @param aUser
     *            utilisateur effectuant la requête
     * @param aParameters
     *            Les paramêtres
     * @return Le resultat de l'effacement
     * @throws ISException
     *             en cas de problème au niveau de la requête à la base de données
     */
    public IDAOResult deleteQuery(IValueObject aVo, ILoggedUser aUser, DAOParameter... aParameters) throws ISException;

    /**
     * Version multiple de deletQuery, les enregistrements vérifiant au moins un query sont supprimés.
     *
     * @param aVos
     *            liste des query
     * @param aUser
     *            utilisateur effectuant la requête
     * @param aParameters
     *            Les paramêtres
     * @return Le resultat de l'effacement
     * @throws ISException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public IDAOResult deleteMultiQuery(List<IValueObject> aVos, ILoggedUser aUser, DAOParameter... aParameters) throws ISException;

    /**
     * Retourne le timestamp
     *
     * @param id
     *            Identifiant
     * @param user
     *            Utilisateur
     * @return Le timestamp de l'objet
     * @throws ISException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public IDAOResult getTimestamp(Object id, ILoggedUser user) throws ISException;

    /**
     * Retourne la valeur d'un champ pour l'enregistrement donné
     *
     * @param id
     *            identifiant de l'enregistrement dont on veut le champ.
     * @param fieldName
     *            nom du champ
     * @return valeur du champ
     * @throws ISException
     *             en cas de problème au niveau base de données
     */
    public IDAOResult getField(Object id, String fieldName) throws ISException;

    // ----------------------------------------- Méthodes d'initialisation de VO
    /**
     * Méthode retournant un VO avec valeures initiales
     *
     * @param user
     *            utilisateur effectuant la requête
     * @return un value object initialisé
     * @throws ISException
     *             en cas de problèmes au niveau base de données
     */
    public IValueObject getInitVO(ILoggedUser user) throws ISException;

    /**
     * Méthode retournant un VO avec valeures initiales
     *
     * @param mode
     *            mode d'inialisation.
     *
     * @param user
     *            utilisateur effectuant la requête
     * @return un value object initialisé
     * @throws ISException
     *             en cas de problèmes au niveau base de données
     *
     */
    public IValueObject getInitVO(Mode mode, ILoggedUser user) throws ISException;

    /**
     * Traite une requête de recherche et fournit une liste d'entités sous forme d'une collection.
     *
     * @param aVo
     *            Value object contenant les critères de recherche
     * @param aUser
     *            Utilisateur
     * @param aParameters
     *            Les paramêtres
     * @return Le resultat de la recherche
     * @throws ISException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public IDAOResult getList(IValueObject aVo, ILoggedUser aUser, DAOParameter... aParameters) throws ISException;

    /**
     * Permet de modifier un champ sur chaque ligne d'une liste, la valeur à inserér pour le changement se trouve dans aLstValue, la
     * sécurité est assurée. Seule les champs passés sont modifiés, sous réserve d'actions de triggers de la base.
     *
     * @param aLstIds
     *            list des ids à modifier
     * @param aFieldName
     *            nom du champ à modifier dans la db
     * @param aLstValues
     *            valeur à modifier, insérer dans le même ordre que aLstIds
     * @param aUser
     *            paramètre de l'utilisateur
     * @return Le nombre de records impliqués par la mise à jour si positif, sinon:<br>
     *         0 : aucun record mis à jour <br>
     *         -1 : pas trouvé le record à modifier <br>
     *         -2 : le record a changé entre temps <br>
     *         -3 : rien n'a été modifié par le user, pas de update (court-circuit)<br>
     *         -4 : les droits d'accès n'autorisent pas cette modif <br>
     *         -5 : aLstId et aLstValue n'ont pas la même taille
     * @throws ISException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public IDAOResult updateField(List<Object> aLstIds, String aFieldName, List<Object> aLstValues, ILoggedUser aUser) throws ISException;

    /**
     * Obtenition de la liste des valeurs d'un champ sur la base de la requête fournie
     *
     * @param aVo
     *            restriction pour la mise à jour (uniquement les champs de la table principale)
     * @param aFieldName
     *            le nom du champ à modifier
     * @return la liste des valeurs
     * @throws ISException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public IDAOResult getFieldsRequest(IValueObject aVo, String aFieldName, DAOParameter... aParameters) throws ISException;

    /**
     * Obtenition de la liste des valeurs d'un champ sur la base de la requête fournie
     *
     * @param aVo
     *            restriction pour la mise à jour (uniquement les champs de la table principale)
     * @param aFieldName
     *            le nom du champ à modifier
     * @param aUser
     *            utilisateur
     * @return la liste des valeurs
     * @throws ISException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public IDAOResult getFieldsRequest(IValueObject aVo, String aFieldName, ILoggedUser aUser, DAOParameter... aParameters)
            throws ISException;

    /**
     * Obtention de l'aggrégation d'un champ sur la base de la requête fournie
     *
     * @param aVo
     *            restriction pour la mise à jour (uniquement les champs de la table principale)
     * @param aFieldName
     *            le nom du champ à modifier
     * @param aggr
     *            aggrégateur sql: AVG, COUNT, MAX, MIN, SUM
     * @return le resultat de l'aggrégation
     * @throws ISException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public IDAOResult getAggregateField(IValueObject aVo, String aFieldName, Aggregator aggr) throws ISException;

    /**
     * Permet de modifier un champ pour chaque enregistrement repectant la requête donnée.
     *
     * @param aVo
     *            requête
     * @param aFieldName
     *            nom du champ à modifier dans la db
     * @param aValue
     *            valeur à modifier, insérer dans le même ordre que aLstIds
     * @return nombre d'enregistrements modifiés
     * @throws ISException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public IDAOResult updateFieldRequest(IValueObject aVo, String aFieldName, Object aValue) throws ISException;

    /**
     *
     * @param aId
     *            id de l'objet
     * @param aUser
     *            utilisateur
     * @param aParameters
     *            Les paramêtres
     * @throws ISException
     *             en cas de problème au niveau de la requête à la base de données
     */
    public void downloadBlob(Object aId, ILoggedUser aUser, DAOParameter... aParameters) throws ISException;

    /**
     * Exécution d'une méthode spécialisée
     *
     * @param aNameMethode
     *            nom de la méthode
     * @param aVos
     *            list de vos object
     * @param aUser
     *            paramètres de l'utilisateur
     * @return resultat de la méthode sous forme d'une collection
     * @throws ISException
     *             si un problème survient
     */
    public Collection<?> executeMethode(String aNameMethode, List<?> aVos, ILoggedUser aUser) throws ISException;

    /**
     * Exécution d'une méthode spécialisée
     *
     * @param aNameMethode
     *            non de la méthode
     * @param anObject
     *            objet à passer en paramètre
     * @param aUser
     *            utilisateur effectuant la requête
     * @return résultat de la méthode
     * @throws ISException
     *             si un problème survient
     */
    public Object executeMethode(String aNameMethode, Object anObject, ILoggedUser aUser) throws ISException;

    /**
     * @return la clé du tri par défaut
     */
    public String getDefaultOrderKey();

    /**
     * @return liste des propriétés avec configuration DAOlist=true
     */
    public List<String> getDAOList();

    /**
     * @param aBOFactory
     *            BOFactory mis à disposition de l'objet métier lors de l'initialisation
     */
    public void setBOFactory(BOFactory aBOFactory);

    /**
     * @param aContextManager
     *            contextManager mis à disposition de l'objet métier lors de l'inialisation.
     */
    public void setContextManager(IContextManager aContextManager);

    /**
     * @param aVOFactory
     *            VOFactory mis à disposition du processus métier lors de l'initialisation
     */
    public void setVOFactory(IVOFactory aVOFactory);

    /**
     * return VOFactory mis à disposition du processus métier lors de l'initialisation
     */
    public IVOFactory getVOFactory();

    /**
     * return getBOFactory mis à disposition du processus métier lors de l'initialisation
     */
    public BOFactory getBOFactory();

    /**
     * return contextManager mis à disposition de l'objet métier lors de l'inialisation.
     */
    public IContextManager getContextManager();

    /**
     *
     * @return l'orientation du tri par défaut
     */
    public Sort getDefaultSortOrder();

    /**
     * Actualise les valeurs de plusieurs champs pour l'enregistrement donné
     *
     * @param id
     *            identifiant de l'objet
     * @param aFieldNames
     *            noms des champs à modifier
     * @param aValues
     *            valeurs à inserer
     * @return nbr de records modifiés
     * @throws ISException
     *             si un problème survient au niveau base de données
     */
    public IDAOResult updateFields(Object id, String[] aFieldNames, Object[] aValues) throws ISException;

    /**
     * Update some values from a value object.
     *
     * @param aValueObject
     *            the value object, must contain the object ID and timestamp to find and check the object to update, if a field value is
     *            <code>null</code> it's unchanged, if it's <code>Operator.IS_NULL</code> it's removed
     * @param aUser
     *            the user
     * @return the full updated <code>IValueObject</code>
     * @throws ISException
     *             is a problem was found
     */
    public IDAOResult updateFields(IValueObject aValueObject, ILoggedUser aUser) throws ISException;

    /**
     * Lit un enregistrement dans la table spécifiée correspondant à l'ID spécifié
     *
     * @param aTable
     *            nom de la table
     * @param aField
     *            nom du champ id
     * @param aValue
     *            valeur de l'id
     * @return Un value object contenant l'enregistrement demandé
     * @throws ISException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public IDAOResult getRecord(String aTable, String aField, Object aValue) throws ISException;

    /**
     * Traite une requête de recherche à la table spécifiée et fournit une liste d'entités sous forme d'une collection.
     *
     * @param aTable
     *            nom de la table
     * @param aQuery
     *            la requête
     * @return Une collection contenant les value object pour les différents records ou une collection vide (non nulle) s'il n'y a aucun
     *         résultat.
     * @throws ISException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public IDAOResult getList(String aTable, IValueObject aQuery) throws ISException;

    /**
     * Requête de count
     *
     * @param vo
     *            Value object contenant les critères de recherche
     * @param aUser
     *            utilisateur (optionnel)
     * @return nbr d'enregistrement
     * @throws ISException
     *             erreur bd
     */
    public IDAOResult getListCount(IValueObject vo, ILoggedUser aUser, DAOParameter... aParameters) throws ISException;

    /**
     *
     * @param aEngine
     *            engine pour transformer des enregistrement dans un autre format, p.ex. pdf ou csv
     */
    public void setFormatEngine(IFormatEngine aEngine);

}