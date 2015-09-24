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

import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.strategames.engine.gameobject.types.Door;
import com.strategames.engine.utils.Level;

/**
 * The GameMetaData contains generic meta-data about the game
 *
 * uuid:                    unique identifier of the game
 * name:                    name of the game as provided by the game designer
 * designer:                name of the designer of the game
 * additionalInfo:          any info needed by the game that is not generic for all game types
 * saveGameProgressNumber:  identifier to be used to identify multiple saved progressions of this
 *                          game
 */
public class GameMetaData implements Json.Serializable, Writer {
    private String uuid;
    private String name;
    private String designer;
    private HashMap<String, String> additionalInfo = new HashMap<String, String>();
    private int savedGameProgressNumber = 1; // used to be able to keep multiple saved states

    public GameMetaData() {
        this.uuid = UUID.randomUUID().toString();
    }

    @Override
    public void write(Json json) {
        json.writeValue("uuid", uuid);
        json.writeValue("name", name);
        json.writeValue("designer", designer);
        json.writeValue("info", convertAdditionalInfoHashToString());
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        JsonValue child = jsonData.child();

        while( child != null ) {
            if (child.name.contentEquals("uuid")) {
                uuid = child.asString();
            } else if (child.name.contentEquals("name")) {
                name = child.asString();
            } else if (child.name.contentEquals("designer")) {
                designer = child.asString();
            } else if (child.name.contentEquals("info")) {
                parseAdditionalInfo(child.asString());
            }

            child = child.next();
        }
    }

    public void setSavedGameProgressNumber(int savedGameProgressNumber) {
        this.savedGameProgressNumber = savedGameProgressNumber;
    }

    public int getSavedGameProgressNumber() {
        return savedGameProgressNumber;
    }

    public void setAdditionalInfo(String name, String value) {
        this.additionalInfo.put(name, value);
    }

    public String getAdditionalInfo(String name) {
        return additionalInfo.get(name);
    }

    public String getUuid() {
        return uuid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDesigner(String designer) {
        this.designer = designer;
    }

    public String getDesigner() {
        return designer;
    }

    public String getJson() {
        Json json = new Json();
        json.setOutputType(OutputType.minimal);
        return json.toJson(this);
    }

    @Override
    public String getFilename() {
        return "meta";
    }

    @Override
    public boolean equals(Object obj) {
        if( obj == null ) {
            return false;
        }

        if( this == obj ) {
            return true;
        }

        if( ! ( obj instanceof GameMetaData) ) {
            return false;
        }

        GameMetaData gameMetaData = (GameMetaData) obj;

        if( ! compareStrings(this.uuid, gameMetaData.uuid) ) {
            return false;
        }

        if( ! compareStrings(this.name, gameMetaData.name) ) {
            return false;
        }

        if( ! compareStrings(this.designer, gameMetaData.designer) ) {
            return false;
        }

        return additionalInfo.equals(gameMetaData.additionalInfo);
    }

    private boolean compareStrings(String s1, String s2) {
        if( s1 != null ) {
            if( ! s1.contentEquals(s2) ) {
                return false;
            }
        } else {
            if ( s2 != null ) {
                return false;
            }
        }
        return true;
    }

    private void parseAdditionalInfo(String info) {
        if( info == null )
            return;

        String[] elts = info.split(":");

        int size = elts.length - 1;
        for( int i = 0; i < size; i += 2 ) {
            String key = elts[i];
            String value = elts[i+1];
            this.additionalInfo.put(key, value);
        }
    }

    private String convertAdditionalInfoHashToString() {
        StringBuffer stringBuffer = new StringBuffer();
        Set<String> keys = additionalInfo.keySet();
        for( String key : keys ) {
            stringBuffer.append(key + ":" + additionalInfo.get(key) + ":");
        }
        return stringBuffer.toString();
    }

    @Override
    public String toString() {
        return super.toString() + ", uuid="+uuid+", name="+name+", designer="+designer+", savedGameProgressNumber="+savedGameProgressNumber+", additionalInfo="+convertAdditionalInfoHashToString();
    }
}
