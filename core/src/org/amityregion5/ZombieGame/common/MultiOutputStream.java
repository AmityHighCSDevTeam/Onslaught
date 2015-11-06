package org.amityregion5.ZombieGame.common;

import java.io.IOException;
import java.io.OutputStream;

public class MultiOutputStream extends OutputStream {
	
	private OutputStream[] streams;
	
	public MultiOutputStream(OutputStream ... streams) {
		this.streams = streams;
	}

	@Override
	public void write(int b) throws IOException {
		for (OutputStream stream : streams) {
			stream.write(b);
		}
	}
	
	@Override
	public void flush() throws IOException {
		for (OutputStream stream : streams) {
			stream.flush();
		}
	}
	
	@Override
	public void close() throws IOException {
		for (OutputStream stream : streams) {
			stream.close();
		}
	}
}
