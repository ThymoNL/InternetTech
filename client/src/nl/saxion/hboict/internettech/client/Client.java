package nl.saxion.hboict.internettech.client;

import nl.saxion.hboict.internettech.client.protocol.ServerReplies;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	private Socket socket;
	private BufferedReader in;
	private OutputStream os;
	private ServerReplies replies;

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

	/**
	 * Initializes connection to server
	 *
	 * @param host
	 * @param port
	 * @param log
	 * @throws IOException UnknownHostException or SocketException
	 */
	private void init(String host, int port, boolean log) throws IOException {
		try {
			socket = new Socket(host, port);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			os = socket.getOutputStream();
			replies = new ServerReplies(socket.getOutputStream());
		} catch (UnknownHostException | ConnectException e) {
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			return;
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

				if (line.startsWith("DSCN")) {
					socket.close();
					break;
				} else {
					replies.parse(line);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
