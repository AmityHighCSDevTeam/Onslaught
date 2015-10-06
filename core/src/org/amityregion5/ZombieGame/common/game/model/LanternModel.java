package org.amityregion5.ZombieGame.common.game.model;

import org.amityregion5.ZombieGame.client.asset.TextureRegistry;
import org.amityregion5.ZombieGame.client.game.IDrawingLayer;
import org.amityregion5.ZombieGame.client.game.SpriteDrawingLayer;
import org.amityregion5.ZombieGame.common.entity.EntityLantern;
import org.amityregion5.ZombieGame.common.game.Game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;

import box2dLight.Light;

public class LanternModel implements IEntityModel<EntityLantern>{
	public static final Color	LIGHT_COLOR	= new Color(1,1,1,130f/255);

	private Light				light;
	private EntityLantern entity;
	private Game				g;
	private Color				c;
	private SpriteDrawingLayer	sprite;

	public LanternModel(EntityLantern e, Game game, Color color, String spriteTexture) {
		entity = e;
		g = game;
		c = color;
		sprite = new SpriteDrawingLayer(new Sprite(TextureRegistry.getTexturesFor(spriteTexture).get(0)));
	}

	@Override
	public EntityLantern getEntity() {
		return entity;
	}

	@Override
	public void tick(float timeStep) {
		if (light != null) {
			light.setActive(true);
			light.attachToBody(entity.getBody());
		}
		//light.setPosition(entity.getBody().getWorldCenter());
		sprite.getSprite().setOriginCenter();
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
		g.removeEntity(this);
		if (light != null) {
			light.remove();
			light = null;
		}
		return damage;
	}

	@Override
	public IDrawingLayer[] getDrawingLayers() {
		return new IDrawingLayer[] {sprite};
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

	public static Color getLIGHT_COLOR() {
		return LIGHT_COLOR;//new Color(ZombieGame.instance.random.nextFloat(),ZombieGame.instance.random.nextFloat(),ZombieGame.instance.random.nextFloat(), 1);
	}

	public Color getColor() {
		return c;
	}

	public Light getLight() {
		return light;
	}
}
