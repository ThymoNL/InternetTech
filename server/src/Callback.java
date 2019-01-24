import java.util.Set;

public interface Callback {
	void onLogin(ClientHandler client);

	void onDisconnect(ClientHandler client);

	void onBroadcast(ClientHandler client, String msg);

	Set<String> getClients();
}
