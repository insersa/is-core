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
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DAOUtil {

    private static final Log logger = LogFactory.getLog(DAOUtil.class);

    public static List<Object> selectStatement(String sql, Connection aConnection) {
        List<Object> ret = new ArrayList<>();
        try (Statement st = aConnection.createStatement(); ResultSet rs = st.executeQuery(sql);) {
            while (rs.next()) {
                logger.debug(rs.getObject(1) + " Executing : " + sql);
                ret.add(rs.getObject(1));
            }
        } catch (Exception e) {
            logger.error("Error getting list", e);
            return null;
        }
        return ret;
    }

}
