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

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.tengames.jerrysteelcheese.main.Assets;

public class DynamicDialogHScore extends XDialog {
	private Button btSubmit, btExit;

	public DynamicDialogHScore(WindowStyle windowStyle, TextureRegion trBackground, Vector2 size, Vector2 position,
			Vector2 target, int hscore) {
		super(windowStyle, trBackground, size, position, target, TYPE_0);
		dialog.setTouchable(Touchable.disabled);

		Image imgHScore = new Image(Assets.taObjects.findRegion("text-hscore"));
		imgHScore.setPosition(374 / 2 - imgHScore.getWidth() / 2, 210);
		this.addActor(imgHScore);

		Label lbHScore = new Label(hscore + "", Assets.lbSStage);
		lbHScore.setAlignment(Align.center);
		lbHScore.setPosition((374 - lbHScore.getWidth()) / 2, (292 - lbHScore.getHeight()) / 2);
		this.addActor(lbHScore);

		// button
		btSubmit = new Button(new TextureRegionDrawable(Assets.taObjects.findRegion("bt-submit")));
		btSubmit.setPosition(374 / 2 - btSubmit.getWidth() / 2, 15);
		this.addActor(btSubmit);

		btExit = new Button(new TextureRegionDrawable(Assets.taObjects.findRegion("bt-exit")));
		btExit.setPosition(350, 270);
		this.addActor(btExit);

	}

	public Button getBtSubmit() {
		return btSubmit;
	}

	public Button getBtExit() {
		return btExit;
	}

}
