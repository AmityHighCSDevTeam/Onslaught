package org.amityregion5.ZombieGame.common.entity;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Shape;

public interface IEntity {
	public void setShape(Shape e);
	public Shape getShape();
	
	public void setBody(Body b);
	public Body getBody();
	
	public float getSpeed();
	public void setSpeed(float f);
	
	public float getFriction();
	public void setFriction(float f);
}
