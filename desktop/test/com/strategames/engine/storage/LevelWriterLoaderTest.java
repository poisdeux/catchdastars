package com.strategames.engine.storage;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.strategames.engine.utils.Level;
import com.strategames.libgdx.junit.GameTestHelper;
import com.strategames.libgdx.junit.GdxTestRunner;

@RunWith(GdxTestRunner.class)
public class LevelWriterLoaderTest {
	private GameMetaData gameMetaData;

	@Before
	public void setUp() throws Exception {
        this.gameMetaData = GameTestHelper.createGame();
	}

	@After
	public void tearDown() throws Exception {
        GameWriter.deleteOriginal(this.gameMetaData);
        GameWriter.deleteInprogress(this.gameMetaData);
	}

    @Test
    public void testOriginal() {
        int[] pos = GameTestHelper.getPosition(2);
        Level level = this.gameMetaData.getLevel(pos[0], pos[1]);
        assertTrue(level != null);

        LevelWriter.saveOriginal(this.gameMetaData, level);

        Level savedLevel = LevelLoader.loadOriginal(this.gameMetaData, pos);
        assertTrue(savedLevel != null);
        assertTrue(savedLevel.equals(level));

        LevelWriter.deleteOriginal(this.gameMetaData, level);
        savedLevel = LevelLoader.loadOriginal(this.gameMetaData, pos);
        assertFalse(savedLevel != null);
        assertFalse(level.equals(savedLevel));
    }

    @Test
    public void testCompleted() {
        int[] pos = GameTestHelper.getPosition(2);
        Level level = this.gameMetaData.getLevel(pos[0], pos[1]);
        assertTrue(level != null);

        LevelWriter.saveCompleted(this.gameMetaData, level);

        Level savedLevel = LevelLoader.loadCompleted(this.gameMetaData, pos);
        assertTrue(savedLevel != null);
        assertTrue(savedLevel.equals(level));

        LevelWriter.deleteCompleted(this.gameMetaData, level);
        savedLevel = LevelLoader.loadCompleted(this.gameMetaData, pos);
        assertFalse(savedLevel != null);
        assertFalse(level.equals(savedLevel));
    }
}
