/**
 * 
 * Copyright 2015 Martijn Brekhof
 *
 * This file is part of Catch Da Stars.
 *
 * Catch Da Stars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Catch Da Stars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Catch Da Stars.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.strategames.engine.storage;

/**
 * Created by martijn on 1-1-15.
 */
public interface Writer {
    /**
     * Must return the json data to be written
     *
     * @return String containing the json data
     */
    public String getJson();

    /**
     * Must return the filename where the json data should be written to.
     *
     * @return String filename
     */
    public String getFilename();
}
