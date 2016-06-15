package org.amityregion5.onslaught.common.func;

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
public interface Consumer3<A, B, C> {
	public void run(A a, B b, C c);
}
