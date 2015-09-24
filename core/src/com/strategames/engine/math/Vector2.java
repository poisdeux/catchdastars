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

package com.strategames.engine.math;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Required to fix issue with saving Vector2 in json format where x = 0. For some reason when
 * x = 0 only the y value gets saved.
 * Created by martijn on 8-5-15.
 */
public class Vector2 extends com.badlogic.gdx.math.Vector2 implements Json.Serializable {

    public Vector2() {   }

    public Vector2(int x, int y) {
        super(x, y);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        for (JsonValue entry = jsonData.child; entry != null; entry = entry.next) {
            for(JsonValue element = entry.child; element != null; element = element.next) {
                String name = element.name;
                if ( name.contentEquals("x")) {
                    float value = element.asFloat();
                    super.x = value;
                } else if ( name.contentEquals("y")) {
                    float value = element.asFloat();
                    super.y = value;
                }
            }
        }
    }

    @Override
    public void write(Json json) {
        json.writeObjectStart(this.getClass().getCanonicalName());
        json.writeValue("x", super.x);
        json.writeValue("y", super.y);
        json.writeObjectEnd();
    }
}
