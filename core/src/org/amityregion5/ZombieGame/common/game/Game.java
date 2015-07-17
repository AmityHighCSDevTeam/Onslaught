package org.amityregion5.ZombieGame.common.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import java.util.Random;

import org.amityregion5.ZombieGame.common.Constants;
import org.amityregion5.ZombieGame.common.bullet.IBullet;
import org.amityregion5.ZombieGame.common.entity.EntityZombie;
import org.amityregion5.ZombieGame.common.entity.IEntity;
import org.amityregion5.ZombieGame.common.game.model.IEntityModel;
import org.amityregion5.ZombieGame.common.game.model.PlayerModel;
import org.amityregion5.ZombieGame.common.game.model.ZombieModel;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

public class Game implements Disposable {

	private World	world;
	private float	accumulator;
	private ArrayList<IEntityModel<?>>	entities, entitiesToDelete;
	private ArrayList<IBullet>	activeBullets;
	private ArrayList<PlayerModel> players;
	private Random				rand;
	private Difficulty diff;

	private double timeUntilNextWave;
	//private float waveDifficulty;
	private int mobsSpawned = 0;
	private int maxHostiles = 0;
	private int hostiles = 0;
	private double moduloConstant;

	private float big = 25;
	private float small = 12.5f;

	public Game(Difficulty diff) {
		this.diff = diff;

		world = new World(new Vector2(0, 0), true);
		entities = new ArrayList<IEntityModel<?>>();
		entitiesToDelete = new ArrayList<IEntityModel<?>>();
		activeBullets = new ArrayList<IBullet>();
		players = new ArrayList<PlayerModel>();
		rand = new Random();

		timeUntilNextWave = 10;
		moduloConstant = Math.pow(5.5, 10.0/7) - 0.5 - 2*diff.getDifficultyMultiplier();
		maxHostiles = (int) (100 * diff.getDifficultyMultiplier());
		//timeBetWaves = 15 * (Difficulty.diffInvertNum - diff.getDifficultyMultiplier());
		//waveDifficulty = 3 * diff.getDifficultyMultiplier();
	}

	public void tick(float deltaTime) {
		float frameTime = Math.min(deltaTime, 0.25f);
		accumulator += frameTime;
		while (accumulator >= Constants.TIME_STEP) {

			for (IEntityModel<?> e : entities) {
				// Body body = e.getBody();

				e.tick(Constants.TIME_STEP);
			}

			world.step(Constants.TIME_STEP, Constants.VELOCITY_ITERATIONS,
					Constants.POSITION_ITERATIONS);
			accumulator -= Constants.TIME_STEP;
			
			Iterator<IEntityModel<?>> i = entitiesToDelete.iterator();
			if(!world.isLocked()){
				while(i.hasNext()){
					IEntityModel<?> b = i.next();
					world.destroyBody(b.getEntity().getBody());
					entities.remove(b);
					i.remove();
					if (b.isHostile()) {
						hostiles--;
					}
				}
			}

			if (hostiles < maxHostiles) {
				if (timeUntilNextWave <= 0) {
					spawnNext();
					timeUntilNextWave = Math.min(Math.pow((Math.pow(mobsSpawned, 0.7))%(moduloConstant),6)/10000, 25);
					mobsSpawned++;
					//timeBetWaves -= Math.pow(2, (1.4 * timeBetWaves * diff.getDifficultyMultiplier())/60) - 1;
					//wavesSpawned++;
				}
				timeUntilNextWave -= deltaTime;
			}
		}
	}

	private void spawnNext() {
		for (PlayerModel player : players) {
			Vector2 pos = player.getEntity().getBody().getPosition();
			Vector2 spawnPos = null;
			boolean whilePara = true;
			do {
				final Vector2 v = new Vector2(pos.x - big/2 + rand.nextFloat()*big, pos.y - big/2 + rand.nextFloat()*big);
				spawnPos = v;
				whilePara = players.parallelStream().anyMatch((p)->p.getEntity().getBody().getPosition().dst2(v) < small * small);
			} while (whilePara);

			IEntityModel<?> eModel = getSpawningEntity();
			addEntityToWorld(eModel, spawnPos.x, spawnPos.y);
			if (eModel.isHostile()) {
				hostiles++;
			}
		}
	}

	private IEntityModel<?> getSpawningEntity() {
		EntityZombie zom = new EntityZombie();
		zom.setMass(100);
		//zom.setSpeed(1f);
		zom.setFriction(0.99f);

		ZombieModel model = new ZombieModel(zom, this);

		model.setSpeed(0.03f);
		model.setAllHealth((float) (Math.pow(1.1, Math.sqrt(mobsSpawned)) + 4)*(diff.getDifficultyMultiplier()/2+1));
		model.setPrizeMoney((5 + model.getHealth()/2)*(Difficulty.diffInvertNum - diff.getDifficultyMultiplier()));
		model.setDamage((5 + model.getHealth()/2)*(Difficulty.diffInvertNum - diff.getDifficultyMultiplier()));
		model.setRange(zom.getShape().getRadius()*1.1f);

		return model;
	}

	public World getWorld() {
		return world;
	}

	public void addEntityToWorld(IEntityModel<?> entity, float x, float y) {
		Body body = entity.getEntity().getBody();

		if (body == null) {
			// First we create a body definition
			BodyDef bodyDef = new BodyDef();
			// We set our body to dynamic, for something like ground which
			// doesn't move we would set it to StaticBody
			bodyDef.type = BodyType.DynamicBody;

			// Set our body's starting position in the world
			bodyDef.position.set(x, y);

			// Create our body in the world using our body definition
			body = world.createBody(bodyDef);

			entity.getEntity().setBody(body);
		}

		body.setLinearDamping(entity.getEntity().getFriction());
		body.setMassData(entity.getEntity().getMassData());

		Shape shape = entity.getEntity().getShape();

		// Create a fixture definition to apply our shape to
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 0.5f;
		fixtureDef.friction = 0.5f;
		fixtureDef.restitution = 0.6f; // Make it bounce a little bit

		// Create our fixture and attach it to the body
		/* Fixture fixture = */body.createFixture(fixtureDef);

		// Remember to dispose of any shapes after you're done with them!
		// BodyDef and FixtureDef don't need disposing, but shapes do.
		shape.dispose();

		if (entity instanceof PlayerModel) {
			players.add((PlayerModel)entity);
		}

		entities.add(entity);
	}

	public Optional<IEntityModel<?>> getEntityFromBody(Body b) {
		return entities.parallelStream().filter(e -> {
			return e.getEntity().getBody() == b;
		}).findFirst();
	}
	public Optional<IEntityModel<?>> getEntityModelFromEntity(IEntity e) {
		return entities.parallelStream().filter(e2 -> {
			return e2.getEntity() == e;
		}).findFirst();
	}

	@Override
	public void dispose() {
		world.dispose();
	}

	public ArrayList<IEntityModel<?>> getEntities() {
		return entities;
	}

	public void removeEntity(IEntityModel<?> e) {
		if (!entitiesToDelete.contains(e)) {
			entitiesToDelete.add(e);
		}
	}

	public void removeEntity(IEntity entity) {
		getEntityModelFromEntity(entity).ifPresent((m)->removeEntity(m));
	}

	public ArrayList<IBullet> getActiveBullets() {
		return activeBullets;
	}

	public Random getRandom() {
		return rand;
	}

	public Difficulty getDifficulty() {
		return diff;
	}
}
