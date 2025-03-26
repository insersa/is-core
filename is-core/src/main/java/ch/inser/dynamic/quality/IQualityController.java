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

import ch.inser.dynamic.common.IContextManager;
import ch.inser.dynamic.util.Constants.EntityAction;
import ch.inser.dynamic.util.Constants.TierName;

/**
 * Controlleur pour les tests. Une implémentation de cette interface retourne la liste des tests qualité à effectuer en fonction du
 * contexte.
 *
 * Une implémentation doit être injectée dans les différents objets de couche qui l'utilisent (Rest,bo,...).
 *
 * A priori si aucun controlleur n'a été injecté, ou si la méthode retourne null, c'est le système de liste statique de tests issu de la
 * configuration qui doit être utilisé.
 *
 * @author INSER SA
 *
 */
public interface IQualityController {

    public void setContextManager(IContextManager aContextManager);

    public String[] getRules(TierName aTier, EntityAction anEntityAction, String aEntityName, QCParameter... aParameters);

}
