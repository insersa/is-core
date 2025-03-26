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

package ch.inser.dynamic.common;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.inser.dynamic.util.VOInfo;
import ch.inser.jsl.exceptions.ISRuntimeException;
import ch.inser.jsl.tools.PropertyTools;

/**
 * Classe de transport des données des objets métier.
 *
 * @author INSER SA
 * @version 1.1
 */
public abstract class AbstractDynamicVO implements IValueObject {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -8743498768857671120L;

    /**
     * Objet pour les logs.
     */
    private static final Log logger = LogFactory.getLog(AbstractDynamicVO.class);

    // ----------------------------------------------------- Variable d'instance

    /**
     * La mappe pour conserver les attributs avec le type.
     */
    private Map<String, Object> iMap;

    /**
     * La mappe pour conserver des requêtes avec operatuer OR, type: "statut=accepté or date_fin=null or date_fin < sysdate"
     */
    private List<Map<String, Object>> iOrList;

    // ----------------------------------------------------------- Constructeurs

    /**
     * Constructeur, initialise la liste des caractère avec une certaine taille et un facteur de charge égal à 1.
     *
     * @param aInitialCapacity
     *            La taille initiale de la mappe pour conserver les propriétées.
     */
    protected AbstractDynamicVO(int aInitialCapacity) {

        iMap = new HashMap<>(aInitialCapacity, 1);
        iOrList = new ArrayList<>();
    }

    /**
     * Constructeur, initialise la liste des caractère avec la taille des types de propriétées et un facteur de charge de 1.
     */
    protected AbstractDynamicVO() {
        iMap = new HashMap<>();
        iOrList = new ArrayList<>();
    }

    // ----------------------------------------------------- Méthodes abstraites

    /**
     * Retourne une copie profonde (deep copy) de cet objet.
     *
     * @return Un objet VO représentant un copie profonde de cet objet.
     */
    @Override
    public abstract Object clone();

    // -------------------------------------------------------- Méthodes membres

    /**
     * Retourne la valeur d'une propriété.
     *
     * @param aName
     *            Le nom de la proriété demandée.
     * @return La valeur de la propriété demandée.
     */
    @Override
    public Object getProperty(String aName) {

        return iMap.get(aName);
    }

    /**
     * Modifie la valeur d'une propriété.
     *
     * @param aName
     *            Le nom de la proriété à modifier.
     * @param aValue
     *            La valeur de la proriété à modifier.
     */
    @Override
    public void setProperty(String aName, Object aValue) {
        if (aValue != null) {
            iMap.put(aName, aValue);
        } else {
            iMap.remove(aName);
        }
    }

    /**
     * Enlève une propriété.
     *
     * @param aName
     *            La propriété à supprimer.
     * @return La valeur de la propri supprimée.
     */
    @Override
    public Object removeProperty(String aName) {

        return iMap.remove(aName);
    }

    @Override
    public void removeProperties(Set<String> aNames) {
        for (String name : aNames) {
            removeProperty(name);
        }
    }

    @Override
    public void filterProperties(Set<String> aNames) {
        if (aNames == null) {
            return;
        }
        Set<String> removeList = new HashSet<>();
        for (String key : iMap.keySet()) {
            if (!aNames.contains(key)) {
                removeList.add(key);
            }
        }
        removeProperties(removeList);
    }

    /**
     * Retourne le type d'une propriété.
     *
     * @param aName
     *            Le nom de la proprieté.
     * @return Le type de la proprieté demandée.
     */
    @Override
    public Type getPropertyType(String aName) {

        if (getTypes() == null) {
            return null;
        }
        return getTypes().get(aName);
    }

    /**
     * Retourne une copie profonde (deep copy) de cet objet.
     *
     * @param aVo
     *            Le VO a utiliser our la clonation.
     * @return Un objet VO représentant un copie profonde de cet objet.
     */
    protected IValueObject clone(IValueObject aVo) {

        Object obj;

        aVo.clear();
        for (Map.Entry<String, Object> me : iMap.entrySet()) {
            obj = me.getValue();

            if (obj instanceof String) {
                obj = PropertyTools.getObjectCopy((String) obj);
            } else if (obj instanceof Integer) {
                obj = PropertyTools.getObjectCopy((Integer) obj);
            } else if (obj instanceof Long) {
                obj = PropertyTools.getObjectCopy((Long) obj);
            } else if (obj instanceof java.sql.Date) {
                obj = PropertyTools.getObjectCopy((java.sql.Date) obj);
            } else if (obj instanceof Timestamp) {
                obj = PropertyTools.getObjectCopy((Timestamp) obj);
            } else if (obj instanceof Double) {
                obj = PropertyTools.getObjectCopy((Double) obj);
            } else if (obj instanceof Boolean) {
                obj = PropertyTools.getObjectCopy((Boolean) obj);
            } else if (!(obj instanceof Map)// it is an entry for a complex
                    // request
                    && !(obj instanceof List)// it is an entry for a
                    // multiselect!
                    && !(obj instanceof Object[] && me.getKey().endsWith("_list")) // it is an entry
                                                                                   // for a
                                                                                   // multiselect
                    && !(obj instanceof IValueObject)) // it is a parent entry!
            {
                // Vérification si valeur null, possibilité de problème si
                // utilisation direct de la map
                if (obj != null) {
                    logger.debug("The class '" + obj.getClass().getName() + "' is not handled by the deep copy operation.");
                } else {
                    logger.debug("The value of key " + me.getKey() + " is null");
                }

            }

            aVo.setProperty(me.getKey(), obj);
        }

        return aVo;
    }

