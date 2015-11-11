package org.amityregion5.ZombieGame.common.game;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import java.util.Random;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.client.asset.SoundPlayingData;
import org.amityregion5.ZombieGame.common.Constants;
import org.amityregion5.ZombieGame.common.bullet.ExplosionRaycastBullet;
import org.amityregion5.ZombieGame.common.bullet.IBullet;
import org.amityregion5.ZombieGame.common.entity.IEntity;
import org.amityregion5.ZombieGame.common.game.model.IEntityModel;
import org.amityregion5.ZombieGame.common.game.model.IParticle;
import org.amityregion5.ZombieGame.common.game.model.entity.PlayerModel;
import org.amityregion5.ZombieGame.common.game.model.entity.RocketModel;
import org.amityregion5.ZombieGame.common.game.model.particle.ExplosionParticleModel;
import org.amityregion5.ZombieGame.common.helper.VectorFactory;
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

import box2dLight.PointLight;
import box2dLight.RayHandler;

public class Game implements Disposable {
	
	//Static Variables
	private static final float bigSpawnRad = 25;
	private static final float smallSpnRad = 12.5f;
	private static final float explosionMinVal = 0.05f;
	private static final int explosionRaycasts = 720;
	private static final float areaPerParticle = 0.1f;

	//Unsaved variables
	private GameContactListener contactListener;
	private World	world;
	private float	accumulator;
	private ArrayList<IEntityModel<?>>	entities, entitiesToAdd, entitiesToDelete;
	private ArrayList<IParticle>	particles, particlesToAdd, particlesToDelete;
	private ArrayList<IBullet>	activeBullets;
	private ArrayList<PlayerModel> players;
	private Random				rand;
	private RayHandler lighting;
	
	//Calculated Variables (Unsaved)
	private int maxHostiles = 0;
	private double moduloConstant;
	private int hostiles = 0;

	//Saved Variables
	private Difficulty diff;
	private boolean isCheatMode;
	private boolean isSinglePlayer;
	private boolean isPaused;
	private double timeUntilNextSpawn;
	private int mobsSpawned = 0;

	public Game(Difficulty diff, boolean singlePlayer, boolean cheatMode) {
		this.diff = diff;
		this.isCheatMode = cheatMode;
		isPaused = false;
		isSinglePlayer = singlePlayer;

		world = new World(new Vector2(0, 0), true);
		contactListener = new GameContactListener();

		contactListener.addBeginContactListener((c)->{
			if (c.getFixtureA().getBody() != null) {
				Optional<IEntityModel<?>> model = getEntityFromBody(c.getFixtureA().getBody());

				model.ifPresent((en)->{
					if (en instanceof RocketModel) {
						((RocketModel) en).onHit();
					}
				});
			}
			if (c.getFixtureB().getBody() != null) {
				Optional<IEntityModel<?>> model = getEntityFromBody(c.getFixtureB().getBody());

				model.ifPresent((en)->{
					if (en instanceof RocketModel) {
						((RocketModel) en).onHit();
					}
				});
			}
		});

		world.setContactListener(contactListener);
		entities = new ArrayList<IEntityModel<?>>();
		entitiesToAdd = new ArrayList<IEntityModel<?>>();
		entitiesToDelete = new ArrayList<IEntityModel<?>>();
		particles = new ArrayList<IParticle>();
		particlesToAdd = new ArrayList<IParticle>();
		particlesToDelete = new ArrayList<IParticle>();
		activeBullets = new ArrayList<IBullet>();
		players = new ArrayList<PlayerModel>();
		rand = new Random();

		timeUntilNextSpawn = 10;
		moduloConstant = Math.pow(5.5, 10.0/7) - 0.5 - 2*diff.getDifficultyMultiplier();
		maxHostiles = (int) (100 * diff.getDifficultyMultiplier());
		//timeBetWaves = 15 * (Difficulty.diffInvertNum - diff.getDifficultyMultiplier());
		//waveDifficulty = 3 * diff.getDifficultyMultiplier();
	}

