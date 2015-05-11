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
        float x = jsonData.child().asFloat();
        float y = jsonData.child().asFloat();
        Gdx.app.log("Vector2", "read: x="+x+", y="+y);
        set(x, y);
    }

    @Override
    public void write(Json json) {
        json.writeValue("x", super.x);
        json.writeValue("y", super.y);
    }
}
