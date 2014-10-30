package io.github.AmityHighCSDevTeam.ZombieGame.client.gui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

public class GuiButton extends Sprite implements Disposable{
	
	int id;
	private MouseStatus status;
	

	public GuiButton(Sprite sprite, int id, float x, float y, float width, float height) {
		super(sprite);
		this.id = id;
		setX(x);
		setY(y);
		setSize(width, height);
		status = MouseStatus.NONE;
	}
	public GuiButton(Texture texture, int id, float x, float y, float width, float height) {
		super(texture);
		this.id = id;
		setX(x);
		setY(y);
		setSize(width, height);
		status = MouseStatus.NONE;
	}
	public GuiButton(TextureRegion region, int srcX, int srcY, int srcWidth,
			int srcHeight, int id, float x, float y, float width, float height) {
		super(region, srcX, srcY, srcWidth, srcHeight);
		this.id = id;
		setX(x);
		setY(y);
		setSize(width, height);
		status = MouseStatus.NONE;
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
	
	public enum MouseStatus {
		NONE, DOWN, HOVER;
	}
}
