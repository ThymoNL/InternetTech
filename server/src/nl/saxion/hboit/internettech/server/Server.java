package nl.saxion.hboit.internettech.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Server {
	private static final int PORT = 1337;

	private static ClientPool clients = new ClientPool();
	private static Map<String, Group> groups = new HashMap<>();

	private static ServerCall serverCall = new ServerCall() {
		@Override
		public void onLogin(ClientHandler client) {
			clients.add(client);
		}

		@Override
		public void onDisconnect(ClientHandler client) {
			clients.remove(client);
		}

		@Override
		public void onBroadcast(ClientHandler client, String msg) {
			clients.tellAll(client, msg);
		}

		@Override
		public boolean onDirectMessage(ClientHandler client, String recipient, String msg) {
			return clients.tell(client, recipient, msg);
		}

		@Override
		public boolean onGroupAdd(ClientHandler client, String name) {
			if (groups.get(name) != null)
				return false;

			Group group = new Group(client, name);
			groups.put(name, group);
			return true;
		}

		@Override
		public boolean onGroupJoin(ClientHandler client, String groupName) {
			Group group = groups.get(groupName);

			if (group == null) return false;

			group.join(client);
			return true;
		}

		@Override
		public boolean onGroupLeave(ClientHandler client, String groupName) {
			Group group = groups.get(groupName);

			if (group == null) // TODO: Exception instead?
				return false;

			return group.leave(client);
		}

		@Override
		public boolean onGroupMessage(ClientHandler client, String groupName, String msg) {
			Group group = groups.get(groupName);

			if (group == null)
				return false;

			group.tellAll(client, msg);
			return true;
		}

		@Override
		public boolean onGroupKick(ClientHandler client, String groupName, String kickUser) throws ClientNotOwnerException {
			Group group = groups.get(groupName);

			if (group == null) return false; //TODO: Exception instead?

			group.kick(client, kickUser);
			return true;
		}

		@Override
		public boolean onFileTransferRequest(ClientHandler client, String user, String file, int size) {
			return clients.requestTransfer(client, user, file, size);
		}

		@Override
		public void onTransferAccepted(String client, String ip) {
			clients.transferAccept(client, ip);
		}

		@Override
		public void onTransferRejected(String client, String reason) {
			clients.transferReject(client, reason);
		}

		@Override
		public Set<String> getClients() {
			return clients.getNames();
		}

		@Override
		public Set<String> getGroups() {
			return groups.keySet();
		}
	};

	public static void main(String[] args) throws IOException {
		boolean ping = true;
		for (String arg : args)
			ping = !arg.equals("--no-ping");

		ServerSocket socket = new ServerSocket(PORT);
		System.out.println("Server listening on port " + socket.getLocalPort());
		int threadCount = 0;

		//noinspection InfiniteLoopStatement
		while (true) {
			Socket clientSock = socket.accept();

			ClientHandler handler = new ClientHandler(clientSock, serverCall, ping);

			new Thread(handler, "Client" + ++threadCount).start();
		}
	}
}
