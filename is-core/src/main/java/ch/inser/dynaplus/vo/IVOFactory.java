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

import java.util.List;

import ch.inser.dynamic.common.IContextManager;
import ch.inser.dynamic.common.IValueObject;

/**
 * Factory de value objects
 *
 * @author INSER SA
 *
 */
public interface IVOFactory {

    /**
     * Création ou remplacement de toutes les définitions des VO contenu dans le répértoire config/vo
     *
     * @return true si tous les objets métier ont été initialisés sans erreur
     */
    public abstract boolean init();

    /**
     * Retourne un value object selon le nom
     *
     * @param name
     *            nom métier du vo
     * @return instance de value object
     */
    public abstract IValueObject getVO(String name);

    public abstract IValueObject getVO(Enum<?> name);

    /**
     * Recherche la liste des objets ayant un champs shape
     *
     * @return liste des objets métiers
     */
    public abstract List<String> getShapeObjects();

    /**
     *
     * @param aCtx
     *            context manager avec la propriété "configDir"
     * @return
     */
    public abstract void setContextManager(IContextManager aCtx);

}