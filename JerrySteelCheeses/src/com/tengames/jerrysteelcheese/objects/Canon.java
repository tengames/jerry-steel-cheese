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
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Quart;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.tengames.jerrysteelcheese.main.Assets;

public class Canon extends ObjectSprite {
	private ObjectSprite spWheel, spStrengPow, spTom;
	private TweenManager tweenPrimary, tweenSecondary, tweenTom;
	private Vector2 anchor = new Vector2();
	private float angle, pow, rateWidth, rateHeight;
	private byte numBullet;
	private boolean isFinishMove, isPrepare, isPresent, canShoot;

	public Canon(TextureRegion textureRegion, Vector2 position, Vector2 size, Vector2 target, int numBullet) {
		super(textureRegion, position.x, position.y, size.x, size.y);
		this.isFinishMove = false;
		this.isPrepare = false;
		this.canShoot = false;
		this.isPresent = true;
		this.numBullet = (byte) numBullet;
		this.angle = 0;
		this.pow = 0;

		rateWidth = size.x / textureRegion.getRegionWidth();
		rateHeight = size.y / textureRegion.getRegionHeight();

		this.setOrigin(27 * rateWidth, size.y / 2);

		// wheel
		spWheel = new ObjectSprite(Assets.taObjects.findRegion("wheel"), position.x - 20 * rateWidth,
				position.y - 15 * rateHeight);
		spWheel.setSize(rateWidth * Assets.taObjects.findRegion("wheel").getRegionWidth(),
				rateHeight * Assets.taObjects.findRegion("wheel").getRegionHeight());
		spWheel.setOriginCenter(spWheel);

		// power control
		spStrengPow = new ObjectSprite(Assets.taObjects.findRegion("power-control"),
				position.x + this.getWidth() - 20 * rateWidth, position.y + this.getHeight() / 2, 149 * rateWidth,
				73 * rateHeight);
		spStrengPow.setOrigin(-102 * rateWidth, spStrengPow.getHeight() / 2);
		spStrengPow.setPosition(position.x + this.getWidth() - 20 * rateWidth, position.y + this.getHeight() / 2);
		spStrengPow.setScale(0, 0);

		// tom
		spTom = new ObjectSprite(Assets.taObjects.findRegion("tom"), -200, position.y - 15 * rateHeight);
		spTom.setSize(rateWidth * 0.8f * Assets.taObjects.findRegion("tom").getRegionWidth(),
				rateHeight * 0.8f * Assets.taObjects.findRegion("tom").getRegionHeight());

		// create tween
		tweenPrimary = new TweenManager();
		tweenSecondary = new TweenManager();
		tweenTom = new TweenManager();

		Tween.to(this, SpriteAccessor.POS_XY, 2f).ease(Linear.INOUT).target(target.x, target.y).start(tweenPrimary);

		Timeline.createParallel().push(Tween.to(spWheel, SpriteAccessor.ROTATION, 2f).ease(Linear.INOUT).target(-360))
				.push(Tween.to(spWheel, SpriteAccessor.POS_XY, 2f).ease(Linear.INOUT).target(target.x - 20 * rateWidth,
						target.y - 15 * rateHeight))
				.setCallback(new TweenCallback() {
					@Override
					public void onEvent(int arg0, BaseTween<?> arg1) {
						// rotate canon
						Tween.to(Canon.this, SpriteAccessor.ROTATION, 1f).ease(Linear.INOUT).target(45)
								.setCallback(new TweenCallback() {
									@Override
									public void onEvent(int arg0, BaseTween<?> arg1) {
										// tom appear
										Tween.to(spTom, SpriteAccessor.POS_XY, 0.5f).ease(Quart.OUT)
												.target(Canon.this.getX() - 50 * rateWidth,
														Canon.this.getY() - 15 * rateHeight)
												.start(tweenTom);
										// change flag
										canShoot = true;
										isPresent = false;
									}
								}).start(tweenPrimary);
					}
				}).start(tweenSecondary);
	}

	public void update(float deltaTime) {
		// update tom
		tweenTom.update(deltaTime);
		// update present
		if (isPresent) {
			tweenPrimary.update(deltaTime);
			tweenSecondary.update(deltaTime);
		} else {
			if (isPrepare) {
				// update controller
				spStrengPow.setPosition(this.getX() + this.getWidth() - 20 * rateWidth,
						this.getY() + (this.getHeight() - spStrengPow.getHeight()) / 2);
				// update for shoot
				this.setRotation(angle * MathUtils.radiansToDegrees);
				spStrengPow.setRotation(angle * MathUtils.radiansToDegrees);
			}
		}
	}

	public boolean getTouch(float touchX, float touchY) {
		if (canShoot) {
			anchor = new Vector2(touchX, touchY);
			setPrepare(true);
			return true;
		}
		return false;
	}

	public void dragg(float touchX, float touchY) {
		// angle
		angle = MathUtils.atan2((touchY - anchor.y), (touchX - anchor.x));
		if (angle < 0)
			angle = 0;
		else if (angle > 1.5f)
			angle = 1.5f;

		// power
		pow = (float) Math.sqrt(Math.pow((touchX - anchor.x), 2) + Math.pow((touchY - anchor.y), 2));
		if (pow <= 50)
			pow = 50;
		else if (pow >= 210)
			pow = 210;
		spStrengPow.setScale((float) (pow * 1 / 160), 1);
	}

	public void render(SpriteBatch batch) {
		if (isPrepare && numBullet > 0)
			spStrengPow.draw(batch);
		this.draw(batch);
		spWheel.draw(batch);
		spTom.draw(batch);
	}

	public void setPrepare(boolean isPrepare) {
		this.isPrepare = isPrepare;
	}

	public boolean getPrepare() {
		return this.isPrepare;
	}

	public float getAngle() {
		return this.angle;
	}

	public float getPow() {
		return (pow * 11 / 250);
	}

	public float getRateScale() {
		return this.rateWidth;
	}

	public byte getBullet() {
		return this.numBullet;
	}

	public void decreaseBullet() {
		if (numBullet - 1 >= 0)
			numBullet--;
	}

	public Vector2 getShootPoint() {
		if (angle < 0)
			return new Vector2(this.getX() + this.getWidth() / 2, this.getY() + this.getHeight() / 2);
		else if (angle > 1.5f)
			return new Vector2(this.getX() + this.getWidth() / 2, this.getY() + this.getWidth() / 2);
		else
			return new Vector2(this.getX() + this.getWidth() / 2 + 130 * rateWidth - this.getWidth() * angle,
					this.getY() + this.getHeight() / 2 + this.getHeight() / 2 * angle);
	}

	public boolean getFinishMove() {
		return isFinishMove;
	}

	public boolean getCanShoot() {
		return canShoot;
	}

}
