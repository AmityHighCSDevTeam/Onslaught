package org.amityregion5.ZombieGame.common.buff;

import org.amityregion5.ZombieGame.common.game.buffs.Buff;

public class BuffApplicator {
	private Buff buff;
	private String name;
	private double price;
	
	public BuffApplicator(Buff buff, String name, double price) {
		this.buff = buff;
		this.name = name;
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
}
