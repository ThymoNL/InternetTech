package nl.saxion.hboit.internettech.server.keepalive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class Pinger implements Runnable {
	private static final int INTERVAL = 60000; // Ping interval
	private static final int TIMEOUT = 3000; // Max. pong reply time

	private Socket socket;
	private PingTimeout cb;

	private long pongTime;

	public Pinger(Socket socket, PingTimeout cb) {
		this.socket = socket;
		this.cb = cb;
	}

	public void pong() {
		pongTime = System.currentTimeMillis();
	}

	@Override
	public void run() {
		boolean ping = true;
		try {
			PrintWriter out = new PrintWriter(socket.getOutputStream());

			while (ping && socket.isConnected()) {
				pongTime = -1; // Reset timer to something we know is invalid
				Thread.sleep(INTERVAL);

				out.println("PING");
				out.flush();
				long pingTime = System.currentTimeMillis();

				Thread.sleep(TIMEOUT);
				if (pongTime == -1 || pongTime - pingTime > TIMEOUT) {
					cb.onTimeout();
					ping = false;
				}
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}


