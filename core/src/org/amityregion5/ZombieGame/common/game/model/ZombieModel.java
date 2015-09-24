package org.amityregion5.ZombieGame.common.game.model;

import java.util.Optional;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.client.game.HealthBarDrawingLayer;
import org.amityregion5.ZombieGame.client.game.IDrawingLayer;
import org.amityregion5.ZombieGame.client.game.SpriteDrawingLayer;
import org.amityregion5.ZombieGame.client.game.TextureRegistry;
import org.amityregion5.ZombieGame.common.entity.EntityPlayer;
import org.amityregion5.ZombieGame.common.entity.EntityZombie;
import org.amityregion5.ZombieGame.common.entity.IEntity;
import org.amityregion5.ZombieGame.common.game.Game;
import org.amityregion5.ZombieGame.common.helper.BodyHelper;
import org.amityregion5.ZombieGame.common.helper.MathHelper;
import org.amityregion5.ZombieGame.common.helper.VectorFactory;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class ZombieModel implements IEntityModel<EntityZombie> {

	private EntityZombie entity;
	private IEntity		target;
	private float health, maxHealth, speed, damage, range;
	private Game		g;
	private int			textureIndex	= ZombieGame.instance.random
			.nextInt(TextureRegistry.getTexturesFor("*/Zombies/**.png").size());
	private SpriteDrawingLayer		zSprite;
	private double prizeMoney;
	private AIMode ai;
	private float attackCooldown;
	private static final float baseAtkCooldown = 1;

	public ZombieModel(EntityZombie zom, Game g) {
		this.entity = zom;
		this.g = g;
		ai = AIMode.IDLE;
		zSprite = new SpriteDrawingLayer(new Sprite(TextureRegistry.getTexturesFor("*/Zombies/**.png").get(textureIndex)));
	}

	@Override
	public EntityZombie getEntity() {
		return entity;
	}

	@Override
	public void tick(float delta) {
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
								 targetModel.get().damage(damage, this);
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
	public float damage(float damage, IEntityModel<?> source) {
		float damageTaken = Math.min(damage, health);
		if (damageTaken < 0) {
			damageTaken = 0;
		}
		health -= damageTaken;
		if (health <= 0 && damageTaken > 0) {
			if (source != null && source instanceof PlayerModel) {
				PlayerModel pModel = (PlayerModel) source;
				pModel.setMoney(pModel.getMoney() + prizeMoney);
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
}
