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

package ch.inser.dynaplus.auth;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import ch.inser.dynamic.common.DAOParameter;
import ch.inser.dynamic.common.DAOParameter.Name;
import ch.inser.dynamic.common.ILoggedUser;
import ch.inser.dynamic.common.IValueObject;
import ch.inser.dynaplus.bo.BOFactory;
import ch.inser.dynaplus.bo.IBusinessObject;
import ch.inser.dynaplus.util.Constants.Entity;
import ch.inser.dynaplus.util.Constants.TabItem;
import ch.inser.dynaplus.vo.IVOFactory;
import ch.inser.jsl.exceptions.ISException;
import ch.inser.jsl.list.ListHandler.Sort;

/**
 * Implémentation de UserGroup indépendent de technologie front-end
 *
 *
 * @author INSER SA
 *
 */
public class GenericUserGroup extends AbstractUserGroup {

    /**
     * UID
     */
    private static final long serialVersionUID = -7912305053397413797L;

    /**
     *
     * @param aId
     *            id du usergroup
     */
    public GenericUserGroup(Object aId) {
        super(aId);
    }

    /**
     * Initialise les droits sur menus, actions et champs en consultants les tables de droits
     *
     * Pour minimizer les requêtes à l'initialisation des LoggedUser, le UserGroup est mis en cache. La librairie pour le caching a des
     * problèmes de serialisazion si le BOFactory est injecté comme variable d'instance. Donc, le BOFactory et VOFactory sont fournis en
     * paramètres dans cette méthode.
     *
     * @param aCon
     *            connexion
     * @param aVOFactory
     *            VOFactory pour construction de requêtes sur les tables de droits
     * @param aBOFactory
     *            BOFactory pour des requêtes sur les tables de droits.
     * @param aUser
     *            Utilisateur pour initialisation d'un logged user, normalement un super user
     * @throws ISException
     *             erreur de consultation des droits dans la base de données
     */
    public void init(Connection aCon, IVOFactory aVOFactory, BOFactory aBOFactory, ILoggedUser aUser) throws ISException {
        initMenu(aCon, aUser, aVOFactory, aBOFactory);
        initAction(aCon, aUser, aVOFactory, aBOFactory);
        initFields(aCon, aVOFactory, aBOFactory);
    }

    /**
     * Initialisation de sécurité read/write sur les champs.
     *
     * * Tous les droits sont initialisé par BPLoggedUser au moment de login:
     *
     * 1. Le UserGroup a une méthode init() qui cherche les droits sur menus, actions et champs dans la base de données et les met dans des
     * map
     *
     * 2. Le UserGroup est mis en cache pour réutilisation.
     *
     * 3. Les maps de droits sont copiés du UserGroup à LoggedUser.
     *
     * 4. Selon type de front-end, une ressource de permission parcours les maps de droits du LoggedUser et les fournit au front-end
     *
     *
     * @param aCon
     *            connexion un super user
     * @param aVOF
     *            VOFactory
     * @param aBOF
     *            BOFactory
     * @throws ISException
     *             erreur d'initialisation
     */
    protected void initFields(Connection aCon, IVOFactory aVOF, BOFactory aBOF) throws ISException {

        ILoggedUser superUser = new SuperUser();
        IBusinessObject bo = aBOF.getBO(Entity.TABITEM.toString());
        if (bo == null) {
            return;
        }
        // Dataperms
        IValueObject qVo = aVOF.getVO(Entity.DATAPERM.toString());
        qVo.setProperty("dap_ugr_id", getId());
        qVo.setProperty("tab_tat_id", TabItem.FIELD.getValue());
        List<IValueObject> dataPerms = aBOF.getBO(Entity.DATAPERM.toString())
                .getList(qVo, superUser, aCon, new DAOParameter(Name.ROWNUM_MAX, 0)).getListObject();

        for (IValueObject perm : dataPerms) {
            setAuthField("fields", ((String) perm.getProperty("tab_name")).toLowerCase(),
                    Integer.valueOf(perm.getProperty("dap_type").toString()));
        }
    }

