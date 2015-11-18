package org.amityregion5.ZombieGame.client.game;

import org.amityregion5.ZombieGame.common.game.model.IEntityModel;
import org.amityregion5.ZombieGame.common.game.model.IParticle;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public interface IDrawingLayer {
	public void draw(IEntityModel<?> e, SpriteBatch batch, ShapeRenderer shapeRenderer);

	public void draw(IParticle p, SpriteBatch batch, ShapeRenderer shapeRenderer);
}
