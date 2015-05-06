package com.strategames.engine.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Created by martijn on 6-5-15.
 */
public class Position {

    private int x = 0;
    private int y = 0;

    public Position() {

    }

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        Position p = null;

        Gdx.app.log("Position", "equals: o="+o+", this="+this);

        try {
            p = (Position) o;
        } catch ( Exception e) {
            return false;
        }

        if( ( p.x == x ) && ( p.y == y ) ) {
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return super.toString() + ", x="+x+", y="+y;
    }
}
