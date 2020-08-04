/*
The MIT License

Copyright (c) 2014 kong <tengames.inc@gmail.com>

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
package com.tengames.jerrysteelcheese.main;

import woodyx.basicapi.screen.Asset;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

public class Assets extends Asset {
	public static Texture txBgMenu, txBgGame;
	public static TextureAtlas taSkin, taObjects;
	public static BitmapFont fNumber, fSmall, fStage;
	public static LabelStyle lbSNumber, lbSSmall, lbSStage;
	public static Music muBgMeu, muBgStage, muBgGame;
	public static Sound soClick, soEat, soWin, soFail, soShoot;

	public static void loadResLoading() {
		loading("drawable/", "atlas", "loading");
		assetManager.finishLoading();
	}

	public static void load() {
		// loading background
		loading("drawable/backgrounds/", "jpg", "bggame", "bgmenu");
		// loading objects
		loading("drawable/objects/", "atlas", "uiskin", "objects");
		// loading fonts
		loading("fonts/", "png", "numfont", "numfontrc");
		// loading sound
		loading("raw/", "ogg", "sobutton", "soeat", "sofail", "sowin", "soshoot");
		// loading music
		loading("raw/", "mp3", "bmmenu", "bmstage", "bmgame");
	}

	public static void loadDone() {
		// loaded backgrounds
		txBgMenu = assetManager.get("drawable/backgrounds/bgmenu.jpg");
		txBgMenu.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		txBgGame = assetManager.get("drawable/backgrounds/bggame.jpg");
		txBgGame.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		// loaded objects
		taSkin = assetManager.get("drawable/objects/uiskin.atlas");
		taObjects = assetManager.get("drawable/objects/objects.atlas");

		// loaded fonts
		Texture txNumFont = assetManager.get("fonts/numfont.png");
		txNumFont.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		fNumber = new BitmapFont(Gdx.files.internal("fonts/numfont.fnt"), new TextureRegion(txNumFont), false);
		fSmall = new BitmapFont(Gdx.files.internal("fonts/numfont.fnt"), new TextureRegion(txNumFont), false);
		fSmall.setScale(0.6f);

		lbSNumber = new LabelStyle();
		lbSNumber.font = fNumber;

		lbSSmall = new LabelStyle(fSmall, Color.YELLOW);

		txNumFont = assetManager.get("fonts/numfontrc.png");
		txNumFont.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		fStage = new BitmapFont(Gdx.files.internal("fonts/numfontrc.fnt"), new TextureRegion(txNumFont), false);
		fStage.setScale(0.6f);

		lbSStage = new LabelStyle();
		lbSStage.font = fStage;

		// loaded sound
		soClick = assetManager.get("raw/sobutton.ogg");
		soEat = assetManager.get("raw/soeat.ogg");
		soFail = assetManager.get("raw/sofail.ogg");
		soWin = assetManager.get("raw/sowin.ogg");
		soShoot = assetManager.get("raw/soshoot.ogg");

		// loaded music
		muBgGame = assetManager.get("raw/bmgame.mp3");
		muBgMeu = assetManager.get("raw/bmmenu.mp3");
		muBgStage = assetManager.get("raw/bmstage.mp3");
	}

}
