package org.amityregion5.ZombieGame.common.game;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

import org.amityregion5.ZombieGame.client.Constants;
import org.amityregion5.ZombieGame.common.bullet.IBullet;
import org.amityregion5.ZombieGame.common.entity.IEntity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

public class Game implements Disposable{
	
	private World world;
	private float accumulator;
	private ArrayList<IEntity> entities, entitiesToDelete;
	private ArrayList<IBullet> activeBullets;
	private Random rand;

	public Game() {
		world = new World(new Vector2(0,0), true);
		entities = new ArrayList<IEntity>();
		entitiesToDelete = new ArrayList<IEntity>();
		activeBullets = new ArrayList<IBullet>();
		rand = new Random();
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
				   //Body body = e.getBody();
				   
				   e.tick(deltaTime);
			}
			
	        world.step(Constants.TIME_STEP, Constants.VELOCITY_ITERATIONS, Constants.POSITION_ITERATIONS);
	        accumulator -= Constants.TIME_STEP;
	    }
	}
	
	public World getWorld() {
		return world;
	}
	
	public void addEntityToWorld(IEntity entity, float x, float y) {
		Body body = entity.getBody();
		
		if (body == null) {		
			// First we create a body definition
			BodyDef bodyDef = new BodyDef();
			// We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
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
		/*Fixture fixture =*/ body.createFixture(fixtureDef);

		// Remember to dispose of any shapes after you're done with them!
		// BodyDef and FixtureDef don't need disposing, but shapes do.
		shape.dispose();
		
		entities.add(entity);
	}
	
	public Optional<IEntity> getEntityFromBody(Body b) {
		return entities.parallelStream().filter(e -> {return e.getBody() == b;}).findFirst();
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
}
