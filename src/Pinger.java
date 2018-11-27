import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Pinger implements Runnable {
	private Socket socket;
	private String expectedResponse = "Pong";

	public Pinger(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		while (socket.isConnected()) {

			try {
				PrintWriter out = new PrintWriter(socket.getOutputStream());
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

				out.println("Ping");
				out.flush();

				Thread thread =  new Thread();
				if(in.equals(expectedResponse)){

				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
