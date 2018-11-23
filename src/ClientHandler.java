import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {
	private Socket client;

	public ClientHandler(Socket client) {
		this.client = client;
	}

	@Override
	public void run() {
		try {
			client.close(); // Just close the connection for now
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
