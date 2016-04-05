package org.amityregion5.ZombieGame.common.game;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.client.asset.SoundPlayingData;
import org.amityregion5.ZombieGame.common.Constants;
import org.amityregion5.ZombieGame.common.bullet.ExplosionRaycastBullet;
import org.amityregion5.ZombieGame.common.bullet.IBullet;
import org.amityregion5.ZombieGame.common.entity.IEntity;
import org.amityregion5.ZombieGame.common.func.Consumer3;
import org.amityregion5.ZombieGame.common.game.difficulty.BasicDifficulty;
import org.amityregion5.ZombieGame.common.game.difficulty.Difficulty;
import org.amityregion5.ZombieGame.common.game.model.IEntityModel;
import org.amityregion5.ZombieGame.common.game.model.IParticle;
import org.amityregion5.ZombieGame.common.game.model.entity.PlayerModel;
import org.amityregion5.ZombieGame.common.game.model.particle.ExplosionParticleModel;
import org.amityregion5.ZombieGame.common.helper.VectorFactory;
import org.amityregion5.ZombieGame.common.shop.IPurchaseable;
import org.amityregion5.ZombieGame.common.weapon.data.SoundData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

import box2dLight.RayHandler;

public class Game implements Disposable {

	// Static Variables
	private static final float	bigSpawnRad			= 25; // Maximum spawn radius
	private static final float	smallSpnRad			= 12.5f; //Minimum spawn radius
	private static final float	explosionMinVal		= 0.05f; //Minimum damage value of explosion rays to do 
	private static final int	explosionRaycasts	= 540; //Raycasts per explosion
	private static final float	areaPerParticle		= 4f; //The area in square meters per explosion particle

	// Unsaved variables
	protected GameContactListener			contactListener; // The contact listener
	protected World							world; //The world
	protected float							accumulator; //An accumulator used for ticks
	protected ArrayList<IEntityModel<?>>	entities, entitiesToAdd, entitiesToDelete; //Entities
	protected ArrayList<IParticle>			particles, particlesToAdd, particlesToDelete; //Particle
	protected ArrayList<IBullet>			activeBullets; //The current bullets
	protected ArrayList<PlayerModel>		players; //All the players
	protected Random						rand; //A random
	protected RayHandler					lighting; //The lighting

	// Calculated Variables (Unsaved)
	protected int		maxHostiles	= 0; // Maximum hostiles that can spawn
	protected double	moduloConstant; // A constant used for computing zombie spawning
	protected int		hostiles	= 0; // The current number of hostiles

	// Saved Variables
	protected Difficulty	diff; // The difficulty
	protected boolean		isCheatMode; //Is the game cheat mode
	protected boolean		isSinglePlayer; //Is the game singleplayer
	protected boolean		isPaused; //Is the game paused
	protected double		timeUntilNextSpawn; //The time in seconds until the next spawn is done
	protected int			mobsSpawned	= 0; // The number of mobs that have been spawned
	protected boolean isLightingEnabled = true;

	public Game(Difficulty diff, boolean singlePlayer, boolean cheatMode) {
		this.diff = diff; //Set difficulty
		isCheatMode = cheatMode; //Set cheat mode
		isPaused = false; //Set not paused
		isSinglePlayer = singlePlayer; //Set singleplayer

		world = new World(new Vector2(0, 0), true); //Create world
		contactListener = new GameContactListener(); //Create contact listener

		world.setContactListener(contactListener); //Set the contact listener
		entities = new ArrayList<IEntityModel<?>>(); //Create entities array
		entitiesToAdd = new ArrayList<IEntityModel<?>>();
		entitiesToDelete = new ArrayList<IEntityModel<?>>();
		particles = new ArrayList<IParticle>(); //Create particles array
		particlesToAdd = new ArrayList<IParticle>();
		particlesToDelete = new ArrayList<IParticle>();
		activeBullets = new ArrayList<IBullet>(); //Create bullets array
		players = new ArrayList<PlayerModel>(); //Create player array
		rand = new Random(); //Create random

		timeUntilNextSpawn = 10; //10 seconds until first zombie spawns

		//5.5^(10/7) = 11.4198654
		//Constant = 10.9198654 - 2 * waveModifier
		moduloConstant = Math.pow(5.5, 9.0 / 7) - 0.5 - 2 * diff.getZombieWaveModifier();

		//Set max hostiles
		maxHostiles = diff.getMaxHostiles();
		
		world.setContactListener(contactListener);
		
		ZombieGame.instance.pluginManager.getPlugins().forEach((m)->m.getPlugins().forEach((p)->p.onGameStart(this)));
	}

