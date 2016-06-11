package org.amityregion5.ZombieGame.common.weapon.data;

import org.amityregion5.ZombieGame.common.buff.Buff;

/**
 * An iterface that defines a basic weapon data.
 * THIS SHOULD HAVE A CONSTRUCTOR THAT TAKES A JsonObject as a parameter
 * 
 * @author sergeys
 *
 */
public interface IWeaponDataBase {

	//Get the price of this weapon 
	public double getPrice();

	//Get the icon texture
	public String getIconTextureString();

	//Get the game texture
	public String getGameTextureString();

	//Get the game scaling factor in meters per pixel
	public double getGameTextureScale();

	//Get the texture offset (Player facing direction)
	public double getGameTextureOffsetY();

	//Get the texture offset (90 degrees right of player facing direction)
	public double getGameTextureOffsetX();

	//Get the texture's center in pixels
	public int getGameTextureOriginX();

	//Get the texture's center in pixels
	public int getGameTextureOriginY();

	//Get the buff that the weapon provides
	public Buff getBuff();
}
