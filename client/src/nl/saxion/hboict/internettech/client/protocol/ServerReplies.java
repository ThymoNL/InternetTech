package nl.saxion.hboict.internettech.client.protocol;

import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;

public class ServerReplies {
	private OutputStream os;

	private MessageDigest md5;
	private Base64.Encoder base64;

	private String username;
	private String lastCommandHash;

	public ServerReplies(OutputStream os) {
		this.os = os;

		try {
			this.md5 = MessageDigest.getInstance("MD5");
			this.base64 = Base64.getEncoder();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public void parse(String line) {
		String[] data = line.split(" ", 2);

		switch (data[0]) {
			case "PING":
				send("PONG");
				break;
			case "HELO":
				helo(data);
				break;
			case "+OK":
				if (!lastCommandHash.equals(data[1])) {
					System.out.println("Hash mismatch");
					send("QUIT");
				}
				break;
			case "-ERR":
				System.out.println("An error has occurred!");
				System.out.println(data[1]);
				send("QUIT");
			default:
				System.out.println("NYI");
		}
	}

	private void helo(String[] data) {
		System.out.println(data[1]);

		Scanner scanner = new Scanner(System.in);
		System.out.println("Please fill in your username:");
		username = scanner.nextLine();
		//scanner.close();

		send("HELO " + username); //TODO: Use printwriter!
	}

	private void send(String data) {
		try {
			lastCommandHash = new String(base64.encode(md5.digest(data.getBytes())));
			os.write((data + "\n").getBytes());
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
