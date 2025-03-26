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

package ch.inser.dynamic.list;

import java.util.Collection;

import ch.inser.dynamic.common.IValueObject;

/**
 * Interface de factory des collections de résultats pour les getList. A priori il est préférable d'avoir des List pour des raisons de
 * compatibilités avec la librairie JSF.
 */
public interface IResultContainerFactory {

    public Collection<IValueObject> createResultContainer();

}
