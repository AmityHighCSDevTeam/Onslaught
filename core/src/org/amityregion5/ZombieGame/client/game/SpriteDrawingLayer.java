package org.amityregion5.ZombieGame.client.game;

import java.util.function.Supplier;

import org.amityregion5.ZombieGame.client.asset.TextureRegistry;
import org.amityregion5.ZombieGame.common.entity.IEntity;
import org.amityregion5.ZombieGame.common.game.model.IEntityModel;
import org.amityregion5.ZombieGame.common.game.model.IParticle;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * A drawing layer for a sprite
 * @author sergeys
 *
 */
public class SpriteDrawingLayer implements IDrawingLayer {

	private Sprite	sprite; //The sprite
	private String	name; //The name of the texture

	private Supplier<Float> sizeSupplier;

	public SpriteDrawingLayer(String name) {
		this(name, null);
	}

	public SpriteDrawingLayer(String name, Supplier<Float> sizeSupplier) {
		this.name = name; //Set values
		sprite = new Sprite(TextureRegistry.getTexturesFor(name).get(0));
		this.sizeSupplier = sizeSupplier;
	}

	public Sprite getSprite() {
		return sprite;
	}

	@Override
	public void draw(IEntityModel<?> em, SpriteBatch batch, ShapeRenderer shapeRenderer) {
		IEntity e = em.getEntity();
		batch.begin();
		//Set rotation
		sprite.setRotation((float) (Math.toDegrees(e.getBody().getAngle()) - 90));
		//Set postitioning of sprite
		sprite.setBounds(e.getBody().getWorldCenter().x - (sizeSupplier == null ? e.getShape().getRadius() : sizeSupplier.get()),
				e.getBody().getWorldCenter().y - (sizeSupplier == null ? e.getShape().getRadius() : sizeSupplier.get()),
				(sizeSupplier == null ? e.getShape().getRadius() : sizeSupplier.get()) * 2,
				(sizeSupplier == null ? e.getShape().getRadius() : sizeSupplier.get()) * 2);

		sprite.draw(batch);
		batch.end();
	}

	@Override
	public void draw(IParticle p, SpriteBatch batch, ShapeRenderer shapeRenderer) {}

	public String getTxtrName() {
		return name;
	}
}
