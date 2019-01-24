package nl.saxion.hboit.internettech.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Server {
	private static final int PORT = 1337;

	private static ClientPool pool = new ClientPool();
	private static Map<String, Group> groups = new HashMap<>();

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
		public boolean onDirectMessage(ClientHandler client, String recipient, String msg) {
			return pool.tell(client, recipient, msg);
		}

		@Override
		public boolean onGroupAdd(ClientHandler client, String name) {
			if (groups.get(name) != null)
				return false;

			Group group = new Group(name);
			group.join(client);
			groups.put(name, group);
			return true;
		}

		@Override
		public boolean onGroupMessage(ClientHandler client, String name, String msg) {
			Group group = groups.get(name);

			if (group == null)
				return false;

			group.tellAll(client, msg);
			return true;
		}

		@Override
		public Set<String> getClients() {
			return pool.getNames();
		}

		@Override
		public Set<String> getGroups() {
			return groups.keySet();
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