	/**
	 * Called each tick to compute changes in the world
	 * 
	 * @param deltaTime the amount of time since last tick
	 */
	public void tick(float deltaTime) {
		//If it is taking longer than 1/4 of a second to tick only calculate up to 1/4 of a second
		float frameTime = Math.min(deltaTime, 0.25f);

		//If the game isnt paused
		if (!isPaused()) {
			//Increment the accumulator
			accumulator += frameTime;
			//If the accumulator is greater than the time step (1/60 or 0.016666668)
			while (accumulator >= Constants.TIME_STEP) {

				//Tick all entities and particles
				entities.forEach((e) -> e.tick(Constants.TIME_STEP));
				particles.forEach((p) -> p.tick(Constants.TIME_STEP));

				//Step the world
				world.step(Constants.TIME_STEP, Constants.VELOCITY_ITERATIONS, Constants.POSITION_ITERATIONS);
				//Decrement the accumulator
				accumulator -= Constants.TIME_STEP;

				{ // Deletion of Entities
					Iterator<IEntityModel<?>> i = entitiesToDelete.iterator();
					if (!world.isLocked()) {
						while (i.hasNext()) {
							IEntityModel<?> b = i.next();
							world.destroyBody(b.getEntity().getBody());
							entities.remove(b);
							i.remove();
							if (b.isHostile()) {
								hostiles--;
							}
							b.dispose();
						}
					}
				}
				{ // Deletion of Particles
					Iterator<IParticle> i = particlesToDelete.iterator();
					if (!world.isLocked()) {
						while (i.hasNext()) {
							IParticle p = i.next();
							particles.remove(p);
							i.remove();
							p.dispose();
						}
					}
				}
				{ // Addition of Entities
					Iterator<IEntityModel<?>> i = entitiesToAdd.iterator();
					if (!world.isLocked()) {
						while (i.hasNext()) {
							IEntityModel<?> b = i.next();
							entities.add(b);
							i.remove();
						}
					}
				}
				{ // Addition of Particles
					Iterator<IParticle> i = particlesToAdd.iterator();
					if (!world.isLocked()) {
						while (i.hasNext()) {
							IParticle p = i.next();
							particles.add(p);
							i.remove();
						}
					}
				}

				// Spawning of mobs
				//If there are less hostiles than the maximum and spawning is enabled
				if (hostiles < maxHostiles && diff.doSpawnZombies()) {
					//While it is time to spawn and we dont have enough hostiles
					while (timeUntilNextSpawn <= 0 && hostiles < maxHostiles) {
						//Spawn the next mob
						spawnNext();
						//Increase time until next spawn
						//Next zombie will spawn in either ((((mobsSpawned^0.7) mod (moduloConstant))^6)/10000) or  (25) depending on which is lower
						timeUntilNextSpawn += Math.min(Math.pow((Math.pow(mobsSpawned, 0.7)) % (moduloConstant), 6) / 10000, 10);
						//Increase mob spawn count
						mobsSpawned++;
					}
					//Decrement time until next spawn
					timeUntilNextSpawn -= frameTime;
				}
			}
		}
	}

	/**
	 * Spawn the next zombie
	 */
	private void spawnNext() {
		//Spawning is per player
		for (PlayerModel player : players) {
			//For each player
			
			//Get a position that is within bigSpawnRad and outside of smallSpawnRadius of each player
			Vector2 pos = player.getEntity().getBody().getPosition();
			Vector2 spawnPos = null;
			boolean whilePara = true;
			do {
				final Vector2 v = new Vector2(pos.x - bigSpawnRad / 2 + rand.nextFloat() * bigSpawnRad,
						pos.y - bigSpawnRad / 2 + rand.nextFloat() * bigSpawnRad);
				spawnPos = v;
				whilePara = players.parallelStream().anyMatch((p) -> p.getEntity().getBody().getPosition().dst2(v) < smallSpnRad * smallSpnRad);
			} while (whilePara);

			//Get an entity to spawn
			IEntityModel<?> eModel = GameRegistry.getSpawn(this);
			//Add the entity to the world
			addEntityToWorld(eModel, spawnPos.x, spawnPos.y);
		}
	}

