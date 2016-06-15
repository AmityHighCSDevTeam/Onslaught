package org.amityregion5.onslaught.common.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.amityregion5.onslaught.common.buff.BuffApplicator;
import org.amityregion5.onslaught.common.shop.BuffPurchaseable;
import org.amityregion5.onslaught.common.shop.GunPurchaseable;
import org.amityregion5.onslaught.common.shop.IPurchaseable;
import org.amityregion5.onslaught.common.weapon.types.IWeapon;

/**
 * A container for a plugin
 * @author sergeys
 *
 */
public class PluginContainer {
	//The arraylist of weapons
	private ArrayList<IWeapon>					weapons			= new ArrayList<IWeapon>();
	//The arraylist of weapon classes
	private ArrayList<Class<? extends IWeapon>>	weaponClasses	= new ArrayList<Class<? extends IWeapon>>();

	//The array list of buff applicators
	private ArrayList<BuffApplicator> buffApplicators = new ArrayList<BuffApplicator>();

	private String			name; //The name of the plugin
	private String			desc; //The description of the plugin
	private String			pluginFolderLoc; //The plugin's folder location
	private List<IPlugin>	plugins; //All Plugin classes

	public PluginContainer() {}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}

	public void addWeapon(IWeapon weapon) {
		weapons.add(weapon);
	}

	public void addWeaponClass(Class<? extends IWeapon> weaponClass) {
		weaponClasses.add(weaponClass);
	}

	public boolean isActive() {
		return true;
	}

	public ArrayList<IWeapon> getWeapons() {
		return weapons;
	}

	public ArrayList<Class<? extends IWeapon>> getWeaponClasses() {
		return weaponClasses;
	}

	public String getPluginFolderLoc() {
		return pluginFolderLoc;
	}

	public void setPluginFolderLoc(String pluginFolderLoc) {
		this.pluginFolderLoc = pluginFolderLoc;
	}

	public void addBuffApplicator(BuffApplicator applicator) {
		buffApplicators.add(applicator);
	}

	public ArrayList<BuffApplicator> getBuffApplicators() {
		return buffApplicators;
	}

	public List<IPurchaseable> getPurchaseables() {
		//Combine weapons and buffs into a single stream of purchaseables before turning it into a list
		return Stream.concat(weapons.stream().map((w) -> new GunPurchaseable(w)), buffApplicators.stream().map((b) -> new BuffPurchaseable(b))).map((o) -> {
			return o;
		}).collect(Collectors.toList());
	}

	public List<IPlugin> getPlugins() {
		return plugins;
	}

	public void setPlugins(List<IPlugin> plugins) {
		this.plugins = plugins;
	}
}
