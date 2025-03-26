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

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.javaunderground.jdbc.StatementFactory;

import ch.inser.dynamic.common.DAOParameter.Name;
import ch.inser.dynamic.common.IDAOResult.Status;
import ch.inser.dynamic.common.IValueObject.Type;
import ch.inser.dynamic.list.IResultContainerFactory;
import ch.inser.dynamic.util.ChildrenInfo;
import ch.inser.jsl.beans.LabelValueBean;
import ch.inser.jsl.list.ListHandler;
import ch.inser.jsl.list.ListHandler.Sort;
import ch.inser.jsl.tools.NumberTools;

/**
 * Classe générique d'accès à la base de données.
 *
 * De manière générale, les méthodes sont déclarées protected, elles doivent être utilisées par le DAO instancié pour cacher les détails
 * techniques!
 *
 * @author INSER SA
 * @version 1.0
 */
public abstract class AbstractDynamicDAO implements DynamicDAO, Serializable {

    /**
     * Factory pour créer un custom container pour le résultat d'une recherche. Le container par défaut est array list.
     */
    private transient IResultContainerFactory iResultContainerFactory;

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -173996324277170914L;

    /**
     * Nombre max de lignes à lire. Valeurs par défaut, si elle est plus grande que 0, il n'est plus possible de faire des requêtes sans
     * limites!
     */
    private static int cResultSetMaxRows = 0;

    /**
     * Nombre maximum de ligne à lire pour cette instance, si non null, surcharge la valeur statique
     */
    private Integer iInstanceResultSetMaxRows = null;

    /**
     * Objet pour les logs.
     */
    private static final Log logger = LogFactory.getLog(AbstractDynamicDAO.class);

    /**
     * Current DAO has attribute names mapped to a DB field name
     */
    protected boolean iHasAttributeNamesMappedToDBNames = false;

    /**
     * Map with with different attribute and DB name values for current DAO
     */
    protected transient Map<String, String> iAttributeNameDBNameMap;

    /**
     * Map with with different DB and attribute name values for current DAO
     */
    protected transient Map<String, String> iDBNameAttributeNameMap;

    /**
     * Buffer par défaut pour un string builder
     */
    public static final int STRING_BUFFER_SIZE = 512;

    /**
     * Lecture d'une entité correspondante à l'identifiant spécifié depuis la base de données. Le record est retourné dans un value objet.
     *
     * @param aIdName
     *            Le nom de l'attribut id.
     * @param aIdValue
     *            La valeur de l'id
     * @param aTable
     *            Le nom de la table
     * @param aVo
     *            L'objet dans le quel le resultat va être retourné.
     * @param aConnection
     *            Connexion pour exécuter la requête.
     *
     * @return Un value object correspondant aux paramètres.
     *
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données. Un erreur au niveau de la base de données.
     */
    protected IDAOResult getRecord(String aIdName, Object aIdValue, String aTable, IValueObject aVo, Connection aConnection)
            throws SQLException {

        Set<String> tables = new HashSet<>(1, 1);
        tables.add(aTable);
        return getRecord(aIdName, aIdValue, tables, (String) null, null, aVo, aConnection);
    }

    /**
     * @param aIdName
     *            nom du champ id
     * @param aIdValue
     *            l'id
     * @param aTable
     *            nom de table
     * @param aVo
     *            vo avec configuration de champs
     * @param aSecurityClause
     *            filtre de sécurité
     * @param aConnection
     *            connexion
     * @return l'enregistrement
     * @throws SQLException
     *             erreur bd
     */
    protected IDAOResult getRecord(String aIdName, Object aIdValue, String aTable, IValueObject aVo, String aSecurityClause,
            Connection aConnection) throws SQLException {

        Set<String> tables = new HashSet<>(1, 1);
        tables.add(aTable);
        return getRecord(aIdName, aIdValue, tables, (String) null, null, aVo, aSecurityClause, aConnection);
    }

    /**
     * Lecture d'une entité correspondante à l'identifiant spécifié depuis la base de données. Le record est retourné dans un value objet.
     *
     * @param aIdName
     *            Le nom de l'attribut id.
     * @param aIdValue
     *            La valeur de l'id.
     * @param aTables
     *            Les noms des tables.
     * @param aJoin
     *            Les conditions de jointure entre les tables.
     * @param aAttributes
     *            Les attributs à lire
     * @param aVo
     *            L'objet dans le quel le resultat va être retourné.
     * @param aConnection
     *            Connexion pour exécuter la requête.
     *
     * @return Un value object correspondant aux paramètres.
     *
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données. Un erreur au niveau de la base de données.
     */
    protected IDAOResult getRecord(String aIdName, Object aIdValue, Set<String> aTables, String aJoin, Set<String> aAttributes,
            IValueObject aVo, Connection aConnection) throws SQLException {
        return getRecord(aIdName, aIdValue, aTables, aJoin, aAttributes, aVo, null, aConnection);
    }

    /**
     * @param aIdName
     *            nom du champ id
     * @param aIdValue
     *            l'id
     * @param aTables
     *            nom de tables pour la clause FROM
     * @param aJoin
     *            expression join pour la clause WHERE
     * @param aAttributes
     *            attributs à retourner dans le résultat
     * @param aVo
     *            vo avec configuration de champs
     * @param aSecurityClause
     *            filtre de sécurité
     * @param aConnection
     *            connexion
     * @return l'enregistrement
     * @throws SQLException
     *             erreur bd
     */
    protected IDAOResult getRecord(String aIdName, Object aIdValue, Set<String> aTables, String aJoin, Set<String> aAttributes,
            IValueObject aVo, String aSecurityClause, Connection aConnection) throws SQLException {
        if (aIdValue == null) {
            return new DAOResult(Status.NOTHING_TODO);
        }
        // On crée la requête SQL selon les critères de recherche existants
        StringBuilder sql = new StringBuilder(STRING_BUFFER_SIZE);
        sql.append("SELECT ");
        List<String> attributes = null;
        if (aAttributes != null) {
            attributes = new ArrayList<>(aAttributes);
        }
        // ajout des attributs
        addAttributesNames(aVo, attributes, null, sql, Mode.SELECT);
        sql.append(" FROM ");
        addNames(aTables, sql);
        sql.append(" WHERE ");
        DAOTools.addClause(aIdName, null, AttributeType.EQUAL, sql);
        if (aJoin != null) {
            sql.append(" AND ");
            sql.append(aJoin);
        }
        if (aSecurityClause != null) {
            sql.append(" AND ");
            sql.append(aSecurityClause);
        }
        return getRecord(aIdName, aIdValue, sql, aAttributes, aVo, aConnection);
    }

    /**
     * Lecture d'une entité correspondante à l'identifiant spécifié depuis la base de données. Le record est retourné dans un value objet.
     *
     * @param aIdName
     *            Le nom de l'attribut id.
     * @param aIdValue
     *            La valeur de l'id.
     * @param aTables
     *            Les noms des tables.
     * @param aJoin
     *            Les conditions de jointure entre les tables.
     * @param aAttributes
     *            Les attributs à lire
     * @param aVo
     *            L'objet dans le quel le resultat va être retourné.
     * @param aConnection
     *            Connexion pour exécuter la requête.
     *
     * @return Un value object correspondant aux paramètres.
     *
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données. Un erreur au niveau de la base de données.
     */
    protected IDAOResult getRecord(String aIdName, Object aIdValue, Set<String> aTables, Set<String> aJoin, Set<String> aAttributes,
            IValueObject aVo, Connection aConnection) throws SQLException {
        return getRecord(aIdName, aIdValue, aTables, joinClause(aJoin), aAttributes, aVo, null, aConnection);
    }

    /**
     *
     * @param aIdName
     *            Le nom de l'attribut id.
     * @param aIdValue
     *            La valeur de l'id.
     * @param aTables
     *            Les noms des tables.
     * @param aJoin
     *            Les conditions de jointure entre les tables.
     * @param aAttributes
     *            Les attributs à lire
     * @param aVo
     *            L'objet dans le quel le resultat va être retourné.
     * @param aSecurityClause
     *            filtre de sécurité
     * @param aConnection
     *            Connexion pour exécuter la requête.
     *
     * @return Un value object correspondant aux paramètres.
     * @throws SQLException
     *             erreur bd
     */
    protected IDAOResult getRecord(String aIdName, Object aIdValue, Set<String> aTables, Set<String> aJoin, Set<String> aAttributes,
            IValueObject aVo, String aSecurityClause, Connection aConnection) throws SQLException {

        return getRecord(aIdName, aIdValue, aTables, joinClause(aJoin), aAttributes, aVo, aSecurityClause, aConnection);
    }

    /**
     * Créé la requète SQL select count(*) avec des "?", e.g. SELECT count(*) FROM t_collaborateur WHERE col_fin_activite=?"
     *
     * @param aVo
     *            vo requête
     * @param aParameters
     *            dao parameters
     * @return statement select count(*) avec clause where
     */
    @SuppressWarnings("unchecked")
    protected StringBuilder getCountStatement(IValueObject aVo, DAOParameter... aParameters) {
        StringBuilder sql = new StringBuilder(STRING_BUFFER_SIZE);
        sql.append("SELECT COUNT(*) FROM ");
        addNames((Collection<String>) DAOParameter.getValue(Name.TABLE_NAMES, aParameters), sql);
        sql.append(getListWhere(aVo, aParameters));
        return sql;
    }

    /**
     * Créé la requète SQL avec des "?", e.g. SELECT col_nom, col_prenom FROM t_collaborateur WHERE col_fin_activite=?"
     *
     * @param aVo
     *            vo requête
     * @param aAttributes
     *            noms de champs pour la clause select
     * @param aParameters
     *            paramètres dao
     * @return statement select avec where, group by et order by
     */
    @SuppressWarnings("unchecked")
    protected StringBuilder getListStatement(IValueObject aVo, DAOParameter... aParameters) {
        StringBuilder sql = new StringBuilder(STRING_BUFFER_SIZE);
        if (aVo.getProperty("DISTINCT") != null && (Boolean) aVo.getProperty("DISTINCT")) {
            sql.append("SELECT DISTINCT ");
        } else {
            sql.append("SELECT ");
        }
        addAttributesNames(aVo, (List<String>) DAOParameter.getValue(Name.ATTRIBUTES, aParameters), null, sql, Mode.SELECT);
        sql.append(" FROM ");
        // ici les jointure sont ajoutées à la Oracle
        addNames((Collection<String>) DAOParameter.getValue(Name.TABLE_NAMES, aParameters), sql);
        sql.append(getListWhere(aVo, aParameters));
        sql.append(getListGroupBy(aParameters));
        sql.append(getListSort(aParameters));
        return sql;
    }

