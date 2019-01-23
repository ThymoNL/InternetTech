package nl.saxion.hboict.internettech.client.protocol;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Scanner;

public class ServerReplies {
	private OutputStream os;

	private String username;

	public ServerReplies(OutputStream os) {
		this.os = os;
	}

	public void parse(String line) {
		String[] data = line.split(" ", 2);

		switch (data[0]) {
			case "HELO":
				helo(data);
				break;
			default:
				System.out.println("NYI");
		}
	}

	private void helo(String[] data) {
		System.out.println(data[1]);

		Scanner scanner = new Scanner(System.in);
		System.out.println("Please fill in your username:");
		username = scanner.nextLine();

		send("HELO " + username);

	}

	private void send(String data) {
		try {
			os.write(data.getBytes());
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
