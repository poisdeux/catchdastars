package com.strategames.engine.utils;

import static org.junit.Assert.assertTrue;

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
		FileWriter.deleteLocalDir();
	}

	@After
	public void tearDown() throws Exception {
		FileWriter.deleteLocalDir();
	}
	
	@Test
	public void testSave() {
		for(Level level : this.game.getLevels()) {
			assertTrue(FileWriter.saveLocal(Files.getGamePath(game), level));
		}
		
		Array<Level> levelsSaved = LevelLoader.loadAllLocalLevels();
		assertTrue(levelsSaved.size == this.game.getLevels().size);
		for(Level level : this.game.getLevels()) {
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
