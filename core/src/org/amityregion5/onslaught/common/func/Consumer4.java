package org.amityregion5.onslaught.common.func;

/**
 * A 4 input consumer
 * 
 * @author sergeys
 *
 * @param <A> input 1
 * @param <B> input 2
 * @param <C> input 3
 * @param <D> input 4
 */
@FunctionalInterface()
public interface Consumer4<A, B, C, D> {
	public void run(A a, B b, C c, D d);
}
