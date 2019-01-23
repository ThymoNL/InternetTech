public interface Callback {

	void onDisconnect(Object o);

	void onBroadcast(Object o, String msg);

	String getClients(String user1, String user2, String user3);
}