	/**
	 * Get the world object
	 * @return the world object
	 */
	public World getWorld() {
		return world;
	}

	/**
	 * Adds a particle to the world
	 * 
	 * @param particle the particle to add
	 */
	public void addParticleToWorld(IParticle particle) {
		particlesToAdd.add(particle);
	}

	/**
	 * Adds a entity to the world the simple way
	 * 
	 * @param entity the entity model
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public void addEntityToWorld(IEntityModel<?> entity, float x, float y) {
		addEntityToWorld(entity, x, y, (short) 1, (short) 1);
	}

	/**
	 * Adds an entity to the world the complicated way
	 * 
	 * @param entity the entity model
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param maskBits the categories that this entity can collide with
	 * @param categoryBits the category of this entity
	 */
	public void addEntityToWorld(IEntityModel<?> entity, float x, float y, short maskBits, short categoryBits) {

		//If it is a hostile increment the hostiles count
		if (entity.isHostile()) {
			hostiles++;
		}

		//Get the body
		Body body = entity.getEntity().getBody();

		//Create it if it doesnt exist
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

		//Set linear damping
		body.setLinearDamping(entity.getEntity().getFriction());
		//Set mass
		body.setMassData(entity.getEntity().getMassData());

		//Get shape
		Shape shape = entity.getEntity().getShape();

		// Create a fixture definition to apply our shape to
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 0.5f;
		fixtureDef.friction = 0.5f;
		fixtureDef.restitution = 0f; // Make it bounce a little bit
		fixtureDef.filter.categoryBits = categoryBits;
		fixtureDef.filter.maskBits = maskBits;

		// Create our fixture and attach it to the body
		/* Fixture fixture = */body.createFixture(fixtureDef);

		// Remember to dispose of any shapes after you're done with them!
		// BodyDef and FixtureDef don't need disposing, but shapes do.
		shape.dispose();

		//If it is a player add it to our list of players
		if (entity instanceof PlayerModel) {
			players.add((PlayerModel) entity);
		}

		//Add it to the entities
		entitiesToAdd.add(entity);
	}

	/**
	 * Get an entity from a body
	 * 
	 * @param b the body to get an entity from
	 * @return an optional that may contain an entity model
	 */
	public Optional<IEntityModel<?>> getEntityFromBody(Body b) {
		return entities.parallelStream().filter(e -> {
			return e.getEntity().getBody() == b;
		}).findFirst();
	}

