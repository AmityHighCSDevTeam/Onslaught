package org.amityregion5.ZombieGame.common.buff;

import org.amityregion5.ZombieGame.common.game.buffs.Buff;

public class BuyableBuffContainer {
	public Buff buff;
	public double price;
	
	/**
	 * @param buff
	 * @param price
	 */
	public BuyableBuffContainer(Buff buff, double price) {
		this.buff = buff;
		this.price = price;
	}
}
