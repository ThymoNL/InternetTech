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

	String getClientList(String user1,String user2,String user3){
		for (int i = 0; i < clients.size(); i++) {
			user1 = clients.get(i).toString();
			System.out.println(user1);
		}
		return user1;
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
