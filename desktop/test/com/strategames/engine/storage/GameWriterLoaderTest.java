package com.strategames.engine.storage;

import com.strategames.engine.utils.Game;
import com.strategames.libgdx.junit.GameTestHelper;
import com.strategames.libgdx.junit.GdxTestRunner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(GdxTestRunner.class)
public class GameWriterLoaderTest {
	private Game game;

	@Before
	public void setUp() throws Exception {
        this.game = GameTestHelper.createGame();
        assertTrue(this.game != null);
	}

	@After
	public void tearDown() throws Exception {
		GameWriter.deleteOriginal(this.game);
        GameWriter.deleteInprogress(this.game);
	}

    @Test
    public void testOriginal() {
        GameWriter.saveOriginal(this.game);

        Game savedGame = GameLoader.loadOriginal(this.game);

        assertTrue(this.game.equals(savedGame));
    }

    @Test
    public void testCompleted() {
        GameWriter.saveInProgress(this.game);

        Game savedGame = GameLoader.loadInProgress(this.game);

        assertTrue(this.game.equals(savedGame));
    }
}
