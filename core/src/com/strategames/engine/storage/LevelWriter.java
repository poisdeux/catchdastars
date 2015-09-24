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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.Json;
import com.strategames.engine.utils.Level;
import com.strategames.engine.utils.ScreenDensity;

public class LevelWriter {

	/**
	 * Saves level as original level which will be loaded when level is played the first time
     * during a game
	 * @param level
	 * @return true if saving was succesful, false otherwise
	 */
	static public boolean saveOriginal(Level level) {

        String filename = Files.getOriginalLevelFilename(level);
        if( filename == null ) {
            Gdx.app.log("LevelWriter", "saveOriginal: failed to get filename for level "+level.getPositionAsString());
            return false;
        }

		FileHandle file = Gdx.files.local(filename);
		try {
			Json json = new Json();
			file.writeString(json.prettyPrint(level.getJson()), false);
			return true;
		} catch (Exception e) {
			Gdx.app.log("LevelWriter", "saveOriginal: could not write: "+file.path()+"\nError: "+e.getMessage());
			return false;
		}
	}

	/**
	 * Deletes the original level file which will be loaded when level is played the first time
     * during a game
	 * @param level
	 * @return
	 */
	static public boolean deleteOriginal(Level level) {
		try {
			FileHandle file = Gdx.files.local(Files.getOriginalLevelFilename(level));
			return file.delete();
		} catch (Exception e) {
			return false;
		}
	}

    /**
    * Saves level as completed level which will be loaded when level is played again
    * @param level
    * @return true if saving was succesful, false otherwise
    */
    static public boolean saveCompleted(Level level) {
        FileHandle file = Gdx.files.local(Files.getCompletedLevelFilename(level));
        try {
            Json json = new Json();
            file.writeString(json.prettyPrint(level.getJson()), false);
            return true;
        } catch (Exception e) {
            Gdx.app.log("LevelWriter", "save: could not write: "+file.path()+"\nError: "+e.getMessage());
            return false;
        }
    }

    /**
     * Deletes the completed level file which will be loaded when level is played again
     * @param level
     * @return true if success, false otherwise
     */
    static public boolean deleteCompleted(Level level) {
        try {
            FileHandle file = Gdx.files.local(Files.getCompletedLevelFilename(level));
            return file.delete();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * TODO Add pixmap to Level. Note this requires implementing json writer to exclude pixmap from from the json file
     * @param level
     * @param pixmap
     * @return
     */
    public static boolean saveScreenshot(Level level, Pixmap pixmap){
        try{
            FileHandle fh = Gdx.files.local(Files.getScreenshotFilename(level));
            PixmapIO.writePNG(fh, pixmap);
            pixmap.dispose();
        }catch (Exception e){
            Gdx.app.log("LevelWriter", "saveScreenshot: "+e.getMessage());
            return false;
        }
        return true;
    }

    public static boolean deleteScreenshot(Level level) {
        try{
            FileHandle fh = Gdx.files.local(Files.getScreenshotFilename(level));
            if( fh.exists() ) {
                return fh.delete();
            }
        }catch (Exception e){
            Gdx.app.log("ScreenshotFactory", "saveScreenshot: "+e.getMessage());
            return false;
        }
        return true;
    }
}
