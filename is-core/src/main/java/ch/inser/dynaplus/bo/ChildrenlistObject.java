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
public class ChildrenlistObject implements IChildrenlistObject {

    /** Children object type */
    private String iObjectType;

    /**
     * La liste des vo (enfants) à créer dans le vo parent
     */
    private List<IValueObject> iCreateList;

    /**
     * La liste des vo (enfants) à modifier dans le vo parent
     */
    private List<IValueObject> iUpdateList;

    /**
     * La liste des id d'enfants à supprimer dans le vo parent
     */
    private List<Object> iDeleteList;

    /**
     * @return the objectType
     */
    @Override
    public String getObjectType() {
        return iObjectType;
    }

    /**
     * @param anObjectType
     *            the objectType to set
     */
    @Override
    public void setObjectType(String anObjectType) {
        iObjectType = anObjectType;
    }

    /**
     * @return the createList
     */
    @Override
    public List<IValueObject> getCreateList() {
        return iCreateList;
    }

    /**
     * @param aCreateList
     *            the createList to set
     */
    @Override
    public void setCreateList(List<IValueObject> aCreateList) {
        iCreateList = aCreateList;
    }

    /**
     * @return the updateList
     */
    @Override
    public List<IValueObject> getUpdateList() {
        return iUpdateList;
    }

    /**
     * @param anUpdateList
     *            the updateList to set
     */
    @Override
    public void setUpdateList(List<IValueObject> anUpdateList) {
        iUpdateList = anUpdateList;
    }

    /**
     * @return the deleteList
     */
    @Override
    public List<Object> getDeleteList() {
        return iDeleteList;
    }

    /**
     * @param aDeleteList
     *            the deleteList to set
     */
    @Override
    public void setDeleteList(List<Object> aDeleteList) {
        iDeleteList = aDeleteList;
    }

}
