package org.amityregion5.ZombieGame.common.game;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

import org.amityregion5.ZombieGame.common.Constants;
import org.amityregion5.ZombieGame.common.bullet.IBullet;
import org.amityregion5.ZombieGame.common.entity.EntityPlayer;
import org.amityregion5.ZombieGame.common.entity.EntityZombie;
import org.amityregion5.ZombieGame.common.entity.IEntity;

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
	private ArrayList<IEntity>	entities, entitiesToDelete;
	private ArrayList<IBullet>	activeBullets;
	private ArrayList<EntityPlayer> players;
	private Random				rand;
	private Difficulty diff;

	private float timeUntilNextWave;
	private float timeBetWaves;
	private float waveDifficulty;
	private int wavesSpawned = 0;
	
	private float big = 100;
	private float small = 12.5f;

	public Game(Difficulty diff) {
		this.diff = diff;

		world = new World(new Vector2(0, 0), true);
		entities = new ArrayList<IEntity>();
		entitiesToDelete = new ArrayList<IEntity>();
		activeBullets = new ArrayList<IBullet>();
		players = new ArrayList<EntityPlayer>();
		rand = new Random();

		timeUntilNextWave = 10 * (Difficulty.diffInvertNum - diff.getDifficultyMultiplier());
		timeBetWaves = 15 * (Difficulty.diffInvertNum - diff.getDifficultyMultiplier());
		waveDifficulty = 3 * diff.getDifficultyMultiplier();
	}

	public void tick(float deltaTime) {
		float frameTime = Math.min(deltaTime, 0.25f);
		accumulator += frameTime;
		while (accumulator >= Constants.TIME_STEP) {

			entities.removeAll(entitiesToDelete);
			for (IEntity e : entitiesToDelete) {
				world.destroyBody(e.getBody());
			}

			entitiesToDelete.clear();

			for (IEntity e : entities) {
				// Body body = e.getBody();

				e.tick(Constants.TIME_STEP);
			}

			world.step(Constants.TIME_STEP, Constants.VELOCITY_ITERATIONS,
					Constants.POSITION_ITERATIONS);
			accumulator -= Constants.TIME_STEP;

			if (timeUntilNextWave <= 0) {
				spawnWave();
				timeUntilNextWave = timeBetWaves;
				timeBetWaves -= Math.pow(2, (1.4 * timeBetWaves * diff.getDifficultyMultiplier())/60) - 1;
				wavesSpawned++;
			}
			timeUntilNextWave -= deltaTime;
		}
	}

	private void spawnWave() {
		for (int i = 0; i<waveDifficulty * wavesSpawned/4; i++) {
			spawnEntity();
		}
	}
	
	private void spawnEntity() {		
		for (EntityPlayer player : players) {
			Vector2 pos = player.getBody().getPosition();
			Vector2 spawnPos = null;
			boolean whilePara = true;
			do {
				final Vector2 v = new Vector2(pos.x - big/2 + rand.nextFloat()*big, pos.y - big/2 + rand.nextFloat()*big);
				spawnPos = v;
				whilePara = players.parallelStream().anyMatch((p)->p.getBody().getPosition().dst2(v) < small * small);
			} while (whilePara);

			addEntityToWorld(getSpawningEntity(), spawnPos.x, spawnPos.y);
		}
	}

	private IEntity getSpawningEntity() {
		EntityZombie zom = new EntityZombie(this);
		zom.setMass(100);
		zom.setSpeed(0.03f);
		//zom.setSpeed(1f);
		zom.setFriction(0.99f);
		zom.setHealth(5 + wavesSpawned);
		zom.setPrizeMoney(5 + wavesSpawned*waveDifficulty + zom.getHealth()/2);

		return zom;
	}

	public World getWorld() {
		return world;
	}

	public void addEntityToWorld(IEntity entity, float x, float y) {
		Body body = entity.getBody();

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

			entity.setBody(body);
		}

		body.setLinearDamping(entity.getFriction());
		body.setMassData(entity.getMassData());

		Shape shape = entity.getShape();

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

		if (entity instanceof EntityPlayer) {
			players.add((EntityPlayer) entity);
		}

		entities.add(entity);
	}

	public Optional<IEntity> getEntityFromBody(Body b) {
		return entities.parallelStream().filter(e -> {
			return e.getBody() == b;
		}).findFirst();
	}

	@Override
	public void dispose() {
		world.dispose();
	}

	public ArrayList<IEntity> getEntities() {
		return entities;
	}

	public void removeEntity(IEntity e) {
		entitiesToDelete.add(e);
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
