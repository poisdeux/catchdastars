package com.strategames.engine.storage;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.badlogic.gdx.utils.Array;
import com.strategames.engine.utils.Game;
import com.strategames.engine.utils.Level;
import com.strategames.libgdx.junit.GameTestHelper;
import com.strategames.libgdx.junit.GdxTestRunner;
import com.strategames.libgdx.junit.LevelTestHelper;

@RunWith(GdxTestRunner.class)
public class LevelWriterLoaderTest {
	private Game game;

	@Before
	public void setUp() throws Exception {
        this.game = GameTestHelper.createGame();
	}

	@After
	public void tearDown() throws Exception {
        GameWriter.deleteOriginal(this.game);
        GameWriter.deleteInprogress(this.game);
	}

    @Test
    public void testOriginal() {
        int[] pos = GameTestHelper.getPosition(2);
        Level level = this.game.getLevel(pos[0], pos[1]);
        assertTrue(level != null);

        LevelWriter.saveOriginal(this.game, level);

        Level savedLevel = LevelLoader.loadOriginal(this.game, pos);
        assertTrue(savedLevel != null);
        assertTrue(savedLevel.equals(level));

        LevelWriter.deleteOriginal(this.game, level);
        savedLevel = LevelLoader.loadOriginal(this.game, pos);
        assertFalse(savedLevel != null);
        assertFalse(level.equals(savedLevel));
    }

    @Test
    public void testCompleted() {
        int[] pos = GameTestHelper.getPosition(2);
        Level level = this.game.getLevel(pos[0], pos[1]);
        assertTrue(level != null);

        LevelWriter.saveCompleted(this.game, level);

        Level savedLevel = LevelLoader.loadCompleted(this.game, pos);
        assertTrue(savedLevel != null);
        assertTrue(savedLevel.equals(level));

        LevelWriter.deleteCompleted(this.game, level);
        savedLevel = LevelLoader.loadCompleted(this.game, pos);
        assertFalse(savedLevel != null);
        assertFalse(level.equals(savedLevel));
    }
}
