package org.amityregion5.ZombieGame.common.weapon.data;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class PlaceableWeaponData implements IWeaponDataBase {
	private double	price, ammoPrice,preFireDelay, postFireDelay, reloadTime, maxRange, gameScale, gameOffX, gameOffY;
	private int		maxAmmo, gameOrgX, gameOrgY;
	private String iconTextureString, gameTextureString, placingObject;
	private List<SoundData> sounds;

	public PlaceableWeaponData(JSONObject o) {
		if (o.containsKey("price")) {
			price = ((Number) o.get("price")).doubleValue();
		}
		if (o.containsKey("ammoPrice")) {
			ammoPrice = ((Number) o.get("ammoPrice")).doubleValue();
		}
		if (o.containsKey("maxAmmo")) {
			maxAmmo = ((Number) o.get("maxAmmo")).intValue();
		}
		if (o.containsKey("reloadTime")) {
			reloadTime = ((Number) o.get("reloadTime")).doubleValue();
		}
		if (o.containsKey("preFireDelay")) {
			preFireDelay = ((Number) o.get("preFireDelay")).doubleValue();
		}
		if (o.containsKey("postFireDelay")) {
			postFireDelay = ((Number) o.get("postFireDelay")).doubleValue();
		}
		if (o.containsKey("gameScale")) {
			gameScale = ((Number) o.get("gameScale")).doubleValue();
		} else {
			gameScale = 1;
		}
		if (o.containsKey("gameOffX")) {
			gameOffX = ((Number) o.get("gameOffX")).doubleValue();
		}
		if (o.containsKey("gameOffY")) {
			gameOffY = ((Number) o.get("gameOffY")).doubleValue();
		}
		if (o.containsKey("gameOriginX")) {
			gameOrgX = ((Number) o.get("gameOriginX")).intValue();
		}
		if (o.containsKey("gameOriginY")) {
			gameOrgY = ((Number) o.get("gameOriginY")).intValue();
		}
		if (o.containsKey("maxRange")) {
			maxRange = ((Number) o.get("maxRange")).doubleValue();
		}
		if (o.containsKey("object")) {
			placingObject = ((String) o.get("object"));
		} else {
			placingObject = "";
		}
		if (o.containsKey("iconTxtr")) {
			iconTextureString = ((String) o.get("iconTxtr"));
		} else {
			iconTextureString = "";
		}
		if (o.containsKey("gameTxtr")) {
			gameTextureString = ((String) o.get("gameTxtr"));
		} else {
			gameTextureString = "";
		}
		sounds = new ArrayList<SoundData>();
		if (o.containsKey("sounds")) {
			JSONArray arr = (JSONArray)o.get("sounds");
			
			for (Object obj : arr) {
				JSONObject soundJSON = (JSONObject)obj;
				SoundData sound = SoundData.getSoundData(soundJSON);
				if (sound != null) {
					sounds.add(sound);
				}
			}
		}

	}

	/**
	 * @return the price
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(double price) {
		this.price = price;
	}

	/**
	 * @return the ammoPrice
	 */
	public double getAmmoPrice() {
		return ammoPrice;
	}

	/**
	 * @param ammoPrice the ammoPrice to set
	 */
	public void setAmmoPrice(double ammoPrice) {
		this.ammoPrice = ammoPrice;
	}

	/**
	 * @return the preFireDelay
	 */
	public double getPreFireDelay() {
		return preFireDelay;
	}

	/**
	 * @param preFireDelay the preFireDelay to set
	 */
	public void setPreFireDelay(double preFireDelay) {
		this.preFireDelay = preFireDelay;
	}

	/**
	 * @return the postFireDelay
	 */
	public double getPostFireDelay() {
		return postFireDelay;
	}

	/**
	 * @param postFireDelay the postFireDelay to set
	 */
	public void setPostFireDelay(double postFireDelay) {
		this.postFireDelay = postFireDelay;
	}

	/**
	 * @return the reloadTime
	 */
	public double getReloadTime() {
		return reloadTime;
	}

	/**
	 * @param reloadTime the reloadTime to set
	 */
	public void setReloadTime(double reloadTime) {
		this.reloadTime = reloadTime;
	}

	/**
	 * @return the maxRange
	 */
	public double getMaxRange() {
		return maxRange;
	}

	/**
	 * @param maxRange the maxRange to set
	 */
	public void setMaxRange(double maxRange) {
		this.maxRange = maxRange;
	}

	/**
	 * @return the maxAmmo
	 */
	public int getMaxAmmo() {
		return maxAmmo;
	}

	/**
	 * @param maxAmmo the maxAmmo to set
	 */
	public void setMaxAmmo(int maxAmmo) {
		this.maxAmmo = maxAmmo;
	}

	/**
	 * @return the iconTextureString
	 */
	public String getIconTextureString() {
		return iconTextureString;
	}

	/**
	 * @param iconTextureString the iconTextureString to set
	 */
	public void setIconTextureString(String iconTextureString) {
		this.iconTextureString = iconTextureString;
	}

	/**
	 * @return the gameTextureString
	 */
	public String getGameTextureString() {
		return gameTextureString;
	}

	/**
	 * @param gameTextureString the gameTextureString to set
	 */
	public void setGameTextureString(String gameTextureString) {
		this.gameTextureString = gameTextureString;
	}

	/**
	 * @return the placingObject
	 */
	public String getPlacingObject() {
		return placingObject;
	}

	/**
	 * @param placingObject the placingObject to set
	 */
	public void setPlacingObject(String placingObject) {
		this.placingObject = placingObject;
	}

	@Override
	public double getGameTextureScale() {
		return gameScale;
	}

	@Override
	public double getGameTextureOffsetX() {
		return gameOffX;
	}

	@Override
	public double getGameTextureOffsetY() {
		return gameOffY;
	}

	@Override
	public int getGameTextureOriginX() {
		return gameOrgX;
	}

	@Override
	public int getGameTextureOriginY() {
		return gameOrgY;
	}

	/**
	 * @return the sounds
	 */
	public List<SoundData> getSounds() {
		return sounds;
	}

	/**
	 * @param sounds the sounds to set
	 */
	public void setSounds(List<SoundData> sounds) {
		this.sounds = sounds;
	}
	
}
