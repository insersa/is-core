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

/**
 * @author INSER SA
 * @version 1.0
 * @created 29-juillet 2009 16:30:00
 */
public class GenericMail extends AbstractMail {

    /** Singleton object */
    private static GenericMail cInstance = new GenericMail();

    /**
     * Private constructeur to allow singleton mechanism
     */
    private GenericMail() {
        super();
    }

    /**
     * Method in order to get singleton object
     * 
     * @return singleton object.
     */
    public static GenericMail getInstance() {
        return cInstance;
    }
}