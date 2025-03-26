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

package ch.inser.dynaplus.secu.use;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.inser.dynamic.common.DAOResult;
import ch.inser.dynamic.common.IDAOResult;
import ch.inser.dynamic.common.IDAOResult.Status;
import ch.inser.dynamic.common.ILoggedUser;
import ch.inser.dynamic.common.IValueObject;
import ch.inser.dynamic.util.MultiselectInfo;
import ch.inser.dynamic.util.VOInfo;
import ch.inser.dynaplus.bo.AbstractBusinessObject;
import ch.inser.jsl.exceptions.ISException;
import ch.inser.jsl.tools.SecurityTools;

/**
 * Specific BO for handling password encryption!
 */
public class BOUser extends AbstractBusinessObject {

    private static final long serialVersionUID = -7968433822205176222L;

    /** Définition de la catégorie de logging */
    private static final Log logger = LogFactory.getLog(BOUser.class);

    public BOUser(VOInfo aVOInfo) {
        super("User", aVOInfo);
    }

    @Override
    public IDAOResult update(IValueObject valueObject, Connection con, ILoggedUser user) throws SQLException {
        int rowCount = 0;

        // Lit le record qu'on est sur le point de mettre à jour
        IValueObject voOriginal = getDao().getRecord(valueObject.getId(), user, con).getValueObject();

        // Tester si le record à modifier n'a pas été trouvé
        if (voOriginal == null) {
            return new DAOResult(Status.NOT_FOUND);
        }

        // Tester si le timestamp est différent => le record a changé
        if (!voOriginal.getTimestamp().equals(valueObject.getTimestamp())) {
            logger.debug(voOriginal.getTimestamp() + "!=" + valueObject.getTimestamp());
            return new DAOResult(Status.CHANGED_TIMESTAMP);
        }

        // Comparaison avec le VO original et constitution d'une collection
        // Map des attributs à modifier.
        Map<String, Object> updateFields = voOriginal.getDiffProperties(valueObject);

        // solve the password encryption
        if (updateFields.get("use_uspass") == null) {
            updateFields.remove("use_uspass");
        } else {
            try {
                updateFields.put("use_uspass", SecurityTools.toHex(SecurityTools.encryptString((String) updateFields.get("use_uspass"))));
            } catch (Exception e) {
                logger.error("Error encrypting password", e);
            }
        }

        // Effectue la mise à jour
        rowCount = getDao().update(updateFields, valueObject.getId(), valueObject.getTimestamp(), user, con);

        // On vérifie que l'utilisateur avait bien le droit d'effectuer cet
        // update
        if (getDao().getRecord(valueObject.getId(), user, con).getValueObject() == null) {
            // On arrive pas à le relire, il n'avait pas le droit...
            return new DAOResult(Status.NO_RIGHTS);
        }
        // Add the multiselect things
        for (MultiselectInfo multiselect : iVOInfo.getMultiselects()) {
            try {
                updateMultiselected(multiselect, valueObject.getId(), (List<?>) valueObject.getProperty(multiselect.getLinkName()), user,
                        con);
            } catch (ISException e) {
                throw (SQLException) e.getCause();
            }
        }
        logger.debug("return : " + rowCount);
        return new DAOResult(rowCount);
    }

    @Override
    public IDAOResult create(IValueObject valueObject, Connection con, ILoggedUser user) throws SQLException {
        try {
            valueObject.setProperty("use_uspass",
                    SecurityTools.toHex(SecurityTools.encryptString((String) valueObject.getProperty("use_uspass"))));
        } catch (Exception e) {
            logger.error("Error encrypting password", e);
        }
        return super.create(valueObject, con, user);
    }
}
