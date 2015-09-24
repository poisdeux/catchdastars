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
	private Level level;
    private GameMetaData metadata;
    private int[] pos;

	@Before
	public void setUp() throws Exception {
        Game game = GameTestHelper.createGame();
        this.metadata = game.getGameMetaData();
        this.pos = GameTestHelper.getPosition(2);
        this.level = game.getLevel(pos[0], pos[1]);
        assertTrue(level != null);
	}

	@After
	public void tearDown() throws Exception {
        GameWriter.deleteOriginal(metadata);
        GameWriter.deleteInprogress(metadata);
	}

    @Test
    public void saveOriginalTest() {
        assertTrue(LevelWriter.saveOriginal(level));

        Level savedLevel = LevelLoader.loadOriginal(metadata, pos);

        assertTrue(savedLevel != null);
        assertTrue(savedLevel.equals(level));

        LevelWriter.deleteOriginal(level);
        savedLevel = LevelLoader.loadOriginal(metadata, pos);
        assertFalse(savedLevel != null);
        assertFalse(level.equals(savedLevel));
    }

    @Test
    public void testCompleted() {
        LevelWriter.saveCompleted(level);

        Level savedLevel = LevelLoader.loadCompleted(metadata, pos);

        assertTrue(savedLevel != null);
        assertTrue(savedLevel.equals(level));

        LevelWriter.deleteCompleted(level);
        savedLevel = LevelLoader.loadCompleted(metadata, pos);
        assertFalse(savedLevel != null);
        assertFalse(level.equals(savedLevel));
    }
}
