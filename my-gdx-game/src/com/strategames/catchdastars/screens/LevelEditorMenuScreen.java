package com.strategames.catchdastars.screens;



import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.strategames.catchdastars.Game;

public class LevelEditorMenuScreen extends AbstractScreen {
	private Skin skin;

	public LevelEditorMenuScreen(Game game) {
		super(game);

		this.skin = getSkin();
	}

	@Override
	public void show() {
		Table table = new Table( skin );
		table.setFillParent(true);
		table.add( "Level editor" ).expand().top();
		table.row();

		Table levelButtonsTable = new Table(skin);
		ArrayList<TextButton> levelButtons = getLevels();
		for( TextButton button : levelButtons ) {
			levelButtonsTable.add(button).expand();
			levelButtonsTable.row();
		}
		ScrollPane scrollPane = new ScrollPane(levelButtonsTable, skin);
		table.add(scrollPane).expand().fill().colspan(4);;
		table.row();
		
		TextButton addLevel = new TextButton( "New level", skin );
		addLevel.addListener( new ClickListener() {

			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.log("MenuScreen", "touch done at (" +x+ ", " +y+ ")");
				//				game.getSoundManager().play( TyrianSound.CLICK );
			}
		}); 

		table.add( addLevel ).size( Gdx.graphics.getWidth(), 60 ).expand().bottom();
		table.row();

		Stage stage = getStage();
		stage.addActor(table);
		Gdx.input.setInputProcessor( stage );
	}

	private ArrayList<TextButton> getLevels() {
		ArrayList<TextButton> levels = new ArrayList<TextButton>();
		levels.add(new TextButton("1. Mind the wall", skin));
		levels.add(new TextButton("2. Save the red balloon", skin));
		levels.add(new TextButton("3. Up and down", skin));
		levels.add(new TextButton("4. The spiral", skin));
		levels.add(new TextButton("5. Do not pop the balloon", skin));
		levels.add(new TextButton("6. Learn to navigate", skin));
		levels.add(new TextButton("7. Mind the wall", skin));
		levels.add(new TextButton("8. Save the red balloon", skin));
		levels.add(new TextButton("9. Up and down", skin));
		levels.add(new TextButton("10. The spiral", skin));
		levels.add(new TextButton("11. Do not pop the balloon", skin));
		levels.add(new TextButton("12. Learn to navigate", skin));
		levels.add(new TextButton("13. Mind the wall", skin));
		levels.add(new TextButton("14. Save the red balloon", skin));
		levels.add(new TextButton("15. Up and down", skin));
		levels.add(new TextButton("16. The spiral", skin));
		levels.add(new TextButton("17. Do not pop the balloon", skin));
		levels.add(new TextButton("18. Learn to navigate", skin));
		return levels;
	}
}
