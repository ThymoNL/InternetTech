package nl.saxion.hboit.internettech.server;

import java.util.HashMap;
import java.util.Map;

public class Group {
	private String name;
	private Map<String, ClientHandler> clients = new HashMap<>();

	public Group(String name) {
		this.name = name;
	}

	public synchronized void join(ClientHandler user) {
		clients.put(user.getUsername(), user);
	}

	public synchronized void leave(ClientHandler user) {
		clients.remove(user.getUsername());
	}

	public synchronized void tellAll(ClientHandler sender, String msg) {
		for (String s : clients.keySet()) {
			clients.get(s).groupTell(name, sender.getUsername(), msg);
		}
	}
}
