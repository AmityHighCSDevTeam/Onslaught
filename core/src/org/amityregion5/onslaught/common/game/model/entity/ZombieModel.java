package org.amityregion5.onslaught.common.game.model.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.amityregion5.onslaught.Onslaught;
import org.amityregion5.onslaught.client.asset.SoundRegistry;
import org.amityregion5.onslaught.client.asset.TextureRegistry;
import org.amityregion5.onslaught.client.game.HealthBarDrawingLayer;
import org.amityregion5.onslaught.client.game.IDrawingLayer;
import org.amityregion5.onslaught.client.game.SpriteDrawingLayer;
import org.amityregion5.onslaught.common.entity.EntityPlayer;
import org.amityregion5.onslaught.common.entity.EntityZombie;
import org.amityregion5.onslaught.common.entity.IEntity;
import org.amityregion5.onslaught.common.game.DamageTypes;
import org.amityregion5.onslaught.common.game.Game;
import org.amityregion5.onslaught.common.game.model.IEntityModel;
import org.amityregion5.onslaught.common.game.model.particle.BloodParticle;
import org.amityregion5.onslaught.common.game.model.particle.HealthPackParticle;
import org.amityregion5.onslaught.common.helper.BodyHelper;
import org.amityregion5.onslaught.common.helper.MathHelper;
import org.amityregion5.onslaught.common.helper.VectorFactory;
import org.amityregion5.onslaught.common.util.MapUtil;
import org.amityregion5.onslaught.common.weapon.data.SoundData;

