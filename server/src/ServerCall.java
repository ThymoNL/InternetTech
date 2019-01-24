import java.util.Set;

public interface ServerCall {
	void onLogin(ClientHandler client);

	void onDisconnect(ClientHandler client);

	void onBroadcast(ClientHandler client, String msg);

	Set<String> getClients();
}
