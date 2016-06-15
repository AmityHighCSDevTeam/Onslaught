package org.amityregion5.onslaught.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;

/**
 * A class containing useful Client side items
 * @author sergeys
 *
 */
public class Client {
	//An input multiplexer
	public static final InputMultiplexer inputMultiplexer = new InputMultiplexer();
	
	public static final Color greenColor = new Color(27 / 255f, 168 / 255f, 55 / 255f, 1f);
	
	private static boolean wasMouseDown, wasMouseReleased;

	static {
		Gdx.input.setInputProcessor(inputMultiplexer);
	}
	
	public static void update() {
		wasMouseReleased = false;
		if (wasMouseDown && !Gdx.input.isTouched()) {
			wasMouseReleased = true;
		}
		wasMouseDown = Gdx.input.isTouched();
	}
	
	public static boolean mouseJustReleased() {
		return wasMouseReleased;
	}
}
