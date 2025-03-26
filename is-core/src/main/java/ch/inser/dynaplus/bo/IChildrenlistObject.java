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

import java.util.List;

import ch.inser.dynamic.common.IValueObject;

/**
 * Cette classe est utilisé pour créer, modifier ou supprimer des enfants d'une vo parent sans aller par JSF. On ajoute cet objet dans le
 * propriété childrenlist du parent:<br>
 * uneListe.add(childrenlistObject); <br>
 * voParent.setProperty("childrenlistObjects", uneListe); <br>
 * bpParent.create(voParent, ...);
 * 
 * @author INSER SA
 * 
 */
public interface IChildrenlistObject {

    /**
     * @return the objectType
     */
    public String getObjectType();

    /**
     * @param anObjectType
     *            the objectType to set
     */
    public void setObjectType(String anObjectType);

    /**
     * @return the createList
     */
    public List<IValueObject> getCreateList();

    /**
     * @param aCreateList
     *            the createList to set
     */
    public void setCreateList(List<IValueObject> aCreateList);

    /**
     * @return the updateList
     */
    public List<IValueObject> getUpdateList();

    /**
     * @param anUpdateList
     *            the updateList to set
     */
    public void setUpdateList(List<IValueObject> anUpdateList);

    /**
     * @return the deleteList
     */
    public List<Object> getDeleteList();

    /**
     * @param aDeleteList
     *            the deleteList to set
     */
    public void setDeleteList(List<Object> aDeleteList);
}
