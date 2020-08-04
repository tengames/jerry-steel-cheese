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

import woodyx.basicapi.sprite.ObjectSprite;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class IconModel extends ObjectSprite {
	public static final byte CANON = 0;
	public static final byte DISK = 1;
	public static final byte IRON_1 = 2;
	public static final byte IRON_2 = 3;
	public static final byte IRON_3 = 4;
	public static final byte IRON_4 = 5;
	public static final byte JERRY = 6;
	public static final byte SEESAW_NORMAL = 7;
	public static final byte SEESAW_AUTO = 8;
	public static final byte CHEESE = 9;
	public static final byte BALL = 10;
	public static final byte UKEY = 11;

	private boolean isTouch, canTouch;
	private byte type;

	public IconModel(TextureRegion texture, float x, float y, byte type, float scaleX, float scaleY) {
		super(texture, x, y);
		this.type = type;
		this.setScale(scaleX, scaleY);
		setTouch(false);
		setCanTouch(false);
	}

	public byte getType() {
		return type;
	}

	public boolean getTouch() {
		return isTouch;
	}

	public void setTouch(boolean isTouch) {
		this.isTouch = isTouch;
	}

	public boolean getCanTouch() {
		return canTouch;
	}

	public void setCanTouch(boolean canTouch) {
		this.canTouch = canTouch;
	}

	public boolean checkTouch(float x, float y) {
		if (x >= this.getX() && x <= (this.getX() + this.getWidth() * this.getScaleX()) && y >= this.getY()
				&& (y <= (this.getY() + this.getHeight() * this.getScaleY()))) {
			setTouch(true);
			return true;
		}
		return false;
	}
}
