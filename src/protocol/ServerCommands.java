package protocol;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class ServerCommands {
	private InputStream is;
	private BufferedReader in;

	private OutputStream os;
	private PrintWriter out;

	private String lastCommand;

	private MessageDigest md5;
	private Base64.Encoder base64;

	public ServerCommands(InputStream is, OutputStream os) {
		this.is = is;
		this.os = os;

		this.in = new BufferedReader(new InputStreamReader(is));
		this.out = new PrintWriter(os);

		try {
			this.md5 = MessageDigest.getInstance("MD5");
			this.base64 = Base64.getEncoder();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	private void send(String cmd) {
		System.out.println("Sending: " + cmd);
		out.println(cmd);
		out.flush();
	}

	public String receive() throws IOException {
		lastCommand = in.readLine();

		return lastCommand;
	}

	public void helo(String motd) {
		send("HELO " + motd);
	}

	public void ok() {
		byte[] encoded = base64.encode(md5.digest(lastCommand.getBytes()));

		send("+OK " + new String(encoded));
	}

	public void ok(String data) {
		byte[] encoded = base64.encode(md5.digest(data.getBytes()));

		send("+OK " + new String(encoded));
	}

	public void dscn(String reason) {
		send("DSCN " + reason);
	}
}
