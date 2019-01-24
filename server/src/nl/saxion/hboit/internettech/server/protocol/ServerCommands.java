package nl.saxion.hboit.internettech.server.protocol;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Iterator;
import java.util.Set;

public class ServerCommands {
	private PrintWriter out;

	private String lastCommand;

	private MessageDigest md5;
	private Base64.Encoder base64;

	public ServerCommands(OutputStream os) {
		this.out = new PrintWriter(os);

		try {
			this.md5 = MessageDigest.getInstance("MD5");
			this.base64 = Base64.getEncoder();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public void setLastCommand(String lastCommand) {
		this.lastCommand = lastCommand;
	}

	private void send(String cmd) {
		System.out.println("Sending: " + cmd);
		out.println(cmd);
		out.flush();
	}

	private String makeList(Set<String> set) {
		StringBuilder list = new StringBuilder();
		Iterator<String> itr = set.iterator();
		while (itr.hasNext()) {
			list.append(itr.next());

			if (itr.hasNext())
				list.append(',');
		}

		return list.toString();
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

	public void okPlain(String data) {
		send("+OK " + data);
	}

	public void bcst(String user, String msg) {
		send("BCST " + user + " " + msg);
	}

	public void dscn(String reason) {
		send("DSCN " + reason);
	}

	public void err(String reason) {
		send("-ERR " + reason);
	}

	public void lsu(Set<String> set) {
		okPlain(makeList(set));
	}

	public void lsg(Set<String> set) {
		okPlain(makeList(set));
	}

	public void dm(String sender, String msg) {
		send("DM " + sender + " "  + msg);
	}

	public void wspr(String group, String sender, String msg) {
		send("WSPR " + group + " " + sender + " " + msg);
	}

	public void kick(String name, String kickedBy) {
		send("KICK " + name + " "+ kickedBy);
	}
}
