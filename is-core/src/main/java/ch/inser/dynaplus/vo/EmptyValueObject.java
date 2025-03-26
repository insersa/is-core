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

package ch.inser.dynaplus.vo;

import java.util.Set;

/**
 * ValueObject construit de toute pièce, sans fichier de configuration!
 *
 * @author INSER SA
 * @version 1.0
 * @created 29-févr.-2008 08:53:12
 */
public class EmptyValueObject extends AbstractValueObject {

    /**
     * Serial UID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Retourne la liste des attributs à omettre des tests des différences.
     */
    @Override
    public Set<String> getOmit() {
        return null;
    }

    /**
     * Retourne une copie profonde (deep copy) de cet objet.
     */
    @Override
    public Object clone() {
        EmptyValueObject vo = new EmptyValueObject();
        return clone(vo);
    }
}