import java.util.ArrayList;
import java.util.List;

class ClientPool {
	private List<ClientHandler> clients = new ArrayList<>();

	void add(ClientHandler client) {
		clients.add(client);
	}

	void remove(ClientHandler client) {
		clients.remove(client);
	}

	String[] getClients(){
		String[] users = new String[clients.size()];

		for (int i = 0; i < clients.size(); i++) {
			users[i] = clients.get(i).getUsername();
		}

		return users;
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
