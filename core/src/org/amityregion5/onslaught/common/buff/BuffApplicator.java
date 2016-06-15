package org.amityregion5.onslaught.common.buff;

import org.amityregion5.onslaught.common.game.buffs.Buff;

/**
 * A class representing a buff, name, icon, and price
 * @author sergeys
 *
 */
public class BuffApplicator {
	private Buff	buff; //The buff
	private String	name, iconLoc; //The name and icon path
	private double	price; //The price

	public BuffApplicator(Buff buff, String name, double price, String icon) {
		this.buff = buff;
		iconLoc = icon;
		this.name = name; //Set values
		this.price = price;
	}

	public Buff getBuff() {
		return buff;
	}

	public double getPrice() {
		return price;
	}

	public String getName() {
		return name;
	}

	public String getIconLoc() {
		return iconLoc;
	}
}
