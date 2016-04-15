package org.amityregion5.ZombieGame.common.game.model.entity;

import java.util.List;
import java.util.Optional;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.client.asset.SoundRegistry;
import org.amityregion5.ZombieGame.client.asset.TextureRegistry;
import org.amityregion5.ZombieGame.client.game.HealthBarDrawingLayer;
import org.amityregion5.ZombieGame.client.game.IDrawingLayer;
import org.amityregion5.ZombieGame.client.game.SpriteDrawingLayer;
import org.amityregion5.ZombieGame.common.entity.EntityPlayer;
import org.amityregion5.ZombieGame.common.entity.EntityZombie;
import org.amityregion5.ZombieGame.common.entity.IEntity;
import org.amityregion5.ZombieGame.common.func.Consumer3;
import org.amityregion5.ZombieGame.common.game.DamageTypes;
import org.amityregion5.ZombieGame.common.game.Game;
import org.amityregion5.ZombieGame.common.game.model.IEntityModel;
import org.amityregion5.ZombieGame.common.game.model.particle.BloodParticle;
import org.amityregion5.ZombieGame.common.game.model.particle.HealthPackParticle;
import org.amityregion5.ZombieGame.common.helper.BodyHelper;
import org.amityregion5.ZombieGame.common.helper.MathHelper;
import org.amityregion5.ZombieGame.common.helper.VectorFactory;
import org.amityregion5.ZombieGame.common.weapon.data.SoundData;
import org.json.simple.JSONObject;

import com.badlogic.gdx.math.Vector2;

public class ZombieModel implements IEntityModel<EntityZombie> {

	private static final float	baseAtkCooldown		= 1; //Attack cooldown
	private static final float	minSecUntilGrowl	= 10; //Minimum time between growls
	private static final float	maxSecUntilGrowl	= 20; //Maximum time between growls
	private static final float	minGrowlVolume		= 0.8f; //Minimum growl volume
	private static final float	maxGrowlVolume		= 1.0f; //Maximum growl volume

	private EntityZombie		entity; //Entity
	private IEntity				target; //Target
	private float				health, maxHealth, speed, damage, range; //Health, Speed, Damage, Range
	private Game				g; //Game
	private SpriteDrawingLayer	zSprite; //Sprite
	private double				prizeMoney; //Prize money
	private AIMode				ai; //AI Mode
	private float				attackCooldown; //attack cooldown
	private float				secUntilGrowl	= -1; //seconds until growl
	private float				growlVolume		= -1; //Growl volume
	private float				growlPitch		= -1; //Growl pitch
	private float				sizeMultiplier; //Size multiplier

	private float timeUntilIdleCheck = 0;

	public ZombieModel() {}
	
