package org.amityregion5.ZombieGame.common.game;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;

import org.amityregion5.ZombieGame.common.game.difficulty.Difficulty;
import org.amityregion5.ZombieGame.common.game.model.IEntityModel;

/**
 * A registry for various objects used for the Game
 * 
 * @author sergeys
 *
 */
public class GameRegistry {
	//An arraylist of spawning storages
	public static ArrayList<ArrayList<SpawningStorage>> spawning = new ArrayList<ArrayList<SpawningStorage>>();

	//The difficulties used by the game
	public static ArrayList<Difficulty> difficulties = new ArrayList<Difficulty>();

	static {
		//Create the default spawning array
		spawning.add(new ArrayList<SpawningStorage>());
	}

	/**
	 * Add a spawning function
	 * 
	 * @param level the level to add it at. Higher levels will be run through before lower levels
	 * @param chance the chance [0,1] for this function to be selected
	 * @param func The function to create the entity model.
	 */
	public static void addSpawnable(int level, double chance, Function<Game, IEntityModel<?>> func) {
		//Make sure that the arraylist is big enough
		spawning.ensureCapacity(level + 1);
		//Create the storage object
		SpawningStorage ss = new SpawningStorage();
		ss.chance = chance; //Set chance 
		ss.func = func; //Set function

		//If there is no arraylist at the desired level
		if (spawning.get(level) == null) {
			//Create an arraylist for the desired level
			spawning.set(level, new ArrayList<SpawningStorage>());
		}

		//Add the storage to the thing
		spawning.get(level).add(ss);
	}

	/**
	 * Get the next spawning entity
	 * 
	 * @param game the game to get the entity for
	 * @return the entity to spawn
	 */
	public static IEntityModel<?> getSpawn(Game game) {
		//Loop through all of the levels
		for (int level = spawning.size() - 1; level >= 0; level--) {
			//The arraylist for this level
			ArrayList<SpawningStorage> store = spawning.get(level);
			//If the storage doesnt exist go to the next level
			if (store == null) {
				continue;
			}
			//Loop through all spawn storages in this level
			for (SpawningStorage ss : store) {
				//Check it against the random
				if (ss.chance > game.getRandom().nextDouble()) {
					//If suceeded then get the entity
					IEntityModel<?> entity = ss.func.apply(game);
					//If there is an entity return the entity
					if (entity != null) { return entity; }
					//If there is no entity continue
				}
			}
		}
		return null;
	}

	/**
	 * A class used for storing spawning information
	 * @author sergeys
	 *
	 */
	private static class SpawningStorage {
		//The function to get the entity model from
		protected Function<Game, IEntityModel<?>>	func;
		//The chance to spawn
		protected double							chance;
	}

	/**
	 * Get the game difficulty from its ID
	 * @param id the ID
	 * @return the difficulty with this ID
	 */
	public static Optional<Difficulty> getDifficultyFromID(String id) {
		//Loop through all difficulties and get the first one that has the correct ID
		return difficulties.parallelStream().filter((d) -> d.getUniqueID().equals(id)).findFirst();
	}
}
