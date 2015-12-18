package org.amityregion5.ZombieGame.common;

import java.io.IOException;
import java.io.OutputStream;

/**
 * An Output Stream that outputs to multiple sources simultaneously
 * @author sergeys
 *
 */
public class MultiOutputStream extends OutputStream {

	//Array of outputs
	private OutputStream[] streams;

	/**
	 * Constructor for creating the MultiOutputStream
	 * 
	 * @param streams the streams to ouput to
	 */
	public MultiOutputStream(OutputStream ... streams) {
		this.streams = streams;
	}

	@Override
	public void write(int b) throws IOException {
		//Loop through each stream
		for (OutputStream stream : streams) {
			//Write data to that stream
			stream.write(b);
		}
	}

	@Override
	public void flush() throws IOException {
		//Loop through each stream
		for (OutputStream stream : streams) {
			//Flush that stream
			stream.flush();
		}
	}

	@Override
	public void close() throws IOException {
		//Loop through each stream
		for (OutputStream stream : streams) {
			//Close that stream
			stream.close();
		}
	}
}
