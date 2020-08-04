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
package com.tengames.jerrysteelcheese.objects;

import woodyx.basicapi.gui.XDialog;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.tengames.jerrysteelcheese.main.Assets;
import com.tengames.jerrysteelcheese.screens.ScreenGame;

public class DynamicDialog extends XDialog {
	private Button btMenu, btReplay, btNext, btMoreGame, btStage;
	private int value;

	public DynamicDialog(WindowStyle windowStyle, Vector2 position, Vector2 target, int boomUsed, int totalBoom,
			byte type) {
		super(windowStyle, Assets.taObjects.findRegion("dark"), new Vector2(800, 480), position, target, TYPE_0);
		dialog.setTouchable(Touchable.disabled);

		Image imgDialog = null;

		switch (type) {
		case ScreenGame.STATE_WIN:
			// dialog
			imgDialog = new Image(Assets.taObjects.findRegion("text-win"));
			imgDialog.setPosition(150, 200);
			this.addActor(imgDialog);

			Image imgCup = null;
			if ((float) (boomUsed / totalBoom) <= 0.5f) {
				imgCup = new Image(Assets.taObjects.findRegion("star-3"));
				setValue(3);
			} else if ((float) (boomUsed / totalBoom) > 0.5f && (float) (boomUsed / totalBoom) <= 0.8f) {
				imgCup = new Image(Assets.taObjects.findRegion("star-2"));
				setValue(2);
			} else {
				imgCup = new Image(Assets.taObjects.findRegion("star-1"));
				setValue(1);
			}

			imgCup.setPosition(500, 280);
			this.addActor(imgCup);

			// button
			btStage = new Button(new TextureRegionDrawable(Assets.taObjects.findRegion("bt-level-select")));
			btStage.setPosition(50, 60);
			this.addActor(btStage);

			btReplay = new Button(new TextureRegionDrawable(Assets.taObjects.findRegion("bt-tryagain")));
			btReplay.setPosition(230, 60);
			this.addActor(btReplay);

			btMenu = new Button(new TextureRegionDrawable(Assets.taObjects.findRegion("bt-main-menu")));
			btMenu.setPosition(590, 60);
			this.addActor(btMenu);

			btNext = new Button(new TextureRegionDrawable(Assets.taObjects.findRegion("bt-next")));
			btNext.setPosition(410, 60);
			this.addActor(btNext);

			break;

		case ScreenGame.STATE_LOOSE:
			// dialog
			imgDialog = new Image(Assets.taObjects.findRegion("text-fail"));
			imgDialog.setPosition(150, 200);
			this.addActor(imgDialog);

			// button
			btStage = new Button(new TextureRegionDrawable(Assets.taObjects.findRegion("bt-level-select")));
			btStage.setPosition(50, 60);
			this.addActor(btStage);

			btReplay = new Button(new TextureRegionDrawable(Assets.taObjects.findRegion("bt-tryagain")));
			btReplay.setPosition(230, 60);
			this.addActor(btReplay);

			btMenu = new Button(new TextureRegionDrawable(Assets.taObjects.findRegion("bt-main-menu")));
			btMenu.setPosition(410, 60);
			this.addActor(btMenu);

			btMoreGame = new Button(new TextureRegionDrawable(Assets.taObjects.findRegion("bt-more-game")));
			btMoreGame.setPosition(590, 60);
			this.addActor(btMoreGame);

			break;

		default:
			break;
		}
	}

	public Button getBtMenu() {
		return btMenu;
	}

	public Button getBtReplay() {
		return btReplay;
	}

	public Button getBtNext() {
		return btNext;
	}

	public Button getBtLevel() {
		return btStage;
	}

	public Button getBtMoreGame() {
		return btMoreGame;
	}

	public int getValue() {
		return value;
	}

	private void setValue(int value) {
		this.value = value;
	}

}
