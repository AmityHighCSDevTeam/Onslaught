package org.amityregion5.onslaught.common.game;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Random;

import org.amityregion5.onslaught.Onslaught;
import org.amityregion5.onslaught.client.asset.SoundPlayingData;
import org.amityregion5.onslaught.common.Constants;
import org.amityregion5.onslaught.common.bullet.ExplosionRaycastBullet;
import org.amityregion5.onslaught.common.bullet.IBullet;
import org.amityregion5.onslaught.common.entity.IEntity;
import org.amityregion5.onslaught.common.func.Consumer3;
import org.amityregion5.onslaught.common.game.difficulty.BasicDifficulty;
import org.amityregion5.onslaught.common.game.difficulty.Difficulty;
import org.amityregion5.onslaught.common.game.model.IEntityModel;
import org.amityregion5.onslaught.common.game.model.IParticle;
import org.amityregion5.onslaught.common.game.model.entity.PlayerModel;
import org.amityregion5.onslaught.common.game.model.particle.ExplosionParticleModel;
import org.amityregion5.onslaught.common.helper.VectorFactory;
import org.amityregion5.onslaught.common.shop.IPurchaseable;
import org.amityregion5.onslaught.common.weapon.data.SoundData;

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
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.stream.JsonWriter;

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
	protected LinkedList<IEntityModel<?>>	entities, entitiesToAdd, entitiesToDelete; //Entities
	protected List<IParticle>			particles, particlesToAdd, particlesToDelete; //Particle
	protected List<IBullet>			activeBullets; //The current bullets
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
	private boolean aiDisabled;

	private ArrayList<Runnable> runAfterNextTick = new ArrayList<Runnable>();

	public Game(Difficulty diff, boolean singlePlayer, boolean cheatMode) {
		this.diff = diff; //Set difficulty
		isCheatMode = cheatMode; //Set cheat mode
		isPaused = false; //Set not paused
		isSinglePlayer = singlePlayer; //Set singleplayer

		world = new World(new Vector2(0, 0), true); //Create world
		contactListener = new GameContactListener(); //Create contact listener

		world.setContactListener(contactListener); //Set the contact listener
		entities = new LinkedList<IEntityModel<?>>(); //Create entities array
		entitiesToAdd = new LinkedList<IEntityModel<?>>();
		entitiesToDelete = new LinkedList<IEntityModel<?>>();
		particles = new ArrayList<IParticle>(); //Create particles array
		particlesToAdd = new ArrayList<IParticle>();
		particlesToDelete = new ArrayList<IParticle>();
		activeBullets = new ArrayList<IBullet>(); //Create bullets array
		players = new ArrayList<PlayerModel>(); //Create player array
		rand = new Random(); //Create random

		timeUntilNextSpawn = 10; //10 seconds until first zombie spawns

		//5.5^(10/7) = 11.4198654
		//Constant = 10.9198654 - 2 * waveModifier
		moduloConstant = Math.pow(5.5, 8.7 / 7) - diff.getZombieWaveModifier();

		//Set max hostiles
		maxHostiles = diff.getMaxHostiles();

		world.setContactListener(contactListener);

		Onslaught.instance.pluginManager.getPlugins().forEach((m)->m.getPlugins().forEach((p)->p.onGameStart(this)));
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
				{
					ListIterator<IEntityModel<?>> iter = entities.listIterator();
					if (!world.isLocked()) {
						while(iter.hasNext()) {
							IEntityModel<?> model = iter.next();
							model.tick(Constants.TIME_STEP);
							if (model.shouldBeDeleted()) {
								world.destroyBody(model.getEntity().getBody());
								iter.remove();
								if (model.isHostile()) {
									hostiles--;
								}
								model.dispose();
							}
						}
						entities.addAll(entitiesToAdd);
						entitiesToAdd.clear();
					}
				}
				{
					ListIterator<IParticle> iter = particles.listIterator();
					if (!world.isLocked()) {
						while(iter.hasNext()) {
							IParticle part = iter.next();
							part.tick(Constants.TIME_STEP);
							if (part.shouldBeDeleted()) {
								iter.remove();
								part.dispose();
							}
						}
						particles.addAll(particlesToAdd);
						particlesToAdd.clear();
					}
				}

				//entities.forEach((e) -> e.tick(Constants.TIME_STEP));
				//particles.forEach((p) -> p.tick(Constants.TIME_STEP));

				//Step the world
				world.step(Constants.TIME_STEP, Constants.VELOCITY_ITERATIONS, Constants.POSITION_ITERATIONS);
				//Decrement the accumulator
				accumulator -= Constants.TIME_STEP;

				/*
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
				}*/
				if (!world.isLocked()) { // Given runnables
					ArrayList<Runnable> runCopy = new ArrayList<Runnable>(runAfterNextTick);
					runCopy.forEach((r)->{
						r.run();
					});
					runAfterNextTick.removeAll(runCopy);
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

	public List<IEntityModel<?>> getEntities() {
		return entities;
	}/*

	public void removeEntity(IEntityModel<?> e) {
		if (!entitiesToDelete.contains(e)) {
			entitiesToDelete.add(e);
		}
	}

	public void removeEntity(IEntity entity) {
		getEntityModelFromEntity(entity).ifPresent((m) -> removeEntity(m));
	}*/

	public List<IParticle> getParticles() {
		return particles;
	}

	public List<IParticle> getParticlesToAdd() {
		return particlesToAdd;
	}
	/*
	public void removeParticle(IParticle p) {
		if (!particlesToDelete.contains(p)) {
			particlesToDelete.add(p);
		}
	}*/

	public List<IBullet> getActiveBullets() {
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
			Onslaught.error("Explosion Strength value of <= 0 attempted.");
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

	public void onPlayerDeath(PlayerModel playerModel) {
		players.remove(playerModel);
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
	public void saveToFile(String saveName) {
		//File reference
		FileHandle file = Onslaught.instance.settingsFile.parent().child("saves/" + saveName + ".save");
		
		BufferedWriter writer = new BufferedWriter(file.writer(false));

		Onslaught.instance.gson.toJson(this, Game.class, new JsonWriter(writer));
		
		try {
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Load the game from a file
	 * 
	 * @param saveName the file name
	 * @return the loaded game
	 */
	public static GameLoadedContainer loadFromFile(String saveName) {
		//Get file
		FileHandle file = Onslaught.instance.settingsFile.parent().child("saves/" + saveName + ".save");

		Game game = Onslaught.instance.gson.fromJson(file.reader(), Game.class);
		GameLoadedContainer glc = new GameLoadedContainer();

		glc.errors = GameSerializor.errors;
		glc.canBeLoaded = GameSerializor.canLoad;
		glc.game = game;

		//If failed return nothing
		return glc;
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

	public boolean isAIDisabled() {
		return aiDisabled;
	}

	public void setAiDisabled(boolean aiDisabled) {
		this.aiDisabled = aiDisabled;
	}

	public void runAfterNextTick(Runnable run) {
		runAfterNextTick.add(run);
	}

	public double getTimeUntilNextSpawn() {
		return timeUntilNextSpawn;
	}

	public static class GameSerializor implements JsonDeserializer<Game>, JsonSerializer<Game> {

		public static HashMap<String, List<String>> errors = new HashMap<String, List<String>>();
		public static boolean canLoad;

		@Override
		public JsonElement serialize(Game src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject obj = new JsonObject();

			{
				JsonArray arr = new JsonArray();
				for (IEntityModel<?> e : src.getEntities()) {
					JsonElement ele = context.serialize(e);

					if (ele == null) continue;

					JsonObject container = new JsonObject();

					container.addProperty("type", e.getClass().getName());
					container.add("data", ele);

					arr.add(container);
				}
				obj.add("entities", arr);
			}

			{
				JsonArray arr = new JsonArray();
				for (IParticle e : src.getParticles()) {
					JsonElement ele = context.serialize(e);

					if (ele == null) continue;

					JsonObject container = new JsonObject();

					container.addProperty("type", e.getClass().getName());
					container.add("data", ele);

					arr.add(container);
				}
				obj.add("particles", arr);
			}

			obj.addProperty("difficulty", src.diff.getUniqueID());
			obj.addProperty("cheatMode", src.isCheatMode);
			obj.addProperty("singlePlayer", src.isSinglePlayer);
			obj.addProperty("paused", src.isPaused);
			obj.addProperty("timeUntilSpawn", src.timeUntilNextSpawn);
			obj.addProperty("mobsSpawned", src.mobsSpawned);

			return obj;
		}

		@Override
		public Game deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject obj = json.getAsJsonObject();

			Optional<Difficulty> diff = GameRegistry.getDifficultyFromID(obj.get("difficulty").getAsString());
			boolean singleplayer = obj.get("singlePlayer").getAsBoolean();
			boolean cheat = obj.get("cheatMode").getAsBoolean();

			canLoad = true;

			Consumer3<String, String, Boolean> addErrorConsumer = (header, output, allowsLoading) -> {
				if (!errors.containsKey(header)) {
					errors.put(header, new ArrayList<String>());
				}
				errors.get(header).add(output);
				if (!allowsLoading) {
					canLoad = false;
				}
			};

			if (!diff.isPresent()) {
				addErrorConsumer.run("Difficulty not found:", obj.get("difficulty").getAsString(), false);
			}

			Game game = new Game(diff.orElse(BasicDifficulty.ERROR_DIFFICULTY), singleplayer, cheat);

			boolean paused = obj.get("paused").getAsBoolean();
			double timeUntilSpawn = obj.get("timeUntilSpawn").getAsDouble();
			int mobsSpawned = obj.get("mobsSpawned").getAsInt();

			game.setPaused(paused);
			game.setTimeUntilNextSpawn(timeUntilSpawn);
			game.setMobsSpawned(mobsSpawned);

			{
				JsonArray arr = obj.getAsJsonArray("entities");
				for (JsonElement ele : arr) {
					JsonObject ob = ele.getAsJsonObject();
					try {
						Class<?> clazz = Class.forName(ob.get("type").getAsString());
						IEntityModel<?> model = context.deserialize(ob.get("data"), clazz);
						model.doPostDeserialize(game);
					} catch (ClassNotFoundException e) {
						addErrorConsumer.run("Entity class not found:", ob.get("type").getAsString(), true);
						e.printStackTrace();
					}
				}
			}

			{
				JsonArray arr = obj.getAsJsonArray("particles");
				for (JsonElement ele : arr) {
					JsonObject ob = ele.getAsJsonObject();
					try {
						Class<?> clazz = Class.forName(ob.get("type").getAsString());
						IParticle part = context.deserialize(ob.get("data"), clazz);
						part.doPostDeserialize(game);
					} catch (ClassNotFoundException e) {
						addErrorConsumer.run("Particle class not found:", ob.get("type").getAsString(), true);
						e.printStackTrace();
					}
				}
			}

			return game;
		}
	}
}
