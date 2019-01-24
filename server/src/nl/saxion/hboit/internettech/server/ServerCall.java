package nl.saxion.hboit.internettech.server;

import java.util.Set;

public interface ServerCall {
	void onLogin(ClientHandler client);

	void onDisconnect(ClientHandler client);

	void onBroadcast(ClientHandler client, String msg);

	boolean onDirectMessage(ClientHandler client, String recipient, String msg);

	boolean onGroupAdd(ClientHandler client, String name);

	boolean onGroupMessage(ClientHandler client, String group, String msg);

	Set<String> getClients();

	Set<String> getGroups();
}