    /**
     * Crée la clause ORDER BY selon les éléments de tri données en paramètre
     *
     * @param aParameters
     *            paramètres dao
     * @return stringbuilder avec clause ORDER BY
     */
    protected StringBuilder getListSort(DAOParameter... aParameters) {
        StringBuilder sort = new StringBuilder(STRING_BUFFER_SIZE);
        String[] fields = (String[]) DAOParameter.getValue(Name.SORT_FIELDS, aParameters);
        if (fields == null) {
            return sort;
        }
        sort.append(" ORDER BY ");
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].startsWith("text(") || fields[i].startsWith("codetext(")) {
                sort.append(getSortFunction(fields[i], aParameters));
            } else {
                sort.append(fields[i]);
            }
            // Evite de mettre une direction si la direction a été donnée
            // dans le fichier de config "nomduchamp ASC" et ce n'est pas
            // "toggable"
            if (fields[i].indexOf(' ') <= 0) {
                sort.append(getListOrientation(i, aParameters));
            }
            if (i < fields.length - 1) {
                sort.append(", ");
            }
        }
        return sort;
    }

    /**
     * Donne la valeur d'un order by qui est une fonction au lieu d'un nom de champ
     *
     * @param aSortField
     *            nom du champ
     * @param aParameters
     *            paramètres dao
     * @return une fonction FC_ORDERTEXT(...) ou FC_ORDERCOD(...)
     */
    private String getSortFunction(String aSortField, DAOParameter[] aParameters) {
        // Langue actuelle
        String lang = ((ILoggedUser) DAOParameter.getValue(Name.USER, aParameters)).getLocalLanguage();
        // Tri le champ par le texte en langue actuelle
        String[] split = aSortField.split("[()]");
        String fieldname = split[1];
        String function;
        if ("text".equals(split[0])) {
            function = "FC_ORDERTEXT('" + fieldname + "'," + fieldname + ",'" + lang + "')";
        } else {
            function = "FC_ORDERCOD('" + fieldname + "'," + fieldname + ",'" + lang + "')";
        }
        return function;
    }

    /**
     * Donne l'orientation pour un item de sort
     *
     * @param aFieldIndex
     *            l'index du champ dans les sort items (SORT_FIELDS)
     * @param aParameters
     *            paramètres dao avec orientations et togglables pour chaque sort item
     * @return ASC, DESC ou string vide
     */
    protected StringBuilder getListOrientation(int aFieldIndex, DAOParameter[] aParameters) {
        StringBuilder sort = new StringBuilder();
        // Get orientations and toggables from the optional
        // DAOParameters
        ListHandler.Sort[] orientations = (Sort[]) DAOParameter.getValue(Name.SORT_ORIENTATIONS, aParameters);
        Sort orientation = (Sort) DAOParameter.getValue(Name.SORT_ORIENTATION, aParameters);
        Boolean[] toggables = (Boolean[]) DAOParameter.getValue(Name.SORT_TOGGABLES, aParameters);
        if (toggables == null || orientations == null) {
            sort.append(orientation == ListHandler.Sort.DESCENDING ? " DESC" : " ASC");
            return sort;
        }
        if (aFieldIndex >= toggables.length) {
            return sort;
        }

        if (Boolean.FALSE.equals(toggables[aFieldIndex]) || orientation == orientations[0]) {
            // Fixed sort orientation
            sort.append(orientations[aFieldIndex] == ListHandler.Sort.DESCENDING ? " DESC" : " ASC");
        } else {
            // ASC/DESC depends on: 1) configured orientation 2) changed order
            // in the GUI
            sort.append(orientations[aFieldIndex] == ListHandler.Sort.DESCENDING ? " ASC" : " DESC");
        }
        return sort;
    }

    /**
     * @param aVo
     *            vo requête
     * @param aParameters
     *            dao parameters
     * @return clause where d'un statement select
     */
    @SuppressWarnings("unchecked")
    protected StringBuilder getListWhere(IValueObject aVo, DAOParameter... aParameters) {
        StringBuilder where = new StringBuilder(STRING_BUFFER_SIZE);
        StringBuilder returnwhere = new StringBuilder(STRING_BUFFER_SIZE);
        // Retenir les champs utilisé dans la clause WHERE (vo types +
        // children), vider les autres.
        Collection<String> keys = aVo.getProperties().keySet();
        Set<String> keysForClause = new HashSet<>(aVo.getTypes().keySet());
        keysForClause.add("ADDITIONAL_STATEMENT");
        keysForClause.add("childrenmap");
        if (aVo.getProperty("hasChildren") != null) {
            keysForClause.add("hasChildren");
        }
        keys.retainAll(keysForClause);
        Set<String> join = (Set<String>) DAOParameter.getValue(Name.JOIN_CLAUSES, aParameters);
        String security = (String) DAOParameter.getValue(Name.SECURITY_CLAUSE, aParameters);

        // Si on a des clauses (séléctions et/ou joitures)
        if (!aVo.isEmpty() || !aVo.getOrList().isEmpty() || join != null && !join.isEmpty() || security != null) {
            boolean isFirst = true;
            if (aVo.getProperty("ADDITIONAL_STATEMENT") != null) {
                isFirst = false;
                where.append(aVo.getProperty("ADDITIONAL_STATEMENT"));
                aVo.removeProperty("ADDITIONAL_STATEMENT");
            }
            if (!aVo.getOrList().isEmpty()) {
                // OR clause
                addORClause(aVo, aVo.getOrList(), isFirst, where);
                isFirst = false;
            }
            if (!aVo.isEmpty()) {
                if (!isFirst) {
                    where.append(" AND ");
                }
                isFirst = false;
                // Children clause
                addChildrenClause(aVo, where);
                // Master clause
                addClause(aVo, true, where);
            }
            if (security != null) {
                if (!isFirst) {
                    where.append(" AND ");
                }
                where.append(security);
                if (security.length() > 0) {
                    isFirst = false;
                }
            }
            if (join != null && !join.isEmpty()) {
                where.append(getJoinWhere(join, isFirst));
            }
        }
        if (where.length() > 0) {
            returnwhere.append(" WHERE ").append(where);
        }
        return returnwhere;
    }

    /**
     * Ajout de la clause de jointure selon le concepte oracle
     *
     * @param join
     * @param isFirst
     * @param where
     * @return
     */
    protected StringBuilder getJoinWhere(Set<String> join, boolean isFirst) {
        StringBuilder where = new StringBuilder();
        if (!isFirst) {
            where.append(" AND ");
        }
        Iterator<String> it = join.iterator();
        while (it.hasNext()) {
            where.append(it.next());
            if (it.hasNext()) {
                where.append(" AND ");
            }
        }
        return where;
    }

    /**
     * Get the "GROUP BY" statement for a get list request.
     *
     * @param aParameters
     *            the DAO parameters.
     * @return the "GROUP BY" statement
     */
    protected Object getListGroupBy(DAOParameter[] aParameters) {
        StringBuilder orderBy = new StringBuilder(STRING_BUFFER_SIZE);
        Object value = DAOParameter.getValue(Name.GROUP_BY, aParameters);
        if (value != null) {
            orderBy.append(" GROUP BY ");
            orderBy.append(value);
        }
        return orderBy;
    }

    /**
     * Ajoute la clause pour les critères de recherche sur les enfants
     *
     * @param aVo
     *            le vo du master object
     * @param aSql
     *            la requète sql du master object
     */
    private void addChildrenClause(IValueObject aVo, StringBuilder aSql) {

        // Are there children criteria?
        Boolean hasChildren = (Boolean) aVo.getProperty("hasChildren");

        if (hasChildren != null && hasChildren) {
            if (!getChildrenMasterLinks(aVo).isEmpty()) {
                for (String masterLink : getChildrenMasterLinks(aVo)) {
                    // Set master link property in a new vo
                    IValueObject vo = (IValueObject) aVo.clone();
                    vo.clear();
                    Object childClause = aVo.getProperty(masterLink);
                    vo.setProperty(masterLink, childClause);

                    // Add mster link clause
                    addClause(vo, true, aSql);

                    // Remove master link property
                    aVo.removeProperty(masterLink);
                }
            } else {
                // Set id property in a new vo
                IValueObject vo = (IValueObject) aVo.clone();
                vo.clear();
                Object childClause = aVo.getId();
                vo.setId(childClause);

                // Add child clause
                addClause(vo, true, aSql);

                // Remove id property
                if (aVo.getVOInfo() != null && aVo.getVOInfo().getId() != null) {
                    aVo.removeProperty(aVo.getVOInfo().getId());
                }
            }

            // Vérifie s'il y a d'autres champs sauf enfants
            IValueObject voCopy = (IValueObject) aVo.clone();
            voCopy.removeProperty("hasChildren");
            voCopy.removeProperty("childrenmap");
            if (!voCopy.isEmpty()) {
                aSql.append(" AND ");
            }
        }
    }

    /**
     * Cherche les nom des master links configurés pour les enfants
     *
     * @param aVo
     *            vo requête avec childrenmap
     * @return liste avec les master links
     */
    private List<String> getChildrenMasterLinks(IValueObject aVo) {
        List<String> masterLinks = new ArrayList<>();
        Map<?, ?> map = (Map<?, ?>) aVo.getProperty("childrenmap");
        if (aVo.getVOInfo() != null && aVo.getVOInfo().getChildrens() != null) {
            for (ChildrenInfo child : aVo.getVOInfo().getChildrens()) {
                String childName = child.getChildrenName();

                // Test if child contains search criteria
                IValueObject voChild = (IValueObject) map.get(childName);
                if (voChild != null && !voChild.isEmpty()) {
                    masterLinks.add(child.getMasterLink());
                }
            }
        }
        return masterLinks;
    }

    /**
     * Ajoute des conditions avec l'operatuer OR à la requête SQL (PreparedStatement)
     *
     * @param aVo
     *            vo de l'objet métier
     * @param aOrList
     *            liste représentante une requête OR
     * @param aFirst
     *            Indique s'il s'agit de la première clause.
     * @param aSql
     *            La requête à la quelle ajouter les conditions.
     */
    protected void addORClause(IValueObject aVo, List<Map<String, Object>> aOrList, boolean aFirst, StringBuilder aSql) {
        if (!aFirst) {
            aSql.append(" AND ");
        }

        Iterator<Map<String, Object>> itAnd = aOrList.iterator();
        while (itAnd.hasNext()) {
            boolean first = true;
            Map<String, Object> orStatements = itAnd.next();
            Collection<String> keys = new ArrayList<>();
            keys.addAll(orStatements.keySet());
            keys.retainAll(aVo.getTypes().keySet());
            Iterator<String> itOr = keys.iterator();
            String attribute;
            String dbattribute;
            if (first) {
                aSql.append("(");
            }
            if (!first && !keys.isEmpty()) {
                aSql.append(" OR ");
            }

            while (itOr.hasNext()) {
                attribute = itOr.next();
                dbattribute = attribute;
                if (iHasAttributeNamesMappedToDBNames) {
                    String dbNameOfAttribute = getDBName(attribute);
                    if (dbNameOfAttribute != null) {
                        dbattribute = dbNameOfAttribute;
                    }
                }

                String search = "";
                if (aVo.getVOInfo() != null && aVo.getVOInfo().getAttribute(attribute) != null) {
                    search = (String) aVo.getVOInfo().getAttribute(attribute).getInfo("search");
                }

                if (search.equals(AttributeType.EQUAL.toString()) || !(orStatements.get(attribute) instanceof String)) {
                    DAOTools.addClause(dbattribute, orStatements.get(attribute), AttributeType.EQUAL, aSql);
                } else if (search.equals(AttributeType.FULL_LIKE.toString())) {
                    DAOTools.addClause(dbattribute, orStatements.get(attribute), AttributeType.FULL_LIKE, aSql);
                } else if (search.equals(AttributeType.LIKE.toString())) {
                    DAOTools.addClause(dbattribute, orStatements.get(attribute), AttributeType.LIKE, aSql);
                } else {
                    // Valeur par défaut de recherche: upper full like
                    DAOTools.addClause(dbattribute, orStatements.get(attribute), AttributeType.UPPER_FULL_LIKE, aSql);
                }
                if (itOr.hasNext()) {
                    aSql.append(" OR ");
                } else {
                    aSql.append(")");
                }
            }
            if (itAnd.hasNext()) {
                aSql.append(" AND ");
            }
        }
    }

    /**
     * Cherche une liste d'enregistrements
     *
     * @param aVo
     *            critères de recherche
     * @param aConnection
     *            connexion
     * @param aParameters
     *            paramètres additionels
     * @return liste de vos
     * @throws SQLException
     *             erreur au niveau base de données
     */
    @SuppressWarnings("unchecked")
    protected Collection<IValueObject> getList(IValueObject aVo, Connection aConnection, DAOParameter... aParameters) throws SQLException {
        StringBuilder sql = getListStatement(aVo, aParameters);
        sql = getSliceStatement(sql, aParameters);
        List<IValueObject> vos = new ArrayList<>(1);
        vos.add(aVo);
        return getList(vos, sql, (Collection<String>) DAOParameter.getValue(Name.ATTRIBUTES, aParameters), aVo,
                (Integer) DAOParameter.getValue(Name.ROWNUM_MAX, aParameters), aConnection);
    }

    /**
     *
     * @param aSql
     *            le sql statement
     * @param aParameters
     *            additional parameters with start and end rownums
     * @return statement qui cherche une tranche du résultat selon paramètres début et fin
     */
    protected StringBuilder getSliceStatement(StringBuilder aSql, DAOParameter[] aParameters) {
        Object start = null;
        Object end = null;
        for (int j = 0; j < aParameters.length; j++) {
            if (aParameters[j].getName() == Name.ROWNUM_START) {
                start = aParameters[j].getValue();
            } else if (aParameters[j].getName() == Name.ROWNUM_END) {
                end = aParameters[j].getValue();
            }
        }
        if (start == null || end == null) {
            return aSql;
        }

        StringBuilder sliceSql = new StringBuilder("select * from (select a.*, rownum rn from (");
        sliceSql.append(aSql);
        sliceSql.append(") a ) where rn between ");
        sliceSql.append(start);
        sliceSql.append(" and ");
        sliceSql.append(end);
        return sliceSql;
    }

    /**
     * Ajoute des conditions de tri à la requête SQL.
     *
     * @param aSort
     *            Le tableau contenant, dans l'ordre, les attributs de tri à ajouter.
     * @param aSortOrientation
     *            L'ordre de tri.
     * @param aSql
     *            La requête à la quelle ajouter les conditions.
     */
    protected void addSort(String[] aSort, ListHandler.Sort aSortOrientation, StringBuilder aSql) {

        if (aSort == null) {
            return;
        }
        String orientation = aSortOrientation == ListHandler.Sort.DESCENDING ? " DESC" : " ASC";
        boolean first = true;

        for (int i = 0; i < aSort.length; i++) {
            if (first) {
                first = false;
            } else {
                aSql.append(", ");
            }
            addSort(aSort[i], orientation, aSql);
        }
    }

    /**
     * Ajoute une condition de tri à la requête SQL.
     *
     * @param aSort
     *            L'attribut de tri à ajouter.
     * @param aOrientation
     *            L'ordre de tri.
     * @param aSql
     *            La requête à la quelle ajouter la condition.
     */
    protected void addSort(String aSort, String aOrientation, StringBuilder aSql) {

        aSql.append(aSort);
        if (aSort.indexOf(' ') <= 0) {
            aSql.append(aOrientation);
        }
    }

    /**
     * Traite une requête de recherche et fournit une liste d'entités sous forme d'une collection de LabelValueBean. Le label correspond au
     * deuxième paramêtre de la liste des attributs, la valeur au premier.
     *
     * @param aVo
     *            Value object contenant les critères de recherche.
     * @param aTable
     *            Le nom de la table.
     * @param aClauses
     *            Les conditions de recherche supplémentaires.
     * @param aIdName
     *            L'attribut à gérer comme un ID.
     * @param aSort
     *            L'ordre de tri.
     * @param aAttributes
     *            Les attributs à lire.
     * @param aConnection
     *            Connexion pour exécuter la requête.
     *
     * @return Une collection contenant les LabelValueBean.
     *
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données. Un erreur au niveau de la base de données.
     */
    protected List<LabelValueBean> getPopupList(IValueObject aVo, String aTable, String aClauses, String aIdName, String aSort,
            List<String> aAttributes, Connection aConnection) throws SQLException {

        Set<String> idNames = new HashSet<>(1, 1);
        idNames.add(aIdName);
        return getPopupList(aVo, aTable, aClauses, aSort, aAttributes, aConnection);
    }

    /**
     * Traite une requête de recherche et fournit une liste d'entités sous forme d'une collection de LabelValueBean. Le label correspond au
     * deuxième paramêtre de la liste des attributs, la valeur au premier.
     *
     * @param aVo
     *            Value object contenant les critères de recherche.
     * @param aTable
     *            Le nom de la table.
     * @param aClauses
     *            Les conditions de recherche supplémentaires.
     * @param aSort
     *            L'ordre de tri.
     * @param aAttributes
     *            Les attributs à lire.
     * @param aConnection
     *            Connexion pour exécuter la requête.
     *
     * @return Une collection contenant les LabelValueBean.
     *
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données. Un erreur au niveau de la base de données.
     */
    protected List<LabelValueBean> getPopupList(IValueObject aVo, String aTable, String aClauses, String aSort, List<String> aAttributes,
            Connection aConnection) throws SQLException {
        StringBuilder sql = new StringBuilder(STRING_BUFFER_SIZE);

        sql.append("SELECT DISTINCT ");
        addAttributesNames(aVo, aAttributes, null, sql, Mode.SELECT);
        sql.append(" FROM ");
        sql.append(aTable);
        if (!aVo.isEmpty() || aClauses != null) {
            sql.append(" WHERE ");
            if (!aVo.isEmpty()) {
                addClause(aVo, true, sql);
                if (aClauses != null) {
                    sql.append(" AND ");
                }
            }
            if (aClauses != null) {
                sql.append(aClauses);
            }
        }

        if (aSort != null) {
            sql.append(" ORDER BY " + aSort + " ASC");
        }
        return getPopupList(aVo, sql, aConnection);
    }

    /**
     * Traite une requête de recherche et fournit une liste d'entités sous forme d'une collection de LabelValueBean. Le label correspond au
     * deuxième paramêtre de la liste des attributs, la valeur au premier.
     *
     * @param aIds
     *            La liste des id des objets recherchés.
     * @param aTable
     *            Le nom de la table.
     * @param aClauses
     *            Les conditions de recherche supplémentaires.
     * @param aIdName
     *            Les attributs à gérer comme des ID.
     * @param aSort
     *            L'ordre de tri.
     * @param aAttributes
     *            Les attributs à lire.
     * @param aVo
     *            L'objet avec le quel le resultat va être lu.
     * @param aConnection
     *            Connexion pour exécuter la requête.
     *
     * @return Une collection contenant les LabelValueBean.
     *
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données. Un erreur au niveau de la base de données.
     */
    protected List<LabelValueBean> getPopupList(Collection<Object> aIds, String aTable, String aClauses, String aIdName, String aSort,
            List<String> aAttributes, IValueObject aVo, Connection aConnection) throws SQLException {
        StringBuilder sql = new StringBuilder(STRING_BUFFER_SIZE);
        Iterator<Object> it = aIds.iterator();

        sql.append("SELECT DISTINCT ");
        addAttributesNames(aVo, aAttributes, null, sql, Mode.SELECT);
        sql.append(" FROM ");
        sql.append(aTable);
        if (it.hasNext() || aClauses != null) {
            sql.append(" WHERE ");
            if (!aIds.isEmpty()) {
                boolean first = true;
                while (it.hasNext()) {
                    if (first) {
                        first = false;
                    } else {
                        sql.append(" AND ");
                    }
                    it.next();
                    DAOTools.addClause(aIdName, null, AttributeType.EQUAL, sql);
                }
                if (aClauses != null) {
                    sql.append(" AND ");
                }
            }
            if (aClauses != null) {
                sql.append(aClauses);
            }
        }
        if (aSort != null) {
            sql.append(" ORDER BY " + aSort + " ASC");
        }
        // On exécute la requête et on stocke le résultat dans une Map
        List<LabelValueBean> list = new ArrayList<>(getInitListSize());
        try (PreparedStatement ps = getPreparedStatement(sql, aConnection)) {
            it = aIds.iterator();
            int idx = 1;
            while (it.hasNext()) {
                idx = DAOTools.set(idx, it.next(), aVo.getPropertyType(aIdName), AttributeType.EQUAL, ps);
            }
            // Exécute le reqête de recherche
            logger.debug("Exécute SQL: " + ps.toString());
            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    list.add(new LabelValueBean(rs.getString(2), rs.getString(1)));
                }
            }
        }
        return list;
    }

    /**
     * Traite une requête de recherche et fournit une liste d'entités sous forme d'une collection de LabelValueBean. Le label correspond au
     * deuxième paramêtre de la liste des attributs, la valeur au premier.
     *
     * @param aVo
     *            Value object contenant les critères de recherche.
     * @param aSql
     *            La requête SQL à exécuter.
     * @param aConnection
     *            Connexion pour exécuter la requête.
     *
     * @return Une collection contenant les LabelValueBean.
     *
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données. Un erreur au niveau de la base de données.
     */
    protected List<LabelValueBean> getPopupList(IValueObject aVo, StringBuilder aSql, Connection aConnection) throws SQLException {
        // On exécute la requête et on stocke le résultat dans une Map
        List<LabelValueBean> list = new ArrayList<>(getInitListSize());
        try (PreparedStatement ps = getPreparedStatement(aSql, aConnection)) {
            setRecord(aVo, ps, Mode.SELECT, null);
            // Exécute le reqête de recherche
            logger.debug("Exécute SQL: " + ps.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new LabelValueBean(rs.getString(2), rs.getString(1)));
                }
            }
        }
        return list;
    }

    /**
     * Traite une requête de recherche et fournit une liste d'entités sous forme d'une collection de LabelValueBean. Le label et la valeur
     * correspondent à la valeur de l'attribut spécifié.
     *
     * @param aVo
     *            Value object contenant les critères de recherche.
     * @param aTable
     *            Le nom de la table.
     * @param aSort
     *            L'ordre de tri.
     * @param aAttribute
     *            L'attribut à lire.
     * @param aConnection
     *            Connexion pour exécuter la requête.
     *
     * @return Une collection contenant les LabelValueBean.
     *
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données. Un erreur au niveau de la base de données.
     */
    protected List<LabelValueBean> getPopupList(IValueObject aVo, String aTable, String aSort, String aAttribute, Connection aConnection)
            throws SQLException {
        StringBuilder sql = new StringBuilder(STRING_BUFFER_SIZE);

        sql.append("SELECT DISTINCT ");
        List<String> attributes = new ArrayList<>(1);
        attributes.add(aAttribute);
        addAttributesNames(aVo, attributes, null, sql, Mode.SELECT);
        sql.append(" FROM ");
        sql.append(aTable);
        if (!aVo.isEmpty()) {
            sql.append(" WHERE ");
            addClause(aVo, true, sql);
        }

        if (aSort != null) {
            sql.append(" ORDER BY " + aSort + " ASC");
        }

        // On exécute la requête et on stocke le résultat dans une Map
        List<LabelValueBean> list = new ArrayList<>(getInitListSize());

        try (PreparedStatement ps = getPreparedStatement(sql, aConnection)) {
            setRecord(aVo, ps, Mode.SELECT, null);

            // Exécute le reqête de recherche
            logger.debug("Exécute SQL: " + ps.toString());
            try (ResultSet rs = ps.executeQuery()) {
                String label;
                while (rs.next()) {
                    label = rs.getString(1);
                    list.add(new LabelValueBean(label, label));
                }
            }
        }
        return list;
    }

    /**
     * Calcule le nombre de record effectifs d'une requête de liste sans limitation de résultat.
     *
     * @param aVo
     *            Value object contenant les critères de recherche.
     * @param aConnection
     *            Connexion pour exécuter la requête.
     * @param aParameters
     *            dao parameters
     *
     * @return Nombre de records correspondant à la requête de liste.
     *
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données. Un erreur au niveau de la base de données.
     */
    protected IDAOResult getListCount(IValueObject aVo, Connection aConnection, DAOParameter... aParameters) throws SQLException {
        StringBuilder sql = getCountStatement(aVo, aParameters);
        String psStr = null;
        try (PreparedStatement ps = StatementFactory.getStatement(aConnection, sql.toString())) {
            // Utilise un PreparedStatement de type DebuggableStatement
            ps.setMaxRows(1);
            setRecord(aVo, ps, Mode.SELECT, null);
            // Exécute le reqête de recherche
            psStr = ps.toString();
            logger.debug("Exécute SQL: " + psStr);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return new DAOResult(NumberTools.getInteger(rs.getObject(1)));
            }
        }
    }

    /**
     * Lecture du timestamp d'une entité correspondant à l'identifiant spécifié.
     *
     * @param aTimestampName
     *            Le nom de l'attriut contenant le timestamp.
     * @param aTimestampType
     *            Type de propriété, ex. Timestamp ou Date
     * @param aIdName
     *            Le nom de l'attribut contenant l'ID.
     * @param aIdValue
     *            La valeur de l'ID.
     * @param aIDType
     *            Type de propriété, ex. Long
     * @param aTable
     *            Le nom de la table.
     * @param aConnection
     *            Connexion pour exécuter la requête.
     *
     * @return Un objet Timestamp ou null si pas trouvé.
     *
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données. Un erreur au niveau de la base de données.
     */
    protected IDAOResult getTimestamp(String aTimestampName, IValueObject.Type aTimestampType, String aIdName, Object aIdValue,
            IValueObject.Type aIDType, String aTable, Connection aConnection) throws SQLException {
        StringBuilder sql = new StringBuilder(STRING_BUFFER_SIZE);
        sql.append("SELECT ");
        sql.append(aTimestampName);
        sql.append(" FROM ");
        sql.append(aTable);
        sql.append(" WHERE ");
        sql.append(aIdName);
        sql.append("=?");
        Timestamp timestampValue = null;

        try (PreparedStatement ps = StatementFactory.getStatement(aConnection, sql.toString())) {
            // Utilise un PreparedStatement de type DebuggableStatement
            ps.setMaxRows(1);
            DAOTools.set(1, aIdValue, aIDType, AttributeType.EQUAL, ps);
            // Exécute le reqête de recherche
            logger.debug("Exécute SQL: " + ps.toString());
            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    if (aTimestampType == Type.TIMESTAMP) {
                        timestampValue = rs.getTimestamp(1);
                    } else if (aTimestampType == Type.LONG) {
                        timestampValue = new Timestamp(rs.getLong(1));
                    }
                }
            }
        }
        return new DAOResult(timestampValue);
    }

    /**
     * Crée un nouveau record pour une entité.
     *
     * @param aVo
     *            L'objet à créer.
     * @param aTable
     *            Le nom de la table.
     * @param aNotCreate
     *            Les attributs qu'in ne faut pas remplir.
     * @param aMajAttribute
     *            Le nom de l'attribut contenant l'auteur de la mise à jour.
     * @param aTimestampAttribute
     *            Le nom de l'attribut contenant la date de mise à jour.
     * @param aUser
     *            L'utilisateur qui demande l'ajout du record.
     * @param aConnection
     *            Connexion pour exécuter la requête.
     *
     * @return Le nombre d'enregistrements effectués.
     *
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données. Un erreur au niveau de la base de données.
     */
    protected IDAOResult create(IValueObject aVo, String aTable, Set<String> aNotCreate, String aMajAttribute, String aTimestampAttribute,
            ILoggedUser aUser, Connection aConnection) throws SQLException {
        return create(aVo, aTable, null, aNotCreate, aMajAttribute, aTimestampAttribute, aUser, aConnection);
    }

    // Ajouté la possibilité de donner la liste des champs à créer (besoins
    // de dynaplus)
    protected IDAOResult create(IValueObject aVo, String aTable, Set<String> aAttributeSet, Set<String> aNotCreate, String aMajAttribute,
            String aTimestampAttribute, ILoggedUser aUser, Connection aConnection) throws SQLException {
        // enregistre les renseignement sur le _srid, cette valeur est
        // nécessaire pour la création et la mise à jour des champs géométriques
        long srid = -1;
        if (aVo.getProperty("_srid") != null) {
            srid = (Long) aVo.getProperty("_srid");
        }

        Collection<String> toRemove = aVo.getOmit();
        StringBuilder sql = new StringBuilder(1024);
        sql.append("INSERT INTO ");
        sql.append(aTable);
        sql.append(" (");
        if (iHasAttributeNamesMappedToDBNames) {
            List<String> attributes = new ArrayList<>(aVo.getProperties().keySet());
            if (aAttributeSet != null) {
                attributes.retainAll(aAttributeSet);
            }

            Set<String> aNotCreateAttributeName = new HashSet<>();
            for (String aNotCreateDBname : aNotCreate) {
                aNotCreateAttributeName.add(getAttributeName(aNotCreateDBname));
            }
            if (!aNotCreateAttributeName.isEmpty()) {
                attributes.removeAll(aNotCreateAttributeName);
            }

            String aMajAttributeAttributeName = getAttributeName(aMajAttribute);
            if (toRemove != null) {
                toRemove.remove(aMajAttributeAttributeName);
                attributes.removeAll(toRemove);
            }
            if (aMajAttributeAttributeName != null) {
                aVo.setProperty(aMajAttributeAttributeName, aUser.getUserUpdateName());
            }
            // Garde que les attributs d'ont on connait le type.
            attributes.retainAll(aVo.getTypes().keySet());

            // On crée la requête SQL pour l'insertion

            Set<String> dbNamesAttributes = new LinkedHashSet<>();
            for (String attributeName : attributes) {
                dbNamesAttributes.add(getDBName(attributeName));
            }

            addAttributesNames(aVo, new ArrayList<>(dbNamesAttributes), aTimestampAttribute, sql, Mode.CREATE);
            sql.append(") VALUES (");
            addValues(aVo, dbNamesAttributes, sql, srid);
        } else {
            Set<String> attributes = aVo.getProperties().keySet();
            if (aAttributeSet != null) {
                attributes.retainAll(aAttributeSet);
            }
            if (aNotCreate != null) {
                attributes.removeAll(aNotCreate);
            }
            if (toRemove != null) {
                toRemove.remove(aMajAttribute);
                attributes.removeAll(toRemove);
            }
            if (aMajAttribute != null) {
                aVo.setProperty(aMajAttribute, aUser.getUserUpdateName());
            }
            // Garde que les attributs d'ont on connait le type.
            attributes.retainAll(aVo.getTypes().keySet());

            // On crée la requête SQL pour l'insertion
            addAttributesNames(aVo, new ArrayList<>(attributes), aTimestampAttribute, sql, Mode.CREATE);
            sql.append(") VALUES (");
            addValues(aVo, attributes, sql, srid);
        }

        // Insérer le temps selon le type du champ
        if (aTimestampAttribute != null) {
            if (aVo.getTypes().get(aTimestampAttribute).equals(IValueObject.Type.LONG)) {
                sql.append(",");
                sql.append(new java.util.Date().getTime());
            } else {
                sql.append(", ");
                sql.append(getDateTimeFunction());
            }
        }
        sql.append(")");

        PreparedStatement ps = null;
        try {
            // Utilise un PreparedStatement de type DebuggableStatement
            ps = StatementFactory.getStatement(aConnection, sql.toString());
            ps.setMaxRows(1);

            setRecord(aVo, ps, Mode.CREATE, null);

            // Exécute la requête de mise à jour
            logger.debug("Exécute SQL: " + ps.toString());
            int rowCount = ps.executeUpdate();

            IDAOResult result = new DAOResult();
            result.setStatus(rowCount > 0 ? Status.OK : Status.KO);
            result.setNbrRecords(rowCount);
            return result;
        } catch (SQLException e) {
            String psStr = null;
            if (ps != null) {
                psStr = ps.toString();
            }
            logger.error("SqlException: " + psStr, e);

            // Construit une exception chaînée avec la requête SQL
            SQLException ex = new SQLException(psStr, null, -1001);
            e.setNextException(ex);
            throw e;
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    /**
     * Retourne la prochaine valeur du ID en utilisant une sequence.
     *
     * @param aSequence
     *            Le nom de la sequence.
     * @param aIdType
     *            type de propriété, ex. Long
     * @param aConnection
     *            Connexion pour exécuter la requête.
     *
     * @return La valeur du prochain ID.
     *
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données. Un erreur au niveau de la base de données.
     */
    protected Object getNextId(String aSequence, IValueObject.Type aIdType, Connection aConnection) throws SQLException {
        if (aSequence == null || aSequence.length() == 0) {
            return null;
        }

        Object nextId = null;
        try (PreparedStatement ps = StatementFactory.getStatement(aConnection, String.format(getNextIdQuery(), aSequence))) {
            // Utilise un PreparedStatement de type DebuggableStatement
            ps.setMaxRows(1);

            // Exécute le reqête de recherche
            logger.debug("Exécute SQL: " + ps.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    nextId = DAOTools.getFromRS("next", aIdType, rs);
                }
            }
        }
        return nextId;
    }

    /**
     * Get the query to search the next ID.
     *
     * @return the query
     */
    protected String getNextIdQuery() {
        return "SELECT %1$s.NEXTVAL next FROM dual";
    }

    /**
     * Suppression d'un enregistrement.
     *
     * @param aTable
     *            Le nom de la table.
     * @param aIdName
     *            Le nom de l'attribut contenant l'ID.
     * @param aIdValue
     *            La valeur de l'ID.
     * @param aTimestName
     *            Le nom de l'attribut contenant le timestamp.
     * @param aTimestValue
     *            La valeur du timestamp.
     * @param aConnection
     *            Connexion pour exécuter la requête.
     *
     * @return Le nombre de records supprimés par la requête
     *
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données. Un erreur au niveau de la base de données.
     */
    protected IDAOResult delete(String aTable, String aIdName, Object aIdValue, String aTimestName, Timestamp aTimestValue,
            Connection aConnection) throws SQLException {
        return delete(aTable, aIdName, aIdValue, aTimestName, aTimestValue, null, aConnection);
    }

    protected IDAOResult delete(String aTable, String aIdName, Object aIdValue, String aTimestName, Timestamp aTimestValue,
            String aSecurityClause, Connection aConnection) throws SQLException {

        return delete(aTable, aIdName, aIdValue, aTimestName, aTimestValue, IValueObject.Type.TIMESTAMP, aSecurityClause, aConnection);
    }

    protected IDAOResult delete(String aTable, String aIdName, Object aIdValue, String aTimestName, Timestamp aTimestValue,
            IValueObject.Type aTimestType, String aSecurityClause, Connection aConnection) throws SQLException {

        StringBuilder sql = new StringBuilder(128);
        sql.append("DELETE FROM ");
        sql.append(aTable);
        sql.append(" WHERE ");
        addClauseDeleteUpdate(aIdName, aTimestName, aSecurityClause, sql);

        PreparedStatement ps = null;
        int rowCount = 0;

        try {
            // Utilise un PreparedStatement de type DebuggableStatement
            ps = StatementFactory.getStatement(aConnection, sql.toString());
            ps.setMaxRows(1);

            // Ajoute les valeurs des clauses
            addValuesDeleteUpdate(1, aIdValue, aTimestValue, ps, aTimestType);

            // Exécute la requête de mise à jour
            logger.debug("Exécute SQL: " + ps.toString());
            rowCount = ps.executeUpdate();

        } catch (SQLException e) {
            String psStr = null;
            if (ps != null) {
                psStr = ps.toString();
            }
            logger.error("SqlException: " + psStr, e);

            // Construit une exception chaînée avec la requête SQL
            SQLException ex = new SQLException(psStr, null, -1001);
            e.setNextException(ex);
            throw e;
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
        return new DAOResult(rowCount);
    }

    /**
     * Met à jour un enregistrement selon une collection de champs à mettre à jour et à un timestamp pour gérer la transaction longue.
     *
     * @param aUpdateFields
     *            Collection Map des champs à mettre à jour.
     * @param aNotUpdate
     *            Les attribut à pas mettre à jour.
     * @param aIdName
     *            Le nom de l'attribut contenant l'ID.
     * @param aIdValue
     *            La valeur de l'ID.
     * @param aTable
     *            Le nom de la table.
     * @param aTimestName
     *            Le nom de l'attribut contenant le timestamp.
     * @param aTimestValue
     *            La valeur du timestamp.
     * @param aTypes
     *            Les types des attributs de l'enregistrement.
     * @param aMajName
     *            Le nom de l'attribut contenant l'auteur de la mise à jour.
     * @param aMajType
     *            type du champ l'auteur de màj, p.ex. Long ou String
     * @param aUser
     *            Utilisateur qui demande la mise à jour.
     * @param aConnection
     *            Connexion pour exécuter la requête.
     *
     * @return Le nombre de records impliqués dans la mise à jour.
     *
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données. Un erreur au niveau de la base de données.
     */
    protected int update(Map<String, Object> aUpdateFields, Set<String> aNotUpdate, String aIdName, Object aIdValue, String aTable,
            String aTimestName, Timestamp aTimestValue, Map<String, IValueObject.Type> aTypes, String aMajName, IValueObject.Type aMajType,
            ILoggedUser aUser, Connection aConnection) throws SQLException {
        return update(aUpdateFields, null, aNotUpdate, aIdName, aIdValue, aTable, aTimestName, aTimestValue, aTypes, aMajName, aMajType,
                aUser, null, aConnection);
    }

    // On ajoute la notion de securityClause pour assurer les droits d'accès de
    // manière propre. On assure la backcompatibility.
    protected int update(Map<String, Object> aUpdateFields, Set<String> aNotUpdate, String aIdName, Object aIdValue, String aTable,
            String aTimestName, Timestamp aTimestValue, Map<String, IValueObject.Type> aTypes, String aMajName, IValueObject.Type aMajType,
            ILoggedUser aUser, String aSecurityClause, Connection aConnection) throws SQLException {
        return update(aUpdateFields, null, aNotUpdate, aIdName, aIdValue, aTable, aTimestName, aTimestValue, aTypes, aMajName, aMajType,
                aUser, aSecurityClause, aConnection);
    }

    protected int update(Map<String, Object> aUpdateFields, Set<String> fieldsToUpdate, Set<String> aNotUpdate, String aIdName,
            Object aIdValue, String aTable, String aTimestName, Timestamp aTimestValue, Map<String, IValueObject.Type> aTypes,
            String aMajName, IValueObject.Type aMajType, ILoggedUser aUser, Connection aConnection) throws SQLException {
        return update(aUpdateFields, fieldsToUpdate, aNotUpdate, aIdName, aIdValue, aTable, aTimestName, aTimestValue, aTypes, aMajName,
                aMajType, aUser, null, aConnection);
    }

    // Ajout d'un filtre sur les update qu'il faut effectivement réaliser
    // (nécessaire pour dynaplus)
    protected int update(Map<String, Object> aUpdateFields, Set<String> fieldsToUpdate, Set<String> aNotUpdate, String aIdName,
            Object aIdValue, String aTable, String aTimestName, Timestamp aTimestValue, Map<String, IValueObject.Type> aTypes,
            String aMajName, IValueObject.Type aMajType, ILoggedUser aUser, String aSecurityClause, Connection aConnection)
            throws SQLException {

        // prendre l'information du srid si existant
        long srid = -1;
        if (aUpdateFields.get("_srid") != null) {
            srid = (Long) aUpdateFields.get("_srid");
            // Suppression de ce champ qui n'est pas dans la base, uniquement
            // nécessaire à l'appelle de la fonciton
            aUpdateFields.remove("_srid");
        }

        if (iHasAttributeNamesMappedToDBNames) {
            // Create new aUpdateFields map with DB names
            Map<String, Object> aUpdateFieldsWithDBNames = new HashMap<>();
            for (Entry<String, Object> aUpdateFieldsEntry : aUpdateFields.entrySet()) {
                String attributeName = aUpdateFieldsEntry.getKey();
                Object value = aUpdateFieldsEntry.getValue();
                String dbName = getDBName(attributeName);

                aUpdateFieldsWithDBNames.put(dbName, value);
            }

            // Create new fieldsToUpdate set with DB names
            Set<String> fieldsToUpdateWithDBNames = new LinkedHashSet<>();
            for (String attributeName : fieldsToUpdate) {
                String dbName = getDBName(attributeName);

                fieldsToUpdateWithDBNames.add(dbName);
            }

            // S'il n'y a pas de champs à mettre à jour => rien à faire
            if (aUpdateFieldsWithDBNames.isEmpty()) {
                return 0;
            }
            // On crée la requête de mise à jour
            StringBuilder sql = new StringBuilder(STRING_BUFFER_SIZE);

            sql.append("UPDATE ");
            sql.append(aTable);
            sql.append(" SET ");

            // insérer le temps du dernier changement
            if (aTimestName != null && aTimestName.length() > 0) {
                sql.append(aTimestName);
                // Utilisation du ps en cas du type LONG
                if (aTypes.get(aTimestName).equals(IValueObject.Type.LONG)) {
                    sql.append("=?");
                } else {
                    sql.append('=');
                    sql.append(getDateTimeFunction());
                }
            }

            // Insérer l'utilisateur
            if (aUser != null && aUser.getUserUpdateName() != null && aMajName != null && aMajName.length() > 0) {
                sql.append(", ");
                sql.append(aMajName);
                sql.append("=?");
            }

            // On enlève les champs qui ne doivent pas être mis à jour et/ou la
            // date
            // et le nom de l'auteur de la mise à jour.
            if (aNotUpdate != null) {
                for (String nu : aNotUpdate) {
                    aUpdateFieldsWithDBNames.remove(nu);
                }
            }

            // On enlève les champs qui ne font pas partie du fieldToUpdate
            if (!fieldsToUpdateWithDBNames.isEmpty()) {
                Object[] keys = aUpdateFieldsWithDBNames.keySet().toArray();
                if (keys != null) {
                    for (int i = 0; i < keys.length; i++) {
                        if (!fieldsToUpdateWithDBNames.contains(keys[i])) {
                            aUpdateFieldsWithDBNames.remove(keys[i]);
                        }
                    }
                }
            }

            // D'abord on itère pour ajouter les champs à mettre à jour sans
            // leur
            // valeur
            for (Map.Entry<String, Object> me : aUpdateFieldsWithDBNames.entrySet()) {
                if (aTimestName != null && aTimestName.length() > 0
                        || aUser != null && aUser.getUserUpdateName() != null && aMajName != null && aMajName.length() > 0) {
                    sql.append(", ");
                }
                sql.append(me.getKey());
                if (aTypes.get(me.getKey()).equals(IValueObject.Type.SHAPE)) {
                    if (aUpdateFields.get(me.getKey()) == null) {
                        // Mise à null de la valeur, l'utilisation du setNull du
                        // driver ne fonctionne pas pour ce type de champ
                        sql.append("=NULL");
                        aUpdateFields.remove(me.getKey());
                    } else {
                        // insertion de la fonction pour un shape
                        sql.append("=sde.st_geomfromtext(?,");
                        sql.append(srid);
                        sql.append(")");
                    }
                } else {
                    // insertion uniquement de la valeur
                    sql.append("=?");
                }

            }

            sql.append(" WHERE ");
            addClauseDeleteUpdate(aIdName, aTimestName, aSecurityClause, sql);

            PreparedStatement ps = null;
            int rowCount = 0;

            try {
                // Utilise un PreparedStatement de type DebuggableStatement
                ps = StatementFactory.getStatement(aConnection, sql.toString());
                ps.setMaxRows(1);

                int idx = 1;

                // Insérer le temps de la dernière modification
                if (aTimestName != null && aTimestName.length() > 0 && aTypes.get(aTimestName).equals(IValueObject.Type.LONG)) {
                    idx = DAOTools.set(idx, new java.util.Date().getTime(), IValueObject.Type.LONG, AttributeType.EQUAL, ps);

                }

                // Insérer l'utilisateur qui a inséré
                if (aUser != null && aUser.getUserUpdateName() != null && aMajName != null && aMajName.length() > 0) {
                    idx = DAOTools.set(idx, aUser.getUserUpdateName(), aMajType, AttributeType.EQUAL, ps);
                }

                // On itère à nouveau pour placer les valeurs des champs à
                // mettre à
                // jour
                for (Map.Entry<String, Object> me : aUpdateFieldsWithDBNames.entrySet()) {
                    idx = DAOTools.set(idx, me.getValue(), aTypes.get(me.getKey()), AttributeType.EQUAL, ps);
                }

                addValuesDeleteUpdate(idx, aIdValue, aTimestValue, ps, aTypes.get(aTimestName));

                // Exécute la requête de mise à jour
                logger.debug("Exécute SQL: " + ps.toString());
                rowCount = ps.executeUpdate();

            } catch (SQLException e) {
                String psStr = null;
                if (ps != null) {
                    psStr = ps.toString();
                }
                logger.error("SqlException: " + psStr, e);

                // Construit une exception chaînée avec la requête SQL
                SQLException ex = new SQLException(psStr, null, -1001);
                e.setNextException(ex);
                throw e;
            } finally {
                if (ps != null) {
                    ps.close();
                }
            }
            return rowCount;
        }

        // S'il n'y a pas de champs à mettre à jour => rien à faire
        if (aUpdateFields.isEmpty()) {
            return 0;
        }
        // On crée la requête de mise à jour
        StringBuilder sql = new StringBuilder(STRING_BUFFER_SIZE);

        sql.append("UPDATE ");
        sql.append(aTable);
        sql.append(" SET ");

        // insérer le temps du dernier changement
        if (aTimestName != null && aTimestName.length() > 0) {
            sql.append(aTimestName);
            // Utilisation du ps en cas du type LONG
            if (aTypes.get(aTimestName).equals(IValueObject.Type.LONG)) {
                sql.append("=?");
            } else {
                sql.append('=');
                sql.append(getDateTimeFunction());
            }
        }

        // Insérer l'utilisateur
        if (aUser != null && aUser.getUserUpdateName() != null && aMajName != null && aMajName.length() > 0) {
            sql.append(", ");
            sql.append(aMajName);
            sql.append("=?");
        }

        // On enlève les champs qui ne doivent pas être mis à jour et/ou la date
        // et le nom de l'auteur de la mise à jour.
        if (aNotUpdate != null) {
            for (String nu : aNotUpdate) {
                aUpdateFields.remove(nu);
            }
        }

        // On enlève les champs qui ne font pas partie du fieldToUpdate
        if (fieldsToUpdate != null) {
            Object[] keys = aUpdateFields.keySet().toArray();
            if (keys != null) {
                for (int i = 0; i < keys.length; i++) {
                    if (!fieldsToUpdate.contains(keys[i])) {
                        aUpdateFields.remove(keys[i]);
                    }
                }
            }
        }

        // D'abord on itère pour ajouter les champs à mettre à jour sans leur
        // valeur
        for (Map.Entry<String, Object> me : aUpdateFields.entrySet()) {
            if (aTimestName != null && aTimestName.length() > 0
                    || aUser != null && aUser.getUserUpdateName() != null && aMajName != null && aMajName.length() > 0) {
                sql.append(", ");
            }
            sql.append(me.getKey());
            if (aTypes.get(me.getKey()).equals(IValueObject.Type.SHAPE)) {
                if (aUpdateFields.get(me.getKey()) == null) {
                    // Mise à null de la valeur, l'utilisation du setNull du
                    // driver ne fonctionne pas pour ce type de champs
                    sql.append("=NULL");
                    aUpdateFields.remove(me.getKey());
                } else {
                    // insertion de la fonction pour insérer un shape
                    sql.append("=sde.st_geomfromtext(?,");
                    sql.append(srid);
                    sql.append(")");
                }

            } else {
                // insertion uniquement de la valeur
                sql.append("=?");
            }
        }
        sql.append(" WHERE ");
        addClauseDeleteUpdate(aIdName, aTimestName, aSecurityClause, sql);

        PreparedStatement ps = null;
        int rowCount = 0;

        try {
            // Utilise un PreparedStatement de type DebuggableStatement
            ps = StatementFactory.getStatement(aConnection, sql.toString());
            ps.setMaxRows(1);

            int idx = 1;

            // Insérer le temps de la dernière modification
            if (aTimestName != null && aTimestName.length() > 0 && aTypes.get(aTimestName).equals(IValueObject.Type.LONG)) {
                idx = DAOTools.set(idx, new java.util.Date().getTime(), IValueObject.Type.LONG, AttributeType.EQUAL, ps);
            }

            // Insérer l'utilisateur qui a fait la modification
            if (aUser != null && aUser.getUserUpdateName() != null && aMajName != null && aMajName.length() > 0) {
                idx = DAOTools.set(idx, aUser.getUserUpdateName(), aMajType, AttributeType.EQUAL, ps);
            }

            // On itère à nouveau pour placer les valeurs des champs à mettre à
            // jour
            for (Map.Entry<String, Object> me : aUpdateFields.entrySet()) {
                idx = DAOTools.set(idx, me.getValue(), aTypes.get(me.getKey()), AttributeType.EQUAL, ps);
            }
            addValuesDeleteUpdate(idx, aIdValue, aTimestValue, ps, aTypes.get(aTimestName));

            // Exécute la requête de mise à jour
            logger.debug("Exécute SQL: " + ps.toString());
            rowCount = ps.executeUpdate();

        } catch (SQLException e) {
            String psStr = null;
            if (ps != null) {
                psStr = ps.toString();
            }
            logger.error("SqlException: " + psStr, e);

            // Construit une exception chaînée avec la requête SQL
            SQLException ex = new SQLException(psStr, null, -1001);
            e.setNextException(ex);
            throw e;
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
        return rowCount;
    }

    /**
     * Get the current date and time function name.
     *
     * @return the function name to call to give the current date and time in a sql request.
     */
    protected String getDateTimeFunction() {
        return "SYSTIMESTAMP";
    }

    /**
     * Construit la requète SQL finale dans deux étapes en utilisant un "prepared statement" :<br/>
     * 1. Ajoute des numéros d'index à chaque "?" (getPreparedStatement) <br/>
     * 2. Remplace les "?" par des vrai valeurs (setRecord) <br/>
     * Puis execute la requète
     *
     * @param aVos
     *            les valeurs à écrire
     * @param aSql
     *            le statement select
     * @param aAttributes
     *            les attributs à remplir dans les result vos
     * @param aVo
     *            vo vide pour créer les result vos
     * @param aMaxRow
     *            nbr maximale de résultats
     * @param aConnection
     *            connexion
     * @return liste de vos
     * @throws SQLException
     *             erreur bd
     */
    protected Collection<IValueObject> getList(List<IValueObject> aVos, StringBuilder aSql, Collection<String> aAttributes,
            IValueObject aVo, Integer aMaxRow, Connection aConnection) throws SQLException {

        // On exécute la requête et on stocke le résultat dans une collection
        PreparedStatement ps = null;
        IValueObject voEmpty = (IValueObject) aVo.clone();
        voEmpty.clear();
        Collection<IValueObject> list;
        if (iResultContainerFactory == null) {
            list = new ArrayList<>(getInitListSize());
        } else {
            list = iResultContainerFactory.createResultContainer();
        }

        try {
            // Utilise un PreparedStatement de type DebuggableStatement
            if (aMaxRow != null) {
                ps = getPreparedStatement(aSql, aMaxRow, aConnection);
            } else {
                ps = getPreparedStatement(aSql, aConnection);
            }

            setRecord(aVos, ps, Mode.SELECT, null);

            // Exécute le requête de recherche
            logger.debug("Exécute SQL: " + ps.toString());
            try (ResultSet rs = ps.executeQuery()) {
                List<String> columns = DAOTools.getColumns(rs);
                while (rs.next()) {
                    // Crée et remplit le Value Object pour ce record
                    // Ajoute le VO à la collection
                    list.add(fillRecord((IValueObject) voEmpty.clone(), aAttributes, rs, columns).getValueObject());
                }
            }
        } catch (SQLException e) {
            String psStr = null;
            if (ps != null) {
                psStr = ps.toString();
            }
            logger.error("SqlException: " + psStr, e);

            // Construit une exception chaînée avec la requête SQL
            SQLException ex = new SQLException(psStr, null, -1001);
            e.setNextException(ex);
            throw e;
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
        return list;
    }

    /**
     * Lecture d'une entité correspondante à l'identifiant spécifié depuis la base de données. Le record est retourné dans un value objet.
     *
     * @param aVo
     *            L'objet dans le quel le resultat va être retourné.
     * @param aAttributes
     *            Les attributs à lire.
     * @param aRs
     *            Le ResultSet.
     * @param aRsColumns
     *            The list of result set ccolumns, if <code>null</code> this method will get it from the metadata.
     *
     * @return Un value object correspondant aux paramètres.
     *
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données. Un erreur au niveau de la base de données.
     */
    protected IDAOResult fillRecord(IValueObject aVo, Collection<String> aAttributes, ResultSet aRs, List<String> aRsColumns)
            throws SQLException {

        Collection<String> attributes = aAttributes;

        if (iHasAttributeNamesMappedToDBNames) {
            if (attributes == null) {
                attributes = new HashSet<>();
                Set<String> attributeNames = aVo.getTypes().keySet();

                for (String attributeName : attributeNames) {
                    String dbNameOfAttribute = getDBName(attributeName);
                    if (dbNameOfAttribute == null) {
                        dbNameOfAttribute = attributeName;
                    }
                    attributes.add(dbNameOfAttribute);
                }
            }

            // Initialize the list of columns if not already done
            List<String> columns = aRsColumns;
            if (columns == null) {
                columns = DAOTools.getColumns(aRs);
            }

            String attributeName;
            for (String attribute : attributes) {
                // Treat case <tablename.fieldname>
                String fieldname = attribute;
                if (attribute.contains(".")) {
                    fieldname = attribute.substring(attribute.indexOf('.') + 1, fieldname.length());
                }
                if (!columns.contains(fieldname)) {
                    continue;
                }

                attributeName = getAttributeName(attribute);
                aVo.setProperty(attributeName, DAOTools.getFromRS(fieldname, aVo.getPropertyType(attributeName), aRs));
            }
            return new DAOResult(aVo);
        }

        if (attributes == null) {
            attributes = aVo.getTypes().keySet();
        }
        for (String attribute : attributes) {
            fillProperty(attribute, aVo, aRs);
        }
        return new DAOResult(aVo);
    }

    /**
     * Read a property from the database.
     *
     * @param aPropertyName
     *            the property name
     * @param aValueObject
     *            the value object to fill
     * @param aResultSet
     *            the database result set containing the value
     * @throws SQLException
     *             an eception reading from the database
     */
    protected void fillProperty(String aPropertyName, IValueObject aValueObject, ResultSet aResultSet) throws SQLException {
        aValueObject.setProperty(aPropertyName, DAOTools.getFromRS(aPropertyName, aValueObject.getPropertyType(aPropertyName), aResultSet));
    }

    /**
     * Ecriture d'une entité dans la base de données.
     *
     * @param aVo
     *            Les valeurs à écrir.
     * @param aPs
     *            Le PreparedStatement
     * @param aMode
     *            Information sur le mode sélection ou autres(création, update)
     * @param initIdx
     *            Premier index pour l'insertion de valeurs dans le prepared statement. Par défaut 1
     *
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données. Un erreur au niveau de la base de données.
     */
    protected void setRecord(IValueObject aVo, PreparedStatement aPs, Mode aMode, Integer initIdx) throws SQLException {

        List<IValueObject> vos = new ArrayList<>(1);
        vos.add(aVo);
        setRecord(vos, aPs, aMode, initIdx);
    }

    /**
     * Ecriture d'une entité dans la base de données.
     *
     * @param aVos
     *            Les valeurs à écrire.
     * @param aPs
     *            Le PreparedStatement
     * @param aMode
     *            Information sur le mode sélection ou autres(création, update)
     * @param initIdx
     *            Premier index pour l'insertion de valeurs dans le prepared statement. Par défaut 1.
     *
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données. Un erreur au niveau de la base de données.
     */
    protected void setRecord(List<IValueObject> aVos, PreparedStatement aPs, Mode aMode, Integer initIdx) throws SQLException {

        int idx = 1;
        if (initIdx != null && initIdx > 1) {
            idx = initIdx;
        }
        Object obj = null;

        for (IValueObject vo : aVos) {

            // Keys for replacing indexes with values

            List<String> keys = new ArrayList<>();

            // Keys from OR list
            List<Map<String, Object>> orList = vo.getOrList();
            int orLength = 0;
            int orIndex = 0;
            int statementLength = 0;
            int statementIndex = 0;

            if (!orList.isEmpty()) {
                for (Map<String, Object> orStatement : orList) {
                    keys.addAll(orStatement.keySet());

                }
                orLength = orList.size();
            }

            // Keys from children vos
            @SuppressWarnings("unchecked")
            Map<String, IValueObject> childrenmap = (Map<String, IValueObject>) vo.getProperty("childrenmap");
            if (childrenmap != null) {
                for (String child : childrenmap.keySet()) {
                    // On filtre
                    List<String> childrenKeys = new ArrayList<>(childrenmap.get(child).getProperties().keySet());
                    childrenKeys.retainAll(childrenmap.get(child).getTypes().keySet());
                    keys.addAll(childrenKeys);

                }
            }

            // Keys from master vo
            List<String> masterKeys = new ArrayList<>(vo.getProperties().keySet());
            masterKeys.retainAll(vo.getTypes().keySet());
            keys.addAll(masterKeys);

            for (String attribute : keys) {

                // Get object correspoding to key
                IValueObject voObj = vo;

                // From OR list
                if (orLength > 0 && orIndex < orLength) {
                    statementLength = orList.get(orIndex).size();
                }

                if (statementIndex < statementLength) {
                    obj = orList.get(orIndex).get(attribute);
                    statementIndex++;
                } else if (orIndex + 1 < orLength) {
                    obj = orList.get(orIndex + 1).get(attribute);
                    orIndex++;
                    statementIndex = 1;
                } else if (vo.getProperty(attribute) == null) {
                    // From children vo
                    voObj = getChildVO(attribute, childrenmap);
                    if (voObj != null) {
                        obj = voObj.getProperty(attribute);
                    }
                } else {
                    // From master vo
                    obj = vo.getProperty(attribute);
                }

                if (obj != null) {

                    String search = "";
                    if (voObj != null && voObj.getVOInfo() != null && voObj.getVOInfo().getAttribute(attribute) != null) {
                        search = (String) voObj.getVOInfo().getAttribute(attribute).getInfo("search");
                    }

                    if (aMode.equals(Mode.CREATE) || aMode.equals(Mode.UPDATE) || search.equals(AttributeType.EQUAL.toString())) {
                        idx = setValue(idx, obj, voObj != null ? voObj.getPropertyType(attribute) : null, AttributeType.EQUAL, aPs);
                        continue;
                    }
                    AttributeType searchOp = AttributeType.parse(search);
                    if (searchOp == null) {
                        searchOp = AttributeType.UPPER_FULL_LIKE;
                    }
                    idx = setValue(idx, obj, voObj != null ? voObj.getPropertyType(attribute) : null, searchOp, aPs);
                }
            }
        }
    }

    /**
     * Write a value in the database.
     *
     * @param aIdx
     *            the value index in the preparated statement
     * @param aValue
     *            the value
     * @param aType
     *            the property type
     * @param aSearchType
     *            the search type
     * @param aPreparedStatement
     *            the prepared statement
     * @return the next index to use
     * @throws SQLException
     *             exception writing the value in the database
     */
    protected int setValue(int aIdx, Object aValue, Type aType, DynamicDAO.AttributeType aSearchType, PreparedStatement aPreparedStatement)
            throws SQLException {
        return DAOTools.set(aIdx, aValue, aType, aSearchType, aPreparedStatement);
    }

    /**
     * Cherche le vo dans le childrenmap qui contient l'attribut
     *
     * @param aAttribute
     *            nom de l'attribut
     * @param aChildrenmap
     *            le map avec les critères de recherches des enfants
     * @return vo d'enfant qui contient l'attribut
     */
    private IValueObject getChildVO(String aAttribute, Map<String, IValueObject> aChildrenmap) {
        for (String childKey : aChildrenmap.keySet()) {
            IValueObject vo = aChildrenmap.get(childKey);
            if (vo.getProperty(aAttribute) != null) {
                return vo;
            }
        }
        return null;
    }

    /**
     * Lecture d'une entité correspondante à l'identifiant spécifié depuis la base de données. Le record est retourné dans un value objet.
     *
     * @param aIdName
     *            Le nom de l'attribut id.
     * @param aIdValue
     *            La valeur de l'id.
     * @param aSql
     *            La requête SQL.
     * @param aAttributes
     *            Les attributs à lire
     * @param aVo
     *            L'objet dans le quel le resultat va être retourné.
     * @param aConnection
     *            Connexion pour exécuter la requête.
     *
     * @return Un value object correspondant aux paramètres.
     *
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données. Un erreur au niveau de la base de données.
     */
    protected IDAOResult getRecord(String aIdName, Object aIdValue, StringBuilder aSql, Set<String> aAttributes, IValueObject aVo,
            Connection aConnection) throws SQLException {

        // On exécute la requête et on stocke le résultat dans un VO
        aVo.clear();
        try (PreparedStatement ps = getPreparedStatement(aSql, aConnection)) {
            // Utilise un PreparedStatement de type DebuggableStatement
            if (iHasAttributeNamesMappedToDBNames) {
                String attributeName = getAttributeName(aIdName);
                DAOTools.set(1, aIdValue, aVo.getPropertyType(attributeName), AttributeType.EQUAL, ps);
            } else {
                DAOTools.set(1, aIdValue, aVo.getPropertyType(aIdName), AttributeType.EQUAL, ps);
            }

            // Exécute le requête de recherche
            logger.debug("Exécute SQL: " + ps.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return new DAOResult(Status.NOTHING_TODO);
                }
                return fillRecord(aVo, aAttributes, rs, null);
            }
        }
    }

    /**
     * Ajoute le nom des attributs et celui du timestamp à la requête SQL.
     *
     * @param aVo
     *            vo avec critères de recherche
     * @param aAttributes
     *            attributs
     *
     * @param aTimestamp
     *            Le nom de l'attribut timestamp s'il faut le rajouter explicitement en dernier.
     * @param aSql
     *            La requête à la quelle ajouter les noms des attributs.
     * @param aMode
     *            le mode de la requète (SELECT ou CREATE)
     * @param aParameters
     *            Paramètre supplémentaire pas obligatoire
     */
    protected void addAttributesNames(IValueObject aVo, List<String> aAttributes, String aTimestamp, StringBuilder aSql, Mode aMode,
            DAOParameter... aParameters) {
        if (aAttributes == null || aAttributes.isEmpty()) {
            aSql.append("*");
        } else {

            if (aVo != null && aVo.getVOInfo() != null && aVo.getVOInfo().getTypes() != null
                    && aVo.getVOInfo().getTypes().containsValue(Type.SHAPE)) {

                if (aMode == Mode.CREATE) {
                    // Mode INSERT
                    addNames(aAttributes, aSql);
                } else {

                    // Mode UPDATE et SELECT
                    // Création de deux listes champ avec/sans shape
                    List<String> lstAttrStd = new ArrayList<>();
                    List<String> lstAttrShape = new ArrayList<>();

                    // Remplissage des listes
                    for (String att : aAttributes) {
                        Type tp = aVo.getVOInfo().getTypes().get(att);
                        if (tp.equals(Type.SHAPE)) {
                            lstAttrShape.add(att);
                        } else {
                            lstAttrStd.add(att);
                        }
                    }

                    // Ecriture des champs std
                    addNames(lstAttrStd, aSql);
                    // Ecriture des champs shape
                    addShapeName(lstAttrShape, aSql, !lstAttrStd.isEmpty(), aParameters);
                }

            } else {
                // Si pas de champ shape, passage de tous les noms de champs
                addNames(aAttributes, aSql);
            }

        }
        if (aTimestamp != null && aTimestamp.length() > 0) {
            aSql.append(", ");
            aSql.append(aTimestamp);
        }

    }

    /**
     * Ajoute des noms à la requête SQL.
     *
     * @param aNames
     *            Une collection de noms à rajouter.
     * @param aSql
     *            La requête à la quelle ajouter les noms.
     */
    protected void addNames(Collection<String> aNames, StringBuilder aSql) {

        Iterator<String> it = aNames.iterator();
        while (it.hasNext()) {
            aSql.append(it.next());
            if (it.hasNext()) {
                aSql.append(", ");
            }
        }
    }

    /**
     * Ajoute des noms à la requête SQL pour les champs SHAPE.
     *
     * @param aNames
     *            Une collection de noms à rajouter.
     * @param aSql
     *            La requête à la quelle ajouter les noms.
     * @param aIsPrevFields
     *            Information si il y a des noms de champs insérés avant
     * @param aParameters
     *            Paramètre supplémentaire pas obligatoire
     */
    protected void addShapeName(Collection<String> aNames, StringBuilder aSql, boolean aIsPrevFields,
            @SuppressWarnings("unused") DAOParameter... aParameters) {
        // Effectuée uniquement si la collection n'est pas vide
        if (!aNames.isEmpty()) {
            // Séparation entre les champs standard et shape
            if (aIsPrevFields) {
                aSql.append(", ");
            }

            // Iterator sur chaque champ
            Iterator<String> it = aNames.iterator();
            while (it.hasNext()) {
                String name = it.next();
                aSql.append("sde.st_astext(" + name + ") as " + name);
                if (it.hasNext()) {
                    aSql.append(", ");
                }
            }
        }
    }

    /**
     * Création d'un Prepareted Statement pour un requête donnée. Le Prepareted Statement est configuré avec la valeur cResultSetMaxRows.
     *
     * @param aSql
     *            La requête à executer avec le PS.
     * @param aConnection
     *            La connexion pour exécuter le PS.
     *
     * @return Le Prepareted Statement généré.
     *
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données. Un erreur au niveau de la base de données.
     */
    protected PreparedStatement getPreparedStatement(StringBuilder aSql, Connection aConnection) throws SQLException {

        PreparedStatement ps = StatementFactory.getStatement(aConnection, aSql.toString());
        if (iInstanceResultSetMaxRows != null) {
            ps.setMaxRows(iInstanceResultSetMaxRows);
        } else {
            ps.setMaxRows(cResultSetMaxRows);
        }
        return ps;
    }

    /**
     * Création d'un Prepareted Statement pour un requête donnée.
     *
     * @param aSql
     *            La requête à executer avec le PS.
     * @param aMaxRow
     *            Nombre maximale de résultats
     * @param aConnection
     *            La connexion pour exécuter le PS.
     * @return Le Prepareted Statement généré.
     * @throws SQLException
     *             En cas de problème au niveau de la requête à la base de données. Un erreur au niveau de la base de données.
     */
    protected PreparedStatement getPreparedStatement(StringBuilder aSql, int aMaxRow, Connection aConnection) throws SQLException {

        PreparedStatement ps = StatementFactory.getStatement(aConnection, aSql.toString());
        ps.setMaxRows(aMaxRow);
        return ps;
    }

    /**
     * Ajoute des points d'interrogation à la requête SQL.
     *
     * @param aVo
     *            vo pour information du type
     * @param aAttributes
     *            Liste des attributs
     * @param aSql
     *            La requête à la quelle ajouter les attributs.
     * @param aSrid
     *            srid valeur nécessaire pour fonction géométrique
     */
    protected void addValues(IValueObject aVo, Set<String> aAttributes, StringBuilder aSql, long aSrid) {
        boolean first = true;
        // vérification si des champs sont avec une geometrie
        for (String att : aAttributes) {
            boolean isShape = false;
            if (aVo.getVOInfo() != null && aVo.getVOInfo().getTypes() != null && aVo.getVOInfo().getTypes().get(att) != null) {
                isShape = aVo.getVOInfo().getTypes().get(att).equals(Type.SHAPE);
            }
            if (first) {
                first = false;
            } else {
                aSql.append(',');
            }
            addValue(isShape, aSql, aSrid);
        }
    }

    /**
     * Add the interrogation marks to the SQL request for a field.
     *
     * @param aIsShape
     *            <code>true</code> if it is a SDE SHAPE geometry field
     * @param aSql
     *            the sql request
     * @param aSrid
     *            the SRID value for the geometry function
     */
    protected void addValue(boolean aIsShape, StringBuilder aSql, long aSrid) {
        if (aIsShape) {
            // If the field is a SDE SHAPE geometry
            aSql.append("sde.st_geomfromtext(?,");
            aSql.append(aSrid);
            aSql.append(")");
        } else {
            aSql.append("?");
        }
    }

    /**
     * Ajoute les contitions pour le delete et le update (id et SYSTIMESTAMP) à la requête SQL.
     *
     * @param aIdName
     *            Le nom de l'id.
     * @param aTimestName
     *            Le nom du timestamp.
     * @param aSecurityClause
     *            filtre de sécurité
     * @param aSql
     *            La requête à la quelle ajouter la condition.
     */
    protected void addClauseDeleteUpdate(String aIdName, String aTimestName, String aSecurityClause, StringBuilder aSql) {

        DAOTools.addClause(aIdName, null, AttributeType.EQUAL, aSql);
        if (aTimestName != null) {
            aSql.append(" AND ");
            DAOTools.addClause(aTimestName, null, AttributeType.EQUAL, aSql);
        }
        if (aSecurityClause != null) {
            aSql.append(" AND ");
            aSql.append(aSecurityClause);
        }
    }

    /**
     * Crée une clause join: table 1 AND table 2
     *
     * @param aJoin
     *            set de noms de tables
     * @return la clause join
     */
    protected String joinClause(Set<String> aJoin) {

        StringBuilder join = new StringBuilder(128);
        if (aJoin != null) {
            Iterator<String> it = aJoin.iterator();
            while (it.hasNext()) {
                join.append(it.next());
                if (it.hasNext()) {
                    join.append(" AND ");
                }
            }
        }
        return join.length() == 0 ? null : join.toString();
    }

    /**
     * Ajoute des conditions à la requête SQL (PreparedStatement).
     *
     * @param aVo
     *            Le VO contenant les conditions à ajouter.e.
     * @param aFirst
     *            Indique s'il s'agit de la première clause.
     * @param aSql
     *            La requête à la quelle ajouter les conditions.
     *
     * @return True si aucune la prochaine cause est la première.
     */
    protected boolean addClause(IValueObject aVo, boolean aFirst, StringBuilder aSql) {

        boolean first = aFirst;

        // Retenir les champs utilisé dans la clause WHERE
        List<String> keys = new ArrayList<>(aVo.getProperties().keySet());
        keys.retainAll(aVo.getTypes().keySet());

        Iterator<String> it = keys.iterator();
        String attribute;

        if (!first && !keys.isEmpty()) {
            aSql.append(" AND ");
        }
        first = keys.isEmpty();

        while (it.hasNext()) {
            attribute = it.next();
            if (!"childrenmap".equals(attribute)) {

                String search = "";
                if (aVo.getVOInfo() != null && aVo.getVOInfo().getAttribute(attribute) != null) {
                    search = (String) aVo.getVOInfo().getAttribute(attribute).getInfo("search");
                }

                if (iHasAttributeNamesMappedToDBNames) {
                    String dbNameOfAttribute = getDBName(attribute);

                    if (dbNameOfAttribute == null) {
                        dbNameOfAttribute = attribute;
                    }

                    selectAttType(search, attribute, dbNameOfAttribute, aVo, aSql);

                } else {
                    selectAttType(search, attribute, attribute, aVo, aSql);
                }

                if (it.hasNext()) {
                    aSql.append(" AND ");
                }
            }
        }

        return first;
    }

    /**
     * Chois du addClause selon le type de l'attribut
     *
     * @param aSearch
     *            Type de recherche de l'attribut
     * @param aAttribute
     *            Nom de l'attribut
     * @param aAttributeDb
     *            Nom de l'attribut si mapping différent db et fichiers de configuration, autrement mettre la même valeur que aAttribute
     * @param aVo
     *            vo contentant les informations
     * @param aSql
     *            Requête SQL complète
     */
    private void selectAttType(String aSearch, String aAttribute, String aAttributeDb, IValueObject aVo, StringBuilder aSql) {
        AttributeType search = AttributeType.parse(aSearch);
        if (aVo.getProperty(aAttribute) instanceof String && search == null) {
            // Traitement pour les attributs strings
            search = AttributeType.UPPER_FULL_LIKE;
        } else if (search == null || aVo.getProperty(aAttribute) instanceof Long && !AttributeType.LIKE.toString().equals(aSearch)) {
            search = AttributeType.EQUAL;
        }
        DAOTools.addClause(aAttributeDb, aVo.getProperty(aAttribute), search, aSql);
    }

    /**
     * Ajoute des conditions à la requête SQL (PreparedStatement).
     *
     * @param aVos
     *            Les VOs contenant les conditions à ajouter.
     * @param aSql
     *            La requête à la quelle ajouter les conditions.
     */
    protected void addClause(Collection<IValueObject> aVos, StringBuilder aSql) {

        boolean first = true;

        for (IValueObject vo : aVos) {
            first = addClause(vo, first, aSql);
        }
    }

    /**
     * Ajoute les valeurs des contitions pour le delete et le update (id et SYSTIMESTAMP) à la requête SQL.
     *
     * @param aIdx
     *            L'index de l'id dans le Preparated Statement, le timestamp doit être le suivant.
     * @param aIdValue
     *            La valeur de l'id dans le Preparated Statement.
     * @param aTimestValue
     *            La valeur du timestamp dans le Preparated Statement.
     * @param aPs
     *            Le Preparated Statement.
     * @param aTimeStTypes
     *            Type de propriété, ex. Timestamp ou Date
     *
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données. Un erreur au niveau de la base de données.
     */
    protected void addValuesDeleteUpdate(int aIdx, Object aIdValue, Timestamp aTimestValue, PreparedStatement aPs,
            IValueObject.Type aTimeStTypes) throws SQLException {
        // id
        int idx = aIdx;
        IValueObject.Type type = null;
        if (aIdValue instanceof String) {
            type = IValueObject.Type.STRING;
        } else if (aIdValue instanceof Long) {
            type = IValueObject.Type.LONG;
        } else {
            throw new UnsupportedOperationException("Type de l'id '" + aIdValue.getClass().getName() + "'non supporté");
        }
        idx = DAOTools.set(idx, aIdValue, type, AttributeType.EQUAL, aPs);

        // Timestamp
        if (aTimestValue != null) {
            if (aTimeStTypes.equals(IValueObject.Type.LONG)) {
                aPs.setLong(idx, aTimestValue.getTime());
            } else {
                aPs.setTimestamp(idx, aTimestValue);
            }

        }
    }

    /**
     * Retourne la chaine de tri pour des valeurs numériques contenus dans un attributs VARCHAR en une direction donnée.
     *
     * @param aAttribute
     *            Le nom de l'attribute de tri.
     * @param aSortOrientation
     *            La direction de tri.
     *
     * @return La chaine de tri pour des valeurs numériques contenus dans un attributs VARCHAR.
     */
    protected String sortStringAsNumber(String aAttribute, ListHandler.Sort aSortOrientation) {

        StringBuilder sort = new StringBuilder(256);
        sort.append("TO_NUMBER(RTRIM(SUBSTR(");
        sort.append(aAttribute);
        sort.append(", 1, INSTR(TRANSLATE (UPPER(");
        sort.append(aAttribute);
        sort.append(")||' ' ," + " 'QWERTZUIOPASDFGHJKLYXCVBNM,.-;:_ÉÀÈÖÜÄ{}[]$£*çTa$÷SúF\"n!º''^¦+" + "\"*t%&/()=?`~\\',"
                + " '                                                               " + "            ' ) ," + " ' ')-1))) ");
        if (aSortOrientation == ListHandler.Sort.DESCENDING) {
            sort.append("DESC");
        } else {
            sort.append("ASC");
        }
        sort.append(", REPLACE(");
        sort.append(aAttribute);
        sort.append(",' ')");
        return sort.toString();
    }

    /**
     *
     * @return La taille initiale d'une liste de resultats.
     */
    public int getInitListSize() {

        if (cResultSetMaxRows > 100) {
            return cResultSetMaxRows / 10;
        }
        return 500;
    }

    /**
     * Retourne le nombre maximale de lignes à lire.
     *
     * @return Le nombre maximale de lignes à lire.
     */
    public static int getResultSetMaxRows() {

        return cResultSetMaxRows;
    }

    /**
     * Défini le nombre maximale de lignes à lire.
     *
     * Valeurs négatives non autorisées...laisse à 0 au cas où
     *
     * @param aResultSetMaxRows
     *            Le nombre maximale de lignes à lire.
     */
    public static void setResultSetMaxRows(int aResultSetMaxRows) {
        if (aResultSetMaxRows < 0) {
            cResultSetMaxRows = 0;
        } else {
            cResultSetMaxRows = aResultSetMaxRows;
        }
    }

    /**
     * Retourne le nombre maximale de lignes à lire pour cette instance.
     *
     * @return Le nombre maximale de lignes à lire.
     */
    public Integer getInstanceResultSetMaxRows() {
        return iInstanceResultSetMaxRows;
    }

    /**
     * Défini le nombre maximale de lignes à lire pour cette instance. Valeurs négatives non autorisées...laisse à null au cas où
     *
     * @param anInstanceResultSetMaxRows
     *            Le nombre maximale de lignes à lire.
     */
    public void setInstanceResultSetMaxRows(Integer anInstanceResultSetMaxRows) {
        if (anInstanceResultSetMaxRows != null && anInstanceResultSetMaxRows < 0) {
            iInstanceResultSetMaxRows = null;
        } else {
            iInstanceResultSetMaxRows = anInstanceResultSetMaxRows;
        }
    }

    /**
     * Obtenition de la liste des valeurs d'un champ sur la base de la requête fournie
     *
     * @param aVo
     *            restriction pour la mise à jour (uniquement les champs de la table principale)
     * @param aTableName
     *            nom de la table
     * @param aFieldName
     *            le nom du champ à modifier
     * @param aConnection
     *            une connection à utiliser
     * @param aParameters
     *            DAOParameters
     * @return la lista des valeurs
     * @Deprecation: add the tablename as a DAOParameter (Name.TABLE_NAMES, Set<String>) instead of passing it as an argument and call
     *               getFieldsRequest(aVo, aFieldName, aConnection, aParameters) directly
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    @Deprecated
    public IDAOResult getFieldsRequest(IValueObject aVo, String aTableName, String aFieldName, Connection aConnection,
            DAOParameter... aParameters) throws SQLException {
        Set<String> tableNames = new HashSet<>();
        tableNames.add(aTableName);
        List<DAOParameter> list = new ArrayList<>();
        if (aParameters != null) {
            list.addAll(Arrays.asList(aParameters));
        }
        list.add(new DAOParameter(Name.TABLE_NAMES, tableNames));
        return getFieldsRequest(aVo, aFieldName, aConnection, list.toArray(new DAOParameter[list.size()]));
    }

    /**
     * Obtenition de la liste des valeurs d'un champ sur la base de la requête fournie
     *
     * @param aVo
     *            restriction pour la mise à jour (uniquement les champs de la table principale)
     * @param aFieldName
     *            le nom du champ à modifier
     * @param aConnection
     *            une connection à utiliser
     * @param aParameters
     *            DAOParameters avec noms de tables, security clause etc.
     * @return la lista des valeurs
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    @SuppressWarnings("unchecked")
    public IDAOResult getFieldsRequest(IValueObject aVo, String aFieldName, Connection aConnection, DAOParameter... aParameters)
            throws SQLException {
        List<Object> list = new ArrayList<>();
        // On crée la requête de mise à jour
        StringBuilder sql = new StringBuilder(STRING_BUFFER_SIZE);

        sql.append("select ");
        if (aVo.getProperty("DISTINCT") != null) {
            sql.append("DISTINCT ");
        }
        sql.append(aFieldName);
        sql.append(" from ");
        addNames((Collection<String>) DAOParameter.getValue(Name.TABLE_NAMES, aParameters), sql);

        sql.append(getListWhere(aVo, aParameters));
        sql.append(getListSort(aParameters));

        try (PreparedStatement ps = StatementFactory.getStatement(aConnection, sql.toString())) {

            setRecord(aVo, ps, Mode.SELECT, null);

            // Exécute la requête de mise à jour
            logger.debug("Exécute SQL: " + ps.toString());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(DAOTools.getFromRS(aFieldName, aVo.getPropertyType(aFieldName), rs));
                }
            }
        }
        return new DAOResult(list);
    }

    /**
     * Retourne un champ donné pour un identifiant donné
     */
    protected IDAOResult getField(String aIdName, Object aIdValue, String aTable, String aFieldName, IValueObject.Type aType,
            IValueObject.Type aTypeForFieldName, Connection aConnection) throws SQLException {
        return getField(aIdName, aIdValue, aTable, aFieldName, aType, aTypeForFieldName, null, aConnection);
    }

    protected IDAOResult getField(String aIdName, Object aIdValue, String aTable, String aFieldName, IValueObject.Type aType,
            IValueObject.Type aTypeForFieldName, String aSecurityClause, Connection aConnection) throws SQLException {
        if (aIdValue == null) {
            return new DAOResult(Status.NOTHING_TODO);
        }
        // On crée la requête SQL selon les critères de recherche existants
        StringBuilder sql = new StringBuilder(STRING_BUFFER_SIZE);
        sql.append("SELECT ");
        sql.append(aFieldName);
        sql.append(" FROM ");
        sql.append(aTable);
        sql.append(" WHERE ");
        DAOTools.addClause(aIdName, null, AttributeType.EQUAL, sql);
        if (aSecurityClause != null) {
            sql.append(" AND ");
            sql.append(aSecurityClause);
        }
        try (PreparedStatement ps = getPreparedStatement(sql, aConnection)) {
            DAOTools.set(1, aIdValue, aType, AttributeType.EQUAL, ps);

            // Exécute la requête
            logger.debug("Exécute SQL: " + ps.toString());
            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    return new DAOResult(Status.NOTHING_TODO);
                }
                return new DAOResult(DAOTools.getFromRS(aFieldName, aTypeForFieldName, rs));
            }
        }
    }

    /**
     * Aggregationn d'un champ sur la base de la requête fournie
     *
     * @param aVo
     *            restriction pour le calcul de l'aggregation (uniquement les champs de la table principale)
     * @param aTableName
     *            nom de table
     * @param aFieldName
     *            le nom du champ à modifier
     * @param aggr
     *            aggregateur à utiliser
     * @param aConnection
     *            une connection à utiliser
     * @return le max du champ
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public IDAOResult getAggregateField(IValueObject aVo, String aTableName, String aFieldName, Aggregator aggr, Connection aConnection)
            throws SQLException {
        // On crée la requête de mise à jour
        StringBuilder sql = new StringBuilder(STRING_BUFFER_SIZE);

        Set<String> principalFields = aVo.getVOInfo().getNames(aVo.getVOInfo().getTable());
        principalFields.add("ADDITIONAL_STATEMENT");
        // On conserve uniquement les champs de la table principal
        aVo.getProperties().keySet().retainAll(principalFields);
        sql.append("SELECT ");
        sql.append(aggr);
        sql.append("(");
        sql.append(aFieldName);
        sql.append(") FROM ");
        sql.append(aTableName);
        // Si on a des clauses (séléctions et/ou joitures)
        if (!aVo.isEmpty() || !aVo.getOrList().isEmpty()) {
            sql.append(" WHERE ");
            boolean isFirst = true;
            if (aVo.getProperty("ADDITIONAL_STATEMENT") != null) {
                isFirst = false;
                sql.append(aVo.getProperty("ADDITIONAL_STATEMENT"));
                aVo.removeProperty("ADDITIONAL_STATEMENT");
            }
            if (!aVo.getOrList().isEmpty()) {
                // OR clause
                addORClause(aVo, aVo.getOrList(), isFirst, sql);
                isFirst = false;
            }
            if (!aVo.isEmpty()) {
                if (!isFirst) {
                    sql.append(" AND ");
                }
                isFirst = false;
                // Master clause
                addClause(aVo, true, sql);
            }
        }

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            // Utilise un PreparedStatement de type DebuggableStatement
            ps = StatementFactory.getStatement(aConnection, sql.toString());
            ps.setMaxRows(1);

            setRecord(aVo, ps, Mode.UPDATE, null);

            // Exécute la requête de mise à jour
            logger.debug("Exécute SQL: " + ps.toString());
            rs = ps.executeQuery();
            if (rs.next()) {
                return new DAOResult(rs.getObject(1));
            }

        } catch (SQLException e) {
            String psStr = null;
            if (ps != null) {
                psStr = ps.toString();
            }
            logger.error("SqlException: " + psStr, e);

            // Construit une exception chaînée avec la requête SQL
            SQLException ex = new SQLException(psStr, null, -1001);
            e.setNextException(ex);
            throw e;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
        return new DAOResult(Status.NONE);
    }

    /**
     * Mise un jour d'un champ avec la valeur fournie
     *
     * @param aIdName
     *            nom du champ ID
     * @param aIdValue
     *            la valeur de l'id du record à modifier
     * @param anIdType
     *            Type de propriété, ex. Long
     * @param aTable
     *            le nom de la table
     * @param aFieldName
     *            le nom du champ à modifier
     * @param aValue
     *            la valeur à modifier
     * @param aType
     *            le type du champ à modifier
     * @param aConnection
     *            une connection à utiliser
     * @return le nombre d'enregistrements modifiés
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public IDAOResult updateField(String aIdName, Object aIdValue, IValueObject.Type anIdType, String aTable, String aFieldName,
            Object aValue, IValueObject.Type aType, Connection aConnection) throws SQLException {
        // On crée la requête de mise à jour
        StringBuilder sql = new StringBuilder(STRING_BUFFER_SIZE);

        sql.append("UPDATE ");
        sql.append(aTable);
        sql.append(" SET ");
        sql.append(aFieldName);
        sql.append("=?");
        sql.append(" WHERE ");
        DAOTools.addClause(aIdName, null, AttributeType.EQUAL, sql);
        if (!(aType.equals(IValueObject.Type.BLOB) || aType.equals(IValueObject.Type.CLOB) || aType.equals(IValueObject.Type.SHAPE))) {
            sql.append(" AND ");
            if (aValue != null) {
                sql.append("(");
                sql.append(aFieldName);
                sql.append("!=? or ");
                sql.append(aFieldName);
                sql.append(" is null)");
            } else {
                sql.append(aFieldName);
                sql.append(" is not null");
            }
        }

        PreparedStatement ps = null;
        int rowCount = 0;

        try {
            // Utilise un PreparedStatement de type DebuggableStatement
            ps = StatementFactory.getStatement(aConnection, sql.toString());
            ps.setMaxRows(1);

            int idx = 1;
            idx = DAOTools.set(idx, aValue, aType, AttributeType.EQUAL, ps);
            idx = DAOTools.set(idx, aIdValue, anIdType, AttributeType.EQUAL, ps);
            if (aValue != null && !(aType.equals(IValueObject.Type.BLOB) || aType.equals(IValueObject.Type.CLOB)
                    || aType.equals(IValueObject.Type.SHAPE))) {
                idx = DAOTools.set(idx, aValue, aType, AttributeType.EQUAL, ps);
            }

            // Exécute la requête de mise à jour
            logger.debug("Exécute SQL: " + ps.toString());
            rowCount = ps.executeUpdate();

        } catch (SQLException e) {
            String psStr = null;
            if (ps != null) {
                psStr = ps.toString();
            }
            logger.error("SqlException: " + psStr, e);

            // Construit une exception chaînée avec la requête SQL
            SQLException ex = new SQLException(psStr, null, -1001);
            e.setNextException(ex);
            throw e;
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
        return new DAOResult(rowCount);
    }

    /**
     * Mise un jour d'un champ sur la base de la requête fournie
     *
     * @param aVo
     *            restriction pour la mise à jour (uniquement les champs de la table principale)
     * @param aTableName
     *            le nom de la table
     * @param aFieldName
     *            le nom du champ à modifier
     * @param aValue
     *            la valeur à modifier
     * @param aType
     *            le type du champ à modifier
     * @param aConnection
     *            une connection à utiliser
     * @return le nombre d'enregistrements modifiés
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public IDAOResult updateFieldRequest(IValueObject aVo, String aTableName, String aFieldName, Object aValue, IValueObject.Type aType,
            Connection aConnection) throws SQLException {
        // On crée la requête de mise à jour
        StringBuilder sql = new StringBuilder(STRING_BUFFER_SIZE);

        Set<String> principalFields = aVo.getVOInfo().getNames(aVo.getVOInfo().getTable());
        principalFields.add("ADDITIONAL_STATEMENT");
        // On conserve uniquement les champs de la table principal
        aVo.getProperties().keySet().retainAll(principalFields);
        sql.append("UPDATE ");
        sql.append(aTableName);
        sql.append(" SET ");
        sql.append(aFieldName);
        sql.append("=?");
        // Si on a des clauses (séléctions et/ou joitures)
        if (!aVo.isEmpty() || !aVo.getOrList().isEmpty()) {
            sql.append(" WHERE ");
            boolean isFirst = true;
            if (aVo.getProperty("ADDITIONAL_STATEMENT") != null) {
                isFirst = false;
                sql.append(aVo.getProperty("ADDITIONAL_STATEMENT"));
                aVo.removeProperty("ADDITIONAL_STATEMENT");
            }
            if (!aVo.getOrList().isEmpty()) {
                // OR clause
                addORClause(aVo, aVo.getOrList(), isFirst, sql);
                isFirst = false;
            }
            if (!aVo.isEmpty()) {
                if (!isFirst) {
                    sql.append(" AND ");
                }
                isFirst = false;
                // Master clause
                addClause(aVo, true, sql);
            }
        }

        PreparedStatement ps = null;
        int rowCount = 0;

        try {
            // Utilise un PreparedStatement de type DebuggableStatement
            ps = StatementFactory.getStatement(aConnection, sql.toString());

            int idx = 1;
            idx = DAOTools.set(idx, aValue, aType, AttributeType.EQUAL, ps);

            setRecord(aVo, ps, Mode.UPDATE, 2);

            // Exécute la requête de mise à jour
            logger.debug("Exécute SQL: " + ps.toString());
            rowCount = ps.executeUpdate();

        } catch (SQLException e) {
            String psStr = null;
            if (ps != null) {
                psStr = ps.toString();
            }
            logger.error("SqlException: " + psStr, e);

            // Construit une exception chaînée avec la requête SQL
            SQLException ex = new SQLException(psStr, null, -1001);
            e.setNextException(ex);
            throw e;
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
        return new DAOResult(rowCount);
    }

    /**
     * Mise un jour d'un champ sur la base de la requête fournie
     *
     * @param aVo
     *            restriction pour la mise à jour (uniquement les champs de la table principale)
     * @param aTableName
     *            le nom de la table
     * @param aFieldNames
     *            nom des champs à modifier
     * @param aValues
     *            les valeurs à modifier
     * @param aTypes
     *            les types des champs à modifier
     * @param aConnection
     *            une connection à utiliser
     * @return le nombre d'enregistrements modifiés
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public int updateFieldsRequest(IValueObject aVo, String aTableName, String[] aFieldNames, Object[] aValues, IValueObject.Type[] aTypes,
            Connection aConnection) throws SQLException {
        // On crée la requête de mise à jour
        StringBuilder sql = new StringBuilder(STRING_BUFFER_SIZE);

        Set<String> principalFields = aVo.getVOInfo().getNames(aVo.getVOInfo().getTable());
        principalFields.add("ADDITIONAL_STATEMENT");
        // On concerve uniquement les champs de la table principal
        aVo.getProperties().keySet().retainAll(principalFields);
        sql.append("UPDATE ");
        sql.append(aTableName);
        sql.append(" SET ");
        for (String str : aFieldNames) {
            sql.append(str);
            sql.append("=?,");
        }
        sql.setLength(sql.length() - 1);
        // Si on a des clauses (séléctions et/ou joitures)
        if (!aVo.isEmpty() || !aVo.getOrList().isEmpty()) {
            sql.append(" WHERE ");
            boolean isFirst = true;
            if (aVo.getProperty("ADDITIONAL_STATEMENT") != null) {
                if (!isFirst) {
                    sql.append(" AND ");
                }
                isFirst = false;
                sql.append(aVo.getProperty("ADDITIONAL_STATEMENT"));
                aVo.removeProperty("ADDITIONAL_STATEMENT");
            }
            if (!aVo.getOrList().isEmpty()) {
                // OR clause
                addORClause(aVo, aVo.getOrList(), isFirst, sql);
                isFirst = false;
            }
            if (!aVo.isEmpty()) {
                if (!isFirst) {
                    sql.append(" AND ");
                }
                isFirst = false;
                // Master clause
                addClause(aVo, true, sql);
            }
        }

        PreparedStatement ps = null;
        int rowCount = 0;

        try {
            // Utilise un PreparedStatement de type DebuggableStatement
            ps = StatementFactory.getStatement(aConnection, sql.toString());

            int idx = 1;
            for (Object obj : aValues) {
                idx = DAOTools.set(idx, obj, aTypes[idx - 1], AttributeType.EQUAL, ps);
            }

            setRecord(aVo, ps, Mode.UPDATE, idx);

            // Exécute la requête de mise à jour
            logger.debug("Exécute SQL: " + ps.toString());
            rowCount = ps.executeUpdate();

        } catch (SQLException e) {
            String psStr = null;
            if (ps != null) {
                psStr = ps.toString();
            }
            logger.error("SqlException: " + psStr, e);

            // Construit une exception chaînée avec la requête SQL
            SQLException ex = new SQLException(psStr, null, -1001);
            e.setNextException(ex);
            throw e;
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
        return rowCount;
    }

    public IDAOResult updateFields(String aIdName, Object aIdValue, IValueObject.Type anIdType, String aTable, String[] aFieldNames,
            Object[] aValues, IValueObject.Type[] aTypes, Connection aConnection) throws SQLException {
        // On crée la requête de mise à jour
        StringBuilder sql = new StringBuilder(STRING_BUFFER_SIZE);

        sql.append("UPDATE ");
        sql.append(aTable);
        sql.append(" SET ");
        for (String str : aFieldNames) {
            sql.append(str);
            sql.append("=?,");
        }
        sql.setLength(sql.length() - 1);
        sql.append(" WHERE ");
        DAOTools.addClause(aIdName, null, AttributeType.EQUAL, sql);

        PreparedStatement ps = null;
        int rowCount = 0;

        try {
            // Utilise un PreparedStatement de type DebuggableStatement
            ps = StatementFactory.getStatement(aConnection, sql.toString());
            ps.setMaxRows(1);

            int idx = 1;
            for (Object obj : aValues) {
                idx = DAOTools.set(idx, obj, aTypes[idx - 1], AttributeType.EQUAL, ps);
            }
            idx = DAOTools.set(idx, aIdValue, anIdType, AttributeType.EQUAL, ps);

            // Exécute la requête de mise à jour
            logger.debug("Exécute SQL: " + ps.toString());
            rowCount = ps.executeUpdate();

        } catch (SQLException e) {
            String psStr = null;
            if (ps != null) {
                psStr = ps.toString();
            }
            logger.error("SqlException: " + psStr, e);

            // Construit une exception chaînée avec la requête SQL
            SQLException ex = new SQLException(psStr, null, -1001);
            e.setNextException(ex);
            throw e;
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
        return new DAOResult(rowCount);
    }

    /**
     * Mise un jour d'un champ avec la valeur fournie sur un ensemble d'id.
     *
     * @param aIdName
     *            nom du champ ID
     * @param aIdValue
     *            la valeur de l'id du record à modifier
     * @param anIdType
     *            Type du champ id, p.ex. Long
     * @param aTable
     *            le nom de la table
     * @param aFieldName
     *            le nom du champ à modifier
     * @param aValue
     *            la valeur à modifier
     * @param aType
     *            le type du champ à modifier
     * @param aConnection
     *            une connection à utiliser
     * @return le nombre d'enregistrements modifiés
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    public int updatFields(String aIdName, Collection<Object> aIdValue, IValueObject.Type anIdType, String aTable, String aFieldName,
            Object aValue, IValueObject.Type aType, Connection aConnection) throws SQLException {

        // On a un IN dans le where...
        Map<Operator, Collection<Object>> valueMap = new EnumMap<>(Operator.class);
        valueMap.put(Operator.IN, aIdValue);
        StringBuilder sql = new StringBuilder(STRING_BUFFER_SIZE);
        sql.append("UPDATE ");
        sql.append(aTable);
        sql.append(" SET ");
        sql.append(aFieldName);
        sql.append("=?");
        sql.append(" WHERE ");
        DAOTools.addClause(aIdName, valueMap, AttributeType.EQUAL, sql);

        PreparedStatement ps = null;
        int rowCount = 0;

        try {
            // Utilise un PreparedStatement de type DebuggableStatement
            ps = StatementFactory.getStatement(aConnection, sql.toString());
            ps.setMaxRows(1);

            int idx = 1;
            idx = DAOTools.set(idx, aValue, aType, AttributeType.EQUAL, ps);
            idx = DAOTools.set(idx, valueMap, anIdType, AttributeType.EQUAL, ps);

            // Exécute la requête de mise à jour
            logger.debug("Exécute SQL: " + ps.toString());
            rowCount = ps.executeUpdate();

        } catch (SQLException e) {
            String psStr = null;
            if (ps != null) {
                psStr = ps.toString();
            }
            logger.error("SqlException: " + psStr, e);

            // Construit une exception chaînée avec la requête SQL
            SQLException ex = new SQLException(psStr, null, -1001);
            e.setNextException(ex);
            throw e;
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
        return rowCount;
    }

    /**
     * Get map with all different attribute name and database name for current DAO
     *
     * @return all different attribute name and database name for current DAO
     */
    public Map<String, String> getAttributeNameDBNameMap() {
        return iAttributeNameDBNameMap;
    }

    /**
     * Get DB field name for an attribute name
     *
     * @param attributeName
     *            attribute name
     * @return DB field name
     */
    protected String getDBName(String attributeName) {
        String dbName = iAttributeNameDBNameMap.get(attributeName);

        if (dbName != null) {
            return dbName;
        }

        return attributeName;
    }

    /**
     * Get attribute name for an DB field name
     *
     * @param dbName
     *            DB field name
     * @return attribute name
     */
    private String getAttributeName(String dbName) {
        String attributeName = iDBNameAttributeNameMap.get(dbName);

        if (attributeName != null) {
            return attributeName;
        }

        return dbName;
    }

    /**
     * Ajout du container factory pour les résultat
     *
     * @param aIResultContainerFactory
     *            factory pour créer le container pour les résultats d'un select. Par défaut ArrayList<IValueObject>
     */
    public void setiResultContainerFactory(IResultContainerFactory aIResultContainerFactory) {
        iResultContainerFactory = aIResultContainerFactory;
    }

}