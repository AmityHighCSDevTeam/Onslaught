package org.amityregion5.ZombieGame.common.game;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * An implementation for a contact listener that allows multiple hooks
 * @author sergeys
 *
 */
public class GameContactListener implements ContactListener {

	//The arrays for listeners for each type of contact
	private ArrayList<Consumer<Contact>>					beginContactListeners	= new ArrayList<Consumer<Contact>>();
	private ArrayList<Consumer<Contact>>					endContactListeners		= new ArrayList<Consumer<Contact>>();
	private ArrayList<BiConsumer<Contact, Manifold>>		preSolveListeners		= new ArrayList<BiConsumer<Contact, Manifold>>();
	private ArrayList<BiConsumer<Contact, ContactImpulse>>	postSolveListeners		= new ArrayList<BiConsumer<Contact, ContactImpulse>>();

	@Override
	public void beginContact(Contact contact) {
		//Send to each begin contact listener
		beginContactListeners.forEach((c) -> c.accept(contact));
	}

	@Override
	public void endContact(Contact contact) {
		//Send to each end contact listener
		endContactListeners.forEach((c) -> c.accept(contact));
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		//Send to each pre solve listener
		preSolveListeners.forEach((c) -> c.accept(contact, oldManifold));
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		//Send to each post solve listener
		postSolveListeners.forEach((c) -> c.accept(contact, impulse));
	}

	/**
	 * Add a listener to receive begin contact notifications
	 * @param listener the listener to add
	 */
	public void addBeginContactListener(Consumer<Contact> listener) {
		beginContactListeners.add(listener);
	}

	/**
	 * Add a listener to receive end contact notifications
	 * @param listener the listener to add
	 */
	public void addEndContactListener(Consumer<Contact> listener) {
		endContactListeners.add(listener);
	}

	/**
	 * Add a listener to receive pre solve notifications
	 * @param listener the listener to add
	 */
	public void addPreSolveListener(BiConsumer<Contact, Manifold> listener) {
		preSolveListeners.add(listener);
	}

	/**
	 * Add a listener to receive post solve notifications
	 * @param listener the listener to add
	 */
	public void addPostSolveListener(BiConsumer<Contact, ContactImpulse> listener) {
		postSolveListeners.add(listener);
	}
}