    /**
     * Retourne la mappe des propriétés pour des traitements dans des autres modules.
     *
     * @return La mappe des propriétés VO.
     */
    @Override
    public Map<String, Object> getProperties() {

        return iMap;
    }

    /**
     * Compare toutes les propriétés de cet objet avec celles du value object passé en paramètre et fournit une collection Map des
     * propriétés différentes avec le nom de la propriété comme "key" et sa nouvelle valeur comme "value".
     * <p>
     * On ne compare pas le timestamp, ni les libellé de codes.
     *
     * @param aVo
     *            Value object faisant l'objet de la comparaison.
     * @return Une collection Map des différences trouvées (peut être vide).
     */
    @Override
    public Map<String, Object> getDiffProperties(IValueObject aVo) {

        Map<String, Object> diffs = new HashMap<>();
        Set<String> omit = getOmit();

        for (String attribute : aVo.getTypes().keySet()) {
            // Attributs à ne pas comparer
            if (omit != null && omit.contains(attribute)) {
                continue;
            }
            // Comparaison
            if (PropertyTools.arePropertiesNotEqual(getProperty(attribute), aVo.getProperty(attribute))) {
                diffs.put(attribute, aVo.getProperty(attribute));
            }
        }
        return diffs;
    }

    /**
     * Efface tous les attributs.
     */
    @Override
    public void clear() {

        iMap.clear();
    }

    /**
     * Retourne un String représentant l'objet et son contenu.
     *
     * @return La representation textuelle de l'objet.
     */
    @Override
    public String toString() {
        String ls = System.getProperty("line.separator");

        return toString("").insert(0, ls).toString();
    }

    /**
     * Retourne un String représentant l'objet et son contenu.
     *
     * @param aIdent
     *            nom du champ
     *
     * @return La representation textuelle de l'objet.
     */
    public StringBuilder toString(String aIdent) {
        String ls = System.getProperty("line.separator");

        StringBuilder sb = new StringBuilder(128);
        String ident = aIdent + "  ";

        sb.append(getClass().getName());
        sb.append("{");
        Iterator<Map.Entry<String, Object>> it = new TreeMap<>(iMap).entrySet().iterator();
        Map.Entry<String, Object> entry;
        while (it.hasNext()) {
            sb.append(ls);
            entry = it.next();
            sb.append(ident);
            sb.append(entry.getKey());
            sb.append("=");
            if (entry.getValue() instanceof IValueObject) {
                sb.append(((AbstractDynamicVO) entry.getValue()).toString(ident));
            } else if (entry.getValue() instanceof List) {
                for (Object object : (List<?>) entry.getValue()) {
                    if (object instanceof IValueObject) {
                        sb.append(((AbstractDynamicVO) object).toString(ident));
                    } else {
                        sb.append(object);
                    }
                }
            } else {
                sb.append(entry.getValue());
            }
            if (!it.hasNext()) {
                sb.append(ls);
            }
        }
        if (iMap.size() > 0) {
            sb.append(aIdent);
        }
        sb.append("}");
        return sb;
    }

    /**
     * Retourne un String construit de la même sorte qu'avec toString(), mais qui contient seulement le propriétées non nulles.
     *
     * @return Une representation textuelle de l'objet, contenant uniquement les attributs non-vides.
     */
    @Override
    public String getNotNullString() {

        StringBuffer sb = new StringBuffer(128);
        sb.append(getClass().getName());
        sb.append("{");

        boolean first = true;
        for (Map.Entry<String, Object> me : iMap.entrySet()) {
            if (me.getValue() == null) {
                continue;
            }

            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append(me.getKey());
            sb.append("=");
            sb.append(me.getValue());
        }

        sb.append("}");

        return sb.toString();
    }

