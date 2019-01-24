import java.util.*;

class ClientPool {
	private Map<String, ClientHandler> clients = new HashMap<>();

	void add(ClientHandler client) {
		clients.put(client.getUsername(), client);
	}

	void remove(ClientHandler client) {
		clients.remove(client.getUsername());
	}

	Set<String> getUsers() {
		return clients.keySet();
	}

	synchronized void tellAll(ClientHandler sender, String msg) {
		for (int i = 0; i < clients.size(); i++) {
			ClientHandler client = clients.get(i);

			if (!client.equals(sender)) {
				client.broadcast(sender.getUsername(), msg);
			}
		}
	}
}
