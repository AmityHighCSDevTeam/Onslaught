package io.github.AmityHighCSDevTeam.ZombieGame.client.gui;

import io.github.AmityHighCSDevTeam.ZombieGame.ZombieGame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

public class GuiButton extends Sprite implements Disposable{
	
	private int id;
	private MouseStatus status;
	private String text;
	

	public GuiButton(Sprite sprite, int id, String text, float x, float y, float width, float height) {
		super(sprite);
		this.id = id;
		setX(x);
		setY(y);
		setSize(width, height);
		status = MouseStatus.NONE;
		this.text = text;
	}
	public GuiButton(Texture texture, int id, String text, float x, float y, float width, float height) {
		super(texture);
		this.id = id;
		setX(x);
		setY(y);
		setSize(width, height);
		status = MouseStatus.NONE;
		this.text = text;
	}
	public GuiButton(TextureRegion region, int srcX, int srcY, int srcWidth,
			int srcHeight, int id, String text, float x, float y, float width, float height) {
		super(region, srcX, srcY, srcWidth, srcHeight);
		this.id = id;
		setX(x);
		setY(y);
		setSize(width, height);
		status = MouseStatus.NONE;
		this.text = text;
	}
	
	public GuiButton setXYWH(float x, float y, float width, float height) {
		setX(x);
		setY(y);
		setSize(width, height);
		return this;
	}

	public int getID() {
		return id;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public void setStatus(MouseStatus status) {
		this.status = status;
	}
	
	public MouseStatus getStatus() {
		return status;
	}
	
	@Override
	public void dispose() {
		getTexture().dispose();
	}
	
	@Override
	public void draw(Batch batch) {
		super.draw(batch);
		if (text != null) {
			TextBounds b = ZombieGame.buttonFont.getWrappedBounds(text, getWidth());
			ZombieGame.buttonFont.drawWrapped(batch, text, getX(), getY() + ((getHeight() + b.height)/2), getWidth(), HAlignment.CENTER);			
		}
	}
	
	public enum MouseStatus {
		NONE, DOWN, HOVER;
	}
}
