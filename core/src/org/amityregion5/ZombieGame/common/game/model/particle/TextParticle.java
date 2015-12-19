package org.amityregion5.ZombieGame.common.game.model.particle;

import org.amityregion5.ZombieGame.client.game.IDrawingLayer;
import org.amityregion5.ZombieGame.client.game.TextParticleDrawingLayer;
import org.amityregion5.ZombieGame.common.game.Game;
import org.amityregion5.ZombieGame.common.game.model.IParticle;
import org.json.simple.JSONObject;

public class TextParticle implements IParticle {
	private float	x, y, size; //X, Y, Size
	private String	text; //Text

	public TextParticle() {}

	/**
	 * 
	 * @param x x position
	 * @param y y position
	 * @param g Game
	 * @param text text to display
	 */
	public TextParticle(float x, float y, Game g, String text) {
		this.x = x;
		this.y = y; //Set variables
		size = 0.004f;
		this.text = text;
	}

	@Override
	public void tick(float timeStep) {}

	@Override
	public void dispose() {}

	@Override
	public IDrawingLayer[] getMaxDrawingLayers() {
		return new IDrawingLayer[] {TextParticleDrawingLayer.instance};// new IDrawingLayer[] {sprite};
	}

	@Override
	public IDrawingLayer[] getFrontDrawingLayers() {
		return new IDrawingLayer[] {};// new IDrawingLayer[] {sprite};
	}

	@Override
	public IDrawingLayer[] getBackDrawingLayers() {
		return new IDrawingLayer[] {};
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getSize() {
		return size;
	}

	public String getText() {
		return text;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	@Override
	public JSONObject convertToJSONObject() {
		return null;
	}

	@Override
	public IParticle fromJSON(JSONObject obj, Game g) {
		return null;
	}
}