package nl.saxion.hboit.internettech.server;

import java.util.Set;

public interface ServerCall {
	void onLogin(ClientHandler client);

	void onDisconnect(ClientHandler client);

	void onBroadcast(ClientHandler client, String msg);

	boolean onDirectMessage(ClientHandler client, String recipient, String msg);

	boolean onGroupAdd(ClientHandler client, String name);

	boolean onGroupJoin(ClientHandler client, String group);

	boolean onGroupLeave(ClientHandler client, String group);

	boolean onGroupMessage(ClientHandler client, String group, String msg);

	boolean onGroupKick(ClientHandler client, String group, String kickUser) throws ClientNotOwnerException;

	Set<String> getClients();

	Set<String> getGroups();
}
