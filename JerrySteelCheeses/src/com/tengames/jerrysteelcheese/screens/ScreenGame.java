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

import woodyx.basicapi.physics.BoxUtility;
import woodyx.basicapi.physics.ObjectModel;
import woodyx.basicapi.screen.XScreen;
import woodyx.basicapi.sound.SoundManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Json;
import com.tengames.jerrysteelcheese.interfaces.GlobalVariables;
import com.tengames.jerrysteelcheese.main.Assets;
import com.tengames.jerrysteelcheese.main.JerrySteelCheese;
import com.tengames.jerrysteelcheese.objects.Canon;
import com.tengames.jerrysteelcheese.objects.CommonModelList;
import com.tengames.jerrysteelcheese.objects.DynamicButton;
import com.tengames.jerrysteelcheese.objects.DynamicDialog;
import com.tengames.jerrysteelcheese.objects.IconModel;
import com.tengames.jerrysteelcheese.objects.Jerry;
import com.tengames.jerrysteelcheese.objects.SeeSaw;
import com.tengames.jerrysteelcheese.objects.StaticObject;
import com.tengames.jerrysteelcheese.objects.UKey;

public class ScreenGame extends XScreen implements Screen, InputProcessor {
	public static final byte STATE_NULL = 0;
	public static final byte STATE_WIN = 1;
	public static final byte STATE_LOOSE = 2;

	private byte numBullets[] = { 3, 3, 5, 4, 5, 4, 5, 4, 3, 3, 5, 5, 5, 3, 3, 3, 3, 5, 5, 3 };

	private JerrySteelCheese coreGame;
	private World world;
	private Stage stage;
	private DynamicDialog dialog;
	private DynamicButton[] buttons;
	private ArrayList<StaticObject> objects;
	private ArrayList<SeeSaw> seeSaws;
	private ArrayList<UKey> uKeys;
	private Jerry jerry;
	private Canon canon;
	private BufferedReader reader;
	private String strJson;
	private Vector2 target;
	private float countHelp, countFinish;
	private int numLevel, numBullet;
	private byte state;
//	private Skin skin;

	public ScreenGame(JerrySteelCheese coreGame, String strJson, int number) {
		super(800, 480);
		this.coreGame = coreGame;
		this.strJson = strJson;
		this.numLevel = number;
//		startDebugBox();
		initialize();
	}

	@Override
	public void initialize() {
		// create world
		world = new World(GlobalVariables.GRAVITY, true);

		// create stage
		stage = new Stage(800, 480, true);
		((OrthographicCamera) stage.getCamera()).setToOrtho(false, 800, 480);
		InputMultiplexer input = new InputMultiplexer(stage, this);
		// set input
		Gdx.input.setInputProcessor(input);
		Gdx.input.setCatchBackKey(true);

		// create smt
		initializeParams();
		createArrays();
		createModels();
		createUI();
		// check contact listener
		checkCollision();
	}

	private void createArrays() {
		// create array of static objects
		objects = new ArrayList<StaticObject>();
		// create array of seesaws
		seeSaws = new ArrayList<SeeSaw>();
		// create array of Ukey
		uKeys = new ArrayList<UKey>();
	}

	private void initializeParams() {
		SoundManager.MUSIC_ENABLE = coreGame.androidListener.getSound();
		SoundManager.SOUND_ENABLE = coreGame.androidListener.getSound();

		SoundManager.playMusic(Assets.muBgGame, 0.5f, true);

		/* loading data */
//		try {
//			reader = new BufferedReader(new FileReader("/home/woodyx/workspace-gdx/JerrySteelCheesesAndroid/assets/data/jerrysteelcheese.txt"));
//			reader = new BufferedReader(new FileReader("jerrysteelcheese.txt"));
//		} catch (FileNotFoundException e) {}
		reader = coreGame.androidListener.getData();

		// initialize params
		state = STATE_NULL;
		countHelp = 0;
		countFinish = 0;
		numBullet = numBullets[numLevel - 1];

		// trace
		if (numLevel < 10) {
			coreGame.androidListener.traceScene("0" + numLevel);
		} else {
			coreGame.androidListener.traceScene(numLevel + "");
		}

		// show admob
		if (numLevel % 4 == 0)
			coreGame.androidListener.showIntertitial();
	}

