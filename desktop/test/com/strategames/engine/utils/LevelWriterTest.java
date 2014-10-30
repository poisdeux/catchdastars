package com.strategames.engine.utils;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.strategames.libgdx.junit.GdxTestRunner;
import com.strategames.libgdx.junit.LevelsTestHelper;

@RunWith(GdxTestRunner.class)
public class LevelWriterTest {
	private Levels levels;
	
	@Before
	public void setUp() throws Exception {
		this.levels = LevelsTestHelper.createLevels();
		LevelWriter.deleteLocalLevelsDir();
	}

	@After
	public void tearDown() throws Exception {
		LevelWriter.deleteLocalLevelsDir();
	}
	
	@Test
	public void testSave() {
		Array<Level> fails = LevelWriter.save(this.levels.getLevels());
		assertTrue(fails.size == 0);
		Array<Level> levelsSaved = LevelLoader.loadAllLocalLevels();
		assertTrue(levelsSaved.size == this.levels.getLevels().size);
		for(Level level : this.levels.getLevels()) {
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
