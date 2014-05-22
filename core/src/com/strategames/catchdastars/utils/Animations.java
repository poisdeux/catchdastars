package com.strategames.catchdastars.utils;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;

public class Animations {

	public static void fadeIn(Stage stage, float duration, Interpolation interpolation) {
		Array<Actor> actors = stage.getActors();
		int size = actors.size;
		for(int i = 0; i < size; i++) {
			Actor actor = actors.get(i);
			Color color = actor.getColor();
			color.a = 0;
			actor.addAction(sequence ( Actions.fadeIn(duration, interpolation)));
		}
	}
}
