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

package ch.inser.jsl.logger;

import java.io.Serializable;
import java.util.Stack;

import org.apache.logging.log4j.ThreadContext;

public class Log4JNDC implements INdc, Serializable {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -1257390868857974535L;

    @Override
    public void push(String aMessage) {
        ThreadContext.push(aMessage);
    }

    @Override
    public void remove() {
        ThreadContext.removeStack();
    }

    @Override
    public int getDepth() {
        return ThreadContext.getDepth();
    }

    @Override
    public Stack<?> cloneStack() {
        return (Stack<?>) ThreadContext.cloneStack();
    }

}
