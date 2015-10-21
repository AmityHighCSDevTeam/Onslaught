package org.amityregion5.ZombieGame.client.game;

import org.amityregion5.ZombieGame.client.asset.TextureRegistry;
import org.amityregion5.ZombieGame.common.game.model.IEntityModel;
import org.amityregion5.ZombieGame.common.game.model.IParticle;
import org.amityregion5.ZombieGame.common.game.model.entity.PlayerModel;
import org.amityregion5.ZombieGame.common.helper.VectorFactory;
import org.amityregion5.ZombieGame.common.weapon.WeaponStack;
import org.amityregion5.ZombieGame.common.weapon.types.NullWeapon;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class PlayerExtrasDrawingLayer implements IDrawingLayer {
	
	private PlayerModel player;
	
	public PlayerExtrasDrawingLayer(PlayerModel player) {
		this.player = player;
	}
	
	public PlayerModel getSprite() {
		return player;
	}

	@Override
	public void draw(IEntityModel<?> em, SpriteBatch batch, ShapeRenderer shapeRenderer) {
		WeaponStack weapon = player.getCurrentWeapon();
		
		if (weapon == null || weapon.getWeapon() instanceof NullWeapon) {
			return;
		}
		
		Texture texture = TextureRegistry.getTexturesFor(weapon.getGameTextureName()).get(0);
		
		Sprite sprite = new Sprite(texture);
		
		sprite.setOrigin(weapon.getWeaponDataBase().getGameTextureOriginX(), weapon.getWeaponDataBase().getGameTextureOriginY());
		
		Vector2 playerPos = player.getEntity().getBody().getWorldCenter();
		
		double rotation = player.getEntity().getBody().getAngle();
		
		playerPos = playerPos.add(VectorFactory.createVector(0.15f + (float)weapon.getWeaponDataBase().getGameTextureOffsetY(), (float)(rotation)));
		playerPos = playerPos.add(VectorFactory.createVector((float)weapon.getWeaponDataBase().getGameTextureOffsetX(), (float)(rotation - Math.toRadians(90))));
		
		sprite.setRotation((float) (Math.toDegrees(rotation)-90));
		
		sprite.setScale((float) weapon.getWeaponDataBase().getGameTextureScale());
		sprite.setCenter(playerPos.x, playerPos.y);
		
		batch.begin();
		sprite.draw(batch);
		batch.end();
	}

	@Override
	public void draw(IParticle p, SpriteBatch batch, ShapeRenderer shapeRenderer) {}
}
