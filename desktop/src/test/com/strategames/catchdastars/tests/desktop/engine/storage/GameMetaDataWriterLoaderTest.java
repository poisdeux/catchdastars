package com.strategames.catchdastars.tests.desktop.engine.storage;

import com.strategames.catchdastars.tests.desktop.libgdx.junit.GameTestHelper;
import com.strategames.catchdastars.tests.desktop.libgdx.junit.GdxTestRunner;
import com.strategames.engine.storage.GameLoader;
import com.strategames.engine.storage.GameMetaData;
import com.strategames.engine.storage.GameWriter;
import com.strategames.engine.utils.Game;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(GdxTestRunner.class)
public class GameMetaDataWriterLoaderTest {
	private Game game;

	@Before
	public void setUp() throws Exception {
        this.game = GameTestHelper.createGame();
        assertTrue(this.game != null);
	}

	@After
	public void tearDown() throws Exception {
        GameMetaData metaData = this.game.getGameMetaData();
		GameWriter.deleteOriginal(metaData);
        GameWriter.deleteInprogress(metaData);
	}

    @Test
    public void testOriginal() {
        GameMetaData metaData = this.game.getGameMetaData();
        GameWriter.saveOriginal(metaData);

        GameMetaData savedGameMetaData = GameLoader.loadOriginal(metaData);

        assertTrue(metaData.equals(savedGameMetaData));
    }

    @Test
    public void testCompleted() {
        GameMetaData metaData = this.game.getGameMetaData();
        GameWriter.saveProgress(metaData);

        GameMetaData savedGameMetaData = GameLoader.loadInProgress(metaData);

        assertTrue(metaData.equals(savedGameMetaData));
    }
}
