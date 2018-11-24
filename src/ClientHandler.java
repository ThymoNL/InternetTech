import protocol.ServerCommands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ClientHandler implements Runnable {
	private static final String MOTD = "(>'-')> <('-'<) ^('-')^ v('-')v(>'-')> (^-^)";

	private Socket client;
	private BufferedReader in;

	private ServerCommands proto;
	private MessageDigest md5;

	private String username;

	ClientHandler(Socket client) {
		this.client = client;
		try {
			this.md5 = MessageDigest.getInstance("MD5");
			this.proto = new ServerCommands(client.getOutputStream());
			this.in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		} catch (IOException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		if (in == null)
			return; // No connection. Do nothing.

		try {
			proto.helo(MOTD);
			username = in.readLine().split(" ")[1];
			proto.ok(md5hash("HELO " + username).getBytes("UTF-8"));
			System.out.println(username + " logged in.");
			Thread.sleep(10000);
			proto.dscn("Not Implemented");

			client.close();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	private String md5hash(String s) throws UnsupportedEncodingException {
		// Digest the string
		byte[] digest = md5.digest(s.getBytes("UTF-8"));

		// Convert byte array into signum representation
		BigInteger no = new BigInteger(1, digest);

		// Convert message digest into hex value
		String hashtext = no.toString(16);
		while (hashtext.length() < 32) {
			hashtext = "0" + hashtext;
		}

		return hashtext;
	}
}
