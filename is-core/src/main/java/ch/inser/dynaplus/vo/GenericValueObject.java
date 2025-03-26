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

import java.util.Map;
import java.util.Set;

import ch.inser.dynamic.common.IValueObject;
import ch.inser.dynamic.util.VOInfo;

/**
 * Implémentation par défaut du AbstractValueObject
 *
 * @author INSER SA
 * @version 1.0
 * @created 29-févr.-2008 08:53:12
 */
public class GenericValueObject extends AbstractValueObject {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 1446029925998165842L;

    /**
     * Unique constructeur, assurant ainsi la consistance de l'objet
     *
     * @param name
     *            nom métier du value object
     * @param aInfo
     *            informations issues de la configuration
     * @param types
     *            typage des variables
     */
    public GenericValueObject(String name, VOInfo aInfo, Map<String, IValueObject.Type> types) {
        super(name, aInfo, types);
    }

    // ------------------------------------------------------- Méthodes membres

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
        GenericValueObject vo = new GenericValueObject(getName(), getVOInfo(), getTypes());
        return clone(vo);
    }
}