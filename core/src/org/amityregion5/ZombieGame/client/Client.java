package org.amityregion5.ZombieGame.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;

/**
 * A class containing useful Client side items
 * @author sergeys
 *
 */
public class Client {
	//An input multiplexer
	public static final InputMultiplexer inputMultiplexer = new InputMultiplexer();

	static {
		Gdx.input.setInputProcessor(inputMultiplexer);
	}
}
