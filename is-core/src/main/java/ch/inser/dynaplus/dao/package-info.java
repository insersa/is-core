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
 * Integration component of isEJaWA.<br>
 * Package containing the DataAcessObjects.<br>
 * This tier work on the configuration files found under {config}/dao.
 * <h2>Configuration file</h2>
 * 
 * The structure of the configuration file is:
 * 
 * <pre>
{@code <xs:element name="vos">}
	{@code <xs:complexType>}
		{@code <xs:sequence>}
			{@code <xs:element name="ObjectName">}
				{@code <xs:annotation>}
					{@code <xs:appinfo>}
					...
					{@code </xs:appinfo>}
				{@code </xs:annotation>}
				{@code <xs:complexType>}
					{@code <xs:sequence>}
						{@code <xs:element name="PropName" type="java.lang.Long">}
							{@code <xs:annotation>}
								{@code <xs:appinfo>}
								...
								{@code </xs:appinfo>}
							{@code </xs:annotation>}
						{@code </xs:element>}
						...
					{@code <xs:sequence>}
				{@code </xs:complexType>}
			{@code </xs:element>}
		{@code </xs:sequence>}
	{@code </xs:complexType>}
{@code </xs:element>}
  </pre>
 * <p>
 * In the tag {@code <xs:appinfo>} of the object are the following tags possible:<br>
 * <ul>
 * <li>{@code <id>}:Name of the property containing the Id (mandatory)</li>
 * <li>{@code <timestamp>}:Name of the property containing the Timestamp (mandatory if you want to edit or create records)</li>
 * <li>{@code 
 * <table>
 * }:Name of the table in the database (mandatory)</li>
 * <li>{@code <sequence>}:Name of the sequence in the database (mandatory if you want to create records)</li>
 * </ul>
 * TODO documenter le <search>equal</search> qui empêche la recherche sur like!
 * <h2>Requisite libraries</h2>
 *
 * <br>
 * <ul>
 * <li>TODO</il>
 * <li>ISjsl</li>
 * <li>commons-logging</li>
 * <li>jdom</li>
 * <li>javaunderground</li>
 * </ul>
 * 
 * 
 *
 * 
 * <h2>Other documentation</h2>
 *
 * @see ch.inser.jsl JSL
 */
package ch.inser.dynaplus.dao;
