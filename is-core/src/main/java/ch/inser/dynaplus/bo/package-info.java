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
 * Business object component of isEJaWA.<br>
 * Package containing BusinessProcess and BusinessObject.<br>
 * This tier work on the configuration files found under {config}/bp for BusinessProcess and {config}/bo for BusinessObject.
 * 
 * <h2>Configuration file</h2>
 * 
 * The structure of the configuration file is: <br>
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
			{@code </xs:element>}
		{@code </xs:sequence>}
	{@code </xs:complexType>}
{@code </xs:element>}
  </pre>
 * <p>
 * In the tag {@code <xs:appinfo>} of the object are the following tags possible:<br>
 * <ul>
 * <li>{@code <classname>}:Name of the specific business object class (bp and bo)</li>
 * <li>{@code <multiselect>}:Multiselect field (multiple allowed) (bp)</li>
 * <ul>
 * <li>{@code <name>}:Name of the multiselect object (base list of the multiselection)</li>
 * <li>{@code <mlink>}:Name of the parameter link on the master side</li>
 * <li>{@code <slink>}:Name of the parameter link on the select side</li>
 * <li>{@code <link>}:Name of the object solving the n-n relationship</li>
 * <li>{@code <display>}:Name of the field using to display the base list of multiselection.</li>
 * </ul>
 * <li>{@code <parent>}:Parent object, the parent object is loaded by the businnes object as subvalueobject, it allows to manage, for
 * instance: self linked table, double links between tables. The join concept defined on the dao side should not be used in case of multiple
 * or self links. The parent object is accessible in the VO with {#vo.keyname} (bp)</li>
 * <ul>
 * <li>{@code <name>}:Name of the parent object</li>
 * <li>{@code <keyname>}:Keyname of the parent object in the fault (default name of the object)</li>
 * <li>{@code <mlink>}:Name of the parameter linked to the id of the parent object</li>
 * </ul>
 * <li>{@code <delcascade>}:Delete cascade, describe the children and relation with master table (bp)</li>
 * <ul>
 * <li>{@code <name>}:Name of the children object (base list of the multiselection)</li>
 * <li>{@code <primarykey>}:Name of the primary key for the master table</li>
 * <li>{@code <foreignkey>}:Name of the foreign key for the children table</li>
 * <li>{@code <activate>}:option for to activate the delete cascade default=(false)</li>
 * </ul>
 * </ul>
 * 
 * 
 * 
 * <h2>Implementation of multiselect fields</h2> To implement a multiselect field, the configuration file on the BusinessProcess tier should
 * contain informations in the mutliselect tag. This multiselect pattern is appropriate to make multiselection on the GUI, it is not
 * appropriate to handle chlidren!<br>
 * <br>
 * The multiselect field should be declare as "java.util.List" in the vo configuration file:<br>
 * @code{<xs:element name="Multiselectobjectname" type="java.util.List"/>}<br>
 * <br>
 * On the JSF side (for JSF generation purpose only) it should exist a field declaration for the "Multiselectobjectname" with type
 * "java.util.List" and the following properties:<br>
 * @code{<multiselect>true</multiselect>}<br>
 * <h3>Sample</h3> See the implementation of the multiselection Usergroup on the User object.
 * 
 * <h2>Handling children</h2> To handle children use the standard children configuration in isEPlug.
 * 
 * <h2>Implementation of calculate fields</h2> To implement calculate fields, simply extends the GenericBusinessObject with a specific class
 * and declare it in the bo configuration file. After that you can implement the specific comportement in the getRecord.<br>
 * <h3>Sample</h3>
 * <h4>Extends GenericBusinnesObject</h4>
 * 
 * <pre>
public class BOUser extends GenericBusinessObject {<br>
	public BOMyObject() {
		super("MyObject");
	}
	
	{@code @Override} 
	public IValueObject getRecord(Object id, Connection con, AbstractLoggedUser user) throws SQLException {	 
		IValueObject vo = super.getRecord(id,con,user)
			...add some calculated fields to the VO 
		return vo;	
	}
}
	</pre>
 * 
 * If some specific data access is needed just extends the framework on the DAO tier.
 * 
 * @see ch.inser.dynaplus.dao
 *      <h4>Declare in the configuration file</h4> In the bo configuration file:<br>
 * 
 *      <pre>
 * {@code<xs:element name="MyObject">}
 * {@code <xs:annotation>}
	{@code <xs:appinfo>}
		{@code <classname>ch.inser.mypackage.BOMyObject</classname>}
	{@code </xs:appinfo>}
 * {@code </xs:annotation>}
 * {@code </xs:element>}
</pre>
 * 
 *      <h2>Requisite libraries</h2>
 * 
 *      <br>
 *      <ul>
 *      <li>TODO</li>
 *      <li>ISjsl</li>
 *      <li>commons-logging</li>
 *      <li>jdom</li>
 *      <li>javaunderground</li>
 *      </ul>
 * 
 * 
 * 
 *      <h2>Other documentation</h2>
 * @see ch.inser.jsl JSL
 */
package ch.inser.dynaplus.bo;
