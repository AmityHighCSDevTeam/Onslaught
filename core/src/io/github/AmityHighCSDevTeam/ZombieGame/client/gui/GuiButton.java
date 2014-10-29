package io.github.AmityHighCSDevTeam.ZombieGame.client.gui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

public class GuiButton extends Sprite implements Disposable{
	
	int id;

	public GuiButton(int id) {
		super();
		this.id = id;
	}
	public GuiButton(Sprite sprite, int id) {
		super(sprite);
		this.id = id;
	}
	public GuiButton(Texture texture, int srcX, int srcY, int srcWidth,
			int srcHeight, int id) {
		super(texture, srcX, srcY, srcWidth, srcHeight);
		this.id = id;
	}
	public GuiButton(Texture texture, int srcWidth, int srcHeight, int id) {
		super(texture, srcWidth, srcHeight);
		this.id = id;
	}
	public GuiButton(Texture texture, int id) {
		super(texture);
		this.id = id;
	}
	public GuiButton(TextureRegion region, int srcX, int srcY, int srcWidth,
			int srcHeight, int id) {
		super(region, srcX, srcY, srcWidth, srcHeight);
		this.id = id;
	}
	public GuiButton(TextureRegion region, int id) {
		super(region);
		this.id = id;
	}
	
	public int getID() {
		return id;
	}
	
	@Override
	public void dispose() {
		getTexture().dispose();
	}
}
