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

package ch.inser.dynamic.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.jdom2.Element;
import org.jdom2.Namespace;

import ch.inser.dynamic.common.AbstractDynamicVO;
import ch.inser.dynamic.common.DynamicDAO.AttributeType;
import ch.inser.dynamic.common.IValueObject;
import ch.inser.jsl.list.ListHandler.Sort;

/**
 * Class pour la gestion des informations concernant un ValueObject contenu dans le fichier XML-Schema "ValueObject.xsd".
 *
 * @author INSER SA
 * @version 1.0
 */
public class VOInfo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public static final Namespace XS = Namespace.getNamespace("xs", "http://www.w3.org/2001/XMLSchema");

    /**
     * Clé du nom de la table qui contient ce type de ValueObject.
     */
    private static final String TABLE = "table";

    /**
     * Clé du nom de l'identifiant des ValueObjects de ce type.
     */
    private static final String ID = "id";

    /**
     * Clé du nom du Timestamp des ValueObjects de ce type.
     */
    private static final String TIMESTAMP = "timestamp";

    /**
     * Clé pour l'alignement du label et de l'input text en vertical
     */
    private static final String VERTICAL_ALIGNMENT = "verticalAlignement";

    /**
     * Clé pour utiliser la page detail pour faire des recherches
     */
    private static final String USE_DETAIL_PAGE_FOR_SEARCHES = "useDetailPageForSearches";

    /**
     * Clé pour afficher les enfants sur une page de recherche
     */
    private static final String DISPLAY_CHILDREN_IN_SEARCH_PAGE = "displayChildrenInSearchPage";

    /**
     * Clé du nom du champ contenant l'identifiant de l'utilisateur qui a effectué la dernière modification des ValueObjects de ce type.
     */
    private static final String UPDATEUSER = "updateuser";

    /**
     * Clé des jointures utilisées pour trouver l'ensemble des attributs de ce type de ValueObject.
     */
    private static final String JOINS = "joins";

    /**
     * Clé des attributs de ce type de valueObject.
     */
    private static final String ATTRIBUTES = "attributes";

    /**
     * Clé des critères de tri.
     */
    private static final String SORTS = "sorts";

    /**
     * Clé des critères de children.
     */
    private static final String CHILDREN = "children";

    /**
     * Configuration par défaut des simpletogglepanel
     */
    private static final String SIMPLETOGGLEPANEL = "simpletogglepanel";

    /**
     * Clé des critères de parent.
     */
    private static final String PARENT = "parent";

    /**
     * Clé des critères de multiselect.
     */
    private static final String MULTISELECT = "multiselect";

    /**
     * Clé des critères de delcascade.
     */
    private static final String DELCASCADE = "delcascade";

    /**
     * Clé des critères de shell.
     */
    private static final String SHELL = "shell";

    /**
     * Clé de la propriété REST Resource true/false
     */
    public static final String REST_RESOURCE = "restresource";

    /**
     * Clé de la propriété REST Object name resource class name
     */
    public static final String REST_ONR_CLASSNAME = "onrclassname";

    /**
     * Clé de la propriété REST Object resource class name
     */
    public static final String REST_OR_CLASSNAME = "orclassname";

    /**
     * Nom complet (package.class) de ce type (classe) de ValueObject.
     */
    private String iName;

    /**
     * Information sur ce ValueObject
     */
    private Map<String, Object> iInfo;

    /**
     * @param aElement
     *            xml element
     */
    public VOInfo(Element aElement) {

        iName = aElement.getAttributeValue("name");
        iInfo = new HashMap<>();
        iInfo.put(ATTRIBUTES, new HashMap<>());
        iInfo.put(JOINS, new HashMap<>());
        iInfo.put(SORTS, new ArrayList<>());
        iInfo.put(CHILDREN, new ArrayList<>());
        iInfo.put(MULTISELECT, new ArrayList<>());
        iInfo.put(PARENT, new ArrayList<>());
        iInfo.put(DELCASCADE, new ArrayList<>());
        iInfo.put(SHELL, new ArrayList<>());
        iInfo.put(SIMPLETOGGLEPANEL, new ArrayList<>());

        // Lecture des informations générales du VO
        Element element = aElement.getChild("annotation", XS);
        if (element != null) {
            readAppInfo(element.getChild("appinfo", XS));
        }

        // Lecture des informations spécifiques à chaque attribut.
        element = aElement.getChild("complexType", XS);
        if (element != null) {
            Iterator<?> it = element.getChild("sequence", XS).getChildren().iterator();
            int idx = 0;
            while (it.hasNext()) {
                // Map pour les informations concernant l'attribut
                put(new AttributeInfo(iName, idx++, (Element) it.next()));
            }
        }

        // Traitement des différents valeurs par défaut
        AttributeInfo id = getAttribute(getId());
        if (id != null) {
            if (id.getValue(AttributeInfo.LIST) == null) {
                id.setValue(AttributeInfo.LIST, AttributeInfo.TRUE);
            }
            id.setValue(AttributeInfo.SEARCH, AttributeType.EQUAL.toString());
        }
    }

    private AttributeInfo put(AttributeInfo aAttributeInfo) {

        return getAttributes().put(aAttributeInfo.getName(), aAttributeInfo);
    }

    private void readAppInfo(Element aElement) {

        Iterator<?> it = aElement.getChildren().iterator();
        Element element;
        while (it.hasNext()) {
            element = (Element) it.next();
            if ("join".equals(element.getName())) {
                put(new JoinInfo(iName, element));
            } else if ("sort".equals(element.getName())) {
                put(new SortInfo(iName, element));
            } else if ("children".equals(element.getName())) {
                put(new ChildrenInfo(iName, element));
            } else if ("multiselect".equals(element.getName())) {
                put(new MultiselectInfo(iName, element));
            } else if ("parent".equals(element.getName())) {
                put(new ParentInfo(iName, element));
            } else if ("delcascade".equals(element.getName())) {
                put(new DelCascadeInfo(iName, element));
            } else if ("shell".equals(element.getName())) {
                put(new ShellInfo(iName, element));
            } else if ("simpletogglepanel".equals(element.getName())) {
                put(new SimpleTogglePanelInfo(iName, element));
            } else {
                iInfo.put(element.getName(), element.getText());
            }
        }
    }

    private JoinInfo put(JoinInfo aJoinInfo) {

        return getJoins().put(aJoinInfo.getTable(), aJoinInfo);
    }

    private boolean put(SortInfo aSortInfo) {

        return getSorts().add(aSortInfo);
    }

    private boolean put(ChildrenInfo aChildrenInfo) {

        return getChildrens().add(aChildrenInfo);
    }

    private boolean put(MultiselectInfo aMultiselectInfo) {
        return getMultiselects().add(aMultiselectInfo);
    }

    private boolean put(ParentInfo aParentInfo) {
        return getParents().add(aParentInfo);
    }

    private boolean put(DelCascadeInfo aDelCascadeInfo) {
        return getDelCascade().add(aDelCascadeInfo);
    }

    private boolean put(ShellInfo aShellInfo) {
        return getShellInfo().add(aShellInfo);
    }

    private boolean put(SimpleTogglePanelInfo aSimpleTogglePanelInfo) {
        return getSimpleTogglePanelInfo().add(aSimpleTogglePanelInfo);
    }

    /**
     * Retourne une information concernante le ValueObject.
     *
     * @param aName
     *            Le nom de l'information recherchée.
     * @return L'information recherchée concernante le ValueObject.
     */
    public Object getValue(String aName) {

        return iInfo.get(aName);
    }

    /**
     * Retourne l'information sur un attribut du ValueObject.
     *
     * @param aAttributeName
     *            Le nom de l'attribut.
     * @return L'information sur l'attribut.
     */
    public AttributeInfo getAttribute(String aAttributeName) {

        return getAttributes().get(aAttributeName);
    }

    /**
     * Retourne la mappe des attributs du ValueObject.
     *
     * @return La mappe des attributs du ValueObject.
     */
    @SuppressWarnings("unchecked")
    public Map<String, AttributeInfo> getAttributes() {

        return (Map<String, AttributeInfo>) iInfo.get(ATTRIBUTES);
    }

    /**
     * Retourne une mappe ou la clé de chaque enregistrement est le nom de l'attribut et la valeur est la valeur d'une information sur cet
     * attribut.
     *
     * @param aInfoName
     *            Le nom de l'iformation.
     * @return La mappe avec les valeurs de l'information.
     */
    public Map<String, Object> getInfos(String aInfoName) {

        Set<Map.Entry<String, AttributeInfo>> entrySet = getAttributes().entrySet();
        Map<String, Object> map = new HashMap<>(entrySet.size(), 1); // Mappe
                                                                     // pour le
                                                                     // resutat
        for (Map.Entry<String, AttributeInfo> entry : entrySet) {
            map.put(entry.getKey(), entry.getValue().getInfo(aInfoName));
        }
        return map;
    }

    /**
     * Retourne la valeur d'une information d'un attribut du ValueObject.
     *
     * @param aAttributeName
     *            Le nom de l'attribut.
     * @param aInfoName
     *            Le nom de l'information.
     * @return La valeur d'une information.
     */
    public Object getInfo(String aAttributeName, String aInfoName) {
        if (getAttributes().get(aAttributeName) == null) {
            return null;
        }
        return getAttributes().get(aAttributeName).getInfo(aInfoName);
    }

    /**
     * Retourne Le <code>Set</code> des noms des attributs.
     *
     * @return Le <code>Set</code> des noms des attributs.
     */
    public Set<String> getNames() {

        return getAttributes().keySet();
    }

    /**
     * Retourne le <code>Set</code> des noms des attributs contenus dans une table.
     *
     * @param aTableName
     *            le nom de la table.
     * @return Le <code>Set</code> des noms des attributs.
     */
    public Set<String> getNames(String aTableName) {

        Set<String> result = getNames(AttributeInfo.TABLE, aTableName);
        // Si c'est la table principale on ajoute tous les attributs dont la
        // table n'est pas spécifiée.
        if (aTableName != null && aTableName.equals(getTable())) {
            result.addAll(getNames(AttributeInfo.TABLE, null));
        }
        return result;
    }

    /**
     * Retourne la <code>List</code> des noms des attributs. Les attributs sont triées par ordre de présence dans le fichier
     * "ValueObject.xsd".
     *
     * @return La <code>List</code> des noms des attributs.
     */
    public List<String> getNamesList() {

        // Mappe triée
        SortedMap<Object, String> map = new TreeMap<>();
        for (Map.Entry<String, Object> entry : getInfos(AttributeInfo.POSITION).entrySet()) {
            // Ajoute à la mappe triée le nom de l'attribut
            map.put(entry.getValue(), entry.getKey());
        }
        // Retourne seulement les noms des attributs.
        return new ArrayList<>(map.values());
    }

    /**
     * Retourne la mappe des informations de jointure.
     *
     * @return La mappe des informations de jointure.
     */
    @SuppressWarnings("unchecked")
    public Map<String, JoinInfo> getJoins() {
        return (Map<String, JoinInfo>) iInfo.get(JOINS);
    }

    /**
     * Retourne le <code>set</code> des clauses de jointure.
     *
     * @return Le <code>set</code> des clauses de jointure.
     */
    public Set<String> getJoinsClauses() {

        Map<String, JoinInfo> joins = getJoins(); // La mappe des informations
        // de jointure.
        Set<String> clauses = new HashSet<>(joins.size());
        for (JoinInfo join : joins.values()) {
            // Ajoute la clause
            clauses.add(join.getClause());
        }
        return clauses;
    }

    /**
     * Retourne la liste des critères de tri.
     *
     * @return La liste des critères de tri.
     */
    @SuppressWarnings("unchecked")
    public List<SortInfo> getSorts() {
        return (List<SortInfo>) iInfo.get(SORTS);
    }

    @SuppressWarnings("unchecked")
    public List<ChildrenInfo> getChildrens() {
        return (List<ChildrenInfo>) iInfo.get(CHILDREN);
    }

    @SuppressWarnings("unchecked")
    public List<MultiselectInfo> getMultiselects() {
        return (List<MultiselectInfo>) iInfo.get(MULTISELECT);
    }

    @SuppressWarnings("unchecked")
    public List<ParentInfo> getParents() {
        return (List<ParentInfo>) iInfo.get(PARENT);
    }

    @SuppressWarnings("unchecked")
    public List<DelCascadeInfo> getDelCascade() {
        return (List<DelCascadeInfo>) iInfo.get(DELCASCADE);
    }

    @SuppressWarnings("unchecked")
    public List<ShellInfo> getShellInfo() {
        return (List<ShellInfo>) iInfo.get(SHELL);
    }

    @SuppressWarnings("unchecked")
    public List<SimpleTogglePanelInfo> getSimpleTogglePanelInfo() {
        return (List<SimpleTogglePanelInfo>) iInfo.get(SIMPLETOGGLEPANEL);
    }

    public String[][] getSortArray() {

        List<SortInfo> list = getSorts();
        if (list.isEmpty()) {
            String[][] array = { { getId() } };
            return array;
        }
        String[][] array = new String[list.size()][];
        List<?> subList = null;
        for (int i = 0; i < array.length; i++) {
            subList = list.get(i).getItems();
            array[i] = new String[subList.size()];
            for (int j = 0; j < array[i].length; j++) {
                array[i][j] = (String) subList.get(j);
            }
        }
        return array;
    }

    /**
     *
     * @return les orientations des items
     */
    public Sort[][] getItemOrientationArray() {

        List<SortInfo> list = getSorts();
        if (list.isEmpty()) {
            return null;
        }
        Sort[][] array = new Sort[list.size()][];
        List<?> subList = null;
        for (int i = 0; i < array.length; i++) {
            subList = list.get(i).getOrientations();
            array[i] = new Sort[subList.size()];
            for (int j = 0; j < array[i].length; j++) {
                array[i][j] = (Sort) subList.get(j);
            }
        }
        return array;
    }

    /**
     *
     * @return les 'toggables' des items
     */
    public Boolean[][] getItemToggableArray() {

        List<SortInfo> list = getSorts();
        if (list.isEmpty()) {
            return null;
        }
        Boolean[][] array = new Boolean[list.size()][];
        List<?> subList = null;
        for (int i = 0; i < array.length; i++) {
            subList = list.get(i).getToggables();
            array[i] = new Boolean[subList.size()];
            for (int j = 0; j < array[i].length; j++) {
                array[i][j] = (Boolean) subList.get(j);
            }
        }
        return array;
    }

    /**
     * Pour obtenir le mapping entre les clés et le numéro de tri
     *
     * @return
     */
    public Map<String, Integer> getSortKeys() {

        List<SortInfo> list = getSorts();
        Map<String, Integer> map = new HashMap<>();
        if (list == null || list.isEmpty()) {
            map.put("id", 0);
            return map;
        }
        for (int i = 0; i < list.size(); i++) {
            // Attention, un -1 est appliqué dans le DAO entre le numéro du
            // tri et la position dans le Array Sort. C'est pour cela que
            // le mapping contient un +1!
            map.put(list.get(i).getId(), i + 1);
        }
        return map;
    }

    /**
     * Retourne la clé du tri par défaut.
     *
     * @return
     */
    public String getDefaultOrderKey() {
        List<SortInfo> list = getSorts();
        if (list == null || list.isEmpty()) {
            return "id";
        }
        return list.get(0).getId();
    }

    public Sort getDefaultSortOrder() {
        List<SortInfo> list = getSorts();
        if (list == null || list.isEmpty() || list.get(0).getId() == null) {
            return Sort.ASCENDING;
        }
        // On prend l'orientation du premier, c'est nécessaire de changer cela
        // si on veut un default order complexe
        Sort sortOrder = list.get(0).getOrientations().get(0);

        return sortOrder;
    }

    /**
     * Retourne la mappe des types des attributs. La clé est le nom de l'attribut la valeur est un <cod>Byte</code> spécifiant le type selon
     * la spécification de <code>DynamicVO</code>.
     *
     * @return La mappe des types des attributs.
     */
    public Map<String, IValueObject.Type> getTypes() {

        // La mappe des types selon le format du fichier "ValueObject.xsd"
        Map<String, Object> map = getInfos(AttributeInfo.TYPE);
        Map<String, IValueObject.Type> result = new HashMap<>(map.size());
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            // Remplace le type au format du fichier "ValueObject.xsd" par
            // le format psécifié dans <code>DynamicVO</code>
            result.put(entry.getKey(), AbstractDynamicVO.type((String) entry.getValue()));
        }
        return result;
    }

    /**
     * Retourne le <code>Set</code> des attributs dont une information est égale à une certaine valeur. L'égalité est donnée par:
     * <code>(o1=null && o2==null) || o1.equal(o2)</code>
     *
     * @param aInfoName
     * @param aInfoValue
     * @return
     */
    public Set<String> getNames(String aInfoName, Object aInfoValue) {

        // Mappe des valurs de l'information
        Map<String, Object> map = getInfos(aInfoName);
        // Le set pour le résultat
        Set<String> result = new HashSet<>(map.size());

        Object value;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            value = entry.getValue();
            // Effectur le test
            if (value == null && aInfoValue == null || value != null && value.equals(aInfoValue)) {
                // Ajoute le nom de l'attribut
                result.add(entry.getKey());
            }
        }
        return result;
    }

    /**
     * Retourne le <code>Set</code> des attributs à pas mettre à jour. Il s'agit des attributs qui ne sont pas dans la page principale, du
     * timestamp et de l'identifiant de l'utilisateur.
     *
     * @return Le <code>Set</code> des attributs à pas mettre à jour.
     */
    public Set<String> getNoCreateUpdate() {

        // Toutes les attributs
        Set<String> result = new HashSet<>(getNames());
        // Enlève les attributs de la table principale
        // -> attributs des autres table
        result.removeAll(getNames(getTable()));
        // Ajoute le timestamp et l'identifiant de l'utilisateur
        result.add(getTimestamp());
        result.add(getUpdateUser());
        return result;
    }

    /**
     * Retourne la liste des champs pour les select, c'est utilisé lors de table contenant un champ shape. La liste des champs sont 1) Les
     * champs de la table en cours 2) Les champs des tables joins, il est nécessaire d'être correct dans le nom de la table dans les
     * fichiers de config (champ 'table'), ce champ est case sensitive
     *
     * @return Le <code>Set</code> des attributs à sélectionner.
     */
    public Set<String> getSelectFields() {
        // Prendre les attributs de la table principale
        Set<String> result = new HashSet<>(getNames(getTable()));

        // Ajouter les champs pour toutes les jointures
        Map<String, JoinInfo> joins = getJoins();
        for (Map.Entry<String, JoinInfo> join : joins.entrySet()) {
            result.addAll(getNames(join.getKey()));
        }

        return result;
    }

    /**
     * Retourne le <code>Set</code> des attribut cachées.
     *
     * @return Le <code>Set</code> des attribut cachées.
     */
    public Set<String> getHiddens() {

        return getNames(AttributeInfo.HIDDEN, Boolean.TRUE);
    }

    public Set<String> getRequireds() {

        return getNames(AttributeInfo.REQUIRED, Boolean.TRUE);
    }

    /**
     * Retourne le <code>Set</code> des attribut de la liste des résultat.
     *
     * @return Le <code>Set</code> des attribut de la liste des résultat.
     */
    public Set<String> getLists() {

        return getNames(AttributeInfo.LIST, Boolean.TRUE);
    }

    /**
     * Retourne la <code>List</code> des attribut de la liste des résultat. Les attributs sont triées par ordre de présence dans le fichier
     * "ValueObject.xsd".
     *
     * @return La <code>List</code> des attribut de la liste des résultat.
     */
    public List<String> getListsList() {

        // Mappe triée
        SortedMap<Object, String> map = new TreeMap<>();
        for (String name : getLists()) {
            // Ajoute les attributs de la liste des résultats avec la position
            map.put(getAttribute(name).getInfo(AttributeInfo.POSITION), name);
        }

        // Retourne les noms des attributs
        return new ArrayList<>(map.values());
    }

    /**
     * Retourne un mapping entre les attributs et les noms correspondants dans la base de données
     *
     * @return map avec les attributs et les noms correspondants dans la base de données
     */
    public Map<String, String> getAttributeNameDBNameMap() {
        Map<String, String> resultingAttributeNameDBNameMap = new HashMap<>();

        Map<String, Object> attributeNameDBNameMap = getInfos(AttributeInfo.DB_NAME);
        Set<Entry<String, Object>> allAttributeNameDBNameEntrySet = attributeNameDBNameMap.entrySet();

        for (Entry<String, Object> allAttributeNameDBNameEntry : allAttributeNameDBNameEntrySet) {
            String key = allAttributeNameDBNameEntry.getKey();
            Object value = allAttributeNameDBNameEntry.getValue();

            if (value != null) {
                resultingAttributeNameDBNameMap.put(key, value.toString());
            }
        }

        return resultingAttributeNameDBNameMap;
    }

    /**
     * Retourne le nom de la table principale du ValueObject.
     *
     * @return Le nom de la table principale du ValueObject.
     */
    public String getTable() {

        return (String) getValue(TABLE);
    }

    /**
     * Retourne le nom de la table du champ.
     *
     * @return Le nom de la table du champ.
     */
    public String getTable(String aFieldName) {
        String str = (String) getInfo(aFieldName, AttributeInfo.TABLE);
        if (str != null) {
            return str;
        }
        return getTable();
    }

    /**
     * Retourne le nom des tables des champs.
     *
     * @return Le nom des tables des champs.
     */
    public Collection<String> getTables(Collection<String> fieldNames) {
        Set<String> tables = new HashSet<>();
        for (String str : fieldNames) {
            String table = (String) getInfo(str, AttributeInfo.TABLE);
            if (table != null) {
                tables.add(table);
            }
        }
        return tables;
    }

    /**
     * Retourne le <code>Set</code> des noms des tablées du ValueObject.
     *
     * @return Le <code>Set</code> des noms des tablées du ValueObject.
     */
    public Set<String> getTables() {

        Set<String> result = new HashSet<>(getJoins().keySet());
        result.add(getTable());
        return result;
    }

    /**
     * Retourne le <code>Set</code> des attribut dans la recherche doit utiliser l'égalité stricte. Contient seulement les attributs de type
     * <code>
     * java.lang.String</code> et ceux d'autres types avec le paramètre à equal.
     *
     * @return Le <code>Set</code> des attribut dans la recherche doit utiliser l'égalité stricte.
     */
    public Set<String> getEquals() {

        return getNames(AttributeInfo.SEARCH, AttributeType.EQUAL.toString());
    }

    /**
     * Retourne le nom de l'identifiant.
     *
     * @return Le nom de l'identifiant.
     */
    public String getId() {

        return (String) getValue(ID);
    }

    /**
     * Retourne le nom du timestamp.
     *
     * @return Le nom du timestamp.
     */
    public String getTimestamp() {

        return (String) getValue(TIMESTAMP);
    }

    /**
     * Return true only, if label and input text pair are to align vertically
     *
     * @return Return true only, if label and input text pair are to align vertically
     */
    public boolean getVerticalAlignment() {
        String verticalAlignment = (String) getValue(VERTICAL_ALIGNMENT);
        Boolean verticalAlignementBoolean = Boolean.valueOf(verticalAlignment);
        return verticalAlignementBoolean.booleanValue();
    }

    /**
     * Return true only, if detail page is used for searches
     *
     * @return Return true only, if detail page is used for searches
     */
    public boolean isDetailPageToUseForSearches() {
        String detailPageUsedForSearches = (String) getValue(USE_DETAIL_PAGE_FOR_SEARCHES);
        Boolean detailPageUsedForSearchesBoolean = Boolean.valueOf(detailPageUsedForSearches);
        return detailPageUsedForSearchesBoolean.booleanValue();
    }

    /**
     * Return true only, if children are to display in search page
     *
     * @return Return true only, if detail page is used for searches
     */
    public boolean isChildrenToDisplayInSearchPage() {
        String childrenToDisplayInSearchPage = (String) getValue(DISPLAY_CHILDREN_IN_SEARCH_PAGE);
        Boolean childrenToDisplayInSearchPageBoolean = Boolean.valueOf(childrenToDisplayInSearchPage);
        return childrenToDisplayInSearchPageBoolean.booleanValue();
    }

    /**
     * Retourne le nom de l'identifiant de l'utilisateur.
     *
     * @return Le nom de l'identifiant de l'utilisateur.
     */
    public String getUpdateUser() {

        return (String) getValue(UPDATEUSER);
    }

    /**
     * Retourne la representation textuelle de l'objet.
     *
     * @return La representation textuelle de l'objet
     */
    @Override
    public String toString() {

        StringBuffer sb = new StringBuffer(512);
        sb.append("VOInfo[iName=");
        sb.append(iName);
        sb.append(", iInfo=");
        if (iInfo == null) {
            sb.append("null");
        } else {
            sb.append(iInfo.toString());
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * @param aObject
     * @return
     */
    @Override
    public boolean equals(Object aObject) {

        if (!(aObject instanceof VOInfo)) {
            return false;
        }
        VOInfo voInfo = (VOInfo) aObject;
        if (!iName.equals(voInfo.iName)) {
            return false;
        }
        return iInfo.equals(voInfo.iInfo);
    }

    @Override
    public int hashCode() {

        return iName.hashCode() + iInfo.hashCode();
    }

    public String getName() {

        return iName;
    }

    public void setName(String aName) {

        iName = aName;
    }

}