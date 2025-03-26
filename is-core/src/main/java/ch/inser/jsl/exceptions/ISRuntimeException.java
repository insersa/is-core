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

package ch.inser.jsl.exceptions;

/**
 * Exception générique maison utilisé au lieu de "throws RuntimeException" pour remonter des exceptions divers vers les couches plus hauts,
 * p.ex. les exceptions de reflection (SecurityException, NoSuchMethodException, IllegalArgumentException,...)
 * 
 * @author INSER SA
 * 
 */
public class ISRuntimeException extends RuntimeException {

    /**
     * Default serialization id
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception with null as its detail message.
     */
    public ISRuntimeException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     * 
     * @param aMessage
     *            detail message
     */
    public ISRuntimeException(String aMessage) {
        super(aMessage);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     * 
     * @param aMessage
     *            detail message
     * @param aCause
     *            specified cause
     */
    public ISRuntimeException(String aMessage, Throwable aCause) {
        super(aMessage, aCause);
    }

    /**
     * Constructs a new exception with the specified cause and a detail message of (cause==null ? null : cause.toString()) (which typically
     * contains the class and detail message of cause).
     * 
     * @param aCause
     *            specified cause
     */
    public ISRuntimeException(Throwable aCause) {
        super(aCause);
    }
}
