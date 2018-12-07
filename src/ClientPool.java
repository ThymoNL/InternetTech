import java.util.ArrayList;
import java.util.List;

public class ClientPool {
	public static ClientPool instance;

	private List<ClientHandler> clients = new ArrayList<>();

	private ClientPool() {}

	public static ClientPool getPool() {
		if (instance == null) {
			instance = new ClientPool();
		}

		return instance;
	}

	public void add(ClientHandler client) {
		clients.add(client);
	}

	public void remove(ClientHandler client) {
		clients.remove(client);
	}

	public void tellAll(ClientHandler sender, String msg) {
		for (int i = 0; i < clients.size(); i++) {
			ClientHandler client = clients.get(i);

			if (!client.equals(sender)) {
				client.broadcast(sender.getUsername(), msg);
			}
		}
	}
}
