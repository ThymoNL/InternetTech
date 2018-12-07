import protocol.ClientCommands;
import protocol.ServerCommands;
import protocol.UnexpectedCommandException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientHandler implements Runnable {
	private static final String MOTD = "(>'-')> <('-'<) ^('-')^ v('-')v(>'-')> (^-^)";

	private Socket client;
	private BufferedReader in;

	private ClientPool pool;
	private ClientCommands parser;
	private ServerCommands proto;
	private Pinger pinger;

	private String username;

	ClientHandler(Socket client) {
		this.client = client;
		try {
			this.in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			this.pool = ClientPool.getPool();
			this.parser = ClientCommands.getParser();
			this.proto = new ServerCommands(client.getInputStream(), client.getOutputStream());
			this.pinger = new Pinger(client, in);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		if (in == null)
			return; // No connection. Do nothing.

		pool.add(this);

		try {
			proto.helo(MOTD);
			username = parser.helo(receive()); // Wait for login
			proto.ok();
			new Thread(pinger).start();
			System.out.println(username + " logged in.");

			boolean disconnect = false;
			while (!disconnect) {
				String data = receive();

				if (data == null)
					break; // Connection closed

				String commandType = data.split(" ")[0];

				if (commandType.equals("BCST")) {
					String msg = parser.bcst(data);
					System.out.println(username + " says: " + msg);
					pool.tellAll(this, msg);
				} else if (commandType.equals("QUIT")) {
					proto.okPlain("Goodbye");
					disconnect = true;
				} else if (commandType.equals("PONG")) {
					pinger.pong();
				}
			}

			pool.remove(this);
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

	/*private void disconnect(String reason) {

	}*/
}
