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

package ch.inser.dynaplus.birt;

import java.util.Set;

/**
 * Interface representing the meta-data for a report file.
 *
 * Note: this interface has a natural ordering that is inconsistent with equals.
 *
 * @author INSER SA
 */
public interface IReportFile extends Comparable<IReportFile> {

    /**
     * Get the report file name.
     *
     * @return the file name
     */
    public String getFileName();

    /**
     * Get the set keywords contained in the tag IS_keywords.
     *
     * @return the set of keywords
     */
    public Set<String> getKeyWords();

    /**
     * Get the set ids contained in the tag IS_keyids.
     *
     * @return the set of ids
     */
    public Set<String> getKeyIds();

    /**
     * Get the display name of the report.
     *
     * @return the display name
     */
    public String getDisplayName();

    /**
     * Get <code>true</code> if the report file is valid.
     *
     * @return <code>true</code> if the report file is valid
     */
    public boolean isValid();

    /**
     * Get all the parameters except jdbc*.
     *
     * @return all the parameters except jdbc*
     */
    public Set<String> getParameters();

    /**
     * Get the absolute path of the report file.
     *
     * @return the absolute path
     */
    public String getAbsolutePath();

    @Override
    public int compareTo(IReportFile aReportFile);
}