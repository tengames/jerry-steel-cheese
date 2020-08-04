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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.tengames.jerrysteelcheese.interfaces.GlobalVariables;
import com.tengames.jerrysteelcheese.main.Assets;

public class UKey {
	private ObjectSprite spIron1, spIron2, spIron3;
	private ObjectModel mdIron1, mdIron2, mdIron3;

	public UKey(World world, Vector2 position, Vector2 size) {
		// create objectsprites
		spIron1 = new ObjectSprite(Assets.taObjects.findRegion("iron-4"), position.x, position.y, size.x, size.y);
		spIron2 = new ObjectSprite(Assets.taObjects.findRegion("iron-4-rotate"), position.x, position.y, size.y,
				size.x);
		spIron3 = new ObjectSprite(Assets.taObjects.findRegion("iron-4-rotate"), position.x + size.x, position.y,
				size.y, size.x);

		// create objectmodels
		mdIron1 = new ObjectModel(world, ObjectModel.DYNAMIC, ObjectModel.POLYGON,
				new Vector2(spIron1.getWidth(), spIron1.getHeight()), new Vector2(), 0, position, 0, 2, 0.5f, 0.1f,
				GlobalVariables.CATEGORY_SCENERY, GlobalVariables.MASK_SCENERY, "ukey");

		mdIron2 = new ObjectModel(world, ObjectModel.DYNAMIC, ObjectModel.POLYGON,
				new Vector2(spIron2.getWidth(), spIron2.getHeight()), new Vector2(), 0, position, 90, 2, 0.5f, 0.1f,
				GlobalVariables.CATEGORY_SCENERY, GlobalVariables.MASK_SCENERY, "ukey");

		mdIron3 = new ObjectModel(world, ObjectModel.DYNAMIC, ObjectModel.POLYGON,
				new Vector2(spIron3.getWidth(), spIron3.getHeight()), new Vector2(), 0,
				new Vector2(position.x + size.x, position.y), 90, 2, 0.5f, 0.1f, GlobalVariables.CATEGORY_SCENERY,
				GlobalVariables.MASK_SCENERY, "ukey");

		// create joint
		ObjectsJoint wJ1 = new ObjectsJoint(mdIron2, mdIron1, ObjectsJoint.WELD, false);
		wJ1.setAnchorA(0, -spIron2.getHeight() / 2 + spIron1.getHeight() / 2);
		wJ1.setAnchorB(-spIron1.getWidth() / 2 + spIron2.getWidth() / 2, 0);
		wJ1.createJoint(world);

		ObjectsJoint wJ2 = new ObjectsJoint(mdIron3, mdIron1, ObjectsJoint.WELD, false);
		wJ2.setAnchorA(0, -spIron3.getHeight() / 2 + spIron1.getHeight() / 2);
		wJ2.setAnchorB(spIron1.getWidth() / 2 - spIron3.getWidth() / 2, 0);
		wJ2.createJoint(world);
	}

	public void update(float deltaTime) {
		spIron1.updateFollowModel(mdIron1);
		spIron2.updateFollowModel(mdIron2);
		spIron3.updateFollowModel(mdIron3);
	}

	public void render(SpriteBatch batch) {
		spIron2.draw(batch);
		spIron3.draw(batch);
		spIron1.draw(batch);
	}
}
