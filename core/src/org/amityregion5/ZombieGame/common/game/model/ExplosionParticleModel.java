package org.amityregion5.ZombieGame.common.game.model;

import org.amityregion5.ZombieGame.client.game.ExplosionParticleDrawingLayer;
import org.amityregion5.ZombieGame.client.game.IDrawingLayer;
import org.amityregion5.ZombieGame.common.entity.EntityExplosionParticle;
import org.amityregion5.ZombieGame.common.game.Game;

import com.badlogic.gdx.graphics.Color;

import box2dLight.Light;

public class ExplosionParticleModel implements IEntityModel<EntityExplosionParticle>{
	private Light				light;
	private EntityExplosionParticle entity;
	private Game				g;
	private Color				c;

	public ExplosionParticleModel(EntityExplosionParticle e, Game game, Color color) {
		entity = e;
		g = game;
		c = color;
	}

	@Override
	public EntityExplosionParticle getEntity() {
		return entity;
	}

	@Override
	public void tick(float timeStep) {
		if (light != null) {
			light.setActive(true);
			light.setColor(light.getColor().mul(0.9f));
			light.attachToBody(entity.getBody());
			if (light.getColor().a < 0.05) {
				damage(0, this);
			}
		}
		//light.setPosition(entity.getBody().getWorldCenter());
		//sprite.getSprite().setOriginCenter();
	}

	@Override
	public void dispose() {
		if (light != null) {
			light.dispose();
			light = null;
		}
		entity.dispose();
	}

	@Override
	public float damage(float damage, IEntityModel<?> source) {
		if (source != this) {
			return 0;
		}
		g.removeEntity(this);
		if (light != null) {
			light.remove();
			light = null;
		}
		return damage;
	}

	@Override
	public IDrawingLayer[] getDrawingLayers() {
		return new IDrawingLayer[]{ExplosionParticleDrawingLayer.instance};//new IDrawingLayer[] {sprite};
	}

	@Override
	public float getHealth() {
		return 0;
	}

	@Override
	public float getMaxHealth() {
		return 0;
	}

	public void setLight(Light light) {
		this.light = light;
	}

	@Override
	public boolean isHostile() {
		return false;
	}

	public Color getColor() {
		return c;
	}

	public Light getLight() {
		return light;
	}
}
