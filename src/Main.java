import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
	private static final int PORT = 1337;

	private ServerSocket socket;

	public static void main(String[] args) throws IOException {
		new Main().run();
	}

	private void run() throws IOException {
		socket = new ServerSocket(PORT);
		System.out.println("Server listening on port " + socket.getLocalPort());

		while (true) {
			Socket clientSocket = socket.accept();

			PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
			writer.println("Hello from the server!");
			writer.flush();

			clientSocket.close();
		}
	}
}
