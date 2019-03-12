package nl.saxion.hboit.internettech.server;

import nl.saxion.hboit.internettech.server.keepalive.Pinger;
import nl.saxion.hboit.internettech.server.protocol.ClientCommands;
import nl.saxion.hboit.internettech.server.protocol.ServerCommands;
import nl.saxion.hboit.internettech.server.protocol.UnexpectedCommandException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

public class ClientHandler implements Runnable {
	private static final String MOTD = "(>'-')> <('-'<) ^('-')^ v('-')v (>'-')> (^-^)";
	private static final String alphanumeric = "^([A-Za-z0-9_]+)";

	private Socket client;
	private BufferedReader in;

	private ServerCall call;
	private ClientCommands parser;
	private ServerCommands proto;
	private Pinger pinger;
	private String username;

	ClientHandler(Socket client, ServerCall call, boolean ping) {
		this.client = client;
		try {
			this.in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			this.parser = ClientCommands.getParser();
			this.proto = new ServerCommands(client.getOutputStream());
			if (ping) this.pinger = new Pinger(client, () -> disconnect("Pong timeout"));
			this.call = call;
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

			if (username.matches(alphanumeric)) {
				proto.ok();
				call.onLogin(this);
				if (pinger != null) new Thread(pinger).start();
				System.out.println(username + " logged in.");
			} else {
				proto.err("username has an invalid format");
			}

			boolean disconnect = false;
			while (!disconnect) {
				String data;
				try {
					data = receive();
				} catch (SocketException e) {
					break;
				}

				if (data == null)
					break; // Connection closed

				String commandType = data.split(" ")[0];

				String command;
				switch (commandType) {
					case "QUIT":
						disconnect = true;
						proto.okPlain("Goodbye");
						break;
					case "PONG":
						if (pinger != null) pinger.pong();
						break;
					case "BCST":
						String msg = parser.bcst(data);
						System.out.println(username + " says: " + msg);
						call.onBroadcast(this, msg);
						proto.ok();
						break;
					case "LSU":
						proto.lsu(call.getClients());
						break;
					case "LSG":
						proto.lsg(call.getGroups());
						break;
					case "DM":
						command = parser.dm(data);
						String[] dm = command.split(" ", 2);
						if (call.onDirectMessage(this, dm[0], dm[1])) // Did we sent the message?
							proto.ok(command);
						else
							proto.err("User does not exist");
						break;
					case "WSPR":
						command = parser.wspr(data);
						String[] wspr = command.split(" ", 2);
						if (call.onGroupMessage(this, wspr[0], wspr[1]))
							proto.ok(command);
						else
							proto.err("Group does not exist");
						break;
					case "MKG":
						//TODO: Check format
						if (call.onGroupAdd(this, parser.mkg(data)))
							proto.ok(data);
						else
							proto.err("Group exists");
						break;
					case "JOIN":
						if (call.onGroupJoin(this, parser.join(data)))
							proto.ok(data);
						else
							proto.err("Group does not exist");
						break;
					case "LVG":
						if (call.onGroupLeave(this, parser.lvg(data)))
							proto.ok(data);
						else
							proto.err("Not in group");
						break;
					case "KICK":
						command = parser.kick(data);
						String[] kick = command.split(" ", 2);
						try {
							if (call.onGroupKick(this, kick[0], kick[1]))
								proto.ok(command);
							else
								proto.err("Not in group");
						} catch (ClientNotOwnerException e) {
							proto.err("Not owner");
						}
						break;
					default:
						proto.err("Unknown command");
				}
			}

			call.onDisconnect(this);
			client.close();
		} catch (UnexpectedCommandException e) {
			disconnect(e.getMessage());
			System.err.println("Unexpected command: " + e.getMessage());
			System.err.println("Disconnecting client");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getUsername() {
		return username;
	}

	public synchronized void broadcast(String user, String msg) {
		proto.bcst(user, msg);
	}

	public synchronized void tell(String sender, String msg) {
		proto.dm(sender, msg);
	}

	public synchronized void groupTell(String group, String sender, String msg) {
		proto.wspr(group, sender, msg);
	}

	public void kick(String name, String kickedBy) {
		proto.kick(name, kickedBy);
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
