package org.amityregion5.ZombieGame.common.plugin;

public interface IPlugin {
	public void init();
	public void load();
	public void postLoad();
	
	public void cleanup();
	public void deactivate();
}
