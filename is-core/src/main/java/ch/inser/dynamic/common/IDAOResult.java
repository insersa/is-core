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
import java.util.Collection;
import java.util.List;

/**
 * The result of database operations.
 *
 * @author INSER SA
 */
public interface IDAOResult {

    /**
     * The result status possible values.
     *
     * @author INSER SA
     */
    public enum Status {
        /**
         * Undetermined status.
         */
        NONE,

        /**
         * Successfully normal status.
         */
        OK,

        /**
         * Unable without a specific reason.
         */
        KO,

        /**
         * Unable without a specific reason on the parent.
         */
        KO_PARENT,

        /**
         * Unable while not enough right to do it (-4 in the old code).
         */
        NO_RIGHTS,

        /**
         * The object to update was not fount (-1 in the old code).
         */
        NOT_FOUND,

        /**
         * The timestamp cas changed (-2 in the old code).
         */
        CHANGED_TIMESTAMP,

        /**
         * Nothing to update or remove (-3 in the old code).
         */
        NOTHING_TODO,

        /**
         * Unable to update, diffrent list size (-5 in the old code).
         */
        DIFFERENT_SIZE;

        /**
         * Get the number correspondence (from the old code).
         *
         * @return the number correspondence
         */
        public int getNumber() {
            switch (this) {
                case KO:
                case NOT_FOUND:
                    return -1;
                case CHANGED_TIMESTAMP:
                    return -2;
                case KO_PARENT:
                case NOTHING_TODO:
                    return -3;
                case NO_RIGHTS:
                    return -4;
                case DIFFERENT_SIZE:
                    return -5;
                default:
                    return Integer.MIN_VALUE;
            }
        }
    }

    /**
     * Get the status of the result.
     *
     * @return the status of the result
     */
    public Status getStatus();

    /**
     * Return <code>true</code> if the status is <code>Status.OK</code>.
     *
     * @return <code>true</code> if the status is <code>Status.OK</code>
     */
    public boolean isStatusOK();

    /**
     * Return <code>true</code> if the status is <code>Status.NOTHING_TODO</code>.
     *
     * @return <code>true</code> if the status is <code>Status.NOTHING_TODO</code>
     */
    public boolean isStatusNOTHING_TODO();

    /**
     * Set the status of the result.
     *
     * @param aStatus
     *            the status of the result
     */
    public void setStatus(Status aStatus);

    /**
     * Get the value of the result if it is a <code>IValueObject</code>.
     *
     * @return the value of the result
     */
    public IValueObject getValueObject();

    /**
     * Get the id from the <code>IValueObject</code>.
     *
     * @return the id
     */
    public Object getId();

    /**
     * Set the value of the result if it is a <code>IValueObject</code>.
     *
     * @param aValueObject
     *            the value of the result
     */
    public void setValueObject(IValueObject aValueObject);

    /**
     * Get the value of the result if it is a <code>List<IValueObject></code>.
     *
     * @return the list
     */
    public List<IValueObject> getListObject();

    /**
     * Get the value of the result.
     *
     * @return the value of the result
     */
    public Object getValue();

    /**
     * Set the value of the result.
     *
     * @param aValue
     *            the value of the result
     */
    public void setValue(Object aValue);

    /**
     * Get the value of the result if it is a <code>List<Object></code>.
     *
     * @return the listValue
     */
    public List<Object> getListValue();

    /**
     * Get the <code>List<IValueObject></code> or <code>List<Object></code>.
     *
     * @return the list
     */
    public Collection<?> getList();

    /**
     * Set a <code>List<IValueObject></code> or <code>List<Object></code>.
     *
     * @param aList
     *            the list
     */
    public void setList(Collection<?> aList);

    /**
     * Get the number of records in the result. A negative value indicate an error.
     *
     * @return the number of records
     */
    public int getNbrRecords();

    /**
     * Set the number of records in the result. A negative value indicate an error.
     *
     * @param aNbrRecords
     *            the number of records
     */
    public void setNbrRecords(int aNbrRecords);

    /**
     * Get the value as a <code>Timestamp</code>
     *
     * @return
     */
    public Timestamp getTimestamp();
}
