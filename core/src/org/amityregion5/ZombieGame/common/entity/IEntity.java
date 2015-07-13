package org.amityregion5.ZombieGame.common.entity;

import java.util.Optional;

import org.amityregion5.ZombieGame.common.game.PlayerModel;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.MassData;
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

	public void tick(float delta);

	public MassData getMassData();

	public void damage(float damage, PlayerModel source);

	public Optional<Sprite> getSprite();
}
