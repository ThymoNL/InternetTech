package keepalive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class Pinger implements Runnable {
	private static final int TIMEOUT = 3000;

	private Socket socket;
	private InputStream is;
	private PingTimeout cb;

	private long pongTime;

	public Pinger(Socket socket, BufferedReader in, PingTimeout cb) {
		try {
			this.socket = socket;
			this.is = socket.getInputStream();
			this.cb = cb;
		} catch (IOException e) {
			e.printStackTrace();
		}
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
				Thread.sleep(60 * 1000);

				out.println("PING");
				out.flush();
				long pingTime = System.currentTimeMillis();

				Thread.sleep(3 * 1000);
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


