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

package ch.inser.dynaplus.quartz;

import ch.inser.dynamic.common.IContextManager;
import ch.inser.dynamic.common.ILoggedUser;
import ch.inser.dynaplus.bo.BOFactory;
import ch.inser.dynaplus.bo.BPFactory;
import ch.inser.dynaplus.vo.IVOFactory;

/**
 * Classe pour donner les Quartz jobs accès au ContextManager, BOFactory et VOFactory. L'objet est crée au moment où on défini le job et il
 * est passé en paramètre dans le dataMap du job detail. (voir wiki) <br>
 * Implementé dans ISdynajsf.
 *
 * @author INSER SA
 *
 */
public interface IJobInjection {

    /**
     * Getter
     *
     * @return context manager
     */
    public IContextManager getContextManager();

    /**
     * @return BO factory
     */
    public BOFactory getBOFactory();

    /**
     * @return BO factory
     */
    public BPFactory getBPFactory();

    /**
     * @return VOFactory
     */
    public IVOFactory getVOFactory();

    /**
     * Le username de l'utilisateur qui a effectué le déclenchement
     */
    public String getUsername();

    public void setUsername(String aUsername);

    public ILoggedUser getUserstart();

    public void setUserstart(ILoggedUser aUser);

}
