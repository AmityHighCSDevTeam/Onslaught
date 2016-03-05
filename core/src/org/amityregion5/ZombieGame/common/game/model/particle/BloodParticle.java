package org.amityregion5.ZombieGame.common.game.model.particle;

import java.util.List;

import org.amityregion5.ZombieGame.client.asset.TextureRegistry;
import org.amityregion5.ZombieGame.client.game.BloodDrawingLayer;
import org.amityregion5.ZombieGame.client.game.IDrawingLayer;
import org.amityregion5.ZombieGame.common.func.Consumer3;
import org.amityregion5.ZombieGame.common.game.Game;
import org.amityregion5.ZombieGame.common.game.model.IParticle;
import org.json.simple.JSONObject;

public class BloodParticle implements IParticle {
	private Game	g; //Game
	private float	x, y, size, r; //X position, Y position, Size, and rotation
	private String	textureName; //Texture name
	private float	timeAlive; //Time spent alive

	public BloodParticle() {}

	/**
	 * 
	 * @param x x position
	 * @param y y position
	 * @param g game
	 */
	public BloodParticle(float x, float y, Game g) {
		this.x = x; //Set x
		this.y = y; //Set y
		this.g = g; //Set game
		size = 0.004f; //Set size
		r = (float) (g.getRandom().nextFloat() * 2 * Math.PI); //Random rotation
		
		//Random texture name
		List<String> textureNames = TextureRegistry.getTextureNamesFor("blood/**");
		textureName = textureNames.get(g.getRandom().nextInt(textureNames.size()));
	}

	@Override
	public void tick(float timeStep) {
		//Increase time alive
		timeAlive += timeStep;
		
		//If greater than 4 remove this
		if (timeAlive > 4) {
			g.removeParticle(this);
		}
	}

	@Override
	public void dispose() {}

	@Override
	public IDrawingLayer[] getBackDrawingLayers() {
		return new IDrawingLayer[] {BloodDrawingLayer.instance};// new IDrawingLayer[] {sprite};
	}

	@Override
	public IDrawingLayer[] getFrontDrawingLayers() {
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

	public String getTextureName() {
		return textureName;
	}

	public float getOpacity() {
		return (float) (1 - Math.pow(timeAlive / 4, 8));
	}

	public float getR() {
		return r;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject convertToJSONObject() {
		JSONObject obj = new JSONObject();

		obj.put("x", x);
		obj.put("y", y);
		obj.put("r", r);
		obj.put("txtr", textureName);
		obj.put("life", timeAlive);

		return obj;
	}

	@Override
	public IParticle fromJSON(JSONObject obj, Game g, Consumer3<String, String, Boolean> addErrorConsumer) {
		float x = ((Number) obj.get("x")).floatValue();
		float y = ((Number) obj.get("y")).floatValue();
		float r = ((Number) obj.get("r")).floatValue();
		float timeAlive = ((Number) obj.get("life")).floatValue();
		String txtr = (String) obj.get("txtr");

		BloodParticle model = new BloodParticle(x, y, g);

		model.textureName = txtr;
		model.r = r;
		model.timeAlive = timeAlive;

		g.addParticleToWorld(model);

		return model;
	}
}
