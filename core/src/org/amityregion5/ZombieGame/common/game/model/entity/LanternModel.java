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

/**
 * A model representing a lantern
 * @author sergeys
 *
 */
public class LanternModel implements IEntityModel<EntityLantern> {
	
	//The color of the light
	public static final Color LIGHT_COLOR = new Color(1, 1, 1, 130f / 255);

	private Light				light; //The light created by this object
	private EntityLantern		entity; //The entity
	private Game				g; //The game
	private Color				c; //The color of the light
	private SpriteDrawingLayer	sprite; //The sprite drawing layer
	private String				creation; //The creation string

	public LanternModel() {}

	/**
	 * Create a lantern model
	 * 
	 * @param e the lantern entity
	 * @param game The game object
	 * @param color the color of the lantern
	 * @param spriteTexture the lantern's texture
	 * @param creationString the creation string to call when loading this object (Placeable)
	 */
	public LanternModel(EntityLantern e, Game game, Color color, String spriteTexture, String creationString) {
		entity = e;
		g = game;
		creation = creationString; //Set values
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
			light.setActive(true); //Update Light
			light.attachToBody(entity.getBody());
		}
		sprite.getSprite().setOriginCenter(); //Update sprite
	}

	@Override
	public void dispose() {
		if (light != null) {
			light.remove();
			light = null; //Dispose of everything
		}
		entity.dispose();
	}

	@Override
	public float damage(float damage, IEntityModel<?> source, String damageType) {
		g.removeEntity(this);
		if (light != null) {
			light.remove(); //Dispose of the light immediately
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

	/**
	 * Set the light used by this lantern
	 * 
	 * @param light the light
	 */
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

	/**
	 * Get this lantern's color
	 * 
	 * @return the color
	 */
	public Color getColor() {
		return c;
	}

	/**
	 * Get this lantern's light
	 * 
	 * @return the light
	 */
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
