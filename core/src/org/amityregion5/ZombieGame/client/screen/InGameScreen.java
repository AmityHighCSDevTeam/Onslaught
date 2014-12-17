package org.amityregion5.ZombieGame.client.screen;

import org.amityregion5.ZombieGame.common.entity.EntityPlayer;
import org.amityregion5.ZombieGame.common.game.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Vector2;
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
	
	public InGameScreen(GuiScreen prevScreen, Game game) {
		super(prevScreen);
		this.game = game;
		
		player = new EntityPlayer();
		player.setSpeed(10);
		player.setFriction(2f);
		
		game.addEntityToWorld(player);
	}
	
	//Font
	private BitmapFont calibri30;
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); //Clear screen
	
		game.tick(delta);

		debugRenderer.render(game.getWorld(), camera.combined);
		
		if (Gdx.input.isKeyPressed(Keys.W)) {
			player.getBody().applyForceToCenter(new Vector2(0, player.getSpeed()), true);
		}	
		if (Gdx.input.isKeyPressed(Keys.S)) {
			player.getBody().applyForceToCenter(new Vector2(0, -player.getSpeed()), true);
		}
		if (Gdx.input.isKeyPressed(Keys.D)) {
			player.getBody().applyForceToCenter(new Vector2(player.getSpeed(),0), true);
		}	
		if (Gdx.input.isKeyPressed(Keys.A)) {
			player.getBody().applyForceToCenter(new Vector2(-player.getSpeed(), 0), true);
		}
		
		super.render(delta);
	}
	
	@Override
	protected void drawScreen(float delta) {
		super.drawScreen(delta);
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
		parameter.size = 40;
		
		calibri30 = generator.generateFont(parameter);
		
		generator.dispose();
		
		calibri30.setColor(1, 1, 1, 1);
		
		debugRenderer = new Box2DDebugRenderer();
		
		camera = new OrthographicCamera(120,90);
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
		calibri30.dispose();
		debugRenderer.dispose();
		game.dispose();
	}
}
