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
 * JSF component of isEJaWA.
 *
 * <h2>Package specification</h2>
 *
 * Requisite libraries: <br>
 * <br>
 * <ul>
 * <li>TODO</il>
 * <li>ISjsl</li>
 * <li>commons-logging</li>
 * <li>jdom</li>
 * <li>javaunderground</li>
 * </ul>
 * 
 * <h2>Configuration file</h2>
 * 
 * The structure of the configuration file is: <br>
 * {@code <xs:element name="vos">} <BLOCKQUOTE> {@code <xs:complexType>} <BLOCKQUOTE> {@code <xs:sequence>} <BLOCKQUOTE>
 * {@code <xs:element name="ObjectName">} <BLOCKQUOTE> {@code <xs:annotation>} <BLOCKQUOTE> {@code <xs:appinfo>}<br>
 * ...<br>
 * {@code </xs:appinfo>} </BLOCKQUOTE> {@code </xs:annotation>}<br>
 * {@code <xs:complexType>} <BLOCKQUOTE> {@code <xs:sequence>} <BLOCKQUOTE> {@code <xs:element name="PropName" type="java.lang.Long">}
 * <BLOCKQUOTE> {@code <xs:annotation>} <BLOCKQUOTE> {@code <xs:appinfo>}<br>
 * ...<br>
 * {@code </xs:appinfo>} </BLOCKQUOTE> {@code </xs:annotation>} </BLOCKQUOTE> {@code </xs:element>}<br>
 * ... </BLOCKQUOTE> {@code <xs:sequence>} </BLOCKQUOTE> {@code </xs:complexType>} </BLOCKQUOTE> {@code </xs:element>} </BLOCKQUOTE>
 * {@code </xs:sequence>} </BLOCKQUOTE> {@code </xs:complexType>} </BLOCKQUOTE> {@code </xs:element>}
 * <p>
 * In the tag {@code <xs:appinfo>} of the object are the following tags possible:<br>
 * <ul>
 * <li>{@code <id>}:Name of the property containing the Id (mandatory)</li>
 * <li>{@code <timestamp>}:Name of the property containing the Timestamp (mandatory)</li>
 * <li>{@code <useDetailPageForSearches>}:The detail page is used for searches</li>
 * <li>{@code <displayChildrenInSearchPage>}:Children of an object are also displayed in a search page</li>
 * </ul>
 * 
 *
 * 
 * 
 * <h2>Other documentation</h2>
 *
 * @see ch.inser.jsl JSL
 */
package ch.inser.dynaplus.vo;
