package nl.saxion.hboict.internettech.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	private Socket socket;
	private BufferedReader in;
	private OutputStream os;

	public static void main(String[] args) {
		String host = "localhost";
		int port = 1337;
		boolean log = true;

		for (int i = 0; i < args.length; i++) {
			switch (args[i]) {
				case "-h":
				case "--host":
					host = args[i + 1];
					break;
				case "-p":
				case "--port":
					port = Integer.parseInt(args[i + 1]);
					break;
				case "--no-logs":
					log = false;
					break;
				case "--usage":
				case "--help":
					System.out.println("Usage: chat-client [-h|--host HOSTNAME] [-p|--port PORT]");
					System.out.println("\t\t--no-logs: don't show the messages from and to the server in the console.");
					return;
			}
		}

		if (port < 1 || port > 65535) {
			System.err.println("Invalid port!");
			return;
		}

		try {
			new Client().init(host, port, log);
		} catch (IOException e) {
			System.err.println("Could not connect to host");
		}
	}

	private void init(String host, int port, boolean log) throws UnknownHostException {
		try {
			socket = new Socket(host, port);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			os = socket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}

		loop(log);
	}

	private void loop(boolean log) {
		try {
			while (true) {
				String line = in.readLine();

				if (log) {
					System.out.println(line); //TODO: Add color
				}

				if ("PING".equals(line)) {
					send("PONG");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void send(String s) {
		try {
			os.write(s.getBytes());
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
