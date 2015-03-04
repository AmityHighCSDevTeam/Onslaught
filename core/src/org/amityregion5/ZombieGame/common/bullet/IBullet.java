package org.amityregion5.ZombieGame.common.bullet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

public interface IBullet extends RayCastCallback{
	public void setDamage(float damage);
	public void setMass(float mass);
	public void setSpeed(float speed);
	public float getDamage();
	public float getMass();
	public float getSpeed();
	public void setDir(float dir);
	public float getDir();
	public void finishRaycast() ;
	public void setStart(Vector2 start);
	public Vector2 getStart();
	public Vector2 getEnd();
	public Color getColor();
}
