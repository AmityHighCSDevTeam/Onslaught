package org.amityregion5.ZombieGame.common.game;

import java.util.ArrayList;
import java.util.function.Function;

import org.amityregion5.ZombieGame.common.game.model.IEntityModel;

public class GameRegistry {
	public static ArrayList<ArrayList<SpawningStorage>> spawning = new ArrayList<ArrayList<SpawningStorage>>();
	
	public static ArrayList<Difficulty> difficulties = new ArrayList<Difficulty>();
	
	static {
		spawning.add(new ArrayList<SpawningStorage>());
	}
	
	public static void addSpawnable(int level, double chance, Function<Game, IEntityModel<?>> func) {
		spawning.ensureCapacity(level+1);
		SpawningStorage ss = new SpawningStorage();
		ss.chance = chance;
		ss.func = func;
		
		if (spawning.get(level) == null) {
			spawning.set(level, new ArrayList<SpawningStorage>());
		}
		
		spawning.get(level).add(ss);
	}
	
	public static IEntityModel<?> getSpawn(Game game) {
		for (int level = spawning.size()-1; level >= 0; level--) {
			ArrayList<SpawningStorage> store = spawning.get(level);
			for (SpawningStorage ss : store) {
				if (ss.chance > game.getRandom().nextDouble()) {
					IEntityModel<?> entity = ss.func.apply(game);
					if (entity != null) {
						return entity;
					}
				}
			}
		}
		return null;
	}
	
	private static class SpawningStorage{
		protected Function<Game, IEntityModel<?>> func;
		protected double chance;
	}
	
	public static Difficulty getDifficultyFromID(String id) {
		return difficulties.parallelStream().filter((d)->d.getUniqueID().equals(id)).findFirst().get();
	}
}
