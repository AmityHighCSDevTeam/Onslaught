package org.amityregion5.ZombieGame.common.game.model.particle;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.amityregion5.ZombieGame.client.asset.TextureRegistry;
import org.amityregion5.ZombieGame.client.game.IDrawingLayer;
import org.amityregion5.ZombieGame.client.game.SpriteDrawingLayer;
import org.amityregion5.ZombieGame.common.game.Game;
import org.amityregion5.ZombieGame.common.game.model.IParticle;

import com.badlogic.gdx.math.Rectangle;
import com.google.gson.annotations.SerializedName;

public class BloodParticle implements IParticle {
	private float	x, y, size, r; //X position, Y position, Size, and rotation
	@SerializedName(value="txtr") private String textureName; //Texture name
	
	private transient Game	g; //Game
	private transient SpriteDrawingLayer sprite;

	public BloodParticle() {}

	public static void addBloodToWorld(float x, float y, Game g) {
		Optional<BloodParticle> closest = Stream.concat(g.getParticles().parallelStream(), g.getParticlesToAdd().parallelStream())
				.filter((p)->{return p instanceof BloodParticle;}).map((p)->(BloodParticle)p)
				.filter((p)->{return p.size < 25;})
				.filter((b)->{return ((b.x-x)*(b.x-x)+(b.y-y)*(b.y-y))<0.5;})
				.sorted((p1, p2)->Double.compare(((p1.x-x)*(p1.x-x)+(p1.y-y)*(p1.y-y)), ((p2.x-x)*(p2.x-x)+(p2.y-y)*(p2.y-y)))).findFirst();
		if (closest.isPresent()) {
			float totalSize = closest.get().size + 1f;
			closest.get().x = closest.get().x * closest.get().size/totalSize + x * 1f/totalSize;
			closest.get().y = closest.get().y * closest.get().size/totalSize + y * 1f/totalSize;
			closest.get().size = totalSize;
		} else {
			g.addParticleToWorld(new BloodParticle(x, y, g));
		}
	}

	/**
	 * 
	 * @param x x position
	 * @param y y position
	 * @param g game
	 */
	private BloodParticle(float x, float y, Game g) {
		this.x = x; //Set x
		this.y = y; //Set y
		this.g = g; //Set game
		size = 1f; //Set size
		r = (float) (g.getRandom().nextFloat() * 2 * Math.PI); //Random rotation

		//Random texture name
		List<String> textureNames = TextureRegistry.getTextureNamesFor("blood/**");
		textureName = textureNames.get(g.getRandom().nextInt(textureNames.size()));
		
		sprite = new SpriteDrawingLayer(textureName, this::getSize);
	}

	@Override
	public void tick(float timeStep) {
		//Increase time alive
		size *= 0.99;

		//If greater than 4 remove this
		if (size < 0.1) {
			g.removeParticle(this);
		}
	}

	@Override
	public void dispose() {}

	@Override
	public IDrawingLayer[] getBackDrawingLayers() {
		return new IDrawingLayer[] {sprite};// new IDrawingLayer[] {sprite};
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
		return (float) Math.sqrt(size/Math.PI)/10;
	}

	public String getTextureName() {
		return textureName;
	}

	public float getOpacity() {
		return Math.min(1, (size-0.1f)/0.9f);
	}

	public float getR() {
		return r;
	}
	
	@Override
	public void doPostDeserialize(Game game) {
		g = game;

		//Random texture name
		List<String> textureNames = TextureRegistry.getTextureNamesFor("blood/**");
		textureName = textureNames.get(g.getRandom().nextInt(textureNames.size()));
		
		sprite = new SpriteDrawingLayer(textureName, this::getSize);
		
		g.addParticleToWorld(this);
	}

	@Override
	public Rectangle getRect() {
		return new Rectangle(x, y, size, size);
	}
	
	@Override
	public float getRotation() {
		return r;
	}
}
