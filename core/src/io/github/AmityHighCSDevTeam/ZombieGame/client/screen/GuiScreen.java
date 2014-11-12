package io.github.AmityHighCSDevTeam.ZombieGame.client.screen;

import io.github.AmityHighCSDevTeam.ZombieGame.ZombieGame;
import io.github.AmityHighCSDevTeam.ZombieGame.client.gui.GuiButton;
import io.github.AmityHighCSDevTeam.ZombieGame.client.gui.GuiButton.MouseStatus;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public abstract class GuiScreen implements Screen{

	protected SpriteBatch batch;
	private float lastMouseX, lastMouseY;
	private boolean lastMouseDown = false;
	private HashMap<Integer, GuiButton> buttons = new HashMap<Integer, GuiButton>();
	protected GuiScreen prevScreen;
	
	public GuiScreen(GuiScreen prevScreen) {
		this.prevScreen = prevScreen;
	}
	
	@Override
	public void render(float delta) {
		Vector2 touchPos = new Vector2();
		touchPos.set(Gdx.input.getX(), Gdx.input.getY());
		
		if(Gdx.input.isTouched()) {
			lastMouseX = touchPos.x;
			lastMouseY = touchPos.y;
			if (!lastMouseDown) {
				lastMouseDown = true;
				mouseDown(touchPos.x, touchPos.y);
			}
		} else {
			if (lastMouseDown) {
				lastMouseDown = false;
				mouseUp(lastMouseX, lastMouseY);
			}
		}
		
		for (GuiButton b : buttons.values()) {
			if (b.getBoundingRectangle().contains(touchPos.x, getHeight() - touchPos.y)) {
				b.setStatus(MouseStatus.HOVER);
			} else {
				b.setStatus(MouseStatus.NONE);
			}
		}
		
		batch.begin();
		
		drawScreen(delta);
		
		batch.end();
	}
	@Override
	public void resize(int width, int height) {
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
		for (GuiButton b : buttons.values()) {
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
	protected void mouseUp(float x, float y) {
		for (GuiButton b : buttons.values()) {
			if (b.getBoundingRectangle().contains(x, getHeight() - y)) {
				buttonClicked(b.getID());
			}
		}
	}
	protected void drawScreen(float delta) {		
		for (GuiButton b : buttons.values()) {
			b.draw(batch);
		}
	}
	

	public int getWidth() {return ZombieGame.instance.width;}
	public int getHeight() {return ZombieGame.instance.height;}

	protected void addButton(GuiButton button) {
		buttons.put(button.getID(), button);
	}
	
	protected HashMap<Integer, GuiButton> getButtons() {
		return buttons;
	}
}
