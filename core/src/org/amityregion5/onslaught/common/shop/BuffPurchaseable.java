package org.amityregion5.onslaught.common.shop;

import java.util.HashMap;
import java.util.Map;

import org.amityregion5.onslaught.common.buff.Buff;
import org.amityregion5.onslaught.common.buff.BuffApplicator;
import org.amityregion5.onslaught.common.buff.BuyableBuffContainer;
import org.amityregion5.onslaught.common.game.model.entity.PlayerModel;

import com.google.gson.JsonPrimitive;

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
		
		BuyableBuffContainer cont = buff.getContainers()[Math.min(buff.getContainers().length-1, player.getData().getOrDefault(buff.getUID(), new JsonPrimitive(0)).getAsInt())];

		for (String mBuff : cont.buff.getMultiplicative().keySet()) {
			currMap.put(mBuff, emptyBuff.getMult(mBuff) * 100 + "%");
		}

		for (String aBuff : cont.buff.getAdditive().keySet()) {
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
		
		BuyableBuffContainer cont = buff.getContainers()[Math.min(buff.getContainers().length-1, player.getData().getOrDefault(buff.getUID(), new JsonPrimitive(0)).getAsInt())];

		for (String mBuff : cont.buff.getMultiplicative().keySet()) {
			map.put(mBuff, cont.buff.getMult(mBuff) * 100 + "%");
		}

		for (String aBuff : cont.buff.getAdditive().keySet()) {
			map.put(aBuff, (cont.buff.getAdd(aBuff) > 0 ? "+" : "") +cont.buff.getAdd(aBuff));
		}

		return map;
	}

	@Override
	public boolean hasNextLevel(PlayerModel player) {
		return buff.getContainers().length > player.getData().getOrDefault(buff.getUID(), new JsonPrimitive(0)).getAsInt();
	}

	@Override
	public int getCurrentLevel(PlayerModel player) {
		return hasNextLevel(player) ? -1 : 0;
	}

	@Override
	public int getNumLevels() {
		return buff.getContainers().length-1;
	}

	@Override
	public double getPrice(PlayerModel player) {
		if (!hasNextLevel(player)) { return Double.POSITIVE_INFINITY; }
		return buff.getContainers()[ player.getData().getOrDefault(buff.getUID(), new JsonPrimitive(0)).getAsInt()].price;
	}

	@Override
	public boolean canPurchase(PlayerModel player) {
		return hasNextLevel(player);
	}

	@Override
	public void onPurchase(PlayerModel player) {
		BuyableBuffContainer cont = buff.getContainers()[ player.getData().getOrDefault(buff.getUID(), new JsonPrimitive(0)).getAsInt()];
		player.getData().put(buff.getUID(),  new JsonPrimitive(player.getData().getOrDefault(buff.getUID(), new JsonPrimitive(0)).getAsInt() + 1));
		player.applyBuff(cont.buff);
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
