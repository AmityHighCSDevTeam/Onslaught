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

	private static final float baseAtkCooldown = 1;
	private static final float minSecUntilGrowl = 10;
	private static final float maxSecUntilGrowl = 20;
	private static final float minGrowlVolume = 0.8f;
	private static final float maxGrowlVolume = 1.0f;

	private EntityZombie entity;
	private IEntity		target;
	private float health, maxHealth, speed, damage, range;
	private Game		g;
	private SpriteDrawingLayer		zSprite;
	private double prizeMoney;
	private AIMode ai;
	private float attackCooldown;
	private float secUntilGrowl = -1;
	private float growlVolume = -1;
	private float growlPitch = -1;
	private float sizeMultiplier;
	
	public ZombieModel() {
	}

	public ZombieModel(EntityZombie zom, Game g, float sizeMultiplier) {
		this.entity = zom;
		this.g = g;
		ai = AIMode.IDLE;
		this.sizeMultiplier = sizeMultiplier;
		int	textureIndex	= ZombieGame.instance.random
				.nextInt(TextureRegistry.getTextureNamesFor("*/Zombies/**.png").size());
		zSprite = new SpriteDrawingLayer(TextureRegistry.getTextureNamesFor("*/Zombies/**.png").get(textureIndex));
	}

	@Override
	public EntityZombie getEntity() {
		return entity;
	}

	@Override
	public void tick(float delta) {
		if (secUntilGrowl <= 0) {
			growlVolume = g.getRandom().nextFloat()*(maxGrowlVolume-minGrowlVolume) + minGrowlVolume;
			growlPitch = Math.min(Math.max(sizeMultiplier,0.5f),2f);
			List<String> soundNames = SoundRegistry.getSoundNamesFor("*/Audio/Zombie/*");
			g.playSound(new SoundData(soundNames.get(g.getRandom().nextInt(soundNames.size())), growlPitch, growlVolume), entity.getBody().getWorldCenter());
			secUntilGrowl = g.getRandom().nextFloat()*(maxSecUntilGrowl-minSecUntilGrowl) + minSecUntilGrowl;
		}
		secUntilGrowl -= delta;
		switch (ai) {
		case IDLE:
			IEntity closest = null;
			float dist2 = Float.MAX_VALUE;
			for (IEntityModel<?> m : g.getEntities()) {
				IEntity e = m.getEntity();
				if (e instanceof EntityPlayer) {
					float d = entity.getBody().getLocalCenter().dst2(
							e.getBody().getLocalCenter());
					if (d < dist2) {
						closest = e;
						dist2 = d;
					}
				}
			}
			target = closest;
			if (target != null) {
				ai = AIMode.FOLLOWING;
			}
			break;
		case FOLLOWING:
			if (target != null) {
				Optional<IEntityModel<?>> targetModel = g.getEntityModelFromEntity(target);
				if (targetModel.isPresent()) {
					if (targetModel.get().getHealth() > 0) {
						entity.getBody().applyForceToCenter(VectorFactory.createVector(getSpeed(),
								(float) MathHelper.getDirBetweenPoints(entity.getBody().getPosition(),
										target.getBody().getPosition())), true);
						BodyHelper.setPointing(entity.getBody(),
								target.getBody().getWorldCenter(), delta, 10);

						float fixedRange = (range + targetModel.get().getEntity().getShape().getRadius());
						if (entity.getBody().getWorldCenter().dst2(target.getBody().getWorldCenter()) <= fixedRange * fixedRange) {
							ai = AIMode.ATTACKING;
							attackCooldown = 0;
						}

						break;
					}
				}
			}
			ai = AIMode.IDLE;
			break;
		case ATTACKING:
			if (target != null) {
				Optional<IEntityModel<?>> targetModel = g.getEntityModelFromEntity(target);
				if (targetModel.isPresent()) {
					if (targetModel.get().getHealth() > 0) {
						BodyHelper.setPointing(entity.getBody(),
								target.getBody().getWorldCenter(), delta, 10);
						if (attackCooldown == 0) {
							targetModel.get().damage(damage, this, DamageTypes.ZOMBIE);
						} else if (attackCooldown >= baseAtkCooldown) {
							ai = AIMode.FOLLOWING;
						}
						attackCooldown += delta;
						break;
					}
				}
			}
			ai = AIMode.IDLE;
			break;
		}
		zSprite.getSprite().setOriginCenter();
	}

	@Override
	public void dispose() {
		target = null;
		entity.dispose();

	}

	public void setPrizeMoney(double prizeMoney) {
		this.prizeMoney = prizeMoney;
	}

	public double getPrizeMoney() {
		return prizeMoney;
	}

	@Override
	public float damage(float damage, IEntityModel<?> source, String damageType) {
		float damageTaken = Math.min(damage, health);
		if (damageTaken < 0) {
			damageTaken = 0;
		}
		for (int i = 0; i<damageTaken; i+=5) {
			g.addParticleToWorld(new BloodParticle(entity.getBody().getWorldCenter().x - entity.getSize()*1.25f + g.getRandom().nextFloat()*2*entity.getSize()*1.25f, entity.getBody().getWorldCenter().y - entity.getSize()*1.25f + g.getRandom().nextFloat()*2*entity.getSize()*1.25f, g));
		}
		health -= damageTaken;
		if (health <= 0 && damageTaken > 0) {
			if (source != null && source instanceof PlayerModel) {
				PlayerModel pModel = (PlayerModel) source;
				pModel.setMoney(pModel.getMoney() + prizeMoney);
			}
			if (g.getDifficulty().getHealthPackChance() > g.getRandom().nextDouble()) {
				g.addParticleToWorld(new HealthPackParticle(entity.getBody().getWorldCenter().x, entity.getBody().getWorldCenter().y, g));
			}
			g.removeEntity(this);
		}
		return damageTaken; 
	}

	@Override
	public IDrawingLayer[] getDrawingLayers() {
		return new IDrawingLayer[] {zSprite, HealthBarDrawingLayer.instance};
	}

	public void setAllHealth(float health) {
		setHealth(health);
		setMaxHealth(health);
	}

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

	public void setHealth(float health) {
		this.health = health;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public float getSpeed() {
		return speed;
	}

	@Override
	public boolean isHostile() {
		return true;
	}

	public void setDamage(float damage) {
		this.damage = damage;
	}

	public void setRange(float range) {
		this.range = range;
	}

	private enum AIMode {
		IDLE, FOLLOWING, ATTACKING;
	}
	
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
	public ZombieModel fromJSON(JSONObject obj, Game g) {
		float x = ((Number)obj.get("x")).floatValue();
		float y = ((Number)obj.get("y")).floatValue();
		float r = ((Number)obj.get("r")).floatValue();
		float attCool = ((Number)obj.get("attkCooldown")).floatValue();
		float dmg = ((Number)obj.get("damage")).floatValue();
		float spd = ((Number)obj.get("speed")).floatValue();
		float rng = ((Number)obj.get("range")).floatValue();
		float mH = ((Number)obj.get("maxHealth")).floatValue();
		double mny = ((Number)obj.get("money")).doubleValue();
		float szM = ((Number)obj.get("sizeMult")).floatValue();
		String txtr = (String)obj.get("txtr");
		float m = ((Number)obj.get("m")).floatValue();
		float f = ((Number)obj.get("f")).floatValue();
		float h = ((Number)obj.get("health")).floatValue();
		
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
		model.getEntity().getBody().getTransform().setPosition(new Vector2(x,y));
		model.getEntity().getBody().getTransform().setRotation(r);
		
		return null;
	}
}
