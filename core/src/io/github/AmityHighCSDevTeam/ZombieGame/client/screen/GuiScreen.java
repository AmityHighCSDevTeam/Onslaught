package io.github.AmityHighCSDevTeam.ZombieGame.client.screen;

import io.github.AmityHighCSDevTeam.ZombieGame.client.gui.GuiButton;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public abstract class GuiScreen implements Screen{

	private int width,height;
	protected SpriteBatch batch;
	private float lastMouseX, lastMouseY;
	private boolean isMouseDown = false;
	private ArrayList<GuiButton> buttons = new ArrayList<GuiButton>();

	@Override
	public void render(float delta) {
		if(Gdx.input.isTouched()) {
			Vector2 touchPos = new Vector2();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY());
			lastMouseX = touchPos.x;
			lastMouseY = touchPos.y;
			if (!isMouseDown) {
				isMouseDown = true;
				mouseDown(touchPos.x, touchPos.y);
			}
			
			
			for (GuiButton b : buttons) {
				if (b.getBoundingRectangle().contains(touchPos.x, height - touchPos.y)) {
					buttonClicked(b.getID());
				}
			}
		} else {
			if (isMouseDown) {
				isMouseDown = false;
				mouseUp(lastMouseX, lastMouseY);
			}
		}
		
		batch.begin();
		
		drawScreen(delta);
		
		batch.end();
	}
	@Override
	public void resize(int width, int height) {
		this.width = width;
		this.height = height;
	}
	@Override
	public void show() {	
		batch = new SpriteBatch();
	}
	@Override
	public void hide() {}
	@Override
	public void pause() {}
	@Override
	public void resume() {}
	@Override
	public void dispose() {
		for (GuiButton b : buttons) {
			b.dispose();
		}
	}
	/**
	 * Override this to get buttonClicks
	 */
	protected void buttonClicked(int id) {}
	/**
	 * On mouse down
	 */
	protected void mouseDown(float x, float y) {}
	/**
	 * On mouse up
	 */
	protected void mouseUp(float x, float y) {}
	protected void drawScreen(float delta) {		
		for (GuiButton b : buttons) {
			b.draw(batch);
		}
	}
	

	public int getWidth() {return width;}
	public int getHeight() {return height;}

	protected void addButton(GuiButton button) {
		buttons.add(button);
	}
}
