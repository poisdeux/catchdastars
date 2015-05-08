package com.strategames.engine.utils;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Created by martijn on 8-5-15.
 */
public class Vector2 extends com.badlogic.gdx.math.Vector2 implements Json.Serializable {

    public Vector2(int x, int y) {
        super(x, y);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {

    }

    @Override
    public void write(Json json) {
        json.writeValue("x", super.x);
        json.writeValue("y", super.y);
    }
}
