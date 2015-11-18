package org.amityregion5.ZombieGame.common.weapon.data;

import org.amityregion5.ZombieGame.common.game.buffs.Buff;

public interface IWeaponDataBase {

	public double getPrice();

	public String getIconTextureString();

	public String getGameTextureString();

	public double getGameTextureScale();

	public double getGameTextureOffsetX();

	public double getGameTextureOffsetY();

	public int getGameTextureOriginX();

	public int getGameTextureOriginY();

	public Buff getBuff();
}
