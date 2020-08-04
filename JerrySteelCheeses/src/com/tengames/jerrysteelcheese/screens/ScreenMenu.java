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
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import woodyx.basicapi.screen.XScreen;
import woodyx.basicapi.sound.SoundManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.tengames.jerrysteelcheese.interfaces.GlobalVariables;
import com.tengames.jerrysteelcheese.json.JSONObject;
import com.tengames.jerrysteelcheese.main.Assets;
import com.tengames.jerrysteelcheese.main.JerrySteelCheese;
import com.tengames.jerrysteelcheese.objects.DynamicButton;
import com.tengames.jerrysteelcheese.objects.DynamicDialogHScore;
import com.tengames.jerrysteelcheese.objects.MenuPresentation;

@SuppressWarnings("deprecation")
public class ScreenMenu extends XScreen implements Screen {
	private JerrySteelCheese coreGame;
	private Stage stage;
	private DynamicButton[] buttons;
	private MenuPresentation[] presents;
	private DynamicDialogHScore dialog;

	public ScreenMenu(JerrySteelCheese coreGame) {
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
		SoundManager.playMusic(Assets.muBgMeu, 0.5f, true);

		// create stage
		stage = new Stage(800, 480, true) {
			private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back
															// presses.
			private long mBackPressed;

			@Override
			public boolean keyUp(int keyCode) {
				if (keyCode == Keys.BACK) {
					// play sound
					SoundManager.playSound(Assets.soClick);

					if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
						// exit game
						System.exit(0);
					} else {
						coreGame.androidListener.showToast("Tap back button in order to exit !");
					}

					mBackPressed = System.currentTimeMillis();
				}
				return super.keyUp(keyCode);
			}

		};
		((OrthographicCamera) stage.getCamera()).setToOrtho(false, 800, 480);
		Gdx.input.setInputProcessor(stage);
		Gdx.input.setCatchBackKey(true);

		/* create presentation */
		presents = new MenuPresentation[2];
		presents[0] = new MenuPresentation(new Vector2(580, 50), 0, 1);
		presents[1] = new MenuPresentation(new Vector2(0, 0), 1, 2);

		/* create buttons */
		buttons = new DynamicButton[5];
		createButtons();
	}

	private void createButtons() {
		// logo
		buttons[0] = new DynamicButton(Assets.taObjects.findRegion("logo"), new Vector2(200, 600),
				new Vector2(200, 300), 2.5f);

		// sound
		buttons[1] = new DynamicButton(Assets.taObjects.findRegion("bt-soundon"),
				Assets.taObjects.findRegion("bt-soundoff"), new Vector2(720, 420), 0.7f, 3);
		buttons[1].getButton().setChecked(!SoundManager.SOUND_ENABLE);

		buttons[1].getButton().addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// play sound
				SoundManager.playSound(Assets.soClick);
				// turn off sound and music
				SoundManager.MUSIC_ENABLE = !SoundManager.MUSIC_ENABLE;
				if (!SoundManager.MUSIC_ENABLE) {
					SoundManager.pauseMusic(Assets.muBgMeu);
				} else {
					SoundManager.playMusic(Assets.muBgMeu, 0.5f, true);
				}
				SoundManager.SOUND_ENABLE = !SoundManager.SOUND_ENABLE;
				// save sound
				coreGame.androidListener.setSound(SoundManager.SOUND_ENABLE);
			}
		});

		// button play
		buttons[2] = new DynamicButton(Assets.taObjects.findRegion("bt-start"), new Vector2(327, 600),
				new Vector2(327, 200), 3.5f);
		buttons[2].getButton().addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// play sound
				SoundManager.playSound(Assets.soClick);
				// go to screen stage
				coreGame.setScreen(new ScreenStage(coreGame));
			}
		});

		// button highscore
		buttons[3] = new DynamicButton(Assets.taObjects.findRegion("bt-moregame"), new Vector2(327, 600),
				new Vector2(327, 110), 4f);
		buttons[3].getButton().addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// play sound
				SoundManager.playSound(Assets.soClick);

				// show dialog
				final Image imgDark = new Image(Assets.taObjects.findRegion("dark"));
				imgDark.setSize(800, 480);
				imgDark.setPosition(0, 0);

				WindowStyle windowStyle = new WindowStyle();
				windowStyle.titleFont = Assets.fNumber;
				dialog = new DynamicDialogHScore(windowStyle, Assets.taObjects.findRegion("dialog"),
						new Vector2(374, 292), new Vector2(400, 900), new Vector2(400, 240),
						coreGame.androidListener.getHscore());

				dialog.getBtSubmit().addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
