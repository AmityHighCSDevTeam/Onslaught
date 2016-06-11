package org.amityregion5.ZombieGame.common.buff;

import com.google.gson.annotations.SerializedName;

/**
 * A class representing a buff, name, icon, and price
 * @author sergeys
 *
 */
public class BuffApplicator {
	@SerializedName(value="levels")
	private BuyableBuffContainer[]	buffs; //The buffs
	
	private String	name;
	
	private String iconLoc;
	
	@SerializedName(value="uid")
	private String UID; //The name and icon path

	public BuffApplicator(BuyableBuffContainer[] containers, String name, String icon, String UID) {
		this.buffs = containers;
		iconLoc = icon;
		this.name = name; //Set values
		this.UID = UID;
	}

	public BuyableBuffContainer[] getContainers() {
		return buffs;
	}

	public String getName() {
		return name;
	}

	public String getIconLoc() {
		return iconLoc;
	}
	
	public String getUID() {
		return UID;
	}
}
