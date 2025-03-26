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

package ch.inser.dynamic.quality;

import java.sql.Connection;
import java.util.Locale;

import ch.inser.dynamic.common.IContextManager;
import ch.inser.dynamic.common.ILoggedUser;
import ch.inser.dynamic.common.IValueObject;
import ch.inser.dynaplus.bo.BOFactory;
import ch.inser.dynaplus.vo.IVOFactory;
import ch.inser.jsl.exceptions.ISException;

public interface IQualityTest {

    /**
     * Obtient le résultat des tests sous forme de booléan
     *
     * @param qai_ids
     * @param vo
     * @param user
     * @return
     * @throws ISException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public boolean[] check(String[] qai_ids, IValueObject vo, ILoggedUser user) throws ISException;

    public boolean[] check(String[] qai_ids, IValueObject vo, ILoggedUser user, Connection con) throws ISException;

    /**
     * Obtient les messages d'erreurs des tests échoués en fonction du local passé en paramètre, à défaut celui de l'utilisateur. Retourne
     * null si tous les tests sont réussis.
     *
     * @param qai_ids
     * @param vo
     * @param user
     * @param aLocale
     * @return
     * @throws ISException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public String[] check(String[] qai_ids, IValueObject vo, ILoggedUser user, Locale aLocale) throws ISException;

    /**
     * @param aContextManager
     *            contextManager mis à disposition de l'objet métier lors de l'inialisation.
     */
    public void setContextManager(IContextManager aContextManager);

    /**
     * @param aVOFactory
     *            VOFactory mis à disposition de l'objet métier lors de l'inialisation.
     */
    public void setVOFactory(IVOFactory aVOFactory);

    /**
     * @param aBOFactory
     *            BOFactory mis à disposition de l'objet métier lors de l'inialisation.
     */
    public void setBOFactory(BOFactory aBOFactory);

}
