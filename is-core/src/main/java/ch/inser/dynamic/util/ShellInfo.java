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

import org.jdom2.Element;

/**
 * Info for shel on the bo side
 *
 * @author INSER SA
 *
 */
public class ShellInfo {

    /**
     * Le nom du ValueObject.
     */
    private String iVOName;

    /**
     * Shell to be executed (name of the file)
     */
    private String iNameShell;

    /**
     * Type of the shell to be executed (create,create-rollback)
     */
    private String iTypeShell;

    /**
     * Parameters of the shell to be executed (name of parameters in th contextual vo)
     */
    private String iTypeParameters;

    public ShellInfo(String aVOName, Element aElement) {
        iVOName = aVOName;
        iNameShell = readString(aElement, "name");
        iTypeShell = readString(aElement, "type");
        iTypeParameters = readString(aElement, "parameters");
    }

    public String getVOName() {
        return iVOName;
    }

    public void setVOName(String name) {
        iVOName = name;
    }

    private static String readString(Element aElement, String aElementName) {
        if (aElement.getChild(aElementName) == null) {
            return null;
        }
        return aElement.getChild(aElementName).getText();
    }

    public String getNameShell() {
        return iNameShell;
    }

    public void setNameShell(String nameShell) {
        iNameShell = nameShell;
    }

    public String getTypeShell() {
        return iTypeShell;
    }

    public void setTypeShell(String typeShell) {
        iTypeShell = typeShell;
    }

    public String getTypeParameters() {
        return iTypeParameters;
    }

    public void setTypeParameters(String typeParameters) {
        iTypeParameters = typeParameters;
    }

}
