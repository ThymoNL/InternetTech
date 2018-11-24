package protocol;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Base64;

public class ServerCommands {
	private OutputStream os;
	private PrintWriter out;

	private Base64.Encoder base64;

	public ServerCommands(OutputStream os) {
		this.os = os;
		this.out = new PrintWriter(os);
		this.base64 = Base64.getEncoder();
	}

	private void send(String cmd) {
		out.println(cmd);
		out.flush();
	}

	public void helo(String motd) {
		send("HELO " + motd);
	}

	public void ok(byte[] data) {
		send("+OK " + base64.encodeToString(data));
	}

	public void dscn(String reason) {
		send("DSCN " + reason);
	}
}
