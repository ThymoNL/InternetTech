public interface Callback {

	void onDisconnect(Object o);

	void onBroadcast(Object o, String msg);

	String getClients(String user);
}
