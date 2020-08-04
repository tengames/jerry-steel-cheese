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

import woodyx.basicapi.accessor.ActorAccessor;
import woodyx.basicapi.accessor.SpriteAccessor;
import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tengames.jerrysteelcheese.interfaces.AndroidListener;
import com.tengames.jerrysteelcheese.screens.ScreenLoading;

public class JerrySteelCheese extends Game {
	public AndroidListener androidListener;

	@Override
	public void create() {
		// set tween manager
		Tween.setWaypointsLimit(5);
		Tween.setCombinedAttributesLimit(5);
		Tween.registerAccessor(Sprite.class, new SpriteAccessor());
		Tween.registerAccessor(Actor.class, new ActorAccessor());
		// set screen
		setScreen(new ScreenLoading(this));
	}

	@Override
	public void dispose() {
		super.dispose();
		getScreen().dispose();
	}

	public void registerAndroidInterface(AndroidListener androidListener) {
		this.androidListener = androidListener;
	}
}
