package com.strategames.interfaces;

import com.strategames.catchdastars.actors.GameObject;

public interface OnSelectListener {
	public void onObjectSelectListener(GameObject object);
	public void onConfigurationItemSelectListener(String name, Float value);
}
