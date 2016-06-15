package org.amityregion5.onslaught.common.shop;

import java.util.Map;

import org.amityregion5.onslaught.common.game.model.entity.PlayerModel;

/**
 * An interface to define a purchaseable in the shop
 * 
 * @author sergeys
 *
 */
public interface IPurchaseable {
	//The name of the object
	public String getName();

	//The description of the object
	public String getDescription();

	//The descriptors for the current level
	public Map<String, String> getCurrentDescriptors(PlayerModel player);

	//The descriptors for the next level
	public Map<String, String> getNextDescriptors(PlayerModel player);

	//Is there a level beyond the current one for this player
	public boolean hasNextLevel(PlayerModel player);

	//Get the current level of this player
	public int getCurrentLevel(PlayerModel player);

	//Get the number of levels
	public int getNumLevels();

	//Get the price for this player
	public double getPrice(PlayerModel player);

	//Can this player purchase it
	public boolean canPurchase(PlayerModel player);

	//Called when the player purchases this
	public void onPurchase(PlayerModel player);

	//Does it have an icon
	public boolean hasIcon();

	//Get the icon name for this player
	public String getIconName(PlayerModel player);

	//Get the number that this purchaseable should be weighted with in searching using the sections as search terms
	public int numContained(String[] sections, PlayerModel player);
}
