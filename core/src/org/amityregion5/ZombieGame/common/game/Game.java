package org.amityregion5.ZombieGame.common.game;

import java.util.ArrayList;

import org.amityregion5.ZombieGame.client.Constants;
import org.amityregion5.ZombieGame.common.entity.IEntity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

public class Game implements Disposable{
	
	private World world;
	private float accumulator;
	private ArrayList<IEntity> entities;

	public Game() {
		world = new World(new Vector2(0,0), true);
		entities = new ArrayList<IEntity>();
	}
	
	public void tick(float deltaTime) {
		
		for (IEntity e : entities) {
			e.getBody().applyForceToCenter(new Vector2(e.getBody().getLinearVelocity()).scl(e.getFriction()), false);
			//e.getBody().applyForceToCenter(e.getBody().getLinearVelocity() * e.getFriction(), true);
		}
		
	    float frameTime = Math.min(deltaTime, 0.25f);
	    accumulator += frameTime;
	    while (accumulator >= Constants.TIME_STEP) {
	        world.step(Constants.TIME_STEP, Constants.VELOCITY_ITERATIONS, Constants.POSITION_ITERATIONS);
	        accumulator -= Constants.TIME_STEP;
	    }
	}
	
	public World getWorld() {
		return world;
	}
	
	public void addEntityToWorld(IEntity entity) {
		Body body = entity.getBody();
		
		if (body == null) {		
			// First we create a body definition
			BodyDef bodyDef = new BodyDef();
			// We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
			bodyDef.type = BodyType.DynamicBody;
			
			// Set our body's starting position in the world
			bodyDef.position.set(5, 5);

			// Create our body in the world using our body definition
			body = world.createBody(bodyDef);
			
			entity.setBody(body);
		}

		// Create a circle shape and set its radius to 6
		Shape shape = entity.getShape();

		// Create a fixture definition to apply our shape to
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 0.5f; 
		fixtureDef.friction = 0.5f;
		fixtureDef.restitution = 0.6f; // Make it bounce a little bit

		// Create our fixture and attach it to the body
		Fixture fixture = body.createFixture(fixtureDef);

		// Remember to dispose of any shapes after you're done with them!
		// BodyDef and FixtureDef don't need disposing, but shapes do.
		shape.dispose();
		
		entities.add(entity);
	}

	@Override
	public void dispose() {
		world.dispose();
	}
}
