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

/**
 * Librairie de composants dynamiques au niveau des couches métier et intégration.
 *
 * <h2>Specifications du package</h2>
 *
 * Librairies nécessaires: <br>
 * <br>
 * <ul>
 * <li>ISjsl</li>
 * <li>commons-logging</li>
 * <li>jdom</li>
 * <li>javaunderground</li>
 * </ul>
 *
 * <h2>Construction de la requête</h2> Il y a deux manière de construire la requête, ces deux manières passent par un ValueObject:
 * <ul>
 * <li>Requête simple</li> Chaque champ trouvé dans le ValueObject est restreint par égualité, la requête est construite à l'aide de AND.
 * Par défault, les restrictions sur les attributs de type String sont construits avec un LIKE.
 * <li>Requête complex</li> Si dans la propriété d'un ValueObject on trouve une Map, la restriction sur le champ est complexe. Il peut y
 * avoir plusieurs restriction sur chaque champ avec des opérateurs différends de l'égalité. <br>
 * Les opérateurs implémentés sont définis par l'énumération Operator de DynamicDAO.
 * </ul>
 * {@see ch.inser.dynamic.common.DynamicDAO DynamicDAO}
 *
 * <h2>Autre documentation</h2>
 *
 * {@see ch.inser.jsl JSL}
 */
package ch.inser.dynamic;
