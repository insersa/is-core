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

package ch.inser.dynaplus.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton livrant les locator <br>
 * Le ContextManager est livré uniquement par le ServiceLocator de ISdynajsf. Dans la couche BO il est accessible par injection (get/set).
 * (MA 21.03.2012)
 *
 * @author INSER SA
 * @version 1.0
 * @created 29-févr.-2008 08:53:14
 */
public class ServiceLocator {

    private Map<String, ILocator> iLocators = new HashMap<>();

    private static ServiceLocator cInstance = new ServiceLocator();

    private ServiceLocator() {
        init();
    }

    public static ServiceLocator getInstance() {
        return cInstance;
    }

    public void init() {
        iLocators.put("dao", new DAOLocator());
    }

    /**
     * Retourne le service locator propre à une couche
     *
     * @param name
     */
    public ILocator getLocator(String name) {
        return iLocators.get(name);
    }

}