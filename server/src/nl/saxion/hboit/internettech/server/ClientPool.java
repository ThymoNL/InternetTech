package nl.saxion.hboit.internettech.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class ClientPool {
	private Map<String, ClientHandler> clients = new HashMap<>();

	synchronized void add(ClientHandler client) {
		clients.put(client.getUsername(), client);
	}

	synchronized void remove(ClientHandler client) {
		clients.remove(client.getUsername());
	}

	synchronized Set<String> getNames() {
		return clients.keySet();
	}

	synchronized boolean tell(ClientHandler sender, String recipient, String msg) {
		ClientHandler dest = clients.get(recipient);
		if (dest == null) return false;

		dest.tell(sender.getUsername(), msg);

		return true;
	}

	synchronized void tellAll(ClientHandler sender, String msg) {
		for (String s : clients.keySet()) {
			clients.get(s).broadcast(sender.getUsername(), msg);
		}
	}

	/**
	 * Request a file transfer to clinet
	 *
	 * @param sender Requesting client
	 * @param target Target client
	 * @param file File name
	 * @param size File size
	 * @return true: request send. false: client not found
	 */
	synchronized boolean requestTransfer(ClientHandler sender, String target, String file, int size) {
		ClientHandler client = clients.get(target);

		if (client == null)
			return false;

		client.requestTransfer(sender.getUsername(), file, size);
		return true;
	}

	public void transferAccept(String target, String ip) {
		ClientHandler client = clients.get(target);

		if (client == null) {
			System.err.println("Tried accepting transfer from unknown client!");
			return;
		}

		client.acceptedTransfer(ip);
	}

	public void transferReject(String target, String reason) {
		ClientHandler client = clients.get(target);

		if (client == null) {
			System.err.println("Tried rejecting transfer from unknown client!");
			return;
		}

		client.rejectedTransfer(reason);
	}
}