	private void createModels() {
		// create ground
		@SuppressWarnings("unused")
		ObjectModel ground = new ObjectModel(world, ObjectModel.STATIC, ObjectModel.POLYGON, new Vector2(800, -300),
				new Vector2(), 0, new Vector2(), 0, 100, 0.5f, 0.1f, GlobalVariables.CATEGORY_SCENERY,
				GlobalVariables.MASK_SCENERY, "ground");

		// create models
		if (strJson != null)
			createJsonModels();
	}

	private void createUI() {
		// load skin
//		skin = new Skin(Gdx.files.internal("drawable/objects/uiskin.json"), Assets.taSkin);

		// creat buttons
		buttons = new DynamicButton[6];

		// label stage
		buttons[0] = new DynamicButton(("Level: " + numLevel), new Vector2(10, 440), 0.5f);

		// label bullets
		buttons[1] = new DynamicButton(("" + canon.getBullet() + "/" + numBullet), new Vector2(230, 440), 0.7f);

		// icon ball
		buttons[2] = new DynamicButton(Assets.taObjects.findRegion("ball"), new Vector2(190, 440), 1f, 0.9f);

		// button menu
		buttons[3] = new DynamicButton(Assets.taObjects.findRegion("ic-menu"), new Vector2(680, 420), 0.5f, 1.1f);
		buttons[3].getButton().addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
//				 play sound
				SoundManager.playSound(Assets.soClick);
				// back to screen stage
				coreGame.setScreen(new ScreenStage(coreGame));
			}
		});

		// button replay
		buttons[4] = new DynamicButton(Assets.taObjects.findRegion("ic-replay"), new Vector2(740, 420), 0.5f, 1.3f);
		buttons[4].getButton().addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// play sound
				SoundManager.playSound(Assets.soClick);
				// replay
				coreGame.setScreen(new ScreenGame(coreGame, strJson, numLevel));
			}
		});

		// button sound
		buttons[5] = new DynamicButton(Assets.taObjects.findRegion("bt-soundon"),
				Assets.taObjects.findRegion("bt-soundoff"), new Vector2(740, 20), 0.6f, 1.5f);
		buttons[5].getButton().setChecked(!SoundManager.SOUND_ENABLE);

		buttons[5].getButton().addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// play sound
				SoundManager.playSound(Assets.soClick);
				// turn off sound and music
				SoundManager.MUSIC_ENABLE = !SoundManager.MUSIC_ENABLE;
				if (!SoundManager.MUSIC_ENABLE) {
					SoundManager.pauseMusic(Assets.muBgGame);
				} else {
					SoundManager.playMusic(Assets.muBgGame, 0.5f, true);
				}
				SoundManager.SOUND_ENABLE = !SoundManager.SOUND_ENABLE;
				// save sound
				coreGame.androidListener.setSound(SoundManager.SOUND_ENABLE);
			}
		});

		// back to screen scenario
		/*
		 * TextButton btBack = new TextButton("Back", skin); btBack.setPosition(20,
		 * 400); btBack.addListener(new ChangeListener() {
		 * 
		 * @Override public void changed(ChangeEvent event, Actor actor) {
		 * coreGame.setScreen(new ScreenScenario(coreGame)); } });
		 * stage.addActor(btBack);
		 */

		// add to stage
		for (DynamicButton button : buttons) {
			if (button != null)
				stage.addActor(button);
		}
	}

	/* generate map */
	private void createJsonModels() {
		Json json = new Json();
		CommonModelList jsList = new CommonModelList();
		jsList = json.fromJson(CommonModelList.class, strJson);
		// generate map
		for (int i = 0; i < jsList.getSize(); i++) {
			switch (jsList.getModel(i).getType()) {
			/* create objects */
			// case static objects
			case IconModel.CHEESE:
				StaticObject cheese = new StaticObject(world, jsList.getModel(i).getType(),
						jsList.getModel(i).getPosition(), jsList.getModel(i).getSize(),
						jsList.getModel(i).getRotation());
				objects.add(cheese);
				target = jsList.getModel(i).getPositionCenter();
				break;
			case IconModel.DISK:
			case IconModel.IRON_1:
			case IconModel.IRON_2:
			case IconModel.IRON_3:
			case IconModel.IRON_4:
				StaticObject stObject = new StaticObject(world, jsList.getModel(i).getType(),
						jsList.getModel(i).getPosition(), jsList.getModel(i).getSize(),
						jsList.getModel(i).getRotation());
				objects.add(stObject);
				break;

			// case jerry
			case IconModel.JERRY:
				if (jerry == null)
					jerry = new Jerry(world, Assets.taObjects.findRegion("jerry"), jsList.getModel(i).getPosition(),
							jsList.getModel(i).getSize());
				break;

			// case seesaw auto
			case IconModel.SEESAW_AUTO:
				SeeSaw seeSawAuto = new SeeSaw(world, Assets.taObjects.findRegion("seesaw"),
						jsList.getModel(i).getPosition(), jsList.getModel(i).getSize(),
						jsList.getModel(i).getRotation(), true);
				seeSaws.add(seeSawAuto);
				break;

			// case seesaw normal
			case IconModel.SEESAW_NORMAL:
				SeeSaw seeSawNormal = new SeeSaw(world, Assets.taObjects.findRegion("seesaw"),
						jsList.getModel(i).getPosition(), jsList.getModel(i).getSize(),
						jsList.getModel(i).getRotation(), false);
				seeSaws.add(seeSawNormal);
				break;

			// case ukey
			case IconModel.UKEY:
				UKey ukey = new UKey(world, jsList.getModel(i).getPosition(), jsList.getModel(i).getSize());
				uKeys.add(ukey);
				break;

			// case canon
			case IconModel.CANON:
				if (canon == null)
					canon = new Canon(Assets.taObjects.findRegion("canon"),
							new Vector2(jsList.getModel(i).getPosition().x - 250, jsList.getModel(i).getPosition().y),
							jsList.getModel(i).getSize(), jsList.getModel(i).getPosition(), numBullet);
				break;

			default:
				break;
			}
		}

		// free models
		jsList.dispose();
		jsList = null;

		// set target for jerry
		if (jerry != null)
			jerry.setTarget(target);
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

	private void checkFinish(float deltaTime) {
		// check if last ball is die
		if (canon.getBullet() <= 0) {
			if (countFinish >= 0)
				countFinish += deltaTime;
			if (countFinish >= 4) {
				// Fail!
				if (state == STATE_NULL) {
					createDialog(STATE_LOOSE);
					state = STATE_LOOSE;
				}
				countFinish = -1;
			}
		}
	}

	private void createDialog(byte type) {
		// pause music
		SoundManager.pauseMusic(Assets.muBgGame);

		// change input processor
		Gdx.input.setInputProcessor(stage);

		// create image dark
		Image imgDark = new Image(Assets.taObjects.findRegion("dark"));
		imgDark.setPosition(0, 0);
		imgDark.setSize(800, 480);
		stage.addActor(imgDark);

		// create effects
		if (type == STATE_WIN) {
			// play sound
			SoundManager.playSound(Assets.soWin);
		} else {
			// play sound
			SoundManager.playSound(Assets.soFail);
		}

		WindowStyle windowStyle = new WindowStyle();
		windowStyle.titleFont = Assets.fNumber;
		dialog = new DynamicDialog(windowStyle, new Vector2(400, 900), new Vector2(400, 240),
				(numBullets[numLevel - 1] - canon.getBullet()), numBullets[numLevel - 1], type);

		if (type == STATE_WIN) {
			// save data
			coreGame.androidListener.setValue(numLevel - 1, dialog.getValue());
			// unlock new stage
			if (coreGame.androidListener.getValue(numLevel) == 0)
				coreGame.androidListener.setValue(numLevel, 1);

			int boomUsed = numBullets[numLevel - 1] - canon.getBullet();
			int totalBoom = numBullets[numLevel - 1];
			if ((float) (boomUsed / totalBoom) <= 0.5f) {
				// save hscore
				coreGame.androidListener.saveHscore(100);
			} else if ((float) (boomUsed / totalBoom) > 0.5f && (float) (boomUsed / totalBoom) <= 0.8f) {
				// save hscore
				coreGame.androidListener.saveHscore(50);
			} else {
				// save hscore
				coreGame.androidListener.saveHscore(10);
			}
		}

		dialog.getBtLevel().addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// play sound
				SoundManager.playSound(Assets.soClick);
				// back to screen stage
				coreGame.setScreen(new ScreenStage(coreGame));
			}
		});

		dialog.getBtMenu().addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
