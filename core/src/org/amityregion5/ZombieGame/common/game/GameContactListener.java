package org.amityregion5.ZombieGame.common.game;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class GameContactListener implements ContactListener {

	private ArrayList<Consumer<Contact>>					beginContactListeners	= new ArrayList<Consumer<Contact>>();
	private ArrayList<Consumer<Contact>>					endContactListeners		= new ArrayList<Consumer<Contact>>();
	private ArrayList<BiConsumer<Contact, Manifold>>		preSolveListeners		= new ArrayList<BiConsumer<Contact, Manifold>>();
	private ArrayList<BiConsumer<Contact, ContactImpulse>>	postSolveListeners		= new ArrayList<BiConsumer<Contact, ContactImpulse>>();

	@Override
	public void beginContact(Contact contact) {
		beginContactListeners.forEach((c) -> c.accept(contact));
	}

	@Override
	public void endContact(Contact contact) {
		endContactListeners.forEach((c) -> c.accept(contact));
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		preSolveListeners.forEach((c) -> c.accept(contact, oldManifold));
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		postSolveListeners.forEach((c) -> c.accept(contact, impulse));
	}

	public void addBeginContactListener(Consumer<Contact> listener) {
		beginContactListeners.add(listener);
	}

	public void addEndContactListener(Consumer<Contact> listener) {
		endContactListeners.add(listener);
	}

	public void addPreSolveListener(BiConsumer<Contact, Manifold> listener) {
		preSolveListeners.add(listener);
	}

	public void addPostSolveListener(BiConsumer<Contact, ContactImpulse> listener) {
		postSolveListeners.add(listener);
	}
}
