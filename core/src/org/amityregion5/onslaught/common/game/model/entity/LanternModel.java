package org.amityregion5.onslaught.common.game.model.entity;

import java.util.HashMap;

import org.amityregion5.onslaught.Onslaught;
import org.amityregion5.onslaught.client.game.IDrawingLayer;
import org.amityregion5.onslaught.client.game.SpriteDrawingLayer;
import org.amityregion5.onslaught.common.entity.EntityLantern;
import org.amityregion5.onslaught.common.func.Function3;
import org.amityregion5.onslaught.common.game.Game;
import org.amityregion5.onslaught.common.game.model.IEntityModel;
import org.amityregion5.onslaught.common.util.MapUtil;
import org.amityregion5.onslaught.common.weapon.types.Placeable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import box2dLight.Light;

/**
 * A model representing a lantern
 * @author sergeys
 *
 */
public class LanternModel implements IEntityModel<EntityLantern> {

	//The color of the light
	public static final Color LIGHT_COLOR = new Color(1, 1, 1, 130f / 255);

	private HashMap<String, JsonElement> data = new HashMap<String, JsonElement>();

	private transient Game				g; //The game
	private transient Light				light; //The light created by this object
	private transient EntityLantern		entity; //The entity
	private transient SpriteDrawingLayer	sprite; //The sprite drawing layer
	private transient Color c;

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
	public LanternModel(EntityLantern e, Game game, Color color, String spriteTexture, String creationString, HashMap<String, JsonElement> extraData, float life) {
		data = extraData;
		entity = e;
		setLife(life);
		g = game;
		data.put("creation", new JsonPrimitive(creationString));
		data.put("color", new JsonPrimitive(color.toString()));
		c = color;
		data.put("txtr", new JsonPrimitive(spriteTexture));
		sprite = new SpriteDrawingLayer(spriteTexture);
	}
	
	public float setLife(float life) {
		data.put("life", new JsonPrimitive(life));
		return life;
	}
	
	public float getLife() {
		return data.get("life").getAsFloat();
	}
	
	public void setColor(Color c) {
		data.put("color", new JsonPrimitive(c.toString()));
	}
	
	public Color getColor() {
		return Color.valueOf(data.get("color").getAsString());
	}

	@Override
	public EntityLantern getEntity() {
		return entity;
	}

	@Override
	public void tick(float timeStep) {
		if (light != null) {
			float life = setLife(getLife()-timeStep);
			if (life<0) {
				light.setColor(c.cpy().mul(1, 1 + life/30, 1 + life/30, 1 + life/50));
				if (life < -40) {
					damage(100, this, "Out of power");
					return;
				}
			}
			light.setActive(true); //Update Light
			light.attachToBody(entity.getBody());
		} else {
			g.removeEntity(this);
			g.makeExplosion(entity.getBody().getWorldCenter(), 10d, null);
		}
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
		if (source == this) {
			g.removeEntity(this);
		}
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
	 * Get this lantern's light
	 * 
	 * @return the light
	 */
	public Light getLight() {
		return light;
	}
	
	@Override
	public void read(JsonObject obj) {
		data = MapUtil.convertToHashMap(obj.entrySet());
	}
	
	@Override
	public void doPostDeserialize(Game game) {
		Function3<Game, Vector2, HashMap<String, JsonElement>, IEntityModel<?>> func = Placeable.registeredObjects.get(data.get("creation").getAsString());
		
		if (func == null) {
			Onslaught.error("Failed to load LanternModel");
			return;
		}
		
		float x = data.get("x").getAsFloat();
		float y = data.get("y").getAsFloat();
		float r = data.get("r").getAsFloat();
		float vx = data.get("vx").getAsFloat();
		float vy = data.get("vy").getAsFloat();
		float l = data.get("life").getAsFloat();
		
		data.remove("x");
		data.remove("y");
		data.remove("r");
		data.remove("vx");
		data.remove("vy");
		
		game.runAfterNextTick(()-> {
			LanternModel model = (LanternModel) func.apply(g, new Vector2(x, y), data);
			model.setLife(l);
			g.addEntityToWorld(model, x, y);
			model.getEntity().getBody().getTransform().setRotation(r);
			model.getEntity().getBody().setLinearVelocity(vx, vy);
		});
	}
	
	@Override
	public void write(JsonObject obj) {
		MapUtil.addMapToJson(obj, data);
		obj.addProperty("x", entity.getBody().getWorldCenter().x);
		obj.addProperty("y", entity.getBody().getWorldCenter().y);
		obj.addProperty("r", entity.getBody().getTransform().getRotation());
		obj.addProperty("vx", entity.getBody().getLinearVelocity().x);
		obj.addProperty("vy", entity.getBody().getLinearVelocity().y);
	}
}
