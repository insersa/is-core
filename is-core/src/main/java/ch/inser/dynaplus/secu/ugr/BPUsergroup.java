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

package ch.inser.dynaplus.secu.ugr;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.inser.dynamic.common.DAOParameter;
import ch.inser.dynamic.common.IDAOResult;
import ch.inser.dynamic.common.ILoggedUser;
import ch.inser.dynamic.common.IValueObject;
import ch.inser.dynamic.util.VOInfo;
import ch.inser.dynaplus.bo.AbstractBusinessProcess;
import ch.inser.dynaplus.bo.IBusinessObject;
import ch.inser.jsl.exceptions.ISException;

/**
 * Specific for Usergroup to solve the actperm problem
 *
 * @author INSER SA
 *
 */
public class BPUsergroup extends AbstractBusinessProcess {

    private static final long serialVersionUID = -3203785183803962052L;

    public BPUsergroup(VOInfo aVOInfo) {
        super("Usergroup", aVOInfo);
    }

    @Override
    public IDAOResult getRecord(Object id, ILoggedUser user, DAOParameter... aParameters) throws ISException {
        try (Connection con = getContextManager().getDataSource().getConnection()) {
            IDAOResult result = getBOFactory().getBO("Usergroup").getRecord(id, con, user, false, aParameters);
            result.getValueObject().setProperty("Actperm", getActPerm(id, user, con));
            con.commit();
            return result;
        } catch (SQLException e) {
            throw new ISException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public IDAOResult update(IValueObject valueObject, ILoggedUser user) throws ISException {
        IBusinessObject bo = getBOFactory().getBO("Usergroup");
        try (Connection con = getContextManager().getDataSource().getConnection()) {
            updateActperm(valueObject.getId(), (List<IValueObject>) valueObject.getProperty("Actperm"), user, con);
            IDAOResult result = bo.update(valueObject, con, user);
            con.commit();
            return result;
        } catch (SQLException e) {
            throw new ISException(e);
        }
    }

    private List<IValueObject> getActPerm(Object id, ILoggedUser user, Connection con) throws ISException {
        IBusinessObject boActPerm = getBOFactory().getBO("Actperm");
        IBusinessObject boMenuPerm = getBOFactory().getBO("Menuperm");

        // Get the actual Actperm
        Map<String, IValueObject> utilMap = new HashMap<>();
        // Construct based on the actual Menuperm
        IValueObject vo = getVOFactory().getVO("Menuperm");
        vo.setProperty("men_ugr_id", id);
        Collection<?> col = boMenuPerm.getList(vo, user, con).getListObject();
        for (Object obj : col) {
            vo = (IValueObject) obj;
            IValueObject voMenu = getVOFactory().getVO("Menuitem");
            voMenu.setProperty("mei_name", vo.getProperty("mei_name"));
            voMenu.setProperty("mei_id", vo.getProperty("men_mei_id"));
            voMenu.setProperty("MenuActperms", new ArrayList<>());
            utilMap.put((String) vo.getProperty("mei_name"), voMenu);
        }
        vo = getVOFactory().getVO("Actperm");
        vo.setProperty("act_ugr_id", id);
        col = boActPerm.getList(vo, user, con).getListObject();
        for (Object obj : col) {
            vo = (IValueObject) obj;
            if (utilMap.get(vo.getProperty("mei_name")) != null) {
                @SuppressWarnings("unchecked")
                List<Object> list = (List<Object>) utilMap.get(vo.getProperty("mei_name")).getProperty("MenuActperms");
                list.add(vo.getProperty("act_aci_id"));
            }
        }
        return new ArrayList<>(utilMap.values());
    }

    /**
     * To update the actitem of the list of menus
     *
     * @param voId
     *            id of the usergroup
     * @param voMenus
     *            list of Menuitem containing a list of Actitem for authorization
     * @param user
     * @param con
     * @throws ISException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    private void updateActperm(Object id, List<IValueObject> voMenus, ILoggedUser user, Connection con) throws ISException {
        try {

            List<IValueObject> newList = voMenus;
            if (newList == null) {
                return;
            }
            // Do it pro menu item
            for (IValueObject voMenu : voMenus) {
                // Get the actual actperm
                IBusinessObject boActPerm = getBOFactory().getBO("Actperm");
                IValueObject vo = getVOFactory().getVO("Actperm");
                vo.setProperty("act_ugr_id", id);
                vo.setProperty("act_mei_id", voMenu.getId());
                Collection<?> col = boActPerm.getList(vo, user, con).getListObject();
                List<Long> oldAci_ids = new ArrayList<>();
                @SuppressWarnings("unchecked")
                List<Long> newAci_ids = (List<Long>) voMenu.getProperty("MenuActperms");
                // Delete operations
                for (Object obj : col) {
                    IValueObject oldVO = (IValueObject) obj;
                    oldAci_ids.add((Long) oldVO.getProperty("act_aci_id"));
                    if (!newAci_ids.contains(oldVO.getProperty("act_aci_id"))) {
                        boActPerm.delete(oldVO.getId(), oldVO.getTimestamp(), con, user, DAOParameter.EMPTY_PARAMETER);
                    }
                }

                // Create operations
                for (Long newAci_id : newAci_ids) {
                    if (!oldAci_ids.contains(newAci_id)) {
                        vo = getVOFactory().getVO("Actperm");
                        vo.setProperty("act_ugr_id", id);
                        vo.setProperty("act_mei_id", voMenu.getId());
                        vo.setProperty("act_aci_id", newAci_id);
                        boActPerm.create(vo, con, user);
                    }
                }
            }
        } catch (SQLException e) {
            throw new ISException(e);
        }

    }
}
