package org.amityregion5.ZombieGame.common.func;

/**
 * A 3 input consumer
 * 
 * @author sergeys
 *
 * @param <A> input 1
 * @param <B> input 2
 * @param <C> input 3
 */
@FunctionalInterface()
public interface Function3<A, B, C, O> {
	public O apply(A a, B b, C c);
}