    /**
     * Iinitialisation de la sécurité sur les menus
     *
     * @param aCon
     *            connexion base de données
     *
     * @param aSuperUser
     *            Utilisateur pour initialisation d'un logged user, normalement un super user
     * @param aVOF
     *            VOFactory
     * @param aBOF
     *            BOFactory
     * @throws ISException
     *             en cas de problème au niveau de la requête à la base de données. retour de l'exception issue de la base de données
     */
    protected void initMenu(Connection aCon, ILoggedUser aSuperUser, IVOFactory aVOF, BOFactory aBOF) throws ISException {

        // Insérer la liste de tous les menus
        IValueObject voMenuItem = aVOF.getVO(Entity.MENUITEM);

        List<IValueObject> col1 = aBOF.getBO(Entity.MENUITEM).getList(voMenuItem, aSuperUser, aCon, new DAOParameter(Name.SORT_INDEX, 0),
                new DAOParameter(Name.SORT_ORIENTATION, Sort.ASCENDING), new DAOParameter(Name.ROWNUM_MAX, 0)).getListObject();
        for (IValueObject vo : col1) {
            setAuthMenu((String) vo.getProperty("mei_name"), false);
        }

        // Insérer la liste des menus autorisés
        voMenuItem = aVOF.getVO("MenuPerm");
        voMenuItem.setProperty("men_ugr_id", getId());

        List<IValueObject> col2 = aBOF.getBO("MenuPerm").getList(voMenuItem, aSuperUser, aCon, new DAOParameter(Name.SORT_INDEX, 0),
                new DAOParameter(Name.SORT_ORIENTATION, Sort.ASCENDING), new DAOParameter(Name.ROWNUM_MAX, 0)).getListObject();
        for (IValueObject vo : col2) {
            setAuthMenu((String) vo.getProperty("mei_name"), true);
        }
    }

    /**
     * Initialisation de la sécurité sur les actions
     *
     * @param aCon
     *            connexion base de données
     * @param aSuperUser
     *            Utilisateur pour initialisation d'un logged user, normalement un super user
     * @param aVOF
     *            VOFactory
     * @param aBOF
     *            BOFactory
     *
     * @throws ISException
     *             en cas de problème au niveau de la requête à la base de données. retour de l'exception issue de la base de données
     */
    protected void initAction(Connection aCon, ILoggedUser aSuperUser, IVOFactory aVOF, BOFactory aBOF) throws ISException {

        // Insérer tous les liens menu-action, les mettre à faux
        IValueObject voMenuItem = aVOF.getVO(Entity.MENUITEM);
        List<IValueObject> colMenuItem = aBOF.getBO(Entity.MENUITEM)
                .getList(voMenuItem, aSuperUser, aCon, new DAOParameter(Name.SORT_INDEX, 0),
                        new DAOParameter(Name.SORT_ORIENTATION, Sort.ASCENDING), new DAOParameter(Name.ROWNUM_MAX, 0))
                .getListObject();
        IValueObject voActItem = aVOF.getVO(Entity.ACTITEM);
        List<IValueObject> colActItem = aBOF.getBO(Entity.ACTITEM)
                .getList(voActItem, aSuperUser, aCon, new DAOParameter(Name.SORT_INDEX, 0),
                        new DAOParameter(Name.SORT_ORIENTATION, Sort.ASCENDING), new DAOParameter(Name.ROWNUM_MAX, 0))
                .getListObject();
        // Liste des liens enfant parent
        List<IValueObject> lstVoChildPerm = new ArrayList<>();

        for (IValueObject obj : colMenuItem) {
            if (obj.getProperty("mei_mei_id") != null) {
                lstVoChildPerm.add(obj);
            }

            for (IValueObject objAct : colActItem) {
                voMenuItem = obj;
                voActItem = objAct;
                setAuthAction((String) voMenuItem.getProperty("mei_name"), (String) voActItem.getProperty("aci_name"), false);
            }
        }

        // Insérer les menus-actions autorisés
        voMenuItem = aVOF.getVO(Entity.ACTPERM);
        voMenuItem.setProperty("act_ugr_id", getId());

        List<IValueObject> colActPerm = aBOF.getBO(Entity.ACTPERM)
                .getList(voMenuItem, aSuperUser, aCon, new DAOParameter(Name.SORT_INDEX, 0),
                        new DAOParameter(Name.SORT_ORIENTATION, Sort.ASCENDING), new DAOParameter(Name.ROWNUM_MAX, 0))
                .getListObject();

        for (IValueObject obj : colActPerm) {
            voMenuItem = obj;
            setAuthAction((String) voMenuItem.getProperty("mei_name"), (String) voMenuItem.getProperty("aci_name"), true);

            for (IValueObject voChild : lstVoChildPerm) {
                if (voChild.getProperty("mei_mei_id").equals(obj.getProperty("act_mei_id"))) {
                    setAuthAction((String) voChild.getProperty("mei_name"), (String) voMenuItem.getProperty("aci_name"), true);
                }
            }

        }

    }

}
