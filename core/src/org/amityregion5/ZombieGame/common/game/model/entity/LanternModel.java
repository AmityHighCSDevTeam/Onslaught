package org.amityregion5.ZombieGame.common.game.model.entity;

import org.amityregion5.ZombieGame.client.game.IDrawingLayer;
import org.amityregion5.ZombieGame.client.game.SpriteDrawingLayer;
import org.amityregion5.ZombieGame.common.entity.EntityLantern;
import org.amityregion5.ZombieGame.common.game.Game;
import org.amityregion5.ZombieGame.common.game.model.IEntityModel;
import org.amityregion5.ZombieGame.common.weapon.types.Placeable;
import org.json.simple.JSONObject;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import box2dLight.Light;

public class LanternModel implements IEntityModel<EntityLantern> {
	public static final Color LIGHT_COLOR = new Color(1, 1, 1, 130f / 255);

	private Light				light;
	private EntityLantern		entity;
	private Game				g;
	private Color				c;
	private SpriteDrawingLayer	sprite;
	private String				creation;

	public LanternModel() {}

	public LanternModel(EntityLantern e, Game game, Color color, String spriteTexture, String creationString) {
		entity = e;
		g = game;
		creation = creationString;
		c = color;
		sprite = new SpriteDrawingLayer(spriteTexture);
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
		// light.setPosition(entity.getBody().getWorldCenter());
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
	public float damage(float damage, IEntityModel<?> source, String damageType) {
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
		return LIGHT_COLOR;// new
							// Color(ZombieGame.instance.random.nextFloat(),ZombieGame.instance.random.nextFloat(),ZombieGame.instance.random.nextFloat(),
							// 1);
	}

	public Color getColor() {
		return c;
	}

	public Light getLight() {
		return light;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject convertToJSONObject() {
		JSONObject obj = new JSONObject();

		obj.put("x", entity.getBody().getWorldCenter().x);
		obj.put("y", entity.getBody().getWorldCenter().y);
		obj.put("r", entity.getBody().getTransform().getRotation());
		obj.put("creation", creation);

		return obj;
	}

	@Override
	public IEntityModel<EntityLantern> fromJSON(JSONObject obj, Game g) {
		float x = ((Number) obj.get("x")).floatValue();
		float y = ((Number) obj.get("y")).floatValue();
		float r = ((Number) obj.get("r")).floatValue();
		String creationStr = (String) obj.get("creation");

		LanternModel model = (LanternModel) Placeable.registeredObjects.get(creationStr).apply(g, new Vector2(x, y));

		model.getEntity().getBody().getTransform().setRotation(r);

		return model;
	}
}
