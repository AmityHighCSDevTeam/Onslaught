package org.amityregion5.onslaught.common.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.amityregion5.onslaught.common.shop.IPurchaseable;
import org.amityregion5.onslaught.common.weapon.types.IWeapon;

/**
 * The plugin manager
 * 
 * @author sergeys
 *
 */
public class PluginManager {
	private ArrayList<PluginContainer>	plugins; //The list of all plugins
	private PluginContainer				core; //The core plugin

	public PluginManager() {
		//Create list of plugins
		plugins = new ArrayList<PluginContainer>();
	}

	/**
	 * Get a list of all purchaseables
	 * @return a List of all purchaseables
	 */
	public List<IPurchaseable> getPurchaseables() {
		//Loop through all plugins and combine their purchaseables into one big list
		return plugins.parallelStream().filter(PluginContainer::isActive).flatMap((p) -> p.getPurchaseables().stream()).collect(Collectors.toList());
	}

	/**
	 * Get a list of all weapons
	 * @return a List of all weapons
	 */
	public List<IWeapon> getActivatedWeapons() {
		//Loop through all plugins and combine their weapons into one big list
		return plugins.parallelStream().filter(PluginContainer::isActive).flatMap((p) -> p.getWeapons().stream()).collect(Collectors.toList());
	}

	/**
	 * Get a list of all weapon classes
	 * @return a List of all weapon classes
	 */
	public List<Class<? extends IWeapon>> getActivatedWeaponClasses() {
		//Loop through all plugins and combine their weapon classes into one big list
		return plugins.parallelStream().filter(PluginContainer::isActive).flatMap((p) -> p.getWeaponClasses().stream()).collect(Collectors.toList());
	}

	/**
	 * Add a plugin to the manager
	 * @param plugin the plugin to add
	 */
	public void addPlugin(PluginContainer plugin) {
		//If it is the core plugin
		if (plugin.getName().equals("Core") && core == null) {
			//Set it as the core plugin
			core = plugin;
		}
		//Add it to the list of plugins
		plugins.add(plugin);
	}

	/**
	 * Get all of the plugins
	 * @return an ArrayList containing all of the plugins
	 */
	public ArrayList<PluginContainer> getPlugins() {
		return plugins;
	}

	/**
	 * Get the core plugin
	 * @return the core plugin
	 */
	public PluginContainer getCorePlugin() {
		return core;
	}
}
