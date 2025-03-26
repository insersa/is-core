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

package ch.inser.dynaplus.format;

import java.util.List;

import ch.inser.dynamic.common.DAOParameter;
import ch.inser.dynamic.common.IDAOResult;
import ch.inser.dynamic.common.ILoggedUser;
import ch.inser.dynamic.common.IValueObject;
import ch.inser.jsl.exceptions.ISException;

/**
 * Interface pour engine qui transforme des enregistrements en différents formats, p.ex pdf, ou csv
 *
 * @author INSER SA
 *
 */
public interface IFormatEngine {

    /**
     * Options de format
     *
     * @author INSER SA
     *
     */
    public enum Format {
        /** Format PDF */
        PDF,
        /** Format CSV */
        CSV;
    }

    /**
     * Transforme un enregistrement en format demandé
     *
     * @param aVo
     *            l'enregistrement
     * @param aParameters
     *            paramètre pour le formattage, ex. le format, la langue
     * @return objet contenant l'enregistrement dans le format demandé, p.ex. byte[] pour format pdf avec l'implémentation BirtEngine
     * @throws ISException
     *             erreur de génération de l'objet en format demandé
     */
    public IDAOResult format(IValueObject aVo, DAOParameter... aParameters) throws ISException;

    /**
     * Transforme une liste d'enregistrements en format demandé
     *
     * @param aRecords
     *            enregistrements
     * @param aUser
     *            utilisateur
     * @param aParams
     *            paramètres de formattage, ex. champs, clés des entêtes, langue
     * @return objet contenant la liste dans le format demandé
     * @throws ISException
     *             erreur de génération du format
     */
    public IDAOResult format(List<IValueObject> aRecords, ILoggedUser aUser, DAOParameter... aParams) throws ISException;
}