import com.badlogic.gdx.math.Vector2;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class ZombieModel implements IEntityModel<EntityZombie> {

	private static final float	baseAtkCooldown		= 1; //Attack cooldown
	private static final float	minSecUntilGrowl	= 10; //Minimum time between growls
	private static final float	maxSecUntilGrowl	= 20; //Maximum time between growls
	private static final float	minGrowlVolume		= 0.8f; //Minimum growl volume
	private static final float	maxGrowlVolume		= 1.0f; //Maximum growl volume
	
	private HashMap<String, JsonElement> data = new HashMap<String, JsonElement>();

	//private float				health, maxHealth, speed, damage, range; //Health, Speed, Damage, Range

	private transient EntityZombie		entity; //Entity
	private transient IEntity				target; //Target
	private transient Game				g; //Game
	private transient SpriteDrawingLayer	zSprite; //Sprite
	private transient AIMode				ai; //AI Mode
	private transient float				secUntilGrowl	= -1; //seconds until growl
	private transient float				growlVolume		= -1; //Growl volume
	private transient float				growlPitch		= -1; //Growl pitch
	private transient float timeUntilIdleCheck = 0;

	public ZombieModel() {}

	public ZombieModel(EntityZombie zom, Game g, float sizeMultiplier) {
		entity = zom;
		this.g = g;
		ai = AIMode.IDLE; //Set variables
		data.put("sizeMultiplier", new JsonPrimitive(sizeMultiplier));

		//Get random zombie sprite
		int textureIndex = Onslaught.instance.random.nextInt(TextureRegistry.getTextureNamesFor("*/Zombies/**.png").size());
		zSprite = new SpriteDrawingLayer(TextureRegistry.getTextureNamesFor("*/Zombies/**.png").get(textureIndex));
	}

	@Override
	public EntityZombie getEntity() {
		return entity;
	}

	@Override
	public void tick(float delta) {
		//If growl now
		if (secUntilGrowl <= 0) {
			// Set volume and pitch
			growlVolume = g.getRandom().nextFloat() * (maxGrowlVolume - minGrowlVolume) + minGrowlVolume;
			growlPitch = Math.min(Math.max(data.get("sizeMultiplier").getAsFloat(), 0.5f), 2f);
			//Get sound
			List<String> soundNames = SoundRegistry.getSoundNamesFor("*/Audio/Zombie/*");
			//Play sound
			g.playSound(new SoundData(soundNames.get(g.getRandom().nextInt(soundNames.size())), growlPitch, growlVolume), entity.getBody().getWorldCenter());
			//New time until growl
			secUntilGrowl = g.getRandom().nextFloat() * (maxSecUntilGrowl - minSecUntilGrowl) + minSecUntilGrowl;
		}
		//Decrement time until growl
		secUntilGrowl -= delta;

		if (g.isAIDisabled()) {
			ai = AIMode.IDLE;
		}

		//Depending on the AI Mode
		switch (ai) {
			case IDLE: //If Idle mode
				//If it is time to check for a target
				if (timeUntilIdleCheck <= 0) {
					IEntity closest = null; //Storage for closest Entity found
					float dist2 = Float.MAX_VALUE; //Distance to entity

					//Loop through all entity models
					for (IEntityModel<?> m : g.getEntities()) {
						//Get the entity
						IEntity e = m.getEntity();
						//If it is a player
						if (e instanceof EntityPlayer) {
							//Get the distance to it
							float d = entity.getBody().getLocalCenter().dst2(e.getBody().getLocalCenter());
							//If it is closer
							if (d < dist2) {
								//Store it in closest
								closest = e;
								dist2 = d;
							}
						}
					}
					//Closest is now the target
					target = closest;

					//If the target exists
					if (target != null) {
						//Follow it
						ai = AIMode.FOLLOWING;
					}

					//1 second until next check
					timeUntilIdleCheck = 1;
				} else {
					//Tick down time until next check
					timeUntilIdleCheck -= delta;
				}
				break;
			case FOLLOWING: //If following mode

				//If target exists
				if (target != null) {
					//Get the target model
					Optional<IEntityModel<?>> targetModel = g.getEntityModelFromEntity(target);

					//If it exists and has health
					if (targetModel.isPresent() && targetModel.get().getHealth() > 0) {
						//Apply a force towards the target
						entity.getBody().applyForceToCenter(VectorFactory.createVector(getSpeed(),
								(float) MathHelper.getDirBetweenPoints(entity.getBody().getPosition(), target.getBody().getPosition())), true);

						//Rotate to point towards the target
						BodyHelper.setPointing(entity.getBody(), target.getBody().getWorldCenter(), delta, 10);

						//Corrected range of zombie
						float fixedRange = (data.get("range").getAsFloat() + targetModel.get().getEntity().getShape().getRadius());

						//If we are within range
						if (entity.getBody().getWorldCenter().dst2(target.getBody().getWorldCenter()) <= fixedRange * fixedRange) {
							//Set AI to attacking
							ai = AIMode.ATTACKING;
							//Set cooldown
							data.put("attackCooldown", new JsonPrimitive(0));
						}

						break;
					} else {
						//If fails then return to IDLE
						ai = AIMode.IDLE;
					}
				} else {
					//If fails then return to IDLE
					ai = AIMode.IDLE;
				}
				break;
			case ATTACKING: //If currently Attacking mode
				//If target exists
				if (target != null) {
					//Get target
					Optional<IEntityModel<?>> targetModel = g.getEntityModelFromEntity(target);
					//If it exists and has health
					if (targetModel.isPresent() && targetModel.get().getHealth() > 0) {
						//Point at it
						BodyHelper.setPointing(entity.getBody(), target.getBody().getWorldCenter(), delta, 10);

						//If no cooldown yet
						if (data.get("attackCooldown").getAsFloat() == 0) {
							//Damage it
							targetModel.get().damage(data.get("damage").getAsFloat(), this, DamageTypes.ZOMBIE);
						} else if (data.get("attackCooldown").getAsFloat() >= baseAtkCooldown) {
							//If cooldown completed
							//Back to following mode
							ai = AIMode.FOLLOWING;
						}
						//Increase cooldown
						data.put("attackCooldown", new JsonPrimitive(data.get("attackCooldown").getAsFloat() + delta));
						break;
					} else {
						//If fails then return to IDLE
						ai = AIMode.IDLE;
					}
				}
				//If fails then return to IDLE
				ai = AIMode.IDLE;
				break;
		}
	}

	@Override
	public void dispose() {
		target = null; //Clear target
		entity.dispose(); //Dispose entity
	}

	/**
	 * Set the money to be given for killing this zombie
	 * 
	 * @param prizeMoney the money to be given
	 */
	public void setPrizeMoney(double prizeMoney) {
		data.put("prizeMoney", new JsonPrimitive(prizeMoney));
	}

	/**
	 * Get the amount of money to be given for killing this zombie
	 * 
	 * @return the money to be given
	 */
	public double getPrizeMoney() {
		return data.get("prizeMoney").getAsDouble();
	}

	@Override
	public float damage(float damage, IEntityModel<?> source, String damageType) {
		//Get the damage that is taken from the source
		float damageTaken = Math.min(damage, data.get("health").getAsFloat());
		
		//If less than zero
		if (damageTaken < 0) {
			//Set it to zero
			damageTaken = 0;
		}

		//For every 5 units of damage taken
		//for (int i = 0; i < damageTaken; i += 5) {
		//Add a splatter of blood
		BloodParticle.addBloodToWorld(entity.getBody().getWorldCenter().x - entity.getSize() * 1.25f + g.getRandom().nextFloat() * 2 * entity.getSize() * 1.25f,
				entity.getBody().getWorldCenter().y - entity.getSize() * 1.25f + g.getRandom().nextFloat() * 2 * entity.getSize() * 1.25f, g);
		//}

		//Decrease health by damage
		setHealth(getHealth() - damageTaken);
		//Set AI to follow the thing that hurt it
		ai = AIMode.FOLLOWING;

		if (source != null) {
			//Set the thing that hurt it as the target
			target = source.getEntity();
		}

		//If this was the killing blow
		if (getHealth() <= 0 && damageTaken > 0) {
			//If the source is a player
			if (source != null && source instanceof PlayerModel) {
				PlayerModel pModel = (PlayerModel) source;
				//Give the player money
				pModel.setMoney(pModel.getMoney() + getPrizeMoney());
				pModel.addScore(getPrizeMoney() * 0.05 + 1);
			}

			//If the random says it is time for a health pack
			if (g.getDifficulty().getHealthPackChance() > g.getRandom().nextDouble()) {
				//Add a health pack
				g.addParticleToWorld(new HealthPackParticle(entity.getBody().getWorldCenter().x, entity.getBody().getWorldCenter().y, g));
			}

			//Remove this entity
			g.removeEntity(this);
		}
		return damageTaken;
	}

	@Override
	public IDrawingLayer[] getDrawingLayers() {
		return new IDrawingLayer[] {zSprite, HealthBarDrawingLayer.instance};
	}

	/**
	 * Set the health and max health
	 * 
	 * @param health the health to set
	 */
	public void setAllHealth(float health) {
		setHealth(health);
		setMaxHealth(health);
	}

	/**
	 * Set the max health
	 * 
	 * @param maxHealth the new max health
	 */
	public void setMaxHealth(float maxHealth) {
		data.put("maxHealth", new JsonPrimitive(maxHealth));
	}

	@Override
	public float getMaxHealth() {
		return data.get("maxHealth").getAsFloat();
	}

	@Override
	public float getHealth() {
		return data.get("health").getAsFloat();
	}

	/**
	 * Set the health
	 * 
	 * @param health the new health
	 */
	public void setHealth(float health) {
		data.put("health", new JsonPrimitive(health));
	}

	/**
	 * Set the speed
	 * 
	 * @param speed the new speed
	 */
	public void setSpeed(float speed) {
		data.put("speed", new JsonPrimitive(speed));
	}

	/**
	 * Get the speed
	 * 
	 * @return the speed
	 */
	public float getSpeed() {
		return data.get("speed").getAsFloat();
	}

	@Override
	public boolean isHostile() {
		return true;
	}

	/**
	 * Set the damage
	 * 
	 * @param damage the damage
	 */
	public void setDamage(float damage) {
		data.put("damage", new JsonPrimitive(damage));
	}

	/**
	 * Set the range
	 * 
	 * @param range the range
	 */
	public void setRange(float range) {
		data.put("range", new JsonPrimitive(range));
	}

	/**
	 * An Enum for the AI modes
	 * @author sergeys
	 *
	 */
	private enum AIMode {
		IDLE, FOLLOWING, ATTACKING;
	}

	/**
	 * Set the texture name
	 * 
	 * @param txtr the texture name
	 */
	public void setTexture(String txtr) {
		zSprite.setSprite(TextureRegistry.getTextureNamesFor(txtr).get(0));
	}
	
	public HashMap<String, JsonElement> getData() {
		return data;
	}
	
	@Override
	public void read(JsonObject obj) {
		data = MapUtil.convertToHashMap(obj.entrySet());
	}

	@Override
	public void doPostDeserialize(Game game) {
		entity = new EntityZombie(data.get("size").getAsFloat());
		g = game;
		zSprite = new SpriteDrawingLayer(data.get("txtr").getAsString());
		entity.setFriction(data.get("friction").getAsFloat());
		entity.setMass(data.get("mass").getAsFloat());

		game.addEntityToWorld(this, data.get("x").getAsFloat(), data.get("y").getAsFloat());
		entity.getBody().getTransform().setPosition(new Vector2(data.get("x").getAsFloat(), data.get("y").getAsFloat()));
		entity.getBody().getTransform().setRotation(data.get("r").getAsFloat());
		entity.getBody().setLinearVelocity(data.get("vx").getAsFloat(), data.get("vy").getAsFloat());

		data.remove("size");
		data.remove("friction");
		data.remove("mass");
		data.remove("x");
		data.remove("y");
		data.remove("r");
		data.remove("vx");
		data.remove("vy");
		data.remove("txtr");
	}
	
	@Override
	public void write(JsonObject obj) {
		MapUtil.addMapToJson(obj, data);
		obj.addProperty("size", entity.getSize());
		obj.addProperty("friction", entity.getFriction());
		obj.addProperty("mass", entity.getMassData().mass);
		obj.addProperty("x", entity.getBody().getWorldCenter().x);
		obj.addProperty("y", entity.getBody().getWorldCenter().y);
		obj.addProperty("r", entity.getBody().getTransform().getRotation());
		obj.addProperty("vx", entity.getBody().getLinearVelocity().x);
		obj.addProperty("vy", entity.getBody().getLinearVelocity().y);
		obj.addProperty("txtr", zSprite.getTxtrName());
	}
}
