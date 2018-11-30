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

	private ClientCommands parser;
	private ServerCommands proto;

	private String username;

	ClientHandler(Socket client) {
		this.client = client;
		try {
			this.parser = ClientCommands.getParser();
			this.proto = new ServerCommands(client.getInputStream(), client.getOutputStream());
			this.in = new BufferedReader(new InputStreamReader(client.getInputStream()));
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
			username = parser.helo(receive());
			proto.ok();
			System.out.println(username + " logged in.");

			boolean disconnect = false;
			while (!disconnect) {
				String data = receive();
				String commandType = data.split(" ")[0];

				if (commandType.equals("BCST")) {
					System.out.println(username + " says: " + parser.bcst(receive()));
				} else if (commandType.equals("QUIT")) {
					proto.okPlain("Goodbye");
					disconnect = true;
				} else {
					throw new UnexpectedCommandException("BCST or QUIT", commandType);
				}
			}

			client.close();
		} catch (IOException | UnexpectedCommandException e) {
			e.printStackTrace();
		}
	}

	private String receive() throws IOException {
		String line = in.readLine();
		proto.setLastCommand(line);

		return line;
	}

	/*private void disconnect(String reason) {

	}*/
}