    /**
     * Vérifie si l'objet est vide, tous les attributs sont null.
     *
     * @return true si l'objet n'a aucun attribut défini.
     */
    @Override
    public boolean isEmpty() {

        if (iMap.isEmpty()) {
            return true;
        }
        for (Object obj : iMap.values()) {
            if (obj instanceof IValueObject) {
                if (!((IValueObject) obj).isEmpty()) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * Vérifie si l'objet passé en paramètre est égale à l'objet courant. Vérifie si les objets sont de la même classe, dans ce cas il teste
     * tous les attributs définis par les types.
     *
     * @param aObject
     *            L'objet à comparer
     * @return Le resultat du test.
     */
    @Override
    public boolean equals(Object aObject) {

        if (aObject == null || !this.getClass().equals(aObject.getClass())) {
            return false;
        }
        IValueObject object = (IValueObject) aObject;
        for (String property : getTypes().keySet()) {
            if (getProperty(property) == null) {
                if (object.getProperty(property) != null) {
                    return false;
                }
            } else if (object.getProperty(property) == null) {
                return false;
            } else if (!getProperty(property).equals(object.getProperty(property))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int i = 0;
        if (getTypes() == null || getTypes().keySet() == null) {
            return 0;
        }
        for (String property : getTypes().keySet()) {
            if (getProperty(property) != null) {
                i = i + getProperty(property).hashCode();
            }
        }
        return i;
    }

    @Override
    public double doubleValue(String aName) {

        Object result = getProperty(aName);
        if (result != null) {
            return ((Double) result).doubleValue();
        }
        throw new ISRuntimeException("Null not allowed:The property: '" + aName + "' is null, no double value.");
    }

    @Override
    public double doubleValue(String aName, double aDefault) {
        if (aName == null) {
            return aDefault;
        }
        return doubleValue(aName);
    }

    @Override
    public VOInfo getVOInfo() {
        return null;
    }

    @Override
    public Object getId() {
        if (getVOInfo() == null || getVOInfo().getId() == null) {
            return null;
        }
        return getProperty(getVOInfo().getId());
    }

    @Override
    public void setId(Object anId) {
        if (getVOInfo() != null && getVOInfo().getId() != null) {
            setProperty(getVOInfo().getId(), anId);
        }
    }

    @Override
    public Timestamp getTimestamp() {
        if (getVOInfo() != null && getVOInfo().getTimestamp() != null) {

            if (getVOInfo().getInfo(getVOInfo().getTimestamp(), "type").equals("java.lang.Long")) {
                return new java.sql.Timestamp((Long) getProperty(getVOInfo().getTimestamp()));
            }
            return (Timestamp) getProperty(getVOInfo().getTimestamp());
        }
        return null;
    }

    @Override
    public Object getParent() {
        return null;
    }

    @Override
    public boolean isParentInitialised() {
        return false;
    }

    @Override
    public IValueObject getOldValues() {
        return null;
    }

    @Override
    public boolean isOldInitialised() {
        return false;
    }

    /**
     * @param aType
     *            nom de javatype de la propriété
     * @return type de la propriété
     * @since 1.1
     */
    public static IValueObject.Type type(String aType) {

        if (aType.equals(IValueObject.JavaType.STRING.getValue())) {
            return IValueObject.Type.STRING;
        } else if (aType.equals(IValueObject.JavaType.LONG.getValue())) {
            return IValueObject.Type.LONG;
        } else if (aType.equals(IValueObject.JavaType.INTEGER.getValue())) {
            return IValueObject.Type.INTEGER;
        } else if (aType.equals(IValueObject.JavaType.DATE.getValue())) {
            return IValueObject.Type.DATE;
        } else if (aType.equals(IValueObject.JavaType.TIMESTAMP.getValue())) {
            return IValueObject.Type.TIMESTAMP;
        } else if (aType.equals(IValueObject.JavaType.TIME.getValue())) {
            return IValueObject.Type.TIME;
        } else if (aType.equals(IValueObject.JavaType.BYTES.getValue())) {
            return IValueObject.Type.BYTES;
        } else if (aType.equals(IValueObject.JavaType.BOOLEAN.getValue())) {
            return IValueObject.Type.BOOLEAN;
        } else if (aType.equals(IValueObject.JavaType.DOUBLE.getValue())) {
            return IValueObject.Type.DOUBLE;
        } else if (aType.equals(IValueObject.JavaType.LIST.getValue())) {
            return IValueObject.Type.LIST;
        } else if (aType.equals(IValueObject.JavaType.DOCUMENT.getValue())) {
            return IValueObject.Type.DOCUMENT;
        } else if (aType.equals(IValueObject.JavaType.MAP.getValue())) {
            return IValueObject.Type.MAP;
        } else if (aType.equals(IValueObject.JavaType.BLOB.getValue())) {
            return IValueObject.Type.BLOB;
        } else if (aType.equals(IValueObject.JavaType.CLOB.getValue())) {
            return IValueObject.Type.CLOB;
        } else if (aType.equals(IValueObject.JavaType.SHAPE.getValue())) {
            return IValueObject.Type.SHAPE;
        } else if (aType.equals(IValueObject.JavaType.UUID.getValue())) {
            return IValueObject.Type.UUID;
        } else if (aType.equals(IValueObject.JavaType.JSON.getValue())) {
            return IValueObject.Type.UUID;
        }

        return null;
    }

    /**
     * @param aType
     *            type
     * @return java type
     * @since 1.1
     */
    public static IValueObject.JavaType type(IValueObject.Type aType) {

        switch (aType) {
            case STRING:
                return IValueObject.JavaType.STRING;
            case LONG:
                return IValueObject.JavaType.LONG;
            case INTEGER:
                return IValueObject.JavaType.INTEGER;
            case DATE:
                return IValueObject.JavaType.DATE;
            case TIMESTAMP:
                return IValueObject.JavaType.TIMESTAMP;
            case TIME:
                return IValueObject.JavaType.TIME;
            case BYTES:
                return IValueObject.JavaType.BYTES;
            case BOOLEAN:
                return IValueObject.JavaType.BOOLEAN;
            case DOUBLE:
                return IValueObject.JavaType.DOUBLE;
            case LIST:
                return IValueObject.JavaType.LIST;
            case DOCUMENT:
                return IValueObject.JavaType.DOCUMENT;
            case MAP:
                return IValueObject.JavaType.MAP;
            case BLOB:
                return IValueObject.JavaType.BLOB;
            case CLOB:
                return IValueObject.JavaType.CLOB;
            case SHAPE:
                return IValueObject.JavaType.SHAPE;
            default:
                return null;
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see ch.inser.dynamic.common.IValueObject#getStringProperty(java.lang.String)
     */
    @Override
    public String getStringProperty(String aName) {

        return PropertyTools.getVOString(getProperty(aName));
    }

    /**
     * Ecrase les propriétés du VO avec les propriétés de la map (laisse les autres)
     *
     * @param map
     *            map des propriétés à effacer
     */
    @Override
    public void putProperties(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return;
        }
        Iterator<?> it = map.keySet().iterator();
        String key;
        while (it.hasNext()) {
            key = (String) it.next();
            setProperty(key, map.get(key));
        }
    }

    @Override
    public void putProperties(IValueObject vo) {
        if (vo == null) {
            return;
        }
        putProperties(vo.getProperties());
    }

    @Override
    public Object getSecu() {
        if (getVOInfo() != null && getVOInfo().getValue("secu") != null) {
            return getProperty((String) getVOInfo().getValue("secu"));
        }
        return null;
    }

    @Override
    public void setSecu(Object value) {
        if (getVOInfo() != null && getVOInfo().getValue("secu") != null) {
            setProperty((String) getVOInfo().getValue("secu"), value);
        }
    }

    @Override
    public Object getModifyUser() {
        if (getVOInfo() != null && getVOInfo().getValue("modifyuser") != null) {
            return getProperty((String) getVOInfo().getValue("modifyuser"));
        }
        return null;
    }

    @Override
    public void setModifyUser(Object value) {
        if (getVOInfo() != null && getVOInfo().getValue("modifyuser") != null) {
            setProperty((String) getVOInfo().getValue("modifyuser"), value);
        }
    }

    @Override
    public void setTimestamp(Object value) {
        if (getVOInfo() != null && getVOInfo().getTimestamp() != null) {
            setProperty(getVOInfo().getTimestamp(), value);
        }
    }

    @Override
    public Class<?> getPropertyClass(String aName) {
        try {
            Type aType = getPropertyType(aName);
            if (aType == null) {
                return null;
            }

            JavaType value = type(aType);
            if (value == null) {
                return null;
            }
            if (aType == Type.CLOB || aType == Type.SHAPE) {
                return String.class;
            }

            return Class.forName(value.getValue());
        } catch (ClassNotFoundException e) {
            logger.warn("Class not found with error", e);
            return null;
        }
    }

    /**
     * @return Retourne la liste représentante une requête avec l'operatuer OR
     */
    @Override
    public List<Map<String, Object>> getOrList() {

        return iOrList;
    }

    /**
     * Modifie la liste qui représente une requête OR
     *
     * @param aOrList
     *            la liste à mettre comme la nouvelle requête OR
     */
    @Override
    public void setOrList(List<Map<String, Object>> aOrList) {
        iOrList = aOrList;
    }

    /**
     * Vide la liste de la requête OR
     */
    @Override
    public void clearOrList() {
        iOrList.clear();
    }

}