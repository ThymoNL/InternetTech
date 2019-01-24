import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;

public class Server {
	private static final int PORT = 1337;

	private static ClientPool pool = new ClientPool();

	public static void main(String[] args) throws IOException {
		ServerSocket socket = new ServerSocket(PORT);
		System.out.println("Server listening on port " + socket.getLocalPort());
		int threadCount = 0;

		while (true) {
			Socket clientSock = socket.accept();

			ClientHandler handler = new ClientHandler(clientSock, new Callback() {
				@Override
				public void onLogin(ClientHandler client) {
					pool.add(client);
				}

				@Override
				public void onDisconnect(ClientHandler client) {
					pool.remove(client);
				}

				@Override
				public void onBroadcast(ClientHandler client, String msg) {
					pool.tellAll(client, msg);
				}

				@Override
				public Set<String> getClients() {
					return pool.getUsers();
				}
			});

			new Thread(handler, "Client" + ++threadCount).start();
		}
	}

	private void removeClient(ClientHandler client) {
		pool.remove(client);
	}

	private void addClient(ClientHandler client) {
		pool.add(client);
	}

	private void broadcast(String msg, ClientHandler sender) {

	}
}
