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
 * The default implementation.
 *
 * @author INSER SA
 */
public class DAOResult implements IDAOResult {

    /**
     * The operation status
     */
    private Status iStatus = Status.NONE;

    /**
     * The value of the result.
     */
    private Object iValue;

    /**
     * The value of the result if it is a <code>IValueObject</code>.
     */
    private IValueObject iValueObject;

    /**
     * The list of <code>IValueObject</code> or <code>Object</code> of the result.
     */
    private List<?> iList;

    /**
     * The number of record modified by some action.
     */
    private int iNbrRecords = Integer.MIN_VALUE;

    /**
     * Constructor.
     */
    public DAOResult() {
        // Nothing to do
    }

    /**
     * Constructor that sets the status and, in case of error, number of records with error code (-1 not found, -2 changed timestamp etc.)
     *
     * @param aStatus
     *            status
     */
    public DAOResult(Status aStatus) {
        iStatus = aStatus;
        switch (iStatus) {
            case NOT_FOUND:
                iNbrRecords = -1;
                break;
            case CHANGED_TIMESTAMP:
                iNbrRecords = -2;
                break;
            case NOTHING_TODO:
                iNbrRecords = -3;
                break;
            case NO_RIGHTS:
                iNbrRecords = -4;
                break;
            case DIFFERENT_SIZE:
                iNbrRecords = -5;
                break;
            default:
                break;
        }
        if (iStatus == Status.NOTHING_TODO) {
            iNbrRecords = -3;
        }
    }

    /**
     * Constructor for a <code>IValueObject</code>.
     *
     * @param aValueObject
     *            the <code>IValueObject</code>
     */
    public DAOResult(IValueObject aValueObject) {
        if (aValueObject != null) {
            iNbrRecords = 1;
            iValueObject = aValueObject;
            iStatus = Status.OK;
        } else {
            iStatus = Status.KO;
        }
    }

    /**
     * Constructor for a <code>List<IValueObject></code> or <code>List<Object></code>.
     *
     * @param aList
     *            the list
     */
    public DAOResult(Collection<?> aList) {
        if (aList != null) {
            iNbrRecords = aList.size();
            iList = (List<?>) aList;
            iStatus = iNbrRecords > 0 ? Status.OK : Status.NOTHING_TODO;
        } else {
            iStatus = Status.KO;
        }
    }

    /**
     * Constructor for a <code>Object</code>.
     *
     * @param aValue
     *            <code>Object</code>
     */
    public DAOResult(Object aValue) {
        if (aValue != null) {
            iNbrRecords = 1;
            iValue = aValue;
            iStatus = Status.OK;
        } else {
            iStatus = Status.KO;
        }
    }

    /**
     * Constructor for delete.
     *
     * @param aNbrRecords
     *            the number of records deleted
     */
    public DAOResult(int aNbrRecords) {
        iNbrRecords = aNbrRecords;

        if (iNbrRecords > 0) {
            iStatus = Status.OK;
        } else {
            switch (aNbrRecords) {
                case 0:
                    iStatus = Status.NOTHING_TODO;
                    break;
                case -1:
                    iStatus = Status.NOT_FOUND;
                    break;
                case -2:
                    iStatus = Status.CHANGED_TIMESTAMP;
                    break;
                case -3:
                    iStatus = Status.NOTHING_TODO;
                    break;
                case -4:
                    iStatus = Status.NO_RIGHTS;
                    break;
                case -5:
                    iStatus = Status.DIFFERENT_SIZE;
                    break;
                default:
                    iStatus = Status.KO;
            }
        }
    }

    @Override
    public Status getStatus() {
        return iStatus;
    }

    @Override
    public boolean isStatusOK() {
        return iStatus == Status.OK;
    }

    @Override
    public boolean isStatusNOTHING_TODO() {
        return iStatus == Status.NOTHING_TODO;
    }

    @Override
    public void setStatus(Status aStatus) {
        iStatus = aStatus;
    }

    @Override
    public Object getValue() {
        return iValue;
    }

    @Override
    public Timestamp getTimestamp() {
        return (Timestamp) iValue;
    }

    @Override
    public void setValue(Object aValue) {
        iValue = aValue;
    }

    @Override
    public IValueObject getValueObject() {
        return iValueObject;
    }

    @Override
    public Object getId() {
        return iValueObject != null ? iValueObject.getId() : null;
    }

    @Override
    public void setValueObject(IValueObject aValueObject) {
        iValueObject = aValueObject;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Object> getListValue() {
        return (List<Object>) iList;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<IValueObject> getListObject() {
        return (List<IValueObject>) iList;
    }

    @Override
    public Collection<?> getList() {
        return iList;
    }

    @Override
    public void setList(Collection<?> aList) {
        iList = (List<?>) aList;
    }

    @Override
    public int getNbrRecords() {
        return iNbrRecords;
    }

    @Override
    public void setNbrRecords(int aNbrRecords) {
        iNbrRecords = aNbrRecords;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(this.getClass().getName());
        builder.append("[status=");
        builder.append(iStatus);
        builder.append(",nbr_records=");
        builder.append(iNbrRecords);
        builder.append(",value=");
        builder.append(iValue);
        builder.append(",value_object=");
        builder.append(iValueObject);
        builder.append(",list=");
        builder.append(iList);
        builder.append(']');
        return builder.toString();
    }
}
