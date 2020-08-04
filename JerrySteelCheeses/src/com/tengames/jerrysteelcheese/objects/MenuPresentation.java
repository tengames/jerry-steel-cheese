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

import woodyx.basicapi.accessor.SpriteAccessor;
import woodyx.basicapi.sprite.ObjectSprite;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Elastic;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Quart;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.tengames.jerrysteelcheese.main.Assets;

public class MenuPresentation {
	private TweenManager tween;
	private ObjectSprite spPresent;

	public MenuPresentation(Vector2 position, float delay, int type) {
		// create tween
		tween = new TweenManager();

		switch (type) {
		// cheese
		case 1:
			// create sprite
			spPresent = new ObjectSprite(Assets.taObjects.findRegion("cheese"), position.x, position.y);
			spPresent.setOriginCenter(spPresent);

			// apply tween
			Timeline.createSequence().push(Tween.set(spPresent, SpriteAccessor.SCALE_XY).target(0, 0))
					.push(Tween.to(spPresent, SpriteAccessor.SCALE_XY, 1f).target(1, 1).ease(Elastic.OUT))
					.push(Tween.to(spPresent, SpriteAccessor.SCALE_XY, 1).target(0.8f, 0.8f).ease(Linear.INOUT)
							.repeatYoyo(1000, 0))
					.start(tween);
			break;

		// tom and jerry
		case 2:
			// create sprite
			spPresent = new ObjectSprite(Assets.taObjects.findRegion("tomandjerrymenu"), -300, position.y);

			// apply tween
			Timeline.createSequence().pushPause(delay)
					.push(Tween.to(spPresent, SpriteAccessor.POS_XY, 1).target(position.x, position.y).ease(Quart.OUT))
					.push(Tween.to(spPresent, SpriteAccessor.POS_XY, 1).target(position.x - 20, position.y)
							.ease(Linear.INOUT).repeatYoyo(1000, 0))
					.start(tween);
			break;

		default:
			break;
		}
	}

	public void update(float deltaTime) {
		tween.update(deltaTime);
	}

	public void render(SpriteBatch batch) {
		spPresent.draw(batch);
	}
}
