import protocol.ClientCommands;
import protocol.ServerCommands;
import protocol.UnexpectedCommandException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientHandler implements Runnable {
	private static final String MOTD = "(>'-')> <('-'<) ^('-')^ v('-')v(>'-')> (^-^)";
	private static final String regularExpression = "^([A-Za-z0-9_]+)";

	private Socket client;
	private BufferedReader in;

	private Callback cb;
	private ClientCommands parser;
	private ServerCommands proto;
	private Pinger pinger;
	private String username;

	ClientHandler(Socket client, Callback cb) {
		this.client = client;
		try {
			this.in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			this.parser = ClientCommands.getParser();
			this.proto = new ServerCommands(client.getInputStream(), client.getOutputStream());
			this.pinger = new Pinger(client, in, () -> disconnect("Pong timeout"));
			this.cb = cb;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		if (in == null)
			return; // No connection. Do nothing.


		try {
			proto.helo(MOTD);
			username = parser.helo(receive()); // Wait for login

			if (username.matches(regularExpression)) {
				proto.ok();
				new Thread(pinger).start();
				System.out.println(username + " logged in.");
			} else {
				proto.err("username has an invalid format");

			}

			boolean disconnect = false;
			while (!disconnect) {
				String data = receive();

				if (data == null)
					break; // Connection closed

				String commandType = data.split(" ")[0];

				switch (commandType) {
					case "BCST":
						String msg = parser.bcst(data);
						System.out.println(username + " says: " + msg);
						cb.onBroadcast(this, msg);
						proto.ok();
						break;
					case "QUIT":
						disconnect = true;
						proto.okPlain("Goodbye");
						break;
					case "PONG":
						pinger.pong();
						break;
				}
			}

			cb.onDisconnect(this);
			client.close();
		} catch (IOException | UnexpectedCommandException e) {
			e.printStackTrace();
		}
	}

	public String getUsername() {
		return username;
	}

	public synchronized void broadcast(String user, String msg) {
		proto.bcst(user, msg);
	}

	private String receive() throws IOException {
		String line = in.readLine();
		proto.setLastCommand(line);

		return line;
	}

	private void disconnect(String reason) {
		proto.dscn(reason);

		try {
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
