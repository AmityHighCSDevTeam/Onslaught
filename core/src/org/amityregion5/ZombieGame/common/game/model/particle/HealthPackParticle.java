package org.amityregion5.ZombieGame.common.game.model.particle;

import java.util.Optional;

import org.amityregion5.ZombieGame.client.game.HealthPackDrawingLayer;
import org.amityregion5.ZombieGame.client.game.IDrawingLayer;
import org.amityregion5.ZombieGame.common.game.Game;
import org.amityregion5.ZombieGame.common.game.model.IParticle;
import org.amityregion5.ZombieGame.common.game.model.entity.PlayerModel;

public class HealthPackParticle implements IParticle{
	private Game				g;
	private static final float timeBetweenChecks = 0.1f;
	private float x, y, size, timeUntilCheck;

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
	public HealthPackParticle(float x, float y, Game g) {
		this.x = x;
		this.y = y;
		this.g = g;
		size = 0.1f;
	}

	@Override
	public void tick(float timeStep) {
		timeUntilCheck -= timeStep;
		if (timeUntilCheck <= 0) {
			timeUntilCheck += timeBetweenChecks;
			if (g != null && g.getPlayers() != null) {
				Optional<PlayerModel> player = g.getPlayers().parallelStream()
						.filter((p)->{
							if (p == null || p.getEntity() == null || p.getEntity().getBody() == null) {
								return false;
							}
							return p.getEntity().getBody().getWorldCenter().dst2(x, y) <= size*size + 0.15*0.15;
						}).findAny();
				player.ifPresent((p)->{
					PlayerModel pM = player.get();
					if (pM.getHealth() < pM.getMaxHealth()) {
						pM.setHealth(Math.min(pM.getMaxHealth()*0.2f + pM.getHealth(), pM.getMaxHealth()));
						g.removeParticle(this);
					}
				});
			}
		}
	}

	@Override
	public void dispose() {
	}

	@Override
	public IDrawingLayer[] getDrawingLayers() {
		return new IDrawingLayer[]{HealthPackDrawingLayer.instance};//new IDrawingLayer[] {sprite};
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
}