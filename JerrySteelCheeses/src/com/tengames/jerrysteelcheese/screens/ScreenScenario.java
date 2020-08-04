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

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import woodyx.basicapi.screen.XScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Json;
import com.tengames.jerrysteelcheese.main.Assets;
import com.tengames.jerrysteelcheese.main.JerrySteelCheese;
import com.tengames.jerrysteelcheese.objects.CommonModel;
import com.tengames.jerrysteelcheese.objects.CommonModelList;
import com.tengames.jerrysteelcheese.objects.IconModel;

public class ScreenScenario extends XScreen implements Screen, InputProcessor {
	private static ArrayList<IconModel> realArrs;

	private JerrySteelCheese coreGame;
	private Stage stage;
	private Dialog dialog;
	private TextField tfModel;
	private Skin skin;
	private ShapeRenderer shapeRender;
	private ArrayList<IconModel> icoArrs;
	private Texture tempMap;
	private IconModel icoTemp, icoCanon, icoCheese, icoDisk, icoIron1, icoIron2, icoIron3, icoIron4, icoJerry,
			icoSeeSawNormal, icoSeeSawAuto, icoUKey;
	private CommonModelList cmExport;
	private Json json;
	private String strExport;
	private boolean isHide, isHideNet;

	public ScreenScenario(JerrySteelCheese coreGame) {
		super(800, 480);
		this.coreGame = coreGame;
		initialize();
	}

	@Override
	public void initialize() {
		initializeParams();
		createUI();
		createDialog();
		createList();
	}

