import java.net.Socket;

public class Pinger implements Runnable {
	Socket socket;

	public Pinger(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		while (socket.isConnected()) {

		}
	}
}
