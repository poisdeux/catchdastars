/**
 * 
 * Copyright 2015 Martijn Brekhof
 *
 * This file is part of Catch Da Stars.
 *
 * Catch Da Stars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Catch Da Stars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Catch Da Stars.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.strategames.catchdastars.tests.desktop.engine.storage;

import com.badlogic.gdx.Gdx;
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

        Gdx.app.log("LevelWriterLoaderTest", "testOriginal: savedGameMetaData="+savedGameMetaData);
        Gdx.app.log("LevelWriterLoaderTest", "testOriginal: metaData="+metaData);

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
