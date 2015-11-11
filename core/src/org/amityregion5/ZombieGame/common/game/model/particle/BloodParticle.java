package org.amityregion5.ZombieGame.common.game.model.particle;

import java.util.List;

import org.amityregion5.ZombieGame.client.asset.TextureRegistry;
import org.amityregion5.ZombieGame.client.game.BloodDrawingLayer;
import org.amityregion5.ZombieGame.client.game.IDrawingLayer;
import org.amityregion5.ZombieGame.common.game.Game;
import org.amityregion5.ZombieGame.common.game.model.IParticle;
import org.json.simple.JSONObject;

public class BloodParticle implements IParticle{
	private Game g;
	private float x, y, size, r;
	private String textureName;
	private float timeAlive;

	public BloodParticle() {
	}
	
	/**
	 * @param x
	 * @param y
	 * @param c
	 * @param g
	 * @param rotation
	 * @param rotationSpeed
	 * @param vel
	 * @param velDir
	 */
	public BloodParticle(float x, float y, Game g) {
		this.x = x;
		this.y = y;
		this.g = g;
		size = 0.0025f;
		r = (float) (g.getRandom().nextFloat()*2*Math.PI);
		List<String> textureNames = TextureRegistry.getTextureNamesFor("blood/**");
		textureName = textureNames.get(g.getRandom().nextInt(textureNames.size()));
	}

	@Override
	public void tick(float timeStep) {
		timeAlive += timeStep;
		if (timeAlive > 4) {
			g.removeParticle(this);
		}
	}

	@Override
	public void dispose() {
	}

	@Override
	public IDrawingLayer[] getBackDrawingLayers() {
		return new IDrawingLayer[]{BloodDrawingLayer.instance};//new IDrawingLayer[] {sprite};
	}

	@Override
	public IDrawingLayer[] getFrontDrawingLayers() {
		return new IDrawingLayer[]{};
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
		return (float) (1 - Math.pow(timeAlive/4, 8));
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
	public IParticle fromJSON(JSONObject obj, Game g) {
		float x = ((Number)obj.get("x")).floatValue();
		float y = ((Number)obj.get("y")).floatValue();
		float r = ((Number)obj.get("r")).floatValue();
		float timeAlive = ((Number)obj.get("life")).floatValue();
		String txtr = (String) obj.get("txtr");
		
		BloodParticle model = new BloodParticle(x, y, g);
		
		model.textureName = txtr;
		model.r = r;
		model.timeAlive = timeAlive;
		
		g.addParticleToWorld(model);
		
		return model;
	}
}