	/**
	 * Get an entity model from an entity
	 * 
	 * @param e the entity to get the model from
	 * @return and optional that may contain an entity model
	 */
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
		getEntityModelFromEntity(entity).ifPresent((m) -> removeEntity(m));
	}

	public ArrayList<IParticle> getParticles() {
		return particles;
	}

	public void removeParticle(IParticle p) {
		if (!particlesToDelete.contains(p)) {
			particlesToDelete.add(p);
		}
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

	public RayHandler getLighting() {
		return lighting;
	}

	public void setLighting(RayHandler lighting) {
		this.lighting = lighting;
	}

	/**
	 * Simulate an explosion
	 * 
	 * @param pos the position of the center of the explosion
	 * @param strength the strength of the explosion
	 * @param source the player who caused the explosion
	 */
	public void makeExplosion(Vector2 pos, double strength, PlayerModel source) {

		if (strength <= 0) {
			ZombieGame.error("Explosion Strength value of <= 0 attempted.");
			return;
		}

		//Calculate max distance to simulate to
		double dist = (strength / explosionRaycasts) / explosionMinVal;

		//Radians per raycast
		double radPerRaycast = 2 * Math.PI / explosionRaycasts;
		
		//Rotate through each ray
		for (int i = 0; i < explosionRaycasts; i++) {
			//Get its direction
			double dir = radPerRaycast * i;

			//Get its vector
			Vector2 bullVector = VectorFactory.createVector((float) dist, (float) dir);

			//Create the explosion bullet
			ExplosionRaycastBullet bull = new ExplosionRaycastBullet(this, pos, (float) (strength / explosionRaycasts), bullVector, source);
			//Set its direction
			bull.setDir((float) dir);

			//Add it to the list of bullets
			getActiveBullets().add(bull);
			//Ray cast it
			getWorld().rayCast(bull, pos, bullVector.add(pos));
			//Apply damage
			bull.finishRaycast();
		}

		//Area of the explosion
		double area = Math.PI * dist * dist;

		//Number of particles
		int particles = (int) (area / areaPerParticle);

		//Only do for half the distance
		dist /= 2;

		//For each particle
		for (int i = 0; i < particles; i++) {
			//Get its position
			Vector2 pos2 = VectorFactory.createVector((float) (rand.nextDouble() * rand.nextDouble() * dist), (float) (rand.nextDouble() * Math.PI * 2))
					.add(pos);

			//Create the particle
			ExplosionParticleModel explosionParticle = new ExplosionParticleModel(pos2.x, pos2.y, new Color(1f, 1f, 0f, 1f), this,
					(float) (2 * Math.PI * rand.nextDouble()), 100 * (rand.nextFloat() - 0.5f), 0.05f * pos2.dst2(pos), pos2.sub(pos).angleRad());

			//Set its light
			//explosionParticle.setLight(new PointLight(lighting, 10, explosionParticle.getColor(), 2, pos2.x, pos2.y));
			//explosionParticle.getLight().setXray(true);

			//Add it to the world
			addParticleToWorld(explosionParticle);
		}

		//For each player
		for (PlayerModel player : players) {
			//Get distance to explosion
			float distToExplosion = pos.dst(player.getEntity().getBody().getWorldCenter());
			if (distToExplosion < 1) {
				distToExplosion = 1;
			}
			//Vibrate based on distance
			player.setScreenVibrate(player.getScreenVibrate() + strength / distToExplosion / 4);
		}

		//Play explosion sound
		playSound(new SoundData("Core/Audio/explode.wav", 1, strength), pos);
	}

	public GameContactListener getContactListener() {
		return contactListener;
	}

	/**
	 * Play a sound for all players
	 * 
	 * @param sound the sound to play
	 * @param position the position of the sound
	 */
	public void playSound(SoundData sound, Vector2 position) {
		// ZombieGame.debug("Game: Playing Sound: " + sound.getAssetName());
		//For each player
		for (PlayerModel player : players) {
			//Create a playing data for the sound
			SoundPlayingData playing = new SoundPlayingData(sound.getAssetName(),
					Math.min(sound.getMaxVolume() / player.getEntity().getBody().getWorldCenter().dst2(position), 1), sound.getPitch());
			//Send it to the player
			player.playSound(playing);
		}
	}

	public ArrayList<PlayerModel> getPlayers() {
		return players;
	}

	public void doPlayerDie(PlayerModel playerModel) {
		players.remove(playerModel);
		removeEntity(playerModel);
	}

	public boolean isGameRunning() {
		return getPlayers().size() > 0;
	}

	public boolean isCheatMode() {
		return isCheatMode;
	}

	public boolean isSinglePlayer() {
		return isSinglePlayer;
	}

	public boolean isPaused() {
		return isPaused;
	}

	public void setPaused(boolean isPaused) {
		this.isPaused = isPaused;
	}

	/**
	 * Save to a file
	 * 
	 * @param saveName the file to save to
	 */
	@SuppressWarnings("unchecked")
	public void saveToFile(String saveName) {
		//File reference
		FileHandle file = ZombieGame.instance.settingsFile.parent().child("saves/" + saveName + ".save");

		JSONObject obj = new JSONObject();

		//Put name
		obj.put("name", saveName);

		JSONArray entitiesSaving = new JSONArray();

		//Put entity data
		for (IEntityModel<?> e : entities) {
			JSONObject o = e.convertToJSONObject();
			
			if (o == null) {
				continue;
			}
			JSONObject container = new JSONObject();
			container.put("type", e.getClass().getName());
			container.put("data", o);
			entitiesSaving.add(container);
		}

		obj.put("entities", entitiesSaving);

		JSONArray particlesSaving = new JSONArray();

		//Put particle data
		for (IParticle e : particles) {
			JSONObject o = e.convertToJSONObject();

			if (o == null) {
				continue;
			}
			JSONObject container = new JSONObject();
			container.put("type", e.getClass().getName());
			container.put("data", o);
			particlesSaving.add(container);
		}

		obj.put("particles", particlesSaving);

		//Put other data
		obj.put("difficulty", diff.getUniqueID());
		obj.put("cheatMode", isCheatMode);
		obj.put("singlePlayer", isSinglePlayer);
		obj.put("paused", isPaused);
		obj.put("timeUntilSpawn", timeUntilNextSpawn);
		obj.put("mobsSpawned", mobsSpawned);

		//Write to file
		file.writeString(obj.toJSONString(), false);
	}

	/**
	 * Load the game from a file
	 * 
	 * @param saveName the file name
	 * @return the loaded game
	 */
	public static GameLoadedContainer loadFromFile(String saveName) {
		//Get file
		FileHandle file = ZombieGame.instance.settingsFile.parent().child("saves/" + saveName + ".save");
		JSONParser parser = new JSONParser();
		
		HashMap<String, List<String>> errors = new HashMap<String, List<String>>();
		GameLoadedContainer glc = new GameLoadedContainer();
		glc.canBeLoaded = true;
		
		Consumer3<String, String, Boolean> addErrorConsumer = (header, output, allowsLoading) -> {
			if (!errors.containsKey(header)) {
				errors.put(header, new ArrayList<String>());
			}
			errors.get(header).add(output);
			if (!allowsLoading) {
				glc.canBeLoaded = false;
			}
		};

		Reader reader = file.reader();
		try {
			JSONObject obj = (JSONObject) parser.parse(reader);

			//Get entities
			JSONArray entities = (JSONArray) obj.get("entities");
			//Get particles
			JSONArray particles = (JSONArray) obj.get("particles");

			//Get Variables
			Optional<Difficulty> diff = GameRegistry.getDifficultyFromID((String) obj.get("difficulty"));
			if (!diff.isPresent()) {
				addErrorConsumer.run("Difficulty not found:", (String) obj.get("difficulty"), false);
			}
			boolean cheatMode = (Boolean) obj.get("cheatMode");
			boolean singlePlayer = (Boolean) obj.get("singlePlayer");
			boolean paused = (Boolean) obj.get("paused");
			double timeUntilSpawn = ((Number) obj.get("timeUntilSpawn")).doubleValue();
			int mobsSpawned = ((Number) obj.get("mobsSpawned")).intValue();

			//Create the game
			Game game = new Game(diff.orElse(BasicDifficulty.ERROR_DIFFICULTY), singlePlayer, cheatMode);
			//Set variables
			game.setPaused(paused);
			game.setTimeUntilNextSpawn(timeUntilSpawn);
			game.setMobsSpawned(mobsSpawned);

			//Add all entites to the world
			for (Object o : entities) {
				JSONObject container = (JSONObject) o;

				String className = (String) container.get("type");

				try {
					Class<?> clazz = Class.forName(className);
					JSONObject e = (JSONObject) container.get("data");

					//fromJSON method called
					Method method = clazz.getMethod("fromJSON", JSONObject.class, Game.class, Consumer3.class);
					method.invoke(clazz.newInstance(), e, game, addErrorConsumer);
				} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | InstantiationException e1) {
					e1.printStackTrace();
					addErrorConsumer.run("Failed to load entities:", className, true);
				}
			}
			//Add all particles to the world
			for (Object o : particles) {
				JSONObject container = (JSONObject) o;

				String className = (String) container.get("type");

				try {
					Class<?> clazz = Class.forName(className);
					JSONObject e = (JSONObject) container.get("data");

					//fromJSON method called
					Method method = clazz.getMethod("fromJSON", JSONObject.class, Game.class, Consumer3.class);
					method.invoke(clazz.newInstance(), e, game, addErrorConsumer);
				} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | InstantiationException e1) {
					e1.printStackTrace();
					addErrorConsumer.run("Failed to load particles:", className, true);
				}
			}
			
			glc.game = game;
			glc.errors = errors;
			
			//Return the game
			return glc;
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		//If failed return nothing
		return null;
	}

	private void setTimeUntilNextSpawn(double timeUntilNextSpawn) {
		this.timeUntilNextSpawn = timeUntilNextSpawn;
	}

	private void setMobsSpawned(int mobsSpawned) {
		this.mobsSpawned = mobsSpawned;
	}

	public PlayerModel getSingleplayerPlayer() {
		return players.get(0);
	}

	public int getMobsSpawned() {
		return mobsSpawned;
	}

	public boolean isLightingEnabled() {
		return isLightingEnabled;
	}

	public int getHostiles() {
		return hostiles;
	}

	public void setLightingEnabled(boolean b) {
		isLightingEnabled = b;
	}
	
	public boolean isPurchaseAllowed(IPurchaseable purchaseable) {
		return true;
	}
	
	public boolean canSaveGame() {
		return true;
	}
}