	public ZombieModel(EntityZombie zom, Game g, float sizeMultiplier) {
		entity = zom;
		this.g = g;
		ai = AIMode.IDLE; //Set variables
		this.sizeMultiplier = sizeMultiplier;

		//Get random zombie sprite
		int textureIndex = ZombieGame.instance.random.nextInt(TextureRegistry.getTextureNamesFor("*/Zombies/**.png").size());
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
			growlPitch = Math.min(Math.max(sizeMultiplier, 0.5f), 2f);
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
						float fixedRange = (range + targetModel.get().getEntity().getShape().getRadius());

						//If we are within range
						if (entity.getBody().getWorldCenter().dst2(target.getBody().getWorldCenter()) <= fixedRange * fixedRange) {
							//Set AI to attacking
							ai = AIMode.ATTACKING;
							//Set cooldown
							attackCooldown = 0;
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
						if (attackCooldown == 0) {
							//Damage it
							targetModel.get().damage(damage, this, DamageTypes.ZOMBIE);
						} else if (attackCooldown >= baseAtkCooldown) {
							//If cooldown completed
							//Back to following mode
							ai = AIMode.FOLLOWING;
						}
						//Increase cooldown
						attackCooldown += delta;
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
		this.prizeMoney = prizeMoney; //Set prize money
	}

	/**
	 * Get the amount of money to be given for killing this zombie
	 * 
	 * @return the money to be given
	 */
	public double getPrizeMoney() {
		return prizeMoney; //Get prize money
	}

	@Override
	public float damage(float damage, IEntityModel<?> source, String damageType) {
		//Get the damage that is taken from the source
		float damageTaken = Math.min(damage, health);
		
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
		health -= damageTaken;
		//Set AI to follow the thing that hurt it
		ai = AIMode.FOLLOWING;
		//Set the thing that hurt it as the target
		target = source.getEntity();
		
		//If this was the killing blow
		if (health <= 0 && damageTaken > 0) {
			//If the source is a player
			if (source != null && source instanceof PlayerModel) {
				PlayerModel pModel = (PlayerModel) source;
				//Give the player money
				pModel.setMoney(pModel.getMoney() + prizeMoney);
				pModel.addScore(prizeMoney * 0.05 + 1);
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
		this.maxHealth = maxHealth;
	}

	@Override
	public float getMaxHealth() {
		return maxHealth;
	}

	@Override
	public float getHealth() {
		return health;
	}

	/**
	 * Set the health
	 * 
	 * @param health the new health
	 */
	public void setHealth(float health) {
		this.health = health;
	}

	/**
	 * Set the speed
	 * 
	 * @param speed the new speed
	 */
	public void setSpeed(float speed) {
		this.speed = speed;
	}

	/**
	 * Get the speed
	 * 
	 * @return the speed
	 */
	public float getSpeed() {
		return speed;
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
		this.damage = damage;
	}

	/**
	 * Set the range
	 * 
	 * @param range the range
	 */
	public void setRange(float range) {
		this.range = range;
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
		zSprite.getSprite().setTexture(TextureRegistry.getTexturesFor(txtr).get(0));
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject convertToJSONObject() {
		JSONObject obj = new JSONObject();

		obj.put("x", entity.getBody().getWorldCenter().x);
		obj.put("y", entity.getBody().getWorldCenter().y);
		obj.put("r", entity.getBody().getTransform().getRotation());
		obj.put("attkCooldown", attackCooldown);
		obj.put("damage", damage);
		obj.put("speed", speed);
		obj.put("range", range);
		obj.put("maxHealth", maxHealth);
		obj.put("money", prizeMoney);
		obj.put("sizeMult", sizeMultiplier);
		obj.put("txtr", zSprite.getTxtrName());
		obj.put("m", entity.getMassData().mass);
		obj.put("f", entity.getFriction());
		obj.put("health", health);

		return obj;
	}

	@Override
	public ZombieModel fromJSON(JSONObject obj, Game g, Consumer3<String, String, Boolean> addErrorConsumer) {
		float x = ((Number) obj.get("x")).floatValue();
		float y = ((Number) obj.get("y")).floatValue();
		float r = ((Number) obj.get("r")).floatValue();
		float attCool = ((Number) obj.get("attkCooldown")).floatValue();
		float dmg = ((Number) obj.get("damage")).floatValue();
		float spd = ((Number) obj.get("speed")).floatValue();
		float rng = ((Number) obj.get("range")).floatValue();
		float mH = ((Number) obj.get("maxHealth")).floatValue();
		double mny = ((Number) obj.get("money")).doubleValue();
		float szM = ((Number) obj.get("sizeMult")).floatValue();
		String txtr = (String) obj.get("txtr");
		float m = ((Number) obj.get("m")).floatValue();
		float f = ((Number) obj.get("f")).floatValue();
		float h = ((Number) obj.get("health")).floatValue();

		ZombieModel model = new ZombieModel(new EntityZombie(0.15f * szM), g, szM);
		model.attackCooldown = attCool;
		model.damage = dmg;
		model.speed = spd;
		model.range = rng;
		model.maxHealth = mH;
		model.prizeMoney = mny;
		model.health = h;
		model.setTexture(txtr);
		model.getEntity().setFriction(f);
		model.getEntity().setMass(m);
		g.addEntityToWorld(model, x, y);
		model.getEntity().getBody().getTransform().setPosition(new Vector2(x, y));
		model.getEntity().getBody().getTransform().setRotation(r);

		return null;
	}
}
