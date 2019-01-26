package nl.saxion.hboit.internettech.server;

import java.util.HashMap;
import java.util.Map;

public class Group {
	private String name;
	private String owner;
	private Map<String, ClientHandler> clients = new HashMap<>();

	/**
	 * Create a new group and join it
	 * @param owner The user that creates the group
	 * @param name Group name
	 * @author Thymo van Beers
	 */
	public Group(ClientHandler owner, String name) {
		this.name = name;
		join(owner);
		this.owner = owner.getUsername();
	}

	/**
	 * Join a group
	 * @param user User that wants to join
	 * @author Thymo van Beers
	 */
	public synchronized void join(ClientHandler user) {
		clients.put(user.getUsername(), user);
	}

	/**
	 * Leave group
	 * If the owner leaves a new one is assigned.
	 * @param user User that wants to lvg
	 * @return true: left group, false: not in group
	 * @author Thymo van Beers
	 */
	public synchronized boolean leave(ClientHandler user) {
		if (clients.remove(user.getUsername()) == null)
			return false;

		if (user.getUsername().equals(owner) && clients.size() != 0) {
			owner = clients.keySet().iterator().next(); // Assign new owner
			System.out.println(name + " new owner: " + owner);
		}

		return true;
	}

	public synchronized boolean kick(ClientHandler initiator, String kickClient) throws ClientNotOwnerException {
		if (!initiator.getUsername().equals(owner))
			throw new ClientNotOwnerException(); // Only the owner can kick

		ClientHandler kicked = clients.remove(kickClient);

		if (kicked == null)
			return false;

		kicked.kick(name, owner);
		return true;
	}

	/**
	 * Broadcast a message to everyone in the group
	 * @param sender Message sender
	 * @param msg Message content
	 */
	public synchronized void tellAll(ClientHandler sender, String msg) {
		for (String s : clients.keySet()) {
			clients.get(s).groupTell(name, sender.getUsername(), msg);
		}
	}
}
