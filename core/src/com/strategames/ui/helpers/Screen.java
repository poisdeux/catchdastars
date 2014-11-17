package com.strategames.ui.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class Screen {

	/**
	 * Returns the full screen coordinates for the given stage
	 * @param stage stage in which the coordinates should be calculated for full screen
	 * @param start vector that will hold the left-bottom corner in stage coordinates
	 * @param end vector that will hold the top-right corner in stage coordinates
	 */
	static public void getFullScreenCoordinates(Stage stage, Vector2 start, Vector2 end) {
		start.x = -1;
		start.y = Gdx.graphics.getHeight() + 1;
		end.x = Gdx.graphics.getWidth() + 1;
		end.y = -1;
		
		stage.screenToStageCoordinates(start);
		stage.screenToStageCoordinates(end);
		
		end.x += Math.abs(start.x);
		end.y += Math.abs(start.y);
	}
}
