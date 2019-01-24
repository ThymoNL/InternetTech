import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;

public class Server {
	private static final int PORT = 1337;

	private static ClientPool pool = new ClientPool();

	private static ServerCall serverCall = new ServerCall() {
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
	};

	public static void main(String[] args) throws IOException {
		ServerSocket socket = new ServerSocket(PORT);
		System.out.println("Server listening on port " + socket.getLocalPort());
		int threadCount = 0;

		//noinspection InfiniteLoopStatement
		while (true) {
			Socket clientSock = socket.accept();

			ClientHandler handler = new ClientHandler(clientSock, serverCall);

			new Thread(handler, "Client" + ++threadCount).start();
		}
	}
}
