package org.amityregion5.ZombieGame.common.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.amityregion5.ZombieGame.common.shop.IPurchaseable;
import org.amityregion5.ZombieGame.common.weapon.types.IWeapon;

public class PluginManager {
	private ArrayList<PluginContainer>	plugins;
	private PluginContainer				core;

	public PluginManager() {
		plugins = new ArrayList<PluginContainer>();
	}

	public List<IPurchaseable> getPurchaseables() {
		return plugins.parallelStream().filter(PluginContainer::isActive).flatMap((p) -> p.getPurchaseables().stream()).collect(Collectors.toList());
	}

	public List<IWeapon> getActivatedWeapons() {
		return plugins.parallelStream().filter(PluginContainer::isActive).flatMap((p) -> p.getWeapons().stream()).collect(Collectors.toList());
	}
	/*
	 * public List<BuffApplicator> getBuffApplicators() { return plugins.parallelStream().filter(PluginContainer::isActive).flatMap((p)->p.getBuffApplicators().stream()).collect( Collectors.toList()); }
	 */

	public List<Class<? extends IWeapon>> getActivatedWeaponClasses() {
		return plugins.parallelStream().filter(PluginContainer::isActive).flatMap((p) -> p.getWeaponClasses().stream()).collect(Collectors.toList());
	}

	public void addPlugin(PluginContainer plugin) {
		if (plugin.getName().equals("Core") && core == null) {
			core = plugin;
		}
		plugins.add(plugin);
	}

	public ArrayList<PluginContainer> getPlugins() {
		return plugins;
	}

	public PluginContainer getCorePlugin() {
		return core;
	}
}
