package nl.saxion.hboict.internettech.client;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class NonblockingBufferedReader {
	private final BlockingQueue<String> lines = new LinkedBlockingQueue();
	private volatile boolean closed = false;
	private Thread readerThread = null;

	public NonblockingBufferedReader(final BufferedReader reader) {
		this.readerThread = new Thread(() -> {
			try {
				while (!Thread.interrupted()) {
					String line = reader.readLine();
					if (line == null) {
						break;
					}

					lines.add(line);
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			} finally {
				closed = true;
			}

		});
		this.readerThread.setDaemon(true);
		this.readerThread.start();
	}

	public String readLine() throws IOException {
		try {
			return closed && lines.isEmpty() ? null : lines.poll(500, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new IOException("The BackgroundReaderThread was interrupted!", e);
		}
	}

	public void close() {
		if (readerThread != null) {
			readerThread.interrupt();
			readerThread = null;
		}

	}
}
