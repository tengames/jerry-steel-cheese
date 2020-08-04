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
import woodyx.basicapi.physics.ObjectModel;
import woodyx.basicapi.sound.SoundManager;
import woodyx.basicapi.sprite.ObjectSprite;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Linear;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.tengames.jerrysteelcheese.interfaces.GlobalVariables;
import com.tengames.jerrysteelcheese.main.Assets;

public class Jerry extends ObjectSprite {
	private ObjectModel model;
	private TweenManager tween;
	private Vector2 target;
	private boolean isHide, isProcHide, isFinish;

	public Jerry(World world, TextureRegion textureRegion, Vector2 position, Vector2 size) {
		super(textureRegion, position.x, position.y, size.x, size.y);
		this.isHide = false;
		this.isProcHide = false;
		this.isFinish = false;

		// create jerry model
		model = new ObjectModel(world, ObjectModel.DYNAMIC, ObjectModel.CIRCLE,
				new Vector2(this.getWidth(), this.getHeight()), new Vector2(), this.getWidth() / 2, position, 0, 1,
				0.5f, 0.1f, GlobalVariables.CATEGORY_SCENERY, GlobalVariables.MASK_SCENERY, "jerry");
	}

	public void update(float deltaTime) {
		if (!isHide)
			this.updateFollowModel(model);
		if (tween != null)
			tween.update(deltaTime);
		if (isHide && !isProcHide) {
			// deactive model
			if (model.getBody().isActive())
				model.getBody().setActive(false);
			// run tween
			tween = new TweenManager();
			Timeline.createParallel().push(Tween.to(this, SpriteAccessor.ROTATION, 1f).ease(Linear.INOUT).target(360))
					.push(Tween.to(this, SpriteAccessor.OPACITY, 1f).ease(Linear.INOUT).target(0))
					.push(Tween.to(this, SpriteAccessor.SCALE_XY, 1f).ease(Linear.INOUT).target(0, 0))
					.push(Tween.to(this, SpriteAccessor.CPOS_XY, 1f).ease(Linear.INOUT).target(target.x, target.y))
					.setCallback(new TweenCallback() {
						@Override
						public void onEvent(int arg0, BaseTween<?> arg1) {
							isFinish = true;
						}
					}).start(tween);
			// turn on flag
			isProcHide = true;
		}
	}

	public void render(SpriteBatch batch) {
		this.draw(batch);
	}

	public void setTarget(Vector2 target) {
		this.target = target;
	}

	public void setHide() {
		if (!isHide) {
			// play sound
			SoundManager.playSound(Assets.soEat);
			isHide = true;
		}
	}

	public boolean getHide() {
		return this.isHide;
	}

	public boolean getFinish() {
		return this.isFinish;
	}
}
