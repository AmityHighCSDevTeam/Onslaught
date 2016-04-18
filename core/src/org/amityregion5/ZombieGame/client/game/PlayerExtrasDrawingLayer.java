package org.amityregion5.ZombieGame.client.game;

import org.amityregion5.ZombieGame.client.asset.TextureRegistry;
import org.amityregion5.ZombieGame.common.game.model.IEntityModel;
import org.amityregion5.ZombieGame.common.game.model.IParticle;
import org.amityregion5.ZombieGame.common.game.model.entity.PlayerModel;
import org.amityregion5.ZombieGame.common.helper.VectorFactory;
import org.amityregion5.ZombieGame.common.weapon.WeaponStack;
import org.amityregion5.ZombieGame.common.weapon.types.NullWeapon;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * A drawing layer for the gun that the player is holding
 * @author sergeys
 *
 */
public class PlayerExtrasDrawingLayer implements IDrawingLayer {

	private PlayerModel player;
	private Sprite sprite;
	private String textureName = "--null--";

	public PlayerExtrasDrawingLayer(PlayerModel player) {
		this.player = player;
	}

	public PlayerModel getSprite() {
		return player;
	}

	@Override
	public void draw(IEntityModel<?> em, SpriteBatch batch, ShapeRenderer shapeRenderer, Rectangle cullRect) {
		//Get the weapon
		WeaponStack weapon = player.getCurrentWeapon();

		if (weapon == null || weapon.getWeapon() instanceof NullWeapon) { return; }
		
		if (sprite == null || !textureName.equals(TextureRegistry.getTextureNamesFor(weapon.getGameTextureName()).get(0))) {
			textureName = TextureRegistry.getTextureNamesFor(weapon.getGameTextureName()).get(0);
			sprite = TextureRegistry.getAtlas().createSprite(textureName);
			//Set the origin
			sprite.setOrigin(weapon.getWeaponDataBase().getGameTextureOriginX(), weapon.getWeaponDataBase().getGameTextureOriginY());
		}

		//Player position
		Vector2 playerPos = player.getEntity().getBody().getWorldCenter();

		//Rotation
		double rotation = player.getEntity().getBody().getAngle();

		//Move the position to the correctposition
		playerPos = playerPos.add(VectorFactory.createVector(0.15f + (float) weapon.getWeaponDataBase().getGameTextureOffsetY(), (float) (rotation)));
		playerPos = playerPos
				.add(VectorFactory.createVector((float) weapon.getWeaponDataBase().getGameTextureOffsetX(), (float) (rotation - Math.toRadians(90))));

		//Set the rotation
		sprite.setRotation((float) (Math.toDegrees(rotation) - 90));

		//Set the scale
		sprite.setScale((float) weapon.getWeaponDataBase().getGameTextureScale());
		//Set the center
		sprite.setCenter(playerPos.x, playerPos.y);

		//Draw the sprite
		sprite.draw(batch);
	}

	@Override
	public void draw(IParticle p, SpriteBatch batch, ShapeRenderer shapeRenderer, Rectangle cullRect) {}
}
