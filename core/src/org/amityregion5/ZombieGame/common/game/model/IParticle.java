package org.amityregion5.ZombieGame.common.game.model;

import org.amityregion5.ZombieGame.client.game.IDrawingLayer;

public interface IParticle {
	void tick(float timeStep);
	
	void dispose();

	IDrawingLayer[] getDrawingLayers();
}