//				 play sound
				SoundManager.playSound(Assets.soClick);
				// back to screen stage
				coreGame.setScreen(new ScreenMenu(coreGame));
			}
		});

		dialog.getBtReplay().addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// play sound
				SoundManager.playSound(Assets.soClick);
				// replay
				coreGame.setScreen(new ScreenGame(coreGame, strJson, numLevel));
			}
		});

		if (dialog.getBtMoreGame() != null) {
			dialog.getBtMoreGame().addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					// play sound
					SoundManager.playSound(Assets.soClick);
					// TODO : More Game
					coreGame.androidListener.gotoStore(GlobalVariables.STORE_URL);
				}
			});
		}

		if (dialog.getBtNext() != null) {
			dialog.getBtNext().addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					// play sound
					SoundManager.playSound(Assets.soClick);
					if (numLevel == 20) {
						// return stage menu
						coreGame.setScreen(new ScreenStage(coreGame));
					} else {
						// next level
						coreGame.androidListener.setValue(numLevel, 1);
						export(numLevel);
					}
				}
			});
		}

		// add to stage
		stage.addActor(dialog);
	}

	private void createBall() {
		// play sound
		SoundManager.playSound(Assets.soShoot);

		// create new ball
		StaticObject ball = new StaticObject(world, IconModel.BALL, canon.getShootPoint(),
				new Vector2(63 * canon.getRateScale(), 63 * canon.getRateScale()), 0);
		ball.setShoot(canon.getPow(), canon.getAngle());
		objects.add(ball);

		// decrease bullet
		canon.decreaseBullet();

		// set text
		buttons[1].setText("" + canon.getBullet() + "/" + numBullet);
	}

	@Override
	public void update(float deltaTime) {
		updateWorld(deltaTime);
		checkFinish(deltaTime);
		updateJerry(deltaTime);
		updateObjects(deltaTime);
		updateStage(deltaTime);
	}

	private void updateWorld(float deltaTime) {
		world.step(deltaTime, 8, 3);
	}

	private void updateJerry(float deltaTime) {
		// update jerrys
		if (jerry != null) {
			jerry.update(deltaTime);
			// check finish
			if (jerry.getHide() && jerry.getFinish()) {
				// win
				if (state == STATE_NULL) {
					createDialog(STATE_WIN);
					state = STATE_WIN;
				}
			}
			if (jerry.getY() <= -100) {
				// Fail
				if (state == STATE_NULL) {
					createDialog(STATE_LOOSE);
					state = STATE_LOOSE;
				}
			}
		}
	}

	private void updateObjects(float deltaTime) {
		// update objects
		if (!objects.isEmpty()) {
			for (int i = 0; i < objects.size(); i++) {
				if (objects.get(i) != null) {
					// check die
					if (objects.get(i).getPosition().y <= -150)
						objects.get(i).setDie();
					// update
					objects.get(i).update(deltaTime);
				}
			}
		}

		// update seesaws
		if (!seeSaws.isEmpty()) {
			for (SeeSaw seeSaw : seeSaws) {
				if (seeSaw != null)
					seeSaw.update(deltaTime);
			}
		}

		// update ukeys
		if (!uKeys.isEmpty()) {
			for (UKey uKey : uKeys) {
				if (uKey != null)
					uKey.update(deltaTime);
			}
		}

		// update help
		if (numLevel == 1) {
			if (countHelp >= 0)
				countHelp += deltaTime;
			if (countHelp >= 10)
				countHelp = -1;
		}

		// update canon
		if (canon != null)
			canon.update(deltaTime);
	}

	private void updateStage(float deltaTime) {
		// update buttons
		for (DynamicButton button : buttons) {
			if (button != null)
				button.update(deltaTime);
		}
		// update dialog
		if (dialog != null) {
			dialog.update(deltaTime);
		}
	}

	@Override
	public void draw() {
		renderBackGround();
		renderObjects();
		renderStage();
	}

	private void renderBackGround() {
		bgDrawable(true);
		batch.draw(Assets.txBgGame, 0, 0, 800, 480);
		bgDrawable(false);
	}

	private void renderObjects() {
		objDrawable(true);
		renderHelp();
		renderStaticObjects();
		renderJerrys();
		renderUkeys();
		renderSeeSaws();
		renderCanon();
		objDrawable(false);
	}

	private void renderStaticObjects() {
		// render objects
		if (!objects.isEmpty()) {
			for (StaticObject object : objects) {
				object.render(batch);
			}
		}
	}

	private void renderCanon() {
		if (canon != null)
			canon.render(batch);
	}

	private void renderJerrys() {
		if (jerry != null) {
			jerry.render(batch);
		}
	}

	private void renderSeeSaws() {
		if (!seeSaws.isEmpty()) {
			for (SeeSaw seeSaw : seeSaws) {
				if (seeSaw != null)
					seeSaw.render(batch);
			}
		}
	}

	private void renderUkeys() {
		if (!uKeys.isEmpty()) {
			for (UKey uKey : uKeys) {
				if (uKey != null)
					uKey.render(batch);
			}
		}
	}

	private void renderHelp() {
		// render help
		if (numLevel == 1) {
			if (countHelp != -1) {
				batch.draw(Assets.taObjects.findRegion("help-1"), 80, 230);
				batch.draw(Assets.taObjects.findRegion("help-2"), 440, 150);
			}
		}
	}

	private void renderStage() {
		stage.draw();
	}

	@Override
	public void render(float deltaTime) {
		clearScreen(deltaTime);
		clearWorld();
		update(deltaTime);
		draw();
//		renderDebug(world);
	}

	private void clearWorld() {
		world.clearForces();
	}

	private void checkCollision() {
		world.setContactListener(new ContactListener() {

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
			}

			@Override
			public void endContact(Contact contact) {
			}

			@Override
			public void beginContact(Contact contact) {
				if (!objects.isEmpty()) {
					for (StaticObject object : objects) {
						if ((object != null && object.getType() == IconModel.CHEESE) && jerry != null) {
							if (BoxUtility.detectCollision(contact, "jerry", "cheese")) {
								// set tween for jerry
								jerry.setHide();
							}
						}
					}
				}
			}
		});
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		// play music
		SoundManager.playMusic(Assets.muBgGame, 0.5f, true);
	}

	@Override
	public void hide() {
		// pause music, sound
		SoundManager.pauseMusic(Assets.muBgGame);
		switch (state) {
		case STATE_LOOSE:
			SoundManager.stopSound(Assets.soFail);
			break;
		case STATE_WIN:
			SoundManager.stopSound(Assets.soWin);
			break;
		default:
			break;
		}
	}

	@Override
	public void pause() {
		// pause music, sound
		SoundManager.pauseMusic(Assets.muBgGame);
		switch (state) {
		case STATE_LOOSE:
			SoundManager.stopSound(Assets.soFail);
			break;
		case STATE_WIN:
			SoundManager.stopSound(Assets.soWin);
			break;
		default:
			break;
		}
	}

	@Override
	public void resume() {
		// play music
		SoundManager.playMusic(Assets.muBgGame, 0.5f, true);
	}

	@Override
	public void dispose() {
		world.dispose();
		stage.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Keys.BACK) {
			// TODO: Back Key
			// play sound
			SoundManager.playSound(Assets.soClick);
			// back to stage
			coreGame.setScreen(new ScreenStage(coreGame));
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	Vector3 touchPoint = new Vector3();

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		camera.unproject(touchPoint.set(screenX, screenY, 0));
		// touch canon
		canon.getTouch(touchPoint.x, touchPoint.y);
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		camera.unproject(touchPoint.set(screenX, screenY, 0));
		// set shoot
		if (canon.getPrepare() && canon.getCanShoot() && (canon.getBullet() - 1 >= 0)) {
			createBall();
			canon.setPrepare(false);
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		camera.unproject(touchPoint.set(screenX, screenY, 0));
		// dragg canon
		canon.dragg(touchPoint.x, touchPoint.y);
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
