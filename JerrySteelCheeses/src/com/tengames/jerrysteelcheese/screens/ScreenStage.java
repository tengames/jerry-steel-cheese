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
package com.tengames.jerrysteelcheese.screens;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import woodyx.basicapi.screen.XScreen;
import woodyx.basicapi.sound.SoundManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.tengames.jerrysteelcheese.main.Assets;
import com.tengames.jerrysteelcheese.main.JerrySteelCheese;
import com.tengames.jerrysteelcheese.objects.DynamicButton;

public class ScreenStage extends XScreen implements Screen {
	public static boolean canNextLevel11 = false;

	private JerrySteelCheese coreGame;
	private Stage stage;
	private DynamicButton[] buttons;
	private ArrayList<DynamicButton> maps;
	private ArrayList<Integer> mapInfor;
	private BufferedReader reader;
	private String strJson;

	public ScreenStage(JerrySteelCheese coreGame) {
		super(800, 480);
		this.coreGame = coreGame;
		initialize();
	}

	@Override
	public void initialize() {
		// get data
		SoundManager.MUSIC_ENABLE = coreGame.androidListener.getSound();
		SoundManager.SOUND_ENABLE = coreGame.androidListener.getSound();

		// play music
		SoundManager.playMusic(Assets.muBgStage, 0.5f, true);

		// create stage
		stage = new Stage(800, 480, true) {
			@Override
			public boolean keyUp(int keyCode) {
				if (keyCode == Keys.BACK) {
					// back to menu
					coreGame.setScreen(new ScreenMenu(coreGame));

					// show admob
					if (MathUtils.random(100) > 50)
						coreGame.androidListener.showIntertitial();
				}
				return super.keyUp(keyCode);
			}

		};
		((OrthographicCamera) stage.getCamera()).setToOrtho(false, 800, 480);
		Gdx.input.setInputProcessor(stage);
		Gdx.input.setCatchBackKey(true);

		/* create buttons */
		buttons = new DynamicButton[3];

		// logo
		buttons[0] = new DynamicButton(Assets.taObjects.findRegion("text-level"), new Vector2(50, 1000),
				new Vector2(50, 80));

		// menu
		buttons[1] = new DynamicButton(Assets.taObjects.findRegion("bt-menu"), new Vector2(5, 20), 0.6f, 1);
		buttons[1].getButton().addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// play sound
				SoundManager.playSound(Assets.soClick);
				// back to menu
				coreGame.setScreen(new ScreenMenu(coreGame));

				// show admob
				if (MathUtils.random(100) > 50)
					coreGame.androidListener.showIntertitial();
			}
		});

		// sound
		buttons[2] = new DynamicButton(Assets.taObjects.findRegion("bt-soundon"),
				Assets.taObjects.findRegion("bt-soundoff"), new Vector2(720, 420), 0.7f, 1.2f);
		buttons[2].getButton().setChecked(!SoundManager.SOUND_ENABLE);

		buttons[2].getButton().addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// play sound
				SoundManager.playSound(Assets.soClick);
				// turn off sound and music
				SoundManager.MUSIC_ENABLE = !SoundManager.MUSIC_ENABLE;
				if (!SoundManager.MUSIC_ENABLE) {
					SoundManager.pauseMusic(Assets.muBgStage);
				} else {
					SoundManager.playMusic(Assets.muBgStage, 0.5f, true);
				}
				SoundManager.SOUND_ENABLE = !SoundManager.SOUND_ENABLE;
				// save sound
				coreGame.androidListener.setSound(SoundManager.SOUND_ENABLE);
			}
		});

		// add to stage
		for (DynamicButton button : buttons) {
			if (button != null)
				stage.addActor(button);
		}

		/* loading data */
//		try {
//			reader = new BufferedReader(new FileReader("/home/woodyx/workspace-gdx/JerrySteelCheesesAndroid/assets/data/jerrysteelcheese.txt"));
//			reader = new BufferedReader(new FileReader("jerrysteelcheese.txt"));
//		} catch (FileNotFoundException e) {}
		reader = coreGame.androidListener.getData();

		/* creat maps */
		mapInfor = new ArrayList<Integer>();

		// get data
		for (int i = 0; i < 20; i++) {
			int value = coreGame.androidListener.getValue(i);
			if (value == 0)
				break;
			mapInfor.add(value);
		}
		maps = new ArrayList<DynamicButton>();
		float timeDelay = 1;

		Table table = new Table();
		table.setSize(600, 480);
		table.setBounds(100, 0, 600, 480);
		for (int i = 0; i < 20; i++) {
			DynamicButton button = null;
			if (mapInfor.size() > 20) {
				button = new DynamicButton(mapInfor.get(i).intValue(), maps.size() + 1, timeDelay);
			} else {
				if (i < mapInfor.size())
					button = new DynamicButton(mapInfor.get(i).intValue(), maps.size() + 1, timeDelay);
				else
					button = new DynamicButton(0, maps.size() + 1, timeDelay);
			}
			maps.add(button);
			timeDelay += maps.size() * 0.01f;
			table.add(button).pad(10, 10, 10, 10);
			if ((i + 1) % 5 == 0)
				table.row();
		}

		stage.addActor(table);

		// add listener for stages
		for (int i = 0; i < maps.size(); i++) {
			final int map = i;
			maps.get(map).getButton().addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					// play sound
					SoundManager.playSound(Assets.soClick);
					// go to screen game
					if (maps.get(map).getValue() != 0)
						export(map);
				}
			});
		}
	}

	private void export(int number) {
		String line = null, strExport = "MAP: " + (number + 1);
		try {
			while ((line = reader.readLine()) != null) {
				if (line.equals(strExport)) {
					// export strJson
					strJson = reader.readLine();
					break;
				}
			}
			// close reader
			reader.close();
			// set new Screen
			coreGame.setScreen(new ScreenGame(coreGame, strJson, (number + 1)));
		} catch (IOException e) {
		}
	}

	@Override
	public void update(float deltaTime) {
		// check can next stage
		if (canNextLevel11) {
			export(10);
			canNextLevel11 = false;
		}

		// update buttons
		for (DynamicButton button : buttons) {
			if (button != null)
				button.update(deltaTime);
		}

		// update maps
		for (DynamicButton map : maps) {
			if (map != null)
				map.update(deltaTime);
		}

		// update stage
		stage.act(deltaTime);
	}

	@Override
	public void draw() {
		renderBackground();
		renderStage();
	}

	private void renderBackground() {
		bgDrawable(true);
		batch.draw(Assets.txBgGame, 0, 0, 800, 480);
		bgDrawable(false);
	}

	private void renderStage() {
		stage.draw();
	}

	@Override
	public void render(float deltaTime) {
		clearScreen(deltaTime);
		update(deltaTime);
		draw();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		// play music
		SoundManager.playMusic(Assets.muBgStage, 0.5f, true);
	}

	@Override
	public void hide() {
		// pause music
		SoundManager.pauseMusic(Assets.muBgStage);
	}

	@Override
	public void pause() {
		// pause music
		SoundManager.pauseMusic(Assets.muBgStage);
	}

	@Override
	public void resume() {
		// play music
		SoundManager.playMusic(Assets.muBgStage, 0.5f, true);
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

}