//						 play sound
						SoundManager.playSound(Assets.soClick);

						// remove
						imgDark.remove();
						dialog.remove();
						dialog = null;

						// TODO: highscore
						try {
							sendRequest();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

				dialog.getBtExit().addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
//						 play sound
						SoundManager.playSound(Assets.soClick);

						// remove
						imgDark.remove();
						dialog.remove();
						dialog = null;
					}
				});

				// add to stage
				stage.addActor(imgDark);
				stage.addActor(dialog);

			}
		});

		// button store
		buttons[4] = new DynamicButton(Assets.taObjects.findRegion("bt-about"), new Vector2(327, 600),
				new Vector2(327, 20), 4.5f);
		buttons[4].getButton().addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// play sound
				SoundManager.playSound(Assets.soClick);
				// TODO: go to store
				coreGame.androidListener.gotoStore(GlobalVariables.STORE_URL);
			}
		});

		// add to stage
		for (DynamicButton button : buttons) {
			if (button != null)
				stage.addActor(button);
		}
	}

	// HTTP GET request
	@SuppressWarnings({ "resource", "unused" })
	private void sendRequest() throws Exception {
		coreGame.androidListener.showLoading(true);

		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(getHighScoreReq(coreGame.androidListener.getDeviceId(), "6325587836665856",
				"Jerry%20Steel%20Cheese", coreGame.androidListener.getHscore(), ""));

		// add request header
		request.addHeader("User-Agent", "Android");
		HttpResponse response = client.execute(request);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}

		coreGame.androidListener.showLoading(false);

		// show result and code
		if (response == null) {
			coreGame.androidListener.showToast("Network Error !");
		}

		if (response.getStatusLine().getStatusCode() == 600) { // error code
			coreGame.androidListener.showDialog(result.toString());
		} else if (response.getStatusLine().getStatusCode() == 200) { // valid code
			JSONObject json = new JSONObject(result.toString());

			if (!json.getString(GlobalVariables.RES_INFORM).equals("")) {

			}

			if (!json.getString(GlobalVariables.RES_BONUS).equals("")) {

			}

			if (!json.getString(GlobalVariables.RES_MESSAGE).equals("")) {

			}

			if (!json.getString(GlobalVariables.RES_LINK).equals("")) {
				coreGame.androidListener.gotoHighscore(json.getString(GlobalVariables.RES_LINK));
			}

		}

	}

	private String getHighScoreReq(String deviceId, String gameId, String gameName, int score, String params) {
		return (GlobalVariables.HSCORE_URL + "?" + GlobalVariables.REQ_DEVICEID + "=" + deviceId + "&"
				+ GlobalVariables.REQ_GAMEID + "=" + gameId + "&" + GlobalVariables.REQ_GAMENAME + "=" + gameName + "&"
				+ GlobalVariables.REQ_DEVICETYPE + "=" + GlobalVariables.DEV_ADR + "&" + GlobalVariables.REQ_SCORE + "="
				+ score + "&" + GlobalVariables.REQ_PARAMS + "=" + params);
	}

	@Override
	public void update(float deltaTime) {
		// update presentation
		for (MenuPresentation present : presents) {
			if (present != null)
				present.update(deltaTime);
		}

		// update buttons
		for (DynamicButton button : buttons) {
			if (button != null) {
				button.update(deltaTime);
			}
		}

		// update dialog
		if (dialog != null)
			dialog.update(deltaTime);
	}

	@Override
	public void draw() {
		renderBackground();
		renderObjects();
		renderStage();
	}

	private void renderBackground() {
		bgDrawable(true);
		batch.draw(Assets.txBgMenu, 0, 0, 800, 480);
		bgDrawable(false);
	}

	private void renderObjects() {
		objDrawable(true);
		for (MenuPresentation present : presents) {
			if (present != null)
				present.render(batch);
		}
		objDrawable(false);
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
		SoundManager.playMusic(Assets.muBgMeu, 0.5f, true);
	}

	@Override
	public void hide() {
		// pause music
		SoundManager.pauseMusic(Assets.muBgMeu);
	}

	@Override
	public void pause() {
		// pause music
		SoundManager.pauseMusic(Assets.muBgMeu);
	}

	@Override
	public void resume() {
		// play music
		SoundManager.playMusic(Assets.muBgMeu, 0.5f, true);
	}

	@Override
	public void dispose() {
		stage.dispose();
	}
}
