package com.strategames.engine.tweens;

import aurelienribon.tweenengine.TweenAccessor;

import com.badlogic.gdx.Gdx;
import com.strategames.engine.gameobjects.GameObject;

public class GameObjectAccessor implements TweenAccessor<GameObject> {

	public static final int POSITION_X = 1;
    public static final int POSITION_Y = 2;
    public static final int POSITION_XY = 3;
    public static final int ALPHA = 4;
    public static final int SCALE = 5;
    public static final int ROTATE = 6;

    @Override
    public int getValues(GameObject target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case POSITION_X: returnValues[0] = target.getX(); return 1;
            case POSITION_Y: returnValues[0] = target.getY(); return 1;
            case POSITION_XY:
                returnValues[0] = target.getX();
                returnValues[1] = target.getY();
                return 2;
            case ALPHA: returnValues[0] = target.getColor().a; return 1;
            case SCALE: 
            	returnValues[0] = target.getScaleX();
            	returnValues[1] = target.getScaleY();
            	return 2;
            case ROTATE: returnValues[0] = target.getRotation(); return 1;
            default: assert false; return -1;
        }
    }
    
    @Override
    public void setValues(GameObject target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case POSITION_X:
            	Gdx.app.log("GameObjectAccessor", "wordt dit aangeroepen?");
            	target.moveX(newValues[0]);
            	break;
            case POSITION_Y: 
            	target.moveY(newValues[0]);
            	break;
            case POSITION_XY:
                target.moveTo(newValues[0], newValues[1]);
                break;
            case ALPHA:
            	target.getColor().a = newValues[0];
            	break;
            case SCALE: 
            	target.setScaleX(newValues[0]);
            	target.setScaleY(newValues[1]);
            	break;
            case ROTATE: target.setRotation(newValues[0]);
            default: assert false; break;
        }
    }

}
