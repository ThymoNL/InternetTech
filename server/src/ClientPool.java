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

	synchronized Set<String> getUsers() {
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
}
