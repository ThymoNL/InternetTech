import protocol.ClientCommands;
import protocol.ServerCommands;

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
			username = parser.helo(proto.receive());
			proto.ok();
			System.out.println(username + " logged in.");
			Thread.sleep(10000);
			//proto.dscn("Not Implemented");

			client.close();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
