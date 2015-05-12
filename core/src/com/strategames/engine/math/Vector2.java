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
