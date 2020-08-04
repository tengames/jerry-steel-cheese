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

import woodyx.basicapi.physics.ObjectModel;
import woodyx.basicapi.physics.ObjectsJoint;
import woodyx.basicapi.sprite.ObjectSprite;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.tengames.jerrysteelcheese.interfaces.GlobalVariables;

public class SeeSaw extends ObjectSprite {
	private ObjectModel model, anchor;

	public SeeSaw(World world, TextureRegion textureRegion, Vector2 position, Vector2 size, float angle,
			boolean isAuto) {
		super(textureRegion, position.x, position.y, size.x, size.y);

		// create models
		anchor = new ObjectModel(world, ObjectModel.STATIC, ObjectModel.POLYGON, new Vector2(5, 5), new Vector2(), 0,
				new Vector2(this.getX() + this.getWidth() / 2, this.getY()), 0, 10, 0.5f, 0.1f,
				GlobalVariables.CATEGORY_SCENERY, GlobalVariables.MASK_SCENERY, "seesaw");
		model = new ObjectModel(world, ObjectModel.DYNAMIC, ObjectModel.POLYGON, size, new Vector2(), 0, position,
				angle, 3, 0.5f, 0.1f, GlobalVariables.CATEGORY_SCENERY, GlobalVariables.MASK_SCENERY, "seesaw");

		// create joints
		ObjectsJoint joint = new ObjectsJoint(anchor, model, ObjectsJoint.REVOLUTE, false);
		joint.setAnchorB(0, 0);
		joint.setAnchorA(0, 0);
		if (isAuto)
			joint.setMotor(20, 30, true);
		joint.createJoint(world);
	}

	public void update(float deltaTime) {
		// update follow
		this.updateFollowModel(model);
	}

	public void render(SpriteBatch batch) {
		// render
		this.draw(batch);
	}
}
