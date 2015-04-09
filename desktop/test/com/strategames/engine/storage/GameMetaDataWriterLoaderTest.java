package com.strategames.engine.storage;

import com.strategames.libgdx.junit.GameTestHelper;
import com.strategames.libgdx.junit.GdxTestRunner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(GdxTestRunner.class)
public class GameMetaDataWriterLoaderTest {
	private GameMetaData gameMetaData;

	@Before
	public void setUp() throws Exception {
        this.gameMetaData = GameTestHelper.createGame();
        assertTrue(this.gameMetaData != null);
	}

	@After
	public void tearDown() throws Exception {
		GameWriter.deleteOriginal(this.gameMetaData);
        GameWriter.deleteInprogress(this.gameMetaData);
	}

    @Test
    public void testOriginal() {
        GameWriter.saveOriginal(this.gameMetaData);

        GameMetaData savedGameMetaData = GameLoader.loadOriginal(this.gameMetaData);

        assertTrue(this.gameMetaData.equals(savedGameMetaData));
    }

    @Test
    public void testCompleted() {
        GameWriter.saveInProgress(this.gameMetaData);

        GameMetaData savedGameMetaData = GameLoader.loadInProgress(this.gameMetaData);

        assertTrue(this.gameMetaData.equals(savedGameMetaData));
    }
}
