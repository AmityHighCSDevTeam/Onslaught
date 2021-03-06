package org.amityregion5.onslaught.common.game.model.particle;

import java.util.Optional;

import org.amityregion5.onslaught.client.game.HealthPackDrawingLayer;
import org.amityregion5.onslaught.client.game.IDrawingLayer;
import org.amityregion5.onslaught.common.game.Game;
import org.amityregion5.onslaught.common.game.model.IParticle;
import org.amityregion5.onslaught.common.game.model.entity.PlayerModel;

import com.badlogic.gdx.math.Rectangle;

public class HealthPackParticle implements IParticle {
	//Time between checks if player is close enough
	private static final float	timeBetweenChecks	= 0.1f;
	//X, Y, Size, time until check
	private float x;
	private float y;
	private boolean dead;
	private transient float size;
	private transient float timeUntilCheck;
	
	private transient Game g; //Game

	public HealthPackParticle() {}

	/**
	 * 
	 * @param x x position
	 * @param y y position
	 * @param g Game
	 */
	public HealthPackParticle(float x, float y, Game g) {
		this.x = x;
		this.y = y; //Set values
		this.g = g;
		size = 0.1f;
	}

	@Override
	public void tick(float timeStep) {
		timeUntilCheck -= timeStep; //Decrement time until check
		if (timeUntilCheck <= 0) { //If time to check
			timeUntilCheck = timeBetweenChecks; //Set time until check to time between checks 
			if (g != null && g.getPlayers() != null) { //If game and players exist
				//Get player that is on it
				Optional<PlayerModel> player = g.getPlayers().parallelStream().filter((p) -> {
					if (p == null || p.getEntity() == null || p.getEntity().getBody() == null) { return false; }
					return p.getEntity().getBody().getWorldCenter().dst2(x, y) <= size * size + 0.15 * 0.15;
				}).findAny();
				
				//If there is a player
				player.ifPresent((p) -> {
					//Get the player
					PlayerModel pM = player.get();
					//if it is missing health
					if (pM.getHealth() < pM.getMaxHealth()) {
						//Give it health
						pM.setHealth(Math.min(pM.getMaxHealth() * 0.2f + pM.getHealth(), pM.getMaxHealth()));
						//Remove particle
						dead = true;
					}
				});
			}
		}
	}

	@Override
	public void dispose() {}

	@Override
	public IDrawingLayer[] getBackDrawingLayers() {
		return new IDrawingLayer[] {HealthPackDrawingLayer.instance};// new IDrawingLayer[] {sprite};
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

	public void doPostDeserialize(Game game) {
		g = game;
		
		size = 0.1f;
		
		g.addParticleToWorld(this);
	}

	@Override
	public IDrawingLayer[] getFrontDrawingLayers() {
		return new IDrawingLayer[] {};
	}
	
	@Override
	public Rectangle getRect() {
		return new Rectangle(x,y,size, size);
	}
	
	@Override
	public float getRotation() {
		return 0;
	}
	
	@Override
	public boolean shouldBeDeleted() {
		return dead;
	}
}
