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

import ch.inser.dynamic.util.VOInfo;

/**
 * @author INSER SA
 * @version 1.0
 * @created 29-févr.-2008 08:53:11
 */
public class GenericBusinessObject extends AbstractBusinessObject {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 8383787529445011225L;

    /**
     * Constucteur par défaut
     *
     * @param name
     *            nom du business object
     * @param aVOInfo
     *            paramètre applicatif de ce business object
     */
    public GenericBusinessObject(String name, VOInfo aVOInfo) {
        super(name, aVOInfo);
    }
}