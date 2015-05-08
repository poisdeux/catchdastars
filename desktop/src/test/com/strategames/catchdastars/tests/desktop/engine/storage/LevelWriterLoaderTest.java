package com.strategames.catchdastars.tests.desktop.engine.storage;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.strategames.catchdastars.tests.desktop.libgdx.junit.GameTestHelper;
import com.strategames.catchdastars.tests.desktop.libgdx.junit.GdxTestRunner;
import com.strategames.engine.storage.GameMetaData;
import com.strategames.engine.storage.GameWriter;
import com.strategames.engine.storage.LevelLoader;
import com.strategames.engine.storage.LevelWriter;
import com.strategames.engine.utils.Game;
import com.strategames.engine.utils.Level;

@RunWith(GdxTestRunner.class)
public class LevelWriterLoaderTest {
	private Game game;

	@Before
	public void setUp() throws Exception {
        this.game = GameTestHelper.createGame();
	}

	@After
	public void tearDown() throws Exception {
        GameMetaData metadata = this.game.getGameMetaData();
//        GameWriter.deleteOriginal(metadata);
//        GameWriter.deleteInprogress(metadata);
	}

    @Test
    public void saveOriginalTest() {
        GameMetaData metadata = this.game.getGameMetaData();
        int[] pos = GameTestHelper.getPosition(2);
        Level level = this.game.getLevel(pos[0], pos[1]);
        assertTrue(level != null);

        Array<Vector2> accessibleBy = level.getAccessibleBy();
        for( Vector2 v : accessibleBy ) {
            Gdx.app.log("LevelWriterLoaderTest", "saveOriginalTest: accessibleBy="+v);
        }

        //level.delAccessibleBy(0,1);
        level.addAccessibleBy(0,2);

        assertTrue(LevelWriter.saveOriginal(metadata, level));

        Level savedLevel = LevelLoader.loadOriginal(metadata, pos);
        assertTrue(savedLevel != null);
        assertTrue(savedLevel.equals(level));

//        LevelWriter.deleteOriginal(metadata, level);
//        savedLevel = LevelLoader.loadOriginal(metadata, pos);
//        assertFalse(savedLevel != null);
//        assertFalse(level.equals(savedLevel));
    }

    @Test
    public void testCompleted() {
        GameMetaData metadata = this.game.getGameMetaData();
        int[] pos = GameTestHelper.getPosition(2);
        Level level = this.game.getLevel(pos[0], pos[1]);
        assertTrue(level != null);

        LevelWriter.saveCompleted(metadata, level);

        Level savedLevel = LevelLoader.loadCompleted(metadata, pos);
        assertTrue(savedLevel != null);
        assertTrue(savedLevel.equals(level));

        LevelWriter.deleteCompleted(metadata, level);
        savedLevel = LevelLoader.loadCompleted(metadata, pos);
        assertFalse(savedLevel != null);
        assertFalse(level.equals(savedLevel));
    }
}
