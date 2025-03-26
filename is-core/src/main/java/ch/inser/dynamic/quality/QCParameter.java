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

package ch.inser.dynamic.quality;

/**
 * Classe qui permet de passer des paramètres au QualityController, par example le user
 *
 * @author INSER SA
 *
 */
public class QCParameter {

    /**
     * Enumeration de noms de paramètres
     *
     */
    public enum Name {
        /**
         * Objet de type ILoggedUser
         */
        USER,

        /**
         * IValueObject permettant des calculs complexes
         */
        VALUEOBJECT,

        /**
         * Other parameter, must be identified by the iOtherName
         */
        OTHER,

        /**
         * Empty parameter only to bypass deprecated methods.
         */
        EMPTY
    }

    /**
     * An empty parameter only to bypass deprecated methods.
     */
    public static final QCParameter EMPTY_PARAMETER;

    /**
     * Static constructor.
     */
    static {
        EMPTY_PARAMETER = new QCParameter();
        EMPTY_PARAMETER.iName = Name.EMPTY;
    }

    /**
     * Get the parameter in an array of parameters.
     *
     * @param aName
     * @param aParameters
     *            the parameter name
     * @param aParameters
     *            an array of parameters
     * @return the parameter or <code>null</code> if not found
     */
    public static QCParameter getParameter(Name aName, QCParameter[] aParameters) {
        if (aParameters == null) {
            return null;
        }
        for (int i = 0; i < aParameters.length; i++) {
            if (aParameters[i].iName == aName) {
                return aParameters[i];
            }
        }
        return null;
    }

    /**
     * Get the other parameter in an array of parameters.
     *
     * @param aOtherName
     *            the other parameter name
     * @param aParameters
     *            an array of parameters
     * @return the parameter or <code>null</code> if not found
     */
    private static QCParameter getParameter(String aOtherName, QCParameter[] aParameters) {
        for (int i = 0; i < aParameters.length; i++) {
            if (aParameters[i].iName == Name.OTHER && aParameters[i].iOtherName.equals(aOtherName)) {
                return aParameters[i];
            }
        }

        return null;
    }

    /**
     * Get the value of a parameter in an array of parameters.
     *
     * @param aName
     *            the parameter name
     * @param aParameters
     *            an array of parameters
     * @return the value of the parameter or <code>null</code> if not found
     */
    public static Object getValue(Name aName, QCParameter[] aParameters) {
        QCParameter parameter = getParameter(aName, aParameters);
        return parameter != null ? parameter.getValue() : null;
    }

    /**
     * Get the value of an other parameter in an array of parameters.
     *
     * @param aOtherName
     *            the other parameter name
     * @param aParameters
     *            an array of parameters
     * @return the value of the parameter or <code>null</code> if not found
     */
    public static Object getValue(String aOtherName, QCParameter[] aParameters) {
        QCParameter parameter = getParameter(aOtherName, aParameters);
        return parameter != null ? parameter.getValue() : null;
    }

    /**
     * Add some new parameters to an existing array of parameters
     *
     * @param aParameters
     *            the existing array of parameters
     * @param aNewParameters
     *            the new parameters
     * @return a new array combining the existing and new parameters
     */
    public static QCParameter[] add(QCParameter[] aParameters, QCParameter... aNewParameters) {
        QCParameter[] result = new QCParameter[aParameters.length + aNewParameters.length];
        for (int i = 0; i < aParameters.length; i++) {
            result[i] = aParameters[i];
        }
        for (int i = 0; i < aNewParameters.length; i++) {
            result[i + aParameters.length] = aNewParameters[i];
        }
        return result;
    }

    /**
     * Nom du paramètre
     */
    private Name iName;

    /**
     * Identify <cod>OTHER</code> parameters.
     */
    private String iOtherName;

    /**
     * Valeur du paramètre
     */
    private Object iValue;

    public QCParameter() {
    }

    public QCParameter(Name aName, Object aValue) {
        iName = aName;
        iValue = aValue;
    }

    public QCParameter(String anOtherName, Object aValue) {
        iName = Name.OTHER;
        iOtherName = anOtherName;
        iValue = aValue;
    }

    @Override
    public String toString() {
        return String.format("%s=%s", iName != Name.OTHER ? iName : iOtherName, iValue);
    }

    /**
     *
     * @return nom du paramètre
     */
    public Name getName() {
        return iName;
    }

    /**
     *
     * @param aName
     *            nom du paramètre
     */
    public void setName(Name aName) {
        iName = aName;
    }

    /**
     *
     * @return nom du paramètre
     */
    public String getOtherName() {
        return iOtherName;
    }

    /**
     *
     * @param aName
     *            nom du paramètre
     */
    public void setOtherName(String anOtherName) {
        iOtherName = anOtherName;
    }

    /**
     *
     * @return valeur du paramètre
     */
    public Object getValue() {
        return iValue;
    }

    /**
     *
     * @param aValue
     *            valeur du paramètre
     */
    public void setValue(Object aValue) {
        iValue = aValue;
    }
}
