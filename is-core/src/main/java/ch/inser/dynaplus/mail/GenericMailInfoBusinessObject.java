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

package ch.inser.dynaplus.mail;

import ch.inser.dynamic.util.VOInfo;

/**
 * @author INSER SA
 * @version 1.0
 * @created 09-juillet-2009 16:30:00
 */
public class GenericMailInfoBusinessObject extends AbstractMailInfoBusinessObject {

    private static final long serialVersionUID = 8734035604684907750L;

    /**
     * Constructor
     * 
     * @param info
     *            value object information
     */
    public GenericMailInfoBusinessObject(VOInfo info) {
        super("Mail", info);
    }
}
