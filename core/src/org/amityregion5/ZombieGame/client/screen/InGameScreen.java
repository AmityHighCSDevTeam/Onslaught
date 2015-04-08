package org.amityregion5.ZombieGame.client.screen;

import java.text.NumberFormat;
import java.util.ArrayList;

import org.amityregion5.ZombieGame.common.bullet.IBullet;
import org.amityregion5.ZombieGame.common.entity.EntityLantern;
import org.amityregion5.ZombieGame.common.entity.EntityPlayer;
import org.amityregion5.ZombieGame.common.entity.EntityZombie;
import org.amityregion5.ZombieGame.common.game.Game;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

/**
 * 
 * @author sergeys
 *
 */
public class InGameScreen extends GuiScreen {

	private Game game;
	private Box2DDebugRenderer debugRenderer;
	private OrthographicCamera camera;
	private EntityPlayer player;
	private ShapeRenderer shapeRenderer;
	private RayHandler rayHandler;

	//Font
	private BitmapFont font1, font2;



	public InGameScreen(GuiScreen prevScreen, Game game) {
		super(prevScreen);
		this.game = game;

		rayHandler = new RayHandler(game.getWorld());

		shapeRenderer = new ShapeRenderer();

		player = new EntityPlayer(game);
		player.setSpeed(0.05f);
		player.setFriction(0.99f);
		player.setMass(100);

		ConeLight light = new ConeLight(rayHandler, 250, Color.WHITE.mul(1,1,1,0.7f), 10, 0, 0, 0, 30);
		player.setLight(light);

		game.addEntityToWorld(player, 0, 0);

		EntityZombie zom = new EntityZombie(game);
		zom.setMass(100);
		zom.setSpeed(0.03f);
		zom.setFriction(0.99f);
		zom.setHealth(5);

		game.addEntityToWorld(zom, 1, 1);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0.15f, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); //Clear screen

		game.tick(delta);
		
		camera.translate(player.getBody().getWorldCenter().x - camera.position.x, player.getBody().getWorldCenter().y - camera.position.y);
		camera.update();
		
		debugRenderer.render(game.getWorld(), camera.combined);

		rayHandler.setCombinedMatrix(camera.combined);
		rayHandler.updateAndRender();

		super.render(delta);


		shapeRenderer.setProjectionMatrix(camera.combined);
		Gdx.gl.glLineWidth(1);
		shapeRenderer.begin(ShapeType.Line);
		for (IBullet bull : new ArrayList<IBullet>(game.getActiveBullets())) {
			if (bull.getEnd() != null) {
				shapeRenderer.setColor(bull.getColor());

				Vector3 start = new Vector3(bull.getStart().x, bull.getStart().y, 0);
				Vector3 end =  new Vector3(bull.getEnd().x, bull.getEnd().y, 0);

				shapeRenderer.line(start.x, start.y, end.x, end.y);
			}
			game.getActiveBullets().remove(bull);
		}
		shapeRenderer.end();
		Gdx.gl.glLineWidth(1);


	}

	@Override
	protected void drawScreen(float delta) {
		super.drawScreen(delta);

		//font2.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 0, font1.getBounds("FPS: " + Gdx.graphics.getFramesPerSecond()).height);
		font1.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 0, font1.getBounds("FPS: " + Gdx.graphics.getFramesPerSecond()).height);

		Vector3 mouseCoord = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
		player.setMousePos(new Vector2(mouseCoord.x, mouseCoord.y));

		drawHUD();
		
		
		
		/*
		if (coolDown > 0) {
			coolDown -= delta;
		}
		if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
			if (coolDown <= 0) {


				double dir = MathHelper.clampAngleAroundCenter(player.getBody().getAngle(), 
						MathHelper.getDirBetweenPoints(player.getBody().getPosition(), new Vector2(mouseCoord.x, mouseCoord.y)), 
						Math.toRadians(15));


				Vector2 v = MathHelper.getEndOfLine(player.getBody().getPosition(), player.getShape().getRadius() - 0.01, dir);


				Vector2 bullVector = VectorFactory.createVector(200f, (float) dir);

				BasicBullet bull = new BasicBullet(game, v, 1, 18/1000f, 1, bullVector);
				bull.setDir((float) dir);

				game.getActiveBullets().add(bull);
				game.getWorld().rayCast(bull, v, bullVector);
				bull.finishRaycast();

				coolDown += 0.1f;
			}
		}*/
		if (Gdx.input.isKeyJustPressed(Keys.L)) { 
			EntityLantern lantern = new EntityLantern(game);
			lantern.setLight(new PointLight(rayHandler, 400, EntityLantern.LIGHT_COLOR, 15, mouseCoord.x, mouseCoord.y));
			lantern.getLight().setXray(false);
			lantern.setMass(10);

			game.addEntityToWorld(lantern, mouseCoord.x, mouseCoord.y);
		}


		if (Gdx.input.isButtonPressed(Buttons.RIGHT))  {			
			EntityZombie zom = new EntityZombie(game);
			zom.setMass(100);
			zom.setSpeed(0.03f);
			zom.setFriction(0.99f);
			zom.setHealth(5);

			game.addEntityToWorld(zom, mouseCoord.x, mouseCoord.y);
		}
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	protected void setUpScreen() {
		super.setUpScreen();
	}

	@Override
	public void show() {
		super.show();

		//Create the font
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
				Gdx.files.internal("font/Calibri.ttf"));

		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 20;

		font1 = generator.generateFont(parameter);

		parameter.size = 24;
		font2 = generator.generateFont(parameter);

		generator.dispose();

		font1.setColor(1, 1, 1, 1);
		font2.setColor(0, 0, 0, 1);

		debugRenderer = new Box2DDebugRenderer();

		camera = new OrthographicCamera(12,9);
	}

	private void drawHUD() {
		batch.end();
		
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shapeRenderer.begin(ShapeType.Filled);

		shapeRenderer.setColor(75/255f, 75/255f, 75/255f, 75/255f);
		Vector3 start = camera.unproject(new Vector3(getWidth()-400, getHeight()-200, 0));
		Vector3 end = camera.unproject(new Vector3(getWidth(), getHeight(), 0));
		shapeRenderer.rect(start.x, start.y, end.x - start.x, end.y - start.y);
		shapeRenderer.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
		
		batch.begin();
		font1.draw(batch, player.getCurrentWeapon().getName(), getWidth()-390, 190);
		font1.draw(batch, player.getCurrentWeapon().getAmmoString(), getWidth()-390, 170);
		font1.draw(batch, "$" + NumberFormat.getInstance().format(player.getMoney()), getWidth()-390, 150);
	}

	@Override
	protected void buttonClicked(int id) {
		super.buttonClicked(id);
	}

	@Override
	public void hide() {
		super.hide();
	}

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void resume() {
		super.resume();
	}

	@Override
	public void dispose() {
		super.dispose();
		batch.dispose(); //Clear memory
		font1.dispose();
		font2.dispose();
		debugRenderer.dispose();
		game.dispose();
		shapeRenderer.dispose();
		rayHandler.dispose();
	}
}
