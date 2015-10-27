package org.amityregion5.ZombieGame.common.shop;

import java.util.HashMap;
import java.util.Map;

import org.amityregion5.ZombieGame.common.buff.BuffApplicator;
import org.amityregion5.ZombieGame.common.game.buffs.Buff;
import org.amityregion5.ZombieGame.common.game.model.entity.PlayerModel;

public class BuffPurchaseable implements IPurchaseable {
	
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
		return "Price: " + buff.getPrice();
	}

	@Override
	public Map<String, String> getCurrentDescriptors(PlayerModel player) {
		Map<String, String> currMap = getActualCurrDesc(player);
		
		Buff emptyBuff = new Buff();
		
		for (String mBuff : buff.getBuff().getMultiplicative().keySet()) {
			currMap.put(mBuff, emptyBuff.getMult(mBuff)*100 + "%");
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
			map.put(mBuff, buff.getBuff().getMult(mBuff)*100 + "%");
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
		return false;
	}

	@Override
	public String getIconName(PlayerModel player) {
		return null;
	}
}
