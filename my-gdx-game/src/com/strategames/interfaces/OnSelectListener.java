package com.strategames.interfaces;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.strategames.catchdastars.actors.GameObject;

public interface OnSelectListener {
	public void onObjectSelectListener(GameObject object);
	public void onConfigurationItemSelectListener(String name, Float value);
	public void onButtonPressedListener(Button button);
}