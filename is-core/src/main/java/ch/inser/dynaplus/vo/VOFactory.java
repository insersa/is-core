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

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.inser.dynamic.common.IValueObject;
import ch.inser.dynamic.common.IValueObject.Type;
import ch.inser.dynamic.util.SchemaInfo;
import ch.inser.dynamic.util.VOInfo;
import ch.inser.dynaplus.util.AbstractFactory;
import ch.inser.dynaplus.util.Constants.Entity;

/**
 * Factory de Value Objects, implémenté comme un singleton
 *
 * @author INSER SA
 * @version 1.0
 * @created 29-févr.-2008 08:53:14
 */
public class VOFactory extends AbstractFactory implements IVOFactory, Serializable {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -1939837729800005604L;

    /**
     * Objet pour les logs.
     */
    private static final Log logger = LogFactory.getLog(VOFactory.class);

    /**
     * Instance propre du siongleton
     */
    private static IVOFactory cInstance = new VOFactory();

    /**
     * Récipient de toutes les infos paramètre des values objects servis par cette factory. Organisé en "nom métier"->"info paramètres"
     */
    private Map<String, VOInfo> iVOInfos = new HashMap<>();

    /**
     * Récipient de toutes les types de variables des values objects servis par cette factory. Organisé en "nom métier"->"Map de types"
     */
    private Map<String, Map<String, IValueObject.Type>> iTypes = new HashMap<>();

    /**
     * Récipient des noms des classes particulières des VO, si un nom métier n'est pas contenu dans cette map, il est instancié à l'aide
     * d'un GenricValueObject.
     *
     * @see GenericValueObject
     */
    private Map<String, String> iVOClassNames = new HashMap<>();

    /**
     * Constructeur privé, implémentation du singleton.
     */
    private VOFactory() {
        super("vo");
    }

    /**
     * @return l'instance du singleton
     */
    public static IVOFactory getInstance() {
        return cInstance;
    }

    /*
     * (non-Javadoc)
     *
     * @see ch.inser.dynaplus.util.AbstractFactory#initFactoryObjects(ch.inser. dynamic.util.SchemaInfo)
     */
    @Override
    protected void initFactoryObjects(SchemaInfo aSchema) {
        for (Object obj : aSchema.getVONameSet()) {
            String str = (String) obj;
            VOInfo voInfo = aSchema.getVOInfo(str);
            if (voInfo.getValue("classname") != null) {
                iVOClassNames.put(str, (String) voInfo.getValue("classname"));
                iVOInfos.remove(str);
                iTypes.remove(str);
            } else {
                iVOInfos.put(str, voInfo);
                iTypes.put(str, voInfo.getTypes());
                iVOClassNames.remove(str);
            }
            logger.debug("VO  '" + str + "' added");
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see ch.inser.dynaplus.vo.IVOFactory#getVO(java.lang.String)
     */
    @Override
    public IValueObject getVO(String name) {
        if (name.equals(Entity.anonymous.toString())) {
            return new EmptyValueObject();
        }
        VOInfo voInfo = iVOInfos.get(name);
        if (voInfo != null) {
            return new GenericValueObject(name, voInfo, iTypes.get(name));
        }
        if (iVOClassNames.get(name) != null) {
            try {
                Class<?> cl = Class.forName(iVOClassNames.get(name));
                Constructor<?> constr = cl.getConstructor((Class[]) null);
                return (IValueObject) constr.newInstance((Object[]) null);
            } catch (Exception e) {
                logger.warn("Error instantiating Class : " + name, e);
                return null;
            }
        }
        return null;
    }

    @Override
    public IValueObject getVO(Enum<?> name) {
        return getVO(name.toString());
    }

    /**
     *
     * @param aName
     *            nom de l'objet métier
     * @return la configuration de l'objet métier
     */
    public VOInfo getVOInfo(String aName) {
        return iVOInfos.get(aName);
    }

    /*
     * (non-Javadoc)
     *
     * @see ch.inser.dynaplus.vo.IVOFactory#getShapeObjects()
     */

    @Override
    public List<String> getShapeObjects() {
        // Création d'un liste
        List<String> lstObject = new ArrayList<>();

        // Recherche des objets comprenant un champ shape
        for (Map.Entry<String, VOInfo> voInfos : iVOInfos.entrySet()) {
            if (voInfos.getValue().getTypes().containsValue(Type.SHAPE)) {
                lstObject.add(voInfos.getKey());
            }

        }

        return lstObject;

    }

}