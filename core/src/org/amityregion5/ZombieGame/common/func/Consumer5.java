package org.amityregion5.ZombieGame.common.func;

/**
 * A 5 input consumer
 * 
 * @author sergeys
 *
 * @param <A> input 1
 * @param <B> input 2
 * @param <C> input 3
 * @param <D> input 4
 * @param <E> input 5
 */
@FunctionalInterface()
public interface Consumer5<A, B, C, D, E> {
	public void run(A a, B b, C c, D d, E e);
}
