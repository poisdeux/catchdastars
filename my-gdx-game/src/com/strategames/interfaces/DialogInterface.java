package com.strategames.interfaces;

import com.strategames.catchdastars.actors.GameObject;
import com.strategames.ui.Dialog;

public interface DialogInterface {
	public void onObjectSelectListener(GameObject object);
//	public void onCopyObjectListener(GameObject object);
//	public void onPressedListener(Button button);
	
	public static interface OnClickListener {
		public void onClick(Dialog dialog, int which);
	}
}
