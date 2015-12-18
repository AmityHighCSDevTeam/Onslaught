package org.amityregion5.ZombieGame.common.shop;

import java.util.HashMap;
import java.util.Map;

import org.amityregion5.ZombieGame.common.buff.BuffApplicator;
import org.amityregion5.ZombieGame.common.game.buffs.Buff;
import org.amityregion5.ZombieGame.common.game.model.entity.PlayerModel;

/**
 * See Gun Purchaseable for comments (most are identical)
 * 
 * @author sergeys
 *
 */
public class BuffPurchaseable implements IPurchaseable {

	//The buff applicator
	private BuffApplicator buff;

	public BuffPurchaseable(BuffApplicator buff) {
		this.buff = buff;
	}

	@Override
	public String getName() {
		return buff.getName();
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public Map<String, String> getCurrentDescriptors(PlayerModel player) {
		Map<String, String> currMap = getActualCurrDesc(player);

		Buff emptyBuff = new Buff();

		for (String mBuff : buff.getBuff().getMultiplicative().keySet()) {
			currMap.put(mBuff, emptyBuff.getMult(mBuff) * 100 + "%");
		}

		for (String aBuff : buff.getBuff().getAdditive().keySet()) {
			currMap.put(aBuff, (emptyBuff.getAdd(aBuff) > 0 ? "+" : "") + emptyBuff.getAdd(aBuff));
		}

		return currMap;
	}

	private Map<String, String> getActualCurrDesc(PlayerModel player) {
		return new HashMap<String, String>();
	}

	@Override
	public Map<String, String> getNextDescriptors(PlayerModel player) {
		Map<String, String> map = new HashMap<String, String>();

		for (String mBuff : buff.getBuff().getMultiplicative().keySet()) {
			map.put(mBuff, buff.getBuff().getMult(mBuff) * 100 + "%");
		}

		for (String aBuff : buff.getBuff().getAdditive().keySet()) {
			map.put(aBuff, (buff.getBuff().getAdd(aBuff) > 0 ? "+" : "") + buff.getBuff().getAdd(aBuff));
		}

		return map;
	}

	@Override
	public boolean hasNextLevel(PlayerModel player) {
		return !player.hasBuff(buff.getBuff());
	}

	@Override
	public int getCurrentLevel(PlayerModel player) {
		return hasNextLevel(player) ? -1 : 0;
	}

	@Override
	public int getNumLevels() {
		return 0;
	}

	@Override
	public double getPrice(PlayerModel player) {
		if (!hasNextLevel(player)) { return Double.POSITIVE_INFINITY; }
		return buff.getPrice();
	}

	@Override
	public boolean canPurchase(PlayerModel player) {
		return hasNextLevel(player);
	}

	@Override
	public void onPurchase(PlayerModel player) {
		player.applyBuff(buff.getBuff());
	}

	@Override
	public boolean hasIcon() {
		return true;
	}

	@Override
	public String getIconName(PlayerModel player) {
		return buff.getIconLoc();
	}

	@Override
	public int numContained(String[] sections, PlayerModel player) {
		int num = 0;

		for (String s : sections) {
			if (s.equalsIgnoreCase("buff")) {
				num++;
			}
			if (getName().toLowerCase().contains(s.toLowerCase())) {
				num++;
			}
			if (getDescription().toLowerCase().contains(s.toLowerCase())) {
				num++;
			}
			num += getCurrentDescriptors(player).keySet().parallelStream().filter((k) -> k.toLowerCase().contains(s.toLowerCase())).count();
			num += getCurrentDescriptors(player).values().parallelStream().filter((k) -> k.toLowerCase().contains(s.toLowerCase())).count();
			if (hasNextLevel(player) && getNextDescriptors(player) != null) {
				num += getNextDescriptors(player).keySet().parallelStream().filter((k) -> k.toLowerCase().contains(s.toLowerCase())).count();
				num += getNextDescriptors(player).values().parallelStream().filter((k) -> k.toLowerCase().contains(s.toLowerCase())).count();
			}
		}

		return num;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BuffPurchaseable) { return buff == ((BuffPurchaseable) obj).buff; }
		return false;
	}
}
