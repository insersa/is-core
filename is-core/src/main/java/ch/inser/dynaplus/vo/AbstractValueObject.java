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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.inser.dynamic.common.AbstractDynamicVO;
import ch.inser.dynamic.common.IValueObject;
import ch.inser.dynamic.util.VOInfo;

/**
 * @author INSER SA
 * @version 1.0
 * @created 29-févr.-2008 08:53:10
 */
public abstract class AbstractValueObject extends AbstractDynamicVO {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -5236685061234650279L;

    /**
     * Objet pour les logs.
     */
    @SuppressWarnings("unused")
    private static final Log logger = LogFactory.getLog(AbstractValueObject.class);
    /**
     * Liste des attributs et de leur type.
     */
    private Map<String, IValueObject.Type> iTypes = null;

    /**
     * Mappe avec les informations supplémentaires.
     */
    private VOInfo iInfo;

    /**
     * Nom du ValueObject
     */
    private String iName;

    /**
     * Constructeur par défaut
     */
    protected AbstractValueObject() {
        // Constructeur unique pour une classe implémentée de toute pièce...
        super(0);
    }

    /**
     * Modifie la valeur d'une propriété. Pour les String: nettoie le string selon une expression régulière, p.ex. en remplacant les
     * caractères spéciaux (\t,\n, \r) par un espace.
     *
     * @param aName
     *            Le nom de la proriété à modifier.
     * @param aValue
     *            La valeur de la proriété à modifier.
     */
    @Override
    public void setProperty(String aName, Object aValue) {
        Object value = aValue;

        if (value != null && iInfo != null && value instanceof String && iInfo.getAttribute(aName) != null) {
            String expr = (String) iInfo.getAttribute(aName).getInfo("cleanexpr");
            if (expr != null) {
                String replacement = (String) iInfo.getAttribute(aName).getInfo("cleanreplacement");
                value = ((String) value).replaceAll(expr, replacement == null ? "" : replacement);
            }
            if ("true".equals(iInfo.getAttribute(aName).getInfo("cleantrim"))) {
                value = ((String) value).trim();
            }

        }
        super.setProperty(aName, value);
    }

    /**
     * Constructeur complet permettant d'indiquer le nom, les types utilisé et les infos issus de la configuration
     *
     * @param name
     *            nom métier du value object
     * @param aInfo
     *            informations issues de la configuration
     * @param types
     *            typage des variables
     */
    protected AbstractValueObject(String name, VOInfo aInfo, Map<String, IValueObject.Type> types) {
        super(0);
        iName = name;
        iInfo = aInfo;
        iTypes = types;
    }

    /**
     * Retourne la liste des attributs et de leur type.
     */
    @Override
    public Map<String, IValueObject.Type> getTypes() {

        return iTypes;
    }

    @Override
    public VOInfo getVOInfo() {
        return iInfo;
    }

    @Override
    public String getName() {
        return iName;
    }

    @Override
    public boolean isEmpty() {
        if (getProperties().isEmpty()) {
            return true;
        }
        for (String key : getProperties().keySet()) {
            Object obj = getProperty(key);
            if (obj instanceof IValueObject) {
                if (!((IValueObject) obj).isEmpty()) {
                    return false;
                }
            } else if ("childrenmap".equals(key)) {
                @SuppressWarnings("unchecked")
                Map<String, IValueObject> map = (Map<String, IValueObject>) getProperty(key);
                for (Object obj2 : map.values()) {
                    if (!((IValueObject) obj2).isEmpty()) {
                        return false;
                    }
                }
            } else {
                return false;
            }
        }
        return true;
    }
}