import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
	private static final int PORT = 1337;

	private static ClientPool pool = new ClientPool();

	public static void main(String[] args) throws IOException {
		ServerSocket socket = new ServerSocket(PORT);
		System.out.println("Server listening on port " + socket.getLocalPort());

		while (true) {
			Socket client = socket.accept();

			ClientHandler handler = new ClientHandler(client, new Callback() {
				@Override
				public void onDisconnect(Object o) {
					pool.remove((ClientHandler) o);
				}

				@Override
				public void onBroadcast(Object o, String msg) {
					pool.tellAll((ClientHandler) o, msg);
				}
			});

			pool.add(handler);
			new Thread(handler).start();
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
