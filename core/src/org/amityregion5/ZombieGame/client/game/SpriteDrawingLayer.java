package org.amityregion5.ZombieGame.client.game;

import java.util.function.Supplier;

import org.amityregion5.ZombieGame.client.asset.TextureRegistry;
import org.amityregion5.ZombieGame.common.entity.IEntity;
import org.amityregion5.ZombieGame.common.game.model.IEntityModel;
import org.amityregion5.ZombieGame.common.game.model.IParticle;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

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
		sprite = TextureRegistry.getAtlas().createSprite(TextureRegistry.getTextureNamesFor(name).get(0));
		this.sizeSupplier = sizeSupplier;
	}

	public Sprite getSprite() {
		return sprite;
	}
	
	/**
	 * Warning Expensive method do not call every frame
	 * 
	 * @param name
	 */
	public void setSprite(String name) {
		sprite = TextureRegistry.getAtlas().createSprite(TextureRegistry.getTextureNamesFor(name).get(0));
	}

	@Override
	public void draw(IEntityModel<?> em, SpriteBatch batch, ShapeRenderer shapeRenderer, Rectangle cullRect) {
		IEntity e = em.getEntity();
		sprite.setOriginCenter();
		//Set rotation
		sprite.setRotation((float) (Math.toDegrees(e.getBody().getAngle()) - 90));
		//Set postitioning of sprite
		sprite.setBounds(e.getBody().getWorldCenter().x - (sizeSupplier == null ? e.getShape().getRadius() : sizeSupplier.get()),
				e.getBody().getWorldCenter().y - (sizeSupplier == null ? e.getShape().getRadius() : sizeSupplier.get()),
				(sizeSupplier == null ? e.getShape().getRadius() : sizeSupplier.get()) * 2,
				(sizeSupplier == null ? e.getShape().getRadius() : sizeSupplier.get()) * 2);
		
		if (cullRect.overlaps(sprite.getBoundingRectangle())) {
			sprite.draw(batch);
		}
	}

	@Override
	public void draw(IParticle p, SpriteBatch batch, ShapeRenderer shapeRenderer, Rectangle cullRect) {
		sprite.setOriginCenter();
		//Set rotation
		sprite.setRotation((float) (Math.toDegrees(p.getRotation()) - 90));
		//Set postitioning of sprite
		sprite.setBounds(p.getRect().x - (sizeSupplier == null ? p.getRect().width : sizeSupplier.get()),
				p.getRect().y - (sizeSupplier == null ? p.getRect().height : sizeSupplier.get()),
				(sizeSupplier == null ? p.getRect().width : sizeSupplier.get()) * 2,
				(sizeSupplier == null ? p.getRect().height : sizeSupplier.get()) * 2);
		
		if (cullRect.overlaps(sprite.getBoundingRectangle())) {
			sprite.draw(batch);
		}
	}

	public String getTxtrName() {
		return name;
	}
}