	private void createUI() {
		// create stage
		stage = new Stage(800, 480, true);
		((OrthographicCamera) stage.getCamera()).setToOrtho(false, 800, 480);

		// set input
		InputMultiplexer input = new InputMultiplexer(stage, this);
		Gdx.input.setInputProcessor(input);

		// load skin
		skin = new Skin(Gdx.files.internal("drawable/objects/uiskin.json"), Assets.taSkin);

		// create shape renderer
		shapeRender = new ShapeRenderer();

		/* create buttons */
		// button hide dialog
		TextButton btHide = new TextButton("HideDialog", skin);
		btHide.setPosition(10, 450);
		btHide.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				isHide = !isHide;
			}
		});

		// button target
		TextButton btTarget = new TextButton("Target", skin);
		btTarget.setPosition(10, 420);
		btTarget.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// set target

			}
		});

		// button long
		TextButton btLong = new TextButton("Long", skin);
		btLong.setPosition(80, 420);
		btLong.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// set long
				Vector2 oldSize = new Vector2(realArrs.get(index).getWidth(), realArrs.get(index).getHeight());
				realArrs.get(index).setSize(oldSize.x * 1.1f, oldSize.y);
			}
		});

		// button short
		TextButton btShort = new TextButton("Short", skin);
		btShort.setPosition(140, 420);
		btShort.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// set short
				Vector2 oldSize = new Vector2(realArrs.get(index).getWidth(), realArrs.get(index).getHeight());
				realArrs.get(index).setSize(oldSize.x / 1.1f, oldSize.y);
			}
		});

		// button export data
		TextButton btExport = new TextButton("RunDemo", skin);
		btExport.setPosition(110, 450);
		btExport.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				saveMap();
			}
		});

		// button size up
		TextButton btSizeUp = new TextButton("SizeUp", skin);
		btSizeUp.setPosition(210, 450);
		btSizeUp.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (!realArrs.isEmpty()) {
					// resize models
					Vector2 oldSize = new Vector2(realArrs.get(index).getWidth(), realArrs.get(index).getHeight());
					realArrs.get(index).setSize(oldSize.x * 1.1f, oldSize.y * 1.1f);
				}
			}
		});

		// button size down
		TextButton btSizeDown = new TextButton("SizeDown", skin);
		btSizeDown.setPosition(310 - 20, 450);
		btSizeDown.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// resize models
				Vector2 oldSize = new Vector2(realArrs.get(index).getWidth(), realArrs.get(index).getHeight());
				realArrs.get(index).setSize(oldSize.x / 1.1f, oldSize.y / 1.1f);
			}
		});

		// button rotate left
		TextButton btRotateLeft = new TextButton("RotRight", skin);
		btRotateLeft.setPosition(410 - 20, 450);
		btRotateLeft.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (!realArrs.isEmpty()) {
					realArrs.get(index).rotate(rotate - 5);
				}
			}
		});

		// button rotate right
		TextButton btRotateRight = new TextButton("RotLeft", skin);
		btRotateRight.setPosition(510 - 30, 450);
		btRotateRight.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (!realArrs.isEmpty()) {
					realArrs.get(index).rotate(rotate + 5);
				}
			}
		});

		// button hide net
		TextButton btHideNet = new TextButton("HideNet", skin);
		btHideNet.setPosition(610 - 60, 450);
		btHideNet.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				isHideNet = !isHideNet;
			}
		});

		// button delete object
		TextButton btDelete = new TextButton("Delete", skin);
		btDelete.setPosition(710 - 80, 450);
		btDelete.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (!realArrs.isEmpty()) {
					// remove touch model
					if (index <= realArrs.size() - 1 && realArrs.get(index) != null) {
						realArrs.remove(index);
					}
				}
			}
		});

		// button map
		TextButton btMap = new TextButton("Map", skin);
		btMap.setPosition(690, 450);
		btMap.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {

			}
		});

		// button reborn
		TextButton btReborn = new TextButton("Rebuilt", skin);
		btReborn.setPosition(735, 450);
		btReborn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				showDialog();
			}
		});

		// add to stage
		stage.addActor(btExport);
		stage.addActor(btHide);
		stage.addActor(btRotateLeft);
		stage.addActor(btRotateRight);
		stage.addActor(btSizeDown);
		stage.addActor(btSizeUp);
		stage.addActor(btHideNet);
		stage.addActor(btDelete);
		stage.addActor(btMap);
		stage.addActor(btReborn);
		stage.addActor(btTarget);
		stage.addActor(btLong);
		stage.addActor(btShort);
	}

	private void initializeParams() {
		isHide = false;
		isHideNet = false;
		// create background
		tempMap = Assets.txBgGame;
		// create jsons
		cmExport = new CommonModelList();
	}

	private void createList() {
		/* create models */
		if (realArrs == null)
			realArrs = new ArrayList<IconModel>();
		// create array icon models
		icoArrs = new ArrayList<IconModel>();

		// create icon models
		icoCanon = new IconModel(Assets.taObjects.findRegion("canon"), 0, 350, IconModel.CANON, 0.2f, 0.3f);
		icoCheese = new IconModel(Assets.taObjects.findRegion("cheese"), 60, 350, IconModel.CHEESE, 0.19f, 0.2f);
		icoDisk = new IconModel(Assets.taObjects.findRegion("disk"), 0, 300, IconModel.DISK, 0.5f, 0.5f);
		icoIron1 = new IconModel(Assets.taObjects.findRegion("iron-1"), 60, 300, IconModel.IRON_1, 0.25f, 0.5f);
		icoIron2 = new IconModel(Assets.taObjects.findRegion("iron-2"), 0, 250, IconModel.IRON_2, 0.2f, 0.5f);
		icoIron3 = new IconModel(Assets.taObjects.findRegion("iron-3"), 60, 250, IconModel.IRON_3, 0.7f, 0.7f);
		icoIron4 = new IconModel(Assets.taObjects.findRegion("iron-4"), 0, 200, IconModel.IRON_4, 0.2f, 0.5f);
		icoJerry = new IconModel(Assets.taObjects.findRegion("jerry"), 60, 200, IconModel.JERRY, 0.3f, 0.3f);
		icoSeeSawAuto = new IconModel(Assets.taObjects.findRegion("seesaw"), 0, 150, IconModel.SEESAW_AUTO, 0.15f,
				0.5f);
		icoSeeSawNormal = new IconModel(Assets.taObjects.findRegion("seesaw"), 60, 150, IconModel.SEESAW_NORMAL, 0.15f,
				0.5f);
		icoUKey = new IconModel(Assets.taObjects.findRegion("iron-4"), 0, 100, IconModel.UKEY, 0.25f, 0.5f);

		addIcon(icoCanon, icoCheese, icoDisk, icoIron1, icoIron2, icoIron3, icoIron4, icoJerry, icoSeeSawAuto,
				icoSeeSawNormal, icoUKey);
	}

	private void addIcon(IconModel... iconModels) {
		for (IconModel icon : iconModels) {
			icoArrs.add(icon);
		}
	}

	private void saveMap() {
		// parsing real array
		for (IconModel icModel : realArrs) {
			CommonModel com = new CommonModel(icModel.getType(), icModel.getPosition(),
					new Vector2(icModel.getWidth(), icModel.getHeight()), icModel.getRotation());
			cmExport.addModel(com);
		}

		/* export json string */
		// for models
		json = new Json();
		strExport = json.toJson(cmExport);
		// print line
		System.out.println(strExport);
		// export file
		PrintWriter writer = null;
		try {
			writer = new PrintWriter("JerrySteelCheese-Export.txt", "UTF-8");
		} catch (FileNotFoundException e) {
		} catch (UnsupportedEncodingException e) {
		}
		writer.println("=========================== STAGE ============================");
		writer.println(strExport);
		writer.close();
		// set screen
		coreGame.setScreen(new ScreenGame(coreGame, strExport, 1));
	}

	private void createDialog() {
		// show dialog
		if (dialog == null) {
			dialog = new Dialog("REBUILT", skin);
			dialog.setSize(400, 250);
			dialog.setPosition(800 / 2 - 200, 480 / 2 - 120);
			dialog.setVisible(false);
			stage.addActor(dialog);
			// create labels
			Label lbModel = new Label("Models", skin);
			lbModel.setPosition(20, 200);
			dialog.addActor(lbModel);
			// create text fields
			tfModel = new TextField("", skin);
			tfModel.setPosition(20, 140);
			tfModel.setSize(360, 32);
			dialog.addActor(tfModel);

			// create buttons
			TextButton btCancel = new TextButton("Cancel", skin);
			btCancel.setSize(100, 32);
			btCancel.setPosition(250, 10);
			btCancel.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					// hide dialog
					dialog.setVisible(false);
				}
			});
			dialog.addActor(btCancel);
			TextButton btOk = new TextButton("OK", skin);
			btOk.setSize(100, 32);
			btOk.setPosition(50, 10);
			btOk.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					// remove all old objects
					realArrs.clear();
					// remove old temp
					if (icoTemp != null)
						icoTemp = null;
					// reborn
					if (tfModel.equals(""))
						tfModel.setText("{}");
					reBorn(tfModel.getText());
					// hide dialog
					dialog.setVisible(false);
				}
			});
			dialog.addActor(btOk);
		} else {
			dialog.setVisible(true);
		}
	}

	private void showDialog() {
		// reset field
		tfModel.setText("");
		// show dialog
		dialog.setVisible(true);
	}

	private void reBorn(String strModel) {
		/* reborn models */
		Json json = new Json();
		CommonModelList jsList = new CommonModelList();
		jsList = json.fromJson(CommonModelList.class, strModel);
		TextureRegion trModel = null;
		if (jsList != null) {
			for (int i = 0; i < jsList.getSize(); i++) {
				switch (jsList.getModel(i).getType()) {
				/* create objects */
				case IconModel.CANON:
					trModel = Assets.taObjects.findRegion("canon");
					break;
				case IconModel.CHEESE:
					trModel = Assets.taObjects.findRegion("cheese");
					break;
				case IconModel.DISK:
					trModel = Assets.taObjects.findRegion("disk");
					break;
				case IconModel.IRON_1:
					trModel = Assets.taObjects.findRegion("iron-1");
					break;
				case IconModel.IRON_2:
					trModel = Assets.taObjects.findRegion("iron-2");
					break;
				case IconModel.IRON_3:
					trModel = Assets.taObjects.findRegion("iron-3");
					break;
				case IconModel.IRON_4:
					trModel = Assets.taObjects.findRegion("iron-4");
					break;
				case IconModel.JERRY:
					trModel = Assets.taObjects.findRegion("jerry");
					break;
				case IconModel.SEESAW_AUTO:
					trModel = Assets.taObjects.findRegion("seesaw");
					break;
				case IconModel.SEESAW_NORMAL:
					trModel = Assets.taObjects.findRegion("seesaw");
					break;
				case IconModel.UKEY:
					trModel = Assets.taObjects.findRegion("iron-4");
					break;
				default:
					trModel = Assets.taObjects.findRegion("dark");
					break;
				}
				// reborn
				IconModel icon = new IconModel(trModel, jsList.getModel(i).getPosition().x,
						jsList.getModel(i).getPosition().y, jsList.getModel(i).getType(), 1, 1);
				icon.setSize(jsList.getModel(i).getSize().x, jsList.getModel(i).getSize().y);
				icon.setOriginCenter(icon);
				icon.setRotation(jsList.getModel(i).getRotation());
				realArrs.add(icon);
			}
			// free models
			jsList.dispose();
			jsList = null;
		}
	}

	@Override
	public void update(float deltaTime) {

	}

	@Override
	public void draw() {
		renderBackgrounds();
		renderObjects();
		if (!isHideNet)
			renderShape();
		renderStage();
	}

	private void renderBackgrounds() {
		bgDrawable(true);
		batch.draw(tempMap, -50, -10, 900, 500);
		bgDrawable(false);
	}

	private void renderObjects() {
		objDrawable(true);
		// render real models
		if (realArrs != null && !realArrs.isEmpty()) {
			for (IconModel icModel : realArrs) {
				icModel.draw(batch);
			}
		}
		// render choose dialog
		if (!isHide)
			renderCanHideObjs();
		objDrawable(false);
	}

	private void renderCanHideObjs() {
		batch.draw(Assets.taObjects.findRegion("dark"), 0, 100, 100, 300);
		// render icon models
		if (!icoArrs.isEmpty()) {
			for (IconModel icModel : icoArrs) {
				icModel.draw(batch);
			}
		}
		// render temp model
		if (icoTemp != null)
			icoTemp.draw(batch);
	}

	private void renderShape() {
		shapeRender.setProjectionMatrix(camera.combined);
		shapeRender.begin(ShapeType.Line);
		shapeRender.setColor(Color.RED);
		for (int i = 1; i < 10; i++) {
			shapeRender.line(0, 50 * i, 800, 50 * i);
		}
		for (int i = 1; i < 16; i++) {
			shapeRender.line(50 * i, 0, 50 * i, 480);
		}
		shapeRender.end();
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

	}

	@Override
	public void hide() {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {
		stage.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	Vector3 touchPoint = new Vector3();
	int rotate = 0, index = 0;

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		camera.unproject(touchPoint.set(screenX, screenY, 0));
		/* touch models */
		// parsing icon array models
		if (!isHide) {
			for (IconModel icModel : icoArrs) {
				if (icModel.checkTouch(touchPoint.x, touchPoint.y)) {
					icoTemp = new IconModel(icModel.getRegion(), icModel.getX(), icModel.getY(), icModel.getType(), 1f,
							1f);
				}
			}
			// parsing realarrays, check condition
			if (touchPoint.x >= 0 && touchPoint.x <= 100 && touchPoint.y >= 100 && touchPoint.y <= 400) {
			} else {
				for (IconModel icModel : realArrs) {
					icModel.checkTouch(touchPoint.x, touchPoint.y);
				}
			}
		} else {
			for (IconModel icModel : realArrs) {
				icModel.checkTouch(touchPoint.x, touchPoint.y);
			}
		}
		// find last model
		for (int i = 0; i < realArrs.size(); i++) {
			if (realArrs.get(i).getTouch()) {
				// set origin center
				realArrs.get(i).setOriginCenter(realArrs.get(i));
				// get last model
				index = i;
				// reset rotation
				rotate = 0;
			}
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		camera.unproject(touchPoint.set(screenX, screenY, 0));
		// set icon temp
		if (icoTemp != null) {
			icoTemp.setTouch(false);
			// add to realArrs
			realArrs.add(icoTemp);
			// get index
			index = realArrs.size() - 1;
			// set origin center
			realArrs.get(index).setOriginCenter(realArrs.get(index));
			// remove icoTemp
			icoTemp = null;
		}
		// parsing icon array
		for (IconModel icModel : icoArrs) {
			icModel.setTouch(false);
		}
		// parsing real array
		for (IconModel icModel : realArrs) {
			icModel.setTouch(false);
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		camera.unproject(touchPoint.set(screenX, screenY, 0));
		/* touch models */
		// move icon temp
		if (icoTemp != null) {
			icoTemp.setPosition(touchPoint.x - icoTemp.getWidth() / 2, touchPoint.y - icoTemp.getHeight() / 2);
		}
		// parsing real array
		for (IconModel icModel : realArrs) {
			if (icModel.getTouch()) {
				icModel.setPosition(touchPoint.x - icModel.getWidth() / 2, touchPoint.y - icModel.getHeight() / 2);
			}
		}
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
