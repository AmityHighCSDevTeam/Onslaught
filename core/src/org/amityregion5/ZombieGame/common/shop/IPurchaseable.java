package org.amityregion5.ZombieGame.common.shop;

import java.util.Map;

import org.amityregion5.ZombieGame.common.game.model.entity.PlayerModel;

public interface IPurchaseable {
	public String getName();

	public String getDescription();

	public Map<String, String> getCurrentDescriptors(PlayerModel player);

	public Map<String, String> getNextDescriptors(PlayerModel player);

	public boolean hasNextLevel(PlayerModel player);

	public int getCurrentLevel(PlayerModel player);

	public int getNumLevels();

	public double getPrice(PlayerModel player);

	public boolean canPurchase(PlayerModel player);

	public void onPurchase(PlayerModel player);

	public boolean hasIcon();

	public String getIconName(PlayerModel player);

	public int numContained(String[] sections, PlayerModel player);
}
