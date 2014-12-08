package com.strategames.engine.utils;

import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.badlogic.gdx.utils.Array;
import com.strategames.libgdx.junit.GdxTestRunner;
import com.strategames.libgdx.junit.LevelsTestHelper;

@RunWith(GdxTestRunner.class)
public class LevelWriterTest {
	private Game game;
	
	@Before
	public void setUp() throws Exception {
		this.game = LevelsTestHelper.createLevels();
		FileWriter.deleteLocalGamesDir();
	}

	@After
	public void tearDown() throws Exception {
		FileWriter.deleteLocalGamesDir();
	}
	
	@Test
	public void testSave() {
		Collection<Level> levels = this.game.getLevels().values();
		assertTrue("this.game.getLevels() returns null", levels != null);
		for(Level level : levels) {
			assertTrue("Failed to write level: "+level, FileWriter.saveLevelLocal(game, level));
		}
		
		Array<Level> levelsSaved = LevelLoader.loadAllLocalLevels(this.game);
		assertTrue(levelsSaved.size == levels.size());
		for(Level level : levels) {
			int found = 0;
			for( Level savedLevel : levelsSaved ) {
				if( level.equals(savedLevel) ) {
					found++;
				}
			}
			assertTrue("Level "+level+" saved "+found+" times", found == 1);
		}
	}

}
