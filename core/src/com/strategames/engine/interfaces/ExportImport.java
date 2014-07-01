package com.strategames.engine.interfaces;

public interface ExportImport {
	public void export(String text);
	public void importLevels(OnLevelsReceivedListener listener);
}
