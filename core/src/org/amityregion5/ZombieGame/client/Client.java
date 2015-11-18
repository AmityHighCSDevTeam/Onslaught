package org.amityregion5.ZombieGame.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;

public class Client {
	public static final InputMultiplexer inputMultiplexer = new InputMultiplexer();

	static {
		Gdx.input.setInputProcessor(inputMultiplexer);
	}
}
