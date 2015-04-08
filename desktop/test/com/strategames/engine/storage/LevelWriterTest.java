package com.strategames.engine.storage;

import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.badlogic.gdx.utils.Array;
import com.strategames.engine.storage.GameWriter;
import com.strategames.engine.storage.LevelLoader;
import com.strategames.engine.storage.LevelWriter;
import com.strategames.engine.utils.Game;
import com.strategames.engine.utils.Level;
import com.strategames.libgdx.junit.GdxTestRunner;
import com.strategames.libgdx.junit.LevelsTestHelper;

@RunWith(GdxTestRunner.class)
public class LevelWriterTest {
	private Game game;
	
	@Before
	public void setUp() throws Exception {
		this.game = LevelsTestHelper.createLevels();
	}

	@After
	public void tearDown() throws Exception {
		GameWriter.deleteAllOriginalGames();
	}
	
	@Test
	public void testSave() {
		Collection<Level> levels = this.game.getLevels().values();
		assertTrue("this.game.getLevels() returns null", levels != null);
		for(Level level : levels) {
			assertTrue("Failed to write level: "+level, LevelWriter.saveOriginal(game, level));
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

    @Test
    public void testDeleteCompletedLevel() {
        assertTrue(false);
    }

    @Test
    public void testDeleteAllCompletedLevels() {
        assertTrue(false);
    }


}
