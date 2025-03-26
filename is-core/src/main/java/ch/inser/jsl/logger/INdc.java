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

import java.util.Stack;

/**
 * Interface de Nested diagnostic context, pour chacher l'implémentation
 * 
 * @author INSER SA
 * 
 */
public interface INdc {

    /**
     * Push new diagnostic context information for the current thread.
     * 
     * @param message
     *            message du context
     */
    public void push(String message);

    /**
     * Remove the diagnostic context for this thread.
     */
    public void remove();

    /**
     * Get the current nesting depth of this diagnostic context.
     */
    public int getDepth();

    /**
     * @return a clone of the diagnostic context for the current thread.
     */
    public Stack<?> cloneStack();

}
