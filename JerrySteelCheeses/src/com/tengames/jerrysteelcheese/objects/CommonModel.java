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

import com.badlogic.gdx.math.Vector2;

public class CommonModel {
	private byte type;
	private Vector2 position, size;
	private float rotation;

	public CommonModel() {
	}

	public CommonModel(byte type, Vector2 position, Vector2 size, float rotation) {
		this.type = type;
		this.position = position;
		this.size = size;
		this.rotation = rotation;
	}

	public byte getType() {
		return type;
	}

	public Vector2 getPosition() {
		return position;
	}

	public Vector2 getPositionCenter() {
		return new Vector2(position.x + size.x / 2, position.y + size.y / 2);
	}

	public float getRotation() {
		return rotation;
	}

	public Vector2 getSize() {
		return size;
	}
}