	public void tick(float deltaTime) {
		float frameTime = Math.min(deltaTime, 0.25f);
		if (!isPaused()) {
			accumulator += frameTime;
			while (accumulator >= Constants.TIME_STEP) {

				entities.forEach((e)->e.tick(Constants.TIME_STEP));
				particles.forEach((p)->p.tick(Constants.TIME_STEP));

				world.step(Constants.TIME_STEP, Constants.VELOCITY_ITERATIONS,
						Constants.POSITION_ITERATIONS);
				accumulator -= Constants.TIME_STEP;

				{ //Deletion of Entities
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
							b.dispose();
						}
					}
				}
				{ //Deletion of Particles
					Iterator<IParticle> i = particlesToDelete.iterator();
					if(!world.isLocked()){
						while(i.hasNext()){
							IParticle p = i.next();
							particles.remove(p);
							i.remove();
							p.dispose();
						}
					}
				}
				{ //Addition of Entities
					Iterator<IEntityModel<?>> i = entitiesToAdd.iterator();
					if(!world.isLocked()){
						while(i.hasNext()){
							IEntityModel<?> b = i.next();
							entities.add(b);
							i.remove();
						}
					}
				}
				{ //Addition of Particles
					Iterator<IParticle> i = particlesToAdd.iterator();
					if(!world.isLocked()){
						while(i.hasNext()){
							IParticle p = i.next();
							particles.add(p);
							i.remove();
						}
					}
				}

				//Spawning of mobs
				if (hostiles < maxHostiles) {
					while (timeUntilNextSpawn <= 0 && hostiles < maxHostiles) {
						spawnNext();
						timeUntilNextSpawn += Math.min(Math.pow((Math.pow(mobsSpawned, 0.7))%(moduloConstant),6)/10000, 25);
						mobsSpawned++;
						//timeBetWaves -= Math.pow(2, (1.4 * timeBetWaves * diff.getDifficultyMultiplier())/60) - 1;
						//wavesSpawned++;
					}
					timeUntilNextSpawn -= frameTime;
				}
			}
		}
	}

	private void spawnNext() {
		for (PlayerModel player : players) {
			Vector2 pos = player.getEntity().getBody().getPosition();
			Vector2 spawnPos = null;
			boolean whilePara = true;
			do {
				final Vector2 v = new Vector2(pos.x - bigSpawnRad/2 + rand.nextFloat()*bigSpawnRad, pos.y - bigSpawnRad/2 + rand.nextFloat()*bigSpawnRad);
				spawnPos = v;
				whilePara = players.parallelStream().anyMatch((p)->p.getEntity().getBody().getPosition().dst2(v) < smallSpnRad * smallSpnRad);
			} while (whilePara);

			IEntityModel<?> eModel = getSpawningEntity();
			addEntityToWorld(eModel, spawnPos.x, spawnPos.y);
			if (eModel.isHostile()) {
				hostiles++;
			}
		}
	}

	private IEntityModel<?> getSpawningEntity() {
		return GameRegistry.getSpawn(this);
	}
	
	public World getWorld() {
		return world;
	}

	public void addParticleToWorld(IParticle particle) {
		particlesToAdd.add(particle);
	}

	public void addEntityToWorld(IEntityModel<?> entity, float x, float y) {
		addEntityToWorld(entity, x, y, (short)1, (short)1);
	}

	public void addEntityToWorld(IEntityModel<?> entity, float x, float y, short maskBits, short categoryBits) {
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
		fixtureDef.restitution = 0f; // Make it bounce a little bit
		fixtureDef.filter.categoryBits = categoryBits;
		fixtureDef.filter.maskBits = maskBits;

		// Create our fixture and attach it to the body
		/* Fixture fixture = */body.createFixture(fixtureDef);

		// Remember to dispose of any shapes after you're done with them!
		// BodyDef and FixtureDef don't need disposing, but shapes do.
		shape.dispose();

		if (entity instanceof PlayerModel) {
			players.add((PlayerModel)entity);
		}

		entitiesToAdd.add(entity);
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

	public void makeExplosion(Vector2 pos, double strength, PlayerModel source) {

		if (strength <= 0) {
			ZombieGame.error("Explosion Strength value of <= 0 attempted.");
			return;
		}

		double dist = (strength/explosionRaycasts)/explosionMinVal;

		double radPerRaycast = 2 * Math.PI / explosionRaycasts;
		for (int i=0; i<explosionRaycasts; i++) {
			double dir = radPerRaycast * i;

			Vector2 bullVector = VectorFactory.createVector((float)dist, (float)dir);

			ExplosionRaycastBullet bull = new ExplosionRaycastBullet(this, pos, (float)(strength/explosionRaycasts), bullVector, source);
			bull.setDir((float) dir);

			this.getActiveBullets().add(bull);
			this.getWorld().rayCast(bull, pos, bullVector.add(pos));
			bull.finishRaycast();
		}

		double area = Math.PI * dist * dist;

		int particles = (int) (area/areaPerParticle);

		dist/=2;

		for (int i = 0; i<particles; i++) {
			Vector2 pos2 = VectorFactory.createVector((float)(rand.nextDouble() * rand.nextDouble() * dist), (float)(rand.nextDouble()*Math.PI*2)).add(pos);

			ExplosionParticleModel explosionParticle = new ExplosionParticleModel(pos2.x, pos2.y ,new Color(1f, 1f, 0f, 1f), this,
					(float) (2*Math.PI*rand.nextDouble()), 100*(rand.nextFloat()-0.5f), 0.05f*pos2.dst2(pos), pos2.sub(pos).angleRad());

			explosionParticle.setLight(new PointLight(lighting, 50, explosionParticle.getColor(), 2, pos2.x, pos2.y));
			explosionParticle.getLight().setXray(true);

			addParticleToWorld(explosionParticle);
		}

		for (PlayerModel player : players) {
			float distToExplosion = pos.dst(player.getEntity().getBody().getWorldCenter());
			if (distToExplosion < 1) {
				distToExplosion = 1;
			}
			player.setScreenVibrate(player.getScreenVibrate() + strength/distToExplosion/4 );
		}

		playSound(new SoundData("Core/Audio/explode.wav", 1, strength), pos);
	}

	public GameContactListener getContactListener() {
		return contactListener;
	}

	public void playSound(SoundData sound, Vector2 position) {
		//ZombieGame.debug("Game: Playing Sound: " + sound.getAssetName());
		for (PlayerModel player : players) {
			SoundPlayingData playing = new SoundPlayingData(sound.getAssetName(), Math.min(sound.getMaxVolume()/player.getEntity().getBody().getWorldCenter().dst2(position),1), sound.getPitch());
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

	@SuppressWarnings("unchecked")
	public void saveToFile(String saveName) {
		FileHandle file = ZombieGame.instance.settingsFile.parent().child("saves/"+saveName+".save");
		
		JSONObject obj = new JSONObject();
		
		obj.put("name", saveName);

		JSONArray entitiesSaving = new JSONArray();

		for (IEntityModel<?> e : entities) {
			JSONObject o = e.convertToJSONObject();
			JSONObject container = new JSONObject();
			container.put("type", e.getClass().getName());
			container.put("data", o);
			entitiesSaving.add(container);
		}
		
		obj.put("entities", entitiesSaving);

		JSONArray particlesSaving = new JSONArray();

		for (IParticle e : particles) {
			JSONObject o = e.convertToJSONObject();
			JSONObject container = new JSONObject();
			container.put("type", e.getClass().getName());
			container.put("data", o);
			particlesSaving.add(container);
		}
		
		obj.put("particles", particlesSaving);
		
		obj.put("difficulty", diff.name());
		
		obj.put("cheatMode", isCheatMode);
		obj.put("singlePlayer", isSinglePlayer);
		obj.put("paused", isPaused);
		
		obj.put("timeUntilSpawn", timeUntilNextSpawn);
		
		obj.put("mobsSpawned", mobsSpawned);
		
		file.writeString(obj.toJSONString(), false);
	}
	
	public static Game loadFromFile(String saveName) {
		FileHandle file = ZombieGame.instance.settingsFile.parent().child("saves/"+saveName+".save");
		JSONParser parser = new JSONParser();
		
		Reader reader = file.reader();
		try {
			JSONObject obj = (JSONObject)parser.parse(reader);
			
			JSONArray entities = (JSONArray)obj.get("entities");
			JSONArray particles = (JSONArray)obj.get("particles");
			
			Difficulty diff = Difficulty.valueOf((String)obj.get("difficulty")); //√
			
			boolean cheatMode = (Boolean)obj.get("cheatMode"); //√
			boolean singlePlayer = (Boolean)obj.get("singlePlayer"); //√
			boolean paused = (Boolean)obj.get("paused"); //√
			
			double timeUntilSpawn = ((Number)obj.get("timeUntilSpawn")).doubleValue(); //√
			
			int mobsSpawned = ((Number)obj.get("mobsSpawned")).intValue(); //√
			
			Game game = new Game(diff, singlePlayer, cheatMode);
			game.setPaused(paused);
			game.setTimeUntilNextSpawn(timeUntilSpawn);
			game.setMobsSpawned(mobsSpawned);
			
			for (Object o : entities) {
				JSONObject container = (JSONObject)o;
				
				String className = (String) container.get("type");
				
				try {
					Class<?> clazz = Class.forName(className);
					JSONObject e = (JSONObject) container.get("data");
					
					Method method = clazz.getMethod("fromJSON", JSONObject.class, Game.class);
					@SuppressWarnings("unused")
					IEntityModel<?> model = (IEntityModel<?>) method.invoke(clazz.newInstance(), e, game);
				} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e1) {
					e1.printStackTrace();
				}
			}
			for (Object o : particles) {
				JSONObject container = (JSONObject)o;
				
				String className = (String) container.get("type");
				
				try {
					Class<?> clazz = Class.forName(className);
					JSONObject e = (JSONObject) container.get("data");
					
					Method method = clazz.getMethod("fromJSON", JSONObject.class, Game.class);
					@SuppressWarnings("unused")
					IParticle model = (IParticle) method.invoke(clazz.newInstance(), e, game);
				} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e1) {
					e1.printStackTrace();
				}
			}
			return game;
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
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
}
