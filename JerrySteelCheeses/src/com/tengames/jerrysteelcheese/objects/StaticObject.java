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
import woodyx.basicapi.sprite.ObjectSprite;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.TweenPaths;
import aurelienribon.tweenengine.equations.Linear;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.tengames.jerrysteelcheese.interfaces.GlobalVariables;
import com.tengames.jerrysteelcheese.main.Assets;

public class StaticObject extends ObjectSprite {
	private ObjectModel model;
	private TweenManager tween;
	private String name;
	private byte type;
	private boolean isDie, isProcDie;

	public StaticObject(World world, byte type, Vector2 position, Vector2 size, float angle) {
		super(position.x, position.y);
		this.type = type;
		this.isDie = false;
		this.isProcDie = false;

		// set texture region for sprites
		switch (type) {
		case IconModel.BALL:
			name = "ball";
			break;

		case IconModel.DISK:
			name = "disk";
			break;

		case IconModel.CHEESE:
			name = "cheese";
			// create tween
			tween = new TweenManager();
			Tween.to(this, SpriteAccessor.SCALE_XY, 1f + MathUtils.random(2)).ease(Linear.INOUT).target(0.8f, 0.8f)
					.path(TweenPaths.catmullRom).repeatYoyo(-1, 0).start(tween);
			break;

		case IconModel.IRON_1:
			name = "iron-1";
			break;

		case IconModel.IRON_2:
			name = "iron-2";
			break;

		case IconModel.IRON_3:
			name = "iron-3";
			break;

		case IconModel.IRON_4:
			name = "iron-4";
			break;

		default:
			break;
		}

		this.setRegion(Assets.taObjects.findRegion(name));
		this.setSize(size.x, size.y);
		this.setOriginCenter(this);

		// create models
		switch (type) {
		// basic shape
		case IconModel.BALL:
			// create ball
			model = new ObjectModel(world, ObjectModel.DYNAMIC, ObjectModel.CIRCLE,
					new Vector2(this.getWidth(), this.getHeight()), new Vector2(), this.getWidth() / 2, position, angle,
					5, 0.5f, 0.7f, GlobalVariables.CATEGORY_SCENERY, GlobalVariables.MASK_SCENERY, "ball");
			model.getBody().setBullet(true);
			break;

		case IconModel.CHEESE:
			model = new ObjectModel(world, ObjectModel.STATIC, ObjectModel.POLYGON,
					new Vector2(this.getWidth(), this.getHeight()), new Vector2(), 0, position, angle, 3, 0.1f, 0.1f,
					GlobalVariables.CATEGORY_SCENERY, GlobalVariables.MASK_SCENERY, "cheese");
			model.getBody().getFixtureList().get(0).setSensor(true);
			break;

		case IconModel.DISK:
			model = new ObjectModel(world, ObjectModel.DYNAMIC, ObjectModel.CIRCLE,
					new Vector2(this.getWidth(), this.getHeight()), new Vector2(), this.getWidth() / 2, position, angle,
					7, 0.5f, 0.1f, GlobalVariables.CATEGORY_SCENERY, GlobalVariables.MASK_SCENERY, "disk");
			break;

		case IconModel.IRON_1:
		case IconModel.IRON_2:
		case IconModel.IRON_3:
			model = new ObjectModel(world, ObjectModel.STATIC, ObjectModel.POLYGON,
					new Vector2(this.getWidth(), this.getHeight()), new Vector2(), 0, position, angle, 5, 0.1f, 0.1f,
					GlobalVariables.CATEGORY_SCENERY, GlobalVariables.MASK_SCENERY, "iron");
			break;

		case IconModel.IRON_4:
			model = new ObjectModel(world, ObjectModel.DYNAMIC, ObjectModel.POLYGON,
					new Vector2(this.getWidth(), this.getHeight()), new Vector2(), 0, position, angle, 1, 0.1f, 0.1f,
					GlobalVariables.CATEGORY_SCENERY, GlobalVariables.MASK_SCENERY, "iron");
			break;

		default:
			break;
		}
	}

	public void update(float deltaTime) {
		// update tween
		if (tween != null)
			tween.update(deltaTime);
		// update follow
		if (!isDie)
			this.updateFollowModel(model);
		// check die
		if (isDie && !isProcDie) {
			// deactive body
			if (model.getBody().isActive())
				model.getBody().setActive(false);
			// turn off flag
			isProcDie = true;
		}
	}

	public void render(SpriteBatch batch) {
		if (!isDie)
			this.draw(batch);
	}

	public void setShoot(float pow, float angle) {
		model.getBody().setLinearVelocity(new Vector2(pow * MathUtils.cos(angle), pow * MathUtils.sin(angle)));
	}

	public ObjectModel getModel() {
		return this.model;
	}

	public int getType() {
		return this.type;
	}

	public void setDie() {
		if (!isDie) {
			isDie = true;
		}
	}

	public boolean getDie() {
		return this.isDie;
	}

}
